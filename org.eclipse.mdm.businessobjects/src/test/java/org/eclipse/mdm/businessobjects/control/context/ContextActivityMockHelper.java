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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.base.model.ContextComponent;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.EntityCore;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.base.model.EntityCore.ChildrenStore;
import org.mockito.Mockito;


public  final class ContextActivityMockHelper {

	public static int ITEM_COUNT = 1;
	public static int CC_COUNT = 10;
	public static int CC_VALUE_COUNT = 10;
	
	public static URI URI_TESTSTEP = new URI("MDM", "TestSTep", 1L);
	public static URI URI_MEASUREMENT = new URI("MDM", "TestSTep", 1L);
	
	public static ConnectorService createConnectorMock() throws Exception {
		
		ConnectorService connectorBean = Mockito.mock(ConnectorService.class);
		
		List<EntityManager> emList = new ArrayList<>();
		for(int i=0; i<ITEM_COUNT; i++) {
			emList.add(createEntityManagerMock("MDMENV_" + i));
		}
		when(connectorBean.getEntityManagers()).thenReturn(emList);
		when(connectorBean.getEntityManagerByName(anyString())).thenReturn(emList.get(0));
		when(connectorBean.getEntityManagerByURI(anyObject())).thenReturn(emList.get(0));
		return connectorBean;
	}
	
			
	private static EntityManager createEntityManagerMock(String sourceName) throws Exception {
		
		URI uri = new URI(sourceName, "Environment", 1L);
		Environment env = createEntityMock(Environment.class, uri, sourceName, null);
		
		EntityManager em = Mockito.mock(EntityManager.class);
		
		TestStep testStep = createEntityMock(TestStep.class, URI_TESTSTEP, "TestStepWithContext", null);
		Measurement measurement = createEntityMock(Measurement.class, URI_MEASUREMENT, "MeasurementWithContext", null);
		
		when(em.loadEnvironment()).thenReturn(env);
		when(em.load(URI_TESTSTEP)).thenReturn(Optional.of(testStep));	
		when(em.load(URI_MEASUREMENT)).thenReturn(Optional.of(measurement));
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
	

	private static <T extends Entity> T createEntityMock(Class<T> type, URI uri, String name,ChildrenStore childrenStore) 
		throws Exception {
		
		HashMap<String, Value> map = new HashMap<String, Value>();
		map.put("Name", ValueType.STRING.create("Name", name));
				
		
		EntityCore entityCore = Mockito.mock(EntityCore.class);		
		when(entityCore.getValues()).thenReturn(map);
		when(entityCore.getURI()).thenReturn(uri);	
		if(childrenStore != null) {
			when(entityCore.getChildrenStore()).thenReturn(childrenStore);
		}
		
		Constructor<T> constructor  = type.getDeclaredConstructor(EntityCore.class);
		constructor.setAccessible(true);
		T instance = constructor.newInstance(entityCore);
		constructor.setAccessible(false);
		return instance;
	}
	
	
	private static ContextRoot createUUTContextRootMock(String type) throws Exception {
		
		URI contextRootURI = new URI("MDM", "UnitUnderTest", 1L);		
		List<ContextComponent> ccList = createContextComponentMocks("UUT");		
		
		ChildrenStore childrenStore = new ChildrenStore();
		for(ContextComponent cc : ccList) {
			childrenStore.add(cc);
		}
		
		return createEntityMock(ContextRoot.class, contextRootURI, "MessungUUT_" + type, childrenStore);
	}
	
	private static ContextRoot createTSQContextRootMock(String type) throws Exception {
		
		URI contextRootURI = new URI("MDM", "TestSequence", 1L);		
		List<ContextComponent> ccList = createContextComponentMocks("TSQ");	
		
		ChildrenStore childrenStore = new ChildrenStore();
		for(ContextComponent cc : ccList) {
			childrenStore.add(cc);
		}
		
		return createEntityMock(ContextRoot.class, contextRootURI, "MessungTSQ_" + type, childrenStore);		
	}
	
	private static ContextRoot createTEQContextRootMock(String type) throws Exception {
		
		URI contextRootURI = new URI("MDM", "TestEquipment", 1L);		
		List<ContextComponent> ccList = createContextComponentMocks("TEQ");			
				
		ChildrenStore childrenStore = new ChildrenStore();
		for(ContextComponent cc : ccList) {
			childrenStore.add(cc);
		}
		
		return createEntityMock(ContextRoot.class, contextRootURI, "MessungTEQ_" + type, childrenStore);		
	}
	
	private static List<ContextComponent> createContextComponentMocks(String type) throws Exception {
		List<ContextComponent> ccList = new ArrayList<>();
		for(int i=0; i< CC_COUNT; i++) {
			URI ccURI = new URI("MDM", "CC_" + String.valueOf(System.currentTimeMillis()), Long.valueOf(i));
			ContextComponent cc = createEntityMock(ContextComponent.class, ccURI, type + "_ContextComponent_" + i, null);
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

