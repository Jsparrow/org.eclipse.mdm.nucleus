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
package org.eclipse.mdm.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
				LOG.warn("property configuration folder  at '" + componentConfigFolder.getAbsolutePath() 
					+ "' does not exist! No properties available!");
				return;
			}
		
			File globalConfigFile = new File(componentConfigFolder, GLOBAL_PROPERTIES_FILENAME);
			if (!componentConfigFolder.exists()) {
				LOG.warn("property configuration file for global properties at '" + globalConfigFile.getAbsolutePath() 
					+ "' does not exist! no global properties available");
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
	 * @throws GlobalPropertyException  Thrown if the property is not set.
	 */
	@Produces
	@GlobalProperty
	public String getGlobalPropertyValue(InjectionPoint ip) throws GlobalPropertyException {
		GlobalProperty property = ip.getAnnotated().getAnnotation(GlobalProperty.class);
		String propertyName = property.value();		
		if (!globalProperties.containsKey(propertyName)) {
			LOG.warn("global property with name '" + propertyName + "' not found!");
			return "";
		}
		return globalProperties.getProperty(propertyName);
	}
	
	
	
	private void closeInputStream(InputStream is) {
		try {
			if(is != null) {
				is.close();
			}
		} catch(IOException e) {
			LOG.warn(e.getMessage(), e);
			throw new GlobalPropertyException(e.getMessage(), e);
		}
	}
	
}