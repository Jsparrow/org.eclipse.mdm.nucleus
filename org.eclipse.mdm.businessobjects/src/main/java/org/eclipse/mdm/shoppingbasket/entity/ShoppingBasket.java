/*******************************************************************************
  * Copyright (c) 2018 Peak Solution GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  *******************************************************************************/
package org.eclipse.mdm.shoppingbasket.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * ShoppingBasket contains a list of item and an optional name.
 *
 */
@XmlRootElement(name = "shoppingbasket")
@XmlType(propOrder = { "name", "items" })
public class ShoppingBasket {
	private String name;

	private List<BasketItem> items;

	/**
	 * @return the name
	 */
	@XmlElement(name = "name")
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

	@XmlElementWrapper(name = "items")
	@XmlElement(name = "item")
	public List<BasketItem> getItems() {
		return items;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(List<BasketItem> items) {
		this.items = items;
	}
}
