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

package org.eclipse.mdm.preferences.boundary;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.preferences.controller.PreferenceService;
import org.eclipse.mdm.preferences.entity.PreferenceList;
import org.eclipse.mdm.preferences.entity.PreferenceMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Johannes Stamm, Peak Solution GmbH
 *
 */
@Path("/preferences")
public class PreferenceResource {

	private static final Logger LOG = LoggerFactory.getLogger(PreferenceResource.class);

	@EJB
	private PreferenceService preferenceService;

	/**
	 * delegates the request to the {@link PreferenceService}
	 * 
	 * @param scope
	 *            filter by scope, empty loads all
	 * @param key
	 *            filter by key, empty loads all
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPreference(@QueryParam("scope") String scope, @QueryParam("key") String key) {

		try {
			List<PreferenceMessage> config = this.preferenceService.getPreferences(scope, key);
			return toResponse(new PreferenceList(config), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link PreferenceService}
	 * 
	 * @param source
	 *            filter by source
	 * @param key
	 *            filter by key, empty loads all
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Path("/source")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPreferenceBySource(@QueryParam("source") String source, @QueryParam("key") String key) {

		try {
			List<PreferenceMessage> config = this.preferenceService.getPreferencesBySource(source, key);
			return toResponse(new PreferenceList(config), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link PreferenceService}
	 * 
	 * @param preference
	 *            Configuration to save
	 * @return the result of the delegated request as {@link Response}
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setPreference(PreferenceMessage preference) {

		try {
			return toResponse(this.preferenceService.save(preference), Status.CREATED);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link PreferenceService}
	 * 
	 * @param preference
	 *            Configuration to delete
	 * @return the result of the delegated request as {@link Response}
	 */
	@DELETE
	@Path("/{ID}")
	public Response deletePreference(@PathParam("ID") Long id) {

		try {
			return toResponse(this.preferenceService.deletePreference(id), Status.OK);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * converts the given object to a {@link Response} with the given
	 * {@link Status}
	 *
	 * @param responseEntry
	 *            object to convert
	 * @param status
	 *            {@link Status} of the {@link Response}
	 * @return the created {@link Response}
	 */
	private Response toResponse(Object response, Status status) {
		GenericEntity<Object> genEntity = new GenericEntity<>(response, response.getClass());
		return Response.status(status).entity(genEntity).type(MediaType.APPLICATION_JSON).build();
	}
}
