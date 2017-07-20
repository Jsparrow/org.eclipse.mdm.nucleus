package org.eclipse.mdm.freetextindexer.boundary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.eclipse.mdm.api.base.model.ContextComponent;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.freetextindexer.entities.MDMEntityResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHits;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ElasticsearchBoundaryTest {

	private static Node elasticSearchNode;
	private static ElasticsearchBoundary es;

	private static Client client;

	@BeforeClass
	public static void beforeClass() throws Exception {
		File tempDir = File.createTempFile("elasticsearch-temp", Long.toString(System.nanoTime()));
		tempDir.delete();
		tempDir.mkdir();

		String clusterName = UUID.randomUUID().toString();
		elasticSearchNode = NodeBuilder.nodeBuilder().local(true).clusterName(clusterName)
				.settings(Settings.settingsBuilder()
						.put("path.home", File.createTempFile("elasticsearch", "").getParent())
						.put("index.number_of_shards", 1).put("index.number_of_replicas", 0).put("http.port", 9301))
				.node();
		elasticSearchNode.start();
		client = elasticSearchNode.client();

		es = new ElasticsearchBoundary();
		es.esAddress = "http://localhost:9301/";
	}

	@AfterClass
	public static void afterClass() {
		elasticSearchNode.close();
	}

	@Before
	public void setup() {
		es.active = "true";
	}

	@Test
	public void indexSuccessfullyCreated_CaseDoesNotMatter() throws InterruptedException, ExecutionException {
		es.createIndex("BlA");
		assertTrue(es.hasIndex("bla"));
	}

	@Test
	public void indexSuccessfullyCreated_OtherIndizesNot() throws InterruptedException, ExecutionException {
		es.createIndex("someIndex");
		assertFalse(es.hasIndex("asdf"));
	}

	@Ignore
	@Test
	public void deletedDoc_isGone() throws InterruptedException, DataAccessException {
		TestStep ts = mock(TestStep.class);
		when(ts.getID()).thenReturn("1");
		when(ts.getSourceName()).thenReturn("mdmdiff");
		EntityManager manager = mockManager(ts);

		MDMEntityResponse document = MDMEntityResponse.build(TestStep.class, ts, manager);
		es.index(document);

		es.delete("mdmdiff", "TestStep", "1");

		assertEquals(0, searchForASDF("mdmdiff").totalHits());
	}

	@Ignore
	@Test
	public void docIsIndexed_isFound() throws DataAccessException, InterruptedException {
		TestStep ts = mock(TestStep.class);
		when(ts.getSourceName()).thenReturn("mdm");
		EntityManager manager = mockManager(ts);

		MDMEntityResponse document = MDMEntityResponse.build(TestStep.class, ts, manager);
		es.index(document);

		assertEquals(1, searchForASDF("mdm").totalHits());
	}

	@Test(expected = IllegalStateException.class)
	public void indexCreatedTwice_ThrowsError() {
		es.createIndex("someRandomIndex");
		es.createIndex("someRandomIndex");
	}

	@Test
	public void indexDeactivated_NoIndexingDone() {
		es.active = "false";

		es.createIndex("someSource");
		assertFalse(es.hasIndex("someSource"));
	}

	private SearchHits searchForASDF(String index) throws InterruptedException {
		Thread.sleep(1000); // wait until cluster is healthy again

		SearchRequestBuilder request = client.prepareSearch(index)
				.setQuery(QueryBuilders.simpleQueryStringQuery("asdf").field("_all").field("name^2").lenient(true));
		SearchResponse getResponse = request.execute().actionGet();
		SearchHits hits = getResponse.getHits();
		return hits;
	}

	private EntityManager mockManager(TestStep ts) throws DataAccessException {
		EntityManager manager = mock(EntityManager.class);
		ContextRoot root = mock(ContextRoot.class);
		ContextComponent comp = mock(ContextComponent.class);
		Value value = mock(Value.class);

		Map<ContextType, ContextRoot> map = new HashMap<>();
		List<ContextComponent> comps = new ArrayList<>();
		Map<String, Value> values = new HashMap<>();
		values.put("name", value);
		comps.add(comp);

		when(value.toString()).thenReturn("asdf");
		when(comp.getValues()).thenReturn(values);
		when(root.getContextComponents()).thenReturn(comps);
		map.put(ContextType.UNITUNDERTEST, root);

		when(manager.loadContexts(any(TestStep.class), anyVararg())).thenReturn(map);

		return manager;
	}

}
