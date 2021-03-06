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
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextSensor;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.businessobjects.control.ContextActivity;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.NavigationActivity;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.connector.boundary.ConnectorService;

/**
 * MeasurementService Bean implementation with available {@link Measurement}
 * operations
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class MeasurementService {

	@Inject
	private ConnectorService connectorService;
	@EJB
	private I18NActivity i18nActivity;
	@EJB
	private NavigationActivity navigationActivity;
	@EJB
	private ContextActivity contextActivity;
	@EJB
	private SearchActivity searchActivity;

	/**
	 * returns the matching {@link Measurement}s using the given filter or all
	 * {@link Measurement}s if no filter is available
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link Measurement} result
	 * @return the found {@link Measurement}s
	 */
	public List<Measurement> getMeasurements(String sourceName, String filter) {
		try {
			ApplicationContext context = this.connectorService.getContextByName(sourceName);
			EntityManager em = context
					.getEntityManager()
					.orElseThrow(() -> new MDMEntityAccessException("Entity manager not present!"));

			if (filter == null || StringUtils.trim(filter).length() <= 0) {
				return em.loadAll(Measurement.class);
			}

			if (ServiceUtils.isParentFilter(context, filter, Measurement.PARENT_TYPE_TESTSTEP)) {
				String id = ServiceUtils.extactIdFromParentFilter(context, filter, Measurement.PARENT_TYPE_TESTSTEP);
				return this.navigationActivity.getMeasurements(sourceName, id);
			}

			return this.searchActivity.search(context, Measurement.class, filter);

		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the {@link SearchAttribute} for the entity type Measurement in
	 * the given data source.
	 * 
	 * @param sourceName
	 *            The name of the data source.
	 * @return the found {@link SearchAttribute}s
	 */
	public List<SearchAttribute> getSearchAttributes(String sourceName) {
		return this.searchActivity.listAvailableAttributes(this.connectorService.getContextByName(sourceName), Measurement.class);
	}

	/**
	 * returns a {@link Measurement} identified by the given id.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param measurementId
	 *            id of the {@link Measurement}
	 * @return the matching {@link Measurement}
	 */
	public Measurement getMeasurement(String sourceName, String measurementId) {
		try {
			EntityManager em = this.connectorService.getContextByName(sourceName)
					.getEntityManager()
					.orElseThrow(() -> new MDMEntityAccessException("Entity manager not present!"));
			return em.load(Measurement.class, measurementId);
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * returns the complete context data (ordered and measured) for a
	 * {@link Measurement}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param measurementId
	 *            id of the {@link Measurement}
	 * @return a map with the complete context data (ordered and measured)
	 */
	public Map<String, Map<ContextType, ContextRoot>> getContext(String sourceName, String measurementId) {
		return this.contextActivity.getMeasurementContext(sourceName, measurementId);
	}

	/**
	 * returns the UnitUnderTest context data (ordered and measured) for a
	 * {@link Measurement}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param measurementIdId
	 *            id of the {@link Measurement}
	 * @return a map with the UnitUnderTest context data (ordered and measured)
	 */
	public Map<String, Map<ContextType, ContextRoot>> getContextUUT(String sourceName, String measurementId) {
		return this.contextActivity.getMeasurementContext(sourceName, measurementId, ContextType.UNITUNDERTEST);
	}

	/**
	 * returns the TestSequence context data (ordered and measured) for a
	 * {@link Measurement}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param measurementId
	 *            id of the {@link Measurement}
	 * @return a map with the TestSequence context data (ordered and measured)
	 */
	public Map<String, Map<ContextType, ContextRoot>> getContextTSQ(String sourceName, String measurementId) {
		return this.contextActivity.getMeasurementContext(sourceName, measurementId, ContextType.TESTSEQUENCE);
	}

	/**
	 * returns the TestEquipment context data (ordered and measured) for a
	 * {@link Measurement}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param measurementId
	 *            id of the {@link Measurement}
	 * @return a map with the TestEquipment context data (ordered and measured)
	 */
	public Map<String, Map<ContextType, ContextRoot>> getContextTEQ(String sourceName, String measurementId) {
		return this.contextActivity.getMeasurementContext(sourceName, measurementId, ContextType.TESTEQUIPMENT);
	}

	/**
	 * returns all sensor context data of TestEquipment sensor configuration
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param measurementId
	 *            id of the {@link Measurement}
	 * @return a map with the TestEquipment sensor context data (ordered and
	 *         measured)
	 */
	public Map<String, List<ContextSensor>> getSensors(String sourceName, String measurementId) {
		return this.contextActivity.getMeasurementSensorContext(sourceName, measurementId);
	}

	/**
	 * returns localized {@link Measurement} attributes
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Measurement} attributes
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName) {
		return this.i18nActivity.localizeAttributes(sourceName, Measurement.class);
	}

	/**
	 * returns the localized {@link Measurement} type name
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Measurement} type name
	 */
	public Map<EntityType, String> localizeType(String sourceName) {
		return this.i18nActivity.localizeType(sourceName, Measurement.class);
	}
}
