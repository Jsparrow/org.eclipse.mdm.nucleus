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

package org.eclipse.mdm.action.delete.rest;

import org.eclipse.mdm.action.delete.bean.DeleteActionBean;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;

/**
 * Rest interface for {@link DeleteActionBean}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public interface DeleteActionRestIF {

	
	
	/**
	 * delegates the delete rest call to the {@link DeleteActionBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param testId id of a MDM {@link Test} business object 
	 * @return empty string if the delete operation was successful, testId if the
	 * delete operation fails.
	 */
	String deleteTest(String sourceName, long testId);
	
	
	
	/**
	 * delegates the delete rest call to the {@link DeleteActionBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param testStepId id of a MDM {@link TestStep} business object 
	 * @return empty string if the delete operation was successful, testStepId if the
	 * delete operation fails.
	 */
	String deleteTestStep(String sourceName, long testStepId);
	
	
	
	/**
	 * delegates the delete rest call to the {@link DeleteActionBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param measurementId id of a MDM {@link Measurement} business object 
	 * @return empty string if the delete operation was successful, measurementId if the
	 * delete operation fails.
	 */
	String deleteMeasurement(String sourceName, long measurementId);	
}
