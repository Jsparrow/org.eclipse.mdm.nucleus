/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.action.delete.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.eclipse.mdm.action.delete.DeleteActionBeanLI;
import org.eclipse.mdm.action.delete.DeleteActionException;
import org.eclipse.mdm.api.base.EntityManager;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.businesstyperegistry.ActionBeanLI;
import org.eclipse.mdm.connector.ConnectorBeanLI;
import org.eclipse.mdm.connector.ConnectorException;

/**
 * Bean implementation {@link DeleteActionBeanLI} and {@link ActionBeanLI})
 * @author Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
@LocalBean
public class DeleteActionBean implements DeleteActionBeanLI, ActionBeanLI {

	@EJB
	private ConnectorBeanLI connectorBean;
	
	@Override
	public String getActionName() {
		return "delete";
	}

	@Override
	public List<Class<? extends Entity>> getSupportedEntityTypes() {
		List<Class<? extends Entity>> types = new ArrayList<>();
		types.add(Test.class);
		types.add(TestStep.class);
		types.add(Measurement.class);
		return types;
	}

	@Override
	public void delete(URI uri) throws DeleteActionException {
		//TODO: implementation of delete process
	}

	
	@Override
	public URI createURI(String sourceName, Class<? extends Entity> type, long id) throws DeleteActionException {
		
		try {
		
			EntityManager em = this.connectorBean.getEntityManagerByName(sourceName);
			Optional<ModelManager> oMM = em.getModelManager();
			if(!oMM.isPresent()) {
				throw new DeleteActionException("neccessary ModelManager is not present!");
			}
			ModelManager modelManager = oMM.get();
			String typeName = modelManager.getEntityType(type).getName();
			return new URI(sourceName, typeName, id);
		
		} catch(ConnectorException e) {
			throw new DeleteActionException(e.getMessage(), e);
		}
	}	
}
