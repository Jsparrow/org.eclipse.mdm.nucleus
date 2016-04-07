/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.connector.bean;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.eclipse.mdm.api.base.ConnectionException;
import org.eclipse.mdm.api.base.EntityManager;
import org.eclipse.mdm.api.base.EntityManagerFactory;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.connector.ConnectorBeanLI;
import org.eclipse.mdm.connector.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean implementation {@link ConnectorBeanLI}
 * @author Gigatronik Ingolstadt GmbH
 *
 */
@Startup
@Singleton
@LocalBean
public class ConnectorBean implements ConnectorBeanLI {

	
	
	private static final Logger LOG = LoggerFactory.getLogger(ConnectorBean.class); 
	
	private final static String ARG_ODS_NAMESERVICE = "nameservice";
	private final static String ARG_ODS_SERVICENAME = "servicename";
	private final static String ARG_ODS_USER 		= "user";
	private final static String ARG_ODS_PASSWORD    = "password";
		
	//TODO: delete this if a login module is enabled
	private final static String SUPERUSER_NAME = "sa";
	private final static String SUPERUSER_PASSWORD = "sa";
	
	@Resource 
	private SessionContext sessionContext;
	
	@EJB(beanName="ODSEntityManagerFactory")
	private EntityManagerFactory<EntityManager> emf;
		
	private Map<Principal, List<EntityManager>> connectionMap = new HashMap<Principal, List<EntityManager>>();
	private Map<Principal, String> authMap = new HashMap<Principal, String>();
	
	
	
	@Override
	public List<EntityManager> getEntityManagers() throws ConnectorException {
		Principal principal = this.sessionContext.getCallerPrincipal();
		
		//TODO: delete this if a login module is enabled
		if(!this.connectionMap.containsKey(principal)) {
			connect(principal, SUPERUSER_NAME, SUPERUSER_PASSWORD);
		}
		
		List<EntityManager> emList = this.connectionMap.get(principal);	
		
		
		if(emList == null || emList.size() <= 0) {
			throw new ConnectorException("no connections available for user with name '" + principal.getName() + "'");
		}
		return emList;
	}
	
	
	
	@Override
	public EntityManager getEntityManagerByName(String name) throws ConnectorException {
		try {		
			
			List<EntityManager> emList = getEntityManagers();
			for(EntityManager em : emList) {	
				String sourceName = em.loadEnvironment().getURI().getSourceName();
				if(sourceName.equals(name)) {					
					return em;
				}
			}
			
			throw new ConnectorException("no data source with environment name '" + name + "' connected!");
		
		} catch(DataAccessException e) {
			throw new ConnectorException(e.getMessage(), e);
		}
		
	}
	

	
	@Override
	public EntityManager getEntityManagerByURI(URI uri) throws ConnectorException {		
		return getEntityManagerByName(uri.getSourceName());		
	}
	

	@Override
	public String connect(Principal principal, String user, String password) {
		
		try {
		
			if(!this.connectionMap.containsKey(principal)) {			
				List<EntityManager> emList = new ArrayList<EntityManager>();
				List<ServiceConfiguration> serviceConfigurations = listServiceConfigurations();
				
				for(ServiceConfiguration serviceConfiguration : serviceConfigurations) {			
					connectEntityManagers(user, password, serviceConfiguration, emList);
				}
				
				if(emList == null || emList.size() <= 0) {
					return "unable to logon user with name '" + user + "' (initial login)";
				}
				
				this.connectionMap.put(principal, emList);
				this.authMap.put(principal, password);
				
				LOG.info("user with name '" + user + "' has been logged on!");
				
			} else {
				String cachedPassword = this.authMap.get(principal);
				if(!cachedPassword.equals(password)) {
					return "unable to logon user with name '" + user + "'";
				}
			}
		
		} catch(ConnectionException e) {
			return e.getMessage();
		}
		
		return "";
	}	
	
	
	
	@Override
	public void disconnect() {
		Principal principal = this.sessionContext.getCallerPrincipal();
		if(this.connectionMap.containsKey(principal)) {
			List<EntityManager> emList = this.connectionMap.remove(principal);
			disconnectEntityManagers(emList);
		}
		
	}
		
	
	
	private void connectEntityManagers(String user, String password, ServiceConfiguration source, 
		List<EntityManager> emList) {
		
		try {
		
			Map<String, String> connectionParameters = new HashMap<String, String>();
			connectionParameters.put(ARG_ODS_NAMESERVICE, source.getNameService());
			connectionParameters.put(ARG_ODS_SERVICENAME, source.getServiceName());
			connectionParameters.put(ARG_ODS_USER, user);
			connectionParameters.put(ARG_ODS_PASSWORD, password);
				
			emList.add(this.emf.connect(connectionParameters));
			
		} catch(ConnectionException e) {
			LOG.warn("unable to logon user with name '" + user + "' at data source '" 
				+ source.toString() + "' (reason: " + e.getMessage() + "'");
		}		
	}
	
	
	private void disconnectEntityManagers(List<EntityManager> emList) {
		for(EntityManager em : emList) {
			disconnectEntityManager(em);
		}
	}		
	
	
	private void disconnectEntityManager(EntityManager em) {
	
		try {
			if(em != null) {
				em.close();		
			}
		} catch(ConnectionException e) {
			LOG.error("unable to logout user from MDM datasource (reason: " + e.getMessage() + ")");
		}
	}
	
	
	private List<ServiceConfiguration> listServiceConfigurations() throws ConnectionException {
		ServiceConfigurationReader serviceReader = new ServiceConfigurationReader();
		return serviceReader.readServiceConfigurations();
	}

}
