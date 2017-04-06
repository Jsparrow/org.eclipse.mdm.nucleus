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
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.Filter;
import org.eclipse.mdm.api.base.query.ModelManager;
import org.eclipse.mdm.api.base.query.Result;
import org.eclipse.mdm.api.base.query.SearchService;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.businessobjects.control.MDMEntityAccessException;
import org.eclipse.mdm.businessobjects.control.SearchParamParser;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.property.GlobalProperty;
import org.eclipse.mdm.query.entity.QueryRequest;
import org.eclipse.mdm.query.entity.Row;
import org.eclipse.mdm.query.entity.SourceFilter;
import org.eclipse.mdm.query.entity.SuggestionRequest;
import org.eclipse.mdm.query.util.Util;

/**
 * 
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
@Stateless
public class QueryService {
	
	@Inject
	@GlobalProperty(value = "businessobjects.query.maxresultspersource")
	private String maxResultsPerSource = "1001";
	
	@EJB
	ConnectorService connectorService;
	
	
	public List<Row> queryRows(QueryRequest request) {
		List<Row> rows = new ArrayList<>();
		
		for (SourceFilter filter : request.getFilters()) {
			EntityManager em = this.connectorService.getEntityManagerByName(filter.getSourceName());
			
			rows.addAll(queryRowsForSource(em, request.getResultType(), request.getColumns(), filter.getFilter(), filter.getSearchString()));
		}
			
		return rows;
	}

	public <T> List<T> getSuggestions(SuggestionRequest suggestionRequest) {
		
		String type = suggestionRequest.getType();
		String attrName = suggestionRequest.getAttrName();
		
		List<T> res = new ArrayList<>();
		for (String envName: suggestionRequest.getEnvironments()) {
			ModelManager modelManager = getModelManager(this.connectorService.getEntityManagerByName(envName));
			EntityType entityType = modelManager.getEntityType(type);
			Attribute attr = entityType.getAttribute(attrName);

			try {
				List<Result> results = modelManager.createQuery()
					.select(attr)
					.group(attr)
					.fetch();
				results.forEach(r -> res.add(r.getValue(attr).extract()));
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
		}
		return res;
	}
	
	List<Row> queryRowsForSource(EntityManager em, String resultEntity, List<String> columns, String filterString, String searchString) {
		try {
			ModelManager modelManager = getModelManager(em);
			
			SearchService searchService = ServiceUtils.getSearchService(em);
			
			Class<? extends Entity> resultType = getEntityClassByNameType(searchService, resultEntity);

			List<EntityType> searchableTypes = searchService.listEntityTypes(resultType);
			List<Attribute> attributes = columns.stream()
					.map(c -> getAttribute(searchableTypes, c))
					.filter(Optional::isPresent)
				    .map(Optional::get)
					.collect(Collectors.toList());

			Filter filter = SearchParamParser.parseFilterString(searchableTypes, filterString);
			
			List<Result> result = searchService.fetchResults(resultType, attributes, filter, searchString);
	
			
			return Util.convertResultList(result.subList(0, Math.min(result.size(), getMaxResultsPerSource())), resultType, modelManager.getEntityType(resultType));
			
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}
	
	private ModelManager getModelManager(EntityManager em) {
		Optional<ModelManager> mm = em.getModelManager();
		if(!mm.isPresent()) {
			throw new IllegalStateException("neccessary ModelManager is not available");
		}
		return mm.get();
	}
	
	private Optional<Attribute> getAttribute(List<EntityType> searchableTypes, String c) {
		String[] parts = c.split("\\.");
		
		if (parts.length != 2) {
			throw new IllegalArgumentException("Cannot parse attribute " + c + "!");
		}
		
		String type = parts[0];
		String attributeName = parts[1];
		
		Optional<EntityType> entityType = searchableTypes.stream()
				.filter(e -> ServiceUtils.workaroundForTypeMapping(e).equalsIgnoreCase(type))
				.findFirst();
		
		if (entityType.isPresent()) {
			return entityType.get().getAttributes().stream()
				.filter(a -> a.getName().equalsIgnoreCase(attributeName))
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
				+ s.listSearchableTypes().stream().map(c -> c.getSimpleName()).collect(Collectors.joining(", ")));
	}
	
	private int getMaxResultsPerSource() {
		try {
			return Integer.parseInt(maxResultsPerSource);
		}
		catch (NumberFormatException e) {
			return 1001;
		}
	}
	
}
