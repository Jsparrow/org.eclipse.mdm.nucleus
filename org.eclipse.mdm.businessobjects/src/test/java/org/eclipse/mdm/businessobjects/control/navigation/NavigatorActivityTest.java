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

package org.eclipse.mdm.businessobjects.control.navigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.List;

import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.businessobjects.control.NavigationActivity;
import org.junit.Test;


public class NavigatorActivityTest {


	@Test
	public void testGetEnvironments() throws Exception {
		
		NavigationActivity navigationActivity = createdMockedActivity();
		List<Environment> envList = navigationActivity.getEnvironments();
		
		assertNotNull("environment list should be not null", envList);
		assertEquals("environment list size should be " + NavigationActivityMockHelper.ITEM_COUNT, 
			NavigationActivityMockHelper.ITEM_COUNT, envList.size());
	
	}

	@Test
	public void testGetTests() throws Exception {
		
		NavigationActivity navigationActivity = createdMockedActivity();
		List<Environment> envList = navigationActivity.getEnvironments();
		String sourceName = envList.get(0).getSourceName();
		List<org.eclipse.mdm.api.base.model.Test> testList = navigationActivity.getTests(sourceName);
		
		assertNotNull("test list should be not null", testList);
		assertEquals("test list size should be " + NavigationActivityMockHelper.ITEM_COUNT, 
				NavigationActivityMockHelper.ITEM_COUNT, testList.size());		
	}

	
	@Test
	public void testGetTestSteps() throws Exception {		
			
		NavigationActivity navigationActivity = createdMockedActivity();
		List<Environment> envList = navigationActivity.getEnvironments();
		String sourceName = envList.get(0).getSourceName();
		List<org.eclipse.mdm.api.base.model.Test> testList = navigationActivity.getTests(sourceName);
		org.eclipse.mdm.api.base.model.Test test = testList.get(0);
		List<TestStep> testStepList = navigationActivity.getTestSteps(sourceName, test.getID());
		
		assertNotNull("teststep list should be not null", testStepList);
		assertEquals("teststep list size should be " + NavigationActivityMockHelper.ITEM_COUNT, 
				NavigationActivityMockHelper.ITEM_COUNT, testStepList.size());	
	}
	
	@Test
	public void testGetMeasurements() throws Exception {
		
		NavigationActivity navigationActivity = createdMockedActivity();
		List<Environment> envList = navigationActivity.getEnvironments();
		String sourceName = envList.get(0).getSourceName();
		List<org.eclipse.mdm.api.base.model.Test> testList = navigationActivity.getTests(sourceName);
		org.eclipse.mdm.api.base.model.Test test = testList.get(0);
		List<TestStep> testStepList = navigationActivity.getTestSteps(sourceName, test.getID());
		TestStep testStep = testStepList.get(0);
		List<Measurement> measurementList = navigationActivity.getMeasurements(sourceName, testStep.getID());
		
		assertNotNull("measurement list should be not null", measurementList);
		assertEquals("measurement list size should be " + NavigationActivityMockHelper.ITEM_COUNT, 
				NavigationActivityMockHelper.ITEM_COUNT, measurementList.size());
		
	}
	
	@Test
	public void testGetChannelGroups() throws Exception {
		
		NavigationActivity navigationActivity = createdMockedActivity();
		List<Environment> envList = navigationActivity.getEnvironments();
		String sourceName = envList.get(0).getSourceName();
		List<org.eclipse.mdm.api.base.model.Test> testList = navigationActivity.getTests(sourceName);
		org.eclipse.mdm.api.base.model.Test test = testList.get(0);
		List<TestStep> testStepList = navigationActivity.getTestSteps(sourceName, test.getID());
		TestStep testStep = testStepList.get(0);
		List<Measurement> measurementList = navigationActivity.getMeasurements(sourceName, testStep.getID());
		Measurement measurement = measurementList.get(0);
		List<ChannelGroup> channelGroupList = navigationActivity.getChannelGroups(sourceName, measurement.getID());
		
		assertNotNull("channel group list should be not null", channelGroupList);
		assertEquals("channel group list size should be " + NavigationActivityMockHelper.ITEM_COUNT, 
				NavigationActivityMockHelper.ITEM_COUNT, channelGroupList.size());
	}
	
	
	@Test
	public void testGetChannels() throws Exception {
		
		NavigationActivity navigationActivity = createdMockedActivity();
		List<Environment> envList = navigationActivity.getEnvironments();
		String sourceName = envList.get(0).getSourceName();
		List<org.eclipse.mdm.api.base.model.Test> testList = navigationActivity.getTests(sourceName);
		org.eclipse.mdm.api.base.model.Test test = testList.get(0);
		List<TestStep> testStepList = navigationActivity.getTestSteps(sourceName, test.getID());
		TestStep testStep = testStepList.get(0);
		List<Measurement> measurementList = navigationActivity.getMeasurements(sourceName, testStep.getID());
		Measurement measurement = measurementList.get(0);
		List<ChannelGroup> channelGroupList = navigationActivity.getChannelGroups(sourceName, measurement.getID());
		ChannelGroup channelGroup = channelGroupList.get(0);
		List<Channel> channelList = navigationActivity.getChannels(sourceName, channelGroup.getID());
		
		assertNotNull("channel list should be not null", channelList);
		assertEquals("channel list size should be " + NavigationActivityMockHelper.ITEM_COUNT, 
				NavigationActivityMockHelper.ITEM_COUNT, channelList.size());
			
	}

	
	public NavigationActivity createdMockedActivity() throws Exception {
		
		NavigationActivity navigationActivity = new NavigationActivity();
		Field field = navigationActivity.getClass().getDeclaredField("connectorService");
		field.setAccessible(true);
		field.set(navigationActivity, NavigationActivityMockHelper.createConnectorMock());
		field.setAccessible(false);
		return navigationActivity;
	}
	
}
