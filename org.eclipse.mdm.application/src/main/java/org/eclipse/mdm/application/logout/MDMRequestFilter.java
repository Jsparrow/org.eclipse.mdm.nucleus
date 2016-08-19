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

package org.eclipse.mdm.application.logout;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MDMRequestFilter
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class MDMRequestFilter implements Filter {

	private static String SERVLET_NAME_MDMNENUE = "mdmmenu";
	
	@Inject
	private MDMSessionExpiredListener sessionExpiredListener;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {	
		
		if(this.sessionExpiredListener != null) {
			this.sessionExpiredListener.update();
		}
		
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			String requestedURL = httpRequest.getRequestURI().toLowerCase();
			
			if(requestedURL.trim().endsWith(SERVLET_NAME_MDMNENUE)) {
				if(response instanceof HttpServletResponse) {
					String location = httpRequest.getContextPath();
					((HttpServletResponse) response).sendRedirect(location);
				}
			} else {
				chain.doFilter(request, response);
			}
		}
		
			
	}

	@Override
	public void destroy() {
	}

}
