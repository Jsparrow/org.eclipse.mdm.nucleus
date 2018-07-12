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
package org.eclipse.mdm.query.boundary;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

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
import org.eclipse.mdm.query.entity.SourceFilter;
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

		for (SourceFilter filter : request.getFilters()) {
			try {
				ApplicationContext context = this.connectorService.getContextByName(filter.getSourceName());

				rows.addAll(queryRowsForSource(context, request.getResultType(), request.getColumns(), filter.getFilter(),
						filter.getSearchString()));
			} catch (ConnectorServiceException e) {
				LOG.warn("Could not retrieve EntityManager for environment '" + filter.getSourceName() + "'!", e);
			} catch (Exception e) {
				LOG.warn("Could not retrieve query results for environment '" + filter.getSourceName() + "': "
						+ e.getMessage(), e);
			}
		}

		return rows;
	}

	public List<String> getSuggestions(SuggestionRequest suggestionRequest) {

		List<String> suggestions = new ArrayList<>();

		for (String envName : suggestionRequest.getSourceNames()) {

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
					LOG.warn("Cannot retreive suggestions " + suggestionRequest + " for Environment + " + envName + "!",
							e);
				}
			}
		}
		return suggestions;
	}

	List<Row> queryRowsForSource(ApplicationContext context, String resultEntity, List<String> columns, String filterString,
			String searchString) throws DataAccessException {


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
			throw new IllegalArgumentException("Cannot parse attribute " + c + "!");
		}

		String type = parts[0];
		String attributeName = parts[1];

		Optional<EntityType> entityType = searchableTypes.stream()
				.filter(e -> ServiceUtils.workaroundForTypeMapping(e).equalsIgnoreCase(type)).findFirst();

		if (entityType.isPresent()) {
			return entityType.get().getAttributes().stream().filter(a -> a.getName().equalsIgnoreCase(attributeName))
					.findFirst();
		} else {
			return Optional.empty();
		}
	}

	private Class<? extends Entity> getEntityClassByNameType(SearchService s, String name) {

		for (Class<? extends Entity> entityClass : s.listSearchableTypes()) {
			if (entityClass.getSimpleName().equalsIgnoreCase(name)) {
				return entityClass;
			}
		}
		throw new IllegalArgumentException("Invalid Entity '" + name + "'. Allowed values are: "
				+ s.listSearchableTypes().stream().map(Class::getSimpleName).collect(Collectors.joining(", ")));
	}

	private int getMaxResultsPerSource() {
		try {
			return Integer.parseInt(maxResultsPerSource);
		} catch (NumberFormatException e) {
			return 1001;
		}
	}

}
