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
import org.junit.BeforeClass;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Test class for TemplateRootResource for UnitUnderTest {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class TemplateRootUUTResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing TemplateRootUUTResourceIntegrationTest");

		// set up test data
		setContextClass(TemplateRootUUTResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, "/tplroots/unitundertest");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testTplRootpUUT");
		putTestDataValue(TESTDATA_ENTITY_TYPE, "TemplateRoot");

		JsonObject json = new JsonObject();
		json.add(ResourceConstants.ENTITYATTRIBUTE_NAME, new JsonPrimitive(getTestDataValue(TESTDATA_ENTITY_NAME)));
		putTestDataValue(TESTDATA_CREATE_JSON_BODY, json.toString());

		json = new JsonObject();
		JsonObject enumerationJson = new JsonObject();
		enumerationJson.add("Enumeration", new JsonPrimitive("VersionState"));
		enumerationJson.add("EnumerationValue", new JsonPrimitive("VALID"));
		json.add("ValidFlag", enumerationJson);
		putTestDataValue(TESTDATA_UPDATE_JSON_BODY, json.toString());
	}
}
