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
package org.eclipse.mdm.businessobjects.boundary.integration;

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
		//create ValueList
		// TODO tear down valueList entity
		ValueListResourceIntegrationTest.prepareTestData();
		String valueListId = new ValueListResourceIntegrationTest()
				.createEntity()
				.getTestDataValue(ValueListResourceIntegrationTest.class, TESTDATA_ENTITY_ID);
		
		setTestDataValue(ValueListValueResourceIntegrationTest.class, TESTDATA_RESOURCE_URI,
				"/valuelists/" + valueListId + "/values");
		setTestDataValue(ValueListValueResourceIntegrationTest.class, TESTDATA_ENTITY_NAME, "testValueListValue");
		setTestDataValue(ValueListValueResourceIntegrationTest.class, TESTDATA_ENTITY_TYPE, "ValueListValue");

		JsonObject json = new JsonObject();
		json.add("name",
				new JsonPrimitive(getTestDataValue(ValueListValueResourceIntegrationTest.class, TESTDATA_ENTITY_NAME)));
		setTestDataValue(ValueListValueResourceIntegrationTest.class, TESTDATA_CREATE_JSON_BODY, json.toString());
	}
}
