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
 * Test class for CatalogSensorAttributeResource
 * 
 * @author Philipp Schweinbenz, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class CatalogSensorAttributeResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing CatalogSensorAttributeResourceIntegrationTest");

		// prepare test data for creating the CatalogSensor
		CatalogSensorResourceIntegrationTest.prepareTestData();
		CatalogSensorResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(CatalogSensorAttributeResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, "/catcomps/testequipment/"
				+ getTestDataValue(CatalogComponentTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)
				+ "/catsensors/" + getTestDataValue(CatalogSensorResourceIntegrationTest.class, TESTDATA_ENTITY_ID)
				+ "/catsensorattrs");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testCatSensorAttr");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "CatalogAttribute");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		json.add(ResourceConstants.ENTITYATTRIBUTE_DATATYPE, new JsonPrimitive("STRING"));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(CatalogSensorResourceIntegrationTest.class);
		CatalogSensorResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogComponentTEQResourceIntegrationTest.class);
		CatalogComponentTEQResourceIntegrationTest.deleteEntity();
	}
}
