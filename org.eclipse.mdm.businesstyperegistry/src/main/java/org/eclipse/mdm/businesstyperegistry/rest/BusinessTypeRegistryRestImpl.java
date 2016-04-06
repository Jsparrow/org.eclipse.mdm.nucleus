/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.businesstyperegistry.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.businesstyperegistry.ActionBeanLI;
import org.eclipse.mdm.businesstyperegistry.BusinessTypeRegistryBeanLI;
import org.eclipse.mdm.businesstyperegistry.BusinessTypeRegistryException;
import org.eclipse.mdm.businesstyperegistry.rest.transferable.Action;
import org.eclipse.mdm.businesstyperegistry.rest.transferable.ActionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Rest implementation of {@link BusinessTypeRegistryRestIF}
 * @author Gigatronik Ingolstadt GmbH
 *
 */
@Path("environments")
public class BusinessTypeRegistryRestImpl implements BusinessTypeRegistryRestIF {

	private static final Logger LOG = LoggerFactory.getLogger(BusinessTypeRegistryRestImpl.class); 
	
	@EJB
	private BusinessTypeRegistryBeanLI btRegistryBean;
	
	@Override
	@GET
	@Path("/{SOURCENAME}/tests/actions")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTestActions(@PathParam("SOURCENAME") String sourceName) {
		
		try {		
			List<ActionBeanLI> actionBeanList = this.btRegistryBean.getActions(sourceName, Test.class);
			List<Action> actions = actionBeans2Actions(actionBeanList);
			return new Gson().toJson(new ActionResponse(actions));
		
		} catch(BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new ActionResponse());
	}
	
	
	@Override
	@GET
	@Path("/{SOURCENAME}/teststeps/actions")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTestStepActions(@PathParam("SOURCENAME") String sourceName) {
		
		try {		
			List<ActionBeanLI> actionBeanList = this.btRegistryBean.getActions(sourceName, TestStep.class);
			List<Action> actions = actionBeans2Actions(actionBeanList);
			return new Gson().toJson(new ActionResponse(actions));
		
		} catch(BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new ActionResponse());
	}


	@Override
	@GET
	@Path("/{SOURCENAME}/measurements/actions")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMeasurementActions(@PathParam("SOURCENAME") String sourceName) {
	
		try {		
			List<ActionBeanLI> actionBeanList = this.btRegistryBean.getActions(sourceName, Measurement.class);
			List<Action> actions = actionBeans2Actions(actionBeanList);
			return new Gson().toJson(new ActionResponse(actions));
		
		} catch(BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new ActionResponse());
	}
	
	

	private List<Action> actionBeans2Actions(List<ActionBeanLI> actionBeanList) {
		List<Action> actions = new ArrayList<>();
		for(ActionBeanLI actionBean : actionBeanList) {
			actions.add(new Action(actionBean.getActionName()));
		}
		return actions;
	}
}
