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

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Test class for QuantityResource.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class QuantityResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing QuantityResourceIntegrationTest");

		// prepare test data for creating the Unit
		UnitResourceIntegrationTest.prepareTestData();
		UnitResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(QuantityResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, "/quantities");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testQuantity");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "Quantity");

		JsonObject json = new JsonObject();
		json.add("name", new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		// TODO anehmer on 2017-11-17: create unit (and physDim) instead of taking fixed
		// one
		json.add("unit", new JsonPrimitive("1"));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
		json.add("unit", new JsonPrimitive(getTestDataValue(UnitResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(UnitResourceIntegrationTest.class);
		UnitResourceIntegrationTest.deleteEntity();
	}
}
