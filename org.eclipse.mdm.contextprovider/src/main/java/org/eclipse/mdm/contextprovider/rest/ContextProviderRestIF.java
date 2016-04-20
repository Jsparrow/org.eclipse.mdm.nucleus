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

package org.eclipse.mdm.contextprovider.rest;

import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.contextprovider.Context;
import org.eclipse.mdm.contextprovider.bean.ContextProviderBean;

/**
 * Rest interface for {@link ContextProviderBean}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public interface ContextProviderRestIF {

	
	
	/**
	 * delegates the rest call to the {@link ContextProviderBean} implementation
	 * and requests the UnitUnderTest context informations for a {@link TestStep}
	 *  
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param testStepID id of a MDM {@link TestStep} business object
	 * @return a JSON String which contains the UnitUnderTest {@link Context} 
	 * for the given {@link TestStep}
	 */
	String getTestStepContextUUT(String sourceName, long testStepID);
	
	
	
	/**
	 * delegates the rest call to the {@link ContextProviderBean} implementation
	 * and requests the TestSequence context informations for a {@link TestStep}
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param testStepID id of a MDM {@link TestStep} business object
	 * @return a JSON String which contains the TestSequence {@link Context} 
	 * for the given {@link TestStep}
	 */
	String getTestStepContextTSQ(String sourceName, long testStepID);
	
	
	
	/**
	 * delegates the rest call to the {@link ContextProviderBean} implementation
	 * and requests the TestEquipment context informations for a {@link TestStep}
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param testStepID id of a MDM {@link TestStep} business object
	 * @return a JSON String which contains the TestEquipment {@link Context} 
	 * for the given {@link TestStep}
	 */
	String getTestStepContextTEQ(String sourceName, long testStepID);
	
	
	
	/**
	 * delegates the rest call to the {@link ContextProviderBean} implementation
	 * and requests the all context informations for a {@link TestStep}
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param testStepID id of a MDM {@link TestStep} business object
	 * @return a JSON String which contains all {@link Context} 
	 * for the given {@link TestStep}
	 */
	String getTestStepContext(String sourceName, long testStepID);
	
	
	
	/**
	 * delegates the rest call to the {@link ContextProviderBean} implementation
	 * and requests the UnitUnderTest context informations for a {@link Measurement}
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param measurementID id of a MDM {@link Measurement} business object
	 * @return a JSON String which contains the UnitUnderTest {@link Context} 
	 * for the given {@link Measurement}
	 */
	String getMeasurementContextUUT(String sourceName, long measurementID);
	
	
	
	/**
	 * delegates the rest call to the {@link ContextProviderBean} implementation
	 * and requests the TestSequence context informations for a {@link Measurement}
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param measurementID id of a MDM {@link Measurement} business object
	 * @return a JSON String which contains the TestSequence {@link Context} 
	 * for the given {@link Measurement}
	 */
	String getMeasurementContextTSQ(String sourceName, long measurementID);
	
	
	
	/**
	 * delegates the rest call to the {@link ContextProviderBean} implementation
	 * and requests the TestEquipment context informations for a {@link Measurement}
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param measurementID id of a MDM {@link Measurement} business object
	 * @return a JSON String which contains the TestEquipment {@link Context} 
	 * for the given {@link Measurement}
	 */
	String getMeasurementContextTEQ(String sourceName, long measurementID);
	
	
	
	/**
	 * delegates the rest call to the {@link ContextProviderBean} implementation
	 * and requests all context informations for a {@link Measurement}
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param measurementID id of a MDM {@link Measurement} business object
	 * @return a JSON String which contains all {@link Context} 
	 * for the given {@link Measurement}
	 */
	String getMeasurementContext(String sourceName, long measurementID);
}
