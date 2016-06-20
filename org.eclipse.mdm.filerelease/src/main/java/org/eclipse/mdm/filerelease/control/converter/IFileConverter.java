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



import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.dflt.EntityManager;
import org.eclipse.mdm.filerelease.entity.FileRelease;

public interface IFileConverter {

	public void execute(FileRelease fileRelease, TestStep testStep, EntityManager em) throws FileConverterException;	
	public String getConverterName();
}
