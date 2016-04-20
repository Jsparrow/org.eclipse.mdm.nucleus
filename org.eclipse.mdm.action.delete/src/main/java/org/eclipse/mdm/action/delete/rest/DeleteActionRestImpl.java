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

package org.eclipse.mdm.action.delete.rest;

import javax.ejb.EJB;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.mdm.action.delete.DeleteActionBeanLI;
import org.eclipse.mdm.action.delete.DeleteActionException;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.businesstyperegistry.BusinessTypeRegistryBeanLI;
import org.eclipse.mdm.businesstyperegistry.BusinessTypeRegistryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Rest implementation of {@link DeleteActionRestIF}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Path("environments")
public class DeleteActionRestImpl implements DeleteActionRestIF {

	private static final Logger LOG = LoggerFactory.getLogger(DeleteActionRestImpl.class); 
	
	@EJB
	private DeleteActionBeanLI deleteBean;
	
	@EJB
	private BusinessTypeRegistryBeanLI businessTypeRegistry;

	@Override
	@DELETE
	@Path("/{SOURCENAME}/tests/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteTest(@PathParam("SOURCENAME") String sourceName, @QueryParam("test.id") long testId) {
		try {
			URI uri = this.businessTypeRegistry.createURI(sourceName, Test.class, testId);
			this.deleteBean.delete(uri);
			return "";
		} catch(DeleteActionException | BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(testId);
	}

	
	
	@Override
	@DELETE
	@Path("/{SOURCENAME}/teststeps/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteTestStep(@PathParam("SOURCENAME") String sourceName, @QueryParam("teststep.id")  long testStepId) {
		try {
			URI uri = this.businessTypeRegistry.createURI(sourceName, TestStep.class, testStepId);
			this.deleteBean.delete(uri);
			return "";
		} catch(DeleteActionException | BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(testStepId);
	}

	
	
	@Override
	@DELETE
	@Path("/{SOURCENAME}/measurements/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteMeasurement(@PathParam("SOURCENAME") String sourceName, @QueryParam("measurement.id")  long measurementId) {
		try {
			URI uri = this.businessTypeRegistry.createURI(sourceName, Measurement.class, measurementId);
			this.deleteBean.delete(uri);
			return "";
		} catch(DeleteActionException | BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(measurementId);
	}

}
