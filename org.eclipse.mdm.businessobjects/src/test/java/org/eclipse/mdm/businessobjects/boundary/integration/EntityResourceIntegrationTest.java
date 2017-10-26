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

import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.http.ContentType;

/**
 * Abstract test class for Entity resources. Tests are executed in
 * {@link FixMethodOrder(MethodSorters.NAME_ASCENDING)} as test2Find(),
 * test3FindAll(), test3Delete() and test4Update() depend on the entity created
 * by test1Create().
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class EntityResourceIntegrationTest {

	private static final String HOST = "localhost";
	private static final String PORT = "8080";
	private static final String BASE_PATH = "org.eclipse.mdm.nucleus";
	private static final String API_PATH = "mdm";
	private static final String ENV_PATH = "environments/PODS";

	private static final String AUTH_USERNAME = "sa";
	private static final String AUTH_PASSWORD = "sa";

	private static String entityId;
	private static String entityName;
	private static String entityType;
	private static String createJSONBody;
	private static String resourceURI;

	@Before
	// TODO that should happen as @BeforeClass but initTestData cannot be static to
	// be referenced by this static context
	public void before() throws Throwable {
		StringBuilder baseURI = new StringBuilder();
		baseURI.append("http://")
				.append(HOST)
				.append(":")
				.append(PORT)
				.append("/")
				.append(BASE_PATH)
				.append("/")
				.append(API_PATH);
		RestAssured.baseURI = baseURI.toString();
		RestAssured.basePath = ENV_PATH;

		PreemptiveBasicAuthScheme authScheme = new PreemptiveBasicAuthScheme();
		authScheme.setUserName(AUTH_USERNAME);
		authScheme.setPassword(AUTH_PASSWORD);

		RestAssured.authentication = authScheme;

		initTestData();
	}

	public abstract void initTestData();

	@Test
	public void test1Create() {
		String id = given().contentType(ContentType.JSON)
				.body(getCreateJSONBody())
				.post(getResourceURI())
				.then()
				.contentType(ContentType.JSON)
				.and()
				.body("data.first().name", equalTo(getEntityName()))
				.and()
				.body("data.first().type", equalTo(getEntityType()))
				.extract()
				.path("data.first().id");

		setEntityId(id);
	}

	@Test
	public void test2Find() {
		given().get(getResourceURI() + "/" + getEntityId())
				.then()
				.contentType(ContentType.JSON)
				.body("data.first().name", equalTo(getEntityName()))
				.body("data.first().type", equalTo(getEntityType()));
	}

	@Test
	public void test3FindAll() {
		given().get(getResourceURI())
				.then()
				.contentType(ContentType.JSON)
				.body("data.first().type", equalTo(getEntityType()));
	}

	@Test
	public void test4Update() {
		given().contentType(ContentType.JSON)
				// TODO the update should use different data but as the returned JSON represents
				// the entity prior update it does not make any difference as the update is
				// performed just based on identical data. We should discuss the PUT-behaviour
				// instead: return the old or updated object as returning the updated one would
				// mean to perform another get as the ODSTransaction.update() does not return
				// the updated entity
				.body(getCreateJSONBody())
				.put(getResourceURI() + "/" + getEntityId())
				.then()
				.contentType(ContentType.JSON)
				.body("data.first().name", equalTo(getEntityName()))
				.body("data.first().type", equalTo(getEntityType()));
	}

	@Test
	public void test5Delete() {
		given().delete(getResourceURI() + "/" + getEntityId())
				.then()
				.body("data.first().name", equalTo(getEntityName()))
				.body("data.first().type", equalTo(getEntityType()));
	}

	public static String getEntityId() {
		return entityId;
	}

	public static void setEntityId(String id) {
		entityId = id;
	}

	public static String getCreateJSONBody() {
		return createJSONBody;
	}

	public static void setCreateJSONBody(String createJSONBody) {
		EntityResourceIntegrationTest.createJSONBody = createJSONBody;
	}

	public static String getResourceURI() {
		return resourceURI;
	}

	public static void setResourceURI(String resourceURI) {
		EntityResourceIntegrationTest.resourceURI = resourceURI;
	}

	public static String getEntityName() {
		return entityName;
	}

	public static void setEntityName(String entityName) {
		EntityResourceIntegrationTest.entityName = entityName;
	}

	public static String getEntityType() {
		return entityType;
	}

	public static void setEntityType(String entityType) {
		EntityResourceIntegrationTest.entityType = entityType;
	}
}
