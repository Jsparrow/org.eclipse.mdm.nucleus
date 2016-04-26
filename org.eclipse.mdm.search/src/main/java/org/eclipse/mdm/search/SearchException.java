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

package org.eclipse.mdm.search;

/**
 * SearchException
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class SearchException extends Exception {
	
	
	
	private static final long serialVersionUID = -6250897002302846523L;


	
	/**
	 * Constructor 
	 * @param message error message
	 */
	public SearchException(String message) {
		super(message);
	}
	
	
	
	/**
	 * Constructor
	 * @param message error message
	 * @param t parent throwable
	 */
	public SearchException(String message, Throwable t) {
		super(message, t);
	}
}