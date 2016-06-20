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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.eclipse.mdm.property.BeanProperty;


@RequestScoped
public class FileConverterPAK2RAW extends AbstractFileConverter {
	
	private static final String OUTPUT_SUB_DIRECTORY = "raw";
	private static final String ATFX_OUTPUT_FILE_NAME_PREFIX = "_RAW.zip";
	
	@Inject
	@BeanProperty
	private String pakInputEntity = "";
	@Inject
	@BeanProperty
	private String pakInputAttribute = "";
	
	@Override
	public void execute(FileRelease fileRelease, TestStep testStep, EntityManager em) throws FileConverterException {
		
		String inputPath = locateStringAttributeValue(em, testStep, 
				this.pakInputEntity, this.pakInputAttribute);	
		File inputDirectory = locateInputDirectory(inputPath);
		
		File outputDirectory = createDirectory(inputDirectory.getAbsolutePath()+ File.separator + OUTPUT_SUB_DIRECTORY);
		File outputZIPFile = new File(outputDirectory, testStep.getName() + ATFX_OUTPUT_FILE_NAME_PREFIX);
		
		if(outputZIPFile.exists()) {
			LOG.debug("executing zip process for pak raw data ...");		
			zipFolder(outputZIPFile.getAbsolutePath(), inputDirectory.getAbsolutePath(), true);		
			LOG.debug("executing zip process for pak raw data ... done");
		}
		
		fileRelease.fileLink = outputZIPFile.getAbsolutePath();
	}

	@Override
	public String getConverterName() {
		return "PAK2RAW ";
	}

}
