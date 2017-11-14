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

import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.ENTITYATTRIBUTE_NAME;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_ID;
import static org.eclipse.mdm.businessobjects.boundary.ResourceConstants.REQUESTPARAM_SOURCENAME;

import java.util.List;
import java.util.Map;

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

import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.dflt.model.Project;
import org.eclipse.mdm.businessobjects.boundary.utils.ResourceHelper;
import org.eclipse.mdm.businessobjects.entity.I18NResponse;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.entity.SearchAttributeResponse;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.control.Option;
import io.vavr.control.Try;

// TODO: Use entityService (and vavr) in all Methods

/**
 * {@link Project} resource
 * 
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
@Path("/environments/{SOURCENAME}/projects")
public class ProjectResource {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectResource.class);

	@EJB
	private ProjectService projectService;

	@EJB
	private EntityService entityService;
	
	/**
	 * delegates the request to the {@link ProjectService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link Project} result
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProjects(@PathParam("SOURCENAME") String sourceName, @QueryParam("filter") String filter) {
		try {
			List<Project> projects = this.projectService.getProjects(sourceName, filter);
			return ServiceUtils.toResponse(new MDMEntityResponse(Project.class, projects), Status.OK);

		} catch (RuntimeException e) {
			LOG.error("Cannot load Projects!", e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link ProjectService}
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
			List<SearchAttribute> searchAttributes = this.projectService.getSearchAttributes(sourceName);
			return ServiceUtils.toResponse(new SearchAttributeResponse(searchAttributes), Status.OK);
		} catch (RuntimeException e) {
			LOG.error("Cannot load search attributes", e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link ProjectService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param projectId
	 *            id of the {@link Project}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{PROJECT_ID}")
	public Response getProject(@PathParam("SOURCENAME") String sourceName, @PathParam("PROJECT_ID") String projectId) {
		try {
			Project project = this.projectService.getProject(sourceName, projectId);
			return ServiceUtils.toResponse(new MDMEntityResponse(Project.class, project), Status.OK);
		} catch (RuntimeException e) {
			LOG.error("Cannot load project!", e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * delegates the request to the {@link ProjectService}
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
			Map<Attribute, String> localizedAttributeMap = this.projectService.localizeAttributes(sourceName);
			Map<EntityType, String> localizedEntityTypeMap = this.projectService.localizeType(sourceName);
			return ServiceUtils.toResponse(new I18NResponse(localizedEntityTypeMap, localizedAttributeMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error("Cannot load localizations!", e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * creates new {@link Project}
	 * 
	 * @param sourceName
	 * 			name of the source (MDM {@link Environment} name)
	 * @param body
	 * 			The {@link Project} to create
	 * @return
	 * 			The created {@link Project} as {@link Response}
	 */
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName, String body) {
		// deserialize JSON into object map
		@SuppressWarnings("unchecked")
		Map<String, Object> mapping = (Map<String, Object>) Try
				.of(() -> new ObjectMapper().readValue(body, new TypeReference<Map<String, Object>>() {
				}))
				.get();
		
		// read name of Project
		Option<String> name = Try.of(() -> mapping.get(ENTITYATTRIBUTE_NAME)
				.toString())
				.toOption();
				
		return Try.of( () -> this.entityService.create(Project.class, sourceName, name.get())
				.get())
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.map(entity -> ServiceUtils.toResponse(new MDMEntityResponse(Project.class, entity), Status.OK))
				.get();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{" + REQUESTPARAM_ID + "}")
	public Response delete(@PathParam(REQUESTPARAM_SOURCENAME) String sourceName,
			@PathParam(REQUESTPARAM_ID) String id) {
		return Try.of(() -> this.entityService.delete(sourceName, Project.class, id).get())
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.map( entity -> ServiceUtils.toResponse(new MDMEntityResponse(Project.class, entity), Status.OK))
				.get();
	}
	
}
