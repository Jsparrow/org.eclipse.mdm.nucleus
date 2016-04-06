/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.connector.bean;

/**
 * ServiceConfiguration
 * 
 * @author Gigatronik Ingolstadt GmbH
 *
 */
public class ServiceConfiguration {

	
	
	private final String nameService;
	private final String serviceName;

	
	/**
	 * Constructor
	 * 
	 * @param nameService name service
	 * @param serviceName service name
	 */
	public ServiceConfiguration(String nameService, String serviceName) {
		this.nameService = nameService;
		this.serviceName = serviceName;	
	}
	
	
	
	@Override
	public String toString() {
		return this.nameService + "#" + serviceName;
	}
	
	
	
	/**
	 * returns the name service of this configuration 
	 * @return the name service of this configuration
	 */
	public String getNameService() {
		return nameService;
	}
	
	
	
	/**
	 * returns the service name of this configuration
	 * @return the service name of this configuration
	 */
	public String getServiceName() {
		return serviceName;
	}

}
