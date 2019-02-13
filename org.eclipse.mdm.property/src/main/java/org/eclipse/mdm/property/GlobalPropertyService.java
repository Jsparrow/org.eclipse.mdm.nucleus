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

package org.eclipse.mdm.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Producer for injectable properties.
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@ApplicationScoped
public class GlobalPropertyService {

	private static final Logger LOG = LoggerFactory.getLogger(GlobalPropertyService.class);

	private static final String COMPONENT_CONFIG_ROOT_FOLDER = "org.eclipse.mdm.property";
	private static final String GLOBAL_PROPERTIES_FILENAME = "global.properties";

	private Properties globalProperties = new Properties();

	/**
	 * Reads the properties from the configuration file.
	 */
	@PostConstruct
	public void readProperties() {

		InputStream isGlobalProperties = null;

		try {
			File componentConfigFolder = new File(COMPONENT_CONFIG_ROOT_FOLDER);

			if (!componentConfigFolder.exists() || !componentConfigFolder.isDirectory()) {
				LOG.warn(new StringBuilder().append("property configuration folder  at '").append(componentConfigFolder.getAbsolutePath()).append("' does not exist! No properties available!").toString());
				return;
			}

			File globalConfigFile = new File(componentConfigFolder, GLOBAL_PROPERTIES_FILENAME);
			if (!componentConfigFolder.exists()) {
				LOG.warn(new StringBuilder().append("property configuration file for global properties at '").append(globalConfigFile.getAbsolutePath()).append("' does not exist! no global properties available").toString());
				return;
			}

			isGlobalProperties = new FileInputStream(globalConfigFile);
			this.globalProperties.load((isGlobalProperties));

		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new GlobalPropertyException(e.getMessage(), e);
		} finally {
			closeInputStream(isGlobalProperties);
		}
	}

	/**
	 * Producer method for global properties.
	 * 
	 * @param ip
	 *            The injection point.
	 * @return The global property value.
	 * @throws GlobalPropertyException
	 *             Thrown if the property is not set.
	 */
	@Produces
	@GlobalProperty
	public String getGlobalPropertyValue(InjectionPoint ip) {
		GlobalProperty property = ip.getAnnotated().getAnnotation(GlobalProperty.class);
		String propertyName = property.value();
		if (globalProperties.containsKey(propertyName)) {
			return globalProperties.getProperty(propertyName);
		}
		LOG.warn(new StringBuilder().append("global property with name '").append(propertyName).append("' not found!").toString());
		return "";
	}
	
	/**
	 * Producer method for a global property map.
	 * 
	 * @param ip
	 *            The injection point.
	 * @return A map with the global properties 
	 */
	@Produces
	@GlobalProperty
	public Map<String, String> getGlobalPropertyMap(InjectionPoint ip) {
		Map<String, String> map = new HashMap<>();
		globalProperties.stringPropertyNames().forEach(key -> map.put(key, globalProperties.getProperty(key)));
		return map;
	}

	private void closeInputStream(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (IOException e) {
			LOG.warn(e.getMessage(), e);
			throw new GlobalPropertyException(e.getMessage(), e);
		}
	}

}