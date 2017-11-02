/*******************************************************************************
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH and others
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

import org.eclipse.mdm.api.base.ConnectionException;
import org.eclipse.mdm.api.base.ServiceNotProvidedException;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.ApplicationContextFactory;
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

	private Map<Principal, List<ApplicationContext>> connectionMap = new HashMap<>();

	/**
	 * returns all available {@link ApplicationContext}s
	 *
	 * @return list of available {@link ApplicationContext}s
	 */
	public List<ApplicationContext> getContexts() {
		Principal principal = sessionContext.getCallerPrincipal();

		List<ApplicationContext> contextList = connectionMap.get(principal);

		if (contextList == null || contextList.size() <= 0) {
			String errorMessage = "no connections available for user with name '" + principal.getName() + "'";
			throw new ConnectorServiceException(errorMessage);
		}
		return contextList;
	}

	/**
	 * returns an {@link ApplicationContext} identified by the given name
	 *
	 * @param name
	 *            source name (e.g. MDM {@link Environment} name)
	 * @return the matching {@link ApplicationContext}
	 */
	public ApplicationContext getContextByName(String name) {
		try {

			List<ApplicationContext> emList = getContexts();
			for (ApplicationContext em : emList) {
				String sourceName = em.getEntityManager()
						.orElseThrow(() -> new ServiceNotProvidedException(EntityManager.class))
						.loadEnvironment().getSourceName();
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
	 * @return a list connected {@link ApplicationContext}s
	 * 
	 */
	public List<ApplicationContext> connect(String user, String password) {

		List<ApplicationContext> contextList = new ArrayList<>();

		List<ServiceConfiguration> serviceConfigurations = serviceConfigurationActivity.readServiceConfigurations();

		for (ServiceConfiguration serviceConfiguration : serviceConfigurations) {
			connectContexts(user, password, serviceConfiguration, contextList);
		}

		return contextList;
	}

	/**
	 * tries to connect the freetextsearch user to the registered
	 * {@link ServiceConfiguration}s This method is called {@link org.eclipse.mdm.freetextindexer.boundary.MdmApiBoundary}
	 *
	 * @return a list connected {@link ApplicationContext}s
	 * 
	 */
	public List<ApplicationContext> connectFreetextSearch(String userParamName, String passwordParamName) {

		List<ApplicationContext> contextList = new ArrayList<>();

		List<ServiceConfiguration> serviceConfigurations = serviceConfigurationActivity.readServiceConfigurations();

		for (ServiceConfiguration serviceConfiguration : serviceConfigurations) {
			String user = serviceConfiguration.getConnectionParameters().get(userParamName);
			String password = serviceConfiguration.getConnectionParameters().get(passwordParamName);
			if (user == null || user.isEmpty() || password == null || password.isEmpty()) {
				throw new IllegalArgumentException(String.format(
						"Cannot login user for freetextindexer! Please provide valid values for the parameters %s and %s.",
						userParamName,
						passwordParamName));
			}
			connectContexts(user, password, serviceConfiguration, contextList);
		}

		return contextList;
	}
	
	/**
	 * registers all connections for a {@link Principal} at the
	 * {@link ConnectorService} This method is call from a {@link LoginModule}
	 * at login phase 2.
	 *
	 * @param principal
	 *            owner of the given connection list (ApplicationContext list)
	 * @param contextList
	 *            connection list
	 */
	public void registerConnections(Principal principal, List<ApplicationContext> contextList) {

		if (contextList == null || contextList.size() <= 0) {
			String errorMessage = "no connections for user with name '" + principal.getName() + "' available!";
			throw new ConnectorServiceException(errorMessage);
		}

		if (!connectionMap.containsKey(principal)) {
			connectionMap.put(principal, contextList);
		} else {
			disconnectContexts(contextList);
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
			List<ApplicationContext> contextList = connectionMap.remove(principal);
			disconnectContexts(contextList);
			LOG.info("user with name '" + principal.getName() + "' has been disconnected!");
			LOG.debug("number of active users: " + connectionMap.keySet().size());
		}

	}

	private void connectContexts(String user, String password, ServiceConfiguration source,
			List<ApplicationContext> emList) {

		try {

			Class<? extends ApplicationContextFactory> contextFactoryClass = Thread.currentThread()
					.getContextClassLoader().loadClass(source.getContextFactoryClass())
					.asSubclass(ApplicationContextFactory.class);
			ApplicationContextFactory contextFactory = contextFactoryClass.newInstance();

			Map<String, String> staticConnectionParameters = source.getConnectionParameters();
			Map<String, String> dynamicConnectionParameters = new LinkedHashMap<>(
					staticConnectionParameters.size() + 3);
			dynamicConnectionParameters.putAll(staticConnectionParameters);
			dynamicConnectionParameters.put(CONNECTION_PARAM_USER, user);
			dynamicConnectionParameters.put(CONNECTION_PARAM_PASSWORD, password);
			if (elasticSearchURL != null && !elasticSearchURL.isEmpty()) {
				dynamicConnectionParameters.put(CONNECTION_PARAM_ELASTIC_SEARCH_URL, elasticSearchURL);
			}

			ApplicationContext context = contextFactory.connect(dynamicConnectionParameters);
			emList.add(context);

		} catch (ConnectionException e) {
			LOG.warn("unable to logon user with name '" + user + "' at data source '" + source.toString()
					+ "' (reason: " + e.getMessage() + ")");
		} catch (Exception e) {
			LOG.error("failed to initialize entity manager using factory '" + source.getContextFactoryClass()
					+ "' (reason: " + e + ")", e);
		}
	}

	private static void disconnectContexts(List<ApplicationContext> contextList) {
		for (ApplicationContext context : contextList) {
			disconnectContext(context);
		}
	}

	private static void disconnectContext(ApplicationContext context) {
		try {
			if (context != null) {
				context.close();
			}
		} catch (ConnectionException e) {
			LOG.error("unable to logout user from MDM datasource (reason: " + e.getMessage() + ")");
			throw new ConnectorServiceException(e.getMessage(), e);
		}
	}

}
