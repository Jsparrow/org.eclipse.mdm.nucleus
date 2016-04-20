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


package org.eclipse.mdm.contextprovider;

import javax.ejb.Local;

import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.contextprovider.bean.ContextProviderBean;

/**
 * Local interface {@link ContextProviderBean}
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Local
public interface ContextProviderBeanLI {

	
	/**
	 * returns the ordered and measurement {@link Context} for a {@link TestStep} MDM business object identified by 
	 * the given {@link URI}. If no {@link ContextType}s are defined for this method call, the method returns all context 
	 * informations of the available {@link ContextType}s. Otherwise you can specify a list of {@link ContextType}s. 
	 * 
	 * Possible {@link ContextType}s are {@link ContextType}.UNITUNDERTEST, {@link ContextType}.TESTSEQUENCE 
	 * and {@link ContextType}.TESTEQUIPMENT.
	 *   
	 * @param testStepURI {@link URI} to identify the {@link TestStep}
	 * @param contextTypes list of {@link ContextType}s
	 * @return the ordered and measured context data as {@link Context} object for the identified {@link Test}
	 * @throws ContextProviderException if an error occurs during lookup the context informations
	 */
	Context getTestStepContext(URI testStepURI, ContextType... contextTypes) throws ContextProviderException;
	
	
	/**
	 * returns the ordered and measurement {@link Context} for a {@link Measurement} MDM business object identified by
	 * the given {@link URI}. If no {@link ContextType}s are defined for this method call, the method returns all context 
	 * informations of the available {@link ContextType}s. Otherwise you can specify a list of {@link ContextType}s.
	 *  
	 * Possible {@link ContextType}s are {@link ContextType}.UNITUNDERTEST, {@link ContextType}.TESTSEQUENCE 
	 * and {@link ContextType}.TESTEQUIPMENT.
	 * 
	 * @param measurementURI {@link URI} to identify the {@link Measurement}
	 * @param contextTypes list of {@link ContextType}s
	 * @return the ordered and measured context data as {@link Context} object for the identified {@link Measurement}
	 * @throws ContextProviderException if an error occurs during lookup the context informations
	 */
	Context getMeasurementContext(URI measurementURI, ContextType... contextTypes) throws ContextProviderException;
	
}
