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

package org.eclipse.mdm.i18n;

import java.util.Map;

import javax.ejb.Local;

import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.i18n.bean.I18NBean;

/**
 * Local interface for {@link I18NBean}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Local
public interface I18NBeanLI {

	/**
	 * localizes all attributes names from a type (e.g. TestStep.class)
	 *  
	 * @param sourceName name of the source (MDM {@link Environment} name)
	 * @param type MDM business object type (e.g. TestStep.class)
	 * @return a Map with localized attribute names (key is attribute name, value is localized attribute name)
	 * @throws I18NException if an error occurs during the localization process
	 */
	Map<String, String> localizeType(String sourceName, Class<? extends Entity> type) throws I18NException;
}
