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

package org.eclipse.mdm.businessobjects.boundary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.connector.boundary.ConnectorService;

/**
 * {@link Environment} Bean implementation with available {@link Environment} operations
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class EnvironmentService {
	
	@EJB
	private ConnectorService connectorService;	
	@EJB
	private I18NActivity i18nActivity;
	
		
	
	/**
	 * returns all {@link Environment}s of the connected {@link EntityManager}s
	 * 
	 * @return the connected {@link Environment}s
	 */
	public List<Environment> getEnvironments() {
		try {
			List<Environment> environments = new ArrayList<>();		
			List<EntityManager> emList = this.connectorService.getEntityManagers();
			for(EntityManager em : emList) {
				environments.add(em.loadEnvironment());
			}
			return environments;
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
		
	}
	
	
	
	/**
	 * returns the {@link Environment} identified by the given name
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @return the {@link Environment} with the given name
	 */
	public Environment getEnvironment(String sourceName) {
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			return em.loadEnvironment();
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}
	
	
	
	/**
	 * returns localized {@link Environment} attributes
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Environment} attributes
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName) {		
		return this.i18nActivity.localizeAttributes(sourceName, Environment.class);
	}
	
	
	
	/**
	 * returns the localized {@link Environment} type name
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Environment} type name
	 */
	public Map<EntityType, String> localizeType(String sourceName) {
		return this.i18nActivity.localizeType(sourceName, Environment.class);
	}
	
}
