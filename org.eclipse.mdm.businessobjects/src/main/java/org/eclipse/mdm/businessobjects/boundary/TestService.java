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

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.Pool;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.NavigationActivity;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.connector.boundary.ConnectorService;

/**
 * TestService Bean implementation with available {@link Test} operations
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class TestService {

	@EJB
	private ConnectorService connectorService;
	@EJB
	private I18NActivity i18nActivity;
	@EJB
	private SearchActivity searchActivity;
	@EJB
	private NavigationActivity navigationActivity;

	/**
	 * returns the matching {@link Test}s using the given filter or all
	 * {@link Test}s if no filter is available
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link Test} result
	 * @return the found {@link Test}s
	 */
	public List<Test> getTests(String sourceName, String filter) {

		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);

			if (filter == null || filter.trim().length() <= 0) {
				return em.loadAll(Test.class);
			}

			if (ServiceUtils.isParentFilter(em, filter, Pool.class)) {
				String id = ServiceUtils.extactIdFromParentFilter(em, filter, Pool.class);
				return this.navigationActivity.getTests(sourceName, id);
			}

			return this.searchActivity.search(em, Test.class, filter);
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the {@link SearchAttribute} for the entity type Test in the given
	 * data source.
	 * 
	 * @param sourceName
	 *            The name of the data source.
	 * @return the found {@link SearchAttribute}s
	 */
	public List<SearchAttribute> getSearchAttributes(String sourceName) {
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		return this.searchActivity.listAvailableAttributes(em, Test.class);
	}

	/**
	 * returns a {@link Test} identified by the given id.
	 * 
	 * @param testId
	 *            id of the {@link Test}
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link Test}
	 * @return the matching {@link Test}
	 */
	public Test getTest(String sourceName, String testId) {
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			return em.load(Test.class, testId);
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * returns localized {@link Test} attributes
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Test} attributes
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName) {
		return this.i18nActivity.localizeAttributes(sourceName, Test.class);
	}

	/**
	 * returns the localized {@link Test} type name
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Test} type name
	 */
	public Map<EntityType, String> localizeType(String sourceName) {
		return this.i18nActivity.localizeType(sourceName, Test.class);
	}
}
