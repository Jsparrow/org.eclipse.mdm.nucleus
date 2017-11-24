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

import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.ENTITYATTRIBUTE_CATALOGSENSOR_ID;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.ENTITYATTRIBUTE_NAME;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.ENTITYATTRIBUTE_QUANTITY_ID;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID2;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID3;
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

import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Quantity;
import org.eclipse.mdm.api.dflt.model.CatalogComponent;
import org.eclipse.mdm.api.dflt.model.CatalogSensor;
import org.eclipse.mdm.api.dflt.model.TemplateComponent;
import org.eclipse.mdm.api.dflt.model.TemplateSensor;
import org.eclipse.mdm.businessobjects.boundary.utils.ResourceHelper;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.EntityNotFoundException;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import io.vavr.collection.Map;
import io.vavr.control.Try;

/**
 * {@link TemplateSensor} resource handling REST requests
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/tplroots/testequipment/{" + REQUESTPARAM_ID + "}/tplcomps/{"
		+ REQUESTPARAM_ID2 + "}/tplsensors")
public class TemplateSensorResource {

	@EJB
	private EntityService entityService;

	/**
	 * Returns the found {@link TemplateSensor}.
	 * 
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            id of the {@link TemplateSensor}
	 * @return the found {@link TemplateSensor} as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID3 + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String tplRootId, @PathParam(REQUESTPARAM_ID2) String tplCompId,
			@PathParam(REQUESTPARAM_ID3) String id) {
		return Try
				.of(() -> entityService.find(sourceName, TemplateSensor.class, id, ContextType.TESTEQUIPMENT, tplRootId,
						tplCompId))
				// error messages from down the callstack? Use Exceptions or some Vavr magic?
				.map(e -> new MDMEntityResponse(TemplateSensor.class, e.get()))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				// TODO anehmer on 2017-11-09: send reponse or error regarding error
				// expressiveness
				.get();

	}

	/**
	 * Returns the (filtered) {@link TemplateSensor}s.
	 * 
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link TemplateSensor} result
	 * @return the (filtered) {@link TemplateSensor}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String tplRootId, @PathParam(REQUESTPARAM_ID2) String tplCompId,
			@QueryParam("filter") String filter) {

		return Try
				.of(() -> entityService.find(sourceName, TemplateComponent.class, tplCompId, ContextType.TESTEQUIPMENT,
						tplRootId))
				.map(maybeTplComp -> maybeTplComp.map(TemplateComponent::getTemplateSensors)
						.get())
				.map(e -> new MDMEntityResponse(TemplateSensor.class, e))
				.map(r -> ServiceUtils.toResponse(r, Status.OK))
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Returns the created {@link TemplateSensorValue}.
	 * 
	 * 
	 * @param body
	 *            The {@link TemplateSensor} to create.
	 * @return the created {@link TemplateSensor} as {@link Response}.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String tplRootId, @PathParam(REQUESTPARAM_ID2) String tplCompId, String body) {

		// get name
		Try<Map<String, Object>> mapper = ResourceHelper.deserializeJSON(body);
		String name = mapper.map(map -> map.get(ENTITYATTRIBUTE_NAME)
				.get()
				.toString())
				.getOrElseThrow(x -> new IllegalArgumentException("Name of TemplateSensor missing in request"));

		String quantityId = mapper.map(map -> map.get(ENTITYATTRIBUTE_QUANTITY_ID)
				.get()
				.toString())
				.getOrElseThrow(() -> new IllegalArgumentException("Id of Quantity missing in request"));

		String catSensorId = mapper.map(map -> map.get(ENTITYATTRIBUTE_CATALOGSENSOR_ID)
				.get()
				.toString())
				.getOrElseThrow(x -> new IllegalArgumentException("Id of CatalogSensor missing in request"));

		TemplateComponent tplComp = entityService
				.find(sourceName, TemplateComponent.class, tplCompId, ContextType.TESTEQUIPMENT, tplRootId)
				.getOrElseThrow(() -> new EntityNotFoundException(TemplateComponent.class, tplCompId));

		CatalogComponent catComp = tplComp.getCatalogComponent();

		CatalogSensor catSensor = entityService
				.find(sourceName, CatalogSensor.class, catSensorId, ContextType.TESTEQUIPMENT, catComp.getID())
				.getOrElseThrow(() -> new IllegalArgumentException("CatalogSensor not found"));

		Quantity quantity = entityService.find(sourceName, Quantity.class, quantityId)
				.getOrElseThrow(() -> new IllegalArgumentException("Quantity not found"));

		return Try.of(() -> entityService.create(TemplateSensor.class, sourceName, name, tplComp, catSensor, quantity)
				.get())
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateSensor.class, entity), Status.OK))
				.get();
	}

	/**
	 * Updates the TemplateSensor with all parameters set in the given JSON body of
	 * the request
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param id
	 *            the identifier of the {@link TemplateSensor} to delete.
	 * @param body
	 *            the body of the request containing the attributes to update
	 * @return the updated {@link TemplateSensor}
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID3 + "}")
	public Response update(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String tplRootId, @PathParam(REQUESTPARAM_ID2) String tplCompId,
			@PathParam(REQUESTPARAM_ID3) String id, String body) {
		return ResourceHelper.deserializeJSON(body)
				.map(valueMap -> entityService.update(sourceName, TemplateSensor.class, id, valueMap,
						ContextType.TESTEQUIPMENT, tplRootId, tplCompId))
				// TODO if update returns ??? and entity is Option(none), why is the following
				// map() executed?
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(TemplateSensor.class, entity.get()),
						Status.OK))
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				.get();
	}

	/**
	 * Deletes and returns the deleted {@link TemplateSensor}.
	 * 
	 * 
	 * @param id
	 *            The identifier of the {@link TemplateSensor} to delete.
	 * @return the deleted {@link TemplateSensor }s as {@link Response}
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID3 + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String tplRootId, @PathParam(REQUESTPARAM_ID2) String tplCompId,
			@PathParam(REQUESTPARAM_ID3) String id) {
		return Try
				.of(() -> entityService.delete(sourceName, TemplateSensor.class, id, ContextType.TESTEQUIPMENT,
						tplRootId, tplCompId))
				.onFailure(ServiceUtils.rethrowAsWebApplicationException)
				// TODO add check for result.isPresent()
				.map(result -> ResourceHelper.toResponse(new MDMEntityResponse(TemplateSensor.class, result.get()),
						Status.OK))
				.get();
	}

	/**
	 * Returns the search attributes for the {@link TemplateSensor} type.
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
		return ServiceUtils.buildSearchAttributesResponse(entityService, TemplateSensor.class, sourceName);
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
		return ServiceUtils.buildLocalizationResponse(entityService, TemplateSensor.class, sourceName);
	}
}