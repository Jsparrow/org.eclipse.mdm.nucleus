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

package org.eclipse.mdm.businessobjects.entity;

public class MDMEntityAccessException extends RuntimeException {
	
	private static final long serialVersionUID = -4196754501456211418L;

	public MDMEntityAccessException(String message) {
		super(message);
	}	

	public MDMEntityAccessException(String message, Throwable t) {
		super(message, t);
	}
	
}
