/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/

package org.eclipse.mdm.query.boundary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.groups.Tuple;
import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.adapter.ModelManager;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.base.query.Query;
import org.eclipse.mdm.api.base.query.Record;
import org.eclipse.mdm.api.base.query.Result;
import org.eclipse.mdm.api.base.search.SearchService;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.Pool;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.connector.boundary.ConnectorServiceException;
import org.eclipse.mdm.query.entity.Column;
import org.eclipse.mdm.query.entity.QueryRequest;
import org.eclipse.mdm.query.entity.Row;
import org.eclipse.mdm.query.entity.SourceFilter;
import org.eclipse.mdm.query.entity.SuggestionRequest;
import org.mockito.Mockito;

public class QueryServiceTest {

	@org.junit.Test
	public void testQuery() {

		ApplicationContext context = mockContext();
		ConnectorService connectorService = Mockito.mock(ConnectorService.class);
		when(connectorService.getContextByName("env1")).thenReturn(context);

		QueryService queryService = new QueryService();
		queryService.connectorService = connectorService;

		SourceFilter filter = new SourceFilter();
		filter.setSourceName("env1");
		filter.setFilter("Test.Name lk '*'");

		QueryRequest request = new QueryRequest();
		request.setResultType("Test");
		request.setColumns(Arrays.asList("Test.Id", "Test.Name"));
		request.setFilters(Arrays.asList(filter));

		Row expectedRow = new Row();
		expectedRow.setSource("env1");
		expectedRow.setType("Test");
		expectedRow.setId("id1");
		expectedRow.addColumns(
				Arrays.asList(new Column("Test", "Id", "id1", null), new Column("Test", "Name", "Test-Name", null)));

		assertThat(queryService.queryRows(request)).contains(expectedRow);

	}

	@org.junit.Test
	public void testQueryMissingEnvironmentShouldByIgnored() {

		ApplicationContext context = mockContext();
		ConnectorService connectorService = Mockito.mock(ConnectorService.class);
		when(connectorService.getContextByName("env1")).thenReturn(context);
		doThrow(ConnectorServiceException.class).when(connectorService).getContextByName("env2");

		QueryService queryService = new QueryService();
		queryService.connectorService = connectorService;

		SourceFilter filterEnv1 = new SourceFilter();
		filterEnv1.setSourceName("env1");
		filterEnv1.setFilter("Test.Name lk '*'");

		SourceFilter filterEnv2 = new SourceFilter();
		filterEnv2.setSourceName("env2");
		filterEnv2.setFilter("Test.Name lk '*'");

		QueryRequest request = new QueryRequest();
		request.setResultType("Test");
		request.setColumns(Arrays.asList("Test.Id", "Test.Name", "Pool.Name"));
		request.setFilters(Arrays.asList(filterEnv1, filterEnv2));

		Row expectedRow = new Row();
		expectedRow.setSource("env1");
		expectedRow.setType("Test");
		expectedRow.setId("id1");
		expectedRow.addColumns(
				Arrays.asList(new Column("Test", "Id", "id1", null), new Column("Test", "Name", "Test-Name", null)));

		assertThat(queryService.queryRows(request)).contains(expectedRow);
	}

	@org.junit.Test
	public void testQueryMissingEntitiesShouldBeIgnored() {

		ApplicationContext context1 = mockContext();
		ApplicationContext context2 = mockContext();
		ConnectorService connectorService = Mockito.mock(ConnectorService.class);
		when(connectorService.getContextByName("env1")).thenReturn(context1);
		when(connectorService.getContextByName("env2")).thenReturn(context2);
		
		EntityType type = mockEntity("env2", "Test");
		EntityType pool = mockEntity("env2", "Pool");

		when(context2.getModelManager().get().getEntityType(Test.class)).thenReturn(type);
		when(context2.getModelManager().get().getEntityType("Test")).thenReturn(type);
		when(context2.getModelManager().get().getEntityType(Pool.class)).thenReturn(pool);
		when(context2.getModelManager().get().getEntityType("Pool")).thenReturn(pool);

		SearchService ss = mock(SearchService.class);
		EntityType test = context2.getModelManager().get().getEntityType(Test.class);
		when(ss.listEntityTypes(Mockito.eq(Test.class))).thenReturn(Arrays.asList(test, pool));
		when(ss.listSearchableTypes()).thenReturn(Arrays.asList(Test.class));

		Record recordTest = new Record(context2.getModelManager().get().getEntityType(Test.class));
		recordTest.addValue(ValueType.STRING.create("Id", "id1"));
		recordTest.addValue(ValueType.STRING.create("Name", "Test-Name"));

		Record recordPool = new Record(context2.getModelManager().get().getEntityType(Pool.class));
		recordPool.addValue(ValueType.STRING.create("Name", "Pool-Name"));
		Result result = new Result();
		result.addRecord(recordPool);
		result.addRecord(recordTest);
		when(context2.getSearchService().get().fetchResults(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList(result));


		QueryService queryService = new QueryService();
		queryService.connectorService = connectorService;

		SourceFilter filterEnv1 = new SourceFilter();
		filterEnv1.setSourceName("env1");
		filterEnv1.setFilter("Test.Name lk '*'");

		SourceFilter filterEnv2 = new SourceFilter();
		filterEnv2.setSourceName("env2");
		filterEnv2.setFilter("Test.Name lk '*'");

		QueryRequest request = new QueryRequest();
		request.setResultType("Test");
		request.setColumns(Arrays.asList("Test.Id", "Test.Name", "Pool.Name"));
		request.setFilters(Arrays.asList(filterEnv1, filterEnv2));

		Row expectedRowEnv1 = new Row();
		expectedRowEnv1.setSource("env1");
		expectedRowEnv1.setType("Test");
		expectedRowEnv1.setId("id1");
		expectedRowEnv1.addColumns(
				Arrays.asList(new Column("Test", "Id", "id1", null), new Column("Test", "Name", "Test-Name", null)));

		Row expectedRowEnv2 = new Row();
		expectedRowEnv2.setSource("env2");
		expectedRowEnv2.setType("Test");
		expectedRowEnv2.setId("id1");
		expectedRowEnv2.addColumns(Arrays.asList(new Column("Test", "Id", "id1", null),
				new Column("Test", "Name", "Test-Name", null), new Column("Pool", "Name", "Pool-Name", null)));

		List<Row> list = queryService.queryRows(request);

		assertThat(list).extracting("source", "type", "id").containsOnly(new Tuple("env1", "Test", "id1"),
				new Tuple("env2", "Test", "id1"));

		assertThat(list.get(0).getColumns()).containsOnlyElementsOf(expectedRowEnv1.getColumns());

		assertThat(list.get(1).getColumns()).containsOnlyElementsOf(expectedRowEnv2.getColumns());

	}

	private EntityType mockEntity(String sourceName, String name) {
		Attribute attrId = mock(Attribute.class);
		when(attrId.getName()).thenReturn("Id");
		when(attrId.getValueType()).thenReturn(ValueType.LONG);

		Attribute attrName = mock(Attribute.class);
		when(attrName.getName()).thenReturn("Name");
		when(attrName.getValueType()).thenReturn(ValueType.STRING);

		EntityType entity = mock(EntityType.class);
		when(entity.getSourceName()).thenReturn(sourceName);
		when(entity.getName()).thenReturn(name);
		when(entity.getAttributes()).thenReturn(Arrays.asList(attrId, attrName));
		when(entity.getAttribute("Name")).thenReturn(attrName);
		when(entity.getIDAttribute()).thenReturn(attrId);

		when(attrId.getEntityType()).thenReturn(entity);
		when(attrName.getEntityType()).thenReturn(entity);

		return entity;
	}
	
	private ApplicationContext mockContext() {
		ModelManager mm = mockModelManager();
		EntityManager em = Mockito.mock(EntityManager.class);
		
		SearchService ss = mock(SearchService.class);
		EntityType type = mm.getEntityType(Test.class);
		when(ss.listEntityTypes(Mockito.eq(Test.class))).thenReturn(Arrays.asList(type));
		when(ss.listSearchableTypes()).thenReturn(Arrays.asList(Test.class));
		
		Record record = new Record(mm.getEntityType(Test.class));
		record.addValue(ValueType.STRING.create("Id", "id1"));
		record.addValue(ValueType.STRING.create("Name", "Test-Name"));
		Result result = new Result();
		result.addRecord(record);
		when(ss.fetchResults(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList(result));
		
		ApplicationContext context = Mockito.mock(ApplicationContext.class);
		when(context.getEntityManager()).thenReturn(Optional.of(em));
		when(context.getModelManager()).thenReturn(Optional.of(mm));
		when(context.getSearchService()).thenReturn(Optional.of(ss));
		return context;
	}
	
	private ModelManager mockModelManager() {
		EntityType type = mockEntity("env1", "Test");

		ModelManager mm = mock(ModelManager.class);
		when(mm.getEntityType(Test.class)).thenReturn(type);
		when(mm.getEntityType("Test")).thenReturn(type);

		return mm;
	}

	@org.junit.Test
	public void testGetSuggestions() throws Exception {
		ApplicationContext context = mockContext();

		Record record = new Record(context.getModelManager().get().getEntityType(Test.class));
		record.addValue(ValueType.LONG.create("Id", 1L));
		record.addValue(ValueType.STRING.create("Name", "Test-Name"));
		Result result = new Result();
		result.addRecord(record);

		Query query = mock(Query.class);
		when(query.select(Mockito.any(Attribute.class))).thenReturn(query);
		when(query.group(Mockito.any(Attribute.class))).thenReturn(query);
		when(query.fetch()).thenReturn(Arrays.asList(result));
		
		org.eclipse.mdm.api.base.query.QueryService qs = mock(org.eclipse.mdm.api.base.query.QueryService.class);
		when(qs.createQuery()).thenReturn(query);
		when(context.getQueryService()).thenReturn(Optional.of(qs));

		ConnectorService connectorService = Mockito.mock(ConnectorService.class);
		when(connectorService.getContextByName("env1")).thenReturn(context);

		QueryService queryService = new QueryService();
		queryService.connectorService = connectorService;

		SuggestionRequest request = new SuggestionRequest();
		request.setSourceNames(Arrays.asList("env1"));
		request.setType("Test");
		request.setAttrName("Name");

		assertThat(queryService.getSuggestions(request)).contains("Test-Name");
	}
}
