/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.connector;


import java.security.Principal;
import java.util.List;

import javax.ejb.Local;

import org.eclipse.mdm.api.base.EntityManager;
import org.eclipse.mdm.api.base.EntityManagerFactory;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.connector.bean.ConnectorBean;

/**
 * Local interface for {@link ConnectorBean}
 * @author Gigatronik Ingolstadt GmbH
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
	 * connect a registered {@link Principal} to the defined MDM data sources via the {@link EntityManagerFactory}
	 *  
	 * @param principal registered {@link Principal} 
	 * @param user name of the user
	 * @param password password of the user
	 * @return an empty String if the connection was successful, or an error message for the LoginRealm
	 * which calls this method from outside.
	 */
	String connect(Principal principal, String user, String password);
	
	
	
	/**
	 * disconnect the current {@link Principal} from all connected data sources
	 */
	void disconnect();
}
