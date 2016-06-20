/*******************************************************************************
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Sebastian Dirsch - initial implementation
 *******************************************************************************/

package org.eclipse.mdm.businessobjects.control.search;

import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.mdm.api.base.model.Core;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.Filter;
import org.eclipse.mdm.api.base.query.Record;
import org.eclipse.mdm.api.base.query.Result;
import org.eclipse.mdm.api.base.query.SearchService;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class SearchMockHelper {

	public static int ITEM_COUNT = 3;

	public static EntityManager createEntityManagerMock() throws Exception {
		EntityManager em = Mockito.mock(EntityManager.class);
		Optional<SearchService> mockedSearchService = createSearchServiceMock();
		when(em.getSearchService()).thenReturn(mockedSearchService);
		return em;
	}

	public static Optional<SearchService> createSearchServiceMock() throws Exception {
		SearchService searchService = Mockito.mock(SearchService.class);
		List<EntityType> etResultMock = createETListMock();
		when(searchService.listEntityTypes(TestStep.class)).thenReturn(etResultMock);
		List<Attribute> attrList = new ArrayList<>();
		attrList.add(Mockito.mock(Attribute.class));
		Map<Entity, Result> mockedSearchResult = createMockedSearchRes(TestStep.class, "TestStep");
		when(searchService.fetch(Matchers.<Class<Entity>> any(), Matchers.anyListOf(Attribute.class),
				Matchers.any(Filter.class))).thenReturn(mockedSearchResult);
		Optional<SearchService> ret = Optional.of(searchService);
		return ret;
	}

	public static <T extends Entity> Map<Entity, Result> createMockedSearchRes(Class<T> clazz,
			String entityTypeName) throws Exception {

		Map<Entity, Result> mockedRes = new HashMap<>();

		for (int i = 0; i < ITEM_COUNT; i++) {
			Result result = new Result();
			EntityType etType = createEntityTypeMock(entityTypeName);
			result.addRecord(new Record(etType));
			T etMock = createEntityMock(clazz, entityTypeName + "_" + i, "Environment", Long.valueOf(i));
			mockedRes.put(etMock, result);
		}

		return mockedRes;
	}

	public static List<EntityType> createETListMock() {
		List<EntityType> mockedEntityList = new ArrayList<>();
		EntityType mockedETTest = createEntityTypeMock("Test");
		EntityType mockedETTestStep = createEntityTypeMock("TestStep");
		EntityType mockedETMeasurement = createEntityTypeMock("Measurement");
		mockedEntityList.add(mockedETTest);
		mockedEntityList.add(mockedETTestStep);
		mockedEntityList.add(mockedETMeasurement);
		return mockedEntityList;
	}

	private static EntityType createEntityTypeMock(String entityName) {
		EntityType mockedETTest = Mockito.mock(EntityType.class);
		List<Attribute> mockedAttributes = new ArrayList<>();
		Attribute attributeMock = Mockito.mock(Attribute.class);
		when(attributeMock.getName()).thenReturn("Name");
		when(attributeMock.getValueType()).thenReturn(ValueType.STRING);
		when(attributeMock.getEntityType()).thenReturn(mockedETTest);
		mockedAttributes.add(attributeMock);
		when(mockedETTest.getName()).thenReturn(entityName);
		when(mockedETTest.getAttributes()).thenReturn(mockedAttributes);
		return mockedETTest;
	}

	private static <T extends Entity> T createEntityMock(Class<T> type, String name, String sourceName, Long id) throws Exception {

		HashMap<String, Value> map = new HashMap<String, Value>();
		map.put("Name", ValueType.STRING.create("Name", name));

		Core core = Mockito.mock(Core.class);
		when(core.getSourceName()).thenReturn(sourceName);
		when(core.getValues()).thenReturn(map);
		when(core.getID()).thenReturn(id);

		Constructor<T> constructor = type.getDeclaredConstructor(Core.class);
		constructor.setAccessible(true);
		T instance = constructor.newInstance(core);
		constructor.setAccessible(false);
		return instance;
	}

}
