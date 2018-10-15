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

package org.eclipse.mdm.shoppingbasket.entity;

import java.net.URI;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * BasketItem represents a single item in a shopping basket.
 *
 */
@XmlRootElement(name = "item")
@XmlType(propOrder = { "source", "restURI", "link" })
public class BasketItem {

	private String source;
	private URI restURI;
	private String link;

	/**
	 * @return the adapter source of this item
	 */
	@XmlElement(name = "source")
	public String getSource() {
		return source;
	}

	/**
	 * @param type
	 *            the adapter source of this item
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return rest URI of this item
	 */
	@XmlElement(name = "resturi")
	public URI getRestURI() {
		return restURI;
	}

	/**
	 * @param restURI
	 *            rest URI of this item
	 */
	public void setRestURI(URI restURI) {
		this.restURI = restURI;
	}

	/**
	 * @return link to this item in the adapter's data store
	 */
	@XmlElement(name = "link")
	public String getLink() {
		return link;
	}

	/**
	 * @param link
	 *            to this item in the adapter's data store
	 */
	public void setLink(String link) {
		this.link = link;
	}
}
