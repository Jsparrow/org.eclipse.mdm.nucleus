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

import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.User;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.SearchService;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.filerelease.control.FileReleaseException;
import org.eclipse.mdm.filerelease.control.FileReleaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileReleaseUtils {


	private static final Logger LOG = LoggerFactory.getLogger(FileReleaseUtils.class);

	public static User getLoggedOnUser(ConnectorService connectorService) {

		try {
			List<EntityManager> emList = connectorService.getEntityManagers();

			if(emList == null || emList.size() <= 0) {
				throw new FileReleaseException("unable to locate neccessary EntityManager for file release service");
			}

			return extractUser(emList.get(0).loadLoggedOnUser());
		} catch(DataAccessException e) {
			throw new FileReleaseException(e.getMessage(), e);
		}
	}



	public static User getResponsiblePerson(ConnectorService connectorService, TestStep testStep) {
		EntityManager em = connectorService.getEntityManagerByName(testStep.getSourceName());
		Test test = getTestParent(em, testStep);
		return extractUser(test.getResponsiblePerson());
	}



	public static TestStep loadTestStep(ConnectorService connectorService, String sourceName, Long id) {
		try {
			EntityManager em = connectorService.getEntityManagerByName(sourceName);
			return em.load(TestStep.class, id);
		} catch(DataAccessException e) {
			throw new FileReleaseException(e.getMessage(), e);
		}
	}


	public static SearchService getSearchService(EntityManager em) {
		Optional<SearchService> optional = em.getSearchService();
		if(!optional.isPresent()) {
			throw new FileReleaseException("mandatory MDM SearchService not found");
		}
		return optional.get();

	}

	public static Response toResponse(Object response, Status status) {
		GenericEntity<Object> genEntity = new GenericEntity<Object>(response, response.getClass());
		return Response.status(status).entity(genEntity).type(MediaType.APPLICATION_JSON).build();
	}


	public static void deleteFileLink(String fileLink) {

		File file = new File(fileLink);
		if(!file.exists() || file.isDirectory()) {
			throw new FileReleaseException("unable to locate file at '" + file.getAbsolutePath() + "'");
		}

		boolean deleted = file.delete();
		if(!deleted) {
			LOG.warn("unable to delete file '" + file.getAbsolutePath() + "'");
		}
	}


	public static boolean isFormatValid(String format) {
		boolean valid = false;

		if(FileReleaseManager.CONVERTER_FORMAT_PAK2RAW.equalsIgnoreCase(format)) {
			valid = true;
		}

		if(FileReleaseManager.CONVERTER_FORMAT_PAK2ATFX.equalsIgnoreCase(format)) {
			valid = true;
		}

		return valid;
	}




	private static User extractUser(Optional<User> oUser) {
		if(!oUser.isPresent()) {
			throw new FileReleaseException("unable to locate neccessary User for file release service");
		}
		return oUser.get();
	}



	private static Test getTestParent(EntityManager em, TestStep testStep) {
		try {
			Optional<Test> oTest = em.loadParent(testStep, TestStep.PARENT_TYPE_TEST);
			if(!oTest.isPresent()) {
				throw new FileReleaseException("unable to locate Test parent for TestStep with ID '"
						+ testStep.getID() + "'");
			}
			return oTest.get();
		} catch(DataAccessException e) {
			throw new FileReleaseException(e.getMessage(), e);
		}
	}
}
