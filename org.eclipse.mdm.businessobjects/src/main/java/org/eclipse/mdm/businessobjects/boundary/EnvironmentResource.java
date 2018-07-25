/*******************************************************************************
  * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Sebastian Dirsch - initial implementation
  *******************************************************************************/

package org.eclipse.mdm.businessobjects.boundary;

import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_SOURCENAME;
import static org.eclipse.mdm.businessobjects.service.EntityService.V;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.businessobjects.entity.I18NResponse;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.RequestBody;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Try;

/**
 * {@link Environment} resource
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Path("/environments")
public class EnvironmentResource {

	private static final Logger LOG = LoggerFactory.getLogger(EnvironmentResource.class);

	@EJB
	private EnvironmentService environmentService;

	@EJB
	private EntityService entityService;

	/**
	 * delegates the request to the {@link EnvironmentService}
	 *
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEnvironments() {
		try {
			List<Environment> environments = this.environmentService.getEnvironments();
			return ServiceUtils.toResponse(new MDMEntityResponse(Environment.class, environments), Status.OK);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link EnvironmentService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_SOURCENAME + "}")
	public Response getEnvironment(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName) {
		try {
			Environment environment = this.environmentService.getEnvironment(sourceName);
			return ServiceUtils.toResponse(new MDMEntityResponse(Environment.class, environment), Status.OK);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Updates the {@link Environment} with all parameters set in the given JSON
	 * body of the request.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param body
	 *            the body of the request containing the attributes to update
	 * @return the updated {@link Environment}
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_SOURCENAME + "}")
	public Response update(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, String body) {
		RequestBody requestBody = RequestBody.create(body);

		return entityService
				.update(V(sourceName), Try.of(() -> this.environmentService.getEnvironment(sourceName)),
						requestBody.getValueMapSupplier())
				.map(e -> ServiceUtils.buildEntityResponse(e, Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * delegates the request to the {@link EnvironmentService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_SOURCENAME + "}/localizations")
	public Response localize(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, @QueryParam("all") boolean all) {

		try {

			if (all) {
				Map<Attribute, String> localizedAttributeMap = this.environmentService
						.localizeAllAttributes(sourceName);
				Map<EntityType, String> localizedEntityTypeMap = this.environmentService.localizeAllTypes(sourceName);
				return ServiceUtils.toResponse(new I18NResponse(localizedEntityTypeMap, localizedAttributeMap),
						Status.OK);
			}

			Map<Attribute, String> localizedAttributeMap = this.environmentService.localizeAttributes(sourceName);
			Map<EntityType, String> localizedEntityTypeMap = this.environmentService.localizeType(sourceName);
			return ServiceUtils.toResponse(new I18NResponse(localizedEntityTypeMap, localizedAttributeMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_SOURCENAME + "}/search")
	public Response search(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, @QueryParam("q") String query) {
		List<Entity> searchResults = environmentService.search(sourceName, query);
		return ServiceUtils.toResponse(new MDMEntityResponse(Environment.class, searchResults), Status.OK);
	}

}
