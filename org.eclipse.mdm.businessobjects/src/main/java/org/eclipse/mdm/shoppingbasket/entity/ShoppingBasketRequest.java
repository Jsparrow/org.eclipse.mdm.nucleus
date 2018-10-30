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

import java.util.Collections;
import java.util.List;

/**
 * ShoppingBasketRequest represents a client request to generate a shopping
 * basket.
 *
 */
public class ShoppingBasketRequest {
	private String name;
	private List<MDMItem> items;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the items
	 */
	public List<MDMItem> getItems() {
		return items;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(List<MDMItem> items) {
		if (items == null) {
			this.items = Collections.emptyList();
		} else {
			this.items = items;
		}
	}

}
