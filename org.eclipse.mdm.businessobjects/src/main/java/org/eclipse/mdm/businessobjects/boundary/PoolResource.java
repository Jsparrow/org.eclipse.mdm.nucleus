/*******************************************************************************
  * Copyright (c) 2017 Peak Solution GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Matthias Koller - initial implementation
  *******************************************************************************/
package org.eclipse.mdm.businessobjects.boundary;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.dflt.model.Pool;
import org.eclipse.mdm.businessobjects.entity.I18NResponse;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.entity.SearchAttributeResponse;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Pool} resource
 * 
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
@Path("/environments/{SOURCENAME}/pools")
public class PoolResource {

	private static final Logger LOG = LoggerFactory.getLogger(PoolResource.class);

	@EJB
	private PoolService poolService;

	/**
	 * delegates the request to the {@link PoolService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link Pool} result
	 * @return the result of the delegated request as {@link Response}
	 */

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPools(@PathParam("SOURCENAME") String sourceName, @QueryParam("filter") String filter) {
		try {
			List<Pool> pools = this.poolService.getPools(sourceName, filter);
			return ServiceUtils.toResponse(new MDMEntityResponse(Pool.class, pools), Status.OK);

		} catch (RuntimeException e) {
			LOG.error("Cannot load pools!", e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link PoolService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchattributes")
	public Response getSearchAttributes(@PathParam("SOURCENAME") String sourceName) {
		try {
			List<SearchAttribute> searchAttributes = this.poolService.getSearchAttributes(sourceName);
			return ServiceUtils.toResponse(new SearchAttributeResponse(searchAttributes), Status.OK);
		} catch (RuntimeException e) {
			LOG.error("Cannot load search attributes!", e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link PoolService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param poolId
	 *            id of the {@link Pool}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{POOL_ID}")
	public Response getPool(@PathParam("SOURCENAME") String sourceName, @PathParam("POOL_ID") long poolId) {
		try {
			Pool pool = this.poolService.getPool(sourceName, poolId);
			return ServiceUtils.toResponse(new MDMEntityResponse(Pool.class, pool), Status.OK);
		} catch (RuntimeException e) {
			LOG.error("Cannot load pools!", e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link PoolService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/localizations")
	public Response localize(@PathParam("SOURCENAME") String sourceName) {

		try {
			Map<Attribute, String> localizedAttributeMap = this.poolService.localizeAttributes(sourceName);
			Map<EntityType, String> localizedEntityTypeMap = this.poolService.localizeType(sourceName);
			return ServiceUtils.toResponse(new I18NResponse(localizedEntityTypeMap, localizedAttributeMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error("Cannot load localizations!", e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}
