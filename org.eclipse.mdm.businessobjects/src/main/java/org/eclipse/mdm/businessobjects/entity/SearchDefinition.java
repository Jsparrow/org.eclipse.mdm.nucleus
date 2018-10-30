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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SearchDefinition (Entity with a list of defined {@link SearchAttribute}s)
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class SearchDefinition {

	private final String name;
	private final String resultType;
	private List<SearchAttribute> attributeList;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name of this search definition
	 * @param resultType
	 *            result type by executing this defined search (e.g. TestStep)
	 */
	public SearchDefinition(String name, String resultType) {
		this.name = name;
		this.resultType = resultType;
		this.attributeList = new ArrayList<>();
	}

	/**
	 * adds an {@link SearchAttribute} to this SearchDefinition
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
	public void addSearchAttribute(String boType, String attrName, String valueType, String criteria) {
		this.attributeList.add(new SearchAttribute(boType, attrName, valueType, criteria));
	}

	/**
	 * returns the name of this search definition
	 * 
	 * @return the name of this search definition
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * returns the result type of this search definition
	 * 
	 * @return the result type of this search definition
	 */
	public String getResultType() {
		return this.resultType;
	}

	/**
	 * lists the defined {@link SearchAttribute}s for this search definition
	 * 
	 * @return the defined {@link SearchAttribute}s for ths search definition
	 */
	public List<SearchAttribute> listSearchAttributes() {
		return Collections.unmodifiableList(this.attributeList);
	}

}
