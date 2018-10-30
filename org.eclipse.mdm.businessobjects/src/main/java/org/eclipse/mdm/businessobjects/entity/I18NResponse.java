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
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;

import java.util.Set;

/**
 * I18NResponse (Container for {@link I18NLocalization}s)
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class I18NResponse {

	/** transferable data content */
	private final List<I18NLocalization> data;

	/**
	 * 
	 * @param localizedEntityTypeMap
	 * @param localizedAttributeMap
	 */
	public I18NResponse(Map<EntityType, String> localizedEntityTypeMap, Map<Attribute, String> localizedAttributeMap) {
		this.data = toTransferable(localizedEntityTypeMap, localizedAttributeMap);
	}

	public List<I18NLocalization> getData() {
		return Collections.unmodifiableList(this.data);
	}

	private List<I18NLocalization> toTransferable(Map<EntityType, String> localizedEntityTypeMap,
			Map<Attribute, String> localizedAttributeMap) {

		List<I18NLocalization> localizationList = new ArrayList<>();

		Set<Entry<EntityType, String>> entityTypeSet = localizedEntityTypeMap.entrySet();

		for (Entry<EntityType, String> entry : entityTypeSet) {

			EntityType entityType = entry.getKey();
			String key = entityType.getName();

			localizationList.add(new I18NLocalization(key, entry.getValue()));
		}

		Set<Entry<Attribute, String>> attributeSet = localizedAttributeMap.entrySet();

		for (Entry<Attribute, String> entry : attributeSet) {

			Attribute attribute = entry.getKey();
			EntityType entityType = attribute.getEntityType();
			String key = entityType.getName() + "." + attribute.getName();

			localizationList.add(new I18NLocalization(key, entry.getValue()));
		}

		return localizationList;
	}
}
