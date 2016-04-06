/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.i18n.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.i18n.I18NBeanLI;
import org.eclipse.mdm.i18n.I18NException;
import org.eclipse.mdm.i18n.rest.tansferable.I18NResponse;
import org.eclipse.mdm.i18n.rest.tansferable.LocalizedAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Rest implementation of {@link I18NRestIF}
 * @author Gigatronik Ingolstadt GmbH
 *
 */
@Path("environments")
public class I18NRestImpl implements I18NRestIF {

	private static final Logger LOG = LoggerFactory.getLogger(I18NRestImpl.class); 
	
	@EJB
	private I18NBeanLI i18NBean;
	
	@Override	
	@GET
	@Path("/{SOURCENAME}/localizations")
	@Produces(MediaType.APPLICATION_JSON)
	public String localizeEnvironment(@PathParam("SOURCENAME") String sourceName) {
		try {
			Map<String, String> map = this.i18NBean.localizeType(sourceName, Environment.class);
			List<LocalizedAttribute> list = localizationMap2LocalizedAttributeList(map);
			return new Gson().toJson(new I18NResponse(list));
		} catch(I18NException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new I18NResponse());
		
	}

	@Override	
	@GET
	@Path("/{SOURCENAME}/tests/localizations")
	@Produces(MediaType.APPLICATION_JSON)
	public String localizeTest(@PathParam("SOURCENAME") String sourceName) {
		try {
			Map<String, String> map = this.i18NBean.localizeType(sourceName, Test.class);
			List<LocalizedAttribute> list = localizationMap2LocalizedAttributeList(map);
			return new Gson().toJson(new I18NResponse(list));
		} catch(I18NException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new I18NResponse());
	}

	@Override
	@GET
	@Path("/{SOURCENAME}/teststeps/localizations")
	@Produces(MediaType.APPLICATION_JSON)
	public String localizeTestStep(@PathParam("SOURCENAME") String sourceName) {
		try {
			Map<String, String> map = this.i18NBean.localizeType(sourceName, TestStep.class);
			List<LocalizedAttribute> list = localizationMap2LocalizedAttributeList(map);
			return new Gson().toJson(new I18NResponse(list));
		} catch(I18NException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new I18NResponse());
	}

	@Override
	@GET
	@Path("/{SOURCENAME}/measurements/localizations")
	@Produces(MediaType.APPLICATION_JSON)
	public String localizeMeasurement(@PathParam("SOURCENAME") String sourceName) {
		try {
			Map<String, String> map = this.i18NBean.localizeType(sourceName, Measurement.class);
			List<LocalizedAttribute> list = localizationMap2LocalizedAttributeList(map);
			return new Gson().toJson(new I18NResponse(list));
		} catch(I18NException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new I18NResponse());
	}

	@Override
	@GET
	@Path("/{SOURCENAME}/channelgroups/localizations")
	@Produces(MediaType.APPLICATION_JSON)
	public String localizeChannelGroup(@PathParam("SOURCENAME") String sourceName) {
		try {
			Map<String, String> map = this.i18NBean.localizeType(sourceName, ChannelGroup.class);
			List<LocalizedAttribute> list = localizationMap2LocalizedAttributeList(map);
			return new Gson().toJson(new I18NResponse(list));
		} catch(I18NException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new I18NResponse());
	}

	@Override
	@GET
	@Path("/{SOURCENAME}/channels/localizations")
	@Produces(MediaType.APPLICATION_JSON)
	public String localizeChannel(@PathParam("SOURCENAME") String sourceName) {
		try {
			Map<String, String> map = this.i18NBean.localizeType(sourceName, Channel.class);
			List<LocalizedAttribute> list = localizationMap2LocalizedAttributeList(map);
			return new Gson().toJson(new I18NResponse(list));
		} catch(I18NException e) {
			LOG.error(e.getMessage(), e);
		}
		return new Gson().toJson(new I18NResponse());
	}

		
	private List<LocalizedAttribute> localizationMap2LocalizedAttributeList(Map<String, String> localizedMap) {
		List<LocalizedAttribute> list = new ArrayList<>();
		Set<Entry<String, String>> entrySet = localizedMap.entrySet();
		for(Entry<String, String> entry : entrySet) {
			list.add(new LocalizedAttribute(entry.getKey(), entry.getValue()));
		}
		return list;
	}
}
