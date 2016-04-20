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
package org.eclipse.mdm.businesstyperegistry;

import java.util.List;

import javax.ejb.Local;

import org.eclipse.mdm.api.base.model.Entity;

/**
 * Local interface for all Actions
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Local
public interface ActionBeanLI {

	
	
	/**
	 * returns the name of the action
	 * 
	 * @return name of the action
	 */
	public String getActionName();	
	
	
	
	/**
	 * returns a list of supported types (e.g. Test.class, TestStep.class, Measurement.class).
	 * if a type is part of the returned list, the action is supported by this type.
	 * 
	 * @return a list of supported types
	 */
	public List<Class<? extends Entity>> getSupportedEntityTypes();
}
