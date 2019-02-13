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

package org.eclipse.mdm.businessobjects.boundary.integrationtest;

import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.businessobjects.boundary.ResourceConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Test class for TemplateComponentResource for TestEquipment
 * {@link ContextType}.
 * 
 * @author Philipp Schweinbenz, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class TemplateSensorAttributeResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing TemplateSensorAttributeResourceIntegrationTest");

		// prepare test data for creating the CatalogSensorAttribute
		CatalogSensorAttributeResourceIntegrationTest.prepareTestData();
		CatalogSensorAttributeResourceIntegrationTest.createEntity();

		// prepare test data for creating the TemplateSensor
		TemplateSensorResourceIntegrationTest.prepareTestData();
		TemplateSensorResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(TemplateSensorAttributeResourceIntegrationTest.class);

		// skip the creation test as TemplateSensorAttributes are implicitly created
		// with the TemplateSensor
		skipTest(TestType.CREATE);
		// skip the deletion test as TemplateSensorAttributes can't be deleted
		skipTest(TestType.DELETE);

		putTestDataValue(TESTDATA_RESOURCE_URI, new StringBuilder().append("/tplroots/testequipment/").append(getTestDataValue(TemplateRootTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/tplcomps/").append(getTestDataValue(TemplateComponentTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/tplsensors/").append(getTestDataValue(TemplateSensorResourceIntegrationTest.class, TESTDATA_ENTITY_ID))
				.append("/tplsensorattrs").toString());
		putTestDataValue(TESTDATA_ENTITY_NAME,
				getTestDataValue(CatalogSensorAttributeResourceIntegrationTest.class, TESTDATA_ENTITY_NAME));
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateAttribute");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(
				getTestDataValue(CatalogSensorAttributeResourceIntegrationTest.class, TESTDATA_ENTITY_NAME)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());

		// get first TemplateSensorAttribute
		TemplateSensorAttributeResourceIntegrationTest.findFirst();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(TemplateSensorResourceIntegrationTest.class);
		TemplateSensorResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateComponentTEQResourceIntegrationTest.class);
		TemplateComponentTEQResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateRootTEQResourceIntegrationTest.class);
		TemplateRootTEQResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogSensorAttributeResourceIntegrationTest.class);
		CatalogSensorAttributeResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogSensorResourceIntegrationTest.class);
		CatalogSensorResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogComponentTEQResourceIntegrationTest.class);
		CatalogComponentTEQResourceIntegrationTest.deleteEntity();

		setContextClass(QuantityResourceIntegrationTest.class);
		QuantityResourceIntegrationTest.deleteEntity();
	}
}
