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

package org.eclipse.mdm.navigator.rest;

import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.businesstyperegistry.rest.transferable.Entry;
import org.eclipse.mdm.navigator.bean.NavigatorBean;

/**
 * Rest interface for {@link NavigatorBean}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public interface NavigatorRestIF {
	
	
	
	/**
	 * delegates the rest call to the {@link NavigatorBean} implementation
	 * 
	 * @return a JSON String which contains a list of {@link Environment} {@link Entry}s
	 */
	String getEnvironments();
	
	
	
	/**	
	 * delegates the rest call to the {@link NavigatorBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @return a JSON String which contains a list of {@link Test} {@link Entry}s
	 */
	String getTests(String sourceName);
	
	
	
	/**	
	 * delegates the rest call to the {@link NavigatorBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param parentID id of the parent MDM {@link Test} business object
	 * @return a JSON String which contains a list of {@link TestStep} {@link Entry}s
	 */
	String getTestSteps(String sourceName, long parentID);
	
	
	
	/**	
	 * delegates the rest call to the {@link NavigatorBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param parentID id of the parent MDM {@link TestStep} business object
	 * @return a JSON String which contains a list of {@link Measurement} {@link Entry}s
	 */
	String getMeasurements(String sourceName, long parentID);
	
	
	
	/**
	 * delegates the rest call to the {@link NavigatorBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param parentID id of the parent MDM {@link Measurement} business object
	 * @return a JSON String which contains a list of {@link ChannelGroup} {@link Entry}s
	 */
	String getChannelGroups(String sourceName, long parentID);
	
	
	
	/**
	 * delegates the rest call to the {@link NavigatorBean} implementation
	 * 
	 * @param sourceName source name (MDM {@link Environment} name)
	 * @param parentID id of the parent MDM {@link ChannelGroup} business object
	 * @return a JSON String which contains a list of {@link Channel} {@link Entry}s
	 */
	String getChannels(String sourceName, long parentID);
}
