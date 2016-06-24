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

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

/**
 * 
 * {@link Executor} implementation.
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Stateless
public class FileReleaseExecutor implements Executor {

	@Override
	@Asynchronous
	public void execute(Runnable command) {
		command.run();
	}

}
