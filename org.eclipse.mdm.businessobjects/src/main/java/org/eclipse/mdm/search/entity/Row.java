package org.eclipse.mdm.search.entity;

import java.util.ArrayList;
import java.util.List;

public class Row {

	List<Column> columns = new ArrayList<>();
	public void addColumn(Column col) {
		columns.add(col);
	}
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
}