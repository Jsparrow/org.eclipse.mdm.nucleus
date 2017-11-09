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
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_CONTEXTTYPE;
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

import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.dflt.model.TemplateTestStep;
import org.eclipse.mdm.businessobjects.boundary.utils.ResourceHelper;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.control.Try;

/**
 * {@link TemplateTestStep} resource handling REST requests
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/tplteststeps")
public class TemplateTestStepResource {

	@EJB
	private EntityService entityService;

	/**
	 * Returns the found {@link TemplateTestStep}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param contextType
	 *            {@link ContextType} of the {@link TemplateTestStep} to load
	 * @param id
	 *            id of the {@link TemplateTestStep}
	 * @return the found {@link TemplateTestStep} as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String id) {
		return Try.of(() -> this.entityService.find(sourceName, TemplateTestStep.class, id))
				// error messages from down the callstack? Use Exceptions or some Vavr magic?
				.map(e -> new MDMEntityResponse(TemplateTestStep.class, e.get()))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				// TODO send reponse or error regarding error expressiveness
				.get();

	}

	/**
	 * Returns the (filtered) {@link TemplateTestStep}s. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param contextType
	 *            {@link ContextType} of the {@link TemplateTestStep} to load
	 * @param filter
	 *            filter string to filter the {@link TemplateTestStep} result
	 * @return the (filtered) {@link TemplateTestStep}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @QueryParam("filter") String filter) {
		return Try.of(() -> this.entityService.findAll(sourceName, TemplateTestStep.class, filter))
				// TODO what if e is not found? Test!
				.map(e -> new MDMEntityResponse(TemplateTestStep.class, e.toJavaList()))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Returns the created {@link TemplateTestStepValue}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param body
	 *            The {@link TemplateTestStep} to create.
	 * @return The created {@link TemplateTestStep} as {@link Response}.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	// TODO test with already existing CatComp -> error handling onFailure in
	// EntityService seems not to trigger
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, String body) {
		// deserialize JSON into object map
		return Try.<Map<String, Object>>of(
				// TODO replace with function in ResourceHelper
				() -> new ObjectMapper().readValue(body, new TypeReference<Map<String, Object>>() {
					// TODO correct to use onFailure instead of getOrThrow
				}))
				// TODO do we really need this or is the failure handled later nevertheless
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.map(mapping -> mapping.get(ENTITYATTRIBUTE_NAME))
				// TODO handle non existing value
				// create TemplateTestStep
				.map(name -> entityService.create(TemplateTestStep.class, sourceName, name)
						.get())
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateTestStep.class, entity),
						Status.OK))
				.get();
	}

	/**
	 * Updates the TemplateTestStep with all parameters set in the given JSON body
	 * of the request
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            the identifier of the {@link TemplateTestStep} to delete.
	 * @param body
	 *            the body of the request containing the attributes to update
	 * @return the updated {@link TemplateTestStep}
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response update(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, @PathParam(REQUESTPARAM_ID) String id,
			String body) {
		return ResourceHelper.deserializeJSON(body)
				.map(valueMap -> this.entityService.update(sourceName, TemplateTestStep.class, id, valueMap))
				// TODO if update returns ??? and entity is Option(none), why is the following
				// map() executed?
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateTestStep.class, entity.get()),
						Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Returns the deleted {@link TemplateTestStep}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param id
	 *            The identifier of the {@link TemplateTestStep} to delete.
	 * @return The deleted {@link TemplateTestStep }s as {@link Response}
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String id) {
		return Try.of(() -> this.entityService.delete(TemplateTestStep.class, sourceName, id)
				.get())
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.map(result -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateTestStep.class, result),
						Status.OK))
				.get();
	}

	/**
	 * Returns the search attributes for the {@link TemplateTestStep} type. Throws a
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
		return ResourceHelper.createSearchAttributesResponse(entityService, TemplateTestStep.class, sourceName);
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
		return ResourceHelper.createLocalizationResponse(entityService, TemplateTestStep.class, sourceName);
	}
}