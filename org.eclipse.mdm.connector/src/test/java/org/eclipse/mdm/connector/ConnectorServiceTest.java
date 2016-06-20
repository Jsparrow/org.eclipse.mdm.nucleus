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

import org.eclipse.mdm.api.base.EntityManagerFactory;
import org.eclipse.mdm.api.base.model.Core;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.connector.control.ServiceConfigurationActivity;
import org.eclipse.mdm.connector.entity.ServiceConfiguration;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * JUNIT Test for {@link ConnectorService}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class ConnectorServiceTest {

	@Test
	public void testGetEntityManagers() throws Exception {
		ConnectorService connectorService = createMockedconnectorService();
		List<EntityManager> emList = connectorService.getEntityManagers();

		assertNotNull("entity manager for uri should not be null", emList);
		assertNotEquals("size of entity manager list should be grater then 0", 0, emList.size());

	}


	@Test
	public void testGetEntityManagerByName() throws Exception {

		ConnectorService connectorService = createMockedconnectorService();
		EntityManager em = connectorService.getEntityManagerByName("MDMTest");
		assertNotNull("entity manager for uri should not be null", em);
	}


	@Test
	public void testConnect() throws Exception {

		Principal principal = Mockito.mock(Principal.class);
		when(principal.toString()).thenReturn("testuser");

		ConnectorService connectorService = createMockedconnectorService();
		List<EntityManager> emList = connectorService.connect("user", "password");

		assertNotNull("EntityManager list should not be null", emList);
		assertNotEquals("EntityManager list size be grather then 0", 0, emList.size());
	}


	@Test
	public void testRegisterConnections() throws Exception {

		Principal principal = Mockito.mock(Principal.class);
		when(principal.toString()).thenReturn("testuser");

		ConnectorService connectorService = createMockedconnectorService();
		List<EntityManager> emList = connectorService.connect("user", "password");

		connectorService.registerConnections(principal, emList);
	}


	@Test
	public void testDisconnect() throws Exception {

		Principal principal = Mockito.mock(Principal.class);
		when(principal.toString()).thenReturn("testuser");

		ConnectorService connectorService = createMockedconnectorService();
		connectorService.disconnect(principal);

	}



	@SuppressWarnings("unchecked")
	private ConnectorService createMockedconnectorService() throws Exception {

		Principal principal = Mockito.mock(Principal.class);
		when(principal.toString()).thenReturn("testuser");

		SessionContext sessionContext = Mockito.mock(SessionContext.class);
		when(sessionContext.getCallerPrincipal()).thenReturn(principal);

		Environment env = createEntityMock(Environment.class, "MDMTest", "MDMTest", 1L);

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

		ServiceConfigurationActivity scReaderMock = Mockito.mock(ServiceConfigurationActivity.class);
		when(scReaderMock.readServiceConfigurations()).thenReturn(scList);


		ConnectorService connectorService = new ConnectorService();

		Field scField = connectorService.getClass().getDeclaredField("sessionContext");
		scField.setAccessible(true);
		scField.set(connectorService, sessionContext);
		scField.setAccessible(false);

		Field mapField = connectorService.getClass().getDeclaredField("connectionMap");
		mapField.setAccessible(true);
		mapField.set(connectorService, map);
		mapField.setAccessible(false);

		Field emfField = connectorService.getClass().getDeclaredField("emf");
		emfField.setAccessible(true);
		emfField.set(connectorService, emf);
		emfField.setAccessible(false);

		Field scrField = connectorService.getClass().getDeclaredField("serviceConfigurationActivity");
		scrField.setAccessible(true);
		scrField.set(connectorService, scReaderMock);
		scrField.setAccessible(false);

		return connectorService;
	}


	private <T extends Entity> T createEntityMock(Class<T> type, String name,
			String sourceName, Long id) throws Exception {

		boolean accessible = false;
		Constructor<T> constructor = null;

		try {
			HashMap<String, Value> map = new HashMap<String, Value>();
			map.put("Name", ValueType.STRING.create("Name", name));

			Core core = Mockito.mock(Core.class);
			when(core.getSourceName()).thenReturn(sourceName);
			when(core.getValues()).thenReturn(map);
			when(core.getID()).thenReturn(id);

			constructor = type.getDeclaredConstructor(Core.class);
			accessible = constructor.isAccessible();
			constructor.setAccessible(true);
			T instance = constructor.newInstance(core);
			return instance;
		} finally {
			if(constructor != null) {
				constructor.setAccessible(accessible);
			}
		}
	}

}
