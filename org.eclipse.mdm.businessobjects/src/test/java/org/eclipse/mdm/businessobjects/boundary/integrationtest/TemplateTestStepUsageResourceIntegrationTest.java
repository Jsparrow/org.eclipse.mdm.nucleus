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
 * Test class for TemplateTestStepUsageResource.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class TemplateTestStepUsageResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing TemplateTestResourceIntegrationTest");

		// prepare test data for creating the Test
		TemplateTestResourceIntegrationTest.prepareTestData();
		TemplateTestResourceIntegrationTest.createEntity();

		// prepare test data for creating the TestStep
		TemplateTestStepResourceIntegrationTest.prepareTestData();
		TemplateTestStepResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(TemplateTestStepUsageResourceIntegrationTest.class);

		// skip the update as TemplateTestStepUsages cannot be updated
		// skipTest(TestType.UPDATE);

		putTestDataValue(TESTDATA_RESOURCE_URI,
				new StringBuilder().append("/tpltests/").append(getTestDataValue(TemplateTestResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/tplteststepusages").toString());
		// indicates that the value is set with the response data from create()
		putTestDataValue(TESTDATA_ENTITY_NAME, TESTDATA_RANDOM_DATA);
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateTestStepUsage");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		json.add(ResourceConstants.ENTITYATTRIBUTE_TEMPLATETESTSTEP_ID, new JsonPrimitive(getTestDataValue(TemplateTestStepResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());

		json = new JsonObject();
		json.add("DefaultActive", new JsonPrimitive(true));
		putTestDataValue(TESTDATA_UPDATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(TemplateTestResourceIntegrationTest.class);
		TemplateTestResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateTestStepResourceIntegrationTest.class);
		TemplateTestStepResourceIntegrationTest.deleteEntity();
	}
}
