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

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assume;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

/**
 * Abstract test class for Entity resources. Tests are executed in
 * {@code FixMethodOrder(MethodSorters.NAME_ASCENDING)} as {@link test1Create},
 * {@link test2Find()}, {@link test3FindAll()}, {@link test4Update()} and
 * {@link test5Delete()} depend on the entity created by test1Create().
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
// TODO anehmer on 2017-11-23: test for specific return codes
// TODO anehmer on 2017-11-24: expand tests to localization and search attribute
// information
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class EntityResourceIntegrationTest {

	private static Logger LOGGER = LoggerFactory.getLogger(EntityResourceIntegrationTest.class);

	private static final String HOST = "localhost";
	private static final String PORT = "8080";
	private static final String BASE_PATH = "org.eclipse.mdm.nucleus";
	private static final String API_PATH = "mdm";
	private static final String ENV_PATH = "environments/PODS";

	private static final String AUTH_USERNAME = "sa";
	private static final String AUTH_PASSWORD = "sa";

	protected final static String TESTDATA_ENTITY_ID = "entityId";
	protected final static String TESTDATA_ENTITY_NAME = "entityName";
	protected final static String TESTDATA_ENTITY_TYPE = "entityType";
	protected final static String TESTDATA_CREATE_JSON_BODY = "createJSONBody";
	protected final static String TESTDATA_UPDATE_JSON_BODY = "updateJSONBody";
	protected final static String TESTDATA_RESOURCE_URI = "resourceURI";
	protected final static String TESTDATA_RANDOM_DATA = "RANDOM_DATA";

	private static final String RANDOM_ENTITY_NAME_SUFFIX = "_" + Long.toHexString(System.currentTimeMillis());

	public enum TestType {
		CREATE, FIND, FINDALL, UPDATE, DELETE;
	}

	private static Map<Class<?>, Set<TestType>> testsToSkip = HashMap.empty();

	private static Map<Class<?>, Map<String, String>> testDataMap = HashMap.empty();

	// if this is set, the resource URI for find() is constructed with the name as
	// the PATH_PARAM
	private static Map<Class<?>, Boolean> findByName = HashMap.empty();

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

		LOGGER.debug(new StringBuilder().append("RestAssured set up to ").append(RestAssured.baseURI).append("/").append(RestAssured.basePath).toString());

		// setup authentication
		PreemptiveBasicAuthScheme authScheme = new PreemptiveBasicAuthScheme();
		authScheme.setUserName(AUTH_USERNAME);
		authScheme.setPassword(AUTH_PASSWORD);

		RestAssured.authentication = authScheme;

		LOGGER.debug(new StringBuilder().append("RestAssured authentication set to credentials [").append(AUTH_USERNAME).append("]/[").append(AUTH_PASSWORD).append("]").toString());
	}

	@Test
	public void test1Create() {
		// only execute if not skipped by implementing test class
		Assume.assumeFalse(testsToSkip.get(getContextClass())
				.get()
				.contains(TestType.CREATE));

		createEntity();
	}

	/**
	 * Static method that can be utilised by tests to create a specific entity or is
	 * called indirectly by JUnit
	 */
	public static void createEntity() {
		// do not create entity if it was already created in a currently running
		// prepareTestData() cascade
		if (isTestDataValuePresent(TESTDATA_ENTITY_ID)) {
			LOGGER.debug(new StringBuilder().append(getContextClass().getSimpleName()).append(".create() aborted as entity ").append(getTestDataValue(TESTDATA_ENTITY_NAME)).append(" of type ").append(getTestDataValue(TESTDATA_ENTITY_TYPE)).append(" was already created")
					.toString());
			return;
		}

		LOGGER.debug(new StringBuilder().append(getContextClass().getSimpleName()).append(".create() sending POST to ").append(getTestDataValue(TESTDATA_RESOURCE_URI)).append(" with: ").append(getTestDataValue(TESTDATA_CREATE_JSON_BODY)).toString());

		ExtractableResponse<io.restassured.response.Response> response = given().contentType(ContentType.JSON)
				.body(getTestDataValue(TESTDATA_CREATE_JSON_BODY))
				.post(getTestDataValue(TESTDATA_RESOURCE_URI))
				.then()
				.log()
				.ifError()
				.contentType(ContentType.JSON)
				// do not check for name equality as that might be created randomly
				.and()
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract();

		LOGGER.debug(new StringBuilder().append(getContextClass().getSimpleName()).append(" created ").append(response.asString()).toString());

		putTestDataValue(TESTDATA_ENTITY_ID, response.path("data.first().id"));
		putTestDataValue(TESTDATA_ENTITY_NAME, response.path("data.first().name"));
	}

	@Test
	public void test2Find() {
		// only execute if not skipped by implementing test class
		Assume.assumeFalse(testsToSkip.get(getContextClass())
				.get()
				.contains(TestType.FIND));

		String uri = new StringBuilder().append(getTestDataValue(TESTDATA_RESOURCE_URI)).append("/").append(EntityResourceIntegrationTest.findByName.getOrElse(getContextClass(), false)
				? getTestDataValue(TESTDATA_ENTITY_NAME)
				: getTestDataValue(TESTDATA_ENTITY_ID)).toString();

		LOGGER.debug(new StringBuilder().append(getContextClass().getSimpleName()).append(".find() sending GET to ").append(uri).toString());

		ExtractableResponse<Response> response = given().get(uri)
				.then()
				.log()
				.ifError()
				.contentType(ContentType.JSON)
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract();

		LOGGER.debug(new StringBuilder().append(getContextClass().getSimpleName()).append(" found ").append(response.asString()).toString());
	}

	/**
	 * Finds the first entity of the {@code EntityType} set in the context and put
	 * the found ID in the context for further usage
	 */
	public static void findFirst() {
		LOGGER.debug(new StringBuilder().append(getContextClass().getSimpleName()).append(".find() sending GET to ").append(getTestDataValue(TESTDATA_RESOURCE_URI)).toString());

		String id = given().get(getTestDataValue(TESTDATA_RESOURCE_URI))
				.then()
				.log()
				.ifError()
				.contentType(ContentType.JSON)
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract()
				.path("data.first().id");

		LOGGER.debug(new StringBuilder().append(getContextClass().getSimpleName()).append(" found ").append(getTestDataValue(TESTDATA_ENTITY_TYPE)).append(" with ID ").append(id).toString());

		putTestDataValue(TESTDATA_ENTITY_ID, id);
	}

	@Test
	public void test3FindAll() {
		// only execute if not skipped by implementing test class
		Assume.assumeFalse(testsToSkip.get(getContextClass())
				.get()
				.contains(TestType.FINDALL));

		LOGGER.debug(new StringBuilder().append(getContextClass().getSimpleName()).append(".findAll() sending GET to ").append(getTestDataValue(TESTDATA_RESOURCE_URI)).toString());

		ExtractableResponse<Response> response = given().get(getTestDataValue(TESTDATA_RESOURCE_URI))
				.then()
				.log()
				.ifError()
				.contentType(ContentType.JSON)
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract();

		LOGGER.debug(new StringBuilder().append(getContextClass().getSimpleName()).append(" found all ").append(response.asString()).toString());
	}

	// TODO anehmer on 2017-11-09: test findAll with filter

	// TODO anehmer on 2018-02-06: update of relations are not checked as the
	// returned Json does not include relations
	@Test
	public void test4Update() {
		// only execute if not skipped by implementing test class
		Assume.assumeFalse(testsToSkip.get(getContextClass())
				.get()
				.contains(TestType.UPDATE));

		JsonObject json;
		// if no UPDATE_JSON_BODY is defined in implementing test, just run the MimeType
		// update
		if (!isTestDataValuePresent(TESTDATA_UPDATE_JSON_BODY)) {
			json = new JsonObject();
		}
		// or add it to the existing update
		else {
			json = new JsonParser().parse(getTestDataValue(TESTDATA_UPDATE_JSON_BODY))
					.getAsJsonObject();
		}
		json.add("MimeType", new JsonPrimitive("updatedMimeType"));
		putTestDataValue(TESTDATA_UPDATE_JSON_BODY, json.toString());

		String uri = new StringBuilder().append(getTestDataValue(TESTDATA_RESOURCE_URI)).append("/").append(getTestDataValue(TESTDATA_ENTITY_ID)).toString();

		LOGGER.debug(new StringBuilder().append(getContextClass().getSimpleName()).append(".update() sending PUT to ").append(uri).append(" with: ").append(getTestDataValue(TESTDATA_UPDATE_JSON_BODY)).toString());

		ExtractableResponse<Response> response = given().contentType(ContentType.JSON)
				// TODO anehmer on 2017-11-15: the update should use different data but as the
				// returned JSON represents
				// the entity prior update it does not make any difference as the update is
				// performed just based on identical data. We should discuss the PUT-behaviour
				// instead: return the old or updated object as returning the updated one would
				// mean to perform another get as the ODSTransaction.update() does not return
				// the updated entity
				// TODO anehmer on 2017-11-15: use Description to test update
				.body(getTestDataValue(TESTDATA_UPDATE_JSON_BODY))
				.put(uri)
				.then()
				.log()
				.ifError()
				.contentType(ContentType.JSON)
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.body("data.first().attributes.find {it.name == 'MimeType'}.value", equalTo("updatedMimeType"))
				.extract();

		LOGGER.debug(new StringBuilder().append(getContextClass().getSimpleName()).append(" updated ").append(response.asString()).toString());
	}

	@Test
	public void test5Delete() {
		// only execute if not skipped by implementing test class
		Assume.assumeFalse(testsToSkip.get(getContextClass())
				.get()
				.contains(TestType.DELETE));

		deleteEntity();
	}

	/**
	 * Static method that can be utilised by tests to delete a specific entity or is
	 * called indirectly by JUnit
	 */
	public static void deleteEntity() {
		String uri = new StringBuilder().append(getTestDataValue(TESTDATA_RESOURCE_URI)).append("/").append(getTestDataValue(TESTDATA_ENTITY_ID)).toString();

		LOGGER.debug(new StringBuilder().append(getContextClass().getSimpleName()).append(".delete() sending DELETE to ").append(uri).toString());

		ExtractableResponse<Response> response = given().delete(uri)
				.then()
				.log()
				.ifError()
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract();

		LOGGER.debug(new StringBuilder().append(getContextClass().getSimpleName()).append(" deleted ").append(response.asString()).toString());

		removeTestDataValue(TESTDATA_ENTITY_ID);
	}

	/**
	 * Gets value with key from the testDataMap. The value map is thereby
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
	 * Gets value with key from the testDataMap using the context specified by
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
								new StringBuilder().append("Key [").append(key).append("] not found in test data value map in context [").append(contextClass.getSimpleName()).append("]")
										.toString())))
				.get();
	}

	/**
	 * Checks if a test data value is present for the given key
	 * 
	 * @param key
	 *            key to check presence of test data value for
	 * @return true, if a test data value for the given key exists, false if not
	 */
	public static boolean isTestDataValuePresent(String key) {
		return testDataMap.get(getContextClass())
				.map(valueMap -> valueMap.get(key)
						.isDefined())
				.get();
	}

	/**
	 * Removes the test data value for the given key. If the key is not present,
	 * nothing happens.
	 * 
	 * @param key
	 *            key to remove test data value for
	 */
	public static void removeTestDataValue(String key) {
		testDataMap.get(getContextClass())
				.map(valueMap -> valueMap.remove(key))
				.map(newValueMap -> testDataMap = testDataMap.put(getContextClass(), newValueMap));
	}

	/**
	 * Puts value with key in the testDataMap. The value map is thereby
	 * automatically identified by the implementing class.
	 * 
	 * @param key
	 *            key to store value under
	 * @param value
	 *            value to store
	 */
	public static void putTestDataValue(String key, String value) {
		Map<String, String> entityTestData = testDataMap.getOrElse(getContextClass(), HashMap.empty());

		// randomize name to allow failure runs not to require to reset the
		// database in case the name of the entity must be unique
		// do not append suffix if name is randomly generated
		if (key.equals(TESTDATA_ENTITY_NAME) && !value.equals(TESTDATA_RANDOM_DATA)
				&& (entityTestData.get(TESTDATA_ENTITY_NAME)
						.isEmpty()
						|| !entityTestData.get(TESTDATA_ENTITY_NAME)
								.get()
								.equals(TESTDATA_RANDOM_DATA))) {
			// append suffix if it was not already appended or an already suffixed value was
			// used for a new one (e.g: TplAttr.name and CatAttr.name)
			if (!StringUtils.endsWith(value, RANDOM_ENTITY_NAME_SUFFIX)) {
				value = value + RANDOM_ENTITY_NAME_SUFFIX;
			}
		}

		entityTestData = entityTestData.put(key, value);

		testDataMap = testDataMap.put(getContextClass(), entityTestData);
	}

	/**
	 * Gets the context class set by a test implementation used to store context
	 * aware test data
	 * 
	 * @return the context class of the test implementation
	 */
	private static Class<?> getContextClass() {
		assertThat(contextClass, is(notNullValue()));
		return contextClass;
	}

	/**
	 * Sets the context class used to store context aware test data. This method
	 * must be called by any test implementation before using
	 * {@link getTestDataValue} or {@link putTestDataValue}
	 * 
	 * @param contextClass
	 *            the context class set by a test implementation
	 */
	public static void setContextClass(Class<?> contextClass) {
		EntityResourceIntegrationTest.contextClass = contextClass;
		testsToSkip = testsToSkip.put(contextClass, HashSet.empty());
		LOGGER = LoggerFactory.getLogger(contextClass);
	}

	/**
	 * Set the test with the given {@link TestType} to be skipped
	 * 
	 * @param testType
	 *            the test to skip
	 */
	public static void skipTest(TestType test) {
		testsToSkip.get(getContextClass())
				.map(tests -> tests.add(test))
				.map(newTests -> testsToSkip = testsToSkip.put(getContextClass(), newTests));
	}

	/**
	 * Sets the option findByName to either true or false. If it's set to true, the
	 * URI for find() is constructed with the name rather than with the id of the
	 * entity as the PATH_PARAM
	 * 
	 * @param findByName
	 *            if find() should use the name instead of the id of the entity
	 */
	public static void setFindByName(boolean findByNameValue) {
		findByName = findByName.put(getContextClass(), findByNameValue);
	}

	/**
	 * Gets the logger
	 * 
	 * @return logger configured to current context class
	 */
	public static Logger getLogger() {
		return LOGGER;
	}
}
