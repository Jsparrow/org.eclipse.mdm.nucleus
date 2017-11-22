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

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.NoSuchElementException;

import org.junit.Assume;
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
	protected final static String TESTDATA_RESOURCE_URI = "resourceURI";
	protected final static String TESTDATA_RANDOM_DATA = "RANDOM_DATA";

	private static final String RANDOM_ENTITY_NAME_SUFFIX = "_" + Long.toHexString(System.currentTimeMillis());

	public enum TestType {
		CREATE, FIND, FINDALL, UPDATE, DELETE;
	}

	private static Map<Class<?>, Set<TestType>> testsToSkip = HashMap.empty();

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
			LOGGER.debug(getContextClass().getSimpleName() + ".create() aborted as entity "
					+ getTestDataValue(TESTDATA_ENTITY_NAME) + " of type " + getTestDataValue(TESTDATA_ENTITY_TYPE)
					+ " was already created");
			return;
		}

		LOGGER.debug(getContextClass().getSimpleName() + ".create() sending POST to "
				+ getTestDataValue(TESTDATA_RESOURCE_URI));

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

		LOGGER.debug(getContextClass().getSimpleName() + " created " + response.asString());

		putTestDataValue(TESTDATA_ENTITY_ID, response.path("data.first().id"));
		putTestDataValue(TESTDATA_ENTITY_NAME, response.path("data.first().name"));
	}

	@Test
	public void test2Find() {
		// only execute if not skipped by implementing test class
		Assume.assumeFalse(testsToSkip.get(getContextClass())
				.get()
				.contains(TestType.FIND));

		String uri = getTestDataValue(TESTDATA_RESOURCE_URI) + "/" + getTestDataValue(TESTDATA_ENTITY_ID);

		LOGGER.debug(getContextClass().getSimpleName() + ".find() sending GET to " + uri);

		ExtractableResponse<Response> response = given().get(uri)
				.then()
				.log()
				.ifError()
				.contentType(ContentType.JSON)
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract();

		LOGGER.debug(getContextClass().getSimpleName() + " found " + response.asString());
	}

	/**
	 * Finds the first entity of the {@code EntityType} set in the context and put
	 * the found ID in the context for further usage
	 */
	public static void findFirst() {
		LOGGER.debug(getContextClass().getSimpleName() + ".find() sending GET to "
				+ getTestDataValue(TESTDATA_RESOURCE_URI));

		String id = given().get(getTestDataValue(TESTDATA_RESOURCE_URI))
				.then()
				.log()
				.ifError()
				.contentType(ContentType.JSON)
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract()
				.path("data.first().id");

		LOGGER.debug(getContextClass().getSimpleName() + " found " + getTestDataValue(TESTDATA_ENTITY_TYPE)
				+ " with ID " + id);

		putTestDataValue(TESTDATA_ENTITY_ID, id);
	}

	@Test
	public void test3FindAll() {
		// only execute if not skipped by implementing test class
		Assume.assumeFalse(testsToSkip.get(getContextClass())
				.get()
				.contains(TestType.FINDALL));

		LOGGER.debug(getContextClass().getSimpleName() + ".findAll() sending GET to "
				+ getTestDataValue(TESTDATA_RESOURCE_URI));

		ExtractableResponse<Response> response = given().get(getTestDataValue(TESTDATA_RESOURCE_URI))
				.then()
				.log()
				.ifError()
				.contentType(ContentType.JSON)
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract();

		LOGGER.debug(getContextClass().getSimpleName() + " found all " + response.asString());
	}

	// TODO anehmer on 2017-11-09: test findAll with filter

	@Test
	public void test4Update() {
		// only execute if not skipped by implementing test class
		Assume.assumeFalse(testsToSkip.get(getContextClass())
				.get()
				.contains(TestType.UPDATE));

		String uri = getTestDataValue(TESTDATA_RESOURCE_URI) + "/" + getTestDataValue(TESTDATA_ENTITY_ID);

		LOGGER.debug(getContextClass().getSimpleName() + ".update() sending PUT to " + uri);

		ExtractableResponse<Response> response = given().contentType(ContentType.JSON)
				// TODO anehmer on 2017-11-15: the update should use different data but as the
				// returned JSON represents
				// the entity prior update it does not make any difference as the update is
				// performed just based on identical data. We should discuss the PUT-behaviour
				// instead: return the old or updated object as returning the updated one would
				// mean to perform another get as the ODSTransaction.update() does not return
				// the updated entity
				// TODO anehmer on 2017-11-15: use Description to test update
				.body(getTestDataValue(TESTDATA_CREATE_JSON_BODY))
				.put(uri)
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
		String uri = getTestDataValue(TESTDATA_RESOURCE_URI) + "/" + getTestDataValue(TESTDATA_ENTITY_ID);

		LOGGER.debug(getContextClass().getSimpleName() + ".delete() sending DELETE to " + uri);

		ExtractableResponse<Response> response = given().delete(uri)
				.then()
				.log()
				.ifError()
				.body("data.first().name", equalTo(getTestDataValue(TESTDATA_ENTITY_NAME)))
				.body("data.first().type", equalTo(getTestDataValue(TESTDATA_ENTITY_TYPE)))
				.extract();

		LOGGER.debug(getContextClass().getSimpleName() + " deleted " + response.asString());

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
								"Key [" + key + "] not found in test data value map in context ["
										+ contextClass.getSimpleName() + "]")))
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
			if (!value.endsWith(RANDOM_ENTITY_NAME_SUFFIX)) {
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

	public static void skipTest(TestType testToSkip) {
		testsToSkip.get(getContextClass())
				.map(tests -> tests.add(testToSkip))
				.map(newTests -> testsToSkip = testsToSkip.put(getContextClass(), newTests));
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
