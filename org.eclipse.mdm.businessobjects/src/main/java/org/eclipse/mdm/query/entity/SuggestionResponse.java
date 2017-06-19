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

import java.util.ArrayList;
import java.util.Collections;
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
public class SuggestionResponse {

	/** transferable data content */
	private List<String> data;

	/**
	 * Constructor
	 * 
	 * @param searchDefinitions
	 *            list of {@link Suggestion}s to transfer
	 */
	public SuggestionResponse(List<String> suggestions) {
		this.data = new ArrayList<>(suggestions);
	}

	public SuggestionResponse() {
		this.data = new ArrayList<>();
	}

	public List<String> getData() {
		return Collections.unmodifiableList(this.data);
	}
}
