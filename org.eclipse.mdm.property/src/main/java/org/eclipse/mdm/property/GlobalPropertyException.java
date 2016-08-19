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
	 * @param error  error message.
	 */
	public GlobalPropertyException(String error) {
		super(error);
	}
	
	/**
	 * Constructor
	 * @param error error message
	 * @param e throwable
	 */
	public GlobalPropertyException(String error, Exception e) {
		super(error, e);
	}
	
}
