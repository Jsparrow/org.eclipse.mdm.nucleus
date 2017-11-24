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
import org.eclipse.mdm.api.dflt.model.CatalogSensor;
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
 * {@link CatalogSensor} resource handling REST requests
 * 
 * @author Gunnar Schmidt, science+computing AG Tuebingen (Atos SE)
 *
 */

@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/catcomps/{" + REQUESTPARAM_CONTEXTTYPE + "}/{" + REQUESTPARAM_ID
		+ "}/catsensors")
public class CatalogSensorResource {

	@EJB
	private EntityService entityService;

	/**
	 * Returns the found {@link CatalogSensor}.
	 * 
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            id of the {@link CatalogSensor}
	 * @return the found {@link CatalogSensor} as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String catCompId, @PathParam(REQUESTPARAM_ID2) String id) {
		// TODO anehmer on 2017-11-17: why does this work without passing the CatComp?
		return Try
				.of(() -> entityService.find(sourceName, CatalogSensor.class, id, ContextType.TESTEQUIPMENT, catCompId))
				// TODO handle failure and respond to client appropriately. How can we deliver
				// error messages from down the callstack? Use Exceptions or some Vavr magic?
				.map(e -> new MDMEntityResponse(CatalogSensor.class, e.get()))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				// TODO send reponse or error regarding error expressiveness
				.get();

	}

	/**
	 * Returns the (filtered) {@link CatalogSensor}s.
	 * 
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link CatalogSensor} result
	 * @return the (filtered) {@link CatalogSensor}s as {@link Response}
	 */
	// TODO anehmer on 2017-11-17: fix -> returns all CatalogSensors and not only
	// the ones in the superordinate CatalogComponent
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String catCompId, @QueryParam("filter") String filter) {
		return Try.of(() -> entityService.find(sourceName, CatalogComponent.class, catCompId, ContextType.TESTEQUIPMENT)
				.map(catComp -> catComp.getCatalogSensors())
				.get())
				// TODO what if e is not found? Test!
				.map(e -> new MDMEntityResponse(CatalogSensor.class, e))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Returns the created {@link CatalogSensor}.
	 * 
	 * 
	 * @param body
	 *            The {@link CatalogSensor} to create.
	 * @return the created {@link CatalogSensor} as {@link Response}.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_CONTEXTTYPE) String contextTypeParam, @PathParam(REQUESTPARAM_ID) String catCompId,
			String body) {

		// deserialize JSON into object map
		@SuppressWarnings("unchecked")
		Map<String, Object> mapping = (Map<String, Object>) Try
				.of(() -> new ObjectMapper().readValue(body, new TypeReference<Map<String, Object>>() {
				}))
				.get();

		// get name
		Option<String> name = Try.of(() -> mapping.get(ENTITYATTRIBUTE_NAME)
				.toString())
				.toOption();

		// get contextType
		Option<ContextType> contextType = Try.of(() -> ServiceUtils.getContextTypeSupplier(contextTypeParam))
				.toOption();

		// get catalog component
		Option<CatalogComponent> catComp = Try
				.of(() -> entityService.find(sourceName, CatalogComponent.class, catCompId, contextType.get()))
				.get();

		return Try.of(() -> entityService.create(CatalogSensor.class, sourceName, name.get(), catComp.get())
				.get())
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(CatalogSensor.class, entity), Status.OK))
				.get();
	}

	/**
	 * Updates the CatalogSensor with all parameters set in the given JSON body of
	 * the request
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            the identifier of the {@link CatalogSensor} to delete.
	 * @param body
	 *            the body of the request containing the attributes to update
	 * @return the updated {@link CatalogSensor}
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response update(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String catCompId, @PathParam(REQUESTPARAM_ID2) String id, String body) {
		return ResourceHelper.deserializeJSON(body)
				.map(valueMap -> entityService.update(sourceName, CatalogSensor.class, id, valueMap,
						ContextType.TESTEQUIPMENT, catCompId))
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(CatalogSensor.class, entity.get()),
						Status.OK))
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Deletes and returns the deleted {@link CatalogSensor}.
	 * 
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            The identifier of the {@link CatalogSensor} to delete.
	 * @return the deleted {@link CatalogSensor }s as {@link Response}
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID2 + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String catCompId, @PathParam(REQUESTPARAM_ID2) String id) {
		return Try
				.of(() -> entityService.delete(sourceName, CatalogSensor.class, id, ContextType.TESTEQUIPMENT,
						catCompId))
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				.map(result -> ResourceHelper.toResponse(new MDMEntityResponse(CatalogSensor.class, result.get()),
						Status.OK))
				.get();
	}

	/**
	 * Returns the search attributes for the {@link CatalogSensor} type.
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
		return ServiceUtils.buildSearchAttributesResponse(entityService, CatalogSensor.class, sourceName);
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
		return ServiceUtils.buildLocalizationResponse(entityService, CatalogSensor.class, sourceName);
	}
}