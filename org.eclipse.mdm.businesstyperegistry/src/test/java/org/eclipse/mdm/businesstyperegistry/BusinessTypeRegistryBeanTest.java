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

package org.eclipse.mdm.businesstyperegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.model.URI;
import org.eclipse.mdm.businesstyperegistry.bean.BusinessTypeRegistryBean;
import org.eclipse.mdm.connector.ConnectorBeanLI;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * JUNIT Test for {@link BusinessTypeRegistryBean}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class BusinessTypeRegistryBeanTest {

	@Test
	public void testGetActions() {
		
		BusinessTypeRegistryException businessTypeRegistryException = null;
		Exception otherException = null;
		
		try {
		
			BusinessTypeRegistryBeanLI businessTypeRegistryBean = createdMockedBusinessRegistryBean();
			List<ActionBeanLI> actionList = businessTypeRegistryBean.getActions("default", org.eclipse.mdm.api.base.model.Test.class);
			
			assertNotNull("action list should not be null", actionList);
			assertEquals("action list size should be 1", 1, actionList.size());			
			assertEquals("name of action bean should be 'testAction'", "testAction", actionList.get(0).getActionName());
			
		} catch(BusinessTypeRegistryException e) {
			businessTypeRegistryException = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no business object registry exception should occur", businessTypeRegistryException);
		assertNull("no other exception should occur", otherException);
		
	}
	
	
	@Test 
	public void testCreateURI() {		

		BusinessTypeRegistryException businessTypeRegistryException = null;
		Exception otherException = null;
						
		try {
			String sourceName = "MDMENV";
			long id = 1L;
			
			BusinessTypeRegistryBeanLI businessTypeRegistryBean = createdMockedBusinessRegistryBean();
			URI envURI = businessTypeRegistryBean.createURI(sourceName, Environment.class, id);			
			URI testURI = businessTypeRegistryBean.createURI(sourceName, org.eclipse.mdm.api.base.model.Test.class, id);
			URI testStepURI = businessTypeRegistryBean.createURI(sourceName, TestStep.class, id);	
			URI measurementURI = businessTypeRegistryBean.createURI(sourceName, Measurement.class, id);	
			URI channelGroupURI = businessTypeRegistryBean.createURI(sourceName, ChannelGroup.class, id);	
			URI channelURI = businessTypeRegistryBean.createURI(sourceName, Channel.class, id);	
			
			assertEquals("Environment URI check", envURI.toString(), "mdmDataItem://MDMENV/Environment/1");
			assertEquals("Test URI check", testURI.toString(), "mdmDataItem://MDMENV/Test/1");
			assertEquals("TestStep URI check", testStepURI.toString(), "mdmDataItem://MDMENV/TestStep/1");
			assertEquals("Measurement URI check", measurementURI.toString(), "mdmDataItem://MDMENV/Measurement/1");
			assertEquals("ChannelGroup URI check", channelGroupURI.toString(), "mdmDataItem://MDMENV/ChannelGroup/1");
			assertEquals("Channel URI check", channelURI.toString(), "mdmDataItem://MDMENV/Channel/1");
			
			
		} catch(BusinessTypeRegistryException e) {
			businessTypeRegistryException = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no navigator exception should occur", businessTypeRegistryException );
		assertNull("no other exception should occur", otherException);
	}
	
	
	
	private BusinessTypeRegistryBeanLI createdMockedBusinessRegistryBean() throws Exception {
		
		List<Class<? extends Entity>> typeList = new ArrayList<>();
		typeList.add(org.eclipse.mdm.api.base.model.Test.class);
				
		ActionBeanLI actionBean = Mockito.mock(ActionBeanLI.class);
		when(actionBean.getActionName()).thenReturn("testAction");
		when(actionBean.getSupportedEntityTypes()).thenReturn(typeList);
		
		Bean<?> bean = Mockito.mock(Bean.class);
		
		Set<Bean<?>> set = new HashSet<>();
		set.add(bean);
		
		BeanManager bm = Mockito.mock(BeanManager.class);
		when(bm.getBeans(ActionBeanLI.class)).thenReturn(set);
		when(bm.getReference(eq(bean), eq(ActionBeanLI.class), anyObject())).thenReturn(actionBean);
		
		ConnectorBeanLI connectorBeanMock = BusinessTypeRegistryMockHelper.createConnectorMock();
		
		BusinessTypeRegistryBeanLI businessTypeRegistryBean = new BusinessTypeRegistryBean();		
		
		Field bmField= businessTypeRegistryBean.getClass().getDeclaredField("beanManager");
		bmField.setAccessible(true);
		bmField.set(businessTypeRegistryBean, bm);
		bmField.setAccessible(false);
		
		Field connectorField= businessTypeRegistryBean.getClass().getDeclaredField("connectorBean");
		connectorField.setAccessible(true);
		connectorField.set(businessTypeRegistryBean, connectorBeanMock);
		connectorField.setAccessible(false);
	
		
		return businessTypeRegistryBean;		
	}
	
	
}
