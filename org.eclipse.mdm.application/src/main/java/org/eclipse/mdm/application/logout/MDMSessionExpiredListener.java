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

import java.io.Serializable;
import java.security.Principal;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.mdm.connector.boundary.ConnectorService;

/**
 * MDMSessionExpiredListener
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@SessionScoped
public class MDMSessionExpiredListener implements Serializable {

	private static final long serialVersionUID = -1250150736708611890L;

	@Resource
	private Principal userPrincipal;

	@Inject
	private ConnectorService connectorService;

	@PreDestroy
	public void destroySession() {
		if (this.userPrincipal != null) {
			this.connectorService.disconnect();
		}

	}

}
