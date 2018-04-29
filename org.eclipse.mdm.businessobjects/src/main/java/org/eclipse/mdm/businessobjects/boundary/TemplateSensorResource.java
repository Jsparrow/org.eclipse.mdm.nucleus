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
import org.eclipse.mdm.api.base.model.Quantity;
import org.eclipse.mdm.api.dflt.model.CatalogSensor;
import org.eclipse.mdm.api.dflt.model.TemplateComponent;
import org.eclipse.mdm.api.dflt.model.TemplateSensor;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.RequestBody;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import io.vavr.Lazy;
import io.vavr.collection.List;

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
		return entityService
				.find(V(sourceName), TemplateSensor.class, V(id), V(ContextType.TESTEQUIPMENT), SL(tplRootId, tplCompId))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Returns the (filtered) {@link TemplateSensor}s.
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
		return entityService
				.find(V(sourceName), TemplateComponent.class, V(tplCompId), V(ContextType.TESTEQUIPMENT), SL(tplRootId))
				.map(tplComp -> List.ofAll(tplComp.getTemplateSensors()))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Returns the created {@link TemplateSensorValue}.
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
		RequestBody requestBody = RequestBody.create(body);

		// TODO anehmer on 2017-11-25: test that tplComp is only once fetched
		// get memoized (Lazy) tplComp as it is used twiced and should only fetched once
		Lazy<TemplateComponent> tplCompSupplier = Lazy.of(() -> entityService
				.find(V(sourceName), TemplateComponent.class, V(tplCompId), V(ContextType.TESTEQUIPMENT), SL(tplRootId))
				.get());

		return entityService
				.create(V(sourceName), TemplateSensor.class,
						L(requestBody.getStringValueSupplier(ENTITYATTRIBUTE_NAME), tplCompSupplier,
						entityService.find(V(sourceName), CatalogSensor.class,
								requestBody.getStringValueSupplier(ENTITYATTRIBUTE_CATALOGSENSOR_ID),
								V(ContextType.TESTEQUIPMENT),
								SL(tplCompSupplier.map(tplComp -> tplComp.getCatalogComponent()
										.getID()))),
						entityService.find(V(sourceName), Quantity.class,
										requestBody.getStringValueSupplier(ENTITYATTRIBUTE_QUANTITY_ID))))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.CREATED))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Updates the {@link TemplateSensor} with all parameters set in the given JSON
	 * body of the request.
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
		RequestBody requestBody = RequestBody.create(body);

		return entityService
				.update(V(sourceName),
						entityService.find(V(sourceName), TemplateSensor.class, V(id),
								V(ContextType.TESTEQUIPMENT), SL(tplRootId, tplCompId)),
						requestBody.getValueMapSupplier())
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Deletes and returns the deleted {@link TemplateSensor}.
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
		return entityService
				.delete(V(sourceName),
						entityService.find(V(sourceName), TemplateSensor.class, V(id),
								V(ContextType.TESTEQUIPMENT), SL(tplRootId, tplCompId)))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Returns the search attributes for the {@link TemplateSensor} type.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the {@link SearchAttribute}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchattributes")
	public Response getSearchAttributes(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName) {
		return ServiceUtils.buildSearchAttributesResponse(V(sourceName), TemplateSensor.class, entityService);
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
		return ServiceUtils.buildLocalizationResponse(V(sourceName), TemplateSensor.class, entityService);
	}
}