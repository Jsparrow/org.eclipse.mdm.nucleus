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
import java.util.List;

import org.eclipse.mdm.api.base.model.ContextSensor;

/**
 * ContextSensorCollection (Entity for context data (ordered and measured))
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class ContextSensorCollection {

	public List<MDMEntity> sensorContextMeasured = new ArrayList<MDMEntity>();
	public List<MDMEntity> sensorContextOrdered = new ArrayList<MDMEntity>();

	/**
	 * set the measured context sensor data list
	 * 
	 * @param sensorList
	 *            the measured context sensor data list
	 */
	public void setMeasuredSensorContext(List<ContextSensor> sensorList) {

		for (ContextSensor contextSensor : sensorList) {
			MDMEntity entity = new MDMEntity(contextSensor);
			this.sensorContextMeasured.add(entity);
		}
	}

	/**
	 * set the ordered context sensor data list
	 * 
	 * @param sensorList
	 *            the ordered context sensor data list
	 */
	public void setOrderedSensorContext(List<ContextSensor> sensorList) {

		for (ContextSensor contextSensor : sensorList) {
			MDMEntity entity = new MDMEntity(contextSensor);
			this.sensorContextOrdered.add(entity);
		}
	}

}
