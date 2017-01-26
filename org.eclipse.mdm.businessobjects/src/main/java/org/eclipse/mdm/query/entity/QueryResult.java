package org.eclipse.mdm.query.entity;

import java.util.List;

public class QueryResult {
	private List<Row> rows;
	
	public QueryResult() {
		// TODO Auto-generated constructor stub
	}

	public QueryResult(List<Row> rows) {
		this.rows = rows;
	}

	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}
}
