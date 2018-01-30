/*******************************************************************************
  * Copyright (c) 2018 Peak Solution GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  *******************************************************************************/
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
