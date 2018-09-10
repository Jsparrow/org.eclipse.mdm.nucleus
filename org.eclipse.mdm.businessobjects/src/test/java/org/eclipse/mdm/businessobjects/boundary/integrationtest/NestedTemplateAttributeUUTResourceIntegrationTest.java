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
import org.eclipse.mdm.businessobjects.boundary.ResourceConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Test class for NestedTemplateAttributeResource for UnitUnderTest
 * {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class NestedTemplateAttributeUUTResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing NestedTemplateAttributeUUTResourceIntegrationTest");

		// prepare test data for creating the CatalogAttribute
		CatalogAttributeUUTResourceIntegrationTest.prepareTestData();
		CatalogAttributeUUTResourceIntegrationTest.createEntity();

		// prepare test data for creating the NestedTemplateComponent
		NestedTemplateComponentUUTResourceIntegrationTest.prepareTestData();
		NestedTemplateComponentUUTResourceIntegrationTest.createEntity();

		setContextClass(NestedTemplateAttributeUUTResourceIntegrationTest.class);

		// set up test data
		putTestDataValue(TESTDATA_RESOURCE_URI, "/tplroots/unitundertest/" + getTestDataValue(TemplateRootUUTResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/tplcomps/" + getTestDataValue(TemplateComponentUUTResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/tplcomps/" + getTestDataValue(NestedTemplateComponentUUTResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/tplattrs");
		putTestDataValue(TESTDATA_ENTITY_NAME, getTestDataValue(CatalogAttributeUUTResourceIntegrationTest.class, TESTDATA_ENTITY_NAME));
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateAttribute");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(CatalogAttributeUUTResourceIntegrationTest.class, TESTDATA_ENTITY_NAME)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());

		// delete the implicitly created NestedTemplateAttribute
		NestedTemplateAttributeUUTResourceIntegrationTest.findFirst();
		NestedTemplateAttributeUUTResourceIntegrationTest.deleteEntity();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(NestedTemplateComponentUUTResourceIntegrationTest.class);
		NestedTemplateComponentUUTResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateComponentUUTResourceIntegrationTest.class);
		TemplateComponentUUTResourceIntegrationTest.deleteEntity();

		setContextClass(TemplateRootUUTResourceIntegrationTest.class);
		TemplateRootUUTResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogAttributeUUTResourceIntegrationTest.class);
		CatalogAttributeUUTResourceIntegrationTest.deleteEntity();

		setContextClass(CatalogComponentUUTResourceIntegrationTest.class);
		CatalogComponentUUTResourceIntegrationTest.deleteEntity();
	}
}
