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

import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.ENTITYATTRIBUTE_DATATYPE;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.ENTITYATTRIBUTE_NAME;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_CONTEXTTYPE;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID2;
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
import org.eclipse.mdm.api.base.model.EnumRegistry;
import org.eclipse.mdm.api.base.model.EnumerationValue;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.dflt.model.CatalogAttribute;
import org.eclipse.mdm.api.dflt.model.CatalogComponent;
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
 * {@link CatalogAttribute} resource handling REST requests
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/catcomps/{" + REQUESTPARAM_CONTEXTTYPE + "}/{" + REQUESTPARAM_ID
		+ "}/catattrs")
public class CatalogAttributeResource {

	@EJB
	private EntityService entityService;

	/**
	 * Returns the found {@link CatalogAttribute}.
	 * 
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param contextType
	 *            {@link ContextType} of the {@link CatalogAttribute} to load
	 * @param id
	 *            id of the {@link CatalogAttribute}
	 * @return the found {@link CatalogAttribute} as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String catCompId,
			@PathParam(REQUESTPARAM_ID2) String id) {
		return Try.of(() -> ServiceUtils.getContextTypeSupplier(contextTypeParam))
				.map(contextType -> entityService.find(sourceName, CatalogAttribute.class, id, contextType, catCompId))
				// TODO error messages from down the callstack? Use Exceptions or some Vavr
				// magic?
				.map(e -> new MDMEntityResponse(CatalogAttribute.class, e.get()))
				.map(r -> ResourceHelper.toResponse(r, Status.OK))
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				// TODO send reponse or error regarding error expressiveness
				.get();

	}

	/**
	 * Returns the (filtered) {@link CatalogAttribute}s.
	 * 
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param contextType
	 *            {@link ContextType} of the {@link CatalogAttribute} to load
	 * @param filter
	 *            filter string to filter the {@link CatalogAttribute} result
	 * @return the (filtered) {@link CatalogAttribute}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String catCompId,
			@QueryParam("filter") String filter) {
		return Try.of(() -> ServiceUtils.getContextTypeSupplier(contextTypeParam))
				// get CatalogAttributes from CatalogComponent as the request is made in the
				// context of one
				// TODO anehmer on 2017-11-09: add filter
				.map(contextType -> entityService.find(sourceName, CatalogComponent.class, catCompId, contextType)
						.map(catComp -> catComp.getCatalogAttributes())
						.get())
				// TODO anehmer on 2017-11-09: what if e is not found? Test!
				.map(e -> new MDMEntityResponse(CatalogAttribute.class, e))
				.map(r -> ResourceHelper.toResponse(r, Status.OK))
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Returns the created {@link CatalogAttributeValue}.
	 * 
	 * 
	 * @param body
	 *            The {@link CatalogAttribute} to create.
	 * @return the created {@link CatalogAttribute} as {@link Response}.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	// TODO test with already existing CatComp -> error handling onFailure in
	// EntityService seems not to trigger
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String catCompId,
			String body) {
		// deserialize JSON into object map
		@SuppressWarnings("unchecked")
		Map<String, Object> mapping = (Map<String, Object>) Try
				.of(() -> new ObjectMapper().readValue(body, new TypeReference<Map<String, Object>>() {
				}))
				.get();

		// TODO clean up that mess

		// TODO error handling
		// get name
		Option<String> name = Try.of(() -> mapping.get(ENTITYATTRIBUTE_NAME)
				.toString())
				.toOption();

		// get enumerationValue
		// TODO handle non-existing valueType
		Option<EnumerationValue> valueType = Try.<EnumerationValue>of(() -> EnumRegistry.getInstance()
				.get(EnumRegistry.VALUE_TYPE)
				.valueOf(mapping.get(ENTITYATTRIBUTE_DATATYPE)
						.toString()))
				.toOption();

		// get contextType
		Option<ContextType> contextType = Try.of(() -> ServiceUtils.getContextTypeSupplier(contextTypeParam))
				.toOption();

		// get catalog component
		Option<CatalogComponent> catComp = Try
				.of(() -> entityService.find(sourceName, CatalogComponent.class, catCompId, contextType.get()))
				.get();

		// create catalog attribute
		return Try
				.of(() -> entityService
						.create(CatalogAttribute.class, sourceName, name.get(), valueType.get(), catComp.get())
						.get())
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(CatalogAttribute.class, entity),
						Status.OK))
				.get();
	}

	/**
	 * Updates the CatalogAttribute with all parameters set in the given JSON body
	 * of the request
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            the identifier of the {@link CatalogAttribute} to delete.
	 * @param body
	 *            the body of the request containing the attributes to update
	 * @return the updated {@link CatalogAttribute}
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response update(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID2) String id,
			@PathParam(REQUESTPARAM_ID) String catCompId, String body) {
		return ResourceHelper.deserializeJSON(body)
				.map(valueMap -> entityService.update(sourceName, CatalogAttribute.class, id, valueMap,
						ServiceUtils.getContextTypeSupplier(contextTypeParam), catCompId))
				// TODO if update returns ??? and entity is Option(none), why is the following
				// map() executed?
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(CatalogAttribute.class, entity.get()),
						Status.OK))
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Deletes and returns the deleted {@link CatalogAttribute}.
	 * 
	 * 
	 * @param id
	 *            The identifier of the {@link CatalogAttribute} to delete.
	 * @return the deleted {@link CatalogAttribute }s as {@link Response}
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String catCompId,
			@PathParam(REQUESTPARAM_ID2) String id) {
		return Try.of(() -> ServiceUtils.getContextTypeSupplier(contextTypeParam))
				.map(contextType -> entityService.delete(sourceName, CatalogAttribute.class, id, contextType,
						catCompId))
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				// TODO add check for result.isPresent()
				.map(result -> ResourceHelper.toResponse(new MDMEntityResponse(CatalogAttribute.class, result.get()),
						Status.OK))
				.get();
	}

	/**
	 * Returns the search attributes for the {@link CatalogAttribute} type.
	 * 
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the {@link SearchAttribute}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchattributes")
	public Response getSearchAttributes(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName) {
		return ServiceUtils.buildSearchAttributesResponse(entityService, CatalogAttribute.class, sourceName);
	}

	/**
	 * Returns a map of localization for the entity type and the attributes.
	 * 
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the I18N as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/localizations")
	public Response localize(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName) {
		return ServiceUtils.buildLocalizationResponse(entityService, CatalogAttribute.class, sourceName);
	}
}