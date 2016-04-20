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

package org.eclipse.mdm.businesstyperegistry.rest.transferable;

/**
 * Transferable Action
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class Action {
	
	
	
	/** name of the action */
	public String actionName;
	
	
	
	/**
	 * Constructor
	 * @param actionName name of the action
	 */
	public Action(String actionName) {
		this.actionName = actionName;		
	}
}
