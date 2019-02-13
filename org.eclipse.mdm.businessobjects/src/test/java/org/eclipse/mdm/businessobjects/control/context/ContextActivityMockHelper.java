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


package org.eclipse.mdm.businessobjects.control.context;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdm.api.base.adapter.ChildrenStore;
import org.eclipse.mdm.api.base.adapter.Core;
import org.eclipse.mdm.api.base.model.ContextComponent;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.mockito.Mockito;

public final class ContextActivityMockHelper {

	public static final int ITEM_COUNT = 1;
	public static final int CC_COUNT = 10;
	public static final int CC_VALUE_COUNT = 10;
	
	private ContextActivityMockHelper() {
	}

	public static ConnectorService createConnectorMock() throws Exception {

		ConnectorService connectorBean = Mockito.mock(ConnectorService.class);

		List<ApplicationContext> contextList = new ArrayList<>();
		for (int i = 1; i <= ITEM_COUNT; i++) {
			contextList.add(createContextMock("MDMENV_" + i));
		}
		when(connectorBean.getContexts()).thenReturn(contextList);
		when(connectorBean.getContextByName(anyString())).thenReturn(contextList.get(0));
		return connectorBean;
	}

	private static ApplicationContext createContextMock(String sourceName) throws Exception {

		Environment env = createEntityMock(Environment.class, sourceName, sourceName, "1", null);

		EntityManager em = Mockito.mock(EntityManager.class);

		TestStep testStep = createEntityMock(TestStep.class, "TestStepWithContext", "MDM", "1", null);
		Measurement measurement = createEntityMock(Measurement.class, "MeasurementWithContext", "MDM", "1", null);

		when(em.loadEnvironment()).thenReturn(env);
		when(em.load(TestStep.class, "1")).thenReturn(testStep);
		when(em.load(Measurement.class, "1")).thenReturn(measurement);
		when(em.loadParent(measurement, Measurement.PARENT_TYPE_TESTSTEP)).thenReturn(Optional.of(testStep));

		List<Measurement> mList = new ArrayList<>();
		mList.add(measurement);

		when(em.loadChildren(testStep, TestStep.CHILD_TYPE_MEASUREMENT)).thenReturn(mList);

		Map<ContextType, ContextRoot> orderedContext = createContext("ordered");
		Map<ContextType, ContextRoot> measuredContext = createContext("measured");
		when(em.loadContexts(testStep)).thenReturn(orderedContext);
		when(em.loadContexts(measurement)).thenReturn(measuredContext);

		ApplicationContext context = Mockito.mock(ApplicationContext.class);
		when(context.getEntityManager()).thenReturn(Optional.of(em));
		return context;
	}

	private static <T extends Entity> T createEntityMock(Class<T> type, String name, String sourceName, String id,
			ChildrenStore childrenStore) throws Exception {

		HashMap<String, Value> map = new HashMap<>();
		map.put("Name", ValueType.STRING.create("Name", name));

		Core core = Mockito.mock(Core.class);
		when(core.getSourceName()).thenReturn(sourceName);
		when(core.getValues()).thenReturn(map);
		when(core.getID()).thenReturn(id);
		if (childrenStore != null) {
			when(core.getChildrenStore()).thenReturn(childrenStore);
		}

		if (ContextRoot.class.equals(type)) {
			if (StringUtils.contains(name, "TSQ_")) {
				when(core.getTypeName()).thenReturn("TestSequence");
			} else if (StringUtils.contains(name, "UUT")) {
				when(core.getTypeName()).thenReturn("UnitUnderTest");
			} else {
				when(core.getTypeName()).thenReturn("TestEquipment");
			}
		}

		Constructor<T> constructor = type.getDeclaredConstructor(Core.class);
		constructor.setAccessible(true);
		T instance = constructor.newInstance(core);
		constructor.setAccessible(false);
		return instance;
	}

	private static ContextRoot createUUTContextRootMock(String type) throws Exception {

		List<ContextComponent> ccList = createContextComponentMocks("UUT");

		ChildrenStore childrenStore = new ChildrenStore();
		ccList.forEach(childrenStore::add);

		return createEntityMock(ContextRoot.class, "MessungUUT_" + type, "MDM", "1", childrenStore);
	}

	private static ContextRoot createTSQContextRootMock(String type) throws Exception {

		List<ContextComponent> ccList = createContextComponentMocks("TSQ");

		ChildrenStore childrenStore = new ChildrenStore();
		ccList.forEach(childrenStore::add);

		return createEntityMock(ContextRoot.class, "MessungTSQ_" + type, "MDM", "1", childrenStore);
	}

	private static ContextRoot createTEQContextRootMock(String type) throws Exception {

		List<ContextComponent> ccList = createContextComponentMocks("TEQ");

		ChildrenStore childrenStore = new ChildrenStore();
		ccList.forEach(childrenStore::add);

		return createEntityMock(ContextRoot.class, "MessungTEQ_" + type, "MDM", "1", childrenStore);
	}

	private static List<ContextComponent> createContextComponentMocks(String type) throws Exception {
		List<ContextComponent> ccList = new ArrayList<>();
		for (int i = 1; i <= CC_COUNT; i++) {
			ContextComponent cc = createEntityMock(ContextComponent.class, new StringBuilder().append(type).append("_ContextComponent_").append(i).toString(), "MDM",
					Integer.toString(i), null);
			when(cc.getValues()).thenReturn(createValues(type));
			ccList.add(cc);
		}
		return ccList;
	}

	private static Map<String, Value> createValues(String type) {
		Map<String, Value> valueMap = new HashMap<>();
		for (int i = 1; i <= CC_VALUE_COUNT; i++) {
			String vName = new StringBuilder().append(type).append("_ValueName_").append(i).toString();
			valueMap.put(vName, ValueType.STRING.create(vName, new StringBuilder().append(type).append("_Value_").append(i).toString()));
		}
		return valueMap;
	}

	private static Map<ContextType, ContextRoot> createContext(String type) throws Exception {
		Map<ContextType, ContextRoot> contextMap = new HashMap<>();
		contextMap.put(ContextType.UNITUNDERTEST, createUUTContextRootMock(type));
		contextMap.put(ContextType.TESTSEQUENCE, createTSQContextRootMock(type));
		contextMap.put(ContextType.TESTEQUIPMENT, createTEQContextRootMock(type));
		return contextMap;
	}

}
