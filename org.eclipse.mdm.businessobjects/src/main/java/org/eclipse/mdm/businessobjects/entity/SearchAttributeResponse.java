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

/**
 * SearchDefinitionResponse (Container for {@link SearchAttribute}s)
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchAttributeResponse {

	/** transferable data content */
	private List<SearchAttribute> data;

	/**
	 * Constructor
	 * 
	 * @param searchAttributes
	 *            list of {@link SearchAttribute}s to transfer
	 */
	public SearchAttributeResponse(List<SearchAttribute> searchAttributes) {
		data = new ArrayList<>(searchAttributes);
	}

	/**
	 * Constructor
	 */
	public SearchAttributeResponse() {
		data = new ArrayList<>();
	}

	public List<SearchAttribute> getData() {
		return Collections.unmodifiableList(this.data);
	}

}
