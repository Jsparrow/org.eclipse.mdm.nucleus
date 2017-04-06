/*******************************************************************************
  * Copyright (c) 2017 Peak Solution GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Matthias Koller - initial implementation
  *******************************************************************************/
package org.eclipse.mdm.query.entity;

import java.util.List;

/**
 * 
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
public class LoadRequest {
	private List<EntityId> entityIds;
	private List<String> columns;
	
	public LoadRequest() {
		// TODO Auto-generated constructor stub
	}
	
	public List<EntityId> getEntityIds() {
		return entityIds;
	}
	
	public void setEntityIds(List<EntityId> entityIds) {
		this.entityIds = entityIds;
	}
	
	public List<String> getColumns() {
		return columns;
	}
	
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
}
