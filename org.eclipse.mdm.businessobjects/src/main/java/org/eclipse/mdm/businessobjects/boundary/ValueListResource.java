/*******************************************************************************
 * Copyright (c) 2017 science + computing AG Tuebingen (ATOS SE)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Nehmer - initial implementation
 *******************************************************************************/
package org.eclipse.mdm.businessobjects.boundary;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.dflt.model.ValueList;
import org.eclipse.mdm.businessobjects.entity.I18NResponse;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.entity.SearchAttributeResponse;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@link ValueList} resource
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
@Path("/environments/{" + EnvironmentResource.SOURCENAME_PARAM + "}/valuelists")
public class ValueListResource {

	private static final Logger LOG = LoggerFactory.getLogger(ValueListResource.class);
	private static final String NAME_PARAM = "name";

	@EJB
	private ValueListService valueListService;

	@EJB
	private EntityService entityService;

	/**
	 * Delegates the getValueList-request to the {@link ValueListService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param valueListId
	 *            id of the {@link ValueList}
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{ID}")
	public Response getValueList(@PathParam(EnvironmentResource.SOURCENAME_PARAM) String sourceName,
			@PathParam("ID") String id) {
		try {
			Optional<ValueList> valueList = this.entityService.find(ValueList.class, sourceName, id);

			// return ValueList representation
			if (valueList.isPresent()) {
				return ServiceUtils.toResponse(new MDMEntityResponse(ValueList.class, valueList.get()), Status.OK);
			} else {
				LOG.error("ValueList could not be created.");
				return Response.serverError().status(Status.NOT_MODIFIED).build();
			}
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Delegates the getValueLists-request to the {@link ValueListService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @param filter
	 *            filter string to filter the {@link ValueList} result
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getValueLists(@PathParam(EnvironmentResource.SOURCENAME_PARAM) String sourceName,
			@QueryParam("filter") String filter) {
		try {
			Optional<List<ValueList>> valueLists = this.entityService.findAll(ValueList.class, sourceName, filter);

			// return representation of ValueLists
			if (valueLists.isPresent()) {
				return ServiceUtils.toResponse(new MDMEntityResponse(ValueList.class, valueLists.get()), Status.OK);
			} else {
				LOG.error("ValueList could not be created.");
				return Response.serverError().status(Status.NOT_MODIFIED).build();
			}
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Delegates the create-request to the {@link ValueListService}
	 * 
	 * @param newValueList
	 *            The {@link ValueList} to create.
	 * @return the result of the delegated request as {@link Response}
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam(EnvironmentResource.SOURCENAME_PARAM) String sourceName, String body) {
		try {
			Optional<ValueList> valueList = Optional.empty();

			// TODO move deser code to EntityService
			// deserialize JSON into object map
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> objectValueMap = mapper.readValue(body, new TypeReference<Map<String, Object>>() {
			});

			// get name from object map
			Optional<Object> valueListName = Optional.ofNullable(objectValueMap.get(NAME_PARAM));
			System.out.println("HAKKK");
			// create ValueList if name is given
			valueList = valueListName.map(name -> entityService.create(ValueList.class, sourceName, name.toString()))
					.map(wrappedValueList -> wrappedValueList.get());

			// return ValueList representation if created
			if (valueList.isPresent()) {
				return ServiceUtils.toResponse(new MDMEntityResponse(ValueList.class, valueList.get()), Status.OK);
			} else {
				LOG.error("ValueList could not be created.");
				return Response.serverError().status(Status.NOT_MODIFIED).build();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			System.out.println();
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Delegates the delete-request to the {@link ValueListService}
	 * 
	 * @param identifier
	 *            The identifier of the {@link ValueList} to delete.
	 * @return the result of the delegated request as {@link Response} (only OK if
	 *         {@link ValueList} has been deleted)
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{ID}")
	public Response delete(@PathParam(EnvironmentResource.SOURCENAME_PARAM) String sourceName,
			@PathParam("ID") String id) {
		try {
			Optional<ValueList> valueList = this.entityService.delete(ValueList.class, sourceName, id);

			// return ValueList representation if it was deleted
			if (valueList.isPresent()) {
				return ServiceUtils.toResponse(new MDMEntityResponse(ValueList.class, valueList.get()), Status.OK);
			} else {
				LOG.error("ValueList could not be deleted.");
				return Response.serverError().status(Status.NOT_MODIFIED).build();
			}
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Delegates the request to the {@link ValueListService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchattributes")
	public Response getSearchAttributes(@PathParam(EnvironmentResource.SOURCENAME_PARAM) String sourceName) {
		try {
			List<SearchAttribute> searchAttributes = this.valueListService.getSearchAttributes(sourceName);
			return ServiceUtils.toResponse(new SearchAttributeResponse(searchAttributes), Status.OK);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Delegates the localize-request to the {@link ValueListService}
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the result of the delegated request as {@link Response}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/localizations")
	public Response localize(@PathParam(EnvironmentResource.SOURCENAME_PARAM) String sourceName) {

		try {
			Map<Attribute, String> localizedAttributeMap = this.valueListService.localizeAttributes(sourceName);
			Map<EntityType, String> localizedEntityTypeMap = this.valueListService.localizeType(sourceName);
			return ServiceUtils.toResponse(new I18NResponse(localizedEntityTypeMap, localizedAttributeMap), Status.OK);

		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}