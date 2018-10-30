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

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.mdm.api.base.query.ComparisonOperator.*;
import static org.eclipse.mdm.api.base.query.ComparisonOperator.GREATER_THAN;
import static org.eclipse.mdm.api.base.query.ComparisonOperator.GREATER_THAN_OR_EQUAL;
import static org.eclipse.mdm.api.base.query.ComparisonOperator.IN_SET;
import static org.eclipse.mdm.api.base.query.ComparisonOperator.LESS_THAN;
import static org.eclipse.mdm.api.base.query.ComparisonOperator.LESS_THAN_OR_EQUAL;
import static org.eclipse.mdm.api.base.query.ComparisonOperator.LIKE;
import static org.eclipse.mdm.api.base.query.ComparisonOperator.NOT_EQUAL;
import static org.eclipse.mdm.businessobjects.control.FilterParser.parseFilterString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.api.SoftAssertions;
import org.eclipse.mdm.api.base.adapter.EntityType;
import org.eclipse.mdm.api.base.query.ComparisonOperator;
import org.eclipse.mdm.api.base.query.Filter;
import org.junit.Test;

public class FilterParserTest {
	private List<EntityType> entities = SearchMockHelper.createETListMock();
	private Map<String, EntityType> map = entities.stream().collect(Collectors.toMap(EntityType::getName, e -> e));

	@Test
	public void testEmpty() throws Exception {
		assertThat(parseFilterString(entities, "").isEmtpty()).isTrue();
	}
	
	@Test
	public void testNull() throws Exception {
		assertThat(parseFilterString(entities, null).isEmtpty()).isTrue();
	}
	

	@Test
	public void testOperatorEqual() throws Exception {

		String filterString = "TestStep.Name eq 'test'";
		
		Filter expected = Filter.and().add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}

	@Test
	public void testOperatorNotEqual() throws Exception {

		String filterString = "TestStep.Name ne 'test'";
		
		Filter expected = Filter.and().add(NOT_EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorLessThan() throws Exception {

		String filterString = "TestStep.LongAttribute lt 10";
		
		Filter expected = Filter.and().add(LESS_THAN.create(map.get("TestStep").getAttribute("LongAttribute"), 10L));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorLessThanOrEqual() throws Exception {

		String filterString = "TestStep.LongAttribute le 10";
		
		Filter expected = Filter.and().add(LESS_THAN_OR_EQUAL.create(map.get("TestStep").getAttribute("LongAttribute"), 10L));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorGreaterThan() throws Exception {

		String filterString = "TestStep.LongAttribute gt 10";
		
		Filter expected = Filter.and().add(GREATER_THAN.create(map.get("TestStep").getAttribute("LongAttribute"), 10L));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorGreaterThanOrEqual() throws Exception {

		String filterString = "TestStep.LongAttribute ge 10";
		
		Filter expected = Filter.and().add(GREATER_THAN_OR_EQUAL.create(map.get("TestStep").getAttribute("LongAttribute"), 10L));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorNotInSet() throws Exception {

		String filterString = "TestStep.Name not_in ('test', 'measurement', 'crash')";
		
		Filter expected = Filter.and().add(NOT_IN_SET.create(map.get("TestStep").getAttribute("Name"), new String[] {"test", "measurement", "crash" }));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorLike() throws Exception {

		String filterString = "TestStep.Name lk 'test*'";
		
		Filter expected = Filter.and().add(LIKE.create(map.get("TestStep").getAttribute("Name"), "test*"));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorNotLike() throws Exception {

		String filterString = "TestStep.Name not_lk 'test*'";
		
		Filter expected = Filter.and().add(NOT_LIKE.create(map.get("TestStep").getAttribute("Name"), "test*"));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}

	
	@Test
	public void testOperatorCIEqual() throws Exception {

		String filterString = "TestStep.Name ci_eq 'test'";
		
		Filter expected = Filter.and().add(CASE_INSENSITIVE_EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}

	@Test
	public void testOperatorCINotEqual() throws Exception {

		String filterString = "TestStep.Name ci_ne 'test'";
		
		Filter expected = Filter.and().add(CASE_INSENSITIVE_NOT_EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorCILessThan() throws Exception {

		String filterString = "TestStep.LongAttribute ci_lt 10";
		
		Filter expected = Filter.and().add(CASE_INSENSITIVE_LESS_THAN.create(map.get("TestStep").getAttribute("LongAttribute"), 10L));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorCILessThanOrEqual() throws Exception {

		String filterString = "TestStep.LongAttribute ci_le 10";
		
		Filter expected = Filter.and().add(CASE_INSENSITIVE_LESS_THAN_OR_EQUAL.create(map.get("TestStep").getAttribute("LongAttribute"), 10L));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorCIGreaterThan() throws Exception {

		String filterString = "TestStep.LongAttribute ci_gt 10";
		
		Filter expected = Filter.and().add(CASE_INSENSITIVE_GREATER_THAN.create(map.get("TestStep").getAttribute("LongAttribute"), 10L));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorCIGreaterThanOrEqual() throws Exception {

		String filterString = "TestStep.LongAttribute ci_ge 10";
		
		Filter expected = Filter.and().add(CASE_INSENSITIVE_GREATER_THAN_OR_EQUAL.create(map.get("TestStep").getAttribute("LongAttribute"), 10L));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorCIInSet() throws Exception {

		String filterString = "TestStep.Name ci_in ('test', 'measurement', 'crash')";
		
		Filter expected = Filter.and().add(CASE_INSENSITIVE_IN_SET.create(map.get("TestStep").getAttribute("Name"), new String[] {"test", "measurement", "crash" }));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorCINotInSet() throws Exception {

		String filterString = "TestStep.Name ci_not_in ('test', 'measurement', 'crash')";
		
		Filter expected = Filter.and().add(CASE_INSENSITIVE_NOT_IN_SET.create(map.get("TestStep").getAttribute("Name"), new String[] {"test", "measurement", "crash" }));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorCILike() throws Exception {

		String filterString = "TestStep.Name ci_lk 'test*'";
		
		Filter expected = Filter.and().add(CASE_INSENSITIVE_LIKE.create(map.get("TestStep").getAttribute("Name"), "test*"));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}

	@Test
	public void testOperatorCINotLike() throws Exception {

		String filterString = "TestStep.Name ci_not_lk 'test*'";
		
		Filter expected = Filter.and().add(CASE_INSENSITIVE_NOT_LIKE.create(map.get("TestStep").getAttribute("Name"), "test*"));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorIsNull() throws Exception {

		String filterString = "TestStep.Name is_null";
		
		Filter expected = Filter.and().add(IS_NULL.create(map.get("TestStep").getAttribute("Name"), null));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}

	@Test
	public void testOperatorIsNotNull() throws Exception {

		String filterString = "TestStep.Name is_not_null";
		
		Filter expected = Filter.and().add(IS_NOT_NULL.create(map.get("TestStep").getAttribute("Name"), null));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOperatorBetween() throws Exception {

		String filterString = "TestStep.LongAttribute bw (100, 1000)";
		
		Filter expected = Filter.and().add(BETWEEN.create(map.get("TestStep").getAttribute("LongAttribute"), new long[] { 100, 1000 }));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testInSetWithImplicitToStringConversion() throws Exception {

		String filterString = "TestStep.Name in (12.2, 'test', 1)";
		
		Filter expected = Filter.and().add(IN_SET.create(map.get("TestStep").getAttribute("Name"), new String[] {"12.2", "test", "1" }));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test(expected = NumberFormatException.class)
	public void testInSetWithStringForDoubleAttribute() throws Exception {

		String filterString = "TestStep.DoubleAttribute in (12.2, 'Test', 1)";
		
		Filter expected = Filter.and().add(IN_SET.create(map.get("TestStep").getAttribute("DoubleAttribute"), new double[] { 12.2 }));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testInSetWithNumericDataTypes() throws Exception {

		String filterString = "TestStep.DoubleAttribute in (12.2, -1.0, 0.006022)";
		
		Filter expected = Filter.and().add(IN_SET.create(map.get("TestStep").getAttribute("DoubleAttribute"), new double[] { 12.2, -1.0, 0.006022 }));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testSimple() throws Exception {

		String filterString = "TestStep.Name eq 'test'";
		
		Filter expected = Filter.and().add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"));

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}

	@Test
	public void testSimpleWithBrackets() throws Exception {

		String filterString = "(TestStep.Name eq 'test')";
		
		Filter expected = Filter.and().add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testOr() throws Exception {
	
		String filterString = "(TestStep.Name eq 'test') or (Test.LongAttribute gt 1)";
		
		Filter expected = Filter.or()
				.add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"))
				.add(ComparisonOperator.GREATER_THAN.create(map.get("Test").getAttribute("LongAttribute"), 1L));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}

	@Test
	public void testAnd() throws Exception {

		String filterString = "(TestStep.Name eq 'test') and (Test.LongAttribute gt 1)";
		
		Filter expected = Filter.and()
				.add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"))
				.add(ComparisonOperator.GREATER_THAN.create(map.get("Test").getAttribute("LongAttribute"), 1L));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testAnd2() throws Exception {

		String filterString = "TestStep.Name eq 'test' and Test.LongAttribute gt 1";
		
		Filter expected = Filter.and()
				.add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"))
				.add(ComparisonOperator.GREATER_THAN.create(map.get("Test").getAttribute("LongAttribute"), 1L));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testSimpleNot() throws Exception {

		String filterString = "not(TestStep.Name eq 'test')";
		
		Filter expected = Filter.and()
				.add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("Name"), "test")).invert();
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testNot() throws Exception {

		String filterString = "TestStep.Name eq 'test' and not(Test.LongAttribute gt 1)";
		
		Filter expected = Filter.and()
				.add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"))
				.merge(Filter.or()
						.add(ComparisonOperator.GREATER_THAN.create(map.get("Test").getAttribute("LongAttribute"), 1L))
						.invert());

		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	
	@Test
	public void testNested() throws Exception {

		String filterString = "TestStep.Name eq 'test' and (Test.LongAttribute gt 1 or Test.IntegerAttribute lt 10)";
		
		Filter expected = Filter.and().add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"))
				.merge(Filter.or()
						.add(ComparisonOperator.GREATER_THAN.create(map.get("Test").getAttribute("LongAttribute"), 1L))
						.add(ComparisonOperator.LESS_THAN.create(map.get("Test").getAttribute("IntegerAttribute"), 10)));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testCaseOfKeywordsIsIgnored() throws Exception {

		String filterString = "TestStep.Name Eq 'test' AND (Test.LongAttribute gt 1 oR Test.IntegerAttribute LT 10)";
		
		Filter expected = Filter.and().add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"))
				.merge(Filter.or()
						.add(ComparisonOperator.GREATER_THAN.create(map.get("Test").getAttribute("LongAttribute"), 1L))
						.add(ComparisonOperator.LESS_THAN.create(map.get("Test").getAttribute("IntegerAttribute"), 10)));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testAndPrecedence() throws Exception {

		String filterString = "TestStep.Name eq 'test' and Test.LongAttribute gt 1 or Test.IntegerAttribute lt 10";
		
		Filter expected = Filter.or().merge(
				Filter.and()
					.add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"))
					.add(ComparisonOperator.GREATER_THAN.create(map.get("Test").getAttribute("LongAttribute"), 1L)),
				Filter.and()
					.add(ComparisonOperator.LESS_THAN.create(map.get("Test").getAttribute("IntegerAttribute"), 10)));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testAndPrecedence2() throws Exception {

		String filterString = "TestStep.Name eq 'test' or Test.LongAttribute gt 1 and Test.IntegerAttribute lt 10";
		
		Filter expected = Filter.or().merge(
				Filter.and()
					.add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("Name"), "test")),
				Filter.and()
					.add(ComparisonOperator.GREATER_THAN.create(map.get("Test").getAttribute("LongAttribute"), 1L))
					.add(ComparisonOperator.LESS_THAN.create(map.get("Test").getAttribute("IntegerAttribute"), 10)));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testComplexFilter() throws Exception {
	
		String filterString = "TestStep.Name eq 'test' and (Test.LongAttribute gt 1 or Test.DoubleAttribute lt 12.3 or Test.IntegerAttribute eq 2) "
				+ " and not(TestStep.FloatAttribute gt 3.14 or TestStep.BooleanAttribute eq true) or TestStep.DateAttribute gt '2017-01-02T01:02:03'";
		
		Filter expected = Filter.or().merge(
				Filter.and().add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("Name"), "test"))
					.merge(Filter.or()
							.add(ComparisonOperator.GREATER_THAN.create(map.get("Test").getAttribute("LongAttribute"), 1L))
							.add(ComparisonOperator.LESS_THAN.create(map.get("Test").getAttribute("DoubleAttribute"), 12.3))
							.add(ComparisonOperator.EQUAL.create(map.get("Test").getAttribute("IntegerAttribute"), 2)),
							Filter.or()
								.add(ComparisonOperator.GREATER_THAN.create(map.get("TestStep").getAttribute("FloatAttribute"), 3.14f))
								.add(ComparisonOperator.EQUAL.create(map.get("TestStep").getAttribute("BooleanAttribute"), true))
								.invert()),
				Filter.and().add(ComparisonOperator.GREATER_THAN.create(map.get("TestStep").getAttribute("DateAttribute"), LocalDateTime.parse("2017-01-02T01:02:03"))));
					
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}

	@Test
	public void testDataTypeString() throws Exception {
		String filterString = "TestStep.Name eq 'Test'";
		
		Filter expected = Filter.and().add(ComparisonOperator.EQUAL.create(
						map.get("TestStep").getAttribute("Name"), 
						"Test"));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testDataTypeDate() throws Exception {
		String filterString = "TestStep.DateAttribute gt '2017-09-28T12:13:14'";
		
		Filter expected = Filter.and().add(ComparisonOperator.GREATER_THAN.create(
						map.get("TestStep").getAttribute("DateAttribute"), 
						LocalDateTime.parse("2017-09-28T12:13:14")));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testDataTypeDateWithMillies() throws Exception {
		String filterString = "TestStep.DateAttribute gt '2017-09-29T12:13:14.123456'";
		
		Filter expected = Filter.and().add(ComparisonOperator.GREATER_THAN.create(
						map.get("TestStep").getAttribute("DateAttribute"), 
						LocalDateTime.parse("2017-09-29T12:13:14.123456")));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testDataTypeDateSequence() throws Exception {
		String filterString = "TestStep.DateAttribute bw ('2017-09-28T12:13:14', '2017-09-29T17:18:19')";
		
		Filter expected = Filter.and().add(ComparisonOperator.BETWEEN.create(
						map.get("TestStep").getAttribute("DateAttribute"), 
						new LocalDateTime[] {
							LocalDateTime.parse("2017-09-28T12:13:14"),
							LocalDateTime.parse("2017-09-29T17:18:19")
						}));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testDataTypeBoolean() throws Exception {
		String filterString = "TestStep.BooleanAttribute eq true";
		
		Filter expected = Filter.and().add(ComparisonOperator.EQUAL.create(
						map.get("TestStep").getAttribute("BooleanAttribute"), 
						true));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}

	@Test
	public void testDataTypeByte() throws Exception {
		String filterString = "TestStep.ByteAttribute eq 127";
		
		Filter expected = Filter.and().add(ComparisonOperator.EQUAL.create(
						map.get("TestStep").getAttribute("ByteAttribute"), 
						(byte) 127));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}

	@Test
	public void testDataTypeShort() throws Exception {
		String filterString = "TestStep.ShortAttribute eq 1024";
		
		Filter expected = Filter.and().add(ComparisonOperator.EQUAL.create(
						map.get("TestStep").getAttribute("ShortAttribute"), 
						(short) 1024));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testDataTypeInteger() throws Exception {
		String filterString = "TestStep.IntegerAttribute eq " + Integer.MAX_VALUE;
		
		Filter expected = Filter.and().add(ComparisonOperator.EQUAL.create(
						map.get("TestStep").getAttribute("IntegerAttribute"), 
						Integer.MAX_VALUE));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testDataTypeLong() throws Exception {
		String filterString = "TestStep.LongAttribute eq " + Long.MAX_VALUE;
		
		Filter expected = Filter.and().add(ComparisonOperator.EQUAL.create(
						map.get("TestStep").getAttribute("LongAttribute"), 
						Long.MAX_VALUE));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testDataTypeFloat() throws Exception {
		String filterString = "TestStep.FloatAttribute gt 12.3";
		
		Filter expected = Filter.and().add(ComparisonOperator.GREATER_THAN.create(
						map.get("TestStep").getAttribute("FloatAttribute"), 
						12.3f));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testDataTypeDouble() throws Exception {
		String filterString = "TestStep.DoubleAttribute gt 12.2";
		
		Filter expected = Filter.and().add(ComparisonOperator.GREATER_THAN.create(
						map.get("TestStep").getAttribute("DoubleAttribute"), 
						12.2));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testKeywordsInValue() throws Exception {
		String filterString = "Test.Name eq 'y eq z and not(x)'";
		
		Filter expected = Filter.and().add(EQUAL.create(
						map.get("Test").getAttribute("Name"), 
						"y eq z and not(x)"));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testEscapedQuotesInValue() throws Exception {
		String filterString = "Test.Name eq 'Test \\'abc\\' 2'";
		
		Filter expected = Filter.and().add(EQUAL.create(
						map.get("Test").getAttribute("Name"), 
						"Test 'abc' 2"));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testEscapedDoubleQuotesInValue() throws Exception {
		String filterString = "Test.Name eq 'Test \"abc\"'";
		
		Filter expected = Filter.and().add(EQUAL.create(
						map.get("Test").getAttribute("Name"), 
						"Test \"abc\""));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testBackslashInValue() throws Exception {
		String filterString = "Test.Name eq 'c:\\Temp'";
		
		Filter expected = Filter.and().add(EQUAL.create(
						map.get("Test").getAttribute("Name"), 
						"c:\\Temp"));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testNewlineInValue() throws Exception {
		String filterString = "Test.Name eq 'new\nline'";
		
		Filter expected = Filter.and().add(EQUAL.create(
						map.get("Test").getAttribute("Name"), 
						"new\nline"));
		
		assertThat(parseFilterString(entities, filterString)).isEqualTo(expected);
	}
	
	@Test
	public void testInvalidFilterStrings() {
		SoftAssertions softly = new SoftAssertions();
		
		softly.assertThatThrownBy(() -> parseFilterString(entities, "xyz"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageStartingWith("Could not parse filter string");
		
		softly.assertThatThrownBy(() -> parseFilterString(entities, "and"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageStartingWith("Could not parse filter string");
		
		softly.assertThatThrownBy(() -> parseFilterString(entities, "x eq y"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageStartingWith("Could not parse filter string");
		
		softly.assertThatThrownBy(() -> parseFilterString(entities, "Test.Name eq y"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageStartingWith("Could not parse filter string");
		
		softly.assertThatThrownBy(() -> parseFilterString(entities, "((Test.Name eq 'y')"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageStartingWith("Could not parse filter string");
		
		softly.assertThatThrownBy(() -> parseFilterString(entities, "Test.Name eq 'y' and Test.Name lt 'x')"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageStartingWith("Could not parse filter string");

		softly.assertThatThrownBy(() -> parseFilterString(entities, "x.y eq 'z'"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageStartingWith("Entity x not found in data source");
		
		softly.assertAll();
	}
}
