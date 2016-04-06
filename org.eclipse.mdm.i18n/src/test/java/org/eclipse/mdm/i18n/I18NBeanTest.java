/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.mdm.i18n;
import java.lang.reflect.Field;
import java.util.Map;

import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.connector.ConnectorBeanLI;
import org.eclipse.mdm.i18n.bean.I18NBean;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUNIT Test for {@link I18NBean}
 * @author Gigatronik Ingolstadt GmbH
 *
 */
public class I18NBeanTest {

	
	@Test
	public void testLocalizeType() {
		
		
		I18NException i18nException = null;
		Exception otherException = null;
		
		try {
			I18NBeanLI i18nBean = createdMockedI18NBean();
			
			Map<String, String> locEnvironmentAttrs = i18nBean.localizeType("default", Environment.class);
			Map<String, String> locTestAttrs = i18nBean.localizeType("default", org.eclipse.mdm.api.base.model.Test.class);
			Map<String, String> locTestStepAttrs = i18nBean.localizeType("default", TestStep.class);
			Map<String, String> locMeasurementAttrs = i18nBean.localizeType("default", Measurement.class);
			Map<String, String> locChannelGroupAttrs = i18nBean.localizeType("default", ChannelGroup.class);
			Map<String, String> locChannelAttrs = i18nBean.localizeType("default", Channel.class);
			
			int expected = 3; //2 attributes localizations + 1 type localization
			assertEquals("map of environment attributes should contain '" + expected + "' localized attributes", 
				expected, locEnvironmentAttrs.size());
			
			assertEquals("map of test attributes should contain '" + expected + "' localized attributes", 
					expected, locTestAttrs.size());
			
			assertEquals("map of test step attributes should contain '" + expected + "' localized attributes", 
					expected, locTestStepAttrs.size());
			
			assertEquals("map of measurement attributes should contain '" + expected + "' localized attributes", 
					expected, locMeasurementAttrs.size());
			
			assertEquals("map of channel group attributes should contain '" + expected + "' localized attributes", 
					expected, locChannelGroupAttrs.size());
			
			assertEquals("map of channel attributes should contain '" + expected + "' localized attributes", 
					expected, locChannelAttrs.size());
			
		
		} catch(I18NException e) {
			i18nException = e;			
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no i18n exception should occur", i18nException);
		assertNull("no other exception should occur", otherException);
		
	}
	
	
	private I18NBeanLI createdMockedI18NBean() throws Exception {
				
		ConnectorBeanLI connectorBeanMock = I18NBeanMockHelper.createConnectorMock();
		
		I18NBeanLI i18NBean = new I18NBean();
		Field fieldConnector = i18NBean.getClass().getDeclaredField("connectorBean");
		fieldConnector.setAccessible(true);
		fieldConnector.set(i18NBean, connectorBeanMock);
		fieldConnector.setAccessible(false);
				
		return i18NBean;
	}
}
