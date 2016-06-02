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
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.security.auth.spi.LoginModule;

import org.eclipse.mdm.api.base.ConnectionException;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.base.EntityManagerFactory;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.connector.control.ServiceConfigurationActivity;
import org.eclipse.mdm.connector.entity.ServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConnectorServcie Bean implementation to create and close connections
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Startup
@Singleton
public class ConnectorService {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectorService.class); 
	
	private final static String ARG_ODS_NAMESERVICE = "nameservice";
	private final static String ARG_ODS_SERVICENAME = "servicename";
	private final static String ARG_ODS_USER 		= "user";
	private final static String ARG_ODS_PASSWORD    = "password";
		
		
	@Resource 
	private SessionContext sessionContext;
	
	@EJB(beanName="ODSEntityManagerFactory")
	private EntityManagerFactory<EntityManager> emf;
	@EJB
	private ServiceConfigurationActivity serviceConfigurationActivity;	
	
	
	private Map<Principal, List<EntityManager>> connectionMap = new HashMap<Principal, List<EntityManager>>();
	

	
	/**
	 * returns all available {@link EntityManager}s
	 * 
	 * @return list of available {@link EntityManager}s
	 */
	public List<EntityManager> getEntityManagers() {
		Principal principal = this.sessionContext.getCallerPrincipal();
		
		List<EntityManager> emList = this.connectionMap.get(principal);	

		if(emList == null || emList.size() <= 0) {
			String errorMessage = "no connections available for user with name '" + principal.getName() + "'";
			throw new ConnectorServiceException(errorMessage);
		}
		return emList;
	}
	
	

	/**
	 * returns an {@link EntityManager} identified by the given name
	 * 
	 * @param name source name (e.g. MDM {@link Environment} name)
	 * @return the matching {@link EntityManager}
	 */
	public EntityManager getEntityManagerByName(String name) {
		try {		
			
			List<EntityManager> emList = getEntityManagers();
			for(EntityManager em : emList) {	
				String sourceName = em.loadEnvironment().getURI().getSourceName();
				if(sourceName.equals(name)) {					
					return em;
				}
			}			
			
			String errorMessage = "no data source with environment name '" + name + "' connected!";
			throw new ConnectorServiceException(errorMessage);
		
		} catch(DataAccessException e) {
			throw new ConnectorServiceException(e.getMessage(), e);
		}
		
	}
	
	
	
	/**
	 * returns an {@link EntityManager} identified by the given business object {@link URI}
	 * 
	 * @param uri business object {@link URI} 
	 * @return the matching {@link EntityManager}
	 */
	public EntityManager getEntityManagerByURI(URI uri) {		
		return getEntityManagerByName(uri.getSourceName());		
	}
	
	
		
	/**
	 * tries to connect a user with the given password to the registered {@link ServiceConfiguration}s
	 * This method is call from a {@link LoginModule} at login phase 1.
	 * 
	 * @param user user login credential
	 * @param password password login credential 
	 * @return a list connected {@link EntityManager}s 

	 */
	public List<EntityManager> connect(String user, String password) {

		List<EntityManager> emList = new ArrayList<EntityManager>();
		
		List<ServiceConfiguration> serviceConfigurations = this.serviceConfigurationActivity.readServiceConfigurations();
				
		for(ServiceConfiguration serviceConfiguration : serviceConfigurations) {			
			connectEntityManagers(user, password, serviceConfiguration, emList);
		}
				
		return emList;		
	}
	

	
	/**
	 * registers all connections for a {@link Principal} at the {@link ConnectorBean}
	 * This method is call from a {@link LoginModule} at login phase 2.
	 * 
	 * @param principal owner of the given connection list (EntityManager list)
	 * @param emList connection list
	 */
	public void registerConnections(Principal principal, List<EntityManager> emList) {
				
		if(emList == null || emList.size() <= 0) {
			String errorMessage = "no connections for user with name '" + principal.getName() + "' available!";
			throw new ConnectorServiceException(errorMessage);
		}
						
		if(!this.connectionMap.containsKey(principal)) {
			this.connectionMap.put(principal, emList);
		} else {		
			disconnectEntityManagers(emList);
		}
	}
	
	
	
	/**
	 * disconnect the given {@link Principal} from all connected data sources
	 * This method is call from a {@link LoginModule} at logout
	 * 
	 * This method is call from a {@link LoginModule}
	 * @param principal the principal to disconnect
	 */
	public void disconnect(Principal principal) {		
		if(this.connectionMap.containsKey(principal)) {
			List<EntityManager> emList = this.connectionMap.remove(principal);
			disconnectEntityManagers(emList);
			LOG.info("user with name '" + principal.getName() + "' has been disconnected!");
			LOG.debug("number of active users: " + this.connectionMap.keySet().size());
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
			throw new ConnectorServiceException(e.getMessage(), e);
		}
	}
	

}
