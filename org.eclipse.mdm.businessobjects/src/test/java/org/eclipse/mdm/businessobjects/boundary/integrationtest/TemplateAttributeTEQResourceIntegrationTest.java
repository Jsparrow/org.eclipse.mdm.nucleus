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
 * Test class for TemplateAttributeResource for TestEquipment
 * {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class TemplateAttributeTEQResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing TemplateAttributeTEQResourceIntegrationTest");

		// prepare test data for creating the CatalogComponent
		CatalogAttributeTEQResourceIntegrationTest.prepareTestData();
		CatalogAttributeTEQResourceIntegrationTest.createEntity();

		// prepare test data for creating the TemplateRoot
		TemplateComponentTEQResourceIntegrationTest.prepareTestData();
		TemplateComponentTEQResourceIntegrationTest.createEntity();


		setContextClass(TemplateAttributeTEQResourceIntegrationTest.class);

		// delete the implicitly created TemplateAttribute
		TemplateAttributeTEQResourceIntegrationTest.deleteEntity();

		// set up test data
		putTestDataValue(TESTDATA_RESOURCE_URI, "/tplroots/testequipment/"
				+ getTestDataValue(TemplateRootTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/tplcomps/"
				+ getTestDataValue(TemplateComponentTEQResourceIntegrationTest.class,
						TESTDATA_ENTITY_ID)
				+ "/tplattrs");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testTplAttrTEQ");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateAttribute");

		JsonObject json = new JsonObject();
		json.add("name", new JsonPrimitive(
				getTestDataValue(CatalogAttributeTEQResourceIntegrationTest.class, TESTDATA_ENTITY_NAME)));
		json.add("catalogattribute", new JsonPrimitive(
				getTestDataValue(CatalogAttributeTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(TemplateComponentTEQResourceIntegrationTest.class);
		TemplateComponentTEQResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateRootTEQResourceIntegrationTest.class);
		TemplateRootTEQResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogAttributeTEQResourceIntegrationTest.class);
		CatalogAttributeTEQResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogComponentTEQResourceIntegrationTest.class);
		CatalogComponentTEQResourceIntegrationTest.deleteEntity();
	}
}
