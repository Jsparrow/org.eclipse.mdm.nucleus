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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.filerelease.control.converter.FileConverterException;
import org.eclipse.mdm.filerelease.control.converter.IFileConverter;
import org.eclipse.mdm.filerelease.entity.FileRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileConvertJob implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(FileConvertJob.class);
	
	private final IFileConverter fileConverter;
	private final FileRelease fileRelease;
	private final TestStep testStep;
	private final EntityManager em;
	
	public FileConvertJob(FileRelease fileRelease, IFileConverter fileConverter, 
		TestStep testStep, EntityManager em) {
		
		this.fileRelease = fileRelease;
		this.fileConverter = fileConverter;
		this.testStep = testStep;
		this.em = em;
	}

	@Override
	public void run() {
		try {						
			this.fileRelease.state = FileReleaseManager.FILE_RELEASE_STATE_PROGRESSING;
			
			this.fileConverter.execute(this.fileRelease, this.testStep, this.em);
			
			this.fileRelease.expire = calculateExpireDate(this.fileRelease.validity);
			this.fileRelease.state = FileReleaseManager.FILE_RELEASE_STATE_RELEASED;
			
		} catch(FileConverterException e) {
			this.fileRelease.state = FileReleaseManager.FILE_RELEASE_STATE_PROGRESSING_ERROR;
			this.fileRelease.errorMessage = e.getMessage();
			LOG.error(e.getMessage(), e);
		} catch(Exception e) {
			this.fileRelease.state = FileReleaseManager.FILE_RELEASE_STATE_PROGRESSING_ERROR;
			this.fileRelease.errorMessage = e.getMessage();
			LOG.error(e.getMessage(), e);
		}
	}
	
	
	private long calculateExpireDate(int validity) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, validity);
		return calendar.getTimeInMillis();
	}
}
