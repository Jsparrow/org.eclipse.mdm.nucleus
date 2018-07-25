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
 * Test class for CatalogAttributeResource for UnitUnderTest
 * {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class CatalogAttributeUUTResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing CatalogAttributeUUTResourceIntegrationTest");

		// prepare test data for creating the CatalogComponent
		CatalogComponentUUTResourceIntegrationTest.prepareTestData();
		CatalogComponentUUTResourceIntegrationTest.createEntity();

		// prepare test data for creating the ValueList to reference
		ValueListResourceIntegrationTest.prepareTestData();
		ValueListResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(CatalogAttributeUUTResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, "/catcomps/unitundertest/" + getTestDataValue(CatalogComponentUUTResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/catattrs");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testCatAttrUUT");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "CatalogAttribute");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		json.add(ResourceConstants.ENTITYATTRIBUTE_DATATYPE, new JsonPrimitive("STRING"));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());

		json = new JsonObject();
		json.add("ValueList",
				new JsonPrimitive(getTestDataValue(ValueListResourceIntegrationTest.class, TESTDATA_ENTITY_ID)));
		putTestDataValue(TESTDATA_UPDATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(CatalogComponentUUTResourceIntegrationTest.class);
		CatalogComponentUUTResourceIntegrationTest.deleteEntity();

		setContextClass(ValueListResourceIntegrationTest.class);
		ValueListResourceIntegrationTest.deleteEntity();
	}
}
