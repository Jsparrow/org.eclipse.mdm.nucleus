/*
 * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

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

import org.eclipse.mdm.api.base.model.Entity;
import org.eclipse.mdm.businesstyperegistry.bean.BusinessTypeRegistryBean;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * JUNIT Test for {@link BusinessTypeRegistryBean}
 * @author Gigatronik Ingolstadt GmbH
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
		
		BusinessTypeRegistryBeanLI businessTypeRegistryBean = new BusinessTypeRegistryBean();
		Field field = businessTypeRegistryBean.getClass().getDeclaredField("beanManager");
		field.setAccessible(true);
		field.set(businessTypeRegistryBean, bm);
		field.setAccessible(false);
		
		return businessTypeRegistryBean;
		
	}
	
	
}
