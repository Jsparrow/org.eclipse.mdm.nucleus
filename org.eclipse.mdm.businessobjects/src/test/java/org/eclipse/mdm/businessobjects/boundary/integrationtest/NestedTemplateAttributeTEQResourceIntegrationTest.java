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
 * Test class for NestedTemplateAttributeResource for TestEquipment
 * {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class NestedTemplateAttributeTEQResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing NestedTemplateAttributeTEQResourceIntegrationTest");

		// prepare test data for creating the CatalogAttribute
		CatalogAttributeTEQResourceIntegrationTest.prepareTestData();
		CatalogAttributeTEQResourceIntegrationTest.createEntity();

		// prepare test data for creating the NestedTemplateComponent
		NestedTemplateComponentTEQResourceIntegrationTest.prepareTestData();
		NestedTemplateComponentTEQResourceIntegrationTest.createEntity();

		setContextClass(NestedTemplateAttributeTEQResourceIntegrationTest.class);

		// set up test data
		putTestDataValue(TESTDATA_RESOURCE_URI, new StringBuilder().append("/tplroots/testequipment/").append(getTestDataValue(TemplateRootTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/tplcomps/").append(getTestDataValue(TemplateComponentTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/tplcomps/").append(getTestDataValue(NestedTemplateComponentTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID))
				.append("/tplattrs").toString());
		putTestDataValue(TESTDATA_ENTITY_NAME, getTestDataValue(CatalogAttributeTEQResourceIntegrationTest.class, TESTDATA_ENTITY_NAME));
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateAttribute");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(CatalogAttributeTEQResourceIntegrationTest.class, TESTDATA_ENTITY_NAME)));
		json.add("catalogattribute", new JsonPrimitive(getTestDataValue(CatalogAttributeTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());

		// delete the implicitly created NestedTemplateAttribute
		NestedTemplateAttributeTEQResourceIntegrationTest.findFirst();
		NestedTemplateAttributeTEQResourceIntegrationTest.deleteEntity();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(NestedTemplateComponentTEQResourceIntegrationTest.class);
		NestedTemplateComponentTEQResourceIntegrationTest.deleteEntity();

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
