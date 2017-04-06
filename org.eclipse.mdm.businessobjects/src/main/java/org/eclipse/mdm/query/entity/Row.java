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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jersey.repackaged.com.google.common.base.MoreObjects;

/**
 * 
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
public class Row {

	private String source;
	private String type;
	private Long id;
	
	private List<Column> columns = new ArrayList<>();
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void addColumn(Column col) {
		columns.add(col);
	}
	
	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	
	public void addColumns(Collection<? extends Column> columns) {
		this.columns.addAll(columns);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(Row.class)
				.add("columns", columns)
				.toString();
	}
}