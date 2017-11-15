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

import org.junit.BeforeClass;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Test class for UnitResource.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class UnitResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing UnitResourceIntegrationTest");

		// set up test data
		setContextClass(UnitResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, "/units");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testUnit");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "Unit");

		JsonObject json = new JsonObject();
		json.add("name",
				new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}
}
