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


package org.eclipse.mdm.filerelease.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.TestStep;

/**
 * FileRelease entity
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@XmlRootElement
public class FileRelease implements Serializable {

	private static final long serialVersionUID = -9016111258701009299L;

	/** unique identifier of a {@link FileRelease} */
	public String identifier = "";
	/** current state of the {@link FileRelease} */
	public String state = "";

	/** name of the source {@link TestStep} */
	public String name = "";
	/**
	 * name of the source {@link Environment} of the business object (part of
	 * MDM URI)
	 */
	public String sourceName = "";
	/** name of the type of the source business object (part of MDM URI) */
	public String typeName = "";
	/** id of the source business object (part of URI) */
	public String id = "";

	/** release sender name (MDM user name) */
	public String sender = "";
	/** release receiver name (MDM user name) */
	public String receiver = "";

	/**
	 * order message to specify by the sender on creating a {@link FileRelease}
	 */
	public String orderMessage = "";
	/**
	 * reject message to specify by the receiver on rejecting a
	 * {@link FileRelease}
	 */
	public String rejectMessage = "";
	/**
	 * system error message set by the converter system if an error occuring
	 * during generating the target file
	 */
	public String errorMessage = "";

	/** output data format of the generated file */
	public String format = "";
	/** relative file link of the generated file */
	public String fileLink = "";

	/** number of days */
	public int validity = 0;
	/** calculated expire date (time stamp) */
	public long expire = 0;
}
