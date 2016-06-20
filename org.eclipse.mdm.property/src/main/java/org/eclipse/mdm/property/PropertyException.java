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
public class PropertyException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8975069381247044049L;

	/**
	 * Constructor.
	 * @param error The error message.
	 */
	public PropertyException(String error) {
		super(error);
	}
	
}
