package org.eclipse.mdm.search.entity;

import java.util.List;

public class ResultRows {
	private List<Row> rows;
	
	public ResultRows() {
		// TODO Auto-generated constructor stub
	}

	public ResultRows(List<Row> rows) {
		this.rows = rows;
	}

	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}
}
