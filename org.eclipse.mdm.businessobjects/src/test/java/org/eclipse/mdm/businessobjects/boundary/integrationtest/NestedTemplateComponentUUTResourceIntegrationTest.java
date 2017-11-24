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
 * Test class for NestedTemplateComponentResource for UnitUnderTest
 * {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class NestedTemplateComponentUUTResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing NestedTemplateComponentUUTResourceIntegrationTest");

		// prepare test data for creating the CatalogComponent
		CatalogComponentUUTResourceIntegrationTest.prepareTestData();
		CatalogComponentUUTResourceIntegrationTest.createEntity();

		// prepare test data for creating the TemplateRoot
		TemplateComponentUUTResourceIntegrationTest.prepareTestData();
		TemplateComponentUUTResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(NestedTemplateComponentUUTResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, "/tplroots/unitundertest/" + getTestDataValue(TemplateRootUUTResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/tplcomps/" + getTestDataValue(TemplateComponentUUTResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/tplcomps");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testNestedTplCompUUT");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateComponent");

		JsonObject json = new JsonObject();
		json.add("name", new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		json.add("catalogcomponent", new JsonPrimitive(getTestDataValue(CatalogComponentUUTResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(TemplateComponentUUTResourceIntegrationTest.class);
		TemplateComponentUUTResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateRootUUTResourceIntegrationTest.class);
		TemplateRootUUTResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogComponentUUTResourceIntegrationTest.class);
		CatalogComponentUUTResourceIntegrationTest.deleteEntity();
	}
}
