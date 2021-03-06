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


package org.eclipse.mdm.businessobjects.control;

public class XMLFormatException extends RuntimeException {

	private static final long serialVersionUID = -4196754501456211418L;

	public XMLFormatException(String message) {
		super(message);
	}

	public XMLFormatException(String message, Throwable t) {
		super(message, t);
	}

}
