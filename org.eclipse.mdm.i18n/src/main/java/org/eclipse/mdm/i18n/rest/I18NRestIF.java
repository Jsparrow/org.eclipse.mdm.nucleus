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

package org.eclipse.mdm.i18n.rest;

import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.i18n.bean.I18NBean;
import org.eclipse.mdm.i18n.rest.tansferable.LocalizedAttribute;

/**
 * Rest interface for {@link I18NBean}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public interface I18NRestIF {

	
	
	/**
	 * delegates the rest call to the {@link I18NBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @return a JSON String with a list of {@link LocalizedAttribute}s for
	 * the MDM business object type {@link Environment}
	 */
	String localizeEnvironment(String sourceName);
	
	
	
	/**
	 * delegates the rest call to the {@link I18NBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @return a JSON String with a list of {@link LocalizedAttribute}s for
	 * the MDM business object type {@link Test}
	 */
	String localizeTest(String sourceName);
	
	
	
	/**
	 * delegates the rest call to the {@link I18NBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @return a JSON String with a list of {@link LocalizedAttribute}s for
	 * the MDM business object type {@link TestStep}
	 */
	String localizeTestStep(String sourceName);
	
	
	
	/**
	 * delegates the rest call to the {@link I18NBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @return a JSON String with a list of {@link LocalizedAttribute}s for
	 * the MDM business object type {@link Measurement}
	 */
	String localizeMeasurement(String sourceName);
	
	
	
	/**
	 * delegates the rest call to the {@link I18NBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @return a JSON String with a list of {@link LocalizedAttribute}s for
	 * the MDM business object type {@link ChannelGroup}
	 */
	String localizeChannelGroup(String sourceName);
	
	
	
	/**
	 * delegates the rest call to the {@link I18NBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @return a JSON String with a list of {@link LocalizedAttribute}s for
	 * the MDM business object type {@link Channel}
	 */
	String localizeChannel(String sourceName);
}
