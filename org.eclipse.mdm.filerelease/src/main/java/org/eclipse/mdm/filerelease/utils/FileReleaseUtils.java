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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.User;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.filerelease.control.FileReleaseException;
import org.eclipse.mdm.filerelease.control.FileReleaseManager;
import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Provides utility methods
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public final class FileReleaseUtils {

	private static final Logger LOG = LoggerFactory.getLogger(FileReleaseUtils.class);

	/**
	 * Returns the {@link User} that is logged in on the given
	 * {@link ConnectorService}
	 * 
	 * @param connectorService
	 *            The {@link ConnectorService}
	 * @return The {@link User}
	 */
	public static User getLoggedOnUser(ConnectorService connectorService) {

		try {
			List<ApplicationContext> contextList = connectorService.getContexts();
					
			if (contextList == null || contextList.size() <= 0) {
				throw new FileReleaseException("unable to locate neccessary EntityManager for file release service");
			}

			return extractUser(contextList.get(0)
					.getEntityManager()
					.map(em -> em.loadLoggedOnUser())
					.orElseThrow(() -> new FileReleaseException("Entity manager not present!")));
		} catch (DataAccessException e) {
			throw new FileReleaseException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the {@link User} that is responsible for the given
	 * {@link TestStep}
	 * 
	 * @param connectorService
	 *            The {@link ConnectorService}
	 * @param testStep
	 *            The {@link TestStep}
	 * @return The responsible {@link User}
	 */
	public static User getResponsiblePerson(ConnectorService connectorService, TestStep testStep) {
		EntityManager em = connectorService.getContextByName(testStep.getSourceName())
				.getEntityManager()
				.orElseThrow(() -> new FileReleaseException("Entity manager not present!"));
		Test test = getTestParent(em, testStep);
		return extractUser(test.getResponsiblePerson());
	}

	/**
	 * 
	 * Loads the {@link TestStep} with the given URI from the given
	 * {@link ConnectorService}
	 * 
	 * @param connectorService
	 *            The {@link ConnectorService}
	 * @param sourceName
	 *            The source name
	 * @param id
	 *            The id of the {@link TestStep}
	 * @return The loaded {@link TestStep}
	 */
	public static TestStep loadTestStep(ConnectorService connectorService, String sourceName, String id) {
		try {
			EntityManager em = connectorService.getContextByName(sourceName)
					.getEntityManager()
					.orElseThrow(() -> new FileReleaseException("Entity manager not present!"));
			return em.load(TestStep.class, id);
		} catch (DataAccessException e) {
			throw new FileReleaseException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Creates a {@link Response} with the given status.
	 * 
	 * @param response
	 *            The object that should be returned in the response.
	 * @param status
	 *            The status of the response.
	 * @return The {@link Response}
	 */
	public static Response toResponse(Object response, Status status) {
		GenericEntity<Object> genEntity = new GenericEntity<Object>(response, response.getClass());
		return Response.status(status).entity(genEntity).type(MediaType.APPLICATION_JSON).build();
	}

	/**
	 * Deletes a filelink
	 * 
	 * @param fileLink
	 *            The filelink to delete
	 */
	public static void deleteFileLink(File file) {

		if (!file.exists() || file.isDirectory()) {
			LOG.warn("unable to delete file '" + file.getAbsolutePath() + "' (file does not exist)!");
			return;
		}

		boolean deleted = file.delete();
		if (!deleted) {
			LOG.warn("unable to delete file '" + file.getAbsolutePath() + "' (unkown error)!");
		}
	}

	/**
	 * Checks if the given format is a valid file converter format.
	 * 
	 * @param format
	 *            The format as string
	 * @return TRUE if the format is valid. Otherwise FALSE.
	 */
	public static boolean isFormatValid(String format) {
		boolean valid = false;

		if (FileReleaseManager.CONVERTER_FORMAT_PAK2RAW.equalsIgnoreCase(format)) {
			valid = true;
		}

		if (FileReleaseManager.CONVERTER_FORMAT_PAK2ATFX.equalsIgnoreCase(format)) {
			valid = true;
		}

		return valid;
	}

	public static List<FileRelease> filterByConnectedSources(List<FileRelease> fileReleases,
			ConnectorService connectorService) {

		List<FileRelease> filteredList = new ArrayList<FileRelease>();

		List<String> sourceNameList = listConnectedSourceNames(connectorService);
		for (FileRelease fileRelease : fileReleases) {
			if (isFileReleaseSourceConnected(fileRelease, sourceNameList)) {
				filteredList.add(fileRelease);
			}
		}
		return filteredList;

	}

	public static File locateTargetDirectory(String targetDirectoryPath) {
		if (targetDirectoryPath == null || targetDirectoryPath.trim().length() <= 0) {
			throw new FileReleaseException("mandatory targetDirectoryPath property is missing");
		}
		File targetDirectory = new File(targetDirectoryPath);
		if (!targetDirectory.exists()) {
			throw new FileReleaseException(
					"configured target directory at '" + targetDirectory.getAbsolutePath() + "' does not exist!");
		}
		return targetDirectory;
	}

	private static boolean isFileReleaseSourceConnected(FileRelease fileRelease, List<String> sourceNameList) {
		for (String sourceName : sourceNameList) {
			if (fileRelease.sourceName != null && fileRelease.sourceName.equals(sourceName)) {
				return true;
			}
		}
		return false;
	}

	private static List<String> listConnectedSourceNames(ConnectorService connectorService) {
		try {
			return connectorService.getContexts()
					.stream()
					.map(c -> c.getEntityManager())
					.filter(Optional::isPresent)
				    .map(Optional::get)
				    .map(em -> em.loadEnvironment().getSourceName())
					.collect(Collectors.toList());
		} catch (DataAccessException e) {
			throw new FileReleaseException(e.getMessage(), e);
		}
	}

	private static User extractUser(Optional<User> oUser) {
		if (!oUser.isPresent()) {
			throw new FileReleaseException("unable to locate neccessary User for file release service");
		}
		return oUser.get();
	}

	private static Test getTestParent(EntityManager em, TestStep testStep) {
		try {
			Optional<Test> oTest = em.loadParent(testStep, TestStep.PARENT_TYPE_TEST);
			if (!oTest.isPresent()) {
				throw new FileReleaseException(
						"unable to locate Test parent for TestStep with ID '" + testStep.getID() + "'");
			}
			return oTest.get();
		} catch (DataAccessException e) {
			throw new FileReleaseException(e.getMessage(), e);
		}
	}
}
