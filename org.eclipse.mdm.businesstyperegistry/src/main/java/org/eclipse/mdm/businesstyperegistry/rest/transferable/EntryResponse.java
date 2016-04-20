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

package org.eclipse.mdm.businesstyperegistry.rest.transferable;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mdm.api.base.model.Entity;

/**
 * Transferable EntryResponse (Container for {@link Entity}s)
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class EntryResponse {

	
	
	/** type of all content entries (e.g. TestStep) */
	public final String type;	
	/** transferable data content */
	public final List<Entry> data;
	
	
	
	/**
	 * Constructor
	 * 
	 * @param type type of all content entries (e.g. TestStep.class)
	 * @param entries list of entries (MDM business objects)
	 */
	public EntryResponse(Class<? extends Entity> type, List<Entry> entries) {
		this.type = type.getSimpleName();
		this.data = entries;
	}	
	
	
	
	/**
	 * Constructor
	 */
	public EntryResponse() {
		this.type = "";
		this.data = new ArrayList<Entry>();
	}
}
