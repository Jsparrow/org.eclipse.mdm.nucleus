package org.eclipse.mdm.search.entity;

import java.util.List;

public class QueryRequest {

	private List<SourceFilter> filters;
	private List<String> columns;
	
	public QueryRequest() {
		// TODO Auto-generated constructor stub
	}
	
	public List<SourceFilter> getFilters() {
		return filters;
	}
	
	public void setFilters(List<SourceFilter> filters) {
		this.filters = filters;
	}
	
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
}
