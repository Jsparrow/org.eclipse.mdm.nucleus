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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.businessobjects.control.ContextActivity;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextResponse {

	/** transferable data content */
	private List<ContextCollection> data;

	/**
	 * Constructor
	 * 
	 * @param contextMap
	 *            map with context data (ordered and measured)
	 */
	public ContextResponse(Map<String, Map<ContextType, ContextRoot>> contextMap) {
		this.data = new ArrayList<>();
		ContextCollection contextData = new ContextCollection();
		contextData.setOrderedContext(contextMap.get(ContextActivity.CONTEXT_GROUP_ORDERED));
		contextData.setMeasuredContext(contextMap.get(ContextActivity.CONTEXT_GROUP_MEASURED));
		this.data.add(contextData);
	}

	/**
	 * returns the context data
	 * 
	 * @return the context data
	 */
	public List<ContextCollection> getData() {
		return Collections.unmodifiableList(this.data);
	}
}
