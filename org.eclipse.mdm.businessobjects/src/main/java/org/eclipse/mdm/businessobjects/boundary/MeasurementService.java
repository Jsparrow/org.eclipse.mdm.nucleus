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
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.businessobjects.control.ContextActivity;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.NavigationActivity;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.connector.boundary.ConnectorService;

/**
 * MeasurementService Bean implementation with available {@link Measurement} operations
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class MeasurementService {

	@EJB
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
	 * returns the matching {@link Measurement}s using the given filter or all {@link Measurement}s 
	 * if no filter is available
	 * 
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param filter filter string to filter the {@link Measurement} result
	 * @return the found {@link Measurement}s
	 */
	public List<Measurement> getMeasurements(String sourceName, String filter) {
		try {			
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			
			if(filter == null || filter.trim().length() <= 0) {
				return em.loadAll(Measurement.class);
			}
			
			if(ServiceUtils.isParentFilter(em, filter, Measurement.PARENT_TYPE_TESTSTEP)) {
				long id = ServiceUtils.extactIdFromParentFilter(em, filter, Measurement.PARENT_TYPE_TESTSTEP);
				URI testStepURI = ServiceUtils.createMDMURI(em, sourceName, Measurement.PARENT_TYPE_TESTSTEP, id);
				return this.navigationActivity.getMeasurements(testStepURI);
			}
			
			return this.searchActivity.search(em, Measurement.class, filter);
	
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		} 
	}
	
	/**
	 * Returns the {@link SearchAttribute} for the entity type Measurement in the given data source.
	 * @param sourceName The name of the data source.
	 * @return the found {@link SearchAttribute}s
	 */
	public List<SearchAttribute> getSearchAttributes(String sourceName) {
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		return this.searchActivity.listAvailableAttributes(em, Measurement.class);
	}
	
	/**
	 * returns a {@link Measurement} identified by the given id.
	 * 
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param measurementId id of the {@link Measurement}
	 * @return the matching {@link Measurement}
	 */
	public Measurement getMeasurement(String sourceName, long measurementId) {
		try {		
			EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
			URI measurementURI = ServiceUtils.createMDMURI(em, sourceName, Measurement.class, measurementId);
			Optional<Measurement> optional = em.load(measurementURI);
			
			if(!optional.isPresent()) {
				String message = "mdm Measurement with id '" + measurementId 
						+ "' does not exist at data source with name '"	+ sourceName + "'";
				throw new MDMEntityAccessException(message);
			}
			
			return optional.get();
		} catch(DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}
	
	
	
	/**
	 * returns the complete context data (ordered and measured) for a {@link Measurement}
	 * 
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param measurementId id of the {@link Measurement}
	 * @return a map with the complete context data (ordered and measured)
	 */
	public Map<String, Map<ContextType, ContextRoot>> getContext(String sourceName, long measurementId) {
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		URI measurementURI = ServiceUtils.createMDMURI(em, sourceName, Measurement.class, measurementId);
		return this.contextActivity.getMeasurementContext(measurementURI);
	}
	
	
		
	/**
	 * returns the UnitUnderTest context data (ordered and measured) for a {@link Measurement}
	 * 
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param measurementIdId id of the {@link Measurement}
	 * @return a map with the UnitUnderTest context data (ordered and measured)
	 */
	public Map<String, Map<ContextType, ContextRoot>> getContextUUT(String sourceName, long measurementId) {
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		URI measurementURI = ServiceUtils.createMDMURI(em, sourceName, Measurement.class, measurementId);
		return this.contextActivity.getMeasurementContext(measurementURI, ContextType.UNITUNDERTEST);
	}
	
	
	
	/**
	 * returns the TestSequence context data (ordered and measured) for a {@link Measurement}
	 * 
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param measurementId id of the {@link Measurement}
	 * @return a map with the TestSequence context data (ordered and measured)
	 */
	public Map<String, Map<ContextType, ContextRoot>> getContextTSQ(String sourceName, long measurementId) {
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		URI measurementURI = ServiceUtils.createMDMURI(em, sourceName, Measurement.class, measurementId);
		return this.contextActivity.getMeasurementContext(measurementURI, ContextType.TESTSEQUENCE);
	}
	
	
	
	/**
	 * returns the TestEquipment context data (ordered and measured) for a {@link Measurement}
	 * 
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param measurementId id of the {@link Measurement}
	 * @return a map with the TestEquipment context data (ordered and measured)
	 */
	public Map<String, Map<ContextType, ContextRoot>> getContextTEQ(String sourceName, long measurementId) {
		EntityManager em = this.connectorService.getEntityManagerByName(sourceName);
		URI measurementURI = ServiceUtils.createMDMURI(em, sourceName, Measurement.class, measurementId);
		return this.contextActivity.getMeasurementContext(measurementURI, ContextType.TESTEQUIPMENT);
	}
	
	
	
	/**
	 * returns localized {@link Measurement} attributes
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Measurement} attributes
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName) {		
		return this.i18nActivity.localizeAttributes(sourceName, Measurement.class);
	}	
		
	
	
	/**
	 * returns the localized {@link Measurement} type name
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @return the localized {@link Measurement} type name
	 */
	public Map<EntityType, String> localizeType(String sourceName) {
		return this.i18nActivity.localizeType(sourceName, Measurement.class);
	}
}
