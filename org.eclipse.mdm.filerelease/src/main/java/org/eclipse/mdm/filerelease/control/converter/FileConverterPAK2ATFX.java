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



import java.io.File;
import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.filerelease.control.FileReleaseException;
import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.eclipse.mdm.property.BeanProperty;

@RequestScoped
public class FileConverterPAK2ATFX extends AbstractFileConverter {

	private static final String CONVERTER_NAME = "PAK2ATFX";
	private final static String COMPONENT_CONFIG_ROOT_FOLDER = "org.eclipse.mdm.filerelease";
	
	private static final String MODEL_FILE_NAME = "model.atfx.technical";
	private static final String OUTPUT_SUB_DIRECTORY = "atfx";
	private static final String OUTPUT_SUB_TEMP_DIRECTORY = "tmp";
	private static final String ATFX_OUTPUT_FILE_NAME_PREFIX = "_ATFX.zip";
	
	@Inject
	@BeanProperty
	private String pakApplicationPath = "";
	@Inject
	@BeanProperty
	private String modelTypeEntity = "";
	@Inject
	@BeanProperty
	private String modelTypeAttribute = "";
	@Inject
	@BeanProperty
	private String pakInputEntity = "";
	@Inject
	@BeanProperty
	private String pakInputAttribute = "";
		
	
	
	@Override
	public void execute(FileRelease fileRelease, TestStep testStep, EntityManager em) throws FileConverterException {
			
			int returnValue = -1;

			String modelType = locateStringAttributeValue(em, testStep, 
				this.modelTypeEntity, this.modelTypeAttribute);
			String inputPath = locateStringAttributeValue(em, testStep, 
				this.pakInputEntity, this.pakInputAttribute);			
					
			File pakApplicationFile = locatePakApplicationFile(this.pakApplicationPath);
			File modelFile = locateModelFileForModelType(modelType);
			File inputDirectory = locateInputDirectory(inputPath);
			File outputDirectory = createDirectory(inputDirectory.getAbsolutePath()+ File.separator + OUTPUT_SUB_DIRECTORY);
			File outputTempDirectory = createDirectory(outputDirectory + File.separator + OUTPUT_SUB_TEMP_DIRECTORY);
			File outputZIPFile = new File(outputDirectory, testStep.getName() + ATFX_OUTPUT_FILE_NAME_PREFIX);
			
			try {				
				if(!outputZIPFile.exists()) {
	 				LOG.debug("executing external pak application ...");
					returnValue = createATFX(pakApplicationFile, inputDirectory, outputTempDirectory, modelFile);
					if(returnValue != 0) {
						String errorMessage = translateErrorCode(returnValue);
						throw new FileConverterException("external pak application ends with an error (message: '" + errorMessage + "')");
					}
					LOG.debug("executing external pak application ... done!");				
					
					LOG.debug("executing zip process for pak application result ...");
								
					zipFolder(outputZIPFile.getAbsolutePath(), outputTempDirectory.getAbsolutePath(), true);
					LOG.debug("executing zip process for pak application result ... done");
				}
				
				fileRelease.fileLink = outputZIPFile.getAbsolutePath();
				
		} catch(IOException |InterruptedException e) {
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
		throws IOException, InterruptedException{
		
		ProcessBuilder pb = new ProcessBuilder(pakApplicationFile.getAbsolutePath(), 
			"-M", inputDirectory.getAbsolutePath(), 
			"-E", outputTempDirectory.getAbsolutePath(), 
			"-MF", modelFile.getAbsolutePath());
		Process process = pb.start();
		
		return process.waitFor();
	}
	
	
	private String translateErrorCode(int returnValue) {
		
		String errorMessage;

		if(returnValue == 1) {
			errorMessage = "invalid input pak measurement";
		} else if(returnValue == 2) {
			errorMessage = "illegal command (a parameter is missing)";
		} else if(returnValue == 6) {
			errorMessage = "foreign model measurement";
		} else if(returnValue == 7) {
			errorMessage = "illegal input path format";
		} else if(returnValue == 8) {
			errorMessage = "source directory does not exist";
		} else if(returnValue == 9) {
			errorMessage = "target directory does not exist";
		} else if(returnValue == 10) {
			errorMessage = "target directory is not empty";
		} else if(returnValue == 11) {
			errorMessage = "illegal commant (model file parameter is missing)";
		} else {
			errorMessage = "unknown error with error code '" + returnValue + "'";
		}
		return errorMessage + "  (errorCode = '" + returnValue + "')";
	}
	
	
	private File locatePakApplicationFile(String pakApplicationFilePath) {
		File pakApplicationFile = new File(pakApplicationFilePath);
		if(!pakApplicationFile.exists()) {
			throw new FileReleaseException("pak application executable file at '" + pakApplicationFile.getAbsolutePath() 
				+ "' does not exist!");
		}
		return pakApplicationFile;
	}
	
	

	
	private File locateModelFileForModelType(String modelType) {
		File modelFile = new File(COMPONENT_CONFIG_ROOT_FOLDER + File.separator + modelType + File.separator + MODEL_FILE_NAME);
		if(!modelFile.exists()) {
			throw new FileReleaseException("mapping file for atfx model with type '" + modelType 
				+ "' does not exist! (expected at: '" + modelFile.getAbsolutePath() + "')");
		}
		return modelFile;
	}
	
}
