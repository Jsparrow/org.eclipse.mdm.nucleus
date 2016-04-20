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

package org.eclipse.mdm.search.rest.transferable;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mdm.search.SearchDefinition;

/**
 * Transferable SearchDefinitionResponse (Container for {@link SearchDefinition}s)
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class SearchDefinitionResponse {

	
	
	/** transferable data content */
	public List<SearchDefinition> data;
	
	
	
	/**
	 * Constructor
	 * @param searchDefinitions list of {@link SearchDefinition}s to transfer
	 */
	public SearchDefinitionResponse(List<SearchDefinition> searchDefinitions) {
		this.data = searchDefinitions;
	}
	
	
	/**
	 * Constructor
	 */
	public SearchDefinitionResponse() {
		this.data = new ArrayList<>();
	}
}
