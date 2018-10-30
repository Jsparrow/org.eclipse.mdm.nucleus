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
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		// TODO anehmer on 2017-11-17: create unit (and physDim) instead of taking fixed
		// one
		json.add(ResourceConstants.ENTITYATTRIBUTE_UNIT_ID, new JsonPrimitive(getTestDataValue(UnitResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(UnitResourceIntegrationTest.class);
		UnitResourceIntegrationTest.deleteEntity();
	}
}
