package org.eclipse.mdm.businessobjects.boundary.integration;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ValueListResourceIntegrationTest extends EntityResourceIntegrationTest {

	@Override
	public void initTestData() {
		setResourceURI("/valuelists");

		setEntityName("alexvl");
		setEntityType("ValueList");

		JsonObject json = new JsonObject();
		json.add("name", new JsonPrimitive(getEntityName()));
		setCreateJSONBody(json.toString());
	}
}
