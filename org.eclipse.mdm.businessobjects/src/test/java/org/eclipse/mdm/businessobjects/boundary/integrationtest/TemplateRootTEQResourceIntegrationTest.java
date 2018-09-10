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
import org.junit.BeforeClass;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Test class for TemplateRootResource for TestEquipment {@link ContextType}.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 * @see EntityResourceIntegrationTest
 *
 */
public class TemplateRootTEQResourceIntegrationTest extends EntityResourceIntegrationTest {

	@BeforeClass
	public static void prepareTestData() {
		getLogger().debug("Preparing TemplateRootTEQResourceIntegrationTest");

		// set up test data
		setContextClass(TemplateRootTEQResourceIntegrationTest.class);

		putTestDataValue(TESTDATA_RESOURCE_URI, "/tplroots/testequipment");
		putTestDataValue(TESTDATA_ENTITY_NAME, "testTplRootTEQ");
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
