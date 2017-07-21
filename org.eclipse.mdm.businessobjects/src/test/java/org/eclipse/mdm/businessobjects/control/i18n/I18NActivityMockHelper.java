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

package org.eclipse.mdm.businessobjects.control.i18n;

import static org.mockito.Matchers.anyString;
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
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.mockito.Mockito;

public final class I18NActivityMockHelper {

	public static int ITEM_COUNT = 3;
	public static int ATTRIBUTE_COUNT = 2;

	public static ConnectorService createConnectorMock() throws Exception {

		ConnectorService connectorBean = Mockito.mock(ConnectorService.class);

		List<EntityManager> emList = new ArrayList<>();
		for (int i = 0; i < ITEM_COUNT; i++) {
			emList.add(createEntityManagerMock("MDMENV_" + i));
		}
		when(connectorBean.getEntityManagers()).thenReturn(emList);
		when(connectorBean.getEntityManagerByName(anyString())).thenReturn(emList.get(0));
		return connectorBean;
	}

	private static EntityManager createEntityManagerMock(String sourceName) throws Exception {

		Environment env = createEntityMock(Environment.class, sourceName, sourceName, "1");

		EntityManager em = Mockito.mock(EntityManager.class);

		when(em.loadEnvironment()).thenReturn(env);

		Optional<ModelManager> modelManagerMock = createModelManagerMock();
		when(em.getModelManager()).thenReturn(modelManagerMock);

		return em;
	}

	private static Optional<ModelManager> createModelManagerMock() throws Exception {

		List<EntityType> etList = new ArrayList<>();

		EntityType envET = Mockito.mock(EntityType.class);
		List<Attribute> envAttrList = createAttributeList(Environment.class, ATTRIBUTE_COUNT);
		when(envET.getName()).thenReturn("Environment");
		when(envET.getAttributes()).thenReturn(envAttrList);
		etList.add(envET);

		EntityType testET = Mockito.mock(EntityType.class);
		List<Attribute> testAttrList = createAttributeList(Test.class, ATTRIBUTE_COUNT);
		when(testET.getName()).thenReturn("Test");
		when(testET.getAttributes()).thenReturn(testAttrList);
		etList.add(testET);

		EntityType testStepET = Mockito.mock(EntityType.class);
		List<Attribute> testStepAttrList = createAttributeList(TestStep.class, ATTRIBUTE_COUNT);
		when(testStepET.getName()).thenReturn("TestStep");
		when(testStepET.getAttributes()).thenReturn(testStepAttrList);
		etList.add(testStepET);

		EntityType measurementET = Mockito.mock(EntityType.class);
		List<Attribute> measurementAttrList = createAttributeList(Measurement.class, ATTRIBUTE_COUNT);
		when(measurementET.getName()).thenReturn("Measurement");
		when(measurementET.getAttributes()).thenReturn(measurementAttrList);
		etList.add(measurementET);

		EntityType channelGroupET = Mockito.mock(EntityType.class);
		List<Attribute> channelGroupAttrList = createAttributeList(ChannelGroup.class, ATTRIBUTE_COUNT);
		when(channelGroupET.getName()).thenReturn("ChannelGroup");
		when(channelGroupET.getAttributes()).thenReturn(channelGroupAttrList);
		etList.add(channelGroupET);

		EntityType channelET = Mockito.mock(EntityType.class);
		List<Attribute> channelAttrList = createAttributeList(Channel.class, ATTRIBUTE_COUNT);
		when(channelET.getName()).thenReturn("Channel");
		when(channelET.getAttributes()).thenReturn(channelAttrList);
		etList.add(channelET);

		ModelManager modelManager = Mockito.mock(ModelManager.class);
		when(modelManager.getEntityType(Environment.class)).thenReturn(envET);
		when(modelManager.getEntityType(Test.class)).thenReturn(testET);
		when(modelManager.getEntityType(TestStep.class)).thenReturn(testStepET);
		when(modelManager.getEntityType(Measurement.class)).thenReturn(measurementET);
		when(modelManager.getEntityType(ChannelGroup.class)).thenReturn(channelGroupET);
		when(modelManager.getEntityType(Channel.class)).thenReturn(channelET);
		when(modelManager.listEntityTypes()).thenReturn(etList);
		return Optional.of(modelManager);
	}

	private static List<Attribute> createAttributeList(Class<? extends Entity> type, long count) {
		List<Attribute> attributeList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			Attribute attribute = Mockito.mock(Attribute.class);
			when(attribute.getName()).thenReturn(type.getSimpleName() + "_attribute_" + i);
			attributeList.add(attribute);
		}
		return attributeList;
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
