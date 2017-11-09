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
import org.junit.Ignore;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Test class for NestedTemplateComponentResource for TestEquipment
 * {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
@Ignore
public class NestedTemplateComponentTEQResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing NestedTemplateComponentTEQResourceIntegrationTest");

		// prepare test data for creating the CatalogComponent
		CatalogComponentTEQResourceIntegrationTest.prepareTestData();
		CatalogComponentTEQResourceIntegrationTest.createEntity();

		// prepare test data for creating the TemplateRoot
		TemplateComponentTEQResourceIntegrationTest.prepareTestData();
		TemplateComponentTEQResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(NestedTemplateComponentTEQResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, "/tplroots/testequipment/"
				+ getTestDataValue(TemplateRootTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/tplcomps/"
				+ getTestDataValue(TemplateComponentTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)
				+ "/tplcomps");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testNestedTplCompTEQ");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateComponent");

		JsonObject json = new JsonObject();
		json.add("name", new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		json.add("catalogcomponent", new JsonPrimitive(
				getTestDataValue(CatalogComponentTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(TemplateRootTEQResourceIntegrationTest.class);
		TemplateRootTEQResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogComponentTEQResourceIntegrationTest.class);
		CatalogComponentTEQResourceIntegrationTest.deleteEntity();
	}
}
