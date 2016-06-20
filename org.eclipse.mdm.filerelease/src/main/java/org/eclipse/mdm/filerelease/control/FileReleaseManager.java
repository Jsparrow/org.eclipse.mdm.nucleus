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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Startup
@Singleton
public class FileReleaseManager {

	private static final Logger LOG = LoggerFactory.getLogger(FileReleaseManager.class); 
	
	public static final String FILE_RELEASE_STATE_ORDERED = "RELEASE_ORDERED";
	public static final String FILE_RELEASE_STATE_REJECTED = "RELEASE_REJECTED";
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
	
	@PostConstruct
	private void onInitialize() {
		this.releaseMap = this.releasePersistance.load();
	}	
		
	@PreDestroy
	private void onDestroy() {
		this.releasePersistance.save(this.releaseMap);
	}
	
	public FileRelease getRelease(String userName, String identifier) {
		
		if(!this.releaseMap.containsKey(identifier)) {
			throw new FileReleaseException("unable to find FileRelease with identifier '" + identifier + "'!");
		}
		
		FileRelease fileRelease = this.releaseMap.get(identifier);
		
		if(fileRelease.sender.equalsIgnoreCase(userName) || fileRelease.receiver.equalsIgnoreCase(userName)) {
			updateExpired(fileRelease);
			return fileRelease;
		}
		
		throw new FileReleaseException("unable to find FileRelease with identifier '" + identifier 
			+ "' for user with name '" + userName + "'!");
		
	}
	
	
	public List<FileRelease> getReleases(String userName, String direction) {
		
		Collection<FileRelease> releases = this.releaseMap.values();
		
		if(direction.equalsIgnoreCase(FILE_RELEASE_DIRECTION_INCOMMING)) {
			return extractIncommingReleases(userName, releases);
		} 
		
		if(direction.equalsIgnoreCase(FILE_RELEASE_DIRECTION_OUTGOING)) {
			return extractOutgoingReleases(userName, releases);
		}
		
		LOG.debug("unknown file request direction value '" + direction	+ "'. Returing empty file request list");
		return Collections.emptyList();
	}
	
	
	
	public List<FileRelease> getReleases(String userName, String direction, String state) {
		List<FileRelease> list = getReleases(userName, direction);
		return extractReleasesByState(state, list);
	}
	
	
	
	public void addFileRelease(FileRelease fileRelease) {
		if(this.releaseMap.containsKey(fileRelease.identifier)) {
			throw new FileReleaseException("FileRelease with identifier '" + fileRelease.identifier + "' already exists!");
		}
		this.releaseMap.put(fileRelease.identifier, fileRelease);
	}
	
	
	
	public void removeFileRelease(FileRelease fileRelease) {
		if(!this.releaseMap.containsKey(fileRelease.identifier)) {
			throw new FileReleaseException("FileRelease with identifier '" + fileRelease.identifier + "' does not exist!");
		}
		this.releaseMap.remove(fileRelease.identifier);
	}
	
	
	public boolean canDeleteFileLink(String fileLink) {
		
		if(fileLink == null || fileLink.trim().length() <= 0) {
			return false;
		}
		
		for(FileRelease fileRelease : this.releaseMap.values()) {
			if(fileRelease.fileLink.equalsIgnoreCase(fileLink)) {
				return false;
			}
		}
		return true;
	}
	
	
	private List<FileRelease> extractIncommingReleases(String receiverName, Collection<FileRelease> releases) {
		List<FileRelease> incommingList = new ArrayList<>();
		for(FileRelease fileRelease : releases) {
			if(fileRelease.receiver.equalsIgnoreCase(receiverName)) {
				updateExpired(fileRelease);
				incommingList.add(fileRelease);
			}
		}
		return incommingList;
	}
	
	
	private List<FileRelease> extractOutgoingReleases(String senderName, Collection<FileRelease> releases) {
		List<FileRelease> outgoingList = new ArrayList<>();		
		for(FileRelease fileRelease : releases) {
			if(fileRelease.sender.equalsIgnoreCase(senderName)) {
				updateExpired(fileRelease);
				outgoingList.add(fileRelease);
			}
		}
		return outgoingList;
	}
	
	
	private List<FileRelease> extractReleasesByState(String state, List<FileRelease> list) {
		List<FileRelease> statedList = new ArrayList<>();
		for(FileRelease fileRelease : list) {
			if(fileRelease.state.equalsIgnoreCase(state)) {
				statedList.add(fileRelease);
			}
		}
		return statedList;
	}
	
	
	private void updateExpired(FileRelease fileRelease) {
		long current = System.currentTimeMillis();
		
		if(!fileRelease.state.equalsIgnoreCase(FILE_RELEASE_STATE_RELEASED)) {
			return;
		}
					
		if(fileRelease.expire <= current) {
			fileRelease.state = FILE_RELEASE_STATE_EXPIRED;
		}
	}
	
}
