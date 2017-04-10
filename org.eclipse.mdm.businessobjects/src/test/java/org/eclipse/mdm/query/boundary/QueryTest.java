/*******************************************************************************
  * Copyright (c) 2017 Peak Solution GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Matthias Koller - initial implementation
  *******************************************************************************/
package org.eclipse.mdm.query.boundary;

import static org.eclipse.mdm.api.odsadapter.ODSEntityManagerFactory.PARAM_NAMESERVICE;
import static org.eclipse.mdm.api.odsadapter.ODSEntityManagerFactory.PARAM_PASSWORD;
import static org.eclipse.mdm.api.odsadapter.ODSEntityManagerFactory.PARAM_SERVICENAME;
import static org.eclipse.mdm.api.odsadapter.ODSEntityManagerFactory.PARAM_USER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.mdm.api.base.ConnectionException;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.api.base.query.Record;
import org.eclipse.mdm.api.base.query.Result;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.EntityFactory;
import org.eclipse.mdm.api.odsadapter.ODSEntityManagerFactory;
import org.eclipse.mdm.query.entity.Column;
import org.eclipse.mdm.query.entity.Row;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Strings;

public class QueryTest {

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
	@Ignore
	public void test() throws DataAccessException, JsonGenerationException, JsonMappingException, IOException {
		ModelManager mm = entityManager.getModelManager().get();
		
		 List<Result> result = mm.createQuery()
			.select(mm.getEntityType("Test").getAttribute("Id"))
			.select(mm.getEntityType("Test").getAttribute("Name"))
			.select(mm.getEntityType("TestStep").getAttribute("Id"))
			.select(mm.getEntityType("TestStep").getAttribute("Name"))
			.fetch();
		 
		 List<Row> rows = new ArrayList<>();
		 
		 for (Result r : result)
		 {
			 Row row = new Row();
			 for (Record record : r) {
				 for (Value value : record.getValues().values()) {
					 row.addColumn(new Column(
							 record.getEntityType().getName(),
							 value.getName(), 
							 Strings.emptyToNull(Objects.toString(value.extract())), 
							 Strings.emptyToNull(value.getUnit())));
				 }
			 }
			 rows.add(row);
		 }
		 
		 ObjectMapper mapper = new ObjectMapper();
		 mapper.enable(SerializationFeature.INDENT_OUTPUT);
		 mapper.writeValue(System.out, rows);
	}
}