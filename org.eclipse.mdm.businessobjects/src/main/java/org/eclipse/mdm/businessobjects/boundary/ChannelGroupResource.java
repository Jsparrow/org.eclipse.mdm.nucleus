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

import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.businessobjects.entity.I18NResponse;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ChannelGroup} resource
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Path("/environments/{SOURCENAME}/channelgroups")
public class ChannelGroupResource {

	private static final Logger LOG = LoggerFactory.getLogger(ChannelGroupResource.class);

	@EJB
	private ChannelGroupService channelGroupService;

	/**
	 * delegates the request to the {@link ChannelGroupService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link ChannelGroup} result
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getChannelGroups(@PathParam("SOURCENAME") String sourceName, @QueryParam("filter") String filter) {

		try {
			List<ChannelGroup> channelGroups = this.channelGroupService.getChannelGroups(sourceName, filter);
			return ServiceUtils.toResponse(new MDMEntityResponse(ChannelGroup.class, channelGroups), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * returns a {@link ChannelGroup} identified by the given id.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param channelId
	 *            id of the {@link ChannelGroup}
	 * @return the matching {@link ChannelGroup}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{CHANNELGROUP_ID}")
	public Response getChannelGroup(@PathParam("SOURCENAME") String sourceName,
			@PathParam("CHANNELGROUP_ID") String channelGroupId) {

		try {
			ChannelGroup channelGroup = this.channelGroupService.getChannelGroup(sourceName, channelGroupId);
			return ServiceUtils.toResponse(new MDMEntityResponse(ChannelGroup.class, channelGroup), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link ChannelGroupService}
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
			Map<Attribute, String> localizedAttributeMap = this.channelGroupService.localizeAttributes(sourceName);
			Map<EntityType, String> localizedEntityTypeMap = this.channelGroupService.localizeType(sourceName);
			return ServiceUtils.toResponse(new I18NResponse(localizedEntityTypeMap, localizedAttributeMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}
