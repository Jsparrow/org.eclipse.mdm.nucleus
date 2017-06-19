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

/**
 * Provides utility methods for checking permissions on {@link FileRelease}s
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public final class FileReleasePermissionUtils {

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
			boolean idEqual = fileRelease.id == newFileRelease.id;
			boolean formatEqual = fileRelease.format.equals(newFileRelease.format);

			if (sourceNameEqual && typeNameEqual && idEqual && formatEqual) {
				throw new FileReleaseException("FileRelease for user '" + userName + "' and path'"
						+ fileRelease.sourceName + "/" + fileRelease.typeName + "/" + fileRelease.id + "' (Format: '"
						+ fileRelease.format + "') already exists!");
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
		if (!fileRelease.sender.equalsIgnoreCase(userName)) {
			throw new FileReleaseException("user with name '" + userName + "' can't remove FileRelease with id '"
					+ fileRelease.identifier + "'");
		}

		if (fileRelease.state.equalsIgnoreCase(FileReleaseManager.FILE_RELEASE_STATE_PROGRESSING)) {
			throw new FileReleaseException("unable to remove FileRelease in state '"
					+ FileReleaseManager.FILE_RELEASE_STATE_PROGRESSING + "'");
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
		if (!fileRelease.receiver.equalsIgnoreCase(userName)) {
			throw new FileReleaseException("user with name '" + userName + "' can't approve FileRelease with id '"
					+ fileRelease.identifier + "'");
		}

		if (!fileRelease.state.equalsIgnoreCase(FileReleaseManager.FILE_RELEASE_STATE_ORDERED)) {
			throw new FileReleaseException("unable to approve FileRelease in state '" + fileRelease.state + "'");
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
		if (!fileRelease.receiver.equalsIgnoreCase(userName)) {
			throw new FileReleaseException("user with name '" + userName + "' can't release FileRelease with id '"
					+ fileRelease.identifier + "'");
		}

		if (!fileRelease.state.equalsIgnoreCase(FileReleaseManager.FILE_RELEASE_STATE_APPROVED)) {
			throw new FileReleaseException("unable to release FileRelease in state '" + fileRelease.state + "'");
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

		if (!fileRelease.receiver.equalsIgnoreCase(userName)) {
			throw new FileReleaseException("user with name '" + userName + "' can't reject FileRelease with id '"
					+ fileRelease.identifier + "'");
		}

		if (!fileRelease.state.equalsIgnoreCase(FileReleaseManager.FILE_RELEASE_STATE_ORDERED)) {
			throw new FileReleaseException("unable to reject FileRelease in state '" + fileRelease.state + "'");
		}

	}

}
