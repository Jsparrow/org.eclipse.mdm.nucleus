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

package org.eclipse.mdm.filerelease.boundary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.User;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.filerelease.control.FileConvertJobManager;
import org.eclipse.mdm.filerelease.control.FileReleaseException;
import org.eclipse.mdm.filerelease.control.FileReleaseManager;
import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.eclipse.mdm.filerelease.entity.FileReleaseRequest;
import org.eclipse.mdm.filerelease.utils.FileReleasePermissionUtils;
import org.eclipse.mdm.filerelease.utils.FileReleaseUtils;

/**
 * FileReleaseService Bean implementation with available {@link FileRelease}
 * operations
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class FileReleaseService {

	@EJB
	private ConnectorService connectorService;
	@EJB
	private FileReleaseManager manager;
	@EJB
	private FileConvertJobManager converter;

	/**
	 * Returns the the {@link FileRelease} with the given identifier
	 * 
	 * @param identifier
	 *            The identifier
	 * @return The {@link FileRelease}
	 */
	public FileRelease getRelease(String identifier) {
		User user = FileReleaseUtils.getLoggedOnUser(this.connectorService);
		return this.manager.getRelease(user.getName(), identifier);
	}

	/**
	 * Returns all {@link FileRelease}s with the given state.
	 * 
	 * @param state
	 *            The state of the {@link FileRelease}s
	 * @return The {@link FileRelease}
	 */
	public List<FileRelease> getReleases(String state) {
		Set<FileRelease> fileReleases = new HashSet<>();
		fileReleases.addAll(getIncommingReleases(state));
		fileReleases.addAll(getOutgoingReleases(state));
		return new ArrayList<>(fileReleases);
	}

	/**
	 * Returns all incoming {@link FileRelease}s with the given state.
	 * 
	 * @param state
	 *            The state of the incoming {@link FileRelease}s
	 * @return The incoming {@link FileRelease}
	 */
	public List<FileRelease> getIncommingReleases(String state) {
		
		User user = FileReleaseUtils.getLoggedOnUser(this.connectorService);				
		if(state == null || state.trim().length() <= 0) { 
			return this.manager.getReleases(user.getName(), FileReleaseManager.FILE_RELEASE_DIRECTION_INCOMMING);
		}
		return this.manager.getReleases(user.getName(), FileReleaseManager.FILE_RELEASE_DIRECTION_INCOMMING, state);
	}

	/**
	 * Returns all outgoing {@link FileRelease}s with the given state.
	 * 
	 * @param state
	 *            The state of the outgoing {@link FileRelease}s
	 * @return The outgoing {@link FileRelease}
	 */
	public List<FileRelease> getOutgoingReleases(String state) {
		
		User user = FileReleaseUtils.getLoggedOnUser(this.connectorService);				
		if(state == null || state.trim().length() <= 0) { 
			return this.manager.getReleases(user.getName(), FileReleaseManager.FILE_RELEASE_DIRECTION_OUTGOING);
		}
		return this.manager.getReleases(user.getName(), FileReleaseManager.FILE_RELEASE_DIRECTION_OUTGOING, state);
	}

	/**
	 * Creates a new {@link FileRelease}. Approves the {@link FileRelease} if
	 * the sender is equals the reciever of the {@link FileRelease}.
	 * 
	 * @param request
	 *            The {@link FileReleaseRequest} that holds the information to create a new {@link FileRelease}
	 */
	public void create(FileReleaseRequest request) {
		checkFileReleaseRequest(request);
		User user = FileReleaseUtils.getLoggedOnUser(this.connectorService);		
		
		List<FileRelease> list = this.manager.getReleases(user.getName(), 
				FileReleaseManager.FILE_RELEASE_DIRECTION_OUTGOING);
		FileReleasePermissionUtils.canCreate(request, user.getName(), list);
		
		TestStep testStep = FileReleaseUtils.loadTestStep(connectorService, request.sourceName, request.id);
		User receiver = FileReleaseUtils.getResponsiblePerson(this.connectorService, testStep);
				
		FileRelease fileRelease = new FileRelease();
		
		fileRelease.identifier = UUID.randomUUID().toString();
		fileRelease.state = FileReleaseManager.FILE_RELEASE_STATE_ORDERED;
		
		fileRelease.sourceName = request.sourceName;
		fileRelease.typeName = request.typeName;
		fileRelease.id = request.id;
		fileRelease.format = request.format;
		fileRelease.validity = request.validity;
		fileRelease.orderMessage = request.message;
		
		fileRelease.sender = user.getName();
		fileRelease.receiver = receiver.getName();

		this.manager.addFileRelease(fileRelease);
		
		if(fileRelease.sender.equalsIgnoreCase(fileRelease.receiver)) {
			approve(fileRelease.identifier);
		}
		
	}

	/**
	 * Deletes the {@link FileRelease} with the given identifier.
	 * 
	 * @param identifier
	 *            The identifier of the {@link FileRelease} to delete.
	 */
	public void delete(String identifier) {

		User user = FileReleaseUtils.getLoggedOnUser(this.connectorService);		
		FileRelease fileRelease = this.manager.getRelease(user.getName(), identifier);
		
		FileReleasePermissionUtils.canDelete(fileRelease, user.getName());
			
		if(this.manager.canDeleteFileLink(fileRelease.fileLink)) {
			FileReleaseUtils.deleteFileLink(fileRelease.fileLink);
		}
		
		this.manager.removeFileRelease(fileRelease);
	}
	

	/**
	 * Approve the {@link FileRelease} with the given identifier.
	 * 
	 * @param identifier
	 *            The identifier of the {@link FileRelease} to approve.
	 */
	public void approve(String identifier) {

		User user = FileReleaseUtils.getLoggedOnUser(this.connectorService);		
		FileRelease fileRelease = this.manager.getRelease(user.getName(), identifier);	

		FileReleasePermissionUtils.canApprove(fileRelease, user.getName());
		
		this.converter.release(fileRelease);
	}

	/**
	 * Rejects the {@link FileRelease} with the given identifier. Sets the given
	 * message as reject message.
	 * 
	 * @param identifier
	 *            The identifier of the {@link FileRelease} to reject.
	 * @param message
	 *            The reject message.
	 */
	public void reject(String identifier, String message) {


		User user = FileReleaseUtils.getLoggedOnUser(this.connectorService);		
		FileRelease fileRelease = this.manager.getRelease(user.getName(), identifier);		
		
		FileReleasePermissionUtils.canReject(fileRelease, user.getName());
		
		fileRelease.state = FileReleaseManager.FILE_RELEASE_STATE_REJECTED;
		fileRelease.rejectMessage = message;
	}
	
	private void checkFileReleaseRequest(FileReleaseRequest request) {
		if(request.sourceName == null || request.sourceName.trim().length() < 1) {
			throw new FileReleaseException("source name for new FileRelease is missing!");
		}

		if(request.typeName == null || request.typeName.trim().length() < 1) {
			throw new FileReleaseException("type name for new FileRelease is missing!");
		}

		if(request.id < 1) {
			throw new FileReleaseException("is is not valid for new FileRelease");
		}

		if(request.validity <= 0) {
			throw new FileReleaseException("validity [days] is not set for new FileRelease");
		}
		
		if(request.format == null || request.format.trim().length() <= 0) {
			throw new FileReleaseException("output format for new FileRelease is missing!");
		}
		
		if(!FileReleaseUtils.isFormatValid(request.format)) {
			throw new FileReleaseException("unsupported file output format '" + request.format + "' was defined for new FileRelease");
		}
		
	}

}