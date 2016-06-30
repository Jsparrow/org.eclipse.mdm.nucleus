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

package org.eclipse.mdm.businessobjects.control.context;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.mdm.api.base.model.ContextComponent;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Core;
import org.eclipse.mdm.api.base.model.Core.ChildrenStore;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.mockito.Mockito;


public  final class ContextActivityMockHelper {

	public static int ITEM_COUNT = 1;
	public static int CC_COUNT = 10;
	public static int CC_VALUE_COUNT = 10;

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

		Environment env = createEntityMock(Environment.class, sourceName, sourceName, 1L, null);

		EntityManager em = Mockito.mock(EntityManager.class);

		TestStep testStep = createEntityMock(TestStep.class, "TestStepWithContext", "MDM", 1L, null);
		Measurement measurement = createEntityMock(Measurement.class, "MeasurementWithContext", "MDM", 1L, null);

		when(em.loadEnvironment()).thenReturn(env);
		when(em.load(TestStep.class, 1L)).thenReturn(testStep);
		when(em.load(Measurement.class, 1L)).thenReturn(measurement);
		when(em.loadParent(measurement, Measurement.PARENT_TYPE_TESTSTEP)).thenReturn(Optional.of(testStep));

		List<Measurement> mList = new ArrayList<>();
		mList.add(measurement);

		when(em.loadChildren(testStep, TestStep.CHILD_TYPE_MEASUREMENT)).thenReturn(mList);

		Map<ContextType, ContextRoot> orderedContext = createContext("ordered");
		Map<ContextType, ContextRoot> measuredContext = createContext("measured");
		when(em.loadContexts(testStep)).thenReturn(orderedContext);
		when(em.loadContexts(measurement)).thenReturn(measuredContext);

		return em;
	}


	private static <T extends Entity> T createEntityMock(Class<T> type, String name, String sourceName, Long id, ChildrenStore childrenStore)
			throws Exception {

		HashMap<String, Value> map = new HashMap<String, Value>();
		map.put("Name", ValueType.STRING.create("Name", name));


		Core core = Mockito.mock(Core.class);
		when(core.getSourceName()).thenReturn(sourceName);
		when(core.getValues()).thenReturn(map);
		when(core.getID()).thenReturn(id);
		if(childrenStore != null) {
			when(core.getChildrenStore()).thenReturn(childrenStore);
		}
		
		if(ContextRoot.class.equals(type)) {
			if(name.contains("TSQ_")) {
				when(core.getTypeName()).thenReturn("TestSequence");
			} else if(name.contains("UUT")) {
				when(core.getTypeName()).thenReturn("UnitUnderTest");
			} else {
				when(core.getTypeName()).thenReturn("TestEquipment");
			}
		}

		Constructor<T> constructor  = type.getDeclaredConstructor(Core.class);
		constructor.setAccessible(true);
		T instance = constructor.newInstance(core);
		constructor.setAccessible(false);
		return instance;
	}


	private static ContextRoot createUUTContextRootMock(String type) throws Exception {

		List<ContextComponent> ccList = createContextComponentMocks("UUT");

		ChildrenStore childrenStore = new ChildrenStore();
		for(ContextComponent cc : ccList) {
			childrenStore.add(cc);
		}

		return createEntityMock(ContextRoot.class, "MessungUUT_" + type, "MDM", 1L, childrenStore);
	}

	private static ContextRoot createTSQContextRootMock(String type) throws Exception {

		List<ContextComponent> ccList = createContextComponentMocks("TSQ");

		ChildrenStore childrenStore = new ChildrenStore();
		for(ContextComponent cc : ccList) {
			childrenStore.add(cc);
		}

		return createEntityMock(ContextRoot.class, "MessungTSQ_" + type, "MDM", 1L, childrenStore);
	}

	private static ContextRoot createTEQContextRootMock(String type) throws Exception {

		List<ContextComponent> ccList = createContextComponentMocks("TEQ");

		ChildrenStore childrenStore = new ChildrenStore();
		for(ContextComponent cc : ccList) {
			childrenStore.add(cc);
		}

		return createEntityMock(ContextRoot.class, "MessungTEQ_" + type, "MDM", 1L, childrenStore);
	}

	private static List<ContextComponent> createContextComponentMocks(String type) throws Exception {
		List<ContextComponent> ccList = new ArrayList<>();
		for(int i=0; i< CC_COUNT; i++) {
			ContextComponent cc = createEntityMock(ContextComponent.class, type + "_ContextComponent_" + i, "MDM", Long.valueOf(i), null);
			when(cc.getValues()).thenReturn(createValues(type));
			ccList.add(cc);
		}
		return ccList;
	}

	private static Map<String, Value> createValues(String type) {
		Map<String, Value> valueMap = new HashMap<>();
		for(int i=0; i<CC_VALUE_COUNT; i++) {
			String vName = type + "_ValueName_" + i;
			valueMap.put(vName, ValueType.STRING.create(vName, type + "_Value_" + i));
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
