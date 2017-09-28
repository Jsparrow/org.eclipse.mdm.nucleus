/*******************************************************************************
 * Copyright (c) 2017 science + computing AG Tuebingen (ATOS SE)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Nehmer - initial implementation
 *******************************************************************************/
package org.eclipse.mdm.businessobjects.boundary;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.ValueList;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.connector.boundary.ConnectorService;

/**
 * ValueListService Bean implementation with available {@link ValueList}
 * operations
 * 
 * @author Alexander Nehmer, science + computing AG Tuebingen (Atos SE)
 *
 */
@Stateless
public class ValueListService {

	@EJB
	private ConnectorService connectorService;
	@EJB
	private I18NActivity i18nActivity;
	@EJB
	private SearchActivity searchActivity;

	/**
	 * Returns a {@link ValueList} identified by the given id.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param valueListId
	 *            id of the {@link ValueList}
	 * @return the matching {@link ValueList}
	 */
	public ValueList getValueList(String sourceName, String valueListId) {
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			return em.load(ValueList.class, valueListId);
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the matching {@link ValueList}s using the given filter or all
	 * {@link ValueList}s if no filter is available
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link ValueList} result
	 * @return the found {@link ValueList}s
	 */
	public List<ValueList> getValueLists(String sourceName, String filter) {

		try {
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);

			if (filter == null || filter.trim().length() <= 0) {
				return em.loadAll(ValueList.class);
			}

			return this.searchActivity.search(em, ValueList.class, filter);
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the {@link SearchAttribute} for the entity type ValueList in the
	 * given data source.
	 * 
	 * @param sourceName
	 *            The name of the data source.
	 * @return the found {@link SearchAttribute}s
	 */
	public List<SearchAttribute> getSearchAttributes(String sourceName) {
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		return this.searchActivity.listAvailableAttributes(em, ValueList.class);
	}

	/**
	 * Returns the localized {@link ValueList} type name
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link ValueList} type name
	 */
	public Map<EntityType, String> localizeType(String sourceName) {
		return this.i18nActivity.localizeType(sourceName, ValueList.class);
	}

	/**
	 * Returns localized {@link ValueList} attributes
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link ValueList} attributes
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName) {
		return this.i18nActivity.localizeAttributes(sourceName, ValueList.class);
	}
}