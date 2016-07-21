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

package org.eclipse.mdm.freetextindexer.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mdm.api.base.model.BaseEntity;
import org.eclipse.mdm.api.base.model.Value;

/**
 * MDMEntity (Entity for a business object (contains a list of
 * {@link MDMAttribute}s)
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class MDMEntity {

	private static final Set<String> NOTINDEXED = new HashSet<>(
			Arrays.asList(BaseEntity.ATTR_ID, BaseEntity.ATTR_MIMETYPE, "Optional", "SortIndex"));

	/** name of the MDM business object */
	public final String name;
	
	/** id of the MDM business object */
	public final long id;

	/** list of attribute to transfer */
	public final Map<String, String> attributes;

	public final List<MDMEntity> components = new ArrayList<>();

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name of the MDM business object
	 * @param type
	 *            type as String of the MDM business object (e.g. TestStep)
	 * @param uri
	 *            URI of an MDM business object
	 * @param values
	 *            values of a MDM business object
	 */
	public MDMEntity(String name, String type, long id, Map<String, Value> values) {
		this.name = name;
		this.id = id;
		
		this.attributes = initAttributes(values);
	}

	/**
	 * converts the MDM business object values to string values
	 * 
	 * @param values
	 *            values of a MDM business object
	 * @return list with converted attribute values
	 */
	private Map<String, String> initAttributes(Map<String, Value> values) {
		Map<String, String> attributes = new HashMap<>();
		for (java.util.Map.Entry<String, Value> entry : values.entrySet()) {

			String key = entry.getKey();
			if (!NOTINDEXED.contains(key) && entry.getValue().isValid()) {
				attributes.put(key, entry.getValue().extract().toString());
			}

		}
		return attributes;
	}

}