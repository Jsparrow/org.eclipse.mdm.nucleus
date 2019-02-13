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

import org.eclipse.mdm.businessobjects.boundary.ResourceConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Test class for UnitResource.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class UnitResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing UnitResourceIntegrationTest");

		// prepare test data for creating the PhysicalDimension
		PhysicalDimensionResourceIntegrationTest.prepareTestData();
		PhysicalDimensionResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(UnitResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, "/units");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testUnit");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "Unit");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		json.add(ResourceConstants.ENTITYATTRIBUTE_PHYSICALDIMENSION_ID, new JsonPrimitive(
				getTestDataValue(PhysicalDimensionResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());

		json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_PHYSICALDIMENSION_ID, new JsonPrimitive(
				getTestDataValue(PhysicalDimensionResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_UPDATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(PhysicalDimensionResourceIntegrationTest.class);
		PhysicalDimensionResourceIntegrationTest.deleteEntity();
	}
}
