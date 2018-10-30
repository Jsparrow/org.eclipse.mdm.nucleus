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
 * SearchAttibute (Entity for {@link SearchAttribute}s defined at a
 * {@link SearchDefinition})
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class SearchAttribute {

	private final String boType;
	private final String attrName;
	private final String valueType;
	private final String criteria;

	/**
	 * Constructor
	 * 
	 * @param boType
	 *            name of MDM business object (e.g. TestStep)
	 * @param attrName
	 *            name of an attribute for the MDM business object (e.g. Name)
	 * @param valueType
	 *            value type of this attribute (e.g. String)
	 * @param criteria
	 *            default criteria
	 */
	public SearchAttribute(String boType, String attrName, String valueType, String criteria) {
		this.boType = boType;
		this.attrName = attrName;
		this.valueType = valueType;
		this.criteria = criteria;
	}

	/**
	 * returns the name of the MDM business object (e.g. TestStep)
	 * 
	 * @return the name of the MDM business object (e.g. TestStep)
	 */
	public String getBoType() {
		return this.boType;
	}

	/**
	 * returns the name of an attribute for the MDM business object (e.g. Name)
	 * 
	 * @return the name of an attribute for the MDM business object (e.g. Name)
	 */
	public String getAttrName() {
		return this.attrName;
	}

	/**
	 * returns the value type of this attribute (e.g. String)
	 * 
	 * @return the value type of this attribute (e.g. String)
	 */
	public String getValueType() {
		return this.valueType;
	}

	/**
	 * returns the default criteria
	 * 
	 * @return the default criteria
	 */
	public String getCriteria() {
		return this.criteria;
	}

}
