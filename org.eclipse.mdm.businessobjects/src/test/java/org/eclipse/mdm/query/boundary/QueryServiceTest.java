package org.eclipse.mdm.query.boundary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.mdm.api.odsadapter.ODSEntityManagerFactory.PARAM_NAMESERVICE;
import static org.eclipse.mdm.api.odsadapter.ODSEntityManagerFactory.PARAM_PASSWORD;
import static org.eclipse.mdm.api.odsadapter.ODSEntityManagerFactory.PARAM_SERVICENAME;
import static org.eclipse.mdm.api.odsadapter.ODSEntityManagerFactory.PARAM_USER;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mdm.api.base.ConnectionException;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.EntityFactory;
import org.eclipse.mdm.api.odsadapter.ODSEntityManagerFactory;
import org.eclipse.mdm.businessobjects.control.QueryTest;
import org.eclipse.mdm.query.entity.Row;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Ignore
public class QueryServiceTest {
	/*
	 * ATTENTION:
	 * ==========
	 *
	 * To run this test make sure the target service is running a
	 * MDM default model and any database constraint which enforces
	 * a relation of Test to a parent entity is deactivated!
	 */

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryTest.class);

	private static final String NAME_SERVICE = "corbaloc::1.2@%s:%s/NameService";

	private static final String USER = "sa";
	private static final String PASSWORD = "sa";

	private static EntityManager entityManager;
	private static EntityFactory entityFactory;

	@BeforeClass
	public static void setUpBeforeClass() throws ConnectionException {
		String nameServiceHost = System.getProperty("host");
		String nameServicePort = System.getProperty("port");
		String serviceName = System.getProperty("service");

		if(nameServiceHost == null || nameServiceHost.isEmpty()) {
			throw new IllegalArgumentException("name service host is unknown: define system property 'host'");
		}

		nameServicePort = nameServicePort == null || nameServicePort.isEmpty() ? String.valueOf(2809) :  nameServicePort;
		if(nameServicePort == null || nameServicePort.isEmpty()) {
			throw new IllegalArgumentException("name service port is unknown: define system property 'port'");
		}

		if(serviceName == null || serviceName.isEmpty()) {
			throw new IllegalArgumentException("service name is unknown: define system property 'service'");
		}

		Map<String, String> connectionParameters = new HashMap<>();
		connectionParameters.put(PARAM_NAMESERVICE, String.format(NAME_SERVICE, nameServiceHost, nameServicePort));
		connectionParameters.put(PARAM_SERVICENAME, serviceName + ".ASAM-ODS");
		connectionParameters.put(PARAM_USER, USER);
		connectionParameters.put(PARAM_PASSWORD, PASSWORD);

		entityManager = new ODSEntityManagerFactory().connect(connectionParameters);
		entityFactory = entityManager.getEntityFactory()
				.orElseThrow(() -> new IllegalStateException("Entity manager factory not available."));
	}

	@AfterClass
	public static void tearDownAfterClass() throws ConnectionException {
		if(entityManager != null) {
			entityManager.close();
		}
	}

	@org.junit.Test
	public void testTest() throws DataAccessException, JsonGenerationException, JsonMappingException, IOException {
		QueryService qa = new QueryService();
		List<Row> rows = qa.queryRowsForSource(entityManager, 
				"Test", 
				Arrays.asList("Test.Id", "Test.Name", "TestStep.Id", "TestStep.Name"), 
				"TestStep.Name lk PBN*", 
				"");
		
		assertThat(rows).as("Expected 2 Tests").hasSize(2);
	}
	
	@org.junit.Test
	public void testTestStep() throws DataAccessException, JsonGenerationException, JsonMappingException, IOException {
		QueryService qa = new QueryService();
		List<Row> rows = qa.queryRowsForSource(entityManager, 
				"TestStep", 
				Arrays.asList("Test.Id", "Test.Name", "TestStep.Id", "TestStep.Name"), 
				"TestStep.Name lk PBN*", 
				"");
		
		assertThat(rows).as("Expected 8 TestSteps").hasSize(8);
	}
	
	@org.junit.Test
	public void testMeasurement() throws DataAccessException, JsonGenerationException, JsonMappingException, IOException {
		QueryService qa = new QueryService();
		List<Row> rows = qa.queryRowsForSource(entityManager, 
				"Measurement", 
				Arrays.asList("Test.Id", "Test.Name", "TestStep.Id", "TestStep.Name"), 
				"TestStep.Name lk PBN*", 
				"");
		
		assertThat(rows).as("Expected 3 Measurements").hasSize(3);
	}
	
	@org.junit.Test
	public void testVehicle() throws DataAccessException, JsonGenerationException, JsonMappingException, IOException {
		QueryService qa = new QueryService();
		List<Row> rows = qa.queryRowsForSource(entityManager, 
				"Measurement", 
				Arrays.asList("Test.Id", "Test.Name", "TestStep.Id", "TestStep.Name", "vehicle.vehicle_type"), 
				"TestStep.Name lk PBN*", 
				"");
		System.out.println(rows);
//		assertThat(rows)
//			.as("Expected 3 Measurements").hasSize(3)
	}
}
