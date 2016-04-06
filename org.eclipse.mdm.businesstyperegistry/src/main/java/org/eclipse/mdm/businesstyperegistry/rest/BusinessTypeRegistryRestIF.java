/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.businesstyperegistry.rest;

import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.businesstyperegistry.bean.BusinessTypeRegistryBean;

/**
 * Rest interface for {@link BusinessTypeRegistryBean}
 * @author Gigatronik Ingolstadt GmbH
 *
 */
public interface BusinessTypeRegistryRestIF {
	
	
	
	/**
	 * delegates the rest call to the {@link BusinessTypeRegistryBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @return a JSON String which contains a list of supported actions
	 * for the MDM business object type {@link Test}
	 */
	String getTestActions(String sourceName);
	
	
	
	/**
	 * delegates the rest call to the {@link BusinessTypeRegistryBean} implementation
	 * 
	 * @param sourceName source name (MDM Environment name)
	 * @return a JSON String which contains a list of supported actions
	 * for the MDM business object type {@link TestStep}
	 */	
	String getTestStepActions(String sourceName);
	
	
	
	/**
	 * delegates the rest call to the {@link BusinessTypeRegistryBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @return a JSON String which contains a list of supported actions
	 * for the MDM business object type {@link Measurement}
	 */
	String getMeasurementActions(String sourceName); 

}
