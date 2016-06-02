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

package org.eclipse.mdm.businessobjects.control;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.mail.search.SearchException;

import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.Filter;
import org.eclipse.mdm.api.base.query.FilterItem;
import org.eclipse.mdm.api.base.query.Record;
import org.eclipse.mdm.api.base.query.SearchService;
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
		Principal principal = this.sessionContext.getCallerPrincipal();
		SearchDefinitionReader scReader = new SearchDefinitionReader(principal);
		return scReader.readSearchDefinitions();
	}

	/**
	 * lists the available search attributes for the given result type.
	 * @param em The entity manager.
	 * @param resultType The result type.
	 * @return The available search attributes.
	 */
	public <T extends Entity> List<SearchAttribute> listAvailableAttributes(EntityManager em, Class<T> resultType) {
		SearchService searchService = ServiceUtils.getSearchService(em);
		
		List<EntityType> entityTypes = searchService.listEntityTypes(resultType);
		List<SearchAttribute> searchAttributes = new ArrayList<>();
		
		for (EntityType entityType : entityTypes) {
			for (Attribute attr : entityType.getAttributes()) {
				searchAttributes.add(new SearchAttribute(entityType.getName(), attr.getName(),
						attr.getValueType().toString(), "*"));
			}
		}
	
		return searchAttributes;
	}

	/**
	 * executes a search using the given filter and returns the search result
	 * 
	 * @param em
	 *            {@link EntityManager}
	 * @param resultType
	 *            business object type of the search results
	 * @param filterString
	 *            filter for the search request
	 * @return the found business objects
	 */
	public <T extends Entity> List<T> search(EntityManager em, Class<T> resultType, String filterString) {
		try {
			SearchService searchService = ServiceUtils.getSearchService(em);
			List<EntityType> searchable = searchService.listEntityTypes(resultType);
			Filter filter = SearchParamParser.parseFilterString(searchable, filterString);
			List<Attribute> attributesList = getAttributeListFromFilter(filter);
			Map<T, List<Record>> result = searchService.fetch(resultType, attributesList, filter);
			return extractEntities(result);
		} catch (DataAccessException e) {
			throw new MDMEntityAccessException(e.getMessage(), e);
		}
	}

	private <T extends Entity> List<T> extractEntities(Map<T, List<Record>> result) {
		List<T> list = new ArrayList<>();
		for (T entity : result.keySet()) {
			list.add(entity);
		}
		return list;
	}

	private List<Attribute> getAttributeListFromFilter(Filter filter) {

		List<Attribute> attributeList = new ArrayList<>();
		Iterator<FilterItem> fIterator = filter.iterator();
		while (fIterator.hasNext()) {
			FilterItem filterItem = fIterator.next();
			if (filterItem.isCondition()) {
				attributeList.add(filterItem.getCondition().getAttribute());
			}
		}

		return attributeList;
	}

	
}
