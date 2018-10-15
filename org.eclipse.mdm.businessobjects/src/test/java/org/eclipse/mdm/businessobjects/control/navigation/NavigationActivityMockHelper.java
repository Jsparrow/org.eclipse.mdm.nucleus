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


package org.eclipse.mdm.businessobjects.control.navigation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.eclipse.mdm.api.base.adapter.Core;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.adapter.ModelManager;
import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.businessobjects.boundary.EnvironmentService;
import org.eclipse.mdm.connector.boundary.ConnectorService;

import com.google.common.collect.ImmutableList;

public final class NavigationActivityMockHelper {

	public static final int ITEM_COUNT = 5;
	
	private NavigationActivityMockHelper() {
	}

	public static ConnectorService createConnectorMock() throws Exception {

		ConnectorService connectorBean = mock(ConnectorService.class);

		List<ApplicationContext> contextList = new ArrayList<>();
		for (int i = 1; i <= ITEM_COUNT; i++) {
			contextList.add(createContextMock("MDMENV_" + i));
		}
		when(connectorBean.getContexts()).thenReturn(contextList);
		when(connectorBean.getContextByName(anyString())).thenReturn(contextList.get(0));
		return connectorBean;
	}

	private static ApplicationContext createContextMock(String sourceName) throws Exception {

		Environment env = createEntityMock(Environment.class, sourceName, sourceName, "1");

		EntityManager em = mock(EntityManager.class);

		when(em.loadEnvironment()).thenReturn(env);

		List<Test> testMocks = createTestMocks(ITEM_COUNT, sourceName);
		when(em.loadAll(Test.class)).thenReturn(testMocks);

		List<TestStep> testStepMocks = createTestStepMocks(ITEM_COUNT, sourceName);
		when(em.loadChildren(any(), eq(TestStep.class))).thenReturn(testStepMocks);

		List<Measurement> measurementMocks = createMeasurementMocks(ITEM_COUNT, sourceName);
		when(em.loadChildren(any(), eq(Measurement.class))).thenReturn(measurementMocks);

		List<ChannelGroup> channelGroupMocks = createChannelGroupMocks(ITEM_COUNT, sourceName);
		when(em.loadChildren(any(), eq(ChannelGroup.class))).thenReturn(channelGroupMocks);

		List<Channel> channelMocks = createChannelMocks(ITEM_COUNT, sourceName);
		when(em.loadChildren(any(), eq(Channel.class))).thenReturn(channelMocks);

		ModelManager modelManagerMock = createModelManagerMock();

		ApplicationContext context = mock(ApplicationContext.class);
		when(context.getEntityManager()).thenReturn(Optional.of(em));
		when(context.getModelManager()).thenReturn(Optional.of(modelManagerMock));
		return context;
	}

	private static List<Test> createTestMocks(int count, String sourceName) throws Exception {
		List<Test> list = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			list.add(createEntityMock(Test.class, "Test_" + count, sourceName, Integer.toString(count)));
		}
		return list;
	}

	private static List<TestStep> createTestStepMocks(int count, String sourceName) throws Exception {
		List<TestStep> list = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			list.add(createEntityMock(TestStep.class, "TestStep_" + count, sourceName, Integer.toString(count)));
		}
		return list;
	}

	private static List<Measurement> createMeasurementMocks(int count, String sourceName) throws Exception {
		List<Measurement> list = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			list.add(createEntityMock(Measurement.class, "Measurement_" + count, sourceName, Integer.toString(count)));
		}
		return list;
	}

	private static List<ChannelGroup> createChannelGroupMocks(int count, String sourceName) throws Exception {
		List<ChannelGroup> list = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			list.add(createEntityMock(ChannelGroup.class, "ChannelGroup_" + count, sourceName, Integer.toString(count)));
		}
		return list;
	}

	private static List<Channel> createChannelMocks(int count, String sourceName) throws Exception {
		List<Channel> list = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			list.add(createEntityMock(Channel.class, "Channel_" + count, sourceName, Integer.toString(count)));
		}
		return list;
	}

	private static ModelManager createModelManagerMock() throws Exception {

		EntityType envET = mock(EntityType.class);
		when(envET.getName()).thenReturn("Environment");

		EntityType testET = mock(EntityType.class);
		when(testET.getName()).thenReturn("Test");

		EntityType testStepET = mock(EntityType.class);
		when(testStepET.getName()).thenReturn("TestStep");

		EntityType measurementET = mock(EntityType.class);
		when(measurementET.getName()).thenReturn("Measurement");

		EntityType channelGroupET = mock(EntityType.class);
		when(channelGroupET.getName()).thenReturn("ChannelGroup");

		EntityType channelET = mock(EntityType.class);
		when(channelET.getName()).thenReturn("Channel");

		ModelManager modelManager = mock(ModelManager.class);
		when(modelManager.getEntityType(Environment.class)).thenReturn(envET);
		when(modelManager.getEntityType(Test.class)).thenReturn(testET);
		when(modelManager.getEntityType(TestStep.class)).thenReturn(testStepET);
		when(modelManager.getEntityType(Measurement.class)).thenReturn(measurementET);
		when(modelManager.getEntityType(ChannelGroup.class)).thenReturn(channelGroupET);
		when(modelManager.getEntityType(Channel.class)).thenReturn(channelET);

		return modelManager;
	}

	private static <T extends Entity> T createEntityMock(Class<T> type, String name, String sourceName, String id)
			throws Exception {

		HashMap<String, Value> map = new HashMap<String, Value>();
		map.put("Name", ValueType.STRING.create("Name", name));

		Core core = mock(Core.class);
		when(core.getSourceName()).thenReturn(sourceName);
		when(core.getValues()).thenReturn(map);
		when(core.getID()).thenReturn(id);

		Constructor<T> constructor = type.getDeclaredConstructor(Core.class);
		constructor.setAccessible(true);
		T instance = constructor.newInstance(core);
		constructor.setAccessible(false);
		return instance;
	}

	public static EnvironmentService createEnvironmentMock() {
		EnvironmentService environmentService = mock(EnvironmentService.class);
		Environment env = mock(Environment.class);

		when(env.getSourceName()).thenReturn("MDMENV_1");
		when(environmentService.getEnvironments()).thenReturn(ImmutableList.of(env));

		return environmentService;
	}
}
