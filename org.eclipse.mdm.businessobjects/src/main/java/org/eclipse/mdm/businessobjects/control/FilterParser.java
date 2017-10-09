/*******************************************************************************
  * Copyright (c) 2017 Peak Solution GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *******************************************************************************/

package org.eclipse.mdm.businessobjects.control;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.mdm.api.base.model.ValueType;
import org.eclipse.mdm.api.base.query.Attribute;
import org.eclipse.mdm.api.base.query.ComparisonOperator;
import org.eclipse.mdm.api.base.query.EntityType;
import org.eclipse.mdm.api.base.query.Filter;
import org.eclipse.mdm.businessobjects.filter.FilterGrammarBaseVisitor;
import org.eclipse.mdm.businessobjects.filter.FilterGrammarLexer;
import org.eclipse.mdm.businessobjects.filter.FilterGrammarParser;
import org.eclipse.mdm.businessobjects.filter.FilterGrammarParser.AndExpressionContext;
import org.eclipse.mdm.businessobjects.filter.FilterGrammarParser.AttributeContext;
import org.eclipse.mdm.businessobjects.filter.FilterGrammarParser.ComparatorExpressionContext;
import org.eclipse.mdm.businessobjects.filter.FilterGrammarParser.ListComparatorExpressionContext;
import org.eclipse.mdm.businessobjects.filter.FilterGrammarParser.NotExpressionContext;
import org.eclipse.mdm.businessobjects.filter.FilterGrammarParser.OrExpressionContext;
import org.eclipse.mdm.businessobjects.filter.FilterGrammarParser.UnaryComparatorExpressionContext;
import org.eclipse.mdm.businessobjects.filter.FilterGrammarParser.ValueContext;
import org.eclipse.mdm.businessobjects.filter.FilterGrammarParser.ValuesContext;
import org.eclipse.mdm.businessobjects.utils.ServiceUtils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;

/**
 * Class for parsing filter strings.
 * 
 * @author Matthias Koller
 *
 */
public class FilterParser {
	/**
	 * Visitor class to convert the parsed tree into a {@link Filter}.
	 */
	private static final class FilterVisitor extends FilterGrammarBaseVisitor<Filter> {
		private List<EntityType> availableEntityTypes;

		/**
		 * Constructs a new Visitor operating on the given search attributes.
		 * 
		 * @param availableEntityTypes
		 *            List of {@link EntityType}s to match the parsed attributes
		 *            against.
		 */
		private FilterVisitor(List<EntityType> availableEntityTypes) {
			this.availableEntityTypes = availableEntityTypes;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.mdm.businessobjects.filter.FilterGrammarBaseVisitor#
		 * visitAndExpression(org.eclipse.mdm.businessobjects.filter.FilterGrammarParser
		 * .AndExpressionContext)
		 */
		@Override
		public Filter visitAndExpression(AndExpressionContext ctx) {
			return Filter.and().merge(visit(ctx.left), visit(ctx.right));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.mdm.businessobjects.filter.FilterGrammarBaseVisitor#
		 * visitOrExpression(org.eclipse.mdm.businessobjects.filter.FilterGrammarParser.
		 * OrExpressionContext)
		 */
		@Override
		public Filter visitOrExpression(OrExpressionContext ctx) {
			return Filter.or().merge(visit(ctx.left), visit(ctx.right));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.mdm.businessobjects.filter.FilterGrammarBaseVisitor#
		 * visitNotExpression(org.eclipse.mdm.businessobjects.filter.FilterGrammarParser
		 * .NotExpressionContext)
		 */
		@Override
		public Filter visitNotExpression(NotExpressionContext ctx) {
			return super.visitNotExpression(ctx).invert();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.mdm.businessobjects.filter.FilterGrammarBaseVisitor#
		 * visitComparatorExpression(org.eclipse.mdm.businessobjects.filter.
		 * FilterGrammarParser.ComparatorExpressionContext)
		 */
		@Override
		public Filter visitComparatorExpression(ComparatorExpressionContext ctx) {
			ComparisonOperator operator = getOperator(ctx.op);
			Attribute attribute = getAttribute(ctx.left);
			Object value = createConditionValue(attribute.getValueType(), getValue(ctx.right));

			return Filter.and().add(operator.create(attribute, value));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.mdm.businessobjects.filter.FilterGrammarBaseVisitor#
		 * visitListComparatorExpression(org.eclipse.mdm.businessobjects.filter.
		 * FilterGrammarParser.ListComparatorExpressionContext)
		 */
		@Override
		public Filter visitListComparatorExpression(ListComparatorExpressionContext ctx) {
			ComparisonOperator operator = getOperator(ctx.op);
			Attribute attribute = getAttribute(ctx.left);
			Object value = createConditionValues(attribute.getValueType(), getValues(ctx.right));

			return Filter.and().add(operator.create(attribute, value));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.mdm.businessobjects.filter.FilterGrammarBaseVisitor#
		 * visitUnaryComparatorExpression(org.eclipse.mdm.businessobjects.filter.
		 * FilterGrammarParser.UnaryComparatorExpressionContext)
		 */
		@Override
		public Filter visitUnaryComparatorExpression(UnaryComparatorExpressionContext ctx) {
			ComparisonOperator operator = getOperator(ctx.op);
			Attribute attribute = getAttribute(ctx.left);
			Object value = createConditionValue(attribute.getValueType(), null);

			return Filter.and().add(operator.create(attribute, value));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.antlr.v4.runtime.tree.AbstractParseTreeVisitor#aggregateResult(java.lang.
		 * Object, java.lang.Object)
		 */
		@Override
		protected Filter aggregateResult(Filter aggregate, Filter nextResult) {
			if (nextResult == null) {
				return aggregate;
			}
			return super.aggregateResult(aggregate, nextResult);
		}

		/**
		 * Extract a string from the given {@link ValueContext}. Basically this methods
		 * returns a string representation of the value without enclosing quotes (if
		 * there were any).
		 * 
		 * @param ctx
		 *            {@link ValueContext} containing the parsed value.
		 * @return string representation of the value given by {@link ValueContext}
		 */
		private String getValue(ValueContext ctx) {

			TerminalNode typeNode = (TerminalNode) ctx.getChild(0);
			switch (typeNode.getSymbol().getType()) {
			case FilterGrammarLexer.STRINGLITERAL:
				return CharMatcher.anyOf("'").trimFrom(ctx.STRINGLITERAL().getText()).replaceAll("\\\\'", "'");
			case FilterGrammarLexer.DECIMAL:
			case FilterGrammarLexer.LONG:
			case FilterGrammarLexer.BOOL:
				return ctx.getText();
			default:
				throw new RuntimeException("Unsupported Symbol: " + typeNode.getSymbol().getType());
			}
		}

		/**
		 * Extract a list string from the given {@link ValuesContext}. Basically this
		 * methods returns a list of string representations of the values without
		 * enclosing quotes (if there were any).
		 * 
		 * @param ctx
		 *            {@link ValuesContext} containing the parsed values.
		 * @return string representations of the values given by {@link ValuesContext}
		 */
		private List<String> getValues(ValuesContext ctx) {
			List<String> values = new ArrayList<>();
			for (org.antlr.v4.runtime.tree.ParseTree child : ctx.children) {
				if (child instanceof ValueContext) {
					values.add(getValue((ValueContext) child));
				}
			}
			return values;
		}

		/**
		 * Converts an {@link AttributeContext} into an {@link Attribute} using the list
		 * of available EntityTypes.
		 * 
		 * @param ctx
		 *            parsed entitytype / attribute
		 * @return the matched {@link Attribute} given by {@link AttributeContext}
		 * @throws IllegalArgumentException
		 *             if {@link EntityType} or {@link Attribute} given by
		 *             <code>ctx</code> cannot be found.
		 */
		private Attribute getAttribute(AttributeContext ctx) {
			String[] name = ctx.getText().split("\\.");
			return availableEntityTypes.stream()
					.filter(e -> ServiceUtils.workaroundForTypeMapping(e).equals(name[0]))
					.findAny()
					.orElseThrow(() -> new IllegalArgumentException("Entity " + name[0] + " not found in data source!"))
					.getAttribute(name[1]);
		}

		/**
		 * Converts a {@link ParserRuleContext} containing a {@link TerminalNode} into a
		 * {@link ComparisonOperator}.
		 * 
		 * @param ctx
		 *            {@link UnaryComparatorExpressionContext},
		 *            {@link ComparatorExpressionContext} or
		 *            {@link ListComparatorExpressionContext} or
		 * @return converted {@link ComparisonOperator}
		 * @throws IllegalArgumentException
		 *             if the operator given by <code>ctx</code> is unknown or cannot be
		 *             converted.
		 */
		private ComparisonOperator getOperator(ParserRuleContext ctx) {
			TerminalNode typeNode = (TerminalNode) ctx.getChild(0);
			switch (typeNode.getSymbol().getType()) {
			case FilterGrammarLexer.EQUAL:
				return ComparisonOperator.EQUAL;
			case FilterGrammarLexer.NOT_EQUAL:
				return ComparisonOperator.NOT_EQUAL;
			case FilterGrammarLexer.LESS_THAN:
				return ComparisonOperator.LESS_THAN;
			case FilterGrammarLexer.LESS_THAN_OR_EQUAL:
				return ComparisonOperator.LESS_THAN_OR_EQUAL;
			case FilterGrammarLexer.GREATER_THAN:
				return ComparisonOperator.GREATER_THAN;
			case FilterGrammarLexer.GREATER_THAN_OR_EQUAL:
				return ComparisonOperator.GREATER_THAN_OR_EQUAL;
			case FilterGrammarLexer.IN_SET:
				return ComparisonOperator.IN_SET;
			case FilterGrammarLexer.NOT_IN_SET:
				return ComparisonOperator.NOT_IN_SET;
			case FilterGrammarLexer.LIKE:
				return ComparisonOperator.LIKE;
			case FilterGrammarLexer.NOT_LIKE:
				return ComparisonOperator.NOT_LIKE;
			case FilterGrammarLexer.CASE_INSENSITIVE_EQUAL:
				return ComparisonOperator.CASE_INSENSITIVE_EQUAL;
			case FilterGrammarLexer.CASE_INSENSITIVE_NOT_EQUAL:
				return ComparisonOperator.CASE_INSENSITIVE_NOT_EQUAL;
			case FilterGrammarLexer.CASE_INSENSITIVE_LESS_THAN:
				return ComparisonOperator.CASE_INSENSITIVE_LESS_THAN;
			case FilterGrammarLexer.CASE_INSENSITIVE_LESS_THAN_OR_EQUAL:
				return ComparisonOperator.CASE_INSENSITIVE_LESS_THAN_OR_EQUAL;
			case FilterGrammarLexer.CASE_INSENSITIVE_GREATER_THAN:
				return ComparisonOperator.CASE_INSENSITIVE_GREATER_THAN;
			case FilterGrammarLexer.CASE_INSENSITIVE_GREATER_THAN_OR_EQUAL:
				return ComparisonOperator.CASE_INSENSITIVE_GREATER_THAN_OR_EQUAL;
			case FilterGrammarLexer.CASE_INSENSITIVE_IN_SET:
				return ComparisonOperator.CASE_INSENSITIVE_IN_SET;
			case FilterGrammarLexer.CASE_INSENSITIVE_NOT_IN_SET:
				return ComparisonOperator.CASE_INSENSITIVE_NOT_IN_SET;
			case FilterGrammarLexer.CASE_INSENSITIVE_LIKE:
				return ComparisonOperator.CASE_INSENSITIVE_LIKE;
			case FilterGrammarLexer.CASE_INSENSITIVE_NOT_LIKE:
				return ComparisonOperator.CASE_INSENSITIVE_NOT_LIKE;
			case FilterGrammarLexer.IS_NULL:
				return ComparisonOperator.IS_NULL;
			case FilterGrammarLexer.IS_NOT_NULL:
				return ComparisonOperator.IS_NOT_NULL;
			case FilterGrammarLexer.BETWEEN:
				return ComparisonOperator.BETWEEN;
			default:
				throw new IllegalArgumentException(
						"Operator " + typeNode.getSymbol().getType() + " not supported yet!");
			}
		}
	}

	/**
	 * Class to convert a antlr syntax error into a unchecked
	 * ParserCancellationException.
	 */
	private static class ThrowingErrorListener extends BaseErrorListener {

		public static final ThrowingErrorListener INSTANCE = new ThrowingErrorListener();

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.antlr.v4.runtime.BaseErrorListener#syntaxError(org.antlr.v4.runtime.
		 * Recognizer, java.lang.Object, int, int, java.lang.String,
		 * org.antlr.v4.runtime.RecognitionException)
		 */
		@Override
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
				String msg, RecognitionException e) throws ParseCancellationException {
			throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
		}
	}

	/**
	 * Parses the given filter string. The filter string must conform to the ANTLR
	 * grammer defined in FilterGrammar.g4.
	 * 
	 * @param possibleEntityTypes
	 *            The possible {@link EntityType}s / {@link Attribute}s
	 * @param filterString
	 *            The filter string to parse.
	 * @return the parsed {@link Filter}
	 * @throws IllegalArgumentExceptionThrown
	 *             if parsing fails.
	 */
	public static Filter parseFilterString(List<EntityType> possibleEntityTypes, String filterString)
			throws IllegalArgumentException {

		if (Strings.isNullOrEmpty(filterString)) {
			return Filter.and();
		}

		try {
			FilterGrammarLexer lexer = new FilterGrammarLexer(new ANTLRInputStream(filterString));
			lexer.removeErrorListeners();
			lexer.addErrorListener(ThrowingErrorListener.INSTANCE);

			FilterGrammarParser parser = new FilterGrammarParser(new CommonTokenStream(lexer));
			parser.removeErrorListeners();
			parser.addErrorListener(ThrowingErrorListener.INSTANCE);

			return new FilterVisitor(possibleEntityTypes).visit(parser.parse());
		} catch (ParseCancellationException e) {
			throw new IllegalArgumentException(
					"Could not parse filter string '" + filterString + "'. Error: " + e.getMessage(), e);
		}
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
	 * Creates the values for the condition from the values given in the list of
	 * strings.
	 * 
	 * @param valueType
	 *            The type that the value should have.
	 * @param valuesAsString
	 *            The values as a list of string.
	 * @return The created value for the condition.
	 * @throws IllegalArgumentException
	 *             Thrown if the value type is not supported
	 */
	private static Object createConditionValues(ValueType<?> valueType, List<String> valuesAsStrings) {

		if (ValueType.BOOLEAN.equals(valueType)) {
			List<Boolean> list = new ArrayList<>();
			for (String valueAsString : valuesAsStrings) {
				list.add(Boolean.valueOf(valueAsString));
			}
			return Booleans.toArray(list);
		} else if (ValueType.LONG.equals(valueType)) {
			List<Long> list = new ArrayList<>();
			for (String valueAsString : valuesAsStrings) {
				list.add(Long.valueOf(valueAsString));
			}
			return Longs.toArray(list);
		} else if (ValueType.STRING.equals(valueType)) {
			List<String> list = new ArrayList<>();
			for (String valueAsString : valuesAsStrings) {
				list.add(valueAsString);
			}
			return list.toArray(new String[0]);
		} else if (ValueType.BYTE.equals(valueType)) {
			List<Byte> list = new ArrayList<>();
			for (String valueAsString : valuesAsStrings) {
				list.add(Byte.valueOf(valueAsString));
			}
			return Bytes.toArray(list);
		} else if (ValueType.DOUBLE.equals(valueType)) {
			List<Double> list = new ArrayList<>();
			for (String valueAsString : valuesAsStrings) {
				list.add(Double.valueOf(valueAsString));
			}
			return Doubles.toArray(list);
		} else if (ValueType.FLOAT.equals(valueType)) {
			List<Float> list = new ArrayList<>();
			for (String valueAsString : valuesAsStrings) {
				list.add(Float.valueOf(valueAsString));
			}
			return Floats.toArray(list);
		} else if (ValueType.INTEGER.equals(valueType)) {
			List<Integer> list = new ArrayList<>();
			for (String valueAsString : valuesAsStrings) {
				list.add(Integer.valueOf(valueAsString));
			}
			return Ints.toArray(list);
		} else if (ValueType.SHORT.equals(valueType)) {
			List<Short> list = new ArrayList<>();
			for (String valueAsString : valuesAsStrings) {
				list.add(Short.valueOf(valueAsString));
			}
			return Shorts.toArray(list);
		} else if (ValueType.DATE.equals(valueType)) {
			List<LocalDateTime> list = new ArrayList<>();
			for (String valueAsString : valuesAsStrings) {
				try {
					list.add(LocalDateTime.parse(valueAsString));
				} catch (DateTimeParseException e) {
					throw new IllegalArgumentException("Unsupported value for date: '" + valueAsString
							+ "'. Expected format: '2007-12-03T10:15:30'");
				}
			}
			return list.toArray(new LocalDateTime[0]);
		} else {
			throw new IllegalArgumentException("Unsupported value type: " + valueType.toString());
		}
	}
}
