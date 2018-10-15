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
 * Test class for TemplateTestStepResource.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class TemplateTestStepResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing TemplateTestResourceIntegrationTest");

		// prepare test data for creating the TemplateRoots
		TemplateRootTEQResourceIntegrationTest.prepareTestData();
		TemplateRootTEQResourceIntegrationTest.createEntity();
		TemplateRootTSQResourceIntegrationTest.prepareTestData();
		TemplateRootTSQResourceIntegrationTest.createEntity();
		TemplateRootUUTResourceIntegrationTest.prepareTestData();
		TemplateRootUUTResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(TemplateTestStepResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, "/tplteststeps");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testTplTestStep");
		putTestDataValue(TESTDATA_ENTITY_TYPE, ResourceConstants.ENTITYATTRIBUTE_TEMPLATETESTSTEP_ID);

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());

		json = new JsonObject();
		JsonObject enumerationJson = new JsonObject();
		enumerationJson.add("Enumeration", new JsonPrimitive("VersionState"));
		enumerationJson.add("EnumerationValue", new JsonPrimitive("VALID"));
		json.add("ValidFlag", enumerationJson);

		json.add("TemplateTestEquipmentRoot",
				new JsonPrimitive(getTestDataValue(TemplateRootTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		json.add("TemplateTestSequenceRoot",
				new JsonPrimitive(getTestDataValue(TemplateRootTSQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		json.add("TemplateUnitUnderTestRoot",
				new JsonPrimitive(getTestDataValue(TemplateRootUUTResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));

		putTestDataValue(TESTDATA_UPDATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(TemplateRootTEQResourceIntegrationTest.class);
		TemplateRootTEQResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateRootTSQResourceIntegrationTest.class);
		TemplateRootTSQResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateRootUUTResourceIntegrationTest.class);
		TemplateRootUUTResourceIntegrationTest.deleteEntity();
	}
}
