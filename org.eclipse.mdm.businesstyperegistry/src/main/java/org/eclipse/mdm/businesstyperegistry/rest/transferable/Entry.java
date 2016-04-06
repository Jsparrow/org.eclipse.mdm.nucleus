/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.businesstyperegistry.rest.transferable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mdm.api.base.model.BaseEntity;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.api.base.model.Value;

/**
 * Transferable Entry
 * @author Gigatronik Ingolstadt GmbH
 *
 */
public class Entry {

	/** name of the MDM business object */
	public final String name;
	/** id of the MDM business object */
	public final long id;
	/** type as String of the MDM business object (e.g. TestStep) */
	public final String type;
	/** source type name of the business object at the data source */
	public final String sourceType;
	/** source name (e.g. MDM Environment name) */
	public final String sourceName;
	/** list of attribute to transfer */
	public final List<Attribute> attributes;
	
	
	
	/**
	 * Constructor 
	 * 
	 * @param name name of the MDM business object
	 * @param type type as String of the MDM business object (e.g. TestStep)
	 * @param uri URI of an MDM business object
	 * @param values values of a MDM business object
	 */
	public Entry(String name, String type, URI uri, Map<String, Value> values) {
		this.name = name;
		this.id = uri.getID();	
		this.type = type;
		this.sourceType = uri.getTypeName();
		this.sourceName = uri.getSourceName();
		this.attributes = convertAttributeValues(values);
	}
	
	
	
	/**
	 * converts the MDM business object values to string values
	 * 
	 * @param values values of a MDM business object
	 * @return list with converted attribute values
	 */
	private List<Attribute> convertAttributeValues(Map<String, Value> values) {
		List<Attribute> attributes = new ArrayList<>();
		Set<java.util.Map.Entry<String, Value>> set = values.entrySet();
		
		for(java.util.Map.Entry<String, Value> entry : set) {
			
			if(entry.getKey().equals(BaseEntity.ATTR_ID)) {
				continue;
			}
			
			if(!entry.getValue().isValid()) {
				String dt = entry.getValue().getValueType().toString();
				attributes.add(new Attribute(entry.getKey(), "", "", dt));
				continue;
			}
			
			if(entry.getValue().getValueType().isSequence()) {
				attributes.add(sequenceType2Attribute(entry.getKey(), entry.getValue()));
			} else {
				attributes.add(singleType2Attribute(entry.getKey(), entry.getValue()));
			}		
		}
		return attributes;
 	}
	
	
	
	/**
	 * converts a single type MDM business object value to a attribute
	 * 
	 * @param name name of the attribute value
	 * @param singleValue single MDM business object value
	 * @return the converted attribute value
	 */
	private Attribute singleType2Attribute(String name, Value singleValue) {
		String value = singleValue.extract().toString();
		String unit = singleValue.getUnit();
		String dt = singleValue.getValueType().toString();		
		return new Attribute(name, value, unit, dt);
	}
	
	
	
	/**
	 * converts a sequence type MDM business object value to a attribute
	 * 
	 * @param name name of the attribute value
	 * @param sequenceValue sequence MDM business object value
	 * @return the converted attribute value
	 */
	private Attribute sequenceType2Attribute(String name, Value sequenceValue) {
		
		if(sequenceValue.getValueType().isStringSequence()) {
			return stringSeq2Attribute(name, sequenceValue);		
		} 
		
		String dt = sequenceValue.getValueType().toString();
		String defValue = "sequence type '" + dt + "' not implemented yet";
		return new Attribute(name, defValue, "", dt);
	}
	
	
	
	/**
	 * converts a string sequence MDM business object value to a attribute
	 * The result is a separated string (separator: ';')
	 * 
	 * @param name name of the attribute value 
	 * @param value string sequence MDM business object value
	 * @return the converted attribute value
	 */
	private Attribute stringSeq2Attribute(String name, Value value) {
		String[] stringSeq = value.extract();	
		StringBuffer sb = new StringBuffer();
		
		for(String stringSeqValue : stringSeq) {
			sb.append(";").append(stringSeqValue);
		}
		
		String stringValue = sb.toString().replaceFirst(";", "");
		String unit = value.getUnit();
		String dt = value.getValueType().toString();	
		return new Attribute(name, stringValue, unit, dt);
	}
	
}
