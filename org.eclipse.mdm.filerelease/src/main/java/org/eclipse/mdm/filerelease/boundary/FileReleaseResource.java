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

import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.eclipse.mdm.filerelease.entity.FileReleaseRequest;
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
			return FileReleaseUtils.toResponse(fileRelease, Status.OK);
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link FileReleaseService}
	 * 
	 * @param state
	 *            The state of the {@link FileRelease}s to return
	 * @return @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReleases(@QueryParam("status") String state) {	
		try {
			List<FileRelease> list = this.fileReleaseService.getReleases(state);
			return FileReleaseUtils.toResponse(list, Status.OK);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link FileReleaseService}
	 * 
	 * @param state
	 *            The state of the incoming {@link FileRelease}s to return
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/incomming") 
	public Response getIncommingReleases(@QueryParam("status") String state) {
		try {
			List<FileRelease> list = this.fileReleaseService.getIncommingReleases(state);
			return FileReleaseUtils.toResponse(list, Status.OK);
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link FileReleaseService}
	 * 
	 * @param state
	 *            The state of the outgoing {@link FileRelease}s to return
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/outgoing") 
	public Response getOutgoingReleases(@QueryParam("status") String state) {
		try {
			List<FileRelease> list = this.fileReleaseService.getOutgoingReleases(state);
			return FileReleaseUtils.toResponse(list, Status.OK);
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link FileReleaseService}
	 * 
	 * @param request
	 *            The {@link FileReleaseRequest} to create.
	 * @return the result of the delegated request as {@link Response}
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/create")
	public Response create(FileReleaseRequest request) {
		try {
			this.fileReleaseService.create(request);
			return Response.ok().build();
	} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link FileReleaseService}
	 * 
	 * @param identifier
	 *            The identifier of the {@link FileRelease} to approve.
	 * @return the result of the delegated request as {@link Response}
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{IDENTIFIER}/approve()")
	public Response approve(@PathParam("IDENTIFIER") String identifier) {
		try {
			this.fileReleaseService.approve(identifier);
			return Response.ok().build();
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link FileReleaseService}
	 * 
	 * @param identifier
	 *            The identifier of the {@link FileRelease} to reject.
	 * @param message
	 *            The reject message.
	 * @return the result of the delegated request as {@link Response}
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{IDENTIFIER}/reject()")
	public Response reject(@PathParam("IDENTIFIER") String identifier, String message) {
		try {
			this.fileReleaseService.reject(identifier, message);
			return Response.ok().build();
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link FileReleaseService}
	 * 
	 * @param identifier
	 *            The identifier of the {@link FileRelease} to delete.
	 * @return the result of the delegated request as {@link Response}
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{IDENTIFIER}")
	public Response delete(@PathParam("IDENTIFIER") String identifier) {
		try {
			this.fileReleaseService.delete(identifier);
			return Response.ok().build();
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

}
