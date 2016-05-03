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

package org.eclipse.mdm.businessobjects.control.i18n;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Map;

import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.junit.Test;


public class I18NActivityTest {

	
	@Test
	public void testLocalizeType() throws Exception {

			I18NActivity i18nBean = createdMockedActivity();
			
			Map<Attribute, String> locEnvironmentAttrs = i18nBean.localizeAttributes("default", Environment.class);
			Map<Attribute, String> locTestAttrs = i18nBean.localizeAttributes("default", org.eclipse.mdm.api.base.model.Test.class);
			Map<Attribute, String> locTestStepAttrs = i18nBean.localizeAttributes("default", TestStep.class);
			Map<Attribute, String> locMeasurementAttrs = i18nBean.localizeAttributes("default", Measurement.class);
			Map<Attribute, String> locChannelGroupAttrs = i18nBean.localizeAttributes("default", ChannelGroup.class);
			Map<Attribute, String> locChannelAttrs = i18nBean.localizeAttributes("default", Channel.class);
			
			int expected = 2;
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
			
	
		
	}
	
	
	private I18NActivity createdMockedActivity() throws Exception {
				
		ConnectorService connectorBeanMock = I18NActivityMockHelper.createConnectorMock();
		
		I18NActivity i18nActivity = new I18NActivity();
		Field fieldConnector = i18nActivity.getClass().getDeclaredField("connectorService");
		fieldConnector.setAccessible(true);
		fieldConnector.set(i18nActivity, connectorBeanMock);
		fieldConnector.setAccessible(false);
				
		return i18nActivity;
	}
}
