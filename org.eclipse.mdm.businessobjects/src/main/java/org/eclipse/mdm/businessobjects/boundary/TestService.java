/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/

package org.eclipse.mdm.businessobjects.boundary;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.ApplicationContext;
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

	@Inject
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
			ApplicationContext context = this.connectorService.getContextByName(sourceName);
			EntityManager em = context
					.getEntityManager()
					.orElseThrow(() -> new MDMEntityAccessException("Entity manager not present!"));

			if (filter == null || StringUtils.trim(filter).length() <= 0) {
				return em.loadAll(Test.class);
			}

			if (ServiceUtils.isParentFilter(context, filter, Pool.class)) {
				String id = ServiceUtils.extactIdFromParentFilter(context, filter, Pool.class);
				return this.navigationActivity.getTests(sourceName, id);
			}

			return this.searchActivity.search(context, Test.class, filter);
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
		return this.searchActivity.listAvailableAttributes(this.connectorService.getContextByName(sourceName), Test.class);
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
			EntityManager em = this.connectorService.getContextByName(sourceName)
					.getEntityManager()
					.orElseThrow(() -> new MDMEntityAccessException("Entity manager not present!"));
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
