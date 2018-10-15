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


package org.eclipse.mdm.property;

/**
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class GlobalPropertyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8975069381247044049L;

	/**
	 * Constructor.
	 * 
	 * @param error
	 *            error message.
	 */
	public GlobalPropertyException(String error) {
		super(error);
	}

	/**
	 * Constructor
	 * 
	 * @param error
	 *            error message
	 * @param e
	 *            throwable
	 */
	public GlobalPropertyException(String error, Exception e) {
		super(error, e);
	}

}
