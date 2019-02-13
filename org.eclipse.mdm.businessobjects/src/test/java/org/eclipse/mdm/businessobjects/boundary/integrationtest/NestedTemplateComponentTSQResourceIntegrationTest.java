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

import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.businessobjects.boundary.ResourceConstants;
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

		putTestDataValue(TESTDATA_RESOURCE_URI, new StringBuilder().append("/tplroots/testsequence/").append(getTestDataValue(TemplateRootTSQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/tplcomps/").append(getTestDataValue(TemplateComponentTSQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/tplcomps").toString());
		putTestDataValue(TESTDATA_ENTITY_NAME, "testNestedTplCompTSQ");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateComponent");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		json.add(ResourceConstants.ENTITYATTRIBUTE_CATALOGCOMPONENT_ID, new JsonPrimitive(getTestDataValue(CatalogComponentTSQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(TemplateComponentTSQResourceIntegrationTest.class);
		TemplateComponentTSQResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateRootTSQResourceIntegrationTest.class);
		TemplateRootTSQResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogComponentTSQResourceIntegrationTest.class);
		CatalogComponentTSQResourceIntegrationTest.deleteEntity();
	}
}
