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


package org.eclipse.mdm.filerelease.control.converter;

import java.io.File;
import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.filerelease.control.FileReleaseException;
import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.eclipse.mdm.property.GlobalProperty;

/**
 * 
 * {@link IFileConverter} implementation for converting PAK to ATFX
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@RequestScoped
public class FileConverterPAK2ATFX extends AbstractFileConverter {

	private static final String CONVERTER_NAME = "PAK2ATFX";
	private static final String COMPONENT_CONFIG_ROOT_FOLDER = "org.eclipse.mdm.filerelease";

	private static final String MODEL_FILE_NAME = "model.atfx.technical";
	private static final String OUTPUT_SUB_TEMP_DIRECTORY = "tmp";
	private static final String ATFX_OUTPUT_FILE_NAME_PREFIX = "_ATFX.zip";

	@Inject
	@GlobalProperty("filerelease.converter.pak.pakApplicationPath")
	private String pakApplicationPath = "";
	@Inject
	@GlobalProperty("filerelease.converter.pak.modelTypeEntity")
	private String modelTypeEntity = "";
	@Inject
	@GlobalProperty("filerelease.converter.pak.modelTypeAttribute")
	private String modelTypeAttribute = "";
	@Inject
	@GlobalProperty("filerelease.converter.pak.pakInputEntity")
	private String pakInputEntity = "";
	@Inject
	@GlobalProperty("filerelease.converter.pak.pakInputAttribute")
	private String pakInputAttribute = "";

	@Override
	public void execute(FileRelease fileRelease, TestStep testStep, ApplicationContext context, File targetDirectory)
			throws FileConverterException {

		int returnValue = -1;

		String pakApplicationPathValue = super.readPropertyValue(this.pakApplicationPath, true, null,
				"pakApplicationPath");
		String modelTypeEntityValue = super.readPropertyValue(this.modelTypeEntity, true, null, "modelTypeEntity");
		String modelTypeAttributeValue = super.readPropertyValue(this.modelTypeAttribute, true, null,
				"modelTypeAttribute");
		String pakInputEntityValue = super.readPropertyValue(this.pakInputEntity, true, null, "pakInputEntity");
		String pakInputAttributeValue = super.readPropertyValue(this.pakInputAttribute, true, null,
				"pakInputAttribute");

		String modelType = locateStringAttributeValue(context, testStep, modelTypeEntityValue, modelTypeAttributeValue);
		String inputPath = locateStringAttributeValue(context, testStep, pakInputEntityValue, pakInputAttributeValue);

		File pakApplicationFile = locatePakApplicationFile(pakApplicationPathValue);
		File modelFile = locateModelFileForModelType(modelType);
		File inputDirectory = locateInputDirectory(inputPath);
		File outputDirectory = createDirectory(new StringBuilder().append(targetDirectory.getAbsolutePath()).append(File.separator).append(fileRelease.name).toString());
		File outputTempDirectory = createDirectory(
				new StringBuilder().append(outputDirectory.getAbsolutePath()).append(File.separator).append(OUTPUT_SUB_TEMP_DIRECTORY).toString());
		File outputZIPFile = new File(outputDirectory, fileRelease.name + ATFX_OUTPUT_FILE_NAME_PREFIX);

		try {
			if (!outputZIPFile.exists()) {
				LOG.debug("executing external pak application ...");
				returnValue = createATFX(pakApplicationFile, inputDirectory, outputTempDirectory, modelFile);
				if (returnValue != 0) {
					String errorMessage = translateErrorCode(returnValue);
					throw new FileConverterException(
							new StringBuilder().append("external pak application ends with an error (message: '").append(errorMessage).append("')").toString());
				}
				LOG.debug("executing external pak application ... done!");

				LOG.debug("executing zip process for pak application result ...");

				zipFolder(outputZIPFile.getAbsolutePath(), outputTempDirectory.getAbsolutePath(), true);
				LOG.debug("executing zip process for pak application result ... done");
			}

			fileRelease.fileLink = new StringBuilder().append(fileRelease.name).append(File.separator).append(outputZIPFile.getName()).toString();

		} catch (IOException | InterruptedException e) {
			throw new FileConverterException(e.getMessage(), e);
		} finally {
			deleteDirectory(outputTempDirectory);
		}
	}

	@Override
	public String getConverterName() {
		return CONVERTER_NAME;
	}

	private int createATFX(File pakApplicationFile, File inputDirectory, File outputTempDirectory, File modelFile)
			throws IOException, InterruptedException {

		ProcessBuilder pb = new ProcessBuilder(pakApplicationFile.getAbsolutePath(), "-M",
				inputDirectory.getAbsolutePath(), "-E", outputTempDirectory.getAbsolutePath(), "-MF",
				modelFile.getAbsolutePath());
		Process process = pb.start();

		return process.waitFor();
	}

	private String translateErrorCode(int returnValue) {

		String errorMessage;

		if (returnValue == 1) {
			errorMessage = "invalid input pak measurement";
		} else if (returnValue == 2) {
			errorMessage = "illegal command (a parameter is missing)";
		} else if (returnValue == 6) {
			errorMessage = "foreign model measurement";
		} else if (returnValue == 7) {
			errorMessage = "illegal input path format";
		} else if (returnValue == 8) {
			errorMessage = "source directory does not exist";
		} else if (returnValue == 9) {
			errorMessage = "target directory does not exist";
		} else if (returnValue == 10) {
			errorMessage = "target directory is not empty";
		} else if (returnValue == 11) {
			errorMessage = "illegal commant (model file parameter is missing)";
		} else {
			errorMessage = new StringBuilder().append("unknown error with error code '").append(returnValue).append("'").toString();
		}
		return new StringBuilder().append(errorMessage).append("  (errorCode = '").append(returnValue).append("')").toString();
	}

	private File locatePakApplicationFile(String pakApplicationFilePath) {
		File pakApplicationFile = new File(pakApplicationFilePath);
		if (!pakApplicationFile.exists()) {
			throw new FileReleaseException(new StringBuilder().append("pak application executable file at '").append(pakApplicationFile.getAbsolutePath()).append("' does not exist!").toString());
		}
		return pakApplicationFile;
	}

	private File locateModelFileForModelType(String modelType) {
		File modelFile = new File(
				new StringBuilder().append(COMPONENT_CONFIG_ROOT_FOLDER).append(File.separator).append(modelType).append(File.separator)
						.append(MODEL_FILE_NAME).toString());
		if (!modelFile.exists()) {
			throw new FileReleaseException(new StringBuilder().append("mapping file for atfx model with type '").append(modelType).append("' does not exist! (expected at: '").append(modelFile.getAbsolutePath()).append("')").toString());
		}
		return modelFile;
	}

}
