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


package org.eclipse.mdm.businessobjects.control.i18n;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Map;

import org.eclipse.mdm.api.base.adapter.Attribute;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.model.Channel;
import org.eclipse.mdm.api.base.model.ChannelGroup;
import org.eclipse.mdm.api.base.model.Environment;
import org.eclipse.mdm.api.base.model.Measurement;
import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.businessobjects.control.I18NActivity;
import org.eclipse.mdm.connector.boundary.ConnectorService;
import org.junit.Test;

public class I18NActivityTest {

	@Test
	public void testLocalizeAttributes() throws Exception {

		I18NActivity i18nActivity = createdMockedActivity();

		Map<Attribute, String> locEnvironmentAttrs = i18nActivity.localizeAttributes("default", Environment.class);
		Map<Attribute, String> locTestAttrs = i18nActivity.localizeAttributes("default",
				org.eclipse.mdm.api.base.model.Test.class);
		Map<Attribute, String> locTestStepAttrs = i18nActivity.localizeAttributes("default", TestStep.class);
		Map<Attribute, String> locMeasurementAttrs = i18nActivity.localizeAttributes("default", Measurement.class);
		Map<Attribute, String> locChannelGroupAttrs = i18nActivity.localizeAttributes("default", ChannelGroup.class);
		Map<Attribute, String> locChannelAttrs = i18nActivity.localizeAttributes("default", Channel.class);

		int expected = 2;
		assertEquals(new StringBuilder().append("map of environment attributes should contain '").append(expected).append("' localized attributes").toString(), expected,
				locEnvironmentAttrs.size());

		assertEquals(new StringBuilder().append("map of test attributes should contain '").append(expected).append("' localized attributes").toString(), expected,
				locTestAttrs.size());

		assertEquals(new StringBuilder().append("map of test step attributes should contain '").append(expected).append("' localized attributes").toString(), expected,
				locTestStepAttrs.size());

		assertEquals(new StringBuilder().append("map of measurement attributes should contain '").append(expected).append("' localized attributes").toString(), expected,
				locMeasurementAttrs.size());

		assertEquals(new StringBuilder().append("map of channel group attributes should contain '").append(expected).append("' localized attributes").toString(), expected,
				locChannelGroupAttrs.size());

		assertEquals(new StringBuilder().append("map of channel attributes should contain '").append(expected).append("' localized attributes").toString(), expected,
				locChannelAttrs.size());

	}

	@Test
	public void testLocalizeType() throws Exception {
		I18NActivity i18nActivity = createdMockedActivity();

		Map<EntityType, String> locEnvironmentType = i18nActivity.localizeType("default", Environment.class);
		Map<EntityType, String> locTestType = i18nActivity.localizeType("default",
				org.eclipse.mdm.api.base.model.Test.class);
		Map<EntityType, String> locTestStepType = i18nActivity.localizeType("default", TestStep.class);
		Map<EntityType, String> locMeasurementType = i18nActivity.localizeType("default", Measurement.class);
		Map<EntityType, String> locChannelGroupType = i18nActivity.localizeType("default", ChannelGroup.class);
		Map<EntityType, String> locChannelType = i18nActivity.localizeType("default", Channel.class);

		int expected = 1;
		assertEquals(new StringBuilder().append("map of Environment types should contain '").append(expected).append("' localized type").toString(), expected,
				locEnvironmentType.size());
		assertEquals(new StringBuilder().append("map of Test types should contain '").append(expected).append("' localized type").toString(), expected,
				locTestType.size());
		assertEquals(new StringBuilder().append("map of TestStep types should contain '").append(expected).append("' localized type").toString(), expected,
				locTestStepType.size());
		assertEquals(new StringBuilder().append("map of Measurement types should contain '").append(expected).append("' localized type").toString(), expected,
				locMeasurementType.size());
		assertEquals(new StringBuilder().append("map of ChannelGroup types should contain '").append(expected).append("' localized type").toString(), expected,
				locChannelGroupType.size());
		assertEquals(new StringBuilder().append("map of Channel types should contain '").append(expected).append("' localized type").toString(), expected,
				locChannelType.size());
	}

	@Test
	public void testAllLocalizeAttributes() throws Exception {
		I18NActivity i18nActivity = createdMockedActivity();

		Map<Attribute, String> localizedMap = i18nActivity.localizeAllAttributes("default");

		int expected = 12; // 6 types with 2 attributes

		assertEquals(new StringBuilder().append("map of all attributes should contain '").append(expected).append("' localized attributes").toString(), expected,
				localizedMap.size());
	}

	@Test
	public void testLocalizeAllTypes() throws Exception {
		I18NActivity i18nActivity = createdMockedActivity();

		Map<EntityType, String> localizedMap = i18nActivity.localizeAllTypes("default");

		int expected = 6;
		assertEquals(new StringBuilder().append("map of all types should contain '").append(expected).append("' localized types").toString(), expected,
				localizedMap.size());
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
