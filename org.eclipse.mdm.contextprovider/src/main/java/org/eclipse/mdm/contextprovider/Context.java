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

package org.eclipse.mdm.contextprovider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;

/**
 * Context collection for ordered and measured context
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class Context {

	
	
	private Map<ContextType, ContextRoot> orderedContext = new HashMap<>();
	private Map<ContextType, ContextRoot> measuredContext = new HashMap<>();
	
	
	
	/**
	 * sets the ordered context data map
	 * @param orderedContext the ordered context data map
	 */
	public void setOrderedContext(Map<ContextType, ContextRoot> orderedContext) {
		this.orderedContext = orderedContext;
	}
	
	
	
	/**
	 * sets the measured context data map
	 * @param measuredContext the measured context data map
	 */
	public void setMeasuredContext(Map<ContextType, ContextRoot> measuredContext) {
		this.measuredContext = measuredContext;
	}
	
	
	
	/**
	 * returns the ordered context data map
	 * @return the ordered context data map
	 */
	public Map<ContextType, ContextRoot> getOrderedContext() {
		return orderedContext;
	}
	
	
	
	/**
	 * returns the measured context data map
	 * @return the measrued context data map
	 */
	public Map<ContextType, ContextRoot> getMeasuredContext() {
		return measuredContext;
	}
}
