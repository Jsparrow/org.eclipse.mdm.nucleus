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

import org.eclipse.mdm.businessobjects.boundary.ResourceConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Test class for ValueListValueResource.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class ValueListValueResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing ValueListValueResourceIntegrationTest");

		// prepare test data for creating the ValueList
		ValueListResourceIntegrationTest.prepareTestData();
		ValueListResourceIntegrationTest.createEntity();

		// reset the context
		setContextClass(ValueListValueResourceIntegrationTest.class);

		// set up test data
		putTestDataValue(TESTDATA_RESOURCE_URI, "/valuelists/" + getTestDataValue(ValueListResourceIntegrationTest.class, TESTDATA_ENTITY_ID) + "/values");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testValueListValue");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "ValueListValue");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}

	@AfterClass
	public static void tearDownAfterClass() {
		setContextClass(ValueListResourceIntegrationTest.class);
		ValueListResourceIntegrationTest.deleteEntity();
	}
}
