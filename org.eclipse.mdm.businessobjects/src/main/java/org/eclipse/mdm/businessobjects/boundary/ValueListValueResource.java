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
import org.eclipse.mdm.api.dflt.model.ValueList;
import org.eclipse.mdm.api.dflt.model.ValueListValue;
import org.eclipse.mdm.businessobjects.boundary.utils.ResourceHelper;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * {@link ValueListValue} resource handling REST requests
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/valuelists/{" + REQUESTPARAM_ID + "}/values")
public class ValueListValueResource {

	@EJB
	private EntityService entityService;

	/**
	 * Returns the found {@link ValueListValue}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            id of the {@link ValueListValue}
	 * @return the found {@link ValueListValue} as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, @PathParam(REQUESTPARAM_ID) String id) {
		return Try.of(() -> this.entityService.find(ValueListValue.class, sourceName, id))
				// TODO handle failure and respond to client appropriately. How can we deliver
				// error messages from down the callstack? Use Exceptions or some Vavr magic?
				.map(e -> new MDMEntityResponse(ValueListValue.class, e.get()))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowException)
				// TODO send reponse or error regarding error expressiveness
				.get();

	}

	/**
	 * Returns the (filtered) {@link ValueListValue}s. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link ValueListValue} result
	 * @return the (filtered) {@link ValueListValue}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String valueListId, @QueryParam("filter") String filter) {

		return Try
				.of(() -> this.entityService.findChildren(ValueList.class, ValueListValue.class, sourceName,
						valueListId, filter))
				// TODO what if e is not found? Test!
				.map(e -> new MDMEntityResponse(ValueListValue.class, e))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowException)
				.get();
	}

	/**
	 * Returns the created {@link ValueListValue}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param valueListId
	 *            id of the {@link ValueList} to create to value for
	 * @param body
	 *            The {@link ValueListValue} to create.
	 * @return The created {@link ValueListValue} as {@link Response}.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String valueListId, String body) {
		// deserialize JSON into object map
		return Try.<Map<String, Object>>of(
				() -> new ObjectMapper().readValue(body, new TypeReference<Map<String, Object>>() {
					// TODO correct to use onFailure instead of getOrThrow
				}))
				// TODO do we really need this or is the failure handleed later nevertheless
				.onFailure(ResourceHelper.rethrowException)
				.map(mapping -> mapping.get(ENTITYATTRIBUTE_NAME))
				// create tuple with name of ValueListValue and null as the ValueList
				.map(name -> Tuple.of(name.toString(), null))
				// TODO handle non existing value
				// get the ValueList
				.mapTry((Tuple2<String, Object> paramTuple) -> this.entityService
						.find(ValueList.class, sourceName, valueListId)
						.map(valueList -> {
							return paramTuple.update2(valueList);
						}))
				// create ValueListValue
				.mapTry((Option<Tuple2<String, Object>> paramTuple) -> {
					return entityService.<ValueListValue>create(ValueListValue.class, sourceName, paramTuple.get()
							._1(),
							(ValueList) paramTuple.get()
									._2());
				})
				.onFailure(ResourceHelper.rethrowException)
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(ValueListValue.class, entity.get()),
						Status.OK))
				.get();
	}

	/**
	 * Returns the deleted {@link ValueListValue}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param id
	 *            The identifier of the {@link ValueListValue} to delete.
	 * @return The deleted {@link ValueListValue }s as {@link Response}
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String id) {
		return Try.of(() -> this.entityService.delete(ValueListValue.class, sourceName, id)
				.get())
				.onFailure(ResourceHelper.rethrowException)
				.map(result -> ServiceUtils.toResponse(new MDMEntityResponse(ValueListValue.class, result), Status.OK))
				.get();
	}

	/**
	 * Returns the search attributes for the {@link ValueListValue} type. Throws a
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
		return ResourceHelper.createSearchAttributesResponse(entityService, ValueListValue.class, sourceName);
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
		return ResourceHelper.createLocalizationResponse(entityService, ValueListValue.class, sourceName);
	}
}