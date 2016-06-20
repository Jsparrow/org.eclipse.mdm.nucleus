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

package org.eclipse.mdm.businessobjects.control.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.mdm.api.base.model.ContextComponent;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.businessobjects.control.ContextActivity;
import org.junit.Test;


public class ContextActivityTest {

	@Test 
	public void testGetTestStepContext() throws Exception {
		
		ContextActivity contextActitity = createMockedActivity();
		Map<String, Map<ContextType, ContextRoot>> contextMap = contextActitity.
			getTestStepContext("MDM", 1L);

		assertNotNull("context should not be null", contextMap);		
		checkContextContent(contextMap);
			
	}
		
	
	
	@Test 
	public void testGetMeasurementContext() throws Exception {
		
		ContextActivity contextActitity = createMockedActivity();
		Map<String, Map<ContextType, ContextRoot>> contextMap = contextActitity.
			getMeasurementContext("MDM", 1L);

		assertNotNull("context should not be null", contextMap);
		checkContextContent(contextMap);		
	}



	private void checkContextContent(Map<String, Map<ContextType, ContextRoot>> contextMap) {
		
		Map<ContextType, ContextRoot> orderedContext = contextMap.get(ContextActivity.CONTEXT_GROUP_ORDERED);
		Set<Entry<ContextType, ContextRoot>> orderedEntrySet = orderedContext.entrySet();
		assertEquals("size of entry set should be 3", 3, orderedEntrySet.size());
		
		for(Entry<ContextType, ContextRoot> entry : orderedEntrySet) {
			ContextRoot cr = entry.getValue();
			List<ContextComponent> ccList = cr.getContextComponents();
			assertEquals("size of context components should be 10", 10, ccList.size());
			for(ContextComponent cc : ccList) {
				Map<String, Value> values = cc.getValues();
				assertEquals("size of value map should be 10", 10, values.size());
			}
		}			
		
		Map<ContextType, ContextRoot> measuredContext = contextMap.get(ContextActivity.CONTEXT_GROUP_MEASURED);
		Set<Entry<ContextType, ContextRoot>> measuredEntrySet = measuredContext.entrySet();
		assertEquals("size of entry set should be 3", 3, measuredEntrySet.size());
		
		for(Entry<ContextType, ContextRoot> entry : measuredEntrySet) {
			ContextRoot cr = entry.getValue();
			List<ContextComponent> ccList = cr.getContextComponents();
			assertEquals("size of context components should be 10", 10, ccList.size());
			for(ContextComponent cc : ccList) {
				Map<String, Value> values = cc.getValues();
				assertEquals("size of value map should be 10", 10, values.size());
			}
		}
	}
	
	
	public ContextActivity createMockedActivity() throws Exception {
		ContextActivity contextActivity = new ContextActivity();
		Field field = contextActivity.getClass().getDeclaredField("connectorService");
		field.setAccessible(true);
		field.set(contextActivity, ContextActivityMockHelper.createConnectorMock());
		field.setAccessible(false);
		return contextActivity;
	}
}
