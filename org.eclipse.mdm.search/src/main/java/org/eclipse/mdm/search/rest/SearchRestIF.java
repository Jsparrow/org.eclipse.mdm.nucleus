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

package org.eclipse.mdm.search.rest;

import org.eclipse.mdm.search.SearchDefinition;
import org.eclipse.mdm.search.bean.SearchBean;

/**
 * Rest interface for {@link SearchBean}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public interface SearchRestIF {
	
	/**
	 * delegates the rest call to the {@link SearchBean} implementation
	 * 
	 * @return a JSON String which contains a list of {@link SearchDefinition}s
	 */
	String listSearchDefinitions();
	
	
	String searchTests(String sourceName, String type);
	
	String searchTestSteps(String sourceName, String filter);
	
	String searchMeasurements(String sourceName, String filter);
}
