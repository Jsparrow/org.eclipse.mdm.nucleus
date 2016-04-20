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


import java.security.Principal;
import java.util.List;

import javax.ejb.Local;
import javax.security.auth.spi.LoginModule;

import org.eclipse.mdm.api.base.EntityManager;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.connector.bean.ConnectorBean;
import org.eclipse.mdm.connector.bean.ServiceConfiguration;

/**
 * Local interface for {@link ConnectorBean}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Local
public interface ConnectorBeanLI {
		
	/**
	 * returns all available {@link EntityManager}s
	 * 
	 * @return list of available {@link EntityManager}s
	 * @throws ConnectorException if an error occurs during the {@link EntityManager} lookup operation
	 */
	List<EntityManager> getEntityManagers() throws ConnectorException;
	
	
	
	/**
	 * returns an {@link EntityManager} identified by the given name
	 * 
	 * @param name source name (e.g. MDM {@link Environment} name)
	 * @return the matching {@link EntityManager}
	 * @throws ConnectorException if a {@link EntityManager} with the given name does not exist
	 */
	EntityManager getEntityManagerByName(String name) throws ConnectorException;
	
	
	
	/**
	 * returns an {@link EntityManager} identified by the given business object {@link URI}
	 * 
	 * @param uri business object {@link URI} 
	 * @return the matching {@link EntityManager}
	 * @throws ConnectorException if a {@link EntityManager} for the given {@link URI} does not exist
	 */
	EntityManager getEntityManagerByURI(URI uri) throws ConnectorException;	
	
	
	
	/**
	 * tries to connect a user with the given password to the registered {@link ServiceConfiguration}s
	 * This method is call from a {@link LoginModule} at login phase 1.
	 * 
	 * @param user user login credential
	 * @param password password login credential 
	 * @return a list connected {@link EntityManager}s  
	 * @throws ConnectorException if an error occurs during the connection process (phase 1)
	 */
	List<EntityManager> connect(String user, String password)  throws ConnectorException;
	
	
	
	/**
	 * registers all connections for a {@link Principal} at the {@link ConnectorBean}
	 * This method is call from a {@link LoginModule} at login phase 2.
	 * 
	 * @param principal owner of the given connection list (EntityManager list)
	 * @param emList connection list
	 * @throws ConnectorException if an error occurs during the connection process (phase 2)
	 */
	void registerConnections(Principal principal, List<EntityManager> emList)  throws ConnectorException;
	
	
	
	/**
	 * disconnect the given {@link Principal} from all connected data sources
	 * This method is call from a {@link LoginModule} at logout
	 * 
	 * This method is call from a {@link LoginModule}
	 * @param principal the principal to disconnect
	 */
	void disconnect(Principal principal);
	
	
	

	



}
