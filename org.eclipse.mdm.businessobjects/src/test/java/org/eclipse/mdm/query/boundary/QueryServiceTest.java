/*******************************************************************************
  * Copyright (c) 2017 Peak Solution GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Matthias Koller - initial implementation
  *******************************************************************************/
package org.eclipse.mdm.query.boundary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.api.base.query.Query;
import org.eclipse.mdm.api.base.query.Record;
import org.eclipse.mdm.api.base.query.Result;
import org.eclipse.mdm.api.base.query.SearchService;
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
	public void testQuery() throws DataAccessException {
		
		ModelManager mm = mockModelManager();
		EntityManager em = mockEntityManager(mm);
		
		ConnectorService connectorService = Mockito.mock(ConnectorService.class);
		when(connectorService.getEntityManagerByName("env1")).thenReturn(em);
		
		QueryService queryService = new QueryService();
		queryService.connectorService = connectorService;
		
		SourceFilter filter = new SourceFilter();
		filter.setSourceName("env1");
		filter.setFilter("Test.Name lk *");
		
		QueryRequest request = new QueryRequest();
		request.setResultType("Test");
		request.setColumns(Arrays.asList("Test.Id", "Test.Name"));
		request.setFilters(Arrays.asList(filter));

		Row expectedRow = new Row();
		expectedRow.setSource("env1");
		expectedRow.setType("Test");
		expectedRow.setId(1L);
		expectedRow.addColumns(Arrays.asList(
				new Column("Test", "Id", "1", null), 
				new Column("Test", "Name", "Test-Name", null)));
		
		assertThat(queryService.queryRows(request))
			.contains(expectedRow);
		
	}

	@org.junit.Test
	public void testQueryMissingEnvironmentShouldByIgnored() throws DataAccessException {
		
		ModelManager mm = mockModelManager();
		EntityManager em = mockEntityManager(mm);
		
		ConnectorService connectorService = Mockito.mock(ConnectorService.class);
		when(connectorService.getEntityManagerByName("env1")).thenReturn(em);
		doThrow(ConnectorServiceException.class).when(connectorService).getEntityManagerByName("env2");
		
		QueryService queryService = new QueryService();
		queryService.connectorService = connectorService;
		
		
		SourceFilter filterEnv1 = new SourceFilter();
		filterEnv1.setSourceName("env1");
		filterEnv1.setFilter("Test.Name lk *");
		
		SourceFilter filterEnv2 = new SourceFilter();
		filterEnv2.setSourceName("env2");
		filterEnv2.setFilter("Test.Name lk *");
		
		QueryRequest request = new QueryRequest();
		request.setResultType("Test");
		request.setColumns(Arrays.asList("Test.Id", "Test.Name", "Pool.Name"));
		request.setFilters(Arrays.asList(filterEnv1, filterEnv2));

		Row expectedRow = new Row();
		expectedRow.setSource("env1");
		expectedRow.setType("Test");
		expectedRow.setId(1L);
		expectedRow.addColumns(Arrays.asList(
				new Column("Test", "Id", "1", null), 
				new Column("Test", "Name", "Test-Name", null)));
		
		assertThat(queryService.queryRows(request))
			.contains(expectedRow);
	}
	
	@org.junit.Test
	public void testQueryMissingEntitiesShouldBeIgnored() throws DataAccessException {
		
		ModelManager mm1 = mockModelManager();
		EntityManager em1 = mockEntityManager(mm1);
		
		EntityType type = mockEntity("env2", "Test");
		EntityType pool = mockEntity("env2", "Pool");
		
		ModelManager mm2 = mock(ModelManager.class);
		when(mm2.getEntityType(Test.class)).thenReturn(type);
		when(mm2.getEntityType("Test")).thenReturn(type);
		when(mm2.getEntityType(Pool.class)).thenReturn(pool);
		when(mm2.getEntityType("Pool")).thenReturn(pool);
		
		EntityManager em2 = mockEntityManager(mm2);
		
		SearchService ss = mock(SearchService.class);
		EntityType test = mm2.getEntityType(Test.class);
		when(ss.listEntityTypes(Mockito.eq(Test.class))).thenReturn(Arrays.asList(test, pool));
		when(ss.listSearchableTypes()).thenReturn(Arrays.asList(Test.class));
		
		Record recordTest = new Record(mm2.getEntityType(Test.class));
		recordTest.addValue(ValueType.LONG.create("Id", 1L));
		recordTest.addValue(ValueType.STRING.create("Name", "Test-Name"));
		
		Record recordPool = new Record(mm2.getEntityType(Pool.class));
		recordPool.addValue(ValueType.STRING.create("Name", "Pool-Name"));
		Result result = new Result();
		result.addRecord(recordPool);
		result.addRecord(recordTest);
		when(em2.getSearchService().get().fetchResults(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(result));
		
		ConnectorService connectorService = Mockito.mock(ConnectorService.class);
		when(connectorService.getEntityManagerByName("env1")).thenReturn(em1);
		when(connectorService.getEntityManagerByName("env2")).thenReturn(em2);
		
		QueryService queryService = new QueryService();
		queryService.connectorService = connectorService;
		
		SourceFilter filterEnv1 = new SourceFilter();
		filterEnv1.setSourceName("env1");
		filterEnv1.setFilter("Test.Name lk *");
		
		SourceFilter filterEnv2 = new SourceFilter();
		filterEnv2.setSourceName("env2");
		filterEnv2.setFilter("Test.Name lk *");
		
		QueryRequest request = new QueryRequest();
		request.setResultType("Test");
		request.setColumns(Arrays.asList("Test.Id", "Test.Name", "Pool.Name"));
		request.setFilters(Arrays.asList(filterEnv1, filterEnv2));

		Row expectedRowEnv1 = new Row();
		expectedRowEnv1.setSource("env1");
		expectedRowEnv1.setType("Test");
		expectedRowEnv1.setId(1L);
		expectedRowEnv1.addColumns(Arrays.asList(
				new Column("Test", "Id", "1", null), 
				new Column("Test", "Name", "Test-Name", null)));
		
		Row expectedRowEnv2 = new Row();
		expectedRowEnv2.setSource("env2");
		expectedRowEnv2.setType("Test");
		expectedRowEnv2.setId(1L);
		expectedRowEnv2.addColumns(Arrays.asList(
				new Column("Test", "Id", "1", null), 
				new Column("Test", "Name", "Test-Name", null),
				new Column("Pool", "Name", "Pool-Name", null)
				));
		
		assertThat(queryService.queryRows(request))
			.containsOnly(expectedRowEnv1, expectedRowEnv2);
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
	
	private EntityManager mockEntityManager(ModelManager mm) throws DataAccessException {
		
		SearchService ss = mock(SearchService.class);
		EntityType type = mm.getEntityType(Test.class);
		when(ss.listEntityTypes(Mockito.eq(Test.class))).thenReturn(Arrays.asList(type));
		when(ss.listSearchableTypes()).thenReturn(Arrays.asList(Test.class));
		
		Record record = new Record(mm.getEntityType(Test.class));
		record.addValue(ValueType.LONG.create("Id", 1L));
		record.addValue(ValueType.STRING.create("Name", "Test-Name"));
		Result result = new Result();
		result.addRecord(record);
		when(ss.fetchResults(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Arrays.asList(result));
		
		EntityManager em = mock(EntityManager.class);
		when(em.getModelManager()).thenReturn(Optional.of(mm));
		when(em.getSearchService()).thenReturn(Optional.of(ss));
		return em;
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
		ModelManager mm = mockModelManager();
		EntityManager em = mockEntityManager(mm);
		
		Record record = new Record(mm.getEntityType(Test.class));
		record.addValue(ValueType.LONG.create("Id", 1L));
		record.addValue(ValueType.STRING.create("Name", "Test-Name"));
		Result result = new Result();
		result.addRecord(record);
		
		Query query = mock(Query.class);
		when(query.select(Mockito.any(Attribute.class))).thenReturn(query);
		when(query.group(Mockito.any(Attribute.class))).thenReturn(query);
		when(query.fetch()).thenReturn(Arrays.asList(result));
		when(mm.createQuery()).thenReturn(query);
		
		ConnectorService connectorService = Mockito.mock(ConnectorService.class);
		when(connectorService.getEntityManagerByName("env1")).thenReturn(em);
		
		QueryService queryService = new QueryService();
		queryService.connectorService = connectorService;
		
		SuggestionRequest request = new SuggestionRequest();
		request.setEnvironments(Arrays.asList("env1"));
		request.setType("Test");
		request.setAttrName("Name");
		
		assertThat(queryService.getSuggestions(request)).contains("Test-Name");
	}
}
