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
import org.eclipse.mdm.businessobjects.control.ContextActivity;
import org.junit.Test;

public class ContextActivityTest {

	@Test
	public void testGetTestStepContext() throws Exception {

		ContextActivity contextActitity = createMockedActivity();
		Map<String, Map<ContextType, ContextRoot>> contextMap = contextActitity.getTestStepContext("MDM", "1");

		assertNotNull("context should not be null", contextMap);
		checkContextContent(contextMap);

	}

	@Test
	public void testGetMeasurementContext() throws Exception {

		ContextActivity contextActitity = createMockedActivity();
		Map<String, Map<ContextType, ContextRoot>> contextMap = contextActitity.getMeasurementContext("MDM", "1");

		assertNotNull("context should not be null", contextMap);
		checkContextContent(contextMap);
	}

	private void checkContextContent(Map<String, Map<ContextType, ContextRoot>> contextMap) {

		Map<ContextType, ContextRoot> orderedContext = contextMap.get(ContextActivity.CONTEXT_GROUP_ORDERED);
		Set<Entry<ContextType, ContextRoot>> orderedEntrySet = orderedContext.entrySet();
		assertEquals("size of entry set should be 3", 3, orderedEntrySet.size());

		orderedEntrySet.stream().map(Map.Entry::getValue).forEach(cr -> {
			List<ContextComponent> ccList = cr.getContextComponents();
			assertEquals("size of context components should be 10", 10, ccList.size());
			ccList.stream().map(ContextComponent::getValues).forEach(values -> assertEquals("size of value map should be 10", 10, values.size()));
		});

		Map<ContextType, ContextRoot> measuredContext = contextMap.get(ContextActivity.CONTEXT_GROUP_MEASURED);
		Set<Entry<ContextType, ContextRoot>> measuredEntrySet = measuredContext.entrySet();
		assertEquals("size of entry set should be 3", 3, measuredEntrySet.size());

		measuredEntrySet.stream().map(Map.Entry::getValue).forEach(cr -> {
			List<ContextComponent> ccList = cr.getContextComponents();
			assertEquals("size of context components should be 10", 10, ccList.size());
			ccList.stream().map(ContextComponent::getValues).forEach(values -> assertEquals("size of value map should be 10", 10, values.size()));
		});
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
