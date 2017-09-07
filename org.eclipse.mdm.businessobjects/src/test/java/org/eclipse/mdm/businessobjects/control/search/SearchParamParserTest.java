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
package org.eclipse.mdm.businessobjects.control.search;

import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.Condition;
import org.eclipse.mdm.api.base.query.Filter;
import org.eclipse.mdm.api.base.query.FilterItem;
import org.eclipse.mdm.api.base.query.ComparisonOperator;
import org.eclipse.mdm.api.base.query.BooleanOperator;
import org.eclipse.mdm.businessobjects.control.SearchParamParser;
import org.junit.Test;

public class SearchParamParserTest {

	@Test
	public void testParseFilterString() {
		Filter filter = SearchParamParser.parseFilterString(SearchMockHelper.createETListMock(),
				"TestStep.Name " + SearchParamParser.EQUALS_OPERATOR + " * " + SearchParamParser.AND_DELIMITER
						+ "Test.Name " + SearchParamParser.LIKE_OPERATOR + " * ");

		assertNotNull("The filter should not be null.", filter);
		List<FilterItem> filterItems = filter.stream().collect(Collectors.toList());
		boolean containsTestNameAttr = false;
		boolean containsTestStepNameAttr = false;
		boolean containsAndOperator = false;

		for (int i = 0; i < filterItems.size(); i++) {
			if (filterItems.get(i).isCondition()) {
				Condition cond = filterItems.get(i).getCondition();
				Attribute attr = filterItems.get(i).getCondition().getAttribute();
				if ("Test".equals(attr.getEntityType().getName()) && "Name".equals(attr.getName())) {
					assertTrue("The type of the operation should be " + ComparisonOperator.CASE_INSENSITIVE_LIKE,
							cond.getComparisonOperator().name().equals(ComparisonOperator.CASE_INSENSITIVE_LIKE.name()));
					containsTestNameAttr = true;
				}

				if ("TestStep".equals(attr.getEntityType().getName()) && "Name".equals(attr.getName())) {
					assertTrue("The type of the operation should be " + ComparisonOperator.CASE_INSENSITIVE_EQUAL,
							cond.getComparisonOperator().name().equals(ComparisonOperator.CASE_INSENSITIVE_EQUAL.name()));
					containsTestStepNameAttr = true;
				}
			} else {
				if (filterItems.get(i).isBooleanOperator() && filterItems.get(i).getBooleanOperator().equals(BooleanOperator.AND)) {
					containsAndOperator = true;
				}

			}
		}

		assertTrue("The filter should contain a filter condition that contains the attribute Test.Name ",
				containsTestNameAttr);
		assertTrue("The filter should contain a filter condition that contains the attribute TestStep.Name ",
				containsTestStepNameAttr);
		assertTrue("The filter should contain an AND Operator.", containsAndOperator);
	}

	@Test
	public void testParseSelectString() {
		List<Attribute> selAttrs = SearchParamParser.parseSelectString(SearchMockHelper.createETListMock(),
				"Test.Name,TestStep.Name");
		assertNotNull("The select attributes list should not be null.", selAttrs);
		assertEquals("The size of the select attribute list should be 2.", 2, selAttrs.size());
		assertEquals("The name of the entity type in first item of the select attribute list should be \"Test\"",
				selAttrs.get(0).getEntityType().getName(), "Test");
		assertEquals("The name of the attribute in first item of the select attribute list should be \"Name\"",
				selAttrs.get(0).getName(), "Name");
		assertEquals("The name of the entity type in second item of the select attribute list should be \"TestStep\"",
				selAttrs.get(1).getEntityType().getName(), "TestStep");
		assertEquals("The name of the attribute in second item of the select attribute list should be \"Name\"",
				selAttrs.get(1).getName(), "Name");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseFilterStringUnsupAttr() {
		// Check if a IllegalArgumentException if a unsupported attribute is
		// given.
		SearchParamParser.parseFilterString(SearchMockHelper.createETListMock(),
				"Test.SomeUnsupportedAttribute " + SearchParamParser.EQUALS_OPERATOR + " * ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseFilterStringUnsupportedET() {
		// Check if a IllegalArgumentException if a unsupported entity type is
		// given.
		SearchParamParser.parseFilterString(SearchMockHelper.createETListMock(),
				"SomeUnsupportedEntityType.Name " + SearchParamParser.EQUALS_OPERATOR + " * ");
	}

}
