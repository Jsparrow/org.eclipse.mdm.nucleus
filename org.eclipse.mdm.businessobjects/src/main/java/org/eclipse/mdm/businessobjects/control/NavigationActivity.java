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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.connector.boundary.ConnectorService;

/**
 * NavigationActivity Bean implementation to lookup specified business object children
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class NavigationActivity {

	@EJB
	private ConnectorService connectorService;	
	
	
	
	/**
	 * returns the MDM {@link Environment} business objects of all connected MDM systems
	 * 
	 * @return MDM {@link Environment} business objects
	 */
	public List<Environment> getEnvironments() {
		try {
			List<Environment> envList = new ArrayList<>();
			List<EntityManager> emList = this.connectorService.getEntityManagers();
			for(EntityManager em : emList) {
				envList.add(em.loadEnvironment());
			}
			return envList;
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	
	
	/**
	 * returns all MDM {@link Test} business objects of the connected MDM system identified by the given {@link URI}
	 * 
	 * @param environemntURI {@link URI} to identify the MDM system 
	 * @return MDM {@link Test} business objects
	 */
	public List<Test> getTests(URI environemntURI) {
		try {
			EntityManager em = this.connectorService.getEntityManagerByURI(environemntURI);
			return em.loadAll(Test.class);			
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	
	
	/**
	 * returns all MDM {@link TestStep} business object children for a MDM {@link Test} identified by the given {@link URI}
	 * 
	 * @param testURI {@link URI} to identify the parent MDM {@link Test}
	 * @return MDM {@link TestStep} business objects
	 */
	public List<TestStep> getTestSteps(URI testURI) {
		return getChildren(TestStep.class, testURI);
	}

	
	
	/**
	 * returns all MDM {@link Measurement} business object children for a MDM {@link TestStep} identified by the given {@link URI}
	 * 
	 * @param testStepURI URI to identify the parent MDM TestStep
	 * @return MDM Measurement business objects
	 */
	public List<Measurement> getMeasurements(URI testStepURI) {		
		return getChildren(Measurement.class, testStepURI);
	}

	
	
	/**
	 * returns all MDM ChannelGroup business object children for a MDM {@link Measurement} identified by the given {@link URI}
	 * 
	 * @param measurementURI {@link URI} to identify the parent MDM {@link Measurement}
	 * @return MDM {@link ChannelGroup} business objects
	 */
	public List<ChannelGroup> getChannelGroups(URI measurementURI) {
		return getChildren(ChannelGroup.class, measurementURI);
	}

	
	
	/**
	 * returns all MDM {@link Channel} business object children for a MDM {@link ChannelGroup} identified by the given {@link URI} 
	 * 
	 * @param channelGroupURI {@link URI} to identify the parent MDM {@link ChannelGroup}
	 * @return MDM {@link Channel} business objects
	 */
	public List<Channel> getChannels(URI channelGroupURI) {
		return getChildren(Channel.class, channelGroupURI);
	}	
	
	
	
	private <T extends Entity> List<T> getChildren(Class<T> type, URI parentURI) {
		
		try {			
			EntityManager em = this.connectorService.getEntityManagerByURI(parentURI);	
			T parentEntity = ServiceUtils.lookupEntityByURI(type, em, parentURI);			
			return em.loadChildren(parentEntity, type);
		
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}
		
}
