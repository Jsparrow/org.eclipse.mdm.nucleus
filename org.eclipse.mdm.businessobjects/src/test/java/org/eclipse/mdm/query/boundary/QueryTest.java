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

package org.eclipse.mdm.query.boundary;

import static org.eclipse.mdm.api.odsadapter.ODSContextFactory.PARAM_NAMESERVICE;
import static org.eclipse.mdm.api.odsadapter.ODSContextFactory.PARAM_PASSWORD;
import static org.eclipse.mdm.api.odsadapter.ODSContextFactory.PARAM_SERVICENAME;
import static org.eclipse.mdm.api.odsadapter.ODSContextFactory.PARAM_USER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdm.api.base.ConnectionException;
import org.eclipse.mdm.api.base.ServiceNotProvidedException;
import org.eclipse.mdm.api.base.adapter.ModelManager;
import org.eclipse.mdm.api.base.query.QueryService;
import org.eclipse.mdm.api.base.query.Record;
import org.eclipse.mdm.api.base.query.Result;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.odsadapter.ODSContextFactory;
import org.eclipse.mdm.query.entity.Column;
import org.eclipse.mdm.query.entity.Row;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Strings;

public class QueryTest {

	/*
	 * ATTENTION: ==========
	 *
	 * To run this test make sure the target service is running a MDM default
	 * model and any database constraint which enforces a relation of Test to a
	 * parent entity is deactivated!
	 */

	private static final String NAME_SERVICE = "corbaloc::1.2@%s:%s/NameService";

	private static final String USER = "sa";
	private static final String PASSWORD = "sa";

	private static ApplicationContext context;
	private static ModelManager modelManager;
	private static org.eclipse.mdm.api.base.query.QueryService queryService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws ConnectionException {
		String nameServiceHost = System.getProperty("host");
		String nameServicePort = System.getProperty("port");
		String serviceName = System.getProperty("service");

		if (nameServiceHost == null || StringUtils.isEmpty(nameServiceHost)) {
			throw new IllegalArgumentException("name service host is unknown: define system property 'host'");
		}

		nameServicePort = nameServicePort == null || StringUtils.isEmpty(nameServicePort) ? String.valueOf(2809) : nameServicePort;
		if (nameServicePort == null || StringUtils.isEmpty(nameServicePort)) {
			throw new IllegalArgumentException("name service port is unknown: define system property 'port'");
		}

		if (serviceName == null || StringUtils.isEmpty(serviceName)) {
			throw new IllegalArgumentException("service name is unknown: define system property 'service'");
		}

		Map<String, String> connectionParameters = new HashMap<>();
		connectionParameters.put(PARAM_NAMESERVICE, String.format(NAME_SERVICE, nameServiceHost, nameServicePort));
		connectionParameters.put(PARAM_SERVICENAME, serviceName + ".ASAM-ODS");
		connectionParameters.put(PARAM_USER, USER);
		connectionParameters.put(PARAM_PASSWORD, PASSWORD);

		context = new ODSContextFactory().connect(connectionParameters);
		modelManager = context.getModelManager()
				.orElseThrow(() -> new ServiceNotProvidedException(ModelManager.class));
		queryService = context.getQueryService()
				.orElseThrow(() -> new ServiceNotProvidedException(QueryService.class));
	}

	@AfterClass
	public static void tearDownAfterClass() throws ConnectionException {
		if (context != null) {
			context.close();
		}
	}

	@org.junit.Test
	@Ignore
	public void test() throws IOException {
		

		List<Result> result = queryService.createQuery().select(modelManager.getEntityType("Test").getAttribute("Id"))
				.select(modelManager.getEntityType("Test").getAttribute("Name"))
				.select(modelManager.getEntityType("TestStep").getAttribute("Id"))
				.select(modelManager.getEntityType("TestStep").getAttribute("Name")).fetch();

		List<Row> rows = new ArrayList<>();

		result.forEach(r -> {
			Row row = new Row();
			for (Record record : r) {
				record.getValues().values().forEach(value -> row.addColumn(new Column(record.getEntityType().getName(), value.getName(),
						Strings.emptyToNull(Objects.toString(value.extract())), Strings.emptyToNull(value.getUnit()))));
			}
			rows.add(row);
		});

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(System.out, rows);
	}
}