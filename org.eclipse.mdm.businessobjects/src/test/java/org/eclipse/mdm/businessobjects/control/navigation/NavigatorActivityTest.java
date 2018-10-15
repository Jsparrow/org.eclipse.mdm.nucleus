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


package org.eclipse.mdm.businessobjects.control.navigation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.businessobjects.control.NavigationActivity;
import org.junit.Before;
import org.junit.Test;

public class NavigatorActivityTest {

	private NavigationActivity navigationActivity;
	private String sourceName = "MDMENV_1";

	@Before
	public void setup() throws Exception {
		navigationActivity = new NavigationActivity(NavigationActivityMockHelper.createConnectorMock());
	}

	@Test
	public void testGetEnvironments() throws Exception {

		List<Environment> envList = navigationActivity.getEnvironments();

		assertNotNull("environment list should be not null", envList);
		assertThat(envList.size(), is(5));
	}

	@Test
	public void testGetTests() throws Exception {
		List<org.eclipse.mdm.api.base.model.Test> testList = navigationActivity.getTests(sourceName);

		assertNotNull("test list should be not null", testList);
		assertEquals("test list size should be " + NavigationActivityMockHelper.ITEM_COUNT,
				NavigationActivityMockHelper.ITEM_COUNT, testList.size());
	}

	@Test
	public void testGetTestSteps() throws Exception {
		List<org.eclipse.mdm.api.base.model.Test> testList = navigationActivity.getTests(sourceName);
		org.eclipse.mdm.api.base.model.Test test = testList.get(0);
		List<TestStep> testStepList = navigationActivity.getTestSteps(sourceName, test.getID());

		assertNotNull("teststep list should be not null", testStepList);
		assertEquals("teststep list size should be " + NavigationActivityMockHelper.ITEM_COUNT,
				NavigationActivityMockHelper.ITEM_COUNT, testStepList.size());
	}

	@Test
	public void testGetMeasurements() throws Exception {
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

}
