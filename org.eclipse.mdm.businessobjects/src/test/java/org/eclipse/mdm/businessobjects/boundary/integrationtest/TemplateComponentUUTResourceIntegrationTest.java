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
 * Test class for TemplateComponentResource for UnitUnderTest
 * {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class TemplateComponentUUTResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing TemplateComponentUUTResourceIntegrationTest");

		// prepare test data for creating the CatalogComponent
		CatalogComponentUUTResourceIntegrationTest.prepareTestData();
		CatalogComponentUUTResourceIntegrationTest.createEntity();

		// prepare test data for creating the TemplateRoot
		TemplateRootUUTResourceIntegrationTest.prepareTestData();
		TemplateRootUUTResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(TemplateComponentUUTResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, new StringBuilder().append("/tplroots/unitundertest/").append(getTestDataValue(TemplateRootUUTResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/tplcomps").toString());
		putTestDataValue(TESTDATA_ENTITY_NAME, "testTplCompUUT");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateComponent");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		json.add(ResourceConstants.ENTITYATTRIBUTE_CATALOGCOMPONENT_ID, new JsonPrimitive(getTestDataValue(CatalogComponentUUTResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(TemplateRootUUTResourceIntegrationTest.class);
		TemplateRootUUTResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogComponentUUTResourceIntegrationTest.class);
		CatalogComponentUUTResourceIntegrationTest.deleteEntity();
	}
}
