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

package org.eclipse.mdm.contextprovider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.mdm.api.base.model.ContextComponent;
import org.eclipse.mdm.api.base.model.ContextRoot;
import org.eclipse.mdm.api.base.model.ContextType;
import org.eclipse.mdm.api.base.model.Value;
import org.eclipse.mdm.contextprovider.bean.ContextProviderBean;
import org.junit.Test;

/**
 * JUNIT Test for {@link ContextProviderBean}
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class ContextProviderBeanTest {

	@Test 
	public void testGetTestStepContext() {
		
		ContextProviderException navigatorExcpetion = null;
		Exception otherException = null;
						
		try {
			
			ContextProviderBeanLI contextProviderBean = createMockedContextProviderBean();
			Context context = contextProviderBean.getTestStepContext(ContextProviderMockHelper.URI_TESTSTEP);

			assertNotNull("context should not be null", context);
			
			checkContextContent(context);
			
		} catch(ContextProviderException e) {
			navigatorExcpetion = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no navigator exception should occur", navigatorExcpetion);
		assertNull("no other exception should occur", otherException);
	}
		
	
	
	@Test 
	public void testGetMeasurementContext() {
		ContextProviderException navigatorExcpetion = null;
		Exception otherException = null;
						
		try {
			
			ContextProviderBeanLI contextProviderBean = createMockedContextProviderBean();
			Context context = contextProviderBean.getMeasurementContext(ContextProviderMockHelper.URI_MEASUREMENT);

			assertNotNull("context should not be null", context);

			checkContextContent(context);
			
		} catch(ContextProviderException e) {
			navigatorExcpetion = e;
		} catch(Exception e) {
			otherException = e;
		}
		
		assertNull("no navigator exception should occur", navigatorExcpetion);
		assertNull("no other exception should occur", otherException);
	}



	private void checkContextContent(Context context) {
		
		//checkOrderedContext
		Map<ContextType, ContextRoot> orderedContext = context.getOrderedContext();
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
		
		//measuredContext
		Map<ContextType, ContextRoot> measuredContext = context.getMeasuredContext();
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
	
	
	public ContextProviderBeanLI createMockedContextProviderBean() throws Exception {
		ContextProviderBeanLI contextProviderBean = new ContextProviderBean();
		Field field = contextProviderBean.getClass().getDeclaredField("connectorBean");
		field.setAccessible(true);
		field.set(contextProviderBean, ContextProviderMockHelper.createConnectorMock());
		field.setAccessible(false);
		return contextProviderBean;
	}
}
