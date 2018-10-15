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
