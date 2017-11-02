/*******************************************************************************
  * Copyright (c) 2017 Peak Solution GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Matthias Koller - initial implementation
  *******************************************************************************/
package org.eclipse.mdm.businessobjects.boundary;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.Environment;
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
 * PoolService Bean implementation with available {@link Pool} operations
 * 
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
@Stateless
public class PoolService {

	@EJB
	private ConnectorService connectorService;
	@EJB
	private I18NActivity i18nActivity;
	@EJB
	private SearchActivity searchActivity;
	@EJB
	private NavigationActivity navigationActivity;

	/**
	 * Default no-arg constructor for EJB
	 */
	public PoolService() {
		// Default no-arg constructor for EJB
	}

	/**
	 * Contructor for unit testing
	 * 
	 * @param connectorService
	 *            {@link ConnectorService} to use
	 * @param searchActivity
	 *            {@link SearchActivity} to use
	 * @param navigationActivity
	 *            {@link NavigationActivity} to use
	 * @param i18nActivity
	 *            {@link I18NActivity} to use
	 */
	PoolService(ConnectorService connectorService, SearchActivity searchActivity, NavigationActivity navigationActivity,
			I18NActivity i18nActivity) {
		this.connectorService = connectorService;
		this.searchActivity = searchActivity;
		this.navigationActivity = navigationActivity;
		this.i18nActivity = i18nActivity;
	}

	/**
	 * returns the matching {@link Pool}s using the given filter or all
	 * {@link Pool}s if no filter is available
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link Pool} result
	 * @return the found {@link Pool}s
	 */
	public List<Pool> getPools(String sourceName, String filter) {

		try {
			ApplicationContext context = this.connectorService.getContextByName(sourceName);
			EntityManager em = context
					.getEntityManager()
					.orElseThrow(() -> new MDMEntityAccessException("Entity manager not present!"));

			if (filter == null || filter.trim().length() <= 0) {
				return em.loadAll(Pool.class);
			}

			if (ServiceUtils.isParentFilter(context, filter, Pool.PARENT_TYPE_PROJECT)) {
				String id = ServiceUtils.extactIdFromParentFilter(context, filter, Pool.PARENT_TYPE_PROJECT);
				return this.navigationActivity.getPools(sourceName, id);
			}

			return this.searchActivity.search(context, Pool.class, filter);
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the {@link SearchAttribute} for the entity type {@link Pool} in
	 * the given data source.
	 * 
	 * @param sourceName
	 *            The name of the data source.
	 * @return the found {@link SearchAttribute}s
	 */
	public List<SearchAttribute> getSearchAttributes(String sourceName) {
		return this.searchActivity.listAvailableAttributes(this.connectorService.getContextByName(sourceName), Pool.class);
	}

	/**
	 * returns a {@link Pool} identified by the given id.
	 * 
	 * @param poolId
	 *            id of the {@link Pool}
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link Pool}
	 * @return the matching {@link Pool}
	 */
	public Pool getPool(String sourceName, String poolId) {
		try {
			EntityManager em = this.connectorService.getContextByName(sourceName)
					.getEntityManager()
					.orElseThrow(() -> new MDMEntityAccessException("Entity manager not present!"));
			return em.load(Pool.class, poolId);
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * returns localized {@link Pool} attributes
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Pool} attributes
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName) {
		return this.i18nActivity.localizeAttributes(sourceName, Pool.class);
	}

	/**
	 * returns the localized {@link Pool} type name
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Pool} type name
	 */
	public Map<EntityType, String> localizeType(String sourceName) {
		return this.i18nActivity.localizeType(sourceName, Pool.class);
	}
}
