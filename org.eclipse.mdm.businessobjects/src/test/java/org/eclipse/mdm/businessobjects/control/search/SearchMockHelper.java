/*******************************************************************************
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Sebastian Dirsch - initial implementation
 *******************************************************************************/

package org.eclipse.mdm.businessobjects.control.search;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.adapter.ModelManager;
import org.eclipse.mdm.api.base.core.Core;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.EnumRegistry;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.base.query.Filter;
import org.eclipse.mdm.api.base.search.SearchService;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

public class SearchMockHelper {

	public static int ITEM_COUNT = 27;

	public static ApplicationContext createContextMock() throws Exception {
		ApplicationContext context = Mockito.mock(ApplicationContext.class);
		List<EntityType> etResultMock = createETListMock();
		Optional<SearchService> mockedSearchService = createSearchServiceMock(etResultMock);
		when(context.getSearchService()).thenReturn(mockedSearchService);
		Optional<ModelManager> mockedModelManager = createModelManagerMock(etResultMock);
		when(context.getModelManager()).thenReturn(mockedModelManager);
		return context;
	}

	public static Optional<ModelManager> createModelManagerMock(List<EntityType> etResultMock) {
		ModelManager modelManager = Mockito.mock(ModelManager.class);
		
		when(modelManager.getEntityType("Test")).thenReturn(etResultMock.get(0));
		when(modelManager.getEntityType("TestStep")).thenReturn(etResultMock.get(1));
		when(modelManager.getEntityType("Measurement")).thenReturn(etResultMock.get(2));
		
		return Optional.of(modelManager);
	}
	
	public static Optional<SearchService> createSearchServiceMock(List<EntityType> etResultMock) throws Exception {
		SearchService searchService = Mockito.mock(SearchService.class);
		when(searchService.listEntityTypes(TestStep.class)).thenReturn(etResultMock);
		List<Attribute> attrList = new ArrayList<>();
		attrList.add(Mockito.mock(Attribute.class));
		List<Entity> mockedSearchResult = createMockedSearchRes(TestStep.class, "TestStep");
		when(searchService.fetch(any(), anyList(), any(Filter.class))).thenReturn(mockedSearchResult);
		Optional<SearchService> ret = Optional.of(searchService);
		return ret;
	}

	public static <T extends Entity> List<Entity> createMockedSearchRes(Class<T> clazz, String entityTypeName)
			throws Exception {

		List<Entity> mockedRes = new ArrayList<>();

		for (int i = 1; i <= ITEM_COUNT; i++) {
			T etMock = createEntityMock(clazz, entityTypeName + "_" + i, "Environment", Integer.toString(i));
			mockedRes.add(etMock);
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
		
		// Initialize enums or else ValueType.name() returns null
		EnumRegistry.getInstance();
		
		mockedAttributes.add(createAttributeMock(mockedETTest, "Name", ValueType.STRING));
		mockedAttributes.add(createAttributeMock(mockedETTest, "DateAttribute", ValueType.DATE));
		mockedAttributes.add(createAttributeMock(mockedETTest, "BooleanAttribute", ValueType.BOOLEAN));
		mockedAttributes.add(createAttributeMock(mockedETTest, "ByteAttribute", ValueType.BYTE));
		mockedAttributes.add(createAttributeMock(mockedETTest, "ShortAttribute", ValueType.SHORT));
		mockedAttributes.add(createAttributeMock(mockedETTest, "IntegerAttribute", ValueType.INTEGER));
		mockedAttributes.add(createAttributeMock(mockedETTest, "LongAttribute", ValueType.LONG));
		mockedAttributes.add(createAttributeMock(mockedETTest, "FloatAttribute", ValueType.FLOAT));
		mockedAttributes.add(createAttributeMock(mockedETTest, "DoubleAttribute", ValueType.DOUBLE));
		
		when(mockedETTest.getName()).thenReturn(entityName);
		when(mockedETTest.getAttributes()).thenReturn(mockedAttributes);
		when(mockedETTest.getAttribute(any())).thenAnswer((InvocationOnMock invocation) -> (Attribute) mockedAttributes.stream().filter(a -> a.getName().equals(invocation.getArgument(0))).findAny().get());
		return mockedETTest;
	}
	
	private static Attribute createAttributeMock(EntityType entity, String attributeName, ValueType<?> valueType) 
	{
		Attribute attributeMock = Mockito.mock(Attribute.class);
		when(attributeMock.getName()).thenReturn(attributeName);
		when(attributeMock.getValueType()).thenReturn(valueType);
		when(attributeMock.createValue(any())).thenCallRealMethod();
		when(attributeMock.createValue(any(), any())).thenCallRealMethod();
		when(attributeMock.createValue(any(), anyBoolean(), any())).thenCallRealMethod();
		when(attributeMock.createValueSeq(any(), any())).thenCallRealMethod();
		when(attributeMock.getEntityType()).thenReturn(entity);
		return attributeMock;
	}

	private static <T extends Entity> T createEntityMock(Class<T> type, String name, String sourceName, String id)
			throws Exception {

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
