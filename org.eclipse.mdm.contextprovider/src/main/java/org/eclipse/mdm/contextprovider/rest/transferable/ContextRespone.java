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

package org.eclipse.mdm.contextprovider.rest.transferable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.contextprovider.Context;

/**
 * Transferable ContextResponse (Container for {@link ContextData)
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class ContextRespone {

	
	
	/** transferable data content */
	public List<ContextData> data = new ArrayList<>();
	
	
	
	/**
	 * Constructor
	 */
	public ContextRespone() {
	}
	
	
	
	/**
	 * Constructor
	 * @param context context collection with ordered and measured context 
	 */
	public ContextRespone(Context context) {
		
		Map<ContextType, ContextRoot> orderedContext = context.getOrderedContext();
		Map<ContextType, ContextRoot> measuredContext = context.getMeasuredContext();
		
		ContextData contextData = new ContextData();
		contextData.setOrderedContext(orderedContext);
		contextData.setMeasuredContext(measuredContext);
				
		this.data.add(contextData);
	}
	
	
	
}
