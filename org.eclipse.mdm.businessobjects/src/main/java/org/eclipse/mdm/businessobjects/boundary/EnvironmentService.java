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
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.connector.boundary.ConnectorService;

/**
 * {@link Environment} Bean implementation with available {@link Environment}
 * operations
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class EnvironmentService {

	@Inject
	private ConnectorService connectorService;
	@EJB
	private I18NActivity i18nActivity;
	@EJB
	private SearchActivity searchActivity;

	/**
	 * returns all {@link Environment}s of the connected {@link EntityManager}s
	 * 
	 * @return the connected {@link Environment}s
	 */
	public List<Environment> getEnvironments() {
		try {
			return this.connectorService.getContexts()
					.stream()
					.map(ApplicationContext::getEntityManager)
					.filter(Optional::isPresent)
				    .map(Optional::get)
					.map(em -> em.loadEnvironment())
					.collect(Collectors.toList());
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}

	}

	/**
	 * returns the {@link Environment} identified by the given name
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the {@link Environment} with the given name
	 */
	public Environment getEnvironment(String sourceName) {
		try {
			EntityManager em = this.connectorService.getContextByName(sourceName)
					.getEntityManager()
					.orElseThrow(() -> new MDMEntityAccessException("Entity manager not present!"));
			return em.loadEnvironment();
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * returns localized {@link Environment} attributes
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Environment} attributes
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName) {
		return this.i18nActivity.localizeAttributes(sourceName, Environment.class);
	}

	/**
	 * returns the localized {@link Environment} type name
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Environment} type name
	 */
	public Map<EntityType, String> localizeType(String sourceName) {
		return this.i18nActivity.localizeType(sourceName, Environment.class);
	}

	/**
	 * returns all localized attributes
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized attributes from the given source
	 */
	public Map<Attribute, String> localizeAllAttributes(String sourceName) {
		return this.i18nActivity.localizeAllAttributes(sourceName);
	}

	/**
	 * returns all localized types
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized types from the given source
	 */
	public Map<EntityType, String> localizeAllTypes(String sourceName) {
		return this.i18nActivity.localizeAllTypes(sourceName);
	}

	public List<Entity> search(String sourceName, String query) {
		ApplicationContext context = this.connectorService.getContextByName(sourceName);

		return this.searchActivity.search(context, query);
	}

}
