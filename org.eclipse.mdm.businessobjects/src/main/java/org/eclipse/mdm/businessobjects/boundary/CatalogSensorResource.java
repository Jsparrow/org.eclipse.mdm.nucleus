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

package org.eclipse.mdm.businessobjects.boundary;

import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.ENTITYATTRIBUTE_NAME;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID2;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_SOURCENAME;
import static org.eclipse.mdm.businessobjects.service.EntityService.L;
import static org.eclipse.mdm.businessobjects.service.EntityService.SL;
import static org.eclipse.mdm.businessobjects.service.EntityService.V;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.dflt.model.CatalogComponent;
import org.eclipse.mdm.api.dflt.model.CatalogSensor;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.RequestBody;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import io.vavr.collection.List;

/**
 * {@link CatalogSensor} resource handling REST requests
 * 
 * @author Philipp Schweinbenz, science+computing AG Tuebingen (Atos SE)
 *
 */

@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/catcomps/testequipment/{" + REQUESTPARAM_ID
		+ "}/catsensors")
public class CatalogSensorResource {

	@EJB
	private EntityService entityService;

	/**
	 * Returns the found {@link CatalogSensor}.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param catCompId
	 *            id of the {@link CatalogComponent}
	 * @param id
	 *            id of the {@link CatalogSensor}
	 * @return the found {@link CatalogSensor} as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String catCompId, @PathParam(REQUESTPARAM_ID2) String id) {
		return entityService
				.find(V(sourceName), CatalogSensor.class, V(id), V(ContextType.TESTEQUIPMENT),
						SL(catCompId))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Returns the (filtered) {@link CatalogSensor}s.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param catCompId
	 *            id of the {@link CatalogComponent}
	 * @param filter
	 *            filter string to filter the {@link CatalogSensor} result
	 * @return the (filtered) {@link CatalogSensor}s as {@link Response}
	 */
	// FIXME anehmer on 2017-11-17: returns all CatalogSensors and not only the ones
	// in the superordinate CatalogComponent
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String catCompId, @QueryParam("filter") String filter) {
		return entityService.find(V(sourceName), CatalogComponent.class, V(catCompId), V(ContextType.TESTEQUIPMENT))
				.map(catComp -> List.ofAll(catComp.getCatalogSensors()))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Returns the created {@link CatalogSensor}.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param catCompId
	 *            id of the {@link CatalogComponent}
	 * @param body
	 *            The {@link CatalogSensor} to create.
	 * @return the created {@link CatalogSensor} as {@link Response}.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String catCompId, String body) {
		RequestBody requestBody = RequestBody.create(body);

		return entityService
				.create(V(sourceName), CatalogSensor.class,
						L(requestBody.getStringValueSupplier(ENTITYATTRIBUTE_NAME),
								entityService.find(V(sourceName), CatalogComponent.class, V(catCompId),
										V(ContextType.TESTEQUIPMENT))))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.CREATED))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Updates the {@link CatalogSensor} with all parameters set in the given JSON
	 * body of the request.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param catCompId
	 *            id of the {@link CatalogComponent}
	 * @param id
	 *            the identifier of the {@link CatalogSensor} to delete.
	 * @param body
	 *            the body of the request containing the attributes to update
	 * @return the updated {@link CatalogSensor}
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response update(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String catCompId, @PathParam(REQUESTPARAM_ID2) String id, String body) {
		RequestBody requestBody = RequestBody.create(body);

		return entityService
				.update(V(sourceName),
						entityService.find(V(sourceName), CatalogSensor.class, V(id), V(ContextType.TESTEQUIPMENT),
								SL(catCompId)),
						requestBody.getValueMapSupplier())
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Deletes and returns the deleted {@link CatalogSensor}.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param catCompId
	 *            id of the {@link CatalogComponent}
	 * @param id
	 *            The identifier of the {@link CatalogSensor} to delete.
	 * @return the deleted {@link CatalogSensor }s as {@link Response}
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String catCompId, @PathParam(REQUESTPARAM_ID2) String id) {
		return entityService
				.delete(V(sourceName),
						entityService.find(V(sourceName), CatalogSensor.class, V(id), V(ContextType.TESTEQUIPMENT),
								SL(catCompId)))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Returns the search attributes for the {@link CatalogSensor} type.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the {@link SearchAttribute}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchattributes")
	public Response getSearchAttributes(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName) {
		return ServiceUtils.buildSearchAttributesResponse(V(sourceName), CatalogSensor.class, entityService);
	}

	/**
	 * Returns a map of localization for the entity type and the attributes.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the I18N as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/localizations")
	public Response localize(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName) {
		return ServiceUtils.buildLocalizationResponse(V(sourceName), CatalogSensor.class, entityService);
	}
}