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
import org.eclipse.mdm.property.GlobalProperty;

/**
 * 
 * {@link IFileConverter} implementation for converting PAK to RAW
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@RequestScoped
public class FileConverterPAK2RAW extends AbstractFileConverter {

	private static final String ATFX_OUTPUT_FILE_NAME_PREFIX = "_RAW.zip";

	@Inject
	@GlobalProperty("filerelease.converter.raw.pakInputEntity")
	private String pakInputEntity = "";
	@Inject
	@GlobalProperty("filerelease.converter.raw.pakInputAttribute")
	private String pakInputAttribute = "";

	@Override
	public void execute(FileRelease fileRelease, TestStep testStep, EntityManager em, File targetDirectory)
			throws FileConverterException {

		String pakInputEntityValue = super.readPropertyValue(this.pakInputEntity, true, null, "pakInputEntity");
		String pakInputAttributeValue = super.readPropertyValue(this.pakInputAttribute, true, null,
				"pakInputAttribute");

		String inputPath = locateStringAttributeValue(em, testStep, pakInputEntityValue, pakInputAttributeValue);
		File inputDirectory = locateInputDirectory(inputPath);

		File outputDirectory = createDirectory(targetDirectory.getAbsolutePath() + File.separator + fileRelease.name);
		File outputZIPFile = new File(outputDirectory, fileRelease.name + ATFX_OUTPUT_FILE_NAME_PREFIX);

		if (!outputZIPFile.exists()) {
			LOG.debug("executing zip process for pak raw data ...");
			zipFolder(outputZIPFile.getAbsolutePath(), inputDirectory.getAbsolutePath(), true);
			LOG.debug("executing zip process for pak raw data ... done");
		}

		fileRelease.fileLink = fileRelease.name + File.separator + outputZIPFile.getName();
	}

	@Override
	public String getConverterName() {
		return "PAK2RAW ";
	}

}
