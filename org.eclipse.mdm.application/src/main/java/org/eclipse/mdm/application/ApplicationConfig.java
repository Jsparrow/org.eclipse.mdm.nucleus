/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.application;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Application configuration
 * @author Gigatronik Ingolstadt GmbH
 *
 */
@ApplicationPath("mdm")
public class ApplicationConfig extends ResourceConfig {
	

	public ApplicationConfig() {
		packages("org.eclipse.mdm.application", 
			"org.eclipse.mdm.navigator.rest", 
			"org.eclipse.mdm.businesstyperegistry.rest", 
			"org.eclipse.mdm.i18n.rest",
			"org.eclipse.mdm.action.delete.rest");
	}

	
}
