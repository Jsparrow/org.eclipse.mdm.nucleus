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

package org.eclipse.mdm.search.utils;

import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.Condition;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.Filter;
import org.eclipse.mdm.api.base.query.Operation;
import org.eclipse.mdm.search.SearchException;

/**
 * Class for parsing the filter strings.
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class FilterParser {

	public static final String AND_DELIMITER = " and ";

	public static final String OR_DELIMITER = " or ";

	private static final String EQUALS_OPERATOR = "eq";
	private static final String LESS_THAN_OPERATOR = "lt";
	private static final String GREATER_THAN_OPERATOR = "gt";

	/**
	 * Parse the given filter string.
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
	public static Filter parse(List<EntityType> possibleSearchAttrs, String filterString)
			throws SearchException {
		FilterBuilder filterBuilder = new FilterBuilder();
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
	 * Parse the given "and token"
	 * 
	 * @param entityType
	 *            The entity type.
	 * @param possibleSearchAttrs
	 *            The possible search attributes
	 * @param filterString
	 *            The filter string to parse.
	 * @param filterBuilder
	 *            The FilterBuilder.
	 * @return The shortened filter string. (The parsed part is cut off)
	 * @throws SearchException
	 *             Thrown if
	 */
	private static String parseAndToken(List<EntityType> possibleSearchAttrs,
			String filterString, FilterBuilder filterBuilder) throws SearchException {
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
	 *            The FilterBuilder.
	 * @return The shortened filter string. (The parsed part is cut off)
	 * @throws SearchException
	 *             Thrown if
	 */
	private static String parseOrToken(List<EntityType> possibleSearchAttrs, String filterString,
			FilterBuilder filterBuilder) throws SearchException {
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
	private static Condition parseCondition(List<EntityType> possibleSearchAttrs,
			String condition) throws SearchException {
		StringTokenizer tokenizer = new StringTokenizer(condition, " ");
		if (tokenizer.countTokens() != 3) {
			throw new SearchException("Unable to parse condition String: " + condition);
		}
		String attributeIdentifier = tokenizer.nextToken();
		Attribute attribute = getAttribute(possibleSearchAttrs, attributeIdentifier);	
		
		Operation operation = stringToOperation(tokenizer.nextToken());
		String conditionToken = tokenizer.nextToken();		
		return operation.create(attribute, conditionToken);
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
	private static Attribute getAttribute(List<EntityType> possibleSearchAttrs,
			String attributeIdentifier) throws SearchException {
		
		String[] attrDefinition = attributeIdentifier.split("\\.");
		String entityName = attrDefinition[0];
		String attrName = attrDefinition[1];
		
		return validateAttribute(entityName, attrName, possibleSearchAttrs);		
		
	}

	
	private static Attribute validateAttribute(String entityName, String attrName, List<EntityType> possibleSearchAttrs) 
		throws SearchException {
		for(EntityType entityType : possibleSearchAttrs) {
			if(entityType.getName().equals(entityName)) {
				List<Attribute> attributes = entityType.getAttributes();
				for(Attribute attribute : attributes) {
					if(attribute.getName().equals(attrName)) {
						return attribute;
					}
				}
			}
		}
		throw new SearchException("attribute with name '" + entityName + "." 
			+ attrName + "' is not supported by this query");
	}
	
	/**
	 * Returns a operation object for the given string.
	 * 
	 * @param operationString
	 *            The string that represents the operation.
	 * @return The operation object.
	 * @throws SearchException
	 *             Thrown if there is no operation available for the given
	 *             operation string.
	 */
	private static Operation stringToOperation(String operationString) throws SearchException {
		Operation operation;
		switch (operationString) {
		case LESS_THAN_OPERATOR:
			operation = Operation.CASE_INSENSITIVE_LESS_THAN;
			break;
		case GREATER_THAN_OPERATOR:
			operation = Operation.CASE_INSENSITIVE_GREATER_THAN;
			break;
		case EQUALS_OPERATOR:
			operation = Operation.CASE_INSENSITIVE_LIKE;
			break;
		default:
			throw new SearchException("Unsupported operation: " + operationString);
		}
		return operation;
	}

}
