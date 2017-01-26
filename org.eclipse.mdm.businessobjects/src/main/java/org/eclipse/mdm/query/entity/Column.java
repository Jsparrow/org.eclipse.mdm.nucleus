package org.eclipse.mdm.query.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jersey.repackaged.com.google.common.base.MoreObjects;

@JsonInclude(Include.NON_NULL)
public class Column {
	private String type;
	private String attribute;
	private String value;
	private String unit;
	
	public Column(String type, String attribute, String value, String unit) {
		this.type = type;
		this.attribute = attribute;
		this.value = value;
		this.unit = unit;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(Column.class)
				.add("type", type)
				.add("attribute", attribute)
				.add("value", value)
				.add("unit", unit)
				.toString();
	}
}