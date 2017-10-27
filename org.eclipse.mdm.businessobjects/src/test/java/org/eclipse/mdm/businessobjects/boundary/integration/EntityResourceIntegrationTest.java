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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.http.ContentType;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

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

	protected static String TESTDATA_ENTITY_ID = "entityId";
	protected static String TESTDATA_ENTITY_NAME = "entityName";
	protected static String TESTDATA_ENTITY_TYPE = "entityType";
	protected static String TESTDATA_CREATE_JSON_BODY = "createJSONBody";
	protected static String TESTDATA_RESOURCE_URI = "resourceURI";

	private static Map<Class<?>, Map<String, String>> testDataMap = HashMap.empty();

	private static Class<?> contextClass;

	/**
	 * Init RestAssured
	 */
	static {
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
	}

	@Test
	public void test1Create() {
		create();
	}

	public static void create() {
		String id = given().contentType(ContentType.JSON)
				.body(getTestDataValue(TESTDATA_CREATE_JSON_BODY))
				.post(getTestDataValue(TESTDATA_RESOURCE_URI))
				.then()
				.contentType(ContentType.JSON)
				.and()
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.and()
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract()
				.path("data.first().id");

		putTestDataValue(TESTDATA_ENTITY_ID, id);
	}

	@Test
	public void test2Find() {
		given().get(getTestDataValue(TESTDATA_RESOURCE_URI) + "/" + getTestDataValue(TESTDATA_ENTITY_ID))
				.then()
				.contentType(ContentType.JSON)
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)));
	}

	@Test
	public void test3FindAll() {
		given().get(getTestDataValue(TESTDATA_RESOURCE_URI))
				.then()
				.contentType(ContentType.JSON)
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)));
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
				.body(getTestDataValue(TESTDATA_CREATE_JSON_BODY))
				.put(getTestDataValue(TESTDATA_RESOURCE_URI) + "/" + getTestDataValue(TESTDATA_ENTITY_ID))
				.then()
				.contentType(ContentType.JSON)
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)));
	}

	@Test
	public void test5Delete() {
		given().delete(getTestDataValue(TESTDATA_RESOURCE_URI) + "/" + getTestDataValue(TESTDATA_ENTITY_ID))
				.then()
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)));
	}

	/**
	 * Get value with key from the testDataMap. The value map is thereby
	 * automatically identified by the implementing class.
	 * 
	 * @param key
	 *            key to get value for
	 * @return value for given key
	 */
	public static String getTestDataValue(String key) {
		// TODO what to do if key is not found?
		return testDataMap.get(getContextClass())
				.get()
				.get(key)
				.get();
	}

	/**
	 * Get value with key from the testDataMap
	 * 
	 * @param testType
	 *            the class of the test type
	 * @param key
	 *            key to get value for
	 * @return value for given key
	 */
	public static String getTestDataValue(Class<?> testType, String key) {
		// TODO what to do if key is not found?
		return testDataMap.get(testType)
				.get()
				.get(key)
				.get();
	}

	/**
	 * Put value with key in the testDataMap. The value map is thereby automatically
	 * identified by the implementing class.
	 * 
	 * @param key
	 *            key to store value under
	 * @param value
	 *            value to store
	 */
	public static void putTestDataValue(String key, String value) {
		Map<String, String> entityTestData = testDataMap.getOrElse(getContextClass(), HashMap.empty());
		entityTestData = entityTestData.put(key, value);
		testDataMap = testDataMap.put(getContextClass(), entityTestData);
	}

	private static Class<?> getContextClass() {
		assertThat(contextClass, is(notNullValue()));
		return contextClass;
	}

	public static void setContextClass(Class<?> contextClass) {
		EntityResourceIntegrationTest.contextClass = contextClass;
	}
}
