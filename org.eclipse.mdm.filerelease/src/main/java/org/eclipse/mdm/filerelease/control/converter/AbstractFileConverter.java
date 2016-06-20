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
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.Filter;
import org.eclipse.mdm.api.base.query.Result;
import org.eclipse.mdm.api.base.query.SearchService;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.filerelease.control.FileReleaseException;
import org.eclipse.mdm.filerelease.utils.FileReleaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFileConverter implements IFileConverter {

	protected static final Logger LOG = LoggerFactory.getLogger(AbstractFileConverter.class);


	protected void zipFolder(String targetFile, String folderToZip) throws FileConverterException {
		zipFolder(targetFile, folderToZip, false);
	}


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


	protected String locateStringAttributeValue(EntityManager em, TestStep testStep, String entityName, String attributeName) {

		try {
			SearchService searchService = FileReleaseUtils.getSearchService(em);
			List<EntityType> list = searchService.listEntityTypes(TestStep.class);

			EntityType entityType = locateEntityType(list, entityName);
			Attribute attribute = locateAttribute(entityType, attributeName);

			EntityType testStepET = locateEntityType(list, TestStep.class.getSimpleName());
			Filter idFilter = Filter.idOnly(testStepET, testStep.getID());

			Map<TestStep, Result> results = searchService.fetch(TestStep.class,
					Collections.singletonList(attribute), idFilter);

			if(results.size() < 0 || results.size() > 1) {
				throw new FileReleaseException("illegal search result for attribute value from '" + entityName
						+ "." + attributeName + "'");
			}

			Result result = results.values().iterator().next();
			return result.getValue(attribute).extract();
		} catch(DataAccessException e) {
			throw new FileReleaseException(e.getMessage(), e);
		}
	}



	protected File createDirectory(String path) {
		File directory = new File(path);
		if(!directory.exists()) {
			if(!directory.mkdir()) {
				throw new FileReleaseException("unable to create directory at '" + directory.getAbsolutePath() + "'");
			}
		}
		return directory;
	}

	protected void deleteDirectory(File directory) {

		if(!directory.exists()) {
			return;
		}

		File[] files = directory.listFiles();
		for(File file : files) {
			if(file.isDirectory()) {
				deleteDirectory(file);
			}
			if(!file.delete()) {
				LOG.warn("unable to delete file at '" + file.getAbsolutePath() + "'");
			}
		}
		if(!directory.delete()) {
			LOG.warn("unable to delete directory at '" + directory.getAbsolutePath() + "'");
		}
	}


	protected File locateInputDirectory(String inputPath) {
		File pakInputDirectory = new File(inputPath);
		if(!pakInputDirectory.exists()) {
			throw new FileReleaseException("input path at '" + pakInputDirectory.getAbsolutePath() + "' does not exist!");
		}

		if(!pakInputDirectory.isDirectory()) {
			throw new FileReleaseException("input path at '" + pakInputDirectory.getAbsolutePath() + "' is not a directory path!");
		}

		return pakInputDirectory;
	}


	private void zipFiles(List<File> list, File target, String sourcePath) throws FileConverterException {

		ZipOutputStream zipStream = null;

		try {
			zipStream = new ZipOutputStream(new FileOutputStream(target));
			for (File file : list) {
				zipFile(file, zipStream, sourcePath);
			}
			zipStream.close();
		} catch (IOException ioe) {
			throw new FileConverterException(
					"An error occured when creating an zip archive from the folder: " + target.getAbsolutePath(), ioe);
		} finally {
			closeZipTargetOutputStream(zipStream, target);
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

		BufferedInputStream in = null;

		try {
			in = new BufferedInputStream(new FileInputStream(file));
			String entryName = file.getAbsolutePath().replace(sourcePath, "");

			ZipEntry zipEntry = new ZipEntry(entryName);
			zipStream.putNextEntry(zipEntry);
			byte[] buffer = new byte[1024];

			for (int len = in.read(buffer); len > 0; len = in.read(buffer)) {
				zipStream.write(buffer, 0, len);
			}

		} catch (IOException ioe) {
			throw new FileConverterException("An error occured when zipping the file: " + file.getAbsolutePath(), ioe);
		} finally {
			closeZipSourceInputStream(in, file);
		}

	}


	private void closeZipTargetOutputStream(ZipOutputStream zipStream, File file) throws FileConverterException {
		try {
			if (zipStream != null) {
				zipStream.close();
			}
		} catch (IOException e) {
			throw new FileConverterException(
					"unable to close zip output stream at file '" + file.getAbsolutePath() + "'", e);
		}
	}


	private void closeZipSourceInputStream(BufferedInputStream in, File file) throws FileConverterException {
		try {
			if (in != null) {
				in.close();
			}
		} catch (IOException e) {
			throw new FileConverterException(
					"unable to close file input stream at file '" + file.getAbsolutePath() + "'", e);
		}
	}


	private EntityType locateEntityType(List<EntityType> list, String entityName) {
		for(EntityType entityType : list) {
			if(entityType.getName().equals(entityName)) {
				return entityType;
			}
		}
		throw new FileReleaseException("entity with name '" + entityName + "' not available for TestStep query");
	}


	private Attribute locateAttribute(EntityType entityType, String attributeName) {
		List<Attribute> list = entityType.getAttributes();
		for(Attribute attribute : list) {
			if(attribute.getName().equals(attributeName)) {
				return attribute;
			}
		}
		throw new FileReleaseException("attribute with name '" + attributeName + "' not exista at entity '"
				+ entityType.getName() + "'");
	}

}
