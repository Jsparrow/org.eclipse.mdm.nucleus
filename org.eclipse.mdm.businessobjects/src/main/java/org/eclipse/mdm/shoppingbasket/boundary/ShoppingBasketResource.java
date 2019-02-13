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

package org.eclipse.mdm.shoppingbasket.boundary;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.Pool;
import org.eclipse.mdm.api.dflt.model.Project;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.shoppingbasket.entity.BasketItem;
import org.eclipse.mdm.shoppingbasket.entity.MDMItem;
import org.eclipse.mdm.shoppingbasket.entity.ShoppingBasket;
import org.eclipse.mdm.shoppingbasket.entity.ShoppingBasketRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * {@link ShoppingBasketResource} resource
 *
 */
@Path("/shoppingbasket")
public class ShoppingBasketResource {

	private static final Logger LOG = LoggerFactory.getLogger(ShoppingBasketResource.class);

	private ConnectorService connectorService;

	@Context
	private UriInfo uriInfo;

	private static final Map<Class<? extends Entity>, String> ENTITY2FRAGMENT_URI = ImmutableMap
			.<Class<? extends Entity>, String>builder()
			.put(Project.class, "projects")
			.put(Pool.class, "pools")
			.put(Test.class, "tests")
			.put(TestStep.class, "teststeps")
			.put(Measurement.class, "measurements")
			.put(ChannelGroup.class, "channelgroups")
			.put(Channel.class, "channels")
			.build();

	private static final Map<String, Class<? extends Entity>> ENTITYNAME2CLASS = ImmutableMap
			.<String, Class<? extends Entity>>builder()
			.put("Project", Project.class)
			.put("Pool", Pool.class)
			.put("Test", Test.class)
			.put("TestStep", TestStep.class)
			.put("Measurement", Measurement.class)
			.put("ChannelGroup", ChannelGroup.class)
			.put("Channel", Channel.class)
			.build();

	/**
	 * @param connectorService
	 *            {@link ConnectorService}
	 */
	@Inject
	ShoppingBasketResource(ConnectorService connectorService) {
		this.connectorService = connectorService;
	}

	/**
	 * Returns a shopping basket XML file containing the requested MDM items
	 * 
	 * @param items
	 *            list with items the shopping basket should contain.
	 * @return a XML file the the shopping basket information.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_XML)
	public Response getShoppingbasket(ShoppingBasketRequest items) {
		try {
			return Response.ok(convertItemsToShoppingBasket(items), MediaType.APPLICATION_XML_TYPE).build();
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Converts a {@link ShoppingBasketRequest} into a {@link ShoppingBasket} by
	 * generating the link and REST URIs for the given MDM items.
	 * 
	 * @param items
	 *            MDM items to convert
	 * @return a shopping basket representing the given MDM items
	 */
	private ShoppingBasket convertItemsToShoppingBasket(ShoppingBasketRequest items) {
		List<BasketItem> basketItems = new ArrayList<>();
		Map<String, List<MDMItem>> itemsBySource = items.getItems().stream()
				.collect(Collectors.groupingBy(MDMItem::getSource));

		for (Map.Entry<String, List<MDMItem>> entry : itemsBySource.entrySet()) {
			ApplicationContext context = connectorService.getContextByName(entry.getKey());
			EntityManager em = context.getEntityManager()
					.orElseThrow(() -> new IllegalArgumentException(
							"Not connected to ApplicationContext with name " + entry.getKey()));

			List<Entity> entities = entry.getValue().stream().map(i -> getEntity(em, i)).collect(Collectors.toList());

			em.getLinks(entities).entrySet().forEach(e -> {

				BasketItem basketItem = new BasketItem();
				basketItem.setSource(context.getAdapterType());
				basketItem.setLink(e.getValue());
				basketItem.setRestURI(getURIForEntity(e.getKey()));

				basketItems.add(basketItem);
			});
		}

		ShoppingBasket basket = new ShoppingBasket();
		basket.setName(items.getName());
		basket.setItems(basketItems);
		return basket;
	}

	/**
	 * Generate the REST URI for a Entity
	 * 
	 * @param entity
	 *            Entity
	 * @return the REST URI of the given Entity
	 */
	private URI getURIForEntity(Entity entity) {
		return uriInfo.getBaseUriBuilder()
				.path("environments")
				.path(entity.getSourceName())
				.path(getURIFragmentForEntity(entity))
				.path(entity.getID())
				.build();
	}

	/**
	 * Returns the URI fragment of a entity. For example, if entity is an instance
	 * of {@link TestStep} the string "teststeps" is returned.
	 * 
	 * @param entity
	 * @return the URI fragment of the Entity's EntityType
	 */
	private String getURIFragmentForEntity(Entity entity) {

		for (Map.Entry<Class<? extends Entity>, String> entry : ENTITY2FRAGMENT_URI.entrySet()) {
			if (entry.getKey().isInstance(entity)) {
				return entry.getValue();
			}
		}
		// fallback to generic fragment
		return StringUtils.lowerCase(entity.getClass().getSimpleName()) + "s";
	}

	/**
	 * Loads the entity specified by the given MDM item.
	 * 
	 * @param em
	 *            {@link EntityManager} used to load the entity.
	 * @param item
	 *            MDM item to load based on its type and id.
	 * @return the loaded Entity
	 */
	private Entity getEntity(EntityManager em, MDMItem item) {
		for (Map.Entry<String, Class<? extends Entity>> entry : ENTITYNAME2CLASS.entrySet()) {
			if (item.getType().equals(entry.getKey())) {
				return em.load(entry.getValue(), item.getId());
			}
		}
		throw new IllegalArgumentException("Cannot load type: " + item.getType());
	}
}
