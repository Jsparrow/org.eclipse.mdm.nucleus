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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * FileReleaseManager bean implementation.
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Startup
@Singleton
public class FileReleaseManager {

	private static final Logger LOG = LoggerFactory.getLogger(FileReleaseManager.class);

	public static final String FILE_RELEASE_STATE_ORDERED = "RELEASE_ORDERED";
	public static final String FILE_RELEASE_STATE_REJECTED = "RELEASE_REJECTED";
	public static final String FILE_RELEASE_STATE_APPROVED = "RELEASE_APPROVED";
	public static final String FILE_RELEASE_STATE_PROGRESSING = "RELEASE_PROGRESSING";
	public static final String FILE_RELEASE_STATE_PROGRESSING_ERROR = "RELEASE_PROGRESSING_ERROR";
	public static final String FILE_RELEASE_STATE_RELEASED = "RELEASE_RELEASED";
	public static final String FILE_RELEASE_STATE_EXPIRED = "RELEASE_EXPIRED";

	public static final String FILE_RELEASE_DIRECTION_INCOMMING = "INCOMMING";
	public static final String FILE_RELEASE_DIRECTION_OUTGOING = "OUTGOING";

	public static final String CONVERTER_FORMAT_PAK2RAW = "PAK2RAW";
	public static final String CONVERTER_FORMAT_PAK2ATFX = "PAK2ATFX";

	private Map<String, FileRelease> releaseMap;

	@EJB
	private FileReleasePersistance releasePersistance;

	/**
	 * Returns the {@link FileRelease} for the given user with the given
	 * identifier.
	 * 
	 * @param userName
	 *            The name of the user to locate the {@link FileRelease}
	 * @param identifier
	 *            The identifier of the {@link FileRelease}
	 * @return The found {@link FileRelease}
	 */
	public FileRelease getRelease(String userName, String identifier) {

		if (!this.releaseMap.containsKey(identifier)) {
			throw new FileReleaseException(new StringBuilder().append("unable to find FileRelease with identifier '").append(identifier).append("'!").toString());
		}

		FileRelease fileRelease = this.releaseMap.get(identifier);

		if (StringUtils.equalsIgnoreCase(fileRelease.sender, userName) || StringUtils.equalsIgnoreCase(fileRelease.receiver, userName)) {
			updateExpired(fileRelease);
			return fileRelease;
		}

		throw new FileReleaseException(new StringBuilder().append("unable to find FileRelease with identifier '").append(identifier).append("' for user with name '").append(userName).append("'!").toString());

	}

	/**
	 * 
	 * Returns all {@link FileRelease}s for the given user in the given
	 * direction ({@link FileReleaseManager#FILE_RELEASE_DIRECTION_INCOMMING} or
	 * ({@link FileReleaseManager#FILE_RELEASE_DIRECTION_OUTGOING}).
	 * 
	 * @param userName
	 *            The name of the user
	 * @param direction
	 *            The direction (
	 *            {@link FileReleaseManager#FILE_RELEASE_DIRECTION_INCOMMING} or
	 *            {@link FileReleaseManager#FILE_RELEASE_DIRECTION_OUTGOING})
	 * @return A list with the {@link FileRelease}s
	 */
	public List<FileRelease> getReleases(String userName, String direction) {

		Collection<FileRelease> releases = this.releaseMap.values();

		if (StringUtils.equalsIgnoreCase(direction, FILE_RELEASE_DIRECTION_INCOMMING)) {
			return extractIncommingReleases(userName, releases);
		}

		if (StringUtils.equalsIgnoreCase(direction, FILE_RELEASE_DIRECTION_OUTGOING)) {
			return extractOutgoingReleases(userName, releases);
		}

		LOG.debug(new StringBuilder().append("unknown file request direction value '").append(direction).append("'. Returing empty file request list").toString());
		return Collections.emptyList();
	}

	/**
	 * Returns all {@link FileRelease}s for a given user, in the given
	 * direction, with the given state.
	 * 
	 * @param userName
	 *            The user name.
	 * @param direction
	 *            The direction (
	 *            {@link FileReleaseManager#FILE_RELEASE_DIRECTION_INCOMMING} or
	 *            {@link FileReleaseManager#FILE_RELEASE_DIRECTION_OUTGOING})
	 * @param state
	 *            The state of the {@link FileRelease}s to return
	 * @return A list with the {@link FileRelease}s
	 */
	public List<FileRelease> getReleases(String userName, String direction, String state) {
		List<FileRelease> list = getReleases(userName, direction);
		return extractReleasesByState(state, list);
	}

	/**
	 * Adds a new {@link FileRelease}.
	 * 
	 * @param fileRelease
	 *            The {@link FileRelease} to add.
	 */
	public void addFileRelease(FileRelease fileRelease) {
		if (this.releaseMap.containsKey(fileRelease.identifier)) {
			throw new FileReleaseException(
					new StringBuilder().append("FileRelease with identifier '").append(fileRelease.identifier).append("' already exists!").toString());
		}
		this.releaseMap.put(fileRelease.identifier, fileRelease);
	}

	/**
	 * 
	 * Removes a {@link FileRelease}
	 * 
	 * @param fileRelease
	 *            The {@link FileRelease} the remove.
	 */
	public void removeFileRelease(FileRelease fileRelease) {
		if (!this.releaseMap.containsKey(fileRelease.identifier)) {
			throw new FileReleaseException(
					new StringBuilder().append("FileRelease with identifier '").append(fileRelease.identifier).append("' does not exist!").toString());
		}
		this.releaseMap.remove(fileRelease.identifier);
	}

	/**
	 * 
	 * Checks if the given file link can be deleted
	 * 
	 * @param fileLink
	 *            The file link to check
	 * @return TRUE if the file link can be deleted. Otherwise FALSE.
	 */
	public boolean canDeleteFileLink(String identfier, String fileLink) {

		if (fileLink == null || StringUtils.trim(fileLink).length() <= 0) {
			return false;
		}

		for (FileRelease fileRelease : this.releaseMap.values()) {

			// skipping fileRelease to delete
			if (StringUtils.equalsIgnoreCase(fileRelease.identifier, identfier)) {
				continue;
			}

			if (StringUtils.equalsIgnoreCase(fileRelease.fileLink, fileLink)) {
				return false;
			}
		}
		return true;
	}

	@PostConstruct
	private void onInitialize() {
		this.releaseMap = this.releasePersistance.load();
	}

	@PreDestroy
	private void onDestroy() {
		this.releasePersistance.save(this.releaseMap);
	}

	private List<FileRelease> extractIncommingReleases(String receiverName, Collection<FileRelease> releases) {
		List<FileRelease> incommingList = new ArrayList<>();
		releases.stream().filter(fileRelease -> StringUtils.equalsIgnoreCase(fileRelease.receiver, receiverName)).forEach(fileRelease -> {
			updateExpired(fileRelease);
			incommingList.add(fileRelease);
		});
		return incommingList;
	}

	private List<FileRelease> extractOutgoingReleases(String senderName, Collection<FileRelease> releases) {
		List<FileRelease> outgoingList = new ArrayList<>();
		releases.stream().filter(fileRelease -> StringUtils.equalsIgnoreCase(fileRelease.sender, senderName)).forEach(fileRelease -> {
			updateExpired(fileRelease);
			outgoingList.add(fileRelease);
		});
		return outgoingList;
	}

	private List<FileRelease> extractReleasesByState(String state, List<FileRelease> list) {
		List<FileRelease> statedList = new ArrayList<>();
		statedList.addAll(list.stream().filter(fileRelease -> StringUtils.equalsIgnoreCase(fileRelease.state, state)).collect(Collectors.toList()));
		return statedList;
	}

	private void updateExpired(FileRelease fileRelease) {
		long current = System.currentTimeMillis();

		if (!StringUtils.equalsIgnoreCase(fileRelease.state, FILE_RELEASE_STATE_RELEASED)) {
			return;
		}

		if (fileRelease.expire <= current) {
			fileRelease.state = FILE_RELEASE_STATE_EXPIRED;
		}
	}

}
