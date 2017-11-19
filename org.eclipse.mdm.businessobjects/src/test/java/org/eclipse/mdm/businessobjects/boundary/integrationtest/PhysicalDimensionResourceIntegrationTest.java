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
 * Test class for PhysicalDimensionResource.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class PhysicalDimensionResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing PhysicalDimensionResourceIntegrationTest");

		// set up test data
		setContextClass(PhysicalDimensionResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, "/physicaldimensions");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testPhysicalDimension");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "PhysicalDimension");

		JsonObject json = new JsonObject();
		json.add("name",
				new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());
	}
}
