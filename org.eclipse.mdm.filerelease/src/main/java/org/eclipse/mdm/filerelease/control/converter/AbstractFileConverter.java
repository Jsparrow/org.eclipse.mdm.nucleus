
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

package org.eclipse.mdm.filerelease.control.converter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.Filter;
import org.eclipse.mdm.api.base.search.SearchService;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.filerelease.control.FileReleaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Abstract implementation of {@link IFileConverter}. Provides some utility
 * methods for further {@link IFileConverter} implementations.
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public abstract class AbstractFileConverter implements IFileConverter {

	protected static final Logger LOG = LoggerFactory.getLogger(AbstractFileConverter.class);

	/**
	 * 
	 * Creates a zip file from the given folder.
	 * 
	 * @param targetFile
	 *            The path to the zip file to create
	 * @param folderToZip
	 *            The path to the folder to zip.
	 * @throws FileConverterException
	 *             Thrown if an error occurs.
	 */
	protected void zipFolder(String targetFile, String folderToZip) throws FileConverterException {
		zipFolder(targetFile, folderToZip, false);
	}

	/**
	 * Creates a zip file from the given folder.
	 * 
	 * @param targetFile
	 *            The path to the zip file to create
	 * @param folderToZip
	 *            The path to the folder to zip.
	 * @param overWrite
	 *            True if the target file should be overwritten.
	 * @throws FileConverterException
	 *             Thrown if an error occurs.
	 */
	protected void zipFolder(String targetFile, String folderToZip, boolean overWrite) throws FileConverterException {
		File source = new File(folderToZip);
		File target = new File(targetFile);
		if (!source.exists()) {
			throw new FileConverterException("Unable to zip folder: " + folderToZip + ". The folder does not exist.");
		}
		if (!overWrite && target.exists()) {
			throw new FileConverterException(
					"Unable to zip folder: " + folderToZip + ". The target file" + targetFile + "already exists.");
		}
		if (overWrite && target.exists()) {
			target.delete();
		}
		zipFiles(listAllFilesRecursive(source), target, folderToZip);
	}

	/**
	 * 
	 * Locates the attribute value for the given string attribute.
	 * 
	 * @param em
	 *            The entity manager that manages the attribute
	 * @param testStep
	 *            The {@link TestStep}
	 * @param entityName
	 *            The name of the entity that belongs to the attribute.
	 * @param attributeName
	 *            The name of the attribute.
	 * @return The attribute value.
	 */
	protected String locateStringAttributeValue(ApplicationContext context, TestStep testStep, String entityName,
			String attributeName) {
		try {
			SearchService searchService = context.getSearchService()
					.orElseThrow(() -> new FileReleaseException("Mandatory MDM SearchService not found"));
			
			List<EntityType> list = searchService.listEntityTypes(TestStep.class);

			EntityType entityType = locateEntityType(list, entityName);
			Attribute attribute = locateAttribute(entityType, attributeName);

			EntityType testStepET = locateEntityType(list, TestStep.class.getSimpleName());
			Filter idFilter = Filter.idOnly(testStepET, testStep.getID());

			List<TestStep> results = searchService.fetch(TestStep.class, Collections.singletonList(attribute),
					idFilter);

			if (results.size() < 0 || results.size() > 1) {
				throw new FileReleaseException(
						"Illegal search result for attribute value from '" + entityName + "." + attributeName + "'");
			}

			TestStep resultTestStep = results.get(0);
			return resultTestStep.getValue(attributeName).extract();
		} catch (DataAccessException e) {
			throw new FileReleaseException(e.getMessage(), e);
		}
	}

	/**
	 * Creates a new directory
	 * 
	 * @param path
	 *            The path to the directory to create
	 * @return The created directory {@link File}
	 */
	protected File createDirectory(String path) {
		File directory = new File(path);
		if (!directory.exists() && !directory.mkdir()) {
			throw new FileReleaseException("Unable to create directory at '" + directory.getAbsolutePath() + "'");
		}

		return directory;
	}

	/**
	 * Deletes the given directory
	 * 
	 * @param directory
	 *            The directory to delete.
	 */
	protected void deleteDirectory(File directory) {
		if (!directory.exists()) {
			return;
		}

		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				deleteDirectory(file);
			}
			if (!file.delete()) {
				LOG.warn("Unable to delete file at '" + file.getAbsolutePath() + "'");
			}
		}
		if (!directory.delete()) {
			LOG.warn("Unable to delete directory at '" + directory.getAbsolutePath() + "'");
		}
	}

	/**
	 * Locates the directory {@link File} for the given path
	 * 
	 * @param inputPath
	 *            The path to the directory.
	 * @return The {@link File}
	 */
	protected File locateInputDirectory(String inputPath) {
		File pakInputDirectory = new File(inputPath);
		if (!pakInputDirectory.exists()) {
			throw new FileReleaseException(
					"Input path at '" + pakInputDirectory.getAbsolutePath() + "' does not exist!");
		}

		if (!pakInputDirectory.isDirectory()) {
			throw new FileReleaseException(
					"Input path at '" + pakInputDirectory.getAbsolutePath() + "' is not a directory path!");
		}

		return pakInputDirectory;
	}

	protected String readPropertyValue(String propertyValue, boolean mandatory, String defaultValue,
			String propertyName) throws FileConverterException {
		if (propertyValue == null || propertyValue.trim().length() <= 0) {
			if (mandatory) {
				throw new FileConverterException("Mandatory property with name '" + propertyName + "' is not defined!");
			}
			return defaultValue;
		}
		return propertyValue;
	}

	private void zipFiles(List<File> list, File target, String sourcePath) throws FileConverterException {
		try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(target))) {
			for (File file : list) {
				zipFile(file, zipStream, sourcePath);
			}
		} catch (IOException ioe) {
			throw new FileConverterException(
					"An error occured when creating an zip archive from the folder: " + target.getAbsolutePath(), ioe);
		}
	}

	private List<File> listAllFilesRecursive(File sourceFolder) {
		List<File> files = new ArrayList<>();
		File[] subFiles = sourceFolder.listFiles();
		for (File f : subFiles) {
			if (f.isDirectory()) {
				files.addAll(listAllFilesRecursive(f));
			} else {
				files.add(f);
			}
		}
		return files;
	}

	private void zipFile(File file, ZipOutputStream zipStream, String sourcePath) throws FileConverterException {

		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
			String entryName = file.getAbsolutePath().replace(sourcePath, "");

			ZipEntry zipEntry = new ZipEntry(entryName);
			zipStream.putNextEntry(zipEntry);
			byte[] buffer = new byte[1024];

			for (int len = in.read(buffer); len > 0; len = in.read(buffer)) {
				zipStream.write(buffer, 0, len);
			}
		} catch (IOException ioe) {
			throw new FileConverterException("An error occured when zipping the file: " + file.getAbsolutePath(), ioe);
		}

	}

	private EntityType locateEntityType(List<EntityType> list, String entityName) {
		for (EntityType entityType : list) {
			if (entityType.getName().equals(entityName)) {
				return entityType;
			}
		}
		throw new FileReleaseException("Entity with name '" + entityName + "' not available for TestStep query");
	}

	private Attribute locateAttribute(EntityType entityType, String attributeName) {
		List<Attribute> list = entityType.getAttributes();
		for (Attribute attribute : list) {
			if (attribute.getName().equals(attributeName)) {
				return attribute;
			}
		}
		throw new FileReleaseException(
				"Attribute with name '" + attributeName + "' does not exist at entity '" + entityType.getName() + "'");
	}

}
