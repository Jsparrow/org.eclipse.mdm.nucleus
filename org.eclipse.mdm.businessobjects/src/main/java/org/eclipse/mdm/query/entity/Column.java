/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/

package org.eclipse.mdm.query.entity;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jersey.repackaged.com.google.common.base.MoreObjects;

/**
 * 
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
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
	public int hashCode() {
		return Objects.hash(type, attribute, value, unit);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}
		final Column other = (Column) obj;
		return Objects.equals(this.type, other.type) && Objects.equals(this.attribute, other.attribute)
				&& Objects.equals(this.value, other.value) && Objects.equals(this.unit, other.unit);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(Column.class).add("type", type).add("attribute", attribute)
				.add("value", value).add("unit", unit).toString();
	}
}