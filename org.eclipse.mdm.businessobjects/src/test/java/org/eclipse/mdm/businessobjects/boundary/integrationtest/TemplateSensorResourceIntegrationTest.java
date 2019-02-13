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
 * Test class for TemplateSensorResource {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class TemplateSensorResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing TemplateAttributeTEQResourceIntegrationTest");

		// prepare test data for creating the CatalogSensor
		CatalogSensorResourceIntegrationTest.prepareTestData();
		CatalogSensorResourceIntegrationTest.createEntity();

		// prepare test data for creating the TemplateComponent
		TemplateComponentTEQResourceIntegrationTest.prepareTestData();
		TemplateComponentTEQResourceIntegrationTest.createEntity();

		// prepare test data for creating the Quantity
		QuantityResourceIntegrationTest.prepareTestData();
		QuantityResourceIntegrationTest.createEntity();

		setContextClass(TemplateSensorResourceIntegrationTest.class);

		// set up test data
		putTestDataValue(TESTDATA_RESOURCE_URI, new StringBuilder().append("/tplroots/testequipment/").append(getTestDataValue(TemplateRootTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/tplcomps/").append(getTestDataValue(TemplateComponentTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/tplsensors").toString());
		putTestDataValue(TESTDATA_ENTITY_NAME, "testTplSensor");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateSensor");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		json.add(ResourceConstants.ENTITYATTRIBUTE_CATALOGSENSOR_ID, new JsonPrimitive(getTestDataValue(CatalogSensorResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		json.add(ResourceConstants.ENTITYATTRIBUTE_QUANTITY_ID, new JsonPrimitive(getTestDataValue(QuantityResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));

		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());

		json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_QUANTITY_ID,
				new JsonPrimitive(getTestDataValue(QuantityResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_UPDATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(TemplateComponentTEQResourceIntegrationTest.class);
		TemplateComponentTEQResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateRootTEQResourceIntegrationTest.class);
		TemplateRootTEQResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogSensorResourceIntegrationTest.class);
		CatalogSensorResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogComponentTEQResourceIntegrationTest.class);
		CatalogSensorResourceIntegrationTest.deleteEntity();

		setContextClass(QuantityResourceIntegrationTest.class);
		QuantityResourceIntegrationTest.deleteEntity();
	}
}
