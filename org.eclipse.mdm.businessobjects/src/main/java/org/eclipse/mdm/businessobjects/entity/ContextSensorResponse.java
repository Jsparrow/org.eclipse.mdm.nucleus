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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.mdm.api.base.model.ContextSensor;
import org.eclipse.mdm.businessobjects.control.ContextActivity;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextSensorResponse {

	/** transferable data content */
	private List<ContextSensorCollection> data;

	/**
	 * Constructor
	 * 
	 * @param contextMap
	 *            map with context data (ordered and measured)
	 */
	public ContextSensorResponse(Map<String, List<ContextSensor>> sensorMap) {
		this.data = new ArrayList<>();
		ContextSensorCollection contextSensorData = new ContextSensorCollection();
		if (sensorMap.containsKey(ContextActivity.CONTEXT_SENSOR_GROUP_ORDERED)) {
			contextSensorData.setOrderedSensorContext(sensorMap.get(ContextActivity.CONTEXT_SENSOR_GROUP_ORDERED));
		}
		if (sensorMap.containsKey(ContextActivity.CONTEXT_SENSOR_GROUP_MEASURED)) {
			contextSensorData.setMeasuredSensorContext(sensorMap.get(ContextActivity.CONTEXT_SENSOR_GROUP_MEASURED));
		}
		this.data.add(contextSensorData);
	}

	/**
	 * returns the {@link ContextSensor} data
	 * 
	 * @return the {@link ContextSensor} data
	 */
	public List<ContextSensorCollection> getData() {
		return Collections.unmodifiableList(this.data);
	}
}
