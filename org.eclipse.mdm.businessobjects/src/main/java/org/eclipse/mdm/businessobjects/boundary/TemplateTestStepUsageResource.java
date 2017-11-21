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

import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.ENTITYATTRIBUTE_TEMPLATETESTSTEP_ID;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID2;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_SOURCENAME;

import java.util.UUID;

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
import org.eclipse.mdm.api.dflt.model.TemplateTest;
import org.eclipse.mdm.api.dflt.model.TemplateTestStep;
import org.eclipse.mdm.api.dflt.model.TemplateTestStepUsage;
import org.eclipse.mdm.businessobjects.boundary.utils.ResourceHelper;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import io.vavr.collection.Map;
import io.vavr.control.Try;

/**
 * {@link TemplateTestStepUsage} resource handling REST requests
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/tpltests/{" + REQUESTPARAM_ID + "}/tplteststepusages")
public class TemplateTestStepUsageResource {

	@EJB
	private EntityService entityService;

	/**
	 * Returns the found {@link TemplateTestStep}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            id of the {@link TemplateTestStep}
	 * @return the found {@link TemplateTestStep} as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String tplTestId, @PathParam(REQUESTPARAM_ID2) String id) {
		return entityService.find(sourceName, TemplateTestStepUsage.class, id, tplTestId)
				// error messages from down the callstack? Use Exceptions or some Vavr magic?
				.map(e -> new MDMEntityResponse(TemplateTestStep.class, e))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				// TODO send reponse or error regarding error expressiveness
				// TODO anehmer on 2017-11-21: get Try from entityService to use Exception to
				// build response
				.getOrElse(ServiceUtils.toResponse(new Exception("TemplateTestUsage not found"),
						Status.INTERNAL_SERVER_ERROR));

	}

	/**
	 * Returns the (filtered) {@link TemplateTestStepUsage}s. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link TemplateTestStepUsage} result
	 * @return the (filtered) {@link TemplateTestStepUsage}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String tplTestId, @QueryParam("filter") String filter) {
		return entityService.find(sourceName, TemplateTest.class, tplTestId)
				.map(tplTest -> tplTest.getTemplateTestStepUsages())
				.map(e -> new MDMEntityResponse(TemplateTestStep.class, e))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				// TODO send reponse or error regarding error expressiveness
				// TODO anehmer on 2017-11-21: get Try from entityService to use Exception to
				// build response
				.getOrElse(ServiceUtils.toResponse(new Exception("TemplateTestUsage not found"),
						Status.INTERNAL_SERVER_ERROR));
	}

	/**
	 * Returns the created {@link TemplateTestStepUsageValue}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param body
	 *            The {@link TemplateTestStepUsage} to create.
	 * @return The created {@link TemplateTestStepUsage} as {@link Response}.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String tplTestId, String body) {
		Try<Map<String, Object>> mapper = ResourceHelper.deserializeJSON(body);
		// TODO rewrite completely and add not-found / error handling

		// get TemplateTest
		// TODO anehmer on 2017-11-21: what should find() return? Try for further
		// processing but here Option seems to fit better. BUT: do we want to use get()?
		// Nope, must be another solution --> should return ErrorResponse as tplTest is
		// not found -> does onEmpty() work to instantly return Response?
		TemplateTest tplTest = this.entityService.find(sourceName, TemplateTest.class, tplTestId)
				.get();

		// get TemplateTest
		TemplateTestStep tplTestStep = mapper
				.mapTry(m -> this.entityService.find(sourceName, TemplateTestStep.class,
						m.get(ENTITYATTRIBUTE_TEMPLATETESTSTEP_ID)
								.get()
								.toString()))
				.get()
				.get();

		return Try.of(() ->
		// create TemplateTestStepUsage but return TemplateTestStep
		entityService.create(TemplateTestStepUsage.class, sourceName, UUID.randomUUID()
				.toString(), tplTest, tplTestStep)
				.get())
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateTestStep.class, entity),
						Status.OK))
				.get();
	}

	/*
	 * Update not implemented as TemplateTestStepUsages cannot be updated
	 */

	/**
	 * Returns the deleted {@link TemplateTestStepUsage}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param id
	 *            The identifier of the {@link TemplateTestStepUsage} to delete.
	 * @return The deleted {@link TemplateTestStepUsage }s as {@link Response}
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String tplTestId, @PathParam(REQUESTPARAM_ID2) String id) {
		return entityService.delete(sourceName, TemplateTestStepUsage.class, id, tplTestId)
				.map(e -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateTestStepUsage.class, e),
						Status.OK))
				.get();
	}

	/**
	 * Returns the search attributes for the {@link TemplateTestStepUsage} type.
	 * Throws a {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return The {@link SearchAttribute}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchattributes")
	public Response getSearchAttributes(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName) {
		return ResourceHelper.createSearchAttributesResponse(entityService, TemplateTestStepUsage.class, sourceName);
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
		return ResourceHelper.createLocalizationResponse(entityService, TemplateTestStepUsage.class, sourceName);
	}
}