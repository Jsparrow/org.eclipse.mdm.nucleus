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
import org.eclipse.mdm.api.dflt.model.TemplateTest;
import org.eclipse.mdm.businessobjects.boundary.utils.ResourceHelper;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.control.Try;

/**
 * {@link TemplateTest} resource handling REST requests
 * 
 * @author Gunnar Schmidt, science+computing AG Tuebingen (Atos SE)
 *
 */
@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/tpltests")
public class TemplateTestResource {

	@EJB
	private EntityService entityService;

	/**
	 * Returns the found {@link TemplateTest}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            id of the {@link TemplateTest}
	 * @return the found {@link TemplateTest} as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, @PathParam(REQUESTPARAM_ID) String id) {
		return Try.of(() -> this.entityService.find(sourceName, TemplateTest.class, id))
				// TODO handle failure and respond to client appropriately. How can we deliver
				// error messages from down the callstack? Use Exceptions or some Vavr magic?
				.map(e -> new MDMEntityResponse(TemplateTest.class, e.get()))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				// TODO send reponse or error regarding error expressiveness
				.get();

	}

	/**
	 * Returns the (filtered) {@link TemplateTest}s. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link TemplateTest} result
	 * @return the (filtered) {@link TemplateTest}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@QueryParam("filter") String filter) {
		return Try.of(() -> this.entityService.findAll(sourceName, TemplateTest.class, sourceName))
				// TODO what if e is not found? Test!
				.map(e -> new MDMEntityResponse(TemplateTest.class, e.toJavaList()))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Returns the created {@link TemplateTestValue}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param body
	 *            The {@link TemplateTest} to create.
	 * @return The created {@link TemplateTest} as {@link Response}.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, String body) {
		// deserialize JSON into object map
		return Try.<Map<String, Object>>of(
				() -> new ObjectMapper().readValue(body, new TypeReference<Map<String, Object>>() {
					// TODO correct to use onFailure instead of getOrThrow
				}))
				// TODO do we really need this or is the failure handled later nevertheless
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.toOption()
				.map(mapping -> mapping.get(ENTITYATTRIBUTE_NAME))
				// TODO handle non existing value
				.toTry()
				.map(name -> entityService.create(TemplateTest.class, sourceName, name.toString())
						.get())
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateTest.class, entity), Status.OK))
				.get();
	}

	/**
	 * Updates the TemplateTest with all parameters set in the given JSON body of
	 * the request
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            the identifier of the {@link TemplateTest} to delete.
	 * @param body
	 *            the body of the request containing the attributes to update
	 * @return the updated {@link TemplateTest}
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response update(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, @PathParam(REQUESTPARAM_ID) String id,
			String body) {
		return ResourceHelper.deserializeJSON(body)
				.map(valueMap -> this.entityService.update(sourceName, TemplateTest.class, id, valueMap))
				// TODO if update returns ??? and entity is Option(none), why is the following
				// map() executed?
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateTest.class, entity.get()),
						Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Returns the deleted {@link TemplateTest}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param id
	 *            The identifier of the {@link TemplateTest} to delete.
	 * @return The deleted {@link TemplateTest }s as {@link Response}
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String id) {
		return Try.of(() -> this.entityService.delete(TemplateTest.class, sourceName, id)
				.get())
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.map(result -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateTest.class, result), Status.OK))
				.get();
	}

	/**
	 * Returns the search attributes for the {@link TemplateTest} type. Throws a
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
		return ResourceHelper.createSearchAttributesResponse(entityService, TemplateTest.class, sourceName);
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
		return ResourceHelper.createLocalizationResponse(entityService, TemplateTest.class, sourceName);
	}
}