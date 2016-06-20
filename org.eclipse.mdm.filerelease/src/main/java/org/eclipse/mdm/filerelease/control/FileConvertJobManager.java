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

package org.eclipse.mdm.filerelease.control;

import java.util.concurrent.Executor;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.query.DataAccessException;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.eclipse.mdm.filerelease.control.converter.FileConverterPAK2ATFX;
import org.eclipse.mdm.filerelease.control.converter.FileConverterPAK2RAW;
import org.eclipse.mdm.filerelease.control.converter.IFileConverter;
import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class FileConvertJobManager {

	private static final Logger LOG = LoggerFactory.getLogger(FileConvertJobManager.class); 
	
	@Inject
	Executor executor;
	
	@EJB
	private ConnectorService connectorService;
	
	@Inject
	private FileConverterPAK2RAW fileConverterPAK2RAW;
	@Inject 
	private FileConverterPAK2ATFX fileConverterPAK2ATFX;
	
	public void release(FileRelease fileRelease) {	
		
		try {
			EntityManager em = this.connectorService.getEntityManagerByName(fileRelease.sourceName);
			TestStep testStep = em.load(TestStep.class, fileRelease.id);
			
			IFileConverter converter = getFileConverterByFormat(fileRelease);
			String identifier = fileRelease.identifier;
			
			LOG.info("starting file release process for FileRelease with identifier '" + identifier 
				+ "' (with '" + converter.getConverterName() + "') ...");
					
			Runnable runnable = new FileConvertJob(fileRelease, converter, testStep, em);
			this.executor.execute(runnable);
		} catch(DataAccessException e) {
			throw new FileReleaseException(e.getMessage(), e);
		}
		
	}
	
	public IFileConverter getFileConverterByFormat(FileRelease fileRelease) {
		if(fileRelease.format.equalsIgnoreCase(FileReleaseManager.CONVERTER_FORMAT_PAK2RAW)) {
			return this.fileConverterPAK2RAW;
	
		} else if(fileRelease.format.equalsIgnoreCase(FileReleaseManager.CONVERTER_FORMAT_PAK2ATFX)) {
			return this.fileConverterPAK2ATFX;
		}
		
		throw new FileReleaseException("no FileConverter found for format '" + fileRelease.format 
		+ "' on executing FileRelease with identifier '" + fileRelease.identifier + "'!");
	}

	
	
	

	
}
