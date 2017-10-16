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

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Test class for ValueListResource
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ValueListResourceIntegrationTest {

	static RequestSpecification reqSpec;
	static String id;

	@Before
	public void prepareTest() {
		RestAssured.baseURI = "http://localhost:8080/org.eclipse.mdm.nucleus";
		RestAssured.basePath = "/mdm/environments/PODS";
		reqSpec = given().auth()
				.preemptive()
				.basic("sa", "sa");
	}

	@Test
	public void test1Create() {
		JsonObject o = new JsonObject();
		o.add("name", new JsonPrimitive("mytestvaluelist"));

		id = reqSpec.contentType(ContentType.JSON)
				.body(o.toString())
				.post("/valuelists")
				.then()
				.contentType(ContentType.JSON)
				.and()
				.body("data.first().name", equalTo("mytestvaluelist"))
				.and()
				.body("data.first().type", equalTo("ValueList"))
				.extract()
				.path("data.first().id");
	}

	@Test
	public void test2Find() {
		reqSpec.get("/valuelists/" + id)
				.then()
				.body("data.first().name", equalTo("mytestvaluelist"))
				.body("data.first().type", equalTo("ValueList"));
	}

	@Test
	public void test3FindAll() {
		reqSpec.get("/valuelists")
				.then()
				// TODO what else to check?
				.contentType(ContentType.JSON);
	}

	@Test
	public void test4Delete() {
		reqSpec.delete("/valuelists/" + id)
				.then()
				.body("data.first().name", equalTo("mytestvaluelist"))
				.body("data.first().type", equalTo("ValueList"));
	}

	@Test
	public void test5Update() {
	}
}
