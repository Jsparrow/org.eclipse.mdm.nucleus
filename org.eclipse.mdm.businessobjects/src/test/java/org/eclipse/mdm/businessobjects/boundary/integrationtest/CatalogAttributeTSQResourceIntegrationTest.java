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
 * Test class for CatalogAttributeResource for TestSequence {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class CatalogAttributeTSQResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing CatalogAttributeTSQResourceIntegrationTest");

		// prepare test data for creating the CatalogComponent
		CatalogComponentTSQResourceIntegrationTest.prepareTestData();
		CatalogComponentTSQResourceIntegrationTest.createEntity();

		// prepare test data for creating the ValueList to reference
		ValueListResourceIntegrationTest.prepareTestData();
		ValueListResourceIntegrationTest.createEntity();

		// set up test data
		setContextClass(CatalogAttributeTSQResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, new StringBuilder().append("/catcomps/testsequence/").append(getTestDataValue(CatalogComponentTSQResourceIntegrationTest.class, TESTDATA_ENTITY_ID)).append("/catattrs").toString());
		putTestDataValue(TESTDATA_ENTITY_NAME, "testCatAttrTSQ");
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
		setContextClass(CatalogComponentTSQResourceIntegrationTest.class);
		CatalogComponentTSQResourceIntegrationTest.deleteEntity();

		setContextClass(ValueListResourceIntegrationTest.class);
		ValueListResourceIntegrationTest.deleteEntity();
	}
}
