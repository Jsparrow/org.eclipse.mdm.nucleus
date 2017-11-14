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
package org.eclipse.mdm.businessobjects.boundary.utils;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.dflt.model.ValueList;
import org.eclipse.mdm.businessobjects.entity.I18NResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttributeResponse;
import org.eclipse.mdm.businessobjects.service.EntityService;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * Helper class providing methods used by the specific Jersey resource classes.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
public final class ResourceHelper {
	// TODO use logger from caller to preserve the error context
	private static final Logger LOG = LoggerFactory.getLogger(ResourceHelper.class);

	/**
	 * Just hide the default constructor
	 */
	private ResourceHelper() {
	}

	/**
	 * Creates a response holding the localized type and attributes of the given.
	 * entityClass
	 * 
	 * @param entityService
	 *            {@link EntityService} used to get localization data
	 * @param entityClass
	 *            {@link Entity} class to get localization data for
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return
	 */
	public static <T extends Entity> Response createLocalizationResponse(EntityService entityService,
			Class<T> entityClass, String sourceName) {
		// TODO better realize like in createSearchAttributesResponse
		Try<Map<Attribute, String>> localizedAttributeMap = Try
				.of(() -> entityService.localizeAttributes(entityClass, sourceName));
		Try<Map<EntityType, String>> localizedEntityTypeMap = Try
				.of(() -> entityService.localizeType(entityClass, sourceName));

		// TODO what if get() fails?
		return Try.of(() -> ServiceUtils.toResponse(new I18NResponse(localizedEntityTypeMap.get()
				.toJavaMap(),
				localizedAttributeMap.get()
						.toJavaMap()),
				Status.OK))
				// TODO enough to deal with potentially failed Tries at top?
				.getOrElse(Response.status(Status.INTERNAL_SERVER_ERROR)
						.build());
	}

	/**
	 * Return the search attributes for the {@link ValueList} type.
	 * 
	 * @param sourceName
	 *            name of the source (MDM {@link Environment} name)
	 * @return the result of the delegated request as {@link Response}
	 */
	public static <T extends Entity> Response createSearchAttributesResponse(EntityService entityService,
			Class<T> entityClass, String sourceName) {
		return Try.of(() -> entityService.getSearchAttributes(entityClass, sourceName))
				.map(searchAttributes -> ServiceUtils
						.toResponse(new SearchAttributeResponse(searchAttributes.toJavaList()), Status.OK))
				.getOrElse(Response.status(Status.INTERNAL_SERVER_ERROR)
						.build());
	}

	/**
	 * Handles a {@link Throwable} by loggging the exception message and rethrowing
	 * a {@link WebApplicationException}
	 */
	// TODO should be replaced in Resources by buildErrorResponse()
	public static final Consumer<? super Throwable> rethrowAsWebApplicationException = e -> {
		LOG.error(e.getMessage(), e);
		throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
	};

	/**
	 * Handles a {œlink {@link Throwable} by loggging the exception message and
	 * rethrowing a {@link WebApplicationException}
	 */
	public static final Function<? super Throwable, ? extends Response> buildErrorResponse(Status status) {
		return e -> {
			LOG.error(e.getMessage(), e);
			return ServiceUtils.toResponse(e, status);
		};
	}

	/**
	 * Static function to get the {@link ContextType} for the provided name
	 * 
	 * @param contextTypeName
	 *            name of the {@link ContextType}
	 * @return the {@link ContextType} for the given name
	 */
	public static ContextType mapContextType(String contextTypeName) {
		return Stream.of(ContextType.values())
				.filter(contextType -> contextType.name()
						.equals(contextTypeName.toUpperCase()))
				// TODO handle non-mapping ContextType
				.get();
	};

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
	 * @return the updated entity
	 */
	// TODO method should return Try and not Option to indicate that the update was
	// successful
	public static <T extends Entity> Option<T> updateEntityValues(T entity, Map<String, Object> valueMap) {

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
				})
				.onFailure(ResourceHelper.rethrowAsWebApplicationException)
				.toOption();
	}

	/**
	 * Returns a {@link Try<java.util.Map<String, Object>>} of the deserialized JSON
	 * object.
	 * 
	 * @param body
	 *            the JSON body to deserialize
	 * @return {@link Try} of the deserialized body
	 */
	public static Try<Map<String, Object>> deserializeJSON(String body) {
		return Try.of(() -> HashMap.<String, Object>ofAll(
				new ObjectMapper().readValue(body, new TypeReference<java.util.Map<String, Object>>() {
					// TODO correct to use onFailure instead of getOrThrow
				})));

	}
}
