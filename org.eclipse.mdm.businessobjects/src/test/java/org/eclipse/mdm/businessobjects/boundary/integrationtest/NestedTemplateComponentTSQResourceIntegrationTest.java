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
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Test class for NestedTemplateComponentResource for TestSequence
 * {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class NestedTemplateComponentTSQResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing NestedTemplateComponentTSQResourceIntegrationTest");

		// prepare test data for creating the CatalogComponent
		CatalogComponentTSQResourceIntegrationTest.prepareTestData();
		CatalogComponentTSQResourceIntegrationTest.createEntity();

		// prepare test data for creating the TemplateRoot
		TemplateComponentTSQResourceIntegrationTest.prepareTestData();
		TemplateComponentTSQResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(NestedTemplateComponentTSQResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, "/tplroots/testsequence/"
				+ getTestDataValue(TemplateRootTSQResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/tplcomps/"
				+ getTestDataValue(TemplateComponentTSQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)
				+ "/tplcomps");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testNestedTplCompTSQ");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateComponent");

		JsonObject json = new JsonObject();
		json.add("name", new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		json.add("catalogcomponent", new JsonPrimitive(
				getTestDataValue(CatalogComponentTSQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(TemplateRootTSQResourceIntegrationTest.class);
		TemplateRootTSQResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogComponentTSQResourceIntegrationTest.class);
		CatalogComponentTSQResourceIntegrationTest.deleteEntity();
	}
}
