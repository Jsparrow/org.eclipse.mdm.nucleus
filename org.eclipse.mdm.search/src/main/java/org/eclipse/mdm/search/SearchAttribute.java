/*******************************************************************************
  * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Sebastian Dirsch - initial implementation
  *******************************************************************************/

package org.eclipse.mdm.search;

/**
 * SearchAtribute class to define a searchable attribute
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
	 * @param boType name of MDM business object (e.g. TestStep)
	 * @param attrName name of an attribute for the MDM business object (e.g. Name)
	 * @param valueType value type of this attribute (e.g. String)
	 * @param criteria default criteria
	 */
	public SearchAttribute(String boType, String attrName, String valueType, String criteria) {
		this.boType = boType;
		this.attrName = attrName;
		this.valueType = valueType;
		this.criteria = criteria;
		
	}
	
	
	
	/**
	 * returns the name of the MDM business object (e.g. TestStep)
	 * @return the name of the MDM business object (e.g. TestStep)
	 */
	public String getBoType() {
		return boType;
	}
	
	
	
	/**
	 * returns the name of an attribute for the MDM business object (e.g. Name)
	 * @return the name of an attribute for the MDM business object (e.g. Name)
	 */
	public String getAttrName() {
		return attrName;
	}
	
	
	/**
	 * returns the value type of this attribute (e.g. String)
	 * @return the value type of this attribute (e.g. String)
	 */
	public String getValueType() {
		return valueType;
	}
	
	
	/**
	 * returns the default criteria 
	 * @return the default criteria 
	 */
	public String getCriteria() {
		return criteria;
	}
	
}
