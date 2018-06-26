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

import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * ConnectorServcie Bean implementation to create and close connections
 *
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 * @author Canoo Engineering (removal of hardcoded ODS dependencies)
 *
 */
@SessionScoped
public class ConnectorService implements Serializable {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectorService.class);
	private static final String CONNECTION_PARAM_FOR_USER = "for_user";

	@Inject
	Principal principal;

	@Inject
	ServiceConfigurationActivity serviceConfigurationActivity;

	@Inject
	@GlobalProperty
	private Map<String, String> globalProperties = Collections.emptyMap();

	private List<ApplicationContext> contexts = Lists.newArrayList();
	
	public ConnectorService() {
		// empty constructor for CDI
	}
	
	
	/**
	 * Creates a connector service for usage outside the session scope of CDI.
	 * 
	 * @param principal Principal the connector service uses.
	 * @param globalProperties global properties supplied the opened application contexts.
	 */
	public ConnectorService(Principal principal, Map<String, String> globalProperties) {
		super();
		this.principal = principal;
		this.serviceConfigurationActivity = new ServiceConfigurationActivity();
		this.globalProperties = globalProperties;
	}


	/**
	 * returns all available {@link ApplicationContext}s
	 *
	 * @return list of available {@link ApplicationContext}s
	 */
	public List<ApplicationContext> getContexts() {
		return ImmutableList.copyOf(contexts);
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
			for (ApplicationContext context : getContexts()) {
				String sourceName = context.getEntityManager()
						.orElseThrow(() -> new ServiceNotProvidedException(EntityManager.class))
						.loadEnvironment().getSourceName();

				if (sourceName.equals(name)) {
					return context;
				}
			}
			String errorMessage = "no data source with environment name '" + name + "' connected!";
			throw new ConnectorServiceException(errorMessage);

		} catch (DataAccessException e) {
			throw new ConnectorServiceException(e.getMessage(), e);
		}

	}


	@PostConstruct
	public void connect() {
		this.contexts = serviceConfigurationActivity
				.readServiceConfigurations().stream()
				.map(this::connectContexts)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
	}

	/**
	 * disconnect from all connected data sources
	 * This method is call from a {@link LoginModule} at logout
	 *
	 * This method is call from a {@link LoginModule}
	 *
     */
    public void disconnect() {
        disconnectContexts(contexts);
        LOG.info("user with name '" + principal.getName() + "' has been disconnected!");
    }

	private Optional<ApplicationContext> connectContexts(ServiceConfiguration source) {
		try {

			Class<? extends ApplicationContextFactory> contextFactoryClass = Thread.currentThread()
					.getContextClassLoader().loadClass(source.getContextFactoryClass())
					.asSubclass(ApplicationContextFactory.class);
			ApplicationContextFactory contextFactory = contextFactoryClass.newInstance();

			Map<String, String> connectionParameters = new HashMap<>();
			connectionParameters.putAll(globalProperties);
			connectionParameters.putAll(source.getConnectionParameters());
			connectionParameters.put(CONNECTION_PARAM_FOR_USER, principal.getName());

			ApplicationContext context = contextFactory.connect(connectionParameters);
			return Optional.of(context);

		} catch (ConnectionException e) {
			LOG.warn("unable to logon user with name '" + principal.getName() + "' at data source '" + source.toString()
					+ "' (reason: " + e.getMessage() + ")");
		} catch (Exception e) {
			LOG.error("failed to initialize entity manager using factory '" + source.getContextFactoryClass()
					+ "' (reason: " + e + ")", e);
		}
		return Optional.empty();
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
