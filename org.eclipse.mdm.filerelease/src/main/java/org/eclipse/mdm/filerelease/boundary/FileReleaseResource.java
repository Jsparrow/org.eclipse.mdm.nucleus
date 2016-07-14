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
	public Response getReleases(@QueryParam ("direction") String direction, @QueryParam("state") String state) {	
		try {			
	
			List<FileRelease> list = null;
			
			if((direction != null) && direction.equalsIgnoreCase(FileReleaseManager.FILE_RELEASE_DIRECTION_INCOMMING)) {
				list = this.fileReleaseService.getIncommingReleases(state);	
			} else if((direction != null) && direction.equalsIgnoreCase(FileReleaseManager.FILE_RELEASE_DIRECTION_OUTGOING)) {
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
	 * @param request
	 *            The {@link FileReleaseRequest} to create.
	 * @return the result of the delegated request as {@link Response}
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(FileRelease newFileRelease) {
		try {
			this.fileReleaseService.create(newFileRelease);
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
	@Path("/{IDENTIFIER}")
	public Response update(@PathParam("IDENTIFIER") String identifier, FileRelease updatedFileRelease) {
		try {
						
			if(identifier != updatedFileRelease.identifier) {
				throw new WebApplicationException("illegal update post request (identifier is not matching)", Status.FORBIDDEN);
			}
			
			if(updatedFileRelease.state.equalsIgnoreCase(FileReleaseManager.FILE_RELEASE_STATE_APPROVED)) {
				FileRelease fr = this.fileReleaseService.approve(updatedFileRelease);
				return FileReleaseUtils.toResponse(new FileReleaseResponse(fr), Status.OK);
			} else if(updatedFileRelease.state.equalsIgnoreCase(FileReleaseManager.FILE_RELEASE_STATE_REJECTED)) {
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

	//Remove this: Only for Test
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/makedata") 
	public Response getMakeData() {
		try {
			for(int i=0; i<10; i++) {
				FileRelease request = new FileRelease();
				request.sourceName = "MDMTEST01";
				request.typeName = "TestStep";
				request.id = (long)30516;
				request.validity = 5;
				request.orderMessage = "test release " + (i+1);
				request.format = "PAK2RAW";				
				this.fileReleaseService.create(request);
			}
			return Response.ok().build();
		} catch(RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
	
}
