/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.navigator.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.EntityManager;
import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.connector.ConnectorBeanLI;
import org.eclipse.mdm.connector.ConnectorException;
import org.eclipse.mdm.navigator.NavigatorBeanLI;
import org.eclipse.mdm.navigator.NavigatorException;

/**
 * Bean implementation of {@link NavigatorBeanLI}
 * @author Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
@LocalBean
public class NavigatorBean implements NavigatorBeanLI {

	@EJB
	private ConnectorBeanLI connectorBean;

	
	
	@Override	
	public List<Environment> getEnvironments() throws NavigatorException {
		try {
			List<Environment> envList = new ArrayList<>();
			List<EntityManager> emList = this.connectorBean.getEntityManagers();
			for(EntityManager em : emList) {
				envList.add(em.loadEnvironment());
			}
			return envList;
		} catch(ConnectorException | DataAccessException e) {
			throw new NavigatorException(e.getMessage(), e);
		}
	}

	
	
	@Override
	public List<Test> getTests(URI environemntURI) throws NavigatorException {
		try {
			EntityManager em = this.connectorBean.getEntityManagerByURI(environemntURI);
			return em.loadAll(Test.class);			
		} catch(ConnectorException | DataAccessException e) {
			throw new NavigatorException(e.getMessage(), e);
		}
	}

	
	
	@Override
	public List<TestStep> getTestSteps(URI testURI) throws NavigatorException {
		return getChildren(TestStep.class, testURI);
	}

	
	
	@Override
	public List<Measurement> getMeasurements(URI testStepURI) throws NavigatorException {		
		return getChildren(Measurement.class, testStepURI);
	}

	
	
	@Override
	public List<ChannelGroup> getChannelGroups(URI measurementURI) throws NavigatorException {
		return getChildren(ChannelGroup.class, measurementURI);
	}

	
	
	@Override
	public List<Channel> getChannels(URI channelGroupURI) throws NavigatorException {
		return getChildren(Channel.class, channelGroupURI);
	}	
	
	
	
	@Override
	public URI createURI(String sourceName, Class<? extends Entity> type, long id) throws NavigatorException {
		
		try {
		
			EntityManager em = this.connectorBean.getEntityManagerByName(sourceName);
			Optional<ModelManager> oMM = em.getModelManager();
			if(!oMM.isPresent()) {
				throw new NavigatorException("neccessary ModelManager is not present!");
			}
			ModelManager modelManager = oMM.get();
			String typeName = modelManager.getEntityType(type).getName();
			return new URI(sourceName, typeName, id);
		
		} catch(ConnectorException e) {
			throw new NavigatorException(e.getMessage(), e);
		}
	}	

	
	
	private <T extends Entity> List<T> getChildren(Class<T> type, URI parentURI) throws NavigatorException {
		
		try {			
			EntityManager em = this.connectorBean.getEntityManagerByURI(parentURI);			
			Optional<Entity> oEntity = em.load(parentURI);
			if(!oEntity.isPresent()) {
				throw new NavigatorException("mdm entry with uri '" + parentURI.toString() + " not found");
			}
			Entity entity = oEntity.get();
			return em.loadChildren(entity, type);
		
		} catch(ConnectorException | DataAccessException e) {
			throw new NavigatorException(e.getMessage(), e);
		}
	}

	
		
}
