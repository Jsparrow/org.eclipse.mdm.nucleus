package org.eclipse.mdm.query.entity;

import java.util.List;

public class QueryRequest {

	private List<SourceFilter> filters;
	private List<String> columns;
	private String resultType;
	
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
	public String getResultType() {
		return resultType;
	}
	
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
}
