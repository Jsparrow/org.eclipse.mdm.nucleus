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


package org.eclipse.mdm.filerelease.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;

import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * FileReleasePersistance bean implementation.
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class FileReleasePersistance {

	private static final Logger LOG = LoggerFactory.getLogger(FileReleasePersistance.class);

	private static final String TARGET_FILE_NAME = "mdm_filerelease_storage.sav";

	/**
	 * Persists the given file release map.
	 * 
	 * @param map
	 *            The map to persist.
	 */
	public void save(Map<String, FileRelease> map) {

		String userHomePath = System.getProperty("user.home");
		File directory = new File(userHomePath);

		File targetFile = new File(directory, TARGET_FILE_NAME);
		writeFile(targetFile, map);
	}

	/**
	 * 
	 * Loads the {@link FileRelease} into a map.
	 * 
	 * @return The map that contains the {@link FileRelease}s
	 */
	public Map<String, FileRelease> load() {

		String userHomePath = System.getProperty("user.home");
		File directory = new File(userHomePath);

		File targetFile = new File(directory, TARGET_FILE_NAME);
		
		return loadFile(targetFile);
	}

	private void writeFile(File targetFile, Map<String, FileRelease> map) {

		LOG.debug(new StringBuilder().append("Writing FileRelease storage file to '").append(targetFile.getAbsolutePath()).append("'").toString());

		try {
			if (targetFile.exists()) {
				deleteFile(targetFile);
			}

			try (ObjectOutputStream oos = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(targetFile)))) {
				oos.writeObject(map);
			}
		} catch (IOException e) {
			throw new FileReleaseException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, FileRelease> loadFile(File targetFile) {

		LOG.debug(new StringBuilder().append("Loading FileRelease storage file from '").append(targetFile.getAbsolutePath()).append("'").toString());

		try {
			if (!targetFile.exists()) {
				LOG.warn(new StringBuilder().append("Storage file does not exist at '").append(targetFile.getAbsolutePath()).append("'. Using an empty FileRelease pool").toString());
				return new HashMap<>();
			}

			try (ObjectInputStream ois = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(targetFile)))) {
				return (Map<String, FileRelease>) ois.readObject();
			}
		} catch (ClassNotFoundException | IOException e) {
			throw new FileReleaseException(e.getMessage(), e);
		}
	}

	private void deleteFile(File targetFile) {
		boolean deleted = targetFile.delete();
		if (!deleted) {
			throw new FileReleaseException(
					new StringBuilder().append("Unable to delete FileRelease storage file at '").append(targetFile.getAbsolutePath()).append("'").toString());
		}
	}
}
