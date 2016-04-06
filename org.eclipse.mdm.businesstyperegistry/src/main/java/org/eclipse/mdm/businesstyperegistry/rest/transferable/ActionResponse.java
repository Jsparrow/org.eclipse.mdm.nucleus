/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.businesstyperegistry.rest.transferable;

import java.util.ArrayList;
import java.util.List;

/**
 * Transferable ActionResponse (Container for {@link Action}s)
 * @author Gigatronik Ingolstadt GmbH
 *
 */
public class ActionResponse {

	
	
	/** transferable data content */
	public final List<Action> data;
	
	
	
	/**
	 * Constructor
	 * @param actions list of {@link Action}s to transfer
	 */
	public ActionResponse(List<Action> actions) {
		this.data = actions;
	}
	
	
	
	/**
	 * Constructor 
	 */
	public ActionResponse() {
		this.data = new ArrayList<>();
	}
}
