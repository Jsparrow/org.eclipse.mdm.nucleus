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
import org.junit.Test;

import io.restassured.specification.RequestSpecification;

/**
 * Test class for ValueListResource
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
public class ValueListResourceIntegrationTest {

	RequestSpecification reqSpec;

	@Before
	public void prepareTest() {
		reqSpec = given().auth()
				.preemptive()
				.basic("sa", "sa");
	}

	@Test
	public void testCreate() {
		reqSpec.contentType("application/json")
				.body("{\"name\":\"mytestvaluelist\"}")
				.post("/org.eclipse.mdm.nucleus/mdm/environments/PODS/valuelists")
				.then()
				.body("type", equalTo("ValueList"));
	}
}
