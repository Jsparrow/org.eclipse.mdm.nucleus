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

import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.dflt.ApplicationContext;
import org.eclipse.mdm.filerelease.entity.FileRelease;

/**
 * 
 * Interface for file converters
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public interface IFileConverter {

	/**
	 * 
	 * Executes the file conversion
	 * 
	 * @param fileRelease
	 *            The file release that contains the file to convert
	 * @param testStep
	 *            The {@link TestStep}
	 * @param context
	 *            The {@link ApplicationContext}
	 * @throws FileConverterException
	 *             Thrown if the file conversion fails.
	 */
	void execute(FileRelease fileRelease, TestStep testStep, ApplicationContext context, File targetDirectory)
			throws FileConverterException;

	/**
	 * Returns the name of the file converter
	 * 
	 * @return The name of the file converter
	 */
	String getConverterName();
}
