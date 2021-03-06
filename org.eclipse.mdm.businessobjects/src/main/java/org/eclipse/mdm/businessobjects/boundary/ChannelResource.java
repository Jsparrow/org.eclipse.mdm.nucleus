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
import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.businessobjects.entity.I18NResponse;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Channel} resource
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Path("/environments/{SOURCENAME}/channels")
public class ChannelResource {

	private static final Logger LOG = LoggerFactory.getLogger(ChannelResource.class);

	@EJB
	private ChannelService channelService;

	/**
	 * delegates the request to the {@link ChannelService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link Channel} result
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getChannels(@PathParam("SOURCENAME") String sourceName, @QueryParam("filter") String filter) {

		try {
			List<Channel> channels = this.channelService.getChannels(sourceName, filter);
			return ServiceUtils.toResponse(new MDMEntityResponse(Channel.class, channels), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link ChannelService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param testId
	 *            id of the {@link Test}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{CHANNEL_ID}")
	public Response getChannel(@PathParam("SOURCENAME") String sourceName,
			@PathParam("CHANNEL_ID") String channelGroupId) {

		try {
			Channel channel = this.channelService.getChannel(sourceName, channelGroupId);
			return ServiceUtils.toResponse(new MDMEntityResponse(Channel.class, channel), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link ChannelService}
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
			Map<Attribute, String> localizedAttributeMap = this.channelService.localizeAttributes(sourceName);
			Map<EntityType, String> localizedEntityTypeMap = this.channelService.localizeType(sourceName);
			return ServiceUtils.toResponse(new I18NResponse(localizedEntityTypeMap, localizedAttributeMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}
