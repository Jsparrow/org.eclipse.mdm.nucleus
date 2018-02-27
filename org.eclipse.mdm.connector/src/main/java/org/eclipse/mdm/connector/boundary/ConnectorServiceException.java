/*
 * Copyright (c) 2016-2018 Gigatronik Ingolstadt GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.connector.boundary;

public class ConnectorServiceException extends RuntimeException {

	private static final long serialVersionUID = 3491930665127242286L;

	public ConnectorServiceException(String message) {
		super(message);
	}

	public ConnectorServiceException(String message, Throwable t) {
		super(message, t);
	}

}
