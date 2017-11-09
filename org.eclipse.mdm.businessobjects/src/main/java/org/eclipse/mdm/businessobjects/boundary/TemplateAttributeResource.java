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
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID2;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID3;
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
import org.eclipse.mdm.api.dflt.model.TemplateAttribute;
import org.eclipse.mdm.api.dflt.model.TemplateComponent;
import org.eclipse.mdm.businessobjects.boundary.utils.ResourceHelper;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.control.Try;

/**
 * {@link TemplateAttribute} resource handling REST requests
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/tplroots/{" + REQUESTPARAM_CONTEXTTYPE + "}/{" + REQUESTPARAM_ID
		+ "}/tplcomps/{" + REQUESTPARAM_ID2 + "}/tplattrs")
public class TemplateAttributeResource {

	@EJB
	private EntityService entityService;

	/**
	 * Returns the found {@link TemplateAttribute}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param contextType
	 *            {@link ContextType} of the {@link TemplateAttribute} to load
	 * @param id
	 *            id of the {@link TemplateAttribute}
	 * @return the found {@link TemplateAttribute} as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID3 + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String tplRootId,
			@PathParam(REQUESTPARAM_ID2) String tplCompId, @PathParam(REQUESTPARAM_ID3) String id) {
		return Try.of(() -> ResourceHelper.mapContextType(contextTypeParam))
				.map(contextType -> this.entityService.find(sourceName, TemplateAttribute.class, id, contextType,
						tplRootId, tplCompId))
				// error messages from down the callstack? Use Exceptions or some Vavr magic?
				.map(e -> new MDMEntityResponse(TemplateAttribute.class, e.get()))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				// TODO anehmer on 2017-11-09: send reponse or error regarding error
				// expressiveness
				.get();

	}

	/**
	 * Returns the (filtered) {@link TemplateAttribute}s. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param contextType
	 *            {@link ContextType} of the {@link TemplateAttribute} to load
	 * @param filter
	 *            filter string to filter the {@link TemplateAttribute} result
	 * @return the (filtered) {@link TemplateAttribute}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String tplRootId,
			@PathParam(REQUESTPARAM_ID2) String tplCompId, @QueryParam("filter") String filter) {

		return Try.of(() -> ResourceHelper.mapContextType(contextTypeParam))
				.map(contextType -> this.entityService.find(sourceName, TemplateComponent.class, tplCompId, contextType,
						tplRootId))
				.map(maybeTplComp -> maybeTplComp.map(TemplateComponent::getTemplateAttributes)
						.get())
				// TODO anehmer on 2017-11-09: filter results
				// TODO what if e is not found? Test!
				.map(e -> new MDMEntityResponse(TemplateAttribute.class, e))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Returns the created {@link TemplateAttributeValue}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param body
	 *            The {@link TemplateAttribute} to create.
	 * @return The created {@link TemplateAttribute} as {@link Response}.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	// TODO test with already existing CatComp -> error handling onFailure in
	// EntityService seems not to trigger
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String tplRootId,
			@PathParam(REQUESTPARAM_ID2) String tplCompId, String body) {
		// deserialize JSON into object map
		@SuppressWarnings("unchecked")
		Map<String, Object> mapping = (Map<String, Object>) Try
				.of(() -> new ObjectMapper().readValue(body, new TypeReference<Map<String, Object>>() {
				}))
				.get();

		// get name
		String name = mapping.get(ENTITYATTRIBUTE_NAME)
				.toString();

		return Try.of(() -> ResourceHelper.mapContextType(contextTypeParam))
				.map(contextType -> this.entityService.find(sourceName, TemplateComponent.class, tplCompId, contextType,
						tplRootId))
				.map(maybeTplComp -> maybeTplComp
						.map(tplComp -> entityService.create(TemplateAttribute.class, sourceName, name, tplComp)
								.get())
						.get())
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateAttribute.class, entity),
						Status.OK))
				.get();
	}

	/**
	 * Updates the TemplateAttribute with all parameters set in the given JSON body
	 * of the request
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            the identifier of the {@link TemplateAttribute} to delete.
	 * @param body
	 *            the body of the request containing the attributes to update
	 * @return the updated {@link TemplateAttribute}
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID3 + "}")
	public Response update(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String tplRootId,
			@PathParam(REQUESTPARAM_ID2) String tplCompId, @PathParam(REQUESTPARAM_ID3) String id,
			String body) {
		return ResourceHelper.deserializeJSON(body)
				.map(valueMap -> this.entityService.update(sourceName, TemplateAttribute.class, id, valueMap,
						ResourceHelper.mapContextType(contextTypeParam), tplRootId, tplCompId))
				// TODO if update returns ??? and entity is Option(none), why is the following
				// map() executed?
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateAttribute.class, entity.get()),
						Status.OK))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Returns the deleted {@link TemplateAttribute}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param id
	 *            The identifier of the {@link TemplateAttribute} to delete.
	 * @return The deleted {@link TemplateAttribute }s as {@link Response}
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID3 + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID3) String id) {
		return Try.of(() -> ResourceHelper.mapContextType(contextTypeParam))
				.map(contextType -> this.entityService.delete(TemplateAttribute.class, sourceName, contextType, id))
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				// TODO add check for result.isPresent()
				.map(result -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateAttribute.class, result.get()),
						Status.OK))
				.get();
	}

	/**
	 * Returns the search attributes for the {@link TemplateAttribute} type. Throws
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
		return ResourceHelper.createSearchAttributesResponse(entityService, TemplateAttribute.class, sourceName);
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
		return ResourceHelper.createLocalizationResponse(entityService, TemplateAttribute.class, sourceName);
	}
}