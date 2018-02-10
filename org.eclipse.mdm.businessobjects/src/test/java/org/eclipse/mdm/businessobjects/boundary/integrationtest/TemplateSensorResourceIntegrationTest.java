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
		putTestDataValue(TESTDATA_RESOURCE_URI, "/tplroots/testequipment/" + getTestDataValue(TemplateRootTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/tplcomps/" + getTestDataValue(TemplateComponentTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/tplsensors");
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
