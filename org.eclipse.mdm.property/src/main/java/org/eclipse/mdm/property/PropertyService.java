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

/**
 * Producer for injectable properties.
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@ApplicationScoped
public class PropertyService {

	private static final String COMPONENT_CONFIG_ROOT_FOLDER = "org.eclipse.mdm.property";

	private static final String BEAN_PROPERTIES_FILENAME = "bean.properties";
	private static final String GLOBAL_PROPERTIES_FILENAME = "global.properties";

	private Properties beanProperties;
	private Properties globalProperties;

	/**
	 * Reads the properties from the configuration file.
	 */
	@PostConstruct
	public void readProperties() {
		beanProperties = new Properties();
		globalProperties = new Properties();
		try {
			File componentConfigFolder = new File(COMPONENT_CONFIG_ROOT_FOLDER);
			System.out.println(componentConfigFolder.getAbsolutePath());
			if (!componentConfigFolder.exists() || !componentConfigFolder.isDirectory()) {
				throw new IOException("mandatory configuration folder '" + componentConfigFolder.getAbsolutePath()
						+ "' does not exist!");
			}
			File beanFile = new File(componentConfigFolder, BEAN_PROPERTIES_FILENAME);
			if (!componentConfigFolder.exists()) {
				throw new IOException(
						"mandatory service configuration file at '" + beanFile.getAbsolutePath() + "' does not exist!");
			}

			File globalConfigFile = new File(componentConfigFolder, GLOBAL_PROPERTIES_FILENAME);
			if (!componentConfigFolder.exists()) {
				throw new IOException("mandatory service configuration file at '" + globalConfigFile.getAbsolutePath()
						+ "' does not exist!");
			}

			InputStream isBeanProps = new FileInputStream(beanFile);
			InputStream isGlobalConfig = new FileInputStream(globalConfigFile);
			
			beanProperties.load(isBeanProps);
			globalProperties.load((isGlobalConfig));
			isBeanProps.close();
			isGlobalConfig.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Producer method for properties.
	 * 
	 * @param ip
	 *            The injection point.
	 * @return The property value.
	 * @throws PropertyException Thrown if the property is not set.
 	 */
	@Produces
	@BeanProperty
	public String getBeanPropertyValue(InjectionPoint ip) throws PropertyException {
		String className = ip.getMember().getDeclaringClass().getName();
		String fieldName = ip.getMember().getName();
		String propertyKey = className + "." + fieldName;
		if (!beanProperties.containsKey(propertyKey)) {
			throw new PropertyException("Mandatory property: " + propertyKey + " not set.");
		}
		return beanProperties.getProperty(propertyKey);
	}

	/**
	 * Producer method for global properties.
	 * 
	 * @param ip
	 *            The injection point.
	 * @return The global property value.
	 * @throws PropertyException  Thrown if the property is not set.
	 */
	@Produces
	@GlobalProperty
	public String getGlobalPropertyValue(InjectionPoint ip) throws PropertyException {
		GlobalProperty property = ip.getAnnotated().getAnnotation(GlobalProperty.class);
		String configKey = property.value();
		if (configKey.isEmpty()) {
			throw new IllegalArgumentException(
					"No global property name given. GlobalProperty annotation must be used with the name (e. g. GlobalProperty(\"propertyname\")).");
		}
		if (!globalProperties.containsKey(configKey)) {
			throw new PropertyException("Mandatory property: " + configKey + " not set.");
		}
		return globalProperties.getProperty(configKey);
	}
}