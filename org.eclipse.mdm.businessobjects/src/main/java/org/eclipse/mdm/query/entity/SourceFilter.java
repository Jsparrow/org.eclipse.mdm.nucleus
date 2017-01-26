package org.eclipse.mdm.query.entity;

public class SourceFilter {
	private String sourceName;
	private String filter;
	private String searchString;
	
	public SourceFilter() {
		// TODO Auto-generated constructor stub
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	public String getSearchString() {
		return searchString;
	}
	
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
}
