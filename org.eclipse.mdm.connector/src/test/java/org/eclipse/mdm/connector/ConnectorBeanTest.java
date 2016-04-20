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

package org.eclipse.mdm.connector;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.SessionContext;

import org.eclipse.mdm.api.base.EntityManager;
import org.eclipse.mdm.api.base.EntityManagerFactory;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.EntityCore;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.connector.bean.ConnectorBean;
import org.eclipse.mdm.connector.bean.ServiceConfiguration;
import org.eclipse.mdm.connector.bean.ServiceConfigurationReader;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * JUNIT Test for {@link ConnectorBean}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class ConnectorBeanTest {

	@Test
	public void testGetEntityManagers() {
		ConnectorException connectorException = null;
		Exception otherException = null;
		
		try {
			ConnectorBeanLI connectorBean = createMockedConnectorBean();		
			List<EntityManager> emList = connectorBean.getEntityManagers();
			
			assertNotNull("entity manager for uri should not be null", emList);
			assertNotEquals("size of entity manager list should be grater then 0", 0, emList.size());
			
		} catch(ConnectorException e) {
			connectorException = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no connector exception should be thrown", connectorException);
		assertNull("no other exception should be thrown", otherException);
	}
	
	
	@Test
	public void testGetEntityManagerByURI() {
		
		ConnectorException connectorException = null;
		Exception otherException = null;
		
		try {
			ConnectorBeanLI connectorBean = createMockedConnectorBean();		
			EntityManager em = connectorBean.getEntityManagerByURI(new URI("MDMTest", "TestType", 1L));			
			assertNotNull("entity manager for uri should not be null", em);
			
		} catch(ConnectorException e) {
			connectorException = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no connector exception should be thrown", connectorException);
		assertNull("no other exception should be thrown", otherException);
	}	
	
	@Test
	public void testGetEntityManagerByName() {
		
		ConnectorException connectorException = null;
		Exception otherException = null;
		
		try {
			ConnectorBeanLI connectorBean = createMockedConnectorBean();		
			EntityManager em = connectorBean.getEntityManagerByName("MDMTest");			
			assertNotNull("entity manager for uri should not be null", em);
			
		} catch(ConnectorException e) {
			connectorException = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no connector exception should be thrown", connectorException);
		assertNull("no other exception should be thrown", otherException);
	}
	
	
	@Test
	public void testConnect() {
		ConnectorException connectorException = null;
		Exception otherException = null;
		
		try {
			Principal principal = Mockito.mock(Principal.class);
			when(principal.toString()).thenReturn("testuser");
			
			ConnectorBeanLI connectorBean = createMockedConnectorBean();		
			List<EntityManager> emList = connectorBean.connect("user", "password");
			
			assertNotNull("EntityManager list should not be null", emList);
			assertNotEquals("EntityManager list size be grather then 0", 0, emList.size());
			
		} catch(ConnectorException e) {
			connectorException = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no ConnectorException should be thrown", connectorException);
		assertNull("no other exception should be thrown", otherException);
	}
	
	
	@Test
	public void testRegisterConnections() {
		ConnectorException connectorException = null;
		Exception otherException = null;
		
		try {
			Principal principal = Mockito.mock(Principal.class);
			when(principal.toString()).thenReturn("testuser");
			
			ConnectorBeanLI connectorBean = createMockedConnectorBean();		
			List<EntityManager> emList = connectorBean.connect("user", "password");
			
			connectorBean.registerConnections(principal, emList);
			
		} catch(ConnectorException e) {
			connectorException = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no ConnectorException should be thrown", connectorException);
		assertNull("no other exception should be thrown", otherException);
	}
	
	
	@Test
	public void testDisconnect() {
		ConnectorException connectorException = null;
		Exception otherException = null;
		
		try {
			Principal principal = Mockito.mock(Principal.class);
			when(principal.toString()).thenReturn("testuser");
			
			ConnectorBeanLI connectorBean = createMockedConnectorBean();		
			connectorBean.disconnect(principal);
						
		} catch(ConnectorException e) {
			connectorException = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no connector exception should be thrown", connectorException);
		assertNull("no other exception should be thrown", otherException);
	}
	
	
	
	@SuppressWarnings("unchecked")
	private ConnectorBeanLI createMockedConnectorBean() throws Exception {
		
		Principal principal = Mockito.mock(Principal.class);
		when(principal.toString()).thenReturn("testuser");
		
		SessionContext sessionContext = Mockito.mock(SessionContext.class);	
		when(sessionContext.getCallerPrincipal()).thenReturn(principal);
		
		Environment env = createEntityMock(Environment.class, "MDMTest", new URI("MDMTest", "Environment", 1L));
				
		EntityManager em = Mockito.mock(EntityManager.class);
		when(em.loadEnvironment()).thenReturn(env);
	    
		List<EntityManager> emList = new ArrayList<EntityManager>();
		emList.add(em);					

		EntityManagerFactory<EntityManager> emf = Mockito.mock(EntityManagerFactory.class);
		when(emf.connect(anyObject())).thenReturn(em);
		
		Map<Principal, List<EntityManager>> map = new HashMap<>();
		map.put(principal, emList);
	
		List<ServiceConfiguration> scList = new ArrayList<>();
		scList.add(new ServiceConfiguration("nameService", "serviceName"));
		
		ServiceConfigurationReader scReaderMock = Mockito.mock(ServiceConfigurationReader.class);
		when(scReaderMock.readServiceConfigurations()).thenReturn(scList);
		
		
		ConnectorBeanLI connectorBean = new ConnectorBean();
	
		Field scField = connectorBean.getClass().getDeclaredField("sessionContext");
		scField.setAccessible(true);
		scField.set(connectorBean, sessionContext);
		scField.setAccessible(false);		
		
		Field mapField = connectorBean.getClass().getDeclaredField("connectionMap");
		mapField.setAccessible(true);
		mapField.set(connectorBean, map);
		mapField.setAccessible(false);	
				
		Field emfField = connectorBean.getClass().getDeclaredField("emf");
		emfField.setAccessible(true);
		emfField.set(connectorBean, emf);
		emfField.setAccessible(false);	
		
		Field scrField = connectorBean.getClass().getDeclaredField("serviceConfigurationReader");
		scrField.setAccessible(true);
		scrField.set(connectorBean, scReaderMock);
		scrField.setAccessible(false);
		
		return connectorBean;
	}
	
	
	private <T extends Entity> T createEntityMock(Class<T> type, String name, URI uri) throws Exception {
		
		boolean accessible = false;
		Constructor<T> constructor = null;
		
		try {
			HashMap<String, Value> map = new HashMap<String, Value>();
			map.put("Name", ValueType.STRING.create("Name", name));
			
			EntityCore entityCore = Mockito.mock(EntityCore.class);		
			when(entityCore.getValues()).thenReturn(map);
			when(entityCore.getURI()).thenReturn(uri);
			
			constructor = type.getDeclaredConstructor(EntityCore.class);
			accessible = constructor.isAccessible();
			constructor.setAccessible(true);
			T instance = constructor.newInstance(entityCore);
			return instance;
		} finally {
			if(constructor != null) {
				constructor.setAccessible(accessible);
			}
		}
	}

}
