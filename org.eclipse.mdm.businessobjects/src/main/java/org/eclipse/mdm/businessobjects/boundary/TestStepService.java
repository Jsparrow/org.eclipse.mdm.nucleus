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

import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextSensor;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.TestStep;
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
 * TestStepService Bean implementation with available {@link TestStep}
 * operations
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class TestStepService {

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
	 * returns the matching {@link TestStep}s using the given filter or all
	 * {@link TestStep}s if no filter is available
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the TestStep result
	 * @return the found {@link TestStep}s
	 */
	public List<TestStep> getTestSteps(String sourceName, String filter) {
		try {

			ApplicationContext context = this.connectorService.getContextByName(sourceName);
			EntityManager em = context
					.getEntityManager()
					.orElseThrow(() -> new MDMEntityAccessException("Entity manager not present!"));

			if (filter == null || filter.trim().length() <= 0) {
				return em.loadAll(TestStep.class);
			}

			if (ServiceUtils.isParentFilter(context, filter, TestStep.PARENT_TYPE_TEST)) {
				String id = ServiceUtils.extactIdFromParentFilter(context, filter, TestStep.PARENT_TYPE_TEST);
				return this.navigationActivity.getTestSteps(sourceName, id);
			}

			return this.searchActivity.search(context, TestStep.class, filter);

		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the {@link SearchAttribute} for the entity type TestStep in the
	 * given data source.
	 * 
	 * @param sourceName
	 *            The name of the data source.
	 * @return the found {@link SearchAttribute}s
	 */
	public List<SearchAttribute> getSearchAttributes(String sourceName) {
		return this.searchActivity.listAvailableAttributes(this.connectorService.getContextByName(sourceName), TestStep.class);
	}

	/**
	 * returns a {@link TestStep} identified by the given id.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link TestStep}
	 * @return the matching {@link TestStep}
	 */
	public TestStep getTestStep(String sourceName, String testStepId) {
		try {
			return this.connectorService.getContextByName(sourceName)
					.getEntityManager()
					.map(em -> em.load(TestStep.class, testStepId))
					.orElseThrow(() -> new MDMEntityAccessException("Entity manager not present!"));
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	/**
	 * returns the complete context data (ordered and measured) for a
	 * {@link TestStep}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link TestStep}
	 * @return a map with the complete context data (ordered and measured)
	 */
	public Map<String, Map<ContextType, ContextRoot>> getContext(String sourceName, String testStepId) {
		return this.contextActivity.getTestStepContext(sourceName, testStepId);
	}

	/**
	 * returns the UnitUnderTest context data (ordered and measured) for a
	 * {@link TestStep}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link TestStep}
	 * @return a map with the UnitUnderTest context data (ordered and measured)
	 */
	public Map<String, Map<ContextType, ContextRoot>> getContextUUT(String sourceName, String testStepId) {
		return this.contextActivity.getTestStepContext(sourceName, testStepId, ContextType.UNITUNDERTEST);
	}

	/**
	 * returns the TestSequence context data (ordered and measured) for a
	 * {@link TestStep}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link TestStep}
	 * @return a map with the TestSequence context data (ordered and measured)
	 */
	public Map<String, Map<ContextType, ContextRoot>> getContextTSQ(String sourceName, String testStepId) {
		return this.contextActivity.getTestStepContext(sourceName, testStepId, ContextType.TESTSEQUENCE);
	}

	/**
	 * returns the TestEquipment context data (ordered and measured) for a
	 * {@link TestStep}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link TestStep}
	 * @return a map with the TestEquipment context data (ordered and measured)
	 */
	public Map<String, Map<ContextType, ContextRoot>> getContextTEQ(String sourceName, String testStepId) {
		return this.contextActivity.getTestStepContext(sourceName, testStepId, ContextType.TESTEQUIPMENT);
	}

	/**
	 * returns all sensor context data of TestEquipment sensor configuration
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testStepId
	 *            id of the {@link TestStep}
	 * @return a map with the TestEquipment sensor context data (ordered and
	 *         measured)
	 */
	public Map<String, List<ContextSensor>> getSensors(String sourceName, String testStepId) {
		return this.contextActivity.getTestStepSensorContext(sourceName, testStepId);
	}

	/**
	 * returns localized {@link TestStep} attributes
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link TestStep} attributes
	 */
	public Map<Attribute, String> localizeAttributes(String sourceName) {
		return this.i18nActivity.localizeAttributes(sourceName, TestStep.class);
	}

	/**
	 * returns the localized {@link TestStep} type name
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the localized {@link TestStep} type name
	 */
	public Map<EntityType, String> localizeType(String sourceName) {
		return this.i18nActivity.localizeType(sourceName, TestStep.class);
	}
}
