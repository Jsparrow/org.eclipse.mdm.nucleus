/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/

package org.eclipse.mdm.businessobjects.boundary;

import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID2;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID3;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID4;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_SOURCENAME;
import static org.eclipse.mdm.businessobjects.service.EntityService.SL;
import static org.eclipse.mdm.businessobjects.service.EntityService.V;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import org.eclipse.mdm.api.dflt.model.TemplateAttribute;
import org.eclipse.mdm.api.dflt.model.TemplateComponent;
import org.eclipse.mdm.api.dflt.model.TemplateRoot;
import org.eclipse.mdm.api.dflt.model.TemplateSensor;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.RequestBody;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import io.vavr.collection.List;

/**
 * {@link TemplateAttribute} resource handling REST requests
 * 
 * @author Philipp Schweinbenz, science+computing AG Tuebingen (Atos SE)
 *
 */
@Path("/environments/{" + REQUESTPARAM_SOURCENAME + "}/tplroots/testequipment/{" + REQUESTPARAM_ID + "}/tplcomps/{"
		+ REQUESTPARAM_ID2 + "}/tplsensors/{" + REQUESTPARAM_ID3 + "}/tplsensorattrs")
public class TemplateSensorAttributeResource {

	@EJB
	private EntityService entityService;

	/*
	 * Create not implemented as TemplateSensorAttributes are created implicitly
	 * with the TemplateSensor
	 */

	/**
	 * Returns the found {@link TemplateAttribute}.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param tplRootId
	 *            id of the {@link TemplateRoot}
	 * @param tplCompId
	 *            id of the {@link TemplateComponent}
	 * @param tplSensorId
	 *            id of the {@link TemplateSensor}
	 * @param id
	 *            id of the {@link TemplateAttribute}
	 * @return the found {@link TemplateAttribute} as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID4 + "}")
	public Response find(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String tplRootId, @PathParam(REQUESTPARAM_ID2) String tplCompId,
			@PathParam(REQUESTPARAM_ID3) String tplSensorId, @PathParam(REQUESTPARAM_ID4) String id) {
		return entityService
				.find(V(sourceName), TemplateAttribute.class, V(id), V(ContextType.TESTEQUIPMENT),
						SL(tplRootId, tplCompId, tplSensorId))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Returns the (filtered) {@link TemplateAttribute}s.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param tplRootId
	 *            id of the {@link TemplateRoot}
	 * @param tplCompId
	 *            id of the {@link TemplateComponent}
	 * @param tplSensorId
	 *            id of the {@link TemplateSensor}
	 * @param filter
	 *            filter string to filter the {@link TemplateAttribute} result
	 * @return the (filtered) {@link TemplateAttribute}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String tplRootId, @PathParam(REQUESTPARAM_ID2) String tplCompId,
			@PathParam(REQUESTPARAM_ID3) String tplSensorId, @QueryParam("filter") String filter) {
		return entityService
				.find(V(sourceName), TemplateSensor.class, V(tplSensorId),
						V(ContextType.TESTEQUIPMENT), SL(tplRootId, tplCompId))
				.map(tplSensor -> List.ofAll(tplSensor.getTemplateAttributes()))
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Updates the {@link TemplateAttribute} with all parameters set in the given
	 * JSON body of the request
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param tplRootId
	 *            id of the {@link TemplateRoot}
	 * @param tplCompId
	 *            id of the {@link TemplateComponent}
	 * @param tplSensorId
	 *            id of the {@link TemplateSensor}
	 * @param id
	 *            the identifier of the {@link TemplateAttribute} to delete.
	 * @param body
	 *            the body of the request containing the attributes to update
	 * @return the updated {@link TemplateAttribute}
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID4 + "}")
	public Response update(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String tplRootId, @PathParam(REQUESTPARAM_ID2) String tplCompId,
			@PathParam(REQUESTPARAM_ID3) String tplSensorId, @PathParam(REQUESTPARAM_ID4) String id, String body) {
		RequestBody requestBody = RequestBody.create(body);

		return entityService
				.update(V(sourceName),
						entityService.find(V(sourceName), TemplateAttribute.class, V(id), V(ContextType.TESTEQUIPMENT),
								SL(tplRootId, tplCompId, tplSensorId)),
						requestBody.getValueMapSupplier())
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/*
	 * Delete not implemented as TemplateSensorAttributes can't be deleted
	 */

	/**
	 * Returns the search attributes for the {@link TemplateAttribute} type.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the {@link SearchAttribute}s as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchattributes")
	public Response getSearchAttributes(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName) {
		return ServiceUtils.buildSearchAttributesResponse(V(sourceName), TemplateAttribute.class, entityService);
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
		return ServiceUtils.buildLocalizationResponse(V(sourceName), TemplateAttribute.class, entityService);
	}
}
