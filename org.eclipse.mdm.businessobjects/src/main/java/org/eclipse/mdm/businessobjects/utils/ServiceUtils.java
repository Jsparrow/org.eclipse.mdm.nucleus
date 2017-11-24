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

package org.eclipse.mdm.businessobjects.utils;

import java.util.function.Function;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.api.base.ServiceNotProvidedException;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.adapter.ModelManager;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.dflt.model.ValueList;
import org.eclipse.mdm.businessobjects.entity.I18NResponse;
import org.eclipse.mdm.businessobjects.entity.MDMEntityResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttributeResponse;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.slf4j.LoggerFactory;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.control.Try;

public final class ServiceUtils {

	/**
	 * returns true if the given filter String is a parent filter of the given
	 * parent type
	 *
	 * @param em
	 *            {@link EntityManager} of the data source
	 * @param filter
	 *            parent filter string to check
	 * @param parentType
	 *            class of the parent entity
	 * @return true if the give filter String is a parent filter
	 */
	public static boolean isParentFilter(ApplicationContext context, String filter,
			Class<? extends Entity> parentType) {
		ModelManager mm = context.getModelManager()
				.orElseThrow(() -> new ServiceNotProvidedException(ModelManager.class));
		EntityType et = mm.getEntityType(parentType);

		String idAttributeName = et.getIDAttribute()
				.getName();
		String matcher = workaroundForTypeMapping(et) + "." + idAttributeName + " eq (\\w+)";
		return filter.matches(matcher);
	}

	/**
	 * returns the business object ID from a parent filter
	 *
	 * @param em
	 *            {@link EntityManager} of the data source
	 * @param filter
	 *            parent filter string
	 * @param parentType
	 *            parent type to identify the Id attribute name
	 * @return the extracted business object Id
	 */
	public static String extactIdFromParentFilter(ApplicationContext context, String filter,
			Class<? extends Entity> parentType) {
		ModelManager mm = context.getModelManager()
				.orElseThrow(() -> new ServiceNotProvidedException(ModelManager.class));
		EntityType et = mm.getEntityType(parentType);

		String idAttributeName = et.getIDAttribute()
				.getName();
		return filter.replace(workaroundForTypeMapping(et) + "." + idAttributeName + " eq ", "");
	}

	/**
	 * Simple workaround for naming mismatch between Adapter and Business object
	 * names.
	 * 
	 * @param entityType
	 *            entity type
	 * @return MDM business object name
	 */
	public static String workaroundForTypeMapping(EntityType entityType) {
		switch (entityType.getName()) {
		case "StructureLevel":
			return "Pool";
		case "MeaResult":
			return "Measurement";
		case "SubMatrix":
			return "ChannelGroup";
		case "MeaQuantity":
			return "Channel";
		default:
			return entityType.getName();
		}
	}

	/**
	 * Builds {@Link Response} from given {@link Entity}
	 * 
	 * @param entity
	 *            {@link Entity} to build {@link Response} from
	 * @return the build {@link Response}
	 */
	public static <T extends Entity> Response buildEntityResponse(T entity, Status status) {
		if (entity != null) {
			MDMEntityResponse response = new MDMEntityResponse(entity.getClass(), entity);
			GenericEntity<Object> genEntity = new GenericEntity<Object>(response, response.getClass());
			return Response.status(status)
					.entity(genEntity)
					.type(MediaType.APPLICATION_JSON)
					.build();
		} else {
			return Response.status(Status.NO_CONTENT)
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
	}

	/**
	 * Builds {@Link Response} from given {@link Entity}
	 * 
	 * @param entity
	 *            {@link Entity} to build {@link Response} from
	 * @return the build {@link Response}
	 */
	public static <T extends Entity> Response buildEntityResponse(List<T> entities, Status status) {
		if (entities.nonEmpty()) {
			@SuppressWarnings("unchecked")
			Class<T> entityClass = (Class<T>) entities.get()
					.getClass();
			MDMEntityResponse response = new MDMEntityResponse(entityClass, entities.asJava());
			GenericEntity<Object> genEntity = new GenericEntity<Object>(response, response.getClass());
			return Response.status(status)
					.entity(genEntity)
					.type(MediaType.APPLICATION_JSON)
					.build();
		} else {
			return Response.status(Status.NO_CONTENT)
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
	}

	/**
	 * Builds {@Link Response} from given {@link Entity}
	 * 
	 * @param entity
	 *            {@link Entity} to build {@link Response} from
	 * @return the build {@link Response}
	 */
	public static <T extends Entity> Response buildErrorResponse(Throwable t, Status status) {
		return Response.status(status)
				.entity(t)
				.type(MediaType.APPLICATION_JSON)
				.build();
	}

	/**
	 * Converts the given object to a {@link Response} with the given {@link Status}
	 *
	 * @param response
	 *            object to convert
	 * @param status
	 *            {@link Status} of the {@link Response}
	 * @return the created {@link Response}
	 */
	public static Response toResponse(Object response, Status status) {
		GenericEntity<Object> genEntity = new GenericEntity<Object>(response, response.getClass());
		return Response.status(status)
				.entity(genEntity)
				.type(MediaType.APPLICATION_JSON)
				.build();
	}

	/**
	 * Return the search attributes for the {@link ValueList} type.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the result of the delegated request as {@link Response}
	 */
	public static <T extends Entity> Response buildSearchAttributesResponse(EntityService entityService,
			Class<T> entityClass, String sourceName) {
		return entityService.getSearchAttributesSupplier(sourceName, entityClass)
				.map(searchAttributes -> ServiceUtils
						.toResponse(new SearchAttributeResponse(searchAttributes.toJavaList()), Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * Return the localized type and attributes for the {@link Entity} type.
	 * 
	 * @param entityService
	 *            {@link EntityService} used to get localization data
	 * @param entityClass
	 *            {@link Entity} class to get localization data for
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the {@link Response} with the localized data
	 */
	public static <T extends Entity> Response buildLocalizationResponse(EntityService entityService,
			Class<T> entityClass, String sourceName) {
		return Try.of(() -> ServiceUtils.toResponse(new I18NResponse(entityService.getLocalizeTypeSupplier(sourceName, entityClass)
				.get()
				.toJavaMap(),
				entityService.getLocalizeAttributesSupplier(sourceName, entityClass)
						.get()
						.toJavaMap()),
				Status.OK))
				.recover(ServiceUtils.ERROR_RESPONSE_SUPPLIER)
				.getOrElse(ServiceUtils.SERVER_ERROR_RESPONSE);
	}

	/**
	 * A Response representing a server error.
	 */
	public final static Response SERVER_ERROR_RESPONSE = Response.serverError()
			.build();

	/**
	 * Builds an error response based on an exception to be sent to the client
	 */
	public static final Function<? super Throwable, Response> ERROR_RESPONSE_SUPPLIER = (e) -> {
		LoggerFactory.getLogger(ServiceUtils.class)
				.error(e.getMessage(), e);
		// TODO anehmer on 2017-11-22: customize status according to exception
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(e.getStackTrace()[0].getClassName() + "." + e.getStackTrace()[0].getMethodName() + ": "
						+ e.getMessage())
				.type(MediaType.APPLICATION_JSON)
				.build();
	};

	/**
	 * Returns a {@link Try} to get the {@link ContextType} for the provided name
	 * 
	 * @param contextTypeName
	 *            name of the {@link ContextType}
	 * @return a {@link Try} of the {@link ContextType} for the given name
	 */
	public static Try<ContextType> getContextTypeSupplier(String contextTypeName) {
		return Stream.of(ContextType.values())
				.filter(contextType -> contextType.name()
						.equals(contextTypeName.toUpperCase()))
				.toTry();
	}

	/**
	 * Updates the given {@link Entity} with the values from the given valueMap. All
	 * matching attributes are updated, whereas attribute matching is case sensitve
	 * (the data model attribute name is the reference).
	 * 
	 * @param entity
	 *            the entity to update
	 * @param valueMap
	 *            values to update the entity with according to matching attribute
	 *            names. The keys are compared case sensitive.
	 * @return a {@link Try} with the the updated entity
	 */
	public static <T extends Entity> Try<T> updateEntityValues(T entity, Map<String, Object> valueMap) {
		// update all entity values with values from the valueMap
		return Try.of(() -> entity)
				.map(e -> {
					// TODO Test: what happens, if an attribute is missing?
					// iterate all key-values of entity
					HashMap.ofAll(e.getValues())
							// get for each the corresponding update value
							.forEach((name, value) -> valueMap.get(name)
									// set the update value
									.peek(mapValue -> value.set(mapValue)));
					return e;
				});
	}
}
