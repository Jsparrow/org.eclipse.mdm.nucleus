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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mdm.api.base.model.ContextComponent;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.model.Value;

/**
 * ContextData (Entity for context data (ordered and measured))
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class ContextCollection {
	
	public Map<ContextType, List<MDMEntity>> contextMeasured = new HashMap<>();
	public Map<ContextType, List<MDMEntity>> contextOrdered= new HashMap<>();	
	
	
	
	
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
				MDMEntity entity = contextComponent2Entry(contextComponent);
				this.contextMeasured.get(contextType).add(entity);		
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
				MDMEntity entity = contextComponent2Entry(contextComponent);
				this.contextOrdered.get(contextType).add(entity);		
			}
		}
	}
	
	
	
	private MDMEntity contextComponent2Entry(ContextComponent contextComponent) {
		String name = contextComponent.getName();
		String type = ContextComponent.class.getName();
		URI uri = contextComponent.getURI();
		Map<String, Value> values = contextComponent.getValues();
		return new MDMEntity(name, type, uri, values);
	}
	
	
}
