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

package org.eclipse.mdm.search;

import java.util.List;

import javax.ejb.Local;

import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.search.bean.SearchBean;

/**
 * Local interface for {@link SearchBean}
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Local
public interface SearchBeanLI {

	/**
	 * lists available global and user specific search definitions
	 * 
	 * @return available global and user specific search definitions
	 * @throws SearchException if an error occurs during lookup available search definitions
	 */
	List<SearchDefinition> listSearchDefinitions() throws SearchException ;
	
	/**
	 * executes a search using the given filter and returns the search result
	 * 
	 * @return a search result 
	 * @throws SearchException if an error occurs during executing the search
	 */
	<T extends Entity> List<T> search(URI uri, Class<T> resultType, String filterString) throws SearchException;	
	
}
