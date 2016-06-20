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

package org.eclipse.mdm.businessobjects.control.navigation;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Core;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.mockito.Mockito;


public  final class NavigationActivityMockHelper {

	public static int ITEM_COUNT = 5;


	public static ConnectorService createConnectorMock() throws Exception {

		ConnectorService connectorBean = Mockito.mock(ConnectorService.class);

		List<EntityManager> emList = new ArrayList<>();
		for(int i=0; i<ITEM_COUNT; i++) {
			emList.add(createEntityManagerMock("MDMENV_" + i));
		}
		when(connectorBean.getEntityManagers()).thenReturn(emList);
		when(connectorBean.getEntityManagerByName(anyString())).thenReturn(emList.get(0));
		return connectorBean;
	}


	private static EntityManager createEntityManagerMock(String sourceName) throws Exception {

		Environment env = createEntityMock(Environment.class, sourceName, sourceName, 1L);

		EntityManager em = Mockito.mock(EntityManager.class);


		when(em.loadEnvironment()).thenReturn(env);

		List<Test> testMocks = createTestMocks(ITEM_COUNT, sourceName);
		when(em.loadAll(Test.class)).thenReturn(testMocks);

		List<TestStep> testStepMocks = createTestStepMocks(ITEM_COUNT, sourceName);
		when(em.loadChildren(anyObject(), eq(TestStep.class))).thenReturn(testStepMocks);

		List<Measurement> measurementMocks = createMeasurementMocks(ITEM_COUNT, sourceName);
		when(em.loadChildren(anyObject(), eq(Measurement.class))).thenReturn(measurementMocks);

		List<ChannelGroup> channelGroupMocks = createChannelGroupMocks(ITEM_COUNT, sourceName);
		when(em.loadChildren(anyObject(), eq(ChannelGroup.class))).thenReturn(channelGroupMocks);

		List<Channel> channelMocks = createChannelMocks(ITEM_COUNT, sourceName);
		when(em.loadChildren(anyObject(), eq(Channel.class))).thenReturn(channelMocks);

		Optional<ModelManager> modelManagerMock = createModelManagerMock();
		when(em.getModelManager()).thenReturn(modelManagerMock);

		return em;
	}


	private static List<Test> createTestMocks(int count, String sourceName) throws Exception {
		List<Test> list = new ArrayList<>();
		for(int i=0; i<count; i++) {
			list.add(createEntityMock(Test.class, "Test_" + count,  sourceName, Long.valueOf(count)));
		}
		return list;
	}


	private static List<TestStep> createTestStepMocks(int count, String sourceName) throws Exception {
		List<TestStep> list = new ArrayList<>();
		for(int i=0; i<count; i++) {
			list.add(createEntityMock(TestStep.class, "TestStep_" + count,  sourceName, Long.valueOf(count)));
		}
		return list;
	}


	private static List<Measurement> createMeasurementMocks(int count, String sourceName) throws Exception {
		List<Measurement> list = new ArrayList<>();
		for(int i=0; i<count; i++) {
			list.add(createEntityMock(Measurement.class, "Measurement_" + count,  sourceName, Long.valueOf(count)));
		}
		return list;
	}


	private static List<ChannelGroup> createChannelGroupMocks(int count, String sourceName) throws Exception {
		List<ChannelGroup> list = new ArrayList<>();
		for(int i=0; i<count; i++) {
			list.add(createEntityMock(ChannelGroup.class, "ChannelGroup_" + count, sourceName, Long.valueOf(count)));
		}
		return list;
	}


	private static List<Channel> createChannelMocks(int count, String sourceName) throws Exception {
		List<Channel> list = new ArrayList<>();
		for(int i=0; i<count; i++) {
			list.add(createEntityMock(Channel.class, "Channel_" + count, sourceName, Long.valueOf(count)));
		}
		return list;
	}


	private static Optional<ModelManager> createModelManagerMock() throws Exception {


		EntityType envET = Mockito.mock(EntityType.class);
		when(envET.getName()).thenReturn("Environment");

		EntityType testET = Mockito.mock(EntityType.class);
		when(testET.getName()).thenReturn("Test");

		EntityType testStepET = Mockito.mock(EntityType.class);
		when(testStepET.getName()).thenReturn("TestStep");

		EntityType measurementET = Mockito.mock(EntityType.class);
		when(measurementET.getName()).thenReturn("Measurement");

		EntityType channelGroupET = Mockito.mock(EntityType.class);
		when(channelGroupET.getName()).thenReturn("ChannelGroup");

		EntityType channelET = Mockito.mock(EntityType.class);
		when(channelET.getName()).thenReturn("Channel");

		ModelManager modelManager = Mockito.mock(ModelManager.class);
		when(modelManager.getEntityType(Environment.class)).thenReturn(envET);
		when(modelManager.getEntityType(Test.class)).thenReturn(testET);
		when(modelManager.getEntityType(TestStep.class)).thenReturn(testStepET);
		when(modelManager.getEntityType(Measurement.class)).thenReturn(measurementET);
		when(modelManager.getEntityType(ChannelGroup.class)).thenReturn(channelGroupET);
		when(modelManager.getEntityType(Channel.class)).thenReturn(channelET);

		return Optional.of(modelManager);
	}

	private static <T extends Entity> T createEntityMock(Class<T> type, String name, String sourceName, Long id) throws Exception {

		HashMap<String, Value> map = new HashMap<String, Value>();
		map.put("Name", ValueType.STRING.create("Name", name));

		Core core = Mockito.mock(Core.class);
		when(core.getSourceName()).thenReturn(sourceName);
		when(core.getValues()).thenReturn(map);
		when(core.getID()).thenReturn(id);

		Constructor<T> constructor  = type.getDeclaredConstructor(Core.class);
		constructor.setAccessible(true);
		T instance = constructor.newInstance(core);
		constructor.setAccessible(false);
		return instance;
	}

}

