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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.mdm.api.base.model.Entity;

/**
 * EntryResponse (Container for {@link MDMEntity}s)
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MDMEntityResponse {

	
	
	/** type of all content entries (e.g. TestStep) */
	private final String type;	
	/** transferable data content */
	private final List<MDMEntity> data;
	
	
	
	/**
	 * Constructor (for a list of business objects {@link MDMEntity}s)
	 * 
	 * @param type type of all containging {@link MDMEntity}s
	 * @param entries list of {@link MDMEntity}
	 */
	public <T extends Entity> MDMEntityResponse(Class<? extends Entity> type, List<T> businessObjects) {
		this.type = type.getSimpleName();
		this.data = toTransferable(businessObjects);
	}	
	
	
		
	/**
	 * Constructor (for a single business object {@link MDMEntity})
	 * 
	 * @param type type of the {@link MDMEntity}
	 * @param businessObject single {@link MDMEntity}
	 */
	public <T extends Entity> MDMEntityResponse(Class<? extends Entity> type, T businessObject) {
		List<T> businessObjects = new ArrayList<>();
		businessObjects.add(businessObject);
		this.type = type.getSimpleName();
		this.data = toTransferable(businessObjects);		
	}
	
	
	
	public String getType() {
		return this.type;
	}
	
	
	public List<MDMEntity> getData() {
		return Collections.unmodifiableList(this.data);
	}
	
	private <T extends Entity> List<MDMEntity> toTransferable(List<T> businessObjects) {
		List<MDMEntity> mdmEntityList = new ArrayList<MDMEntity>();
		for(Entity  businessObject : businessObjects) {				
			MDMEntity mdmEntity = new MDMEntity( businessObject.getName(),  businessObject.getClass().getSimpleName(),
				businessObject.getURI(), businessObject.getValues());
			
			mdmEntityList.add(mdmEntity);
		}
		return mdmEntityList;	
	}	

}
