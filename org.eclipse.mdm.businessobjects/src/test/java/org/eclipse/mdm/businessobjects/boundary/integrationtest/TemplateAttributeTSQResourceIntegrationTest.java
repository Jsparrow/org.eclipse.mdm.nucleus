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
 * Test class for TemplateAttributeResource for TestSequence
 * {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class TemplateAttributeTSQResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing TemplateAttributeTSQResourceIntegrationTest");

		// prepare test data for creating the CatalogComponent
		CatalogAttributeTSQResourceIntegrationTest.prepareTestData();
		CatalogAttributeTSQResourceIntegrationTest.createEntity();

		// prepare test data for creating the TemplateRoot
		TemplateComponentTSQResourceIntegrationTest.prepareTestData();
		TemplateComponentTSQResourceIntegrationTest.createEntity();

		setContextClass(TemplateAttributeTSQResourceIntegrationTest.class);

		// set up test data
		putTestDataValue(TESTDATA_RESOURCE_URI, "/tplroots/testsequence/"
				+ getTestDataValue(TemplateRootTSQResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/tplcomps/"
				+ getTestDataValue(TemplateComponentTSQResourceIntegrationTest.class,
						TESTDATA_ENTITY_ID)
				+ "/tplattrs");
		putTestDataValue(TESTDATA_ENTITY_NAME,
				getTestDataValue(CatalogAttributeTSQResourceIntegrationTest.class, TESTDATA_ENTITY_NAME));
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateAttribute");

		JsonObject json = new JsonObject();
		json.add("name", new JsonPrimitive(
				getTestDataValue(CatalogAttributeTSQResourceIntegrationTest.class, TESTDATA_ENTITY_NAME)));
		json.add("catalogattribute", new JsonPrimitive(
				getTestDataValue(CatalogAttributeTSQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());

		// delete the implicitly created TemplateAttribute
		TemplateAttributeTSQResourceIntegrationTest.findFirst();
		TemplateAttributeTSQResourceIntegrationTest.deleteEntity();
	}

	@AfterClass
	public static void tearDownAfterClass() {
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
