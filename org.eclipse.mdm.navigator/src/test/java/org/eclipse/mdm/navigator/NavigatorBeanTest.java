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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.util.List;

import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.navigator.bean.NavigatorBean;
import org.junit.Test;


/**
 * JUNIT Test for {@link NavigatorBean}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class NavigatorBeanTest {


	@Test
	public void testGetEnvironments() {
		
		NavigatorException navigatorExcpetion = null;
		Exception otherException = null;
						
		try {
			
			NavigatorBeanLI navigatorBean = createMockedNavigatorBean();
			List<Environment> envList = navigatorBean.getEnvironments();
			
			assertNotNull("environment list should be not null", envList);
			assertEquals("environment list size should be " + NavigatorBeanMockHelper.ITEM_COUNT, 
				NavigatorBeanMockHelper.ITEM_COUNT, envList.size());
			
		} catch(NavigatorException e) {
			navigatorExcpetion = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no navigator exception should occur", navigatorExcpetion);
		assertNull("no other exception should occur", otherException);
	}

	@Test
	public void testGetTests(){
		
		NavigatorException navigatorExcpetion = null;
		Exception otherException = null;
						
		try {
			
			NavigatorBeanLI navigatorBean = createMockedNavigatorBean();
			List<Environment> envList = navigatorBean.getEnvironments();
			List<org.eclipse.mdm.api.base.model.Test> testList = navigatorBean.getTests(envList.get(0).getURI());
			
			assertNotNull("test list should be not null", testList);
			assertEquals("test list size should be " + NavigatorBeanMockHelper.ITEM_COUNT, 
					NavigatorBeanMockHelper.ITEM_COUNT, testList.size());
			
		} catch(NavigatorException e) {
			navigatorExcpetion = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no navigator exception should occur", navigatorExcpetion);
		assertNull("no other exception should occur", otherException);
	}

	@Test
	public void testGetTestSteps() {
		
		NavigatorException navigatorExcpetion = null;
		Exception otherException = null;
						
		try {
			
			NavigatorBeanLI navigatorBean = createMockedNavigatorBean();
			List<Environment> envList = navigatorBean.getEnvironments();
			List<org.eclipse.mdm.api.base.model.Test> testList = navigatorBean.getTests(envList.get(0).getURI());
			List<TestStep> testStepList = navigatorBean.getTestSteps(testList.get(0).getURI());
			
			assertNotNull("teststep list should be not null", testStepList);
			assertEquals("teststep list size should be " + NavigatorBeanMockHelper.ITEM_COUNT, 
					NavigatorBeanMockHelper.ITEM_COUNT, testStepList.size());
			
		} catch(NavigatorException e) {
			navigatorExcpetion = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no navigator exception should occur", navigatorExcpetion);
		assertNull("no other exception should occur", otherException);
	}
	
	@Test
	public void testGetMeasurements() {
		
		NavigatorException navigatorExcpetion = null;
		Exception otherException = null;
						
		try {
			
			NavigatorBeanLI navigatorBean = createMockedNavigatorBean();
			List<Environment> envList = navigatorBean.getEnvironments();
			List<org.eclipse.mdm.api.base.model.Test> testList = navigatorBean.getTests(envList.get(0).getURI());
			List<TestStep> testStepList = navigatorBean.getTestSteps(testList.get(0).getURI());
			List<Measurement> measurementList = navigatorBean.getMeasurements(testStepList.get(0).getURI());
			
			assertNotNull("measurement list should be not null", measurementList);
			assertEquals("measurement list size should be " + NavigatorBeanMockHelper.ITEM_COUNT, 
					NavigatorBeanMockHelper.ITEM_COUNT, measurementList.size());
			
		} catch(NavigatorException e) {
			navigatorExcpetion = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no navigator exception should occur", navigatorExcpetion);
		assertNull("no other exception should occur", otherException);
	}
	
	@Test
	public void testGetChannelGroups() {
		
		NavigatorException navigatorExcpetion = null;
		Exception otherException = null;
						
		try {
			
			NavigatorBeanLI navigatorBean = createMockedNavigatorBean();
			List<Environment> envList = navigatorBean.getEnvironments();
			List<org.eclipse.mdm.api.base.model.Test> testList = navigatorBean.getTests(envList.get(0).getURI());
			List<TestStep> testStepList = navigatorBean.getTestSteps(testList.get(0).getURI());
			List<Measurement> measurementList = navigatorBean.getMeasurements(testStepList.get(0).getURI());
			List<ChannelGroup> channelGroupList = navigatorBean.getChannelGroups(measurementList.get(0).getURI());
			
			assertNotNull("channel group list should be not null", channelGroupList);
			assertEquals("channel group list size should be " + NavigatorBeanMockHelper.ITEM_COUNT, 
					NavigatorBeanMockHelper.ITEM_COUNT, channelGroupList.size());
			
		} catch(NavigatorException e) {
			navigatorExcpetion = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no navigator exception should occur", navigatorExcpetion);
		assertNull("no other exception should occur", otherException);
	}
	
	@Test
	public void testGetChannels() {
		
		NavigatorException navigatorExcpetion = null;
		Exception otherException = null;
						
		try {
			
			NavigatorBeanLI navigatorBean = createMockedNavigatorBean();
			List<Environment> envList = navigatorBean.getEnvironments();
			List<org.eclipse.mdm.api.base.model.Test> testList = navigatorBean.getTests(envList.get(0).getURI());
			List<TestStep> testStepList = navigatorBean.getTestSteps(testList.get(0).getURI());
			List<Measurement> measurementList = navigatorBean.getMeasurements(testStepList.get(0).getURI());
			List<ChannelGroup> channelGroupList = navigatorBean.getChannelGroups(measurementList.get(0).getURI());
			List<Channel> channelList = navigatorBean.getChannels(channelGroupList.get(0).getURI());
			
			assertNotNull("channel list should be not null", channelList);
			assertEquals("channel list size should be " + NavigatorBeanMockHelper.ITEM_COUNT, 
					NavigatorBeanMockHelper.ITEM_COUNT, channelList.size());
			
		} catch(NavigatorException e) {
			navigatorExcpetion = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no navigator exception should occur", navigatorExcpetion);
		assertNull("no other exception should occur", otherException);
		
	}

	
	public NavigatorBeanLI createMockedNavigatorBean() throws Exception {
		NavigatorBeanLI navigatorBean = new NavigatorBean();
		Field field = navigatorBean.getClass().getDeclaredField("connectorBean");
		field.setAccessible(true);
		field.set(navigatorBean, NavigatorBeanMockHelper.createConnectorMock());
		field.setAccessible(false);
		return navigatorBean;
	}
	
}
