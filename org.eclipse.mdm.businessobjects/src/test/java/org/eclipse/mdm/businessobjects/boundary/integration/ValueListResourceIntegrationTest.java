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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import io.restassured.http.ContentType;

/**
 * Test class for ValueListResource. Tests are executed in
 * {@link FixMethodOrder(MethodSorters.NAME_ASCENDING)} as test2Find(),
 * test3FindAll(), test3Delete() and test4Update() depend on the entity created
 * by test1Create().
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ValueListResourceIntegrationTest {

	private static String entityId;

	@Test
	public void test1Create() {
		JsonObject json = new JsonObject();
		json.add("name", new JsonPrimitive("mytestvaluelist"));

		String id = given().contentType(ContentType.JSON)
				.body(json.toString())
				.post("/valuelists")
				.then()
				.contentType(ContentType.JSON)
				.and()
				.body("data.first().name", equalTo("mytestvaluelist"))
				.and()
				.body("data.first().type", equalTo("ValueList"))
				.extract()
				.path("data.first().id");

		setEntityId(id);
	}

	@Test
	public void test2Find() {
		given().get("/valuelists/" + getEntityId())
				.then()
				.contentType(ContentType.JSON)
				.body("data.first().name", equalTo("mytestvaluelist"))
				.body("data.first().type", equalTo("ValueList"));
	}

	@Test
	public void test3FindAll() {
		given().get("/valuelists")
				.then()
				.contentType(ContentType.JSON)
				.body("data.first().type", equalTo("ValueList"));
	}

	@Test
	public void test4Update() {
		JsonObject json = new JsonObject();
		json.add("name", new JsonPrimitive("myupdatedtestvaluelist"));

		given().contentType(ContentType.JSON)
				.body(json.toString())
				.put("/valuelists/" + getEntityId())
				.then()
				.contentType(ContentType.JSON)
				.body("data.first().name", equalTo("mytestvaluelist"))
				.body("data.first().type", equalTo("ValueList"));
	}

	@Test
	public void test5Delete() {
		given().delete("/valuelists/" + getEntityId())
				.then()
				.body("data.first().name", equalTo("mytestvaluelist"))
				.body("data.first().type", equalTo("ValueList"));
	}

	public static String getEntityId() {
		return entityId;
	}

	public static void setEntityId(String id) {
		entityId = id;
	}
}
