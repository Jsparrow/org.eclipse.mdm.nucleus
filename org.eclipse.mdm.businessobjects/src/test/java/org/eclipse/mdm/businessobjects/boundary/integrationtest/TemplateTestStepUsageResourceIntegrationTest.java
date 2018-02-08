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
		skipTest(TestType.UPDATE);

		putTestDataValue(TESTDATA_RESOURCE_URI,
				"/tpltests/" + getTestDataValue(TemplateTestResourceIntegrationTest.class, TESTDATA_ENTITY_ID)
						+ "/tplteststepusages");
		// indicates that the value is set with the response data from create()
		putTestDataValue(TESTDATA_ENTITY_NAME, TESTDATA_RANDOM_DATA);
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateTestStepUsage");

		JsonObject json = new JsonObject();
		json.add("name", new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		json.add("templateteststep", new JsonPrimitive(getTestDataValue(TemplateTestStepResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		json.add("templatetest",
				new JsonPrimitive(getTestDataValue(TemplateTestResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(TemplateTestResourceIntegrationTest.class);
		TemplateTestResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateTestStepResourceIntegrationTest.class);
		TemplateTestStepResourceIntegrationTest.deleteEntity();
	}
}
