/*******************************************************************************
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH2
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Sebastian Dirsch - initial implementation
 *******************************************************************************/

package org.eclipse.mdm.connector.boundary;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.security.auth.spi.LoginModule;

import org.eclipse.mdm.api.base.BaseEntityManager;
import org.eclipse.mdm.api.base.ConnectionException;
import org.eclipse.mdm.api.base.EntityManagerFactory;
import org.eclipse.mdm.api.base.model.BaseEntityFactory;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.control.ServiceConfigurationActivity;
import org.eclipse.mdm.connector.entity.ServiceConfiguration;
import org.eclipse.mdm.property.GlobalProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConnectorServcie Bean implementation to create and close connections
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 * @author Canoo Engineering (removal of hardcoded ODS dependencies)
 *
 */
@Startup
@Singleton
public class ConnectorService {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectorService.class);

	private static final String CONNECTION_PARAM_USER = "user";
	private static final String CONNECTION_PARAM_PASSWORD = "password";
	private static final String CONNECTION_PARAM_ELASTIC_SEARCH_URL = "elasticsearch.url";

	@Resource
	private SessionContext sessionContext;
	@EJB
	private ServiceConfigurationActivity serviceConfigurationActivity;

	@Inject
	@GlobalProperty("elasticsearch.url")
	private String elasticSearchURL;

	private Map<Principal, List<EntityManager>> connectionMap = new HashMap<>();

	/**
	 * returns all available {@link EntityManager}s
	 *
	 * @return list of available {@link EntityManager}s
	 */
	public List<EntityManager> getEntityManagers() {
		Principal principal = sessionContext.getCallerPrincipal();

		List<EntityManager> emList = connectionMap.get(principal);

		if (emList == null || emList.size() <= 0) {
			String errorMessage = "no connections available for user with name '" + principal.getName() + "'";
			throw new ConnectorServiceException(errorMessage);
		}
		return emList;
	}

	/**
	 * returns an {@link EntityManager} identified by the given name
	 *
	 * @param name
	 *            source name (e.g. MDM {@link Environment} name)
	 * @return the matching {@link EntityManager}
	 */
	public EntityManager getEntityManagerByName(String name) {
		try {

			List<EntityManager> emList = getEntityManagers();
			for (EntityManager em : emList) {
				String sourceName = em.loadEnvironment().getSourceName();
				if (sourceName.equals(name)) {
					return em;
				}
			}

			String errorMessage = "no data source with environment name '" + name + "' connected!";
			throw new ConnectorServiceException(errorMessage);

		} catch (DataAccessException e) {
			throw new ConnectorServiceException(e.getMessage(), e);
		}

	}

	/**
	 * tries to connect a user with the given password to the registered
	 * {@link ServiceConfiguration}s This method is call from a
	 * {@link LoginModule} at login phase 1.
	 *
	 * @param user
	 *            user login credential
	 * @param password
	 *            password login credential
	 * @return a list connected {@link EntityManager}s
	 * 
	 */
	public List<EntityManager> connect(String user, String password) {

		List<EntityManager> emList = new ArrayList<>();

		List<ServiceConfiguration> serviceConfigurations = serviceConfigurationActivity.readServiceConfigurations();

		for (ServiceConfiguration serviceConfiguration : serviceConfigurations) {
			connectEntityManagers(user, password, serviceConfiguration, emList);
		}

		return emList;
	}

	/**
	 * registers all connections for a {@link Principal} at the
	 * {@link ConnectorService} This method is call from a {@link LoginModule} at
	 * login phase 2.
	 *
	 * @param principal
	 *            owner of the given connection list (EntityManager list)
	 * @param emList
	 *            connection list
	 */
	public void registerConnections(Principal principal, List<EntityManager> emList) {

		if (emList == null || emList.size() <= 0) {
			String errorMessage = "no connections for user with name '" + principal.getName() + "' available!";
			throw new ConnectorServiceException(errorMessage);
		}

		if (!connectionMap.containsKey(principal)) {
			connectionMap.put(principal, emList);
		} else {
			disconnectEntityManagers(emList);
		}
	}

	/**
	 * disconnect the given {@link Principal} from all connected data sources
	 * This method is call from a {@link LoginModule} at logout
	 *
	 * This method is call from a {@link LoginModule}
	 * 
	 * @param principal
	 *            the principal to disconnect
	 */
	public void disconnect(Principal principal) {
		if (connectionMap.containsKey(principal)) {
			List<EntityManager> emList = connectionMap.remove(principal);
			disconnectEntityManagers(emList);
			LOG.info("user with name '" + principal.getName() + "' has been disconnected!");
			LOG.debug("number of active users: " + connectionMap.keySet().size());
		}

	}

	private void connectEntityManagers(String user, String password, ServiceConfiguration source,
			List<EntityManager> emList) {

		try {

			@SuppressWarnings("rawtypes")
			Class<? extends EntityManagerFactory> entityManagerFactoryClass = Thread.currentThread().getContextClassLoader().loadClass(source.getEntityManagerFactoryClass()).asSubclass(EntityManagerFactory.class);
			EntityManagerFactory<?> emf = entityManagerFactoryClass.newInstance();

			Map<String, String> staticConnectionParameters = source.getConnectionParameters();
			Map<String, String> dynamicConnectionParameters = new LinkedHashMap<>(staticConnectionParameters.size() + 3);
			dynamicConnectionParameters.putAll(staticConnectionParameters);
			dynamicConnectionParameters.put(CONNECTION_PARAM_USER, user);
			dynamicConnectionParameters.put(CONNECTION_PARAM_PASSWORD, password);
			if (elasticSearchURL != null && !elasticSearchURL.isEmpty()) {
				dynamicConnectionParameters.put(CONNECTION_PARAM_ELASTIC_SEARCH_URL, elasticSearchURL);
			}

			BaseEntityManager<? extends BaseEntityFactory> em = emf.connect(dynamicConnectionParameters);
			// The cast below is unsafe, but cannot be avoided without changing the API of this class.
			emList.add((EntityManager)em);

		} catch (ConnectionException e) {
			LOG.warn("unable to logon user with name '" + user + "' at data source '" + source.toString()
			+ "' (reason: " + e.getMessage() + ")");
		} catch (Exception e) {
			LOG.error("failed to initialize entity manager using factory '" + source.getEntityManagerFactoryClass() + "' (reason: " + e + ")", e);
		}
	}

	private static void disconnectEntityManagers(List<EntityManager> emList) {
		for (EntityManager em : emList) {
			disconnectEntityManager(em);
		}
	}

	private static void disconnectEntityManager(EntityManager em) {
		try {
			if (em != null) {
				em.close();
			}
		} catch (ConnectionException e) {
			LOG.error("unable to logout user from MDM datasource (reason: " + e.getMessage() + ")");
			throw new ConnectorServiceException(e.getMessage(), e);
		}
	}

}
