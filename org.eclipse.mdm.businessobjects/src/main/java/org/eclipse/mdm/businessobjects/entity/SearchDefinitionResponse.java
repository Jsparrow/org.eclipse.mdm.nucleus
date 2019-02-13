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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * SearchDefinitionResponse (Container for {@link SearchDefinition}s)
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchDefinitionResponse {

	/** transferable data content */
	private List<SearchDefinition> data;

	/**
	 * Constructor
	 * 
	 * @param searchDefinitions
	 *            list of {@link SearchDefinition}s to transfer
	 */
	public SearchDefinitionResponse(List<SearchDefinition> searchDefinitions) {
		this.data = new ArrayList<>(searchDefinitions);
	}

	/**
	 * Constructor
	 */
	public SearchDefinitionResponse() {
		this.data = new ArrayList<>();
	}

	public List<SearchDefinition> getData() {
		return Collections.unmodifiableList(this.data);
	}
}
