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

package org.eclipse.mdm.search.bean;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.EntityManager;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.Filter;
import org.eclipse.mdm.api.base.query.FilterItem;
import org.eclipse.mdm.api.base.query.Record;
import org.eclipse.mdm.api.base.query.SearchService;
import org.eclipse.mdm.connector.ConnectorBeanLI;
import org.eclipse.mdm.connector.ConnectorException;
import org.eclipse.mdm.search.SearchBeanLI;
import org.eclipse.mdm.search.SearchDefinition;
import org.eclipse.mdm.search.SearchException;
import org.eclipse.mdm.search.utils.FilterParser;

/**
 * Bean implementation of {@link SearchBeanLI}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
@LocalBean
public class SearchBean implements SearchBeanLI {

	@Resource
	private SessionContext sessionContext;
	
	@EJB
	private ConnectorBeanLI connectorBean;
	
	@Override
	public List<SearchDefinition> listSearchDefinitions() throws SearchException {
		Principal principal = this.sessionContext.getCallerPrincipal();
		SearchDefinitionReader scReader = new SearchDefinitionReader(principal);
		return scReader.readSerachDefinitions();
	}

	
	@Override
	public <T extends Entity> List<T> search(URI uri, Class<T> resultType,
		String filterString) throws SearchException {
		
		try {
			EntityManager em = this.connectorBean.getEntityManagerByURI(uri);
			SearchService searchService = getSearchService(uri, em);
			List<EntityType> searchable = searchService.listEntityTypes(resultType);
			Filter filter = FilterParser.parse(searchable, filterString);			
			Map<T, List<Record>> result = searchService.fetch(resultType, getAttributeList(filter), filter);
			return extractEntities(result);		
		} catch(ConnectorException | DataAccessException e) {
			throw new SearchException(e.getMessage());
		}		
		
	}
	
	
	private <T extends Entity> List<T> extractEntities(Map<T, List<Record>> result) {
		List<T> list = new ArrayList<>();
		for(T entity : result.keySet()) {
			list.add(entity);
		}
		return list;
	}
 
	private List<Attribute> getAttributeList(Filter filter) {
		
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
	
	
	private SearchService getSearchService(URI uri, EntityManager em) throws SearchException {
		Optional<SearchService> oSS = em.getSearchService();
		if(!oSS.isPresent()) {
			throw new SearchException("unable to get search servie for MDM data source with name '" 
				+ uri.getSourceName() + "'");
		}
		return oSS.get();
	}


	
	
	

}

