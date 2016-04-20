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

package org.eclipse.mdm.i18n;

/**
 * I18NException
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class I18NException extends Exception {
	
	
	
	private static final long serialVersionUID = -6250897002302846523L;

	
	
	/**
	 * Constructor 
	 * @param message error message
	 */
	public I18NException(String message) {
		super(message);
	}
	
	
	
	/**
	 * Constructor
	 * @param message error message
	 * @param throwable parent throwable
	 */
	public I18NException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
