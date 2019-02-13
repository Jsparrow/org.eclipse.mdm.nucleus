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


package org.eclipse.mdm.filerelease.control;

import java.io.File;
import java.util.concurrent.Executor;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.filerelease.control.converter.FileConverterPAK2ATFX;
import org.eclipse.mdm.filerelease.control.converter.FileConverterPAK2RAW;
import org.eclipse.mdm.filerelease.control.converter.IFileConverter;
import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * FileConvertJobManager Bean implementation.
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class FileConvertJobManager {

	private static final Logger LOG = LoggerFactory.getLogger(FileConvertJobManager.class);

	@Inject
	Executor executor;

	@Inject
	private ConnectorService connectorService;

	@Inject
	private FileConverterPAK2RAW fileConverterPAK2RAW;
	@Inject
	private FileConverterPAK2ATFX fileConverterPAK2ATFX;

	/**
	 * releases the given {@link FileRelease} (generates the file in the
	 * specified format)
	 * 
	 * @param fileRelease
	 *            {@link FileRelease} to release
	 * @param targetDirectory
	 *            target output directory of the generated file
	 */
	public void release(FileRelease fileRelease, File targetDirectory) {

		try {
			ApplicationContext context = this.connectorService.getContextByName(fileRelease.sourceName);
			EntityManager em = context
					.getEntityManager()
					.orElseThrow(() -> new FileReleaseException("Entity manager not present!"));
			
			TestStep testStep = em.load(TestStep.class, fileRelease.id);

			IFileConverter converter = getFileConverterByFormat(fileRelease);
			String identifier = fileRelease.identifier;

			LOG.info(new StringBuilder().append("starting file release process for FileRelease with identifier '").append(identifier).append("' (with '").append(converter.getConverterName()).append("') ...").toString());

			Runnable runnable = new FileConvertJob(fileRelease, converter, testStep, context, targetDirectory);
			this.executor.execute(runnable);
		} catch (DataAccessException e) {
			throw new FileReleaseException(e.getMessage(), e);
		}

	}

	private IFileConverter getFileConverterByFormat(FileRelease fileRelease) {
		if (StringUtils.equalsIgnoreCase(fileRelease.format, FileReleaseManager.CONVERTER_FORMAT_PAK2RAW)) {
			return this.fileConverterPAK2RAW;

		} else if (StringUtils.equalsIgnoreCase(fileRelease.format, FileReleaseManager.CONVERTER_FORMAT_PAK2ATFX)) {
			return this.fileConverterPAK2ATFX;
		}

		throw new FileReleaseException(new StringBuilder().append("no FileConverter found for format '").append(fileRelease.format).append("' on executing FileRelease with identifier '").append(fileRelease.identifier).append("'!").toString());
	}

}
