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


package org.eclipse.mdm.application;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class AccessControlResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        
    	final MultivaluedMap<String,Object> responseHeaders = responseContext.getHeaders();        
        final MultivaluedMap<String,String> requestHeaders = requestContext.getHeaders();
        
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, Content-Type");
        responseHeaders.add("Access-Control-Expose-Headers", "Location, Content-Disposition");
        responseHeaders.add("Access-Control-Allow-Methods", "POST, PUT, GET, DELETE, HEAD, OPTIONS");
        
        requestHeaders.add("Access-Control-Allow-Origin", "*");
        requestHeaders.add("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, Content-Type");
        requestHeaders.add("Access-Control-Expose-Headers", "Location, Content-Disposition");
        requestHeaders.add("Access-Control-Allow-Methods", "POST, PUT, GET, DELETE, HEAD, OPTIONS");
    }
}