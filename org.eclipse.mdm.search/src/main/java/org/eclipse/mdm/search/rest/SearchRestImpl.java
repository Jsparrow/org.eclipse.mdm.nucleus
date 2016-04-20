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

package org.eclipse.mdm.search.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.businesstyperegistry.BusinessTypeRegistryBeanLI;
import org.eclipse.mdm.businesstyperegistry.BusinessTypeRegistryException;
import org.eclipse.mdm.businesstyperegistry.rest.transferable.Entry;
import org.eclipse.mdm.businesstyperegistry.rest.transferable.EntryResponse;
import org.eclipse.mdm.search.SearchBeanLI;
import org.eclipse.mdm.search.SearchDefinition;
import org.eclipse.mdm.search.SearchException;
import org.eclipse.mdm.search.rest.transferable.SearchDefinitionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Rest implementation of {@link SearchRestIF}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Path("searchdefinitions")
public class SearchRestImpl implements SearchRestIF {

	private static final Logger LOG = LoggerFactory.getLogger(SearchRestImpl.class); 
	
	@EJB
	private SearchBeanLI searchBean;
	
	@EJB
	private BusinessTypeRegistryBeanLI businessTypeRegistryBean;
	
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String listSearchDefinitions() {
		try {
			List<SearchDefinition> list = this.searchBean.listSearchDefinitions();
			return new Gson().toJson(new SearchDefinitionResponse(list));
		} catch(SearchException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new SearchDefinitionResponse());
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{SOURCENAME}/search/tests")
	public String searchTests(@PathParam("SOURCENAME") String sourceName, @QueryParam("filter") String filterString) {
		try {
			URI uri = this.businessTypeRegistryBean.createURI(sourceName, Environment.class, 1L);
			List<Test> testList = this.searchBean.search(uri, Test.class, filterString);
			List<Entry> list = entities2Entries(testList);
			return new Gson().toJson(new EntryResponse(Test.class, list));
		} catch(BusinessTypeRegistryException | SearchException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new EntryResponse());
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{SOURCENAME}/search/teststeps")
	public String searchTestSteps(String sourceName, @QueryParam("filter") String filterString) {
		try {
			URI uri = this.businessTypeRegistryBean.createURI(sourceName, Environment.class, 1L);
			List<TestStep> testStepList = this.searchBean.search(uri, TestStep.class, filterString);
			List<Entry> list = entities2Entries(testStepList);
			return new Gson().toJson(new EntryResponse(TestStep.class, list));
		} catch(BusinessTypeRegistryException | SearchException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new EntryResponse());
	}

	
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{SOURCENAME}/search/measurements")	
	public String searchMeasurements(String sourceName, @QueryParam("filter") String filterString) {
		try {
			URI uri = this.businessTypeRegistryBean.createURI(sourceName, Environment.class, 1L);
			List<Measurement> meaList = this.searchBean.search(uri, Measurement.class, filterString);
			List<Entry> list = entities2Entries(meaList);
			return new Gson().toJson(new EntryResponse(Measurement.class, list));
		} catch(BusinessTypeRegistryException | SearchException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new EntryResponse());
	}

	
	private <T extends Entity> List<Entry> entities2Entries(List<T> entities) {
		List<Entry> entryList = new ArrayList<Entry>();
		for(Entity entity : entities) {				
			Entry entry = new Entry(entity.getName(), entity.getClass().getSimpleName(),
				entity.getURI(), entity.getValues());
			entryList.add(entry);
		}
		return entryList;	
	}
	
}
