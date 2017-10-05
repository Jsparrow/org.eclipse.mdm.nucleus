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
package org.eclipse.mdm.businessobjects.utils;

import java.util.Map;
import java.util.function.Consumer;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.dflt.model.ValueList;
import org.eclipse.mdm.businessobjects.boundary.EntityService;
import org.eclipse.mdm.businessobjects.entity.I18NResponse;
import org.eclipse.mdm.businessobjects.entity.SearchAttributeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.collection.Stream;
import io.vavr.control.Try;

/**
 * Helper class providing methods used by the specific Jersey response classes.
 * 
 * @author Alexander Nehmer, science+computing AG Tuebingen (Atos SE)
 *
 */
public final class ResourceHelper {
	// TODO use logger from caller to preserve the error context
	private static final Logger LOG = LoggerFactory.getLogger(ResourceHelper.class);

	/**
	 * Parameter name holding the {@link Environment}, i.e. the source name
	 */
	public static final String REQUESTPARAM_SOURCENAME = "SOURCENAME";

	/**
	 * Parameter holding the {@link Entity}s id in the URI path
	 */
	public static final String REQUESTPARAM_ID = "ID";

	/**
	 * Parameter holding the {@link ContextType} of the {@link Entity} in the URI
	 * path
	 */
	public static final String REQUESTPARAM_CONTEXTTYPE = "CONTEXTTYPE";

	/**
	 * Parameter holding the name of the {@link Entity} in the request body
	 */
	public static final String ENTITYATTRIBUTE_NAME = "name";

	/**
	 * Parameter holding the {@link ValueType} of the {@link Entity} in the request
	 * body
	 */
	public static final String ENTITYATTRIBUTE_VALUETYPE = "valuetype";

	/**
	 * * Just hide the default constructor
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

		return Try
				.of(() -> ServiceUtils.toResponse(
						new I18NResponse(localizedEntityTypeMap.get(), localizedAttributeMap.get()), Status.OK))
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
				.map(searchAttributes -> ServiceUtils.toResponse(new SearchAttributeResponse(searchAttributes),
						Status.OK))
				.getOrElse(Response.status(Status.INTERNAL_SERVER_ERROR)
						.build());
	}

	/**
	 * Handles a {œlink {@link Throwable} by loggging the exception message and
	 * rethrowing a {@link WebApplicationException}
	 */
	public static final Consumer<? super Throwable> rethrowException = e -> {
		LOG.error(e.getMessage(), e);
		throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
	};

	/**
	 * Static function to get the {@link ContextType} for the provided name
	 */
	public static final ContextType mapContextType(String contextTypeName) {
		return Stream.of(ContextType.values())
				.filter(contextType -> contextType.name()
						.equals(contextTypeName.toUpperCase()))
				// TODO handle non-mapping ContextType
				.get();
	};
}
