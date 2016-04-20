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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mdm.api.base.model.ContextComponent;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.businesstyperegistry.rest.transferable.Entry;

/**
 * Transferable ContextData
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class ContextData {
	
	
	
	public Map<ContextType, List<Entry>> contextMeasured = new HashMap<>();
	public Map<ContextType, List<Entry>> contextOrdered= new HashMap<>();	
	
	
	
	/**
	 * set the measured context data map
	 * @param contextMap the measured context data map
	 */
	public void setMeasuredContext(Map<ContextType, ContextRoot> contextMap) {
		for(java.util.Map.Entry<ContextType, ContextRoot> setEntry : contextMap.entrySet()) {
			
			ContextType contextType = setEntry.getKey();
			ContextRoot contextRoot = setEntry.getValue();
						
			this.contextMeasured.put(contextType, new ArrayList<>());
			
			for(ContextComponent contextComponent : contextRoot.getContextComponents()) {
				Entry entry = contextComponent2Entry(contextComponent);
				this.contextMeasured.get(contextType).add(entry);		
			}
		}
	}
	
	
	
	/**
	 * sets the ordered context data map
	 * @param contextMap the ordered context data map
	 */
	public void setOrderedContext(Map<ContextType, ContextRoot> contextMap) {
		for(java.util.Map.Entry<ContextType, ContextRoot> setEntry : contextMap.entrySet()) {
			
			ContextType contextType = setEntry.getKey();
			ContextRoot contextRoot = setEntry.getValue();
						
			this.contextOrdered.put(contextType, new ArrayList<>());
			
			for(ContextComponent contextComponent : contextRoot.getContextComponents()) {
				Entry entry = contextComponent2Entry(contextComponent);
				this.contextOrdered.get(contextType).add(entry);		
			}
		}
	}
	
	
	
	private Entry contextComponent2Entry(ContextComponent contextComponent) {
		String name = contextComponent.getName();
		String type = ContextComponent.class.getName();
		URI uri = contextComponent.getURI();
		Map<String, Value> values = contextComponent.getValues();
		return new Entry(name, type, uri, values);
	}
	
	
}
