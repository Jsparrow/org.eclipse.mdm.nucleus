/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.businesstyperegistry.rest.transferable;

/**
 * Transferable Attribute
 * @author Gigatronik Ingolstadt GmbH
 *
 */
public class Attribute {

	/** name of the attribute value */
	public final String name;
	/** string value of the attribute value*/
	public final String value;
	/** unit of the attribute value*/
	public final String unit;
	/** data type of the attribute value */
	public final String dataType;
	
	
	
	/**
	 * Constructor
	 *  
	 * @param name name of the attribute value
	 * @param value string value of the attribute value
	 * @param unit unit of the attribute value
	 * @param dataType data type of the attribute value
	 */
	public Attribute(String name, String value, String unit, String dataType) {
		this.name = name;
		this.value = value;
		this.unit = unit;
		this.dataType = dataType;
	}
}
