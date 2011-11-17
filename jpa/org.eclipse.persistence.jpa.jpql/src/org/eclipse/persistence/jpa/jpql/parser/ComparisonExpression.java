/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * Only the values of like types are permitted to be compared. A type is like another type if they
 * correspond to the same Java language type, or if one is a primitive Java language type and the
 * other is the wrapped Java class type equivalent (e.g., int and Integer are like types in this
 * sense).
 * <p>
 * There is one exception to this rule: it is valid to compare numeric values for which the rules of
 * numeric promotion apply. Conditional expressions attempting to compare non-like type values are
 * disallowed except for this numeric case.
 * <p>
 * Note that the arithmetic operators and comparison operators are permitted to be applied to
 * state-fields and input parameters of the wrapped Java class equivalents to the primitive numeric
 * Java types. Two entities of the same abstract schema type are equal if and only if they have the
 * same primary key value. Only equality/inequality comparisons over enumeration constants are
 * required to be supported.
 * <p>
 * <b>JPA 1.0 - BNF:</b>
 * <pre><code>comparison_expression ::= string_expression comparison_operator {string_expression | all_or_any_expression} |
 *                          boolean_expression {=|<>} {boolean_expression | all_or_any_expression} |
 *                          enum_expression {=|<>} {enum_expression | all_or_any_expression} |
 *                          datetime_expression comparison_operator {datetime_expression | all_or_any_expression} |
 *                          entity_expression {=|<>} {entity_expression | all_or_any_expression} |
 *                          arithmetic_expression comparison_operator {arithmetic_expression | all_or_any_expression}</code></pre>
 *
 * <b>JPA 2.0 - BNF:</b>
 * <pre><code>comparison_expression ::= string_expression comparison_operator {string_expression | all_or_any_expression} |
 *                          boolean_expression {=|<>} {boolean_expression | all_or_any_expression} |
 *                          enum_expression {=|<>} {enum_expression | all_or_any_expression} |
 *                          datetime_expression comparison_operator {datetime_expression | all_or_any_expression} |
 *                          entity_expression {=|<>} {entity_expression | all_or_any_expression} |
 *                          arithmetic_expression comparison_operator {arithmetic_expression | all_or_any_expression} |
 *                          <b>entity_type_expression {=|<>} entity_type_expression}</b></code></pre>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class ComparisonExpression extends CompoundExpression {

	/**
	 * Creates a new <code>ComparisonExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public ComparisonExpression(AbstractExpression parent) {
		super(parent, ExpressionTools.EMPTY_STRING);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Returns the comparison operator, which is either '=', '>', '>=', '<', '<=' or '<>'.
	 *
	 * @return The operator comparing the two expressions
	 */
	public String getComparisonOperator() {
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(ComparisonExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return wordParser.character() == RIGHT_PARENTHESIS ||
		       word.equalsIgnoreCase(OR)                   ||
		       word.equalsIgnoreCase(AND)                  ||
		       word.equalsIgnoreCase(WHEN)                 ||
		       word.equalsIgnoreCase(THEN)                 ||
		       word.equalsIgnoreCase(ELSE)                 ||
		       word.equalsIgnoreCase(END)                  ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String parseIdentifier(WordParser wordParser) {
		switch (wordParser.character()) {
			case '<': {
				switch (wordParser.character(wordParser.position() + 1)) {
					case '=': return LOWER_THAN_OR_EQUAL;
					case '>': return DIFFERENT;
					default:  return LOWER_THAN;
				}
			}

			case '>': {
				switch (wordParser.character(wordParser.position() + 1)) {
					case '=': return GREATER_THAN_OR_EQUAL;
					default:  return GREATER_THAN;
				}
			}

			// =
			default: {
				return EQUAL;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF rightExpressionBNF() {
		return getQueryBNF();
	}
}