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

package org.eclipse.mdm.query.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Matthias Koller, Peak Solution GmbH
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SuggestionRequest {

	private List<String> sourceNames;
	private String type;
	private String attrName;

	public SuggestionRequest() {
		// empty no arg constructor
	}

	public SuggestionRequest(List<String> sourceNames, String type, String name) {
		this.sourceNames = sourceNames;
		this.type = type;
		this.attrName = name;
	}

	public void setSourceNames(List<String> sourceNames) {
		this.sourceNames = sourceNames;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public List<String> getSourceNames() {
		return sourceNames;
	}

	public String getType() {
		return type;
	}

	public String getAttrName() {
		return attrName;
	}
}
