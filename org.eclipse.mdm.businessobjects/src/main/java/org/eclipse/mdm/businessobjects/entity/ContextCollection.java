/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


package org.eclipse.mdm.businessobjects.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mdm.api.base.model.ContextComponent;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;

/**
 * ContextCollection (Entity for context data (ordered and measured))
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class ContextCollection {

	public Map<ContextType, List<MDMEntity>> contextMeasured = new HashMap<>();
	public Map<ContextType, List<MDMEntity>> contextOrdered = new HashMap<>();

	/**
	 * set the measured context data map
	 * 
	 * @param contextMap
	 *            the measured context data map
	 */
	public void setMeasuredContext(Map<ContextType, ContextRoot> contextMap) {

		for (java.util.Map.Entry<ContextType, ContextRoot> setEntry : contextMap.entrySet()) {

			ContextType contextType = setEntry.getKey();
			ContextRoot contextRoot = setEntry.getValue();

			this.contextMeasured.put(contextType, new ArrayList<>());

			for (ContextComponent contextComponent : contextRoot.getContextComponents()) {
				MDMEntity entity = new MDMEntity(contextComponent);
				this.contextMeasured.get(contextType).add(entity);
			}
		}
	}

	/**
	 * sets the ordered context data map
	 * 
	 * @param contextMap
	 *            the ordered context data map
	 */
	public void setOrderedContext(Map<ContextType, ContextRoot> contextMap) {

		for (java.util.Map.Entry<ContextType, ContextRoot> setEntry : contextMap.entrySet()) {

			ContextType contextType = setEntry.getKey();
			ContextRoot contextRoot = setEntry.getValue();

			this.contextOrdered.put(contextType, new ArrayList<>());

			for (ContextComponent contextComponent : contextRoot.getContextComponents()) {
				MDMEntity entity = new MDMEntity(contextComponent);
				this.contextOrdered.get(contextType).add(entity);
			}
		}
	}

}
