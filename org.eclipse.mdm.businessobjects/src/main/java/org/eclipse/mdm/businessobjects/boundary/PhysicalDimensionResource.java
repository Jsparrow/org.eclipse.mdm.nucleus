/*******************************************************************************
 * Copyright (c) 2017 science + computing AG Tuebingen (ATOS SE)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Gunnar Schmidt - initial implementation
 *******************************************************************************/
package org.eclipse.mdm.businessobjects.boundary;

import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.ENTITYATTRIBUTE_NAME;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.ENTITYATTRIBUTE_PHYSICALDIMENSION_ID;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_SOURCENAME;

import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.PhysicalDimension;
import org.eclipse.mdm.businessobjects.boundary.utils.ResourceHelper;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * {@link PhysicalDimension} resource handling REST requests
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/physDimensions")
public class PhysicalDimensionResource {

	@EJB
	private EntityService entityService;

	/**
	 * Returns the found {@link PhysicalDimension}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            id of the {@link PhysicalDimension}
	 * @return the found {@link PhysicalDimension} as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, @PathParam(REQUESTPARAM_ID) String id) {
		return Try.of(() -> this.entityService.find(sourceName, PhysicalDimension.class, id))
				// TODO handle failure and respond to client appropriately. How can we deliver
				// error messages from down the callstack? Use Exceptions or some Vavr magic?
				.map(e -> new MDMEntityResponse(PhysicalDimension.class, e.get()))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				// TODO send reponse or error regarding error expressiveness
				.get();

	}

	/**
	 * Returns the (filtered) {@link PhysicalDimension}s. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link PhysicalDimension} result
	 * @return the (filtered) {@link PhysicalDimension}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@QueryParam("filter") String filter) {
		return Try.of(() -> this.entityService.findAll(sourceName, PhysicalDimension.class, filter))
				// TODO what if e is not found? Test!
				.map(e -> new MDMEntityResponse(PhysicalDimension.class, e.toJavaList()))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.get();
	}


	/**
	 * Returns the created {@link PhysicalDimension}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param body
	 *            The {@link PhysicalDimension} to create.
	 * @return The created {@link PhysicalDimension} as {@link Response}.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, String body) {
		
		// deserialize JSON into object map
		@SuppressWarnings("unchecked")
		Map<String, Object> mapping = (Map<String, Object>) Try
				.of(() -> new ObjectMapper().readValue(body, new TypeReference<Map<String, Object>>() {
				}))
				.get();
		// read name of Quantity
		Option<String> name = Try.of(() -> mapping.get(ENTITYATTRIBUTE_NAME).toString()).toOption();
		// read default unit id
		Option<String> physDimId = Try.of(() -> mapping.get(ENTITYATTRIBUTE_PHYSICALDIMENSION_ID).toString()).toOption();
		// load default unit
		Option<PhysicalDimension> physDim = Try
				.of(() -> this.entityService.find(sourceName, PhysicalDimension.class, physDimId.get()))
				.get();

		return Try
				.of(() -> this.entityService.create(PhysicalDimension.class, sourceName, name.get(), physDim.get())
						.get())
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(PhysicalDimension.class, entity),
						Status.OK))
				.get();
	}

	/**
	 * Returns the deleted {@link PhysicalDimension}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            The identifier of the {@link PhysicalDimension} to delete.
	 * @return The deleted {@link PhysicalDimension }s as {@link Response}
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String id) {
		return Try.of(() -> this.entityService.delete(sourceName, PhysicalDimension.class, id)
				.get())
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.map(result -> ServiceUtils.toResponse(new MDMEntityResponse(PhysicalDimension.class, result),
						Status.OK))
				.get();
	}

	/**
	 * Returns the search attributes for the {@link PhysicalDimension} type. Throws
	 * a {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return The {@link SearchAttribute}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchattributes")
	public Response getSearchAttributes(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName) {
		return ResourceHelper.createSearchAttributesResponse(entityService, PhysicalDimension.class, sourceName);
	}

	/**
	 * Returns a map of localization for the entity type and the attributes. Throws
	 * a {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return The I18N as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/localizations")
	public Response localize(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName) {
		return ResourceHelper.createLocalizationResponse(entityService, PhysicalDimension.class, sourceName);
	}
}