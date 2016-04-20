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

package org.eclipse.mdm.contextprovider.bean;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.EntityManager;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.connector.ConnectorBeanLI;
import org.eclipse.mdm.connector.ConnectorException;
import org.eclipse.mdm.contextprovider.Context;
import org.eclipse.mdm.contextprovider.ContextProviderBeanLI;
import org.eclipse.mdm.contextprovider.ContextProviderException;


/**
 * Bean implementation (ContextProviderBeanLI)
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
@LocalBean
public class ContextProviderBean implements ContextProviderBeanLI {

	
	
	@EJB
	private ConnectorBeanLI connectorBean;
	
	
	
	@Override
	public Context getTestStepContext(URI testStepURI, ContextType... contextTypes) 
		throws ContextProviderException {
		
		try {
			Context contextData = new Context();
			
			EntityManager em = this.connectorBean.getEntityManagerByURI(testStepURI);
			TestStep testStep = lookupEntityByURI(TestStep.class, em, testStepURI);
					
			Map<ContextType, ContextRoot> orderedContext = em.loadContexts(testStep, contextTypes);
			Map<ContextType, ContextRoot> measuredContext = lookupMeasuredContextByTestStep(em, 
				testStep, contextTypes);
			
			contextData.setOrderedContext(orderedContext);
			contextData.setMeasuredContext(measuredContext);
			
			return contextData;
		} catch(ConnectorException | DataAccessException e) {
			throw new ContextProviderException(e.getMessage(), e);
		}
		
	}
	
	
	
	@Override
	public Context getMeasurementContext(URI measurementURI, ContextType... contextTypes) 
		throws ContextProviderException {
		
		try {
			
			Context contextData = new Context();
			
			EntityManager em = this.connectorBean.getEntityManagerByURI(measurementURI);
			Measurement measurement =  lookupEntityByURI(Measurement.class, em, measurementURI);
			
			Map<ContextType, ContextRoot> measuredContext = em.loadContexts(measurement, contextTypes);
			Map<ContextType, ContextRoot> orderedContext = lookupOrderedContextByMeasurement(em, 
				measurement, contextTypes);
			
			contextData.setOrderedContext(orderedContext);
			contextData.setMeasuredContext(measuredContext);
			
			return contextData;
			
		} catch(ConnectorException | DataAccessException e) {
			throw new ContextProviderException(e.getMessage(), e);
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

	
	
	@SuppressWarnings("unchecked")
	private <T extends Entity> T lookupEntityByURI(Class<T> type, EntityManager em, URI uri) throws ConnectorException, 
		DataAccessException  {

		Optional<? extends Entity> optinal = em.load(uri);
		if(!optinal.isPresent()) {
			throw new ConnectorException("mdm entry with uri '" + uri.toString() + " not found");
		}
		return (T)optinal.get();		
	}
}
