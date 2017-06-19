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
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Set;

import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.EntityType;

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
