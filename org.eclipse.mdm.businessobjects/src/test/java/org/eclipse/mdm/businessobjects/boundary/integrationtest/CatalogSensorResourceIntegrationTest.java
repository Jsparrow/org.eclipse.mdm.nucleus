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
 * Test class for CatalogSensorResource
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class CatalogSensorResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing CatalogSensorResourceIntegrationTest");

		// prepare test data for creating the CatalogComponent
		CatalogComponentTEQResourceIntegrationTest.prepareTestData();
		CatalogComponentTEQResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(CatalogSensorResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, new StringBuilder().append("/catcomps/testequipment/").append(getTestDataValue(CatalogComponentTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/catsensors").toString());
		putTestDataValue(TESTDATA_ENTITY_NAME, "testCatSensor");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "CatalogSensor");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(CatalogComponentTEQResourceIntegrationTest.class);
		CatalogComponentTEQResourceIntegrationTest.deleteEntity();
	}
}
