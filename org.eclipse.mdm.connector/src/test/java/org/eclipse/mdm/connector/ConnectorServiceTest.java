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

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ejb.SessionContext;

import org.eclipse.mdm.api.base.ConnectionException;
import org.eclipse.mdm.api.base.EntityManagerFactory;
import org.eclipse.mdm.api.base.model.Core;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.connector.boundary.ConnectorServiceException;
import org.eclipse.mdm.connector.control.ServiceConfigurationActivity;
import org.eclipse.mdm.connector.entity.ServiceConfiguration;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * JUNIT Test for {@link ConnectorService}
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 * @author Canoo Engineering (more tests)
 *
 */
@SuppressWarnings("javadoc")
public class ConnectorServiceTest {

	private final Principal testUser = new SimplePrincipal("testUser");
	private final Principal differentUser = new SimplePrincipal("differentUser");
	private final String testSourceName = "testSource";
	private final String differentSourceName = "differentSource";
	private final EntityManager testEntityManager = createEntityManager(testSourceName);

	@Test(expected = ConnectorServiceException.class)
	public void testGetEntityManagers_noConnectionsYet() throws Exception {
		ConnectorService connectorService = createMockedConnectorService(testUser);
		connectorService.getEntityManagers();
	}

	@Test(expected = ConnectorServiceException.class)
	public void testGetEntityManagers_connectionForDifferentUser() throws Exception {
		ConnectorService connectorService = createMockedConnectorService(testUser);
		connectorService.registerConnections(differentUser, Collections.singletonList(testEntityManager));
		connectorService.getEntityManagers();
	}

	@Test
	public void testGetEntityManagers_happyFlow() throws Exception {
		ConnectorService connectorService = createMockedConnectorService(testUser);
		connectorService.registerConnections(testUser, Collections.singletonList(testEntityManager));
		assertEquals(Collections.singletonList(testEntityManager), connectorService.getEntityManagers());
	}

	@Test(expected = ConnectorServiceException.class)
	public void testGetEntityManagerByName_noConnectionsYet() throws Exception {
		ConnectorService connectorService = createMockedConnectorService(testUser);
		connectorService.getEntityManagerByName(testSourceName);
	}

	@Test(expected = ConnectorServiceException.class)
	public void testGetEntityManagerByName_connectionForDifferentUser() throws Exception {
		ConnectorService connectorService = createMockedConnectorService(testUser);
		connectorService.registerConnections(differentUser, Collections.singletonList(testEntityManager));
		connectorService.getEntityManagerByName(testSourceName);
	}

	@Test(expected = ConnectorServiceException.class)
	public void testGetEntityManagerByName_differentSourceName() throws Exception {
		ConnectorService connectorService = createMockedConnectorService(testUser);
		connectorService.registerConnections(testUser, Collections.singletonList(testEntityManager));
		connectorService.getEntityManagerByName(differentSourceName);
	}

	@Test
	public void testGetEntityManagerByName_happyFlow() throws Exception {
		ConnectorService connectorService = createMockedConnectorService(testUser);
		connectorService.registerConnections(testUser, Collections.singletonList(testEntityManager));
		assertSame(testEntityManager, connectorService.getEntityManagerByName(testSourceName));
	}

	@Test
	public void testConnect() throws Exception {
		ConnectorService connectorService = createMockedConnectorService(testUser);
		List<EntityManager> emList = connectorService.connect("someUser", "somePassword");

		assertNotNull("EntityManager list should not be null", emList);
		assertNotEquals("EntityManager list size should be greater than 0", 0, emList.size());
	}

	@Test(expected = ConnectorServiceException.class)
	public void testRegisterConnections_emptyList() throws Exception {
		ConnectorService connectorService = createMockedConnectorService(testUser);
		connectorService.registerConnections(testUser, Collections.emptyList());
	}

	@Test
	public void testDisconnect() throws Exception {
		ConnectorService connectorService = createMockedConnectorService(testUser);
		connectorService.disconnect(testUser);

	}

	private static final class SimplePrincipal implements Principal {
		private final String name;

		SimplePrincipal(String name) {
			this.name = Objects.requireNonNull(name);
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof SimplePrincipal && ((SimplePrincipal) obj).name.equals(name));
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public String toString() {
			return name;
		}

	}

	private static ConnectorService createMockedConnectorService(Principal user) throws Exception {

		SessionContext sessionContextMock = Mockito.mock(SessionContext.class);
		when(sessionContextMock.getCallerPrincipal()).thenReturn(user);

		ServiceConfiguration serviceConfiguration = new ServiceConfiguration(TestEntityManagerFactory.class.getName(),
				Collections.emptyMap());
		ServiceConfigurationActivity scReaderMock = Mockito.mock(ServiceConfigurationActivity.class);
		when(scReaderMock.readServiceConfigurations()).thenReturn(Collections.singletonList(serviceConfiguration));

		ConnectorService connectorService = new ConnectorService();

		Field scField = connectorService.getClass().getDeclaredField("sessionContext");
		scField.setAccessible(true);
		scField.set(connectorService, sessionContextMock);

		Field scrField = connectorService.getClass().getDeclaredField("serviceConfigurationActivity");
		scrField.setAccessible(true);
		scrField.set(connectorService, scReaderMock);

		return connectorService;
	}

	public static final class TestEntityManagerFactory implements EntityManagerFactory<EntityManager> {

		@Override
		public EntityManager connect(Map<String, String> connectionParameters) throws ConnectionException {
			return createEntityManager("someSource");
		}

	}

	private static EntityManager createEntityManager(String sourceName) {
		Environment env = createEntityMock(Environment.class, "MDMTest", sourceName, 1L);

		EntityManager em = Mockito.mock(EntityManager.class);
		try {
			when(em.loadEnvironment()).thenReturn(env);
		} catch (@SuppressWarnings("unused") DataAccessException e) {
			// ignore - cannot happen
		}

		return em;
	}

	private static <T extends Entity> T createEntityMock(Class<T> type, String name, String sourceName, long id) {

		Map<String, Value> entityAttributes = new HashMap<>();
		entityAttributes.put("Name", ValueType.STRING.create("Name", name));

		Core core = Mockito.mock(Core.class);
		when(core.getSourceName()).thenReturn(sourceName);
		when(core.getValues()).thenReturn(entityAttributes);
		when(core.getID()).thenReturn(id);

		try {
			Constructor<T> constructor = type.getDeclaredConstructor(Core.class);
			constructor.setAccessible(true);
			return constructor.newInstance(core);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
