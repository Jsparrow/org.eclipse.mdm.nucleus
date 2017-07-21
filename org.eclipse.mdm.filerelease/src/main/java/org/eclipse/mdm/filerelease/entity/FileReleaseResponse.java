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
		this.data = new ArrayList<FileRelease>();
		this.data.add(fileRelease);
	}

	/**
	 * Constructor
	 * 
	 * @param list
	 *            list of {@link FileRelease}s
	 */
	public FileReleaseResponse(List<FileRelease> list) {
		this.data = new ArrayList<FileRelease>();
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
