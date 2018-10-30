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


package org.eclipse.mdm.businessobjects.entity;

/**
 * Attribute (Entity for attribute informations)
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class MDMAttribute {

	/** name of the attribute value */
	private final String name;
	/** string value of the attribute value */
	private final String value;
	/** unit of the attribute value */
	private final String unit;
	/** data type of the attribute value */
	private final String dataType;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name of the attribute value
	 * @param value
	 *            string value of the attribute value
	 * @param unit
	 *            unit of the attribute value
	 * @param dataType
	 *            data type of the attribute value
	 */
	public MDMAttribute(String name, String value, String unit, String dataType) {
		this.name = name;
		this.value = value;
		this.unit = unit;
		this.dataType = dataType;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	public String getUnit() {
		return this.unit;
	}

	public String getDataType() {
		return this.dataType;
	}
}
