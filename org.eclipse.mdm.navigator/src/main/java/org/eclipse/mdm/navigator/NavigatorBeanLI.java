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

package org.eclipse.mdm.navigator;

import java.util.List;

import javax.ejb.Local;

import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.Test;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.navigator.bean.NavigatorBean;

/**
 * Local interface for {@link NavigatorBean}
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
@Local
public interface NavigatorBeanLI {

	
	
	/**
	 * returns the MDM {@link Environment} business objects of all connected MDM systems
	 * 
	 * @return MDM {@link Environment} business objects
	 * @throws NavigatorException if an error occurs during the {@link Environment} load operation
	 */
	List<Environment> getEnvironments() throws NavigatorException;
	
	
	
	/**
	 * returns all MDM {@link Test} business objects of the connected MDM system identified by the given {@link URI}
	 * 
	 * @param environemntURI {@link URI} to identify the MDM system 
	 * @return MDM {@link Test} business objects 
	 * @throws NavigatorException if an error occurs during the {@link Test} load operation
	 */
	List<Test> getTests(URI environemntURI) throws NavigatorException;
	
	
	
	/**
	 * returns all MDM {@link TestStep} business object children for a MDM {@link Test} identified by the given {@link URI}
	 * 
	 * @param testURI {@link URI} to identify the parent MDM {@link Test}
	 * @return MDM {@link TestStep} business objects
	 * @throws NavigatorException if an error occurs during the {@link TestStep} load operation
	 */
	List<TestStep> getTestSteps(URI testURI) throws NavigatorException;
	
	
	
	/**
	 * returns all MDM {@link Measurement} business object children for a MDM {@link TestStep} identified by the given {@link URI}
	 * 
	 * @param testStepURI URI to identify the parent MDM TestStep
	 * @return MDM Measurement business objects
	 * @throws NavigatorException if an error occurs during the Measurement load operation
	 */
	List<Measurement> getMeasurements(URI testStepURI) throws NavigatorException;
	
	
	
	/**
	 * returns all MDM ChannelGroup business object children for a MDM {@link Measurement} identified by the given {@link URI}
	 * 
	 * @param measurementURI {@link URI} to identify the parent MDM {@link Measurement}
	 * @return MDM {@link ChannelGroup} business objects
	 * @throws NavigatorException if an error occurs during the {@link ChannelGroup} load operation
	 */
	List<ChannelGroup> getChannelGroups(URI measurementURI) throws NavigatorException;
	
	
	
	/**
	 * returns all MDM {@link Channel} business object children for a MDM {@link ChannelGroup} identified by the given {@link URI} 
	 * 
	 * @param channelGroupURI {@link URI} to identify the parent MDM {@link ChannelGroup}
	 * @return MDM {@link Channel} business objects
	 * @throws NavigatorException if an error occurs during the {@link Channel} load operation
	 */
	List<Channel> getChannels(URI channelGroupURI) throws NavigatorException;


}
