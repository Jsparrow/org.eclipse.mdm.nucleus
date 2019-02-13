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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * {@link FileReleaseResponse}
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class FileReleaseResponse {

	public List<FileRelease> data;

	/**
	 * Constructor
	 * 
	 * @param fileRelease
	 *            {@link FileRelease}
	 */
	public FileReleaseResponse(FileRelease fileRelease) {
		this.data = new ArrayList<>();
		this.data.add(fileRelease);
	}

	/**
	 * Constructor
	 * 
	 * @param list
	 *            list of {@link FileRelease}s
	 */
	public FileReleaseResponse(List<FileRelease> list) {
		this.data = new ArrayList<>();
		this.data.addAll(list);
	}

	/**
	 * returns the {@link FileRelease} data
	 * 
	 * @return the {@link FileRelease} data
	 */
	public List<FileRelease> getData() {
		return Collections.unmodifiableList(this.data);
	}
}
