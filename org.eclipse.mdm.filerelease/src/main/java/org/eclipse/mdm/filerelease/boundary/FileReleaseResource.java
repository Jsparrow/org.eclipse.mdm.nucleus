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


@Path("/filereleases")
public class FileReleaseResource {

	private static final Logger LOG = LoggerFactory.getLogger(FileReleaseResource.class); 
	
	@EJB
	private FileReleaseService fileReleaseService;
	
	
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
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReleases(@QueryParam("status") String state) {	
		try {
			List<FileRelease> list = this.fileReleaseService.getReleases(state);
			return FileReleaseUtils.toResponse(list, Status.OK);
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
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
	
	@GET
	@Path("/asdf")
	@Produces(MediaType.APPLICATION_JSON)
	public FileReleaseRequest getFileReleaseRequest() {
		FileReleaseRequest reReq = new FileReleaseRequest();
		return reReq;
	}
	
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
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{IDENTIFIER}/approve()")
	public Response approve(@PathParam("IDENTIFIER") String identifier) {
		try {
			this.fileReleaseService.approve(identifier);
			return Response.ok().build();
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	
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
	
	
	
	
	/* -------------------------------------------------------- TEST Methods ----------------------------------------------*/
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/create/{SOURCENAME}/{ID}/{CONVERTER}") 
	public Response testCreate(@PathParam("SOURCENAME") String sourceName, @PathParam("ID") long id, @PathParam("CONVERTER") String converter) {
		try {
			FileReleaseRequest request = new FileReleaseRequest();
			request.sourceName = sourceName;
			request.typeName = "TestStep";
			request.id = id;
			request.format = converter;
			request.message= "here IAM a new FileRelease request";
			request.validity = 5;
			this.fileReleaseService.create(request);
			return Response.ok().build();
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
 	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{IDENTIFIER}/delete/") 
	public Response testDelete(@PathParam("IDENTIFIER") String identifier) {
		try {			
			this.fileReleaseService.delete(identifier);			
			return Response.ok().build();
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{IDENTIFIER}/approve/") 
	public Response testApprove(@PathParam("IDENTIFIER") String identifier) {
		try {			
			this.fileReleaseService.approve(identifier);			
			return Response.ok().build();
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{IDENTIFIER}/reject/") 
	public Response testReject(@PathParam("IDENTIFIER") String identifier) {
		try {			
			this.fileReleaseService.reject(identifier, "rejected");			
			return Response.ok().build();
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
	
}
