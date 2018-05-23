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

package org.eclipse.mdm.connector.boundary;

import org.eclipse.mdm.api.base.ConnectionException;
import org.eclipse.mdm.api.base.adapter.Core;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.ApplicationContextFactory;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.control.ServiceConfigurationActivity;
import org.eclipse.mdm.connector.entity.ServiceConfiguration;
import org.junit.Test;

import javax.ejb.SessionContext;
import java.lang.reflect.Constructor;
import java.security.Principal;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
	private static final String testSourceName = "testSource";
	private final String differentSourceName = "differentSource";

	@Test
	public void testGetEntityManagers_happyFlow() throws Exception {
		ConnectorService connectorService = createConnectorService(testUser);
		connectorService.connect();
		assertThat(connectorService.getContexts().size(), is(1));
	}

	@Test(expected = ConnectorServiceException.class)
	public void testGetEntityManagerByName_differentSourceName() throws Exception {
		ConnectorService connectorService = createConnectorService(testUser);
		connectorService.connect();
		connectorService.getContextByName(differentSourceName);
	}

	@Test
	public void testGetEntityManagerByName_happyFlow() throws Exception {
		ConnectorService connectorService = createConnectorService(testUser);
		connectorService.connect();
		assertNotNull(connectorService.getContextByName(testSourceName));
	}

	@Test
	public void testDisconnect() throws Exception {
		ConnectorService connectorService = createConnectorService(testUser);
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

	private static ConnectorService createConnectorService(Principal user) throws Exception {

		SessionContext sessionContextMock = mock(SessionContext.class);
		when(sessionContextMock.getCallerPrincipal()).thenReturn(user);

		ServiceConfiguration serviceConfiguration = new ServiceConfiguration(TestContextFactory.class.getName(), Collections.emptyMap());

		ConnectorService connectorService = new ConnectorService();

		connectorService.principal = user;

		ServiceConfigurationActivity serviceConfigurationActivity = mock(ServiceConfigurationActivity.class);
		when(serviceConfigurationActivity.readServiceConfigurations()).thenReturn(Collections.singletonList(serviceConfiguration));
		connectorService.serviceConfigurationActivity = serviceConfigurationActivity;

		return connectorService;
	}

	public static final class TestContextFactory implements ApplicationContextFactory {

		@Override
		public ApplicationContext connect(Map<String, String> connectionParameters) throws ConnectionException {
			return createContext(testSourceName);
		}
	}


	private static ApplicationContext createContext(String sourceName) {
		Environment env = createEntityMock(Environment.class, "MDMTest", sourceName, "1");

		EntityManager em = mock(EntityManager.class);
		when(em.loadEnvironment()).thenReturn(env);

		ApplicationContext ctx = mock(ApplicationContext.class);
		when(ctx.getEntityManager()).thenReturn(Optional.of(em));

		return ctx;
	}

	private static <T extends Entity> T createEntityMock(Class<T> type, String name, String sourceName, String id) {

		Map<String, Value> entityAttributes = new HashMap<>();
		entityAttributes.put("Name", ValueType.STRING.create("Name", name));

		Core core = mock(Core.class);
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
