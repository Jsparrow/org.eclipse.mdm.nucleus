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


package org.eclipse.mdm.filerelease.control.converter;

/**
 * FileConverterException
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class FileConverterException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The error message
	 */
	public FileConverterException(String message) {
		super(message);
	}

	/**
	 * Constructor#
	 * 
	 * @param message
	 *            The error message
	 * @param t
	 *            The {@link Throwable} that caused the exception.
	 */
	public FileConverterException(String message, Throwable t) {
		super(message, t);
	}

}
