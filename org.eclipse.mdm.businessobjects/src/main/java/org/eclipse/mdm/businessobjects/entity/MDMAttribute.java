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

package org.eclipse.mdm.businessobjects.entity;

/**
 * Attribute (Entity for attribute informations)
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class MDMAttribute {

	/** name of the attribute value */
	private final String name;
	/** string value of the attribute value*/
	private final String value;
	/** unit of the attribute value*/
	private final String unit;
	/** data type of the attribute value */
	private final String dataType;
	
	
	
	/**
	 * Constructor
	 *  
	 * @param name name of the attribute value
	 * @param value string value of the attribute value
	 * @param unit unit of the attribute value
	 * @param dataType data type of the attribute value
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
