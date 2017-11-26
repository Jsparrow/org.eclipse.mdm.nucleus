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

import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.ENTITYATTRIBUTE_CATALOGCOMPONENT_ID;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.ENTITYATTRIBUTE_NAME;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_CONTEXTTYPE;
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
import org.eclipse.mdm.api.dflt.model.TemplateComponent;
import org.eclipse.mdm.api.dflt.model.TemplateRoot;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.RequestBody;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import io.vavr.collection.List;

/**
 * {@link TemplateComponent} resource handling REST requests
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/tplroots/{" + REQUESTPARAM_CONTEXTTYPE + "}/{" + REQUESTPARAM_ID
		+ "}/tplcomps")
public class TemplateComponentResource {

	@EJB
	private EntityService entityService;

	/**
	 * Returns the found {@link TemplateComponent}.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param contextType
	 *            {@link ContextType} of the {@link TemplateComponent} to load
	 * @param id
	 *            id of the {@link TemplateComponent}
	 * @return the found {@link TemplateComponent} as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String tplRootId,
			@PathParam(REQUESTPARAM_ID2) String id) {
		return entityService
				.find(V(sourceName), TemplateComponent.class, V(id), ServiceUtils.getContextTypeSupplier(contextTypeParam),
						SL(tplRootId))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.FOUND))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Returns the (filtered) {@link TemplateComponent}s.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param contextType
	 *            {@link ContextType} of the {@link TemplateComponent} to load
	 * @param filter
	 *            filter string to filter the {@link TemplateComponent} result
	 * @return the (filtered) {@link TemplateComponent}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String tplRootId,
			@QueryParam("filter") String filter) {
		return entityService
				.find(V(sourceName), TemplateRoot.class, V(tplRootId),
						ServiceUtils.getContextTypeSupplier(contextTypeParam))
				.map(tplRoot -> List.ofAll(tplRoot.getTemplateComponents()))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.FOUND))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Returns the created {@link TemplateComponentValue}.
	 * 
	 * @param body
	 *            The {@link TemplateComponent} to create.
	 * @return the created {@link TemplateComponent} as {@link Response}.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String tplRootId,
			String body) {
		RequestBody requestBody = RequestBody.create(body);

		return entityService
				.create(V(sourceName), TemplateComponent.class,
						L(requestBody.getStringValueSupplier(ENTITYATTRIBUTE_NAME),
						entityService.find(V(sourceName), TemplateRoot.class, V(tplRootId),
								ServiceUtils.getContextTypeSupplier(contextTypeParam)),
						entityService.find(V(sourceName), CatalogComponent.class,
								requestBody.getStringValueSupplier(ENTITYATTRIBUTE_CATALOGCOMPONENT_ID),
										ServiceUtils.getContextTypeSupplier(contextTypeParam))))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.FOUND))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Updates the {@link TemplateComponent} with all parameters set in the given
	 * JSON body of the request
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            the identifier of the {@link TemplateComponent} to update.
	 * @param body
	 *            the body of the request containing the attributes to update
	 * @return the updated {@link TemplateComponent}
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response update(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String tplRootId,
			@PathParam(REQUESTPARAM_ID2) String id, String body) {
		RequestBody requestBody = RequestBody.create(body);

		return entityService
				.update(V(sourceName),
						entityService.find(V(sourceName), TemplateComponent.class, V(id),
								ServiceUtils.getContextTypeSupplier(contextTypeParam), SL(tplRootId)),
						requestBody.getValueMapSupplier())
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Deletes and returns the deleted {@link TemplateComponent}.
	 * 
	 * @param id
	 *            The identifier of the {@link TemplateComponent} to delete.
	 * @return the deleted {@link TemplateComponent }s as {@link Response}
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String tplRootId,
			@PathParam(REQUESTPARAM_ID2) String id) {
		return entityService
				.delete(V(sourceName),
						entityService.find(V(sourceName), TemplateComponent.class, V(id),
								ServiceUtils.getContextTypeSupplier(contextTypeParam), SL(tplRootId)))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Returns the search attributes for the {@link TemplateComponent} type.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the {@link SearchAttribute}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchattributes")
	public Response getSearchAttributes(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName) {
		return ServiceUtils.buildSearchAttributesResponse(V(sourceName), TemplateComponent.class, entityService);
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
		return ServiceUtils.buildLocalizationResponse(V(sourceName), TemplateComponent.class, entityService);
	}
}