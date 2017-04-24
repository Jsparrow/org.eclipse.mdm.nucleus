/*******************************************************************************
  * Copyright (c) 2017 Peak Solution GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Matthias Koller - initial implementation
  *******************************************************************************/
package org.eclipse.mdm.query.entity;

import java.util.List;

/**
 * 
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
public class SuggestionRequest {
	
	private List<String> sourceNames;
	private String type;
	private String attrName;
	
	public SuggestionRequest() {
		// empty no arg constructor
	}
	public SuggestionRequest (List<String> sourceNames, String type, String name) {
		this.sourceNames = sourceNames;
		this.type = type;
		this.attrName = name;
	}

	
	public void setEnvironments(List<String> environments) {
		this.sourceNames = environments;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public List<String> getEnvironments() {
		return sourceNames;
	}

	public String getType() {
		return type;
	}

	public String getAttrName() {
		return attrName;
	}
}
