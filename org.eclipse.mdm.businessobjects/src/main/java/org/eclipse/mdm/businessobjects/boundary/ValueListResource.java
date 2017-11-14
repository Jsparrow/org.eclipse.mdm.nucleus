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
package org.eclipse.mdm.businessobjects.boundary;

import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.ENTITYATTRIBUTE_NAME;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_SOURCENAME;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.dflt.model.ValueList;
import org.eclipse.mdm.api.dflt.model.ValueListValue;
import org.eclipse.mdm.businessobjects.boundary.utils.ResourceHelper;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import io.vavr.control.Try;

/**
 * {@link ValueList} resource handling REST requests
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/valuelists")
public class ValueListResource {

	@EJB
	private EntityService entityService;

	/**
	 * Returns the found {@link ValueList}. Throws a {@link WebApplicationException}
	 * on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            id of the {@link ValueList}
	 * @return the found {@link ValueList} as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, @PathParam(REQUESTPARAM_ID) String id) {
		return Try.of(() -> this.entityService.find(sourceName, ValueList.class, id))
				// TODO handle failure and respond to client appropriately. How can we deliver
				// error messages from down the callstack? Use Exceptions or some Vavr magic?
				.map(e -> new MDMEntityResponse(ValueList.class, e.get()))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				// TODO send reponse or error regarding error expressiveness
				.get();

	}

	/**
	 * Returns the (filtered) {@link ValueList}s. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link ValueList} result
	 * @return the (filtered) {@link ValueList}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@QueryParam("filter") String filter) {
		return Try.of(() -> this.entityService.findAll(sourceName, ValueList.class, filter))
				// TODO what if e is not found? Test!
				.map(e -> new MDMEntityResponse(ValueList.class, e.toJavaList()))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Returns the created {@link ValueListValue}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param body
	 *            The {@link ValueList} to create.
	 * @return The created {@link ValueList} as {@link Response}.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, String body) {
		return ResourceHelper.deserializeJSON(body)
				// TODO what about deserialization errors and value not found?
				.map(mapping -> mapping.get(ENTITYATTRIBUTE_NAME)
						.map(name -> name.toString()))
				.map(name -> entityService.create(ValueList.class, sourceName, name.get()))
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(ValueList.class, entity.get()), Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Updates the ValueListValue with all parameters set in the given JSON body of
	 * the request
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            the identifier of the {@link ValueListValue} to delete.
	 * @param body
	 *            the body of the request containing the attributes to update
	 * @return the updated {@link ValueListValue}
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response update(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, @PathParam(REQUESTPARAM_ID) String id,
			String body) {
		return ResourceHelper.deserializeJSON(body)
				.map(valueMap -> this.entityService.update(sourceName, ValueList.class, id, valueMap))
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(ValueList.class, entity.get()), Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Returns the deleted {@link ValueList}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            The identifier of the {@link ValueList} to delete.
	 * @return The deleted {@link ValueList }s as {@link Response}
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String id) {
		return Try.of(() -> this.entityService.delete(sourceName, ValueList.class, id)
				.get())
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.map(result -> ServiceUtils.toResponse(new MDMEntityResponse(ValueList.class, result), Status.OK))
				.get();
	}

	/**
	 * Returns the search attributes for the {@link ValueList} type. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return The {@link SearchAttribute}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchattributes")
	public Response getSearchAttributes(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName) {
		return ResourceHelper.createSearchAttributesResponse(entityService, ValueList.class, sourceName);
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
		return ResourceHelper.createLocalizationResponse(entityService, ValueList.class, sourceName);
	}
}