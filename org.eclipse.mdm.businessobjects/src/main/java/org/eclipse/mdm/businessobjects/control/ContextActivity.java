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

package org.eclipse.mdm.businessobjects.control;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.base.model.ContextDescribable;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.connector.boundary.ConnectorService;


/**
 * ContextActivity Bean implementation to get ordered and measured context data
 * for {@link ContextDescribable} business objects (e.g. TestStep)
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class ContextActivity  {
	
	
	public static final String CONTEXT_GROUP_ORDERED = "ordered";
	public static final String CONTEXT_GROUP_MEASURED = "measured";
	
	@EJB
	private ConnectorService connectorService;
	

	
	/**
	 * returns the ordered and measurement context for a {@link TestStep} MDM business object identified by 
	 * the given {@link URI}. If no {@link ContextType}s are defined for this method call, the method returns all context 
	 * informations of the available {@link ContextType}s. Otherwise you can specify a list of {@link ContextType}s. 
	 * 
	 * Possible {@link ContextType}s are {@link ContextType}.UNITUNDERTEST, {@link ContextType}.TESTSEQUENCE 
	 * and {@link ContextType}.TESTEQUIPMENT.
	 *   
	 * @param testStepURI {@link URI} to identify the {@link TestStep}
	 * @param contextTypes list of {@link ContextType}s
	 * @return the ordered and measured context data as context object for the identified {@link TestStep}
	 * @throws ContextProviderException if an error occurs during lookup the context informations
	 */
	public Map<String, Map<ContextType, ContextRoot>> getTestStepContext(URI testStepURI, ContextType... contextTypes)  {
		
		try {
		
			EntityManager em = this.connectorService.getEntityManagerByURI(testStepURI);
			TestStep testStep = ServiceUtils.lookupEntityByURI(TestStep.class, em, testStepURI);
					
			Map<ContextType, ContextRoot> orderedContext = em.loadContexts(testStep, contextTypes);
			Map<ContextType, ContextRoot> measuredContext = lookupMeasuredContextByTestStep(em, 
				testStep, contextTypes);
			
			Map<String, Map<ContextType, ContextRoot>> contextMap = new HashMap<>();
			contextMap.put(CONTEXT_GROUP_ORDERED, orderedContext);
			contextMap.put(CONTEXT_GROUP_MEASURED, measuredContext);
			
			return contextMap;
			
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
		
	}
	
	
	
	/**
	 * returns the ordered and measurement context for a {@link Measurement} MDM business object identified by
	 * the given {@link URI}. If no {@link ContextType}s are defined for this method call, the method returns all context 
	 * informations of the available {@link ContextType}s. Otherwise you can specify a list of {@link ContextType}s.
	 *  
	 * Possible {@link ContextType}s are {@link ContextType}.UNITUNDERTEST, {@link ContextType}.TESTSEQUENCE 
	 * and {@link ContextType}.TESTEQUIPMENT.
	 * 
	 * @param measurementURI {@link URI} to identify the {@link Measurement}
	 * @param contextTypes list of {@link ContextType}s
	 * @return the ordered and measured context data as context object for the identified {@link Measurement}
	 * @throws ContextProviderException if an error occurs during lookup the context informations
	 */
	public Map<String, Map<ContextType, ContextRoot>> getMeasurementContext(URI measurementURI, ContextType... contextTypes) {
		
		try {					
			
			EntityManager em = this.connectorService.getEntityManagerByURI(measurementURI);
			Measurement measurement =  ServiceUtils.lookupEntityByURI(Measurement.class, em, measurementURI);
			
			Map<ContextType, ContextRoot> measuredContext = em.loadContexts(measurement, contextTypes);
			Map<ContextType, ContextRoot> orderedContext = lookupOrderedContextByMeasurement(em, 
				measurement, contextTypes);
			
			Map<String, Map<ContextType, ContextRoot>> contextMap = new HashMap<>();
			contextMap.put(CONTEXT_GROUP_ORDERED, orderedContext);
			contextMap.put(CONTEXT_GROUP_MEASURED, measuredContext);
			
			return contextMap;
			
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
		
	}
	
	
	
	private Map<ContextType, ContextRoot> lookupOrderedContextByMeasurement(EntityManager em, Measurement measurement,
		ContextType... contextTypes) throws DataAccessException {
		
		Optional<TestStep> optional = em.loadParent(measurement, Measurement.PARENT_TYPE_TESTSTEP);
		
		if(!optional.isPresent()) {
			return Collections.emptyMap();
		}
		
		TestStep testStep = optional.get();
		return em.loadContexts(testStep, contextTypes);
	}
		
		
	
	private Map<ContextType, ContextRoot> lookupMeasuredContextByTestStep(EntityManager em, TestStep testStep,
		ContextType... contextTypes) 
		throws DataAccessException {
		
		List<Measurement> childList = em.loadChildren(testStep, TestStep.CHILD_TYPE_MEASUREMENT);
		if(childList.size() > 0) {
			return em.loadContexts(childList.get(0), contextTypes);
		}
		return Collections.emptyMap();
	}

}
