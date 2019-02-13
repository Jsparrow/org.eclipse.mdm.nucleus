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


package org.eclipse.mdm.filerelease.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdm.filerelease.control.FileReleaseException;
import org.eclipse.mdm.filerelease.control.FileReleaseManager;
import org.eclipse.mdm.filerelease.entity.FileRelease;

/**
 * Provides utility methods for checking permissions on {@link FileRelease}s
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public final class FileReleasePermissionUtils {
	
	private FileReleasePermissionUtils() {
	}

	/**
	 * Checks if the user with the given name has the permission to create the
	 * given {@link FileRelease}s.
	 * 
	 * @param request
	 *            The {@link FileReleaseRequest}
	 * @param userName
	 *            The name of the user to check the permissions
	 * @param list
	 *            The list with the {@link FileRelease}
	 * @throws FileReleaseException
	 *             if the user is not allowed to create the {@link FileRelease}s
	 * 
	 */
	public static void canCreate(FileRelease newFileRelease, String userName, List<FileRelease> list) {
		for (FileRelease fileRelease : list) {

			boolean sourceNameEqual = fileRelease.sourceName.equals(newFileRelease.sourceName);
			boolean typeNameEqual = fileRelease.typeName.equals(newFileRelease.typeName);
			boolean idEqual = fileRelease.id.equals(newFileRelease.id);
			boolean formatEqual = fileRelease.format.equals(newFileRelease.format);

			if (sourceNameEqual && typeNameEqual && idEqual && formatEqual) {
				throw new FileReleaseException(new StringBuilder().append("FileRelease for user '").append(userName).append("' and path'").append(fileRelease.sourceName).append("/")
						.append(fileRelease.typeName).append("/").append(fileRelease.id).append("' (Format: '").append(fileRelease.format)
						.append("') already exists!").toString());
			}
		}

	}

	/**
	 * Checks if the user with the given name has the permission to delete the
	 * given {@link FileRelease}
	 * 
	 * @param fileRelease
	 *            The {@link FileRelease} to check
	 * @param userName
	 *            The name of the user to check the permissions
	 * @throws FileReleaseException
	 *             if the user is not allowed to delete the {@link FileRelease}
	 */
	public static void canDelete(FileRelease fileRelease, String userName) {
		if (!StringUtils.equalsIgnoreCase(fileRelease.sender, userName)) {
			throw new FileReleaseException(new StringBuilder().append("user with name '").append(userName).append("' can't remove FileRelease with id '").append(fileRelease.identifier).append("'").toString());
		}

		if (StringUtils.equalsIgnoreCase(fileRelease.state, FileReleaseManager.FILE_RELEASE_STATE_PROGRESSING)) {
			throw new FileReleaseException(new StringBuilder().append("unable to remove FileRelease in state '").append(FileReleaseManager.FILE_RELEASE_STATE_PROGRESSING).append("'").toString());
		}
	}

	/**
	 * Checks if the user with the given name has the permission to approve the
	 * given {@link FileRelease}
	 * 
	 * @param fileRelease
	 *            The {@link FileRelease} to check
	 * @param userName
	 *            The name of the user to check the permissions
	 * @throws FileReleaseException
	 *             if the user is not allowed to approve the {@link FileRelease}
	 */
	public static void canApprove(FileRelease fileRelease, String userName) {
		if (!StringUtils.equalsIgnoreCase(fileRelease.receiver, userName)) {
			throw new FileReleaseException(new StringBuilder().append("user with name '").append(userName).append("' can't approve FileRelease with id '").append(fileRelease.identifier).append("'").toString());
		}

		if (!StringUtils.equalsIgnoreCase(fileRelease.state, FileReleaseManager.FILE_RELEASE_STATE_ORDERED)) {
			throw new FileReleaseException(new StringBuilder().append("unable to approve FileRelease in state '").append(fileRelease.state).append("'").toString());
		}
	}

	/**
	 * Checks if the user with the given name has the permission to release the
	 * given {@link FileRelease}
	 * 
	 * @param fileRelease
	 *            The {@link FileRelease} to check
	 * @param userName
	 *            The name of the user to check the permissions
	 * @throws FileReleaseException
	 *             if the user is not allowed to approve the {@link FileRelease}
	 */
	public static void canRelease(FileRelease fileRelease, String userName) {
		if (!StringUtils.equalsIgnoreCase(fileRelease.receiver, userName)) {
			throw new FileReleaseException(new StringBuilder().append("user with name '").append(userName).append("' can't release FileRelease with id '").append(fileRelease.identifier).append("'").toString());
		}

		if (!StringUtils.equalsIgnoreCase(fileRelease.state, FileReleaseManager.FILE_RELEASE_STATE_APPROVED)) {
			throw new FileReleaseException(new StringBuilder().append("unable to release FileRelease in state '").append(fileRelease.state).append("'").toString());
		}
	}

	/**
	 * Checks if the user with the given name is allowed to reject the given
	 * {@link FileRelease}
	 * 
	 * @param fileRelease
	 *            The {@link FileRelease} to reject
	 * @param userName
	 *            The name of the user to check the permissions
	 * @throws FileReleaseException
	 *             if the user is not allowed to reject the {@link FileRelease}
	 */
	public static void canReject(FileRelease fileRelease, String userName) {

		if (!StringUtils.equalsIgnoreCase(fileRelease.receiver, userName)) {
			throw new FileReleaseException(new StringBuilder().append("user with name '").append(userName).append("' can't reject FileRelease with id '").append(fileRelease.identifier).append("'").toString());
		}

		if (!StringUtils.equalsIgnoreCase(fileRelease.state, FileReleaseManager.FILE_RELEASE_STATE_ORDERED)) {
			throw new FileReleaseException(new StringBuilder().append("unable to reject FileRelease in state '").append(fileRelease.state).append("'").toString());
		}

	}

}
