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


package org.eclipse.mdm.freetextindexer.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

	private static final Set<String> NOTINDEXED = Collections.unmodifiableSet(new HashSet<>(
			Arrays.asList(BaseEntity.ATTR_ID, BaseEntity.ATTR_MIMETYPE, "Optional", "SortIndex")));

	/** name of the MDM business object */
	public final String name;
	
	/** type of the MDM business object */
	public final String type;

	/** id of the MDM business object */
	public final String id;

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
	public MDMEntity(String name, String type, String id, Map<String, Value> values) {
		this.name = name;
		this.type = type;
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
		Map<String, String> mapAttrs = new HashMap<>();
		values.entrySet().forEach(entry -> {

			String key = entry.getKey();
			if (!NOTINDEXED.contains(key) && entry.getValue().isValid()) {
				mapAttrs.put(key, entry.getValue().extract().toString());
			}
		});
		
		return mapAttrs;
	}

}
