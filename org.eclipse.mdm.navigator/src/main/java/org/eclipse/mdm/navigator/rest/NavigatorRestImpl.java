/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.navigator.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.businesstyperegistry.rest.transferable.Entry;
import org.eclipse.mdm.businesstyperegistry.rest.transferable.EntryResponse;
import org.eclipse.mdm.navigator.NavigatorBeanLI;
import org.eclipse.mdm.navigator.NavigatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Rest implementation of {@link NavigatorRestIF}
 * @author Gigatronik Ingolstadt GmbH
 *
 */
@Path("environments")
public class NavigatorRestImpl implements NavigatorRestIF {

	private static final Logger LOG = LoggerFactory.getLogger(NavigatorRestImpl.class); 
	
	@EJB(beanName = "NavigatorBean")
	private NavigatorBeanLI navigatorBean;
	
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getEnvironments() {
		try {			
			List<Environment> envList = this.navigatorBean.getEnvironments();			
			List<Entry> list = entities2Entries(envList);
			return new Gson().toJson(new EntryResponse(Environment.class, list));
			
		} catch (NavigatorException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new EntryResponse());
	}

	
	@Override
	@GET
	@Path("/{SOURCENAME}/tests")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTests(@PathParam("SOURCENAME") String sourceName) {
		try {			
			URI environmentURI = this.navigatorBean.createURI(sourceName, Environment.class, 1L);
			List<Test> testList = this.navigatorBean.getTests(environmentURI);		
			List<Entry> list = entities2Entries(testList);
			return new Gson().toJson(new EntryResponse(Test.class, list));
			
		} catch (NavigatorException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new EntryResponse());
		
	}

	
	
	
	@Override
	@GET
	@Path("/{SOURCENAME}/teststeps")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTestSteps(@PathParam("SOURCENAME") String sourceName, @QueryParam("test.id") long testId) {
		
		try {			
			URI testURI = this.navigatorBean.createURI(sourceName, Test.class, testId);
			List<TestStep> testStepList = this.navigatorBean.getTestSteps(testURI);	
			List<Entry> list = entities2Entries(testStepList);
			return new Gson().toJson(new EntryResponse(TestStep.class, list));
			
		} catch (NavigatorException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new EntryResponse());
	}


	@Override
	@GET
	@Path("/{SOURCENAME}/measurements")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMeasurements(@PathParam("SOURCENAME") String sourceName, @QueryParam("teststep.id") long testStepId) {

		try {			
			URI testStepURI = this.navigatorBean.createURI(sourceName, TestStep.class, testStepId);
			List<Measurement> measurementsList = this.navigatorBean.getMeasurements(testStepURI);	
			List<Entry> list = entities2Entries(measurementsList);
			return new Gson().toJson(new EntryResponse(Measurement.class, list));
			
		} catch (NavigatorException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new EntryResponse());
	}


	@Override
	@GET
	@Path("/{SOURCENAME}/channelgroups")
	@Produces(MediaType.APPLICATION_JSON)
	public String getChannelGroups(@PathParam("SOURCENAME") String sourceName, @QueryParam("measurement.id") long measurmentId) {
		try {			
			URI measurementURI = this.navigatorBean.createURI(sourceName, Measurement.class, measurmentId);
			List<ChannelGroup> channelGroupList = this.navigatorBean.getChannelGroups(measurementURI);	
			List<Entry> list = entities2Entries(channelGroupList);
			return new Gson().toJson(new EntryResponse(ChannelGroup.class, list));
			
		} catch (NavigatorException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new EntryResponse());
	}


	@Override
	@GET
	@Path("/{SOURCENAME}/channels")
	@Produces(MediaType.APPLICATION_JSON)
	public String getChannels(@PathParam("SOURCENAME") String sourceName, @QueryParam("channelgroup.id") long channelGroupId) {
		try {			
			URI channelGroupURI= this.navigatorBean.createURI(sourceName, ChannelGroup.class, channelGroupId);
			List<Channel> channelList = this.navigatorBean.getChannels(channelGroupURI);
			List<Entry> list = entities2Entries(channelList);
			return new Gson().toJson(new EntryResponse(Channel.class, list));
			
		} catch (NavigatorException e) {
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

