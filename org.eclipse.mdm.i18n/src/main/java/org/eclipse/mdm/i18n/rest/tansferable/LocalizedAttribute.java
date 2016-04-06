/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.i18n.rest.tansferable;

/**
 * Transferable LocalizedAttribute
 * @author Gigatronik Ingolstadt GmbH
 *
 */
public class LocalizedAttribute {

	
	
	/** name of the attribute */
	public final String name;
	/** localized name of the attribute */
	public final String localizedName;
	
	
	
	/**
	 * Constructor
	 * @param name name of the attribute
	 * @param localizedName name of the localized attribute
	 */
	public LocalizedAttribute(String name, String localizedName) {
		this.name = name;
		this.localizedName = localizedName;
	}
	
}
