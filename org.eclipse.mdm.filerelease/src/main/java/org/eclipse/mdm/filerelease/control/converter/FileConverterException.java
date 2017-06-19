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
