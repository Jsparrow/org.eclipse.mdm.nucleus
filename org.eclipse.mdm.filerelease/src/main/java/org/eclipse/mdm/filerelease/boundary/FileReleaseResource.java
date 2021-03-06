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

package org.eclipse.mdm.filerelease.boundary;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdm.filerelease.control.FileReleaseManager;
import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.eclipse.mdm.filerelease.entity.FileReleaseResponse;
import org.eclipse.mdm.filerelease.utils.FileReleaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link FileRelease} resource
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Path("/filereleases")
public class FileReleaseResource {

	private static final Logger LOG = LoggerFactory.getLogger(FileReleaseResource.class);

	@EJB
	private FileReleaseService fileReleaseService;

	/**
	 * 
	 * delegates the request to the {@link FileReleaseService}
	 * 
	 * @param identifier
	 *            The identifier of the {@link FileRelease}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{IDENTIFIER}")
	public Response getRelease(@PathParam("IDENTIFIER") String identifier) {
		try {
			FileRelease fileRelease = this.fileReleaseService.getRelease(identifier);
			return FileReleaseUtils.toResponse(new FileReleaseResponse(fileRelease), Status.OK);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link FileReleaseService}
	 * 
	 * @param state
	 *            The state of the {@link FileRelease}s to return
	 * @param direction
	 *            The file release direction (incoming or outgoing)
	 * @return @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReleases(@QueryParam("direction") String direction, @QueryParam("state") String state) {
		try {

			List<FileRelease> list = null;

			if ((direction != null)
					&& StringUtils.equalsIgnoreCase(direction, FileReleaseManager.FILE_RELEASE_DIRECTION_INCOMMING)) {
				list = this.fileReleaseService.getIncommingReleases(state);
			} else if ((direction != null)
					&& StringUtils.equalsIgnoreCase(direction, FileReleaseManager.FILE_RELEASE_DIRECTION_OUTGOING)) {
				list = this.fileReleaseService.getOutgoingReleases(state);
			} else {
				list = this.fileReleaseService.getReleases(state);
			}

			return FileReleaseUtils.toResponse(new FileReleaseResponse(list), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link FileReleaseService}
	 * 
	 * @param newFileRelease
	 *            The {@link FileReleaseRequest} to create.
	 * @return the result of the delegated request as {@link Response}
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(FileRelease newFileRelease) {
		try {
			FileRelease fileRelease = this.fileReleaseService.create(newFileRelease);
			return FileReleaseUtils.toResponse(new FileReleaseResponse(fileRelease), Status.OK);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link FileReleaseService}
	 * 
	 * @param identifier
	 *            The identifier of the {@link FileRelease} to update.
	 * @param The
	 *            {@link FileRelease} with updated state
	 * @return the result of the delegated request as {@link Response}
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{IDENTIFIER}")
	public Response update(@PathParam("IDENTIFIER") String identifier, FileRelease updatedFileRelease) {
		try {

			if (!identifier.equals(updatedFileRelease.identifier)) {
				throw new WebApplicationException("illegal update post request (identifier is not matching)",
						Status.FORBIDDEN);
			}

			if (StringUtils.equalsIgnoreCase(updatedFileRelease.state, FileReleaseManager.FILE_RELEASE_STATE_APPROVED)) {
				FileRelease fr = this.fileReleaseService.approve(updatedFileRelease);
				return FileReleaseUtils.toResponse(new FileReleaseResponse(fr), Status.OK);
			} else if (StringUtils.equalsIgnoreCase(updatedFileRelease.state, FileReleaseManager.FILE_RELEASE_STATE_REJECTED)) {
				FileRelease fr = this.fileReleaseService.reject(updatedFileRelease);
				return FileReleaseUtils.toResponse(new FileReleaseResponse(fr), Status.OK);
			}
			String errorMessage = "permission denied: only state updates are allowd "
					+ "(expected stats: RELEASE_APPROVED or RELEASE_REJECTED";
			throw new WebApplicationException(errorMessage, Status.FORBIDDEN);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link FileReleaseService}
	 * 
	 * @param identifier
	 *            The identifier of the {@link FileRelease} to delete.
	 * @return the result of the delegated request as {@link Response} (only OK
	 *         if {@link FileRelease} has been deleted)
	 */
	@DELETE
	@Path("/{IDENTIFIER}")
	public Response delete(@PathParam("IDENTIFIER") String identifier) {
		try {
			this.fileReleaseService.delete(identifier);
			return Response.ok().build();
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

}
