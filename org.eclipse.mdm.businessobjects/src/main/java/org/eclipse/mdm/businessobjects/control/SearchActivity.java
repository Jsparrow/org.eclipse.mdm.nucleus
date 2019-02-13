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


package org.eclipse.mdm.businessobjects.control;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.mail.search.SearchException;

import org.eclipse.mdm.api.base.ServiceNotProvidedException;
import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.Filter;
import org.eclipse.mdm.api.base.query.FilterItem;
import org.eclipse.mdm.api.base.search.SearchService;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.eclipse.mdm.businessobjects.entity.SearchDefinition;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

/**
 * SearchActivity Bean for searching business object and managing search
 * definitions
 *
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class SearchActivity {

	@Resource
	private SessionContext sessionContext;

	/**
	 * lists available global and user specific search definitions
	 *
	 * @return available global and user specific search definitions
	 * @throws SearchException
	 *             if an error occurs during lookup available search definitions
	 */
	public List<SearchDefinition> listSearchDefinitions() {
		Principal principal = sessionContext.getCallerPrincipal();
		SearchDefinitionReader scReader = new SearchDefinitionReader(principal);
		return scReader.readSearchDefinitions();
	}

	/**
	 * lists the available search attributes for the given result type.
	 * 
	 * @param resultType
	 *            The result type.
	 * @return The available search attributes.
	 */
	public <T extends Entity> List<SearchAttribute> listAvailableAttributes(ApplicationContext context, Class<T> resultType) {

			SearchService searchService = context.getSearchService()
					.orElseThrow(() -> new MDMEntityAccessException("SearchService not found!"));
		
		try {
			List<SearchAttribute> searchAttributes = new ArrayList<>();
			List<EntityType> entityTypes = searchService.listEntityTypes(resultType);

			entityTypes.forEach(entityType -> entityType.getAttributes().forEach(
					attr -> searchAttributes.add(new SearchAttribute(ServiceUtils.workaroundForTypeMapping(entityType),
							attr.getName(), attr.getValueType().toString(), "*"))));
			return searchAttributes;
			
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}
	}

	/**
	 * executes a search using the given filter and returns the search result
	 *
	 * @param resultType
	 *            business object type of the search results
	 * @param filterString
	 *            filter for the search request
	 * @return the found business objects
	 */
	public <T extends Entity> List<T> search(ApplicationContext context, Class<T> resultType, String filterString) {
		try {
			SearchService searchService = context.getSearchService()
					.orElseThrow(() -> new ServiceNotProvidedException(SearchService.class));
			List<EntityType> searchable = searchService.listEntityTypes(resultType);
			Filter filter = FilterParser.parseFilterString(searchable, filterString);
			List<Attribute> attributesList = getAttributeListFromFilter(filter);
			return searchService.fetch(resultType, attributesList, filter);
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	private List<Attribute> getAttributeListFromFilter(Filter filter) {

		List<Attribute> attributeList = new ArrayList<>();
		for (FilterItem filterItem : filter) {
			if (filterItem.isCondition()) {
				attributeList.add(filterItem.getCondition().getAttribute());
			}
		}

		return attributeList;
	}

	/**
	 * executes a free textsearch
	 *
	 * @param query
	 *            the query given to the search
	 * @return the found business objects
	 */
	public List<Entity> search(ApplicationContext context, String query) {
		try {
			SearchService searchService = context.getSearchService()
					.orElseThrow(() -> new ServiceNotProvidedException(SearchService.class));
			List<Entity> allEntities = new ArrayList<>();

			if (searchService.isTextSearchAvailable()) {
				Map<Class<? extends Entity>, List<Entity>> fetch = searchService.fetch(query);
				fetch.values().forEach(allEntities::addAll);
			}

			return allEntities;
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

}
