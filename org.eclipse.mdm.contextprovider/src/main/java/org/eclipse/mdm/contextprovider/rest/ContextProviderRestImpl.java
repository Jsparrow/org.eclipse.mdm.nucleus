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

package org.eclipse.mdm.contextprovider.rest;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.businesstyperegistry.BusinessTypeRegistryBeanLI;
import org.eclipse.mdm.businesstyperegistry.BusinessTypeRegistryException;
import org.eclipse.mdm.contextprovider.Context;
import org.eclipse.mdm.contextprovider.ContextProviderBeanLI;
import org.eclipse.mdm.contextprovider.ContextProviderException;
import org.eclipse.mdm.contextprovider.rest.transferable.ContextRespone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Rest implementation of {@link ContextProviderRestIF}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Path("environments")
public class ContextProviderRestImpl implements ContextProviderRestIF {

	
	
	private static final Logger LOG = LoggerFactory.getLogger(ContextProviderRestImpl.class);
	
	@EJB
	private ContextProviderBeanLI contextProviderBean;
	
	@EJB BusinessTypeRegistryBeanLI businessTypeRegistryBean;
	
	@Override
	@GET
	@Path("/{SOURCENAME}/teststeps/contexts")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTestStepContext(@PathParam("SOURCENAME") String sourceName, @QueryParam("teststep.id") long testStepID) {
		try {	
			URI testStepURI= this.businessTypeRegistryBean.createURI(sourceName, TestStep.class, testStepID);
			Context contextData = this.contextProviderBean.getTestStepContext(testStepURI);
			return new Gson().toJson(new ContextRespone(contextData));
		
		} catch (ContextProviderException | BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new ContextRespone());
	}
	
	
	
	@Override
	@GET
	@Path("/{SOURCENAME}/teststeps/contexts/unitundertest")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTestStepContextUUT(@PathParam("SOURCENAME") String sourceName, @QueryParam("teststep.id") long testStepID) {
		try {	
			URI testStepURI= this.businessTypeRegistryBean.createURI(sourceName, TestStep.class, testStepID);
			Context contextData = this.contextProviderBean.getTestStepContext(testStepURI, ContextType.UNITUNDERTEST);
			return new Gson().toJson(new ContextRespone(contextData));
		
		} catch (ContextProviderException | BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new ContextRespone());
	}
	
	
	
	@Override
	@GET
	@Path("/{SOURCENAME}/teststeps/contexts/testsequence")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTestStepContextTSQ(@PathParam("SOURCENAME") String sourceName, @QueryParam("teststep.id") long testStepID) {
		try {	
			URI testStepURI= this.businessTypeRegistryBean.createURI(sourceName, TestStep.class, testStepID);
			Context contextData = this.contextProviderBean.getTestStepContext(testStepURI, ContextType.TESTSEQUENCE);
			return new Gson().toJson(new ContextRespone(contextData));
		
		} catch (ContextProviderException | BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new ContextRespone());
	}
	
	
	
	@Override
	@GET
	@Path("/{SOURCENAME}/teststeps/contexts/testequipment")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTestStepContextTEQ(@PathParam("SOURCENAME") String sourceName, @QueryParam("teststep.id") long testStepID) {
		try {	
			URI testStepURI= this.businessTypeRegistryBean.createURI(sourceName, TestStep.class, testStepID);
			Context contextData = this.contextProviderBean.getTestStepContext(testStepURI, ContextType.TESTEQUIPMENT);
			return new Gson().toJson(new ContextRespone(contextData));
		
		} catch (ContextProviderException | BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new ContextRespone());
	}
	
	
	
	@Override
	@GET
	@Path("/{SOURCENAME}/measurements/contexts")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMeasurementContext(@PathParam("SOURCENAME") String sourceName, @QueryParam("measurement.id") long measurementID) {
		try {	
			URI measurementURI= this.businessTypeRegistryBean.createURI(sourceName, Measurement.class, measurementID);
			Context contextData = this.contextProviderBean.getMeasurementContext(measurementURI);
			return new Gson().toJson(new ContextRespone(contextData));
		
		} catch (ContextProviderException | BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new ContextRespone());
	}
	
	
	
	@Override
	@GET
	@Path("/{SOURCENAME}/measurements/contexts/unitundertest")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMeasurementContextUUT(@PathParam("SOURCENAME") String sourceName, @QueryParam("measurement.id") long measurementID) {
		try {	
			URI measurementURI= this.businessTypeRegistryBean.createURI(sourceName, Measurement.class, measurementID);
			Context contextData = this.contextProviderBean.getMeasurementContext(measurementURI, ContextType.UNITUNDERTEST);
			return new Gson().toJson(new ContextRespone(contextData));
		
		} catch (ContextProviderException | BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new ContextRespone());
	}
	
	
	
	@Override
	@GET
	@Path("/{SOURCENAME}/measurements/contexts/testsequence")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMeasurementContextTSQ(@PathParam("SOURCENAME") String sourceName, @QueryParam("measurement.id") long measurementID) {
		try {	
			URI measurementURI= this.businessTypeRegistryBean.createURI(sourceName, Measurement.class, measurementID);
			Context contextData = this.contextProviderBean.getMeasurementContext(measurementURI, ContextType.TESTSEQUENCE);
			return new Gson().toJson(new ContextRespone(contextData));
		
		} catch (ContextProviderException | BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new ContextRespone());
	}
	
	
	
	@Override
	@GET
	@Path("/{SOURCENAME}/measurements/contexts/testequipment")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMeasurementContextTEQ(@PathParam("SOURCENAME") String sourceName, @QueryParam("measurement.id") long measurementID) {
		try {	
			URI measurementURI= this.businessTypeRegistryBean.createURI(sourceName, Measurement.class, measurementID);
			Context contextData = this.contextProviderBean.getMeasurementContext(measurementURI, ContextType.TESTEQUIPMENT);
			return new Gson().toJson(new ContextRespone(contextData));
		
		} catch (ContextProviderException | BusinessTypeRegistryException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new ContextRespone());
	}	
}
