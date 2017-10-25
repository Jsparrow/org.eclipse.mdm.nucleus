package org.eclipse.mdm.businessobjects.boundary.integration;

import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;

/**
 * Test suite for the REST API defining global parameters and tests
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ ValueListResourceIntegrationTest.class })
public class ResourceIntegrationTestSuite {

	private static final String HOST = "localhost";
	private static final String PORT = "8080";
	private static final String BASE_PATH = "org.eclipse.mdm.nucleus";
	private static final String API_PATH = "mdm";
	private static final String ENV_PATH = "environments/PODS";

	private static final String AUTH_USERNAME = "sa";
	private static final String AUTH_PASSWORD = "sa";

	@ClassRule
	public static ExternalResource connectionRule = new ExternalResource() {
		@Override
		protected void before() throws Throwable {
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
	};
}
