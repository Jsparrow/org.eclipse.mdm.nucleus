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
import java.util.List;

import org.eclipse.mdm.api.base.model.ContextSensor;

/**
 * ContextSensorCollection (Entity for context data (ordered and measured))
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class ContextSensorCollection {
	
	public List<MDMEntity> sensorContextMeasured = new ArrayList<MDMEntity>();
	public List<MDMEntity> sensorContextOrdered= new ArrayList<MDMEntity>();	
	
	
	/**
	 * set the measured context sensor data list
	 * @param sensorList the measured context sensor data list
	 */
	public void setMeasuredSensorContext(List<ContextSensor> sensorList) {

		for(ContextSensor contextSensor : sensorList) {
			MDMEntity entity = new MDMEntity(contextSensor);
			this.sensorContextMeasured.add(entity);		
		}
	}
	
	
	/**
	 * set the ordered context sensor data list
	 * @param sensorList the ordered context sensor data list
	 */
	public void setOrderedSensorContext(List<ContextSensor> sensorList) {

		for(ContextSensor contextSensor : sensorList) {
			MDMEntity entity = new MDMEntity(contextSensor);
			this.sensorContextOrdered.add(entity);		
		}
	}
		
	
}
