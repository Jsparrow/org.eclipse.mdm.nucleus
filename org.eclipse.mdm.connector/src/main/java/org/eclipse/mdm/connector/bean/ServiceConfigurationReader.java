/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.connector.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.mdm.api.base.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ServiceConfigurationReader to read service configurations form a resource property file 
 * at 'org/eclipse/mdm/connector/configuration/services'
 * 
 * @author Gigatronik Ingolstadt GmbH
 *
 */
public class ServiceConfigurationReader {

	
	
	private static final Logger LOG = LoggerFactory.getLogger(ServiceConfigurationReader.class); 
	
	private static final String SERVICES_KEY = "SERVICES";
	private static final String SERVICE_ATTRIBUTE_NAMESERVICE_KEY = "NameService";
	private static final String SERVICE_ATTRIBUTE_SERVICENAME_KEY = "ServiceName";
	private static final String SERVICE_KEY_SEPARATOR = ";";
	
	private String SERVICE_FILE = "org/eclipse/mdm/connector/configuration/services";
	private ResourceBundle SERVICE_RESOURCES = ResourceBundle.getBundle(SERVICE_FILE);
	
	
	
	/**
	 * reads the service configurations from a resource property file and returns the read
	 * services as list
	 * 
	 * @return the read service as list with {@link ServiceConfiguration} 
	 * @throws ConnectionException
	 */
	public List<ServiceConfiguration> readServiceConfigurations() throws ConnectionException {
		List<ServiceConfiguration> serviceList = new ArrayList<>();
		String[] serviceKeys = readServiceKeys();
		for(String serviceKey : serviceKeys) {
			
			String nameServiceKey = serviceKey + "." + SERVICE_ATTRIBUTE_NAMESERVICE_KEY;
			String serviceNameKey = serviceKey + "." + SERVICE_ATTRIBUTE_SERVICENAME_KEY;
			
			addServiceConfiguration(nameServiceKey, serviceNameKey, serviceList);
		}
		return serviceList;
	}
	
	
		
	private String[] readServiceKeys() throws ConnectionException {
		try {
			String serviceKey = SERVICE_RESOURCES.getString(SERVICES_KEY);
			return serviceKey.split(SERVICE_KEY_SEPARATOR);
		} catch(MissingResourceException e) {
			String message = "mandatory connector resource key "+ "with name '" + SERVICES_KEY + "' not found";
			LOG.error(message);
			throw new ConnectionException(message, e);
		}
	}
	
	
	
	private void addServiceConfiguration(String nameServiceKey, String serviceNameKey, 
		List<ServiceConfiguration> serviceList) {

		try {
			String nameService = SERVICE_RESOURCES.getString(nameServiceKey);
			String serviceName = SERVICE_RESOURCES.getString(serviceNameKey);
			
			serviceList.add(new ServiceConfiguration(nameService, serviceName));
		
		} catch(MissingResourceException e) {
			String message = "expected service configuration key not found (expected keys at connector resource are: '" 
				+ nameServiceKey + "' and '" + serviceNameKey + "')";
			LOG.error(message);
		}		
	}
}
