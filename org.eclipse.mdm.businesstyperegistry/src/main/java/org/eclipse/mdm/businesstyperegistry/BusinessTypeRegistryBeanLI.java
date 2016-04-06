/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.businesstyperegistry;

import java.util.List;

import javax.ejb.Local;

import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.businesstyperegistry.bean.BusinessTypeRegistryBean;

/**
 * Local interface for {@link BusinessTypeRegistryBean}
 * @author Gigatronik Ingolstadt GmbH
 *
 */
@Local
public interface BusinessTypeRegistryBeanLI {
	
	/**
	 * returns all matching actions (ActionBeans) for the given type
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param type business object type e.g. TestStep.class
	 * @return a list of matching actions (ActionBeans) which are supported for the given type
	 * @throws BusinessTypeRegistryException if an error occurs during action lookup operation
	 */
	List<ActionBeanLI> getActions(String sourceName, Class<? extends Entity> type) throws BusinessTypeRegistryException;
}
