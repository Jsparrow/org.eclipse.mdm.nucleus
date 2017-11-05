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
 * Test class for CatalogAttributeResource for TestEquipment
 * {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class CatalogAttributeTEQResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing CatalogAttributeTEQResourceIntegrationTest");

		// prepare test data for creating the CatalogComponent
		CatalogComponentTEQResourceIntegrationTest.prepareTestData();
		CatalogComponentTEQResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(CatalogAttributeTEQResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, "/catcomps/testequipment/"
				+ getTestDataValue(CatalogComponentTEQResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/catattrs");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testCatAttrTEQ");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "CatalogAttribute");

		JsonObject json = new JsonObject();
		json.add("name", new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		json.add("datatype", new JsonPrimitive("STRING"));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(CatalogComponentTEQResourceIntegrationTest.class);
		CatalogComponentTEQResourceIntegrationTest.deleteEntity();
	}
}
