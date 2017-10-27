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

import java.util.NoSuchElementException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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

	protected static Logger LOGGER = LoggerFactory.getLogger(EntityResourceIntegrationTest.class);

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

	/**
	 * The context class must be set by implementing tests as the context to get
	 * from and put test data values to
	 */
	private static Class<?> contextClass;

	/**
	 * Init RestAssured
	 */
	static {
		// configure URI
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
		LOGGER.debug("RestAssured set up to " + RestAssured.baseURI + "/" + RestAssured.basePath);

		// setup authentication
		PreemptiveBasicAuthScheme authScheme = new PreemptiveBasicAuthScheme();
		authScheme.setUserName(AUTH_USERNAME);
		authScheme.setPassword(AUTH_PASSWORD);

		RestAssured.authentication = authScheme;

		LOGGER.debug("RestAssured authentication set to credentials [" + AUTH_USERNAME + "]/[" + AUTH_PASSWORD + "]");
	}

	@Test
	public void test1Create() {
		createEntity();
	}

	/**
	 * Static method that can be utilised by tests to create a specific entity or is
	 * called indirectly by JUnit
	 */
	public static void createEntity() {
		ExtractableResponse<io.restassured.response.Response> response = given().contentType(ContentType.JSON)
				.body(getTestDataValue(TESTDATA_CREATE_JSON_BODY))
				.post(getTestDataValue(TESTDATA_RESOURCE_URI))
				.then()
				.log()
				.ifError()
				.contentType(ContentType.JSON)
				.and()
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.and()
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract();

		LOGGER.debug(getContextClass().getSimpleName() + " created " + response.asString());

		putTestDataValue(TESTDATA_ENTITY_ID, response.path("data.first().id"));
	}

	@Test
	public void test2Find() {
		ExtractableResponse<Response> response = given()
				.get(getTestDataValue(TESTDATA_RESOURCE_URI) + "/" + getTestDataValue(TESTDATA_ENTITY_ID))
				.then()
				.log()
				.ifError()
				.contentType(ContentType.JSON)
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract();

		LOGGER.debug(getContextClass().getSimpleName() + " found " + response.asString());
	}

	@Test
	public void test3FindAll() {
		ExtractableResponse<Response> response = given().get(getTestDataValue(TESTDATA_RESOURCE_URI))
				.then()
				.log()
				.ifError()
				.contentType(ContentType.JSON)
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract();

		LOGGER.debug(getContextClass().getSimpleName() + " found all " + response.asString());
	}

	@Test
	public void test4Update() {
		ExtractableResponse<Response> response = given().contentType(ContentType.JSON)
				// TODO the update should use different data but as the returned JSON represents
				// the entity prior update it does not make any difference as the update is
				// performed just based on identical data. We should discuss the PUT-behaviour
				// instead: return the old or updated object as returning the updated one would
				// mean to perform another get as the ODSTransaction.update() does not return
				// the updated entity
				.body(getTestDataValue(TESTDATA_CREATE_JSON_BODY))
				.put(getTestDataValue(TESTDATA_RESOURCE_URI) + "/" + getTestDataValue(TESTDATA_ENTITY_ID))
				.then()
				.log()
				.ifError()
				.contentType(ContentType.JSON)
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract();

		LOGGER.debug(getContextClass().getSimpleName() + " updated " + response.asString());
	}

	@Test
	public void test5Delete() {
		deleteEntity();
	}

	/**
	 * Static method that can be utilised by tests to delete a specific entity or is
	 * called indirectly by JUnit
	 */
	public static void deleteEntity() {
		ExtractableResponse<Response> response = given()
				.delete(getTestDataValue(TESTDATA_RESOURCE_URI) + "/" + getTestDataValue(TESTDATA_ENTITY_ID))
				.then()
				.log()
				.ifError()
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract();

		LOGGER.debug(getContextClass().getSimpleName() + " deleted " + response.asString());
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
		return getTestDataValue(getContextClass(), key);
	}

	/**
	 * Get value with key from the testDataMap using the context specified by
	 * contextClass
	 * 
	 * @param contextClass
	 *            the class of the test implementation
	 * @param key
	 *            key to get value for
	 * @return value for given key
	 */
	public static String getTestDataValue(Class<?> contextClass, String key) {
		return testDataMap.get(contextClass)
				.map(valueMap -> valueMap.get(key)
						.getOrElseThrow(() -> new NoSuchElementException(
								"Key [" + key + "] not found in test data value map in context ["
										+ contextClass.getSimpleName() + "]")))
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

	/**
	 * Get the context class set by a test implementation used to store context
	 * aware test data
	 * 
	 * @return the context class of the test implementation
	 */
	private static Class<?> getContextClass() {
		assertThat(contextClass, is(notNullValue()));
		return contextClass;
	}

	/**
	 * Set the context class used to store context aware test data. This method must
	 * be called by any test implementation before using {@link getTestDataValue} or
	 * {@link putTestDataValue}
	 * 
	 * @param contextClass
	 *            the context class set by a test implementation
	 */
	public static void setContextClass(Class<?> contextClass) {
		EntityResourceIntegrationTest.contextClass = contextClass;
		LOGGER = LoggerFactory.getLogger(contextClass);
	}
}
