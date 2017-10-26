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
 * Test class for ValueListResource.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class ValueListResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {

		setTestDataValue(ValueListResourceIntegrationTest.class, TESTDATA_RESOURCE_URI, "/valuelists");
		setTestDataValue(ValueListResourceIntegrationTest.class, TESTDATA_ENTITY_NAME, "testValueList");
		setTestDataValue(ValueListResourceIntegrationTest.class, TESTDATA_ENTITY_TYPE, "ValueList");

		JsonObject json = new JsonObject();
		json.add("name",
				new JsonPrimitive(getTestDataValue(ValueListResourceIntegrationTest.class, TESTDATA_ENTITY_NAME)));
		setTestDataValue(ValueListResourceIntegrationTest.class, TESTDATA_CREATE_JSON_BODY, json.toString());
	}
}
