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


package org.eclipse.mdm.businessobjects.control.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.mdm.api.base.model.TestStep;
import org.eclipse.mdm.businessobjects.control.SearchActivity;
import org.eclipse.mdm.businessobjects.entity.SearchAttribute;
import org.junit.Test;

public class SearchActivityTest {

	@Test
	public void testSearch() throws Exception {
		SearchActivity activity = new SearchActivity();
		List<TestStep> searchResult = activity.search(SearchMockHelper.createContextMock(), TestStep.class,
				"TestStep.Name eq '*' ");
		assertNotNull("search result list should not be null", searchResult);
		assertEquals("The size of the search result list should be " + SearchMockHelper.ITEM_COUNT,
				SearchMockHelper.ITEM_COUNT, searchResult.size());
	}

	@Test
	public void listAvailableAttributes() throws Exception {
		SearchActivity activity = new SearchActivity();
		List<SearchAttribute> attributes = activity.listAvailableAttributes(SearchMockHelper.createContextMock(),
				TestStep.class);
		assertNotNull("test list should be not null", attributes);
		assertEquals("The attributes list size should be " + SearchMockHelper.ITEM_COUNT, SearchMockHelper.ITEM_COUNT,
				attributes.size());
	}

}
