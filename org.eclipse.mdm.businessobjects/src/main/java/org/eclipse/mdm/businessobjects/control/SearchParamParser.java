/*******************************************************************************
  * Copyright (c) 2016 Gigatronik Ingolstadt GmbH and others
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Sebastian Dirsch - initial implementation
  *******************************************************************************/

package org.eclipse.mdm.businessobjects.control;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.mail.search.SearchException;

import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.ComparisonOperator;
import org.eclipse.mdm.api.base.query.Condition;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.Filter;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import com.google.common.base.Strings;

/**
 * Class for parsing the filter strings.
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class SearchParamParser {

	public static final String AND_DELIMITER = " and ";
	public static final String OR_DELIMITER = " or ";

	public static final String EQUALS_OPERATOR = "eq";
	public static final String LESS_THAN_OPERATOR = "lt";
	public static final String LIKE_OPERATOR = "lk";
	public static final String GREATER_THAN_OPERATOR = "gt";

	/**
	 * Just hide the default constructor
	 */
	private SearchParamParser() {

	}
	/**
	 * Parses the given filter string.
	 * 
	 * @param entityType
	 *            The entity type.
	 * @param possibleSearchAttrs
	 *            The possible search attributes
	 * @param filterString
	 *            The filter string to parse.
	 * @return The Filter.
	 * @throws SearchException
	 *             Thrown if parsing fails.
	 */
	public static Filter parseFilterString(List<EntityType> possibleSearchAttrs, String filterString)
			throws IllegalArgumentException {
		if (Strings.isNullOrEmpty(filterString)) {
			return Filter.and();
		}
		SearchFilterBuilder filterBuilder = new SearchFilterBuilder();
		while (filterString.contains(AND_DELIMITER) || filterString.contains(OR_DELIMITER)) {
			int andIndex = filterString.indexOf(AND_DELIMITER) >= 0 ? filterString.indexOf(AND_DELIMITER)
					: Integer.MAX_VALUE;
			int orIndex = filterString.indexOf(OR_DELIMITER) >= 0 ? filterString.indexOf(OR_DELIMITER)
					: Integer.MAX_VALUE;
			if (andIndex < orIndex) {
				filterString = parseAndToken(possibleSearchAttrs, filterString, filterBuilder);
			} else {
				filterString = parseOrToken(possibleSearchAttrs, filterString, filterBuilder);
			}
		}
		filterBuilder.addCondition(parseCondition(possibleSearchAttrs, filterString));
		return filterBuilder.build();
	}

	/**
	 * Parses the given select string.
	 * 
	 * @param possibleSearchAttrs
	 *            The The possible search attributes
	 * @param selectString
	 *            The select string to parse.
	 * @return A list with the parsed attributes.
	 * @throws SearchException
	 *             Thrown in case of an error.
	 */
	public static List<Attribute> parseSelectString(List<EntityType> possibleSearchAttrs, String selectString)
			throws IllegalArgumentException {
		String[] selectAttrsString = selectString.split(",");
		List<Attribute> selAttrs = new ArrayList<>();

		for (String selectAttr : selectAttrsString) {
			selAttrs.add(getAttribute(possibleSearchAttrs, selectAttr));
		}

		return selAttrs;
	}

	/**
	 * Parse the given "and token"
	 * 
	 * @param entityType
	 *            The entity type.
	 * @param possibleSearchAttrs
	 *            The possible search attributes
	 * @param filterString
	 *            The filter string to parse.
	 * @param filterBuilder
	 *            The SearchFilterBuilder.
	 * @return The shortened filter string. (The parsed part is cut off)
	 * @throws SearchException
	 *             Thrown if
	 */
	private static String parseAndToken(List<EntityType> possibleSearchAttrs, String filterString,
			SearchFilterBuilder filterBuilder) throws IllegalArgumentException {
		Condition condition = parseCondition(possibleSearchAttrs,
				filterString.substring(0, filterString.indexOf(AND_DELIMITER)));
		filterBuilder.addCondition(condition);
		filterBuilder.addAnd();
		return filterString.substring(filterString.indexOf(AND_DELIMITER) + AND_DELIMITER.length());
	}

	/**
	 * Parse the given "or token"
	 * 
	 * @param entityType
	 *            The entity type.
	 * @param possibleSearchAttrs
	 *            The possible search attributes
	 * @param filterString
	 *            The filter string to parse.
	 * @param filterBuilder
	 *            The SearchFilterBuilder.
	 * @return The shortened filter string. (The parsed part is cut off)
	 * @throws SearchException
	 *             Thrown if
	 */
	private static String parseOrToken(List<EntityType> possibleSearchAttrs, String filterString,
			SearchFilterBuilder filterBuilder) throws IllegalArgumentException {
		Condition condition = parseCondition(possibleSearchAttrs,
				filterString.substring(0, filterString.indexOf(OR_DELIMITER)));
		filterBuilder.addCondition(condition);
		filterBuilder.addOr();
		return filterString.substring(filterString.indexOf(OR_DELIMITER) + OR_DELIMITER.length());
	}

	/**
	 * Parses the condition string.
	 * 
	 * @param entityType
	 *            The entity type.
	 * @param possibleSearchAttrs
	 *            The possible search attributes
	 * @param condition
	 *            The condition.
	 * @return The Condition object created from the condition string.
	 * @throws SearchException
	 *             Thrown in case of an error.
	 */
	private static Condition parseCondition(List<EntityType> possibleSearchAttrs, String condition)
			throws IllegalArgumentException {
		StringTokenizer tokenizer = new StringTokenizer(condition, " ");
		if (tokenizer.countTokens() < 3) {
			throw new IllegalArgumentException("Unable to parse condition String: " + condition);
		}
		String attributeIdentifier = tokenizer.nextToken();
		Attribute attribute = getAttribute(possibleSearchAttrs, attributeIdentifier);
		String operationString = tokenizer.nextToken();
		ComparisonOperator comparisonOperator = stringToComparisonOperator(operationString);
		condition = condition.substring(condition.indexOf(attributeIdentifier) + attributeIdentifier.length());
		String value = condition.substring(condition.indexOf(operationString) + operationString.length());
		return comparisonOperator.create(attribute, createConditionValue(attribute.getValueType(), value.trim()));
	}

	/**
	 * Returns the attribute with the given identifier.
	 * 
	 * @param entityType
	 *            The entity type.
	 * @param possibleSearchAttrs
	 *            The possible search attributes.
	 * @param attributeIdentifier
	 *            The identifier of the attribute to return.
	 * @return The attribute.
	 * @throws SearchException
	 *             Thrown if the attribute is not found.
	 */
	private static Attribute getAttribute(List<EntityType> possibleSearchAttrs, String attributeIdentifier)
			throws IllegalArgumentException {

		String[] attrDefinition = attributeIdentifier.split("\\.");
		String entityName = attrDefinition[0];
		String attrName = attrDefinition[1];

		return validateAttribute(entityName, attrName, possibleSearchAttrs);

	}

	private static Attribute validateAttribute(String entityName, String attrName, List<EntityType> possibleSearchAttrs)
			throws IllegalArgumentException {
		for (EntityType entityType : possibleSearchAttrs) {
			if (ServiceUtils.workaroundForTypeMapping(entityType).equals(entityName)) {
				List<Attribute> attributes = entityType.getAttributes();
				for (Attribute attribute : attributes) {
					if (attribute.getName().equals(attrName)) {
						return attribute;
					}
				}
			}
		}
		throw new IllegalArgumentException(
				"attribute with name '" + entityName + "." + attrName + "' is not supported by this query");
	}

	/**
	 * Creates the value for the condition from the value given as string.
	 * 
	 * @param valueType
	 *            The type that the value should have.
	 * @param valueAsString
	 *            The value as string.
	 * @return The created value for the condition.
	 * @throws IllegalArgumentException
	 *             Thrown if the value type is not supported
	 */
	private static Object createConditionValue(ValueType<?> valueType, String valueAsString) {
		Object ret = null;
		if (ValueType.BOOLEAN.equals(valueType)) {
			ret = Boolean.valueOf(valueAsString);
		} else if (ValueType.LONG.equals(valueType)) {
			ret = Long.valueOf(valueAsString);
		} else if (ValueType.STRING.equals(valueType)) {
			ret = valueAsString;
		} else if (ValueType.BYTE.equals(valueType)) {
			ret = Byte.valueOf(valueAsString);
		} else if (ValueType.DOUBLE.equals(valueType)) {
			ret = Double.valueOf(valueAsString);
		} else if (ValueType.FLOAT.equals(valueType)) {
			ret = Float.valueOf(valueAsString);
		} else if (ValueType.INTEGER.equals(valueType)) {
			ret = Integer.valueOf(valueAsString);
		} else if (ValueType.SHORT.equals(valueType)) {
			ret = Short.valueOf(valueAsString);
		} else if (ValueType.DATE.equals(valueType)) {
			try {
				ret = LocalDateTime.parse(valueAsString);
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException(
						"Unsupported value for date: '" + valueAsString + "'. Expected format: '2007-12-03T10:15:30'");
			}
		} else {
			throw new IllegalArgumentException("Unsupported value type: " + valueType.toString());
		}
		return ret;
	}

	/**
	 * Returns a operation object for the given string.
	 * 
	 * @param operationString
	 *            The string that represents the operation.
	 * @return The operation object.
	 * @throws IllegalArgumentException
	 *             Thrown if there is no operation available for the given
	 *             operation string.
	 */
	private static ComparisonOperator stringToComparisonOperator(String operationString) {
		ComparisonOperator comparisonOperator;

		if (LESS_THAN_OPERATOR.equals(operationString)) {
			comparisonOperator = ComparisonOperator.CASE_INSENSITIVE_LESS_THAN;
		} else if (GREATER_THAN_OPERATOR.equals(operationString)) {
			comparisonOperator = ComparisonOperator.CASE_INSENSITIVE_GREATER_THAN;
		} else if (EQUALS_OPERATOR.equals(operationString)) {
			comparisonOperator = ComparisonOperator.CASE_INSENSITIVE_EQUAL;
		} else if (LIKE_OPERATOR.equals(operationString)) {
			comparisonOperator = ComparisonOperator.CASE_INSENSITIVE_LIKE;
		} else {
			throw new IllegalArgumentException("Unsupported operation: " + operationString);
		}
		return comparisonOperator;
	}
}
