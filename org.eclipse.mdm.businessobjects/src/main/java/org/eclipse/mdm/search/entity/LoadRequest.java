package org.eclipse.mdm.search.entity;

import java.util.List;

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
