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

package org.eclipse.mdm.query.boundary;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdm.api.base.ServiceNotProvidedException;
import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.adapter.ModelManager;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.Filter;
import org.eclipse.mdm.api.base.query.Result;
import org.eclipse.mdm.api.base.search.SearchService;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.businessobjects.control.FilterParser;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.connector.boundary.ConnectorServiceException;
import org.eclipse.mdm.property.GlobalProperty;
import org.eclipse.mdm.query.entity.QueryRequest;
import org.eclipse.mdm.query.entity.Row;
import org.eclipse.mdm.query.entity.SuggestionRequest;
import org.eclipse.mdm.query.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
@Stateless
public class QueryService {

	private static final Logger LOG = LoggerFactory.getLogger(QueryService.class);

	@Inject
	@GlobalProperty(value = "businessobjects.query.maxresultspersource")
	private String maxResultsPerSource = "1001";

	@Inject
	ConnectorService connectorService;

	public List<Row> queryRows(QueryRequest request) {
		List<Row> rows = new ArrayList<>();

		request.getFilters().forEach(filter -> {
			try {
				ApplicationContext context = this.connectorService.getContextByName(filter.getSourceName());

				rows.addAll(queryRowsForSource(context, request.getResultType(), request.getColumns(), filter.getFilter(),
						filter.getSearchString()));
			} catch (ConnectorServiceException e) {
				LOG.warn(new StringBuilder().append("Could not retrieve EntityManager for environment '").append(filter.getSourceName()).append("'!").toString(), e);
			} catch (Exception e) {
				LOG.warn(new StringBuilder().append("Could not retrieve query results for environment '").append(filter.getSourceName()).append("': ").append(e.getMessage()).toString(), e);
			}
		});

		return rows;
	}

	public List<String> getSuggestions(SuggestionRequest suggestionRequest) {

		List<String> suggestions = new ArrayList<>();

		suggestionRequest.getSourceNames().forEach(envName -> {

			ApplicationContext context = this.connectorService.getContextByName(envName);
			Optional<ModelManager> mm = context.getModelManager();
			Optional<org.eclipse.mdm.api.base.query.QueryService> qs = context.getQueryService();

			if (mm.isPresent() && qs.isPresent()) {

				try {
					EntityType entityType = mm.get().getEntityType(suggestionRequest.getType());

					Attribute attr = entityType.getAttribute(suggestionRequest.getAttrName());

					suggestions.addAll(qs.get().createQuery().select(attr).group(attr).fetch().stream()
							.map(r -> Objects.toString(r.getValue(attr).extract())).collect(Collectors.toList()));

				} catch (DataAccessException | IllegalArgumentException e) {
					LOG.warn(new StringBuilder().append("Cannot retreive suggestions ").append(suggestionRequest).append(" for Environment + ").append(envName).append("!").toString(),
							e);
				}
			}
		});
		return suggestions;
	}

	List<Row> queryRowsForSource(ApplicationContext context, String resultEntity, List<String> columns, String filterString,
			String searchString) {


		ModelManager modelManager = context.getModelManager()
				.orElseThrow(() -> new ServiceNotProvidedException(ModelManager.class));

		SearchService searchService = context.getSearchService()
				.orElseThrow(() -> new IllegalStateException("neccessary SearchService is not available"));

		Class<? extends Entity> resultType = getEntityClassByNameType(searchService, resultEntity);

		List<EntityType> searchableTypes = searchService.listEntityTypes(resultType);
		List<Attribute> attributes = columns.stream().map(c -> getAttribute(searchableTypes, c))
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

		Filter filter = FilterParser.parseFilterString(searchableTypes, filterString);

		List<Result> result = searchService.fetchResults(resultType, attributes, filter, searchString);

		return Util.convertResultList(result.subList(0, Math.min(result.size(), getMaxResultsPerSource())), resultType,
				modelManager.getEntityType(resultType));
	}

	private Optional<Attribute> getAttribute(List<EntityType> searchableTypes, String c) {
		String[] parts = c.split("\\.");

		if (parts.length != 2) {
			throw new IllegalArgumentException(new StringBuilder().append("Cannot parse attribute ").append(c).append("!").toString());
		}

		String type = parts[0];
		String attributeName = parts[1];

		Optional<EntityType> entityType = searchableTypes.stream()
				.filter(e -> StringUtils.equalsIgnoreCase(ServiceUtils.workaroundForTypeMapping(e), type)).findFirst();

		if (entityType.isPresent()) {
			return entityType.get().getAttributes().stream().filter(a -> StringUtils.equalsIgnoreCase(a.getName(), attributeName))
					.findFirst();
		} else {
			return Optional.empty();
		}
	}

	private Class<? extends Entity> getEntityClassByNameType(SearchService s, String name) {

		for (Class<? extends Entity> entityClass : s.listSearchableTypes()) {
			if (StringUtils.equalsIgnoreCase(entityClass.getSimpleName(), name)) {
				return entityClass;
			}
		}
		throw new IllegalArgumentException(new StringBuilder().append("Invalid Entity '").append(name).append("'. Allowed values are: ").append(s.listSearchableTypes().stream().map(Class::getSimpleName).collect(Collectors.joining(", "))).toString());
	}

	private int getMaxResultsPerSource() {
		try {
			return Integer.parseInt(maxResultsPerSource);
		} catch (NumberFormatException e) {
			return 1001;
		}
	}

}
