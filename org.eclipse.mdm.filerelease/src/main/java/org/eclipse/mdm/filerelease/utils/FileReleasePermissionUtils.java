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

package org.eclipse.mdm.filerelease.utils;

import java.util.List;

import org.eclipse.mdm.filerelease.control.FileReleaseException;
import org.eclipse.mdm.filerelease.control.FileReleaseManager;
import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.eclipse.mdm.filerelease.entity.FileReleaseRequest;

public final class FileReleasePermissionUtils {

	
	public static void canCreate(FileReleaseRequest request, String userName, List<FileRelease> list) {
		for(FileRelease fileRelease : list) {
			boolean sourceNameEqual = fileRelease.sourceName.equals(request.sourceName);
			boolean typeNameEqual = fileRelease.typeName.equals(request.typeName);
			boolean idEqual = fileRelease.id == request.id;
			
			if(sourceNameEqual && typeNameEqual && idEqual) {
				throw new FileReleaseException("FileRelease for user '" + userName + "' and path'" 
						+ fileRelease.sourceName + "/" + fileRelease.typeName + "/" 
						+ fileRelease.id + "' already exists!");				
			}
		}
		
	}
	
	
	public static void canDelete(FileRelease fileRelease, String userName) {
		if(!fileRelease.sender.equalsIgnoreCase(userName)) {
			throw new FileReleaseException("user with name '" + userName + "' can't remove FileRelease with id '" 
				+ fileRelease.identifier + "'");
		}
		
		if(fileRelease.state.equalsIgnoreCase(FileReleaseManager.FILE_RELEASE_STATE_PROGRESSING)) {
			throw new FileReleaseException("unable to remove FileRelease in state '" 
				+ FileReleaseManager.FILE_RELEASE_STATE_PROGRESSING + "'");
		}
	}
	
	
	
	public static void canApprove(FileRelease fileRelease, String userName) {
		if(!fileRelease.receiver.equalsIgnoreCase(userName)) {
			throw new FileReleaseException("user with name '" + userName + "' can't approve FileRelease with id '" 
				+ fileRelease.identifier + "'");
		}
		
		if(!fileRelease.state.equalsIgnoreCase(FileReleaseManager.FILE_RELEASE_STATE_ORDERED)) {
			throw new FileReleaseException("unable to approve FileRelease in state '" 
				+ fileRelease.state + "'");
		}		
	}
	
	
	
	public static void canReject(FileRelease fileRelease, String userName) {
						
		if(!fileRelease.receiver.equalsIgnoreCase(userName)) {
			throw new FileReleaseException("user with name '" + userName + "' can't reject FileRelease with id '" 
				+ fileRelease.identifier + "'");
		}
		
		if(!fileRelease.state.equalsIgnoreCase(FileReleaseManager.FILE_RELEASE_STATE_ORDERED)) {
			throw new FileReleaseException("unable to reject FileRelease in state '" 
				+ fileRelease.state + "'");
		}
		
	}
	
}
