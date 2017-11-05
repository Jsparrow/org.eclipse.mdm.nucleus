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
import org.eclipse.mdm.api.dflt.model.CatalogComponent;
import org.eclipse.mdm.api.dflt.model.TemplateComponent;
import org.eclipse.mdm.api.dflt.model.TemplateRoot;
import org.eclipse.mdm.businessobjects.boundary.utils.ResourceHelper;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * {@link TemplateComponent} resource handling REST requests for nested
 * {@link TemplateComponent}s
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/tplroots/{" + REQUESTPARAM_CONTEXTTYPE + "}/{" + REQUESTPARAM_ID
		+ "}/tplcomps/{" + REQUESTPARAM_ID2 + "}/tplcomps")
public class NestedTemplateComponentResource {

	@EJB
	private EntityService entityService;

	/**
	 * Returns the found {@link TemplateComponent}. Throws a
	 * {@link WebApplicationException} on error.
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
	@Path("/{" + REQUESTPARAM_ID3 + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String tplRootId,
			@PathParam(REQUESTPARAM_ID2) String parentTplCompId, @PathParam(REQUESTPARAM_ID3) String id) {
		return Try.of(() -> ResourceHelper.mapContextType(contextTypeParam))
				.map(contextType -> this.entityService.find(TemplateRoot.class, contextType, sourceName, tplRootId))
				// get matching parent
				// TODO not so beautiful to do that get() at the end
				.map(root -> root.map(r -> Stream.ofAll(r.getTemplateComponents())
						.find(tplComp -> tplComp.getID()
								.equals(parentTplCompId))
						.get()))
				// get nested tplComp
				.map(parentTplComp -> parentTplComp.map(comp -> Stream.ofAll(comp.getTemplateComponents())
						.find(nestedTplComp -> nestedTplComp.getID()
								.equals(id))
						.get())
						.get())
				// error messages from down the callstack? Use Exceptions or some Vavr magic?
				.map(e -> new MDMEntityResponse(TemplateComponent.class, e))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowException)
				// TODO send reponse or error regarding error expressiveness
				.get();

	}

	/**
	 * Returns the (filtered) {@link TemplateComponent}s. Throws a
	 * {@link WebApplicationException} on error.
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
			@PathParam(REQUESTPARAM_ID2) String parentTplCompId,
			@QueryParam("filter") String filter) {
		return Try.of(() -> ResourceHelper.mapContextType(contextTypeParam))
				// find the TemplateRoot
				.map(ct -> this.entityService.find(TemplateRoot.class, ct, sourceName, tplRootId))
				// get matching parent
				// TODO not so beautiful to do that get() at the end
				.map(root -> root.map(r -> Stream.ofAll(r.getTemplateComponents())
						.find(tplComp -> tplComp.getID()
								.equals(parentTplCompId))
						.get())
						.get())
				// find the TemplateComponents
				.map(parentTplComp -> parentTplComp.getTemplateComponents())
				// prepare the result
				.map(e -> new MDMEntityResponse(TemplateComponent.class, e))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ResourceHelper.rethrowException)
				.get();
	}

	/**
	 * Returns the created {@link TemplateComponentValue}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param body
	 *            The {@link TemplateComponent} to create.
	 * @param id
	 *            the identifier of the parent {@link TemplateComponent}.
	 * @return The created {@link TemplateComponent} as {@link Response}.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String tplRootId,
			@PathParam(REQUESTPARAM_ID2) String tplParentCompId, String body) {

		// deserialize JSON into object map
		@SuppressWarnings("unchecked")
		Map<String, Object> mapping = (Map<String, Object>) Try
				.of(() -> new ObjectMapper().readValue(body, new TypeReference<Map<String, Object>>() {
				}))
				.get();

		Option<String> name = Try.of(() -> mapping.get(ENTITYATTRIBUTE_NAME)
				.toString())
				.toOption();

		// get contextType
		Option<ContextType> contextType = Try.of(() -> ResourceHelper.mapContextType(contextTypeParam))
				.toOption();

		// get catCompId
		// TODO discuss if name of CatComp (as it is unique) could be used
		// (additionally)
		Option<String> catCompId = Try.of(() -> mapping.get(ENTITYATTRIBUTE_CATALOGCOMPONENT_ID)
				.toString())
				.toOption();

		// get catalog component
		// TODO handle non-existing catComp
		Option<CatalogComponent> catComp = Try.of(
				() -> this.entityService.find(CatalogComponent.class, contextType.get(), sourceName, catCompId.get()))
				.get();

		// get template root
		Option<TemplateRoot> tplRoot = Try
				.of(() -> this.entityService.find(TemplateRoot.class, contextType.get(), sourceName, tplRootId))
				.get();

		// get parent component
		// TODO use vavr
		Option<TemplateComponent> tplParent = tplRoot.map(r -> r.getTemplateComponents()
				.stream()
				.filter(t -> t.getID()
						.equals(tplParentCompId))
				.findFirst()
				.get());

		// set template root in parent Comp
		// TODO should not use that method from EntityService as that's background logic
		tplParent.forEach(p -> EntityService.setParentEntity(p, tplRoot.get(), null));

		// create
		return Try
				.of(() -> this.entityService
						.create(TemplateComponent.class, sourceName, name.get(), tplParent.get(), catComp.get())
						.get())
				.onFailure(ResourceHelper.rethrowException)
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateComponent.class, entity),
						Status.OK))
				.get();

	}

	/**
	 * Updates the TemplateComponent with all parameters set in the given JSON body
	 * of the request
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
	@Path("/{" + REQUESTPARAM_ID3 + "}")
	public Response update(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID3) String id,
			String body) {
		return ResourceHelper.deserializeJSON(body)
				.map(valueMap -> this.entityService.update(TemplateComponent.class,
						ResourceHelper.mapContextType(contextTypeParam), sourceName, id, valueMap))
				// TODO if update returns ??? and entity is Option(none), why is the following
				// map() executed?
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateComponent.class, entity.get()),
						Status.OK))
				.onFailure(ResourceHelper.rethrowException)
				.get();
	}

	/**
	 * Returns the deleted {@link TemplateComponent}. Throws a
	 * {@link WebApplicationException} on error.
	 * 
	 * @param id
	 *            The identifier of the {@link TemplateComponent} to delete.
	 * @return The deleted {@link TemplateComponent }s as {@link Response}
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID3 + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID3) String id) {
		return Try.of(() -> ResourceHelper.mapContextType(contextTypeParam))
				.map(contextType -> this.entityService.delete(TemplateComponent.class, sourceName, contextType, id)
						.get())
				.onFailure(ResourceHelper.rethrowException)
				.map(result -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateComponent.class, result),
						Status.OK))
				.get();
	}

	/**
	 * Returns the search attributes for the {@link TemplateComponent} type. Throws
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
		return ResourceHelper.createSearchAttributesResponse(entityService, TemplateComponent.class, sourceName);
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
		return ResourceHelper.createLocalizationResponse(entityService, TemplateComponent.class, sourceName);
	}
}