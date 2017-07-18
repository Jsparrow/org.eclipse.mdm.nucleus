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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mdm.api.base.model.BaseEntity;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Value;

/**
 * MDMEntity (Entity for a business object (contains a list of
 * {@link MDMAttribute}s)
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class MDMEntity {

	/** name of the MDM business object */
	private final String name;
	/** id of the MDM business object */
	private final String id;
	/** type as String of the MDM business object (e.g. TestStep) */
	private final String type;
	/** source type name of the business object at the data source */
	private final String sourceType;
	/** source name (e.g. MDM Environment name) */
	private final String sourceName;
	/** list of attribute to transfer */
	private List<MDMAttribute> attributes;

	/**
	 * Constructor.
	 * 
	 * @param entity
	 *            the business object
	 */
	public MDMEntity(Entity entity) {
		this.name = entity.getName();
		this.id = Long.toString(entity.getID());
		this.type = entity.getClass().getSimpleName();
		this.sourceType = entity.getTypeName();
		this.sourceName = entity.getSourceName();
		this.attributes = convertAttributeValues(entity.getValues());
	}

	public String getName() {
		return this.name;
	}

	public String getId() {
		return this.id;
	}

	public String getType() {
		return this.type;
	}

	public String getSourceType() {
		return this.sourceType;
	}

	public String getSourceName() {
		return this.sourceName;
	}

	public List<MDMAttribute> getAttributes() {
		return Collections.unmodifiableList(this.attributes);
	}

	/**
	 * converts the MDM business object values to string values
	 * 
	 * @param values
	 *            values of a MDM business object
	 * @return list with converted attribute values
	 */
	private List<MDMAttribute> convertAttributeValues(Map<String, Value> values) {
		List<MDMAttribute> attributes = new ArrayList<>();
		Set<java.util.Map.Entry<String, Value>> set = values.entrySet();

		for (java.util.Map.Entry<String, Value> entry : set) {

			if (entry.getKey().equals(BaseEntity.ATTR_ID)) {
				continue;
			}

			if (!entry.getValue().isValid()) {
				String dt = entry.getValue().getValueType().toString();
				attributes.add(new MDMAttribute(entry.getKey(), "", "", dt));
				continue;
			}

			if (entry.getValue().getValueType().isSequence()) {
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
	 * @param name
	 *            name of the attribute value
	 * @param singleValue
	 *            single MDM business object value
	 * @return the converted attribute value
	 */
	private MDMAttribute singleType2Attribute(String name, Value singleValue) {
		String value = singleValue.extract().toString();
		String unit = singleValue.getUnit();
		String dt = singleValue.getValueType().toString();
		return new MDMAttribute(name, value, unit, dt);
	}

	/**
	 * converts a sequence type MDM business object value to a attribute
	 * 
	 * @param name
	 *            name of the attribute value
	 * @param sequenceValue
	 *            sequence MDM business object value
	 * @return the converted attribute value
	 */
	private MDMAttribute sequenceType2Attribute(String name, Value sequenceValue) {

		if (sequenceValue.getValueType().isStringSequence()) {
			return stringSeq2Attribute(name, sequenceValue);
		}

		String dt = sequenceValue.getValueType().toString();
		String defValue = "sequence type '" + dt + "' not implemented yet";
		return new MDMAttribute(name, defValue, "", dt);
	}

	/**
	 * converts a string sequence MDM business object value to a attribute The
	 * result is a separated string (separator: ';')
	 * 
	 * @param name
	 *            name of the attribute value
	 * @param value
	 *            string sequence MDM business object value
	 * @return the converted attribute value
	 */
	private MDMAttribute stringSeq2Attribute(String name, Value value) {
		String[] stringSeq = value.extract();
		StringBuffer sb = new StringBuffer();

		for (String stringSeqValue : stringSeq) {
			sb.append(";").append(stringSeqValue);
		}

		String stringValue = sb.toString().replaceFirst(";", "");
		String unit = value.getUnit();
		String dt = value.getValueType().toString();
		return new MDMAttribute(name, stringValue, unit, dt);
	}

}
