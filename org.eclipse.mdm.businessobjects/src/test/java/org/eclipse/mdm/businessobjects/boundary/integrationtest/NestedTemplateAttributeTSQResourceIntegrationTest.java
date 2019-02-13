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
 * Test class for NestedTemplateAttributeResource for TestSequence
 * {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class NestedTemplateAttributeTSQResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing NestedTemplateAttributeTSQResourceIntegrationTest");

		// prepare test data for creating the CatalogAttribute
		CatalogAttributeTSQResourceIntegrationTest.prepareTestData();
		CatalogAttributeTSQResourceIntegrationTest.createEntity();

		// prepare test data for creating the NestedTemplateComponent
		NestedTemplateComponentTSQResourceIntegrationTest.prepareTestData();
		NestedTemplateComponentTSQResourceIntegrationTest.createEntity();

		setContextClass(NestedTemplateAttributeTSQResourceIntegrationTest.class);

		// set up test data
		putTestDataValue(TESTDATA_RESOURCE_URI, new StringBuilder().append("/tplroots/testsequence/").append(getTestDataValue(TemplateRootTSQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/tplcomps/").append(getTestDataValue(TemplateComponentTSQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/tplcomps/").append(getTestDataValue(NestedTemplateComponentTSQResourceIntegrationTest.class, TESTDATA_ENTITY_ID))
				.append("/tplattrs").toString());
		putTestDataValue(TESTDATA_ENTITY_NAME, getTestDataValue(CatalogAttributeTSQResourceIntegrationTest.class, TESTDATA_ENTITY_NAME));
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateAttribute");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(CatalogAttributeTSQResourceIntegrationTest.class, TESTDATA_ENTITY_NAME)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());

		// delete the implicitly created NestedTemplateAttribute
		NestedTemplateAttributeTSQResourceIntegrationTest.findFirst();
		NestedTemplateAttributeTSQResourceIntegrationTest.deleteEntity();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(NestedTemplateComponentTSQResourceIntegrationTest.class);
		NestedTemplateComponentTSQResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateComponentTSQResourceIntegrationTest.class);
		TemplateComponentTSQResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateRootTSQResourceIntegrationTest.class);
		TemplateRootTSQResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogAttributeTSQResourceIntegrationTest.class);
		CatalogAttributeTSQResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogComponentTSQResourceIntegrationTest.class);
		CatalogComponentTSQResourceIntegrationTest.deleteEntity();
	}
}
