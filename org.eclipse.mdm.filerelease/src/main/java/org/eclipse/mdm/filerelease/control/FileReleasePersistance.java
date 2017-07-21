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

		LOG.debug("writing FileRelease storage file to '" + targetFile.getAbsolutePath() + "'");

		ObjectOutputStream oos = null;

		try {

			if (targetFile.exists()) {
				deleteFile(targetFile);
			}

			oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(targetFile)));
			oos.writeObject(map);

		} catch (IOException e) {
			throw new FileReleaseException(e.getMessage(), e);
		} finally {
			closeOutputStream(oos);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, FileRelease> loadFile(File targetFile) {

		LOG.debug("loading FileRelease storage file from '" + targetFile.getAbsolutePath() + "'");

		ObjectInputStream ois = null;

		try {
			if (!targetFile.exists()) {
				LOG.warn("storage file does not exist at '" + targetFile.getAbsolutePath()
						+ "'. Using an empty FileRelease pool");
				return new HashMap<String, FileRelease>();
			}

			ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(targetFile)));
			return (Map<String, FileRelease>) ois.readObject();
		} catch (IOException e) {
			throw new FileReleaseException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new FileReleaseException(e.getMessage(), e);
		} finally {
			closeInputSream(ois);
			;
		}
	}

	private void deleteFile(File targetFile) {
		boolean deleted = targetFile.delete();
		if (!deleted) {
			throw new FileReleaseException(
					"unable to delete FileRelease storage file at '" + targetFile.getAbsolutePath() + "'");
		}
	}

	private void closeOutputStream(ObjectOutputStream oos) {
		try {
			if (oos != null) {
				oos.close();
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void closeInputSream(ObjectInputStream ois) {
		try {
			if (ois != null) {
				ois.close();
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
