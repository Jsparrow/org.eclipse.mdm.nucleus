/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.action.delete;

import org.eclipse.mdm.action.delete.bean.DeleteActionBean;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.URI;

/**
 * Local interface for {@link DeleteActionBean}
 * @author Gigatronik Ingolstadt GmbH
 *
 */
public interface DeleteActionBeanLI {

	/**
	 * deletes the business object identified by the given URI
	 * @param uri {@link URI} to identify the business object for the delete operation
	 * @throws DeleteActionException if an error occurs during the delete process
	 */
	void delete(URI uri) throws DeleteActionException;
	
	/**
	 * creates a valid {@link URI} with a valid source type
	 *  
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param type MDM business object type e.g. TestStep.class
	 * @param id MDM business object id
	 * @return the created valid MDM business object {@link URI}
	 * @throws DeleteActionException if an error occurs during the {@link URI} create operation
	 */
	URI createURI(String sourceName, Class<? extends Entity> type, long id) throws DeleteActionException;
}
