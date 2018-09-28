/*******************************************************************************
  * Copyright (c) 2018 Peak Solution GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  *******************************************************************************/
package org.eclipse.mdm.shoppingbasket.boundary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.assertj.core.groups.Tuple;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.shoppingbasket.entity.MDMItem;
import org.eclipse.mdm.shoppingbasket.entity.ShoppingBasket;
import org.eclipse.mdm.shoppingbasket.entity.ShoppingBasketRequest;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;

public class ShoppingBasketResourceTest extends JerseyTest {

	private static ApplicationContext context = Mockito.mock(ApplicationContext.class);
	private static EntityManager em = Mockito.mock(EntityManager.class);
	private static ConnectorService connectorService = Mockito.mock(ConnectorService.class);

	private static TestStep testStep = Mockito.mock(TestStep.class);
	private static Measurement measurement = Mockito.mock(Measurement.class);

	public static class ConnectorServiceFactory implements Factory<ConnectorService> {
		@Override
		public void dispose(ConnectorService connectorService) {
			// nothing to do here
		}

		@Override
		public ConnectorService provide() {
			return connectorService;
		}
	}
	
	@Override
	public Application configure() {
		ResourceConfig config = new ResourceConfig();
		
		config.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(ConnectorServiceFactory.class).to(ConnectorService.class);
			}
		});

		config.register(ShoppingBasketResource.class);
		config.register(JacksonFeature.class);
		return config;
	}

	@Before
	public void init() {

		when(testStep.getSourceName()).thenReturn("MDMTEST");
		when(testStep.getTypeName()).thenReturn("TestStep");
		when(testStep.getID()).thenReturn("1");

		when(measurement.getSourceName()).thenReturn("MDMTEST");
		when(measurement.getTypeName()).thenReturn("Measurement");
		when(measurement.getID()).thenReturn("2");

		when(connectorService.getContextByName("MDMTEST")).thenReturn(context);
		when(context.getEntityManager()).thenReturn(Optional.of(em));
		when(em.load(TestStep.class, "1")).thenReturn(testStep);
		when(context.getAdapterType()).thenReturn("ods");
		when(em.getLinks(Mockito.anyList())).thenReturn(ImmutableMap.of(testStep, "servicename/asampath"));
	}

	@Test
	public void testEmptyShoppingBasketRequest() {
		ShoppingBasketRequest request = new ShoppingBasketRequest();

		ShoppingBasket basket = target("shoppingbasket").request().post(Entity.json(request), ShoppingBasket.class);
		assertThat(basket.getItems()).isEmpty();
	}

	@Test
	public void testShoppingBasket() throws DataAccessException, URISyntaxException {
		when(em.getLinks(Mockito.anyList()))
				.thenReturn(ImmutableMap.of(testStep, "servicename/asampath/testStep", measurement,
						"servicename/asampath/measurement"));

		ShoppingBasket basket = target("shoppingbasket").request()
				.post(Entity.json(getShoppingBasketRequest()), ShoppingBasket.class);

		assertThat(basket.getName()).isEqualTo("my shopping basket (<äöüß\\/>)");
		assertThat(basket.getItems())
				.extracting(i -> Tuple.tuple(i.getSource(), i.getLink(), i.getRestURI()))
				.containsExactly(
						Tuple.tuple("ods", "servicename/asampath/testStep",
								UriBuilder.fromUri(this.getBaseUri()).path("/environments/MDMTEST/teststeps/1")
										.build()),
						Tuple.tuple("ods", "servicename/asampath/measurement", UriBuilder.fromUri(this.getBaseUri())
								.path("/environments/MDMTEST/measurements/2").build()));
	}

	@Test
	public void testValidateShoppingBasketXml() {
		String xml = target("shoppingbasket").request().post(Entity.json(getShoppingBasketRequest()), String.class);
		validateAgainstXSD(xml, ShoppingBasketResourceTest.class.getResourceAsStream("/shoppingbasket1.0.xsd"));
		System.out.println(xml);
	}

	private ShoppingBasketRequest getShoppingBasketRequest() {
		MDMItem item1 = new MDMItem();
		item1.setSource("MDMTEST");
		item1.setType("TestStep");
		item1.setId("1");

		MDMItem item2 = new MDMItem();
		item2.setSource("MDMTEST");
		item2.setType("Measurement");
		item2.setId("2");

		ShoppingBasketRequest request = new ShoppingBasketRequest();
		request.setName("my shopping basket (<äöüß\\/>)");
		request.setItems(Arrays.asList(item1, item2));
		return request;
	}

	private void validateAgainstXSD(String xml, InputStream xsd) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new StreamSource(xsd));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new StringReader(xml)));
		} catch (Exception ex) {
			fail("XML could not be validated: " + xml, ex);
		}
	}

}
