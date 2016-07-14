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

public class FileReleaseResponse {
	
	public List<FileRelease> data;
	
	
	public FileReleaseResponse(FileRelease fileRelease) {
		this.data = new ArrayList<FileRelease>();
		this.data.add(fileRelease);
	}
	
	public FileReleaseResponse(List<FileRelease> list) { 
		this.data = new ArrayList<FileRelease>();
		this.data.addAll(list);
	}
	
	public List<FileRelease> getData() {
		return Collections.unmodifiableList(this.data);
	}
}
