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

/**
 * ContextData (Entity for I18N localizations (with original name and localized
 * name)
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class I18NLocalization {

	/** name of the attribute */
	private final String name;
	/** localized name of the attribute */
	private final String localizedName;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name of the attribute
	 * @param localizedName
	 *            name of the localized attribute
	 */
	public I18NLocalization(String name, String localizedName) {
		this.name = name;
		this.localizedName = localizedName;
	}

	public String getName() {
		return this.name;
	}

	public String getLocalizedName() {
		return this.localizedName;
	}
}
