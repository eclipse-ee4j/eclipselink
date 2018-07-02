/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

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
 *                          boolean_expression {=|{@literal <>}} {boolean_expression | all_or_any_expression} |
 *                          enum_expression {=|{@literal <>}} {enum_expression | all_or_any_expression} |
 *                          datetime_expression comparison_operator {datetime_expression | all_or_any_expression} |
 *                          entity_expression {=|{@literal <>}} {entity_expression | all_or_any_expression} |
 *                          arithmetic_expression comparison_operator {arithmetic_expression | all_or_any_expression}</code></pre>
 *
 * <b>JPA 2.0 - BNF:</b>
 * <pre><code>comparison_expression ::= string_expression comparison_operator {string_expression | all_or_any_expression} |
 *                          boolean_expression {=|{@literal <>}} {boolean_expression | all_or_any_expression} |
 *                          enum_expression {=|{@literal <>}} {enum_expression | all_or_any_expression} |
 *                          datetime_expression comparison_operator {datetime_expression | all_or_any_expression} |
 *                          entity_expression {=|{@literal <>}} {entity_expression | all_or_any_expression} |
 *                          arithmetic_expression comparison_operator {arithmetic_expression | all_or_any_expression} |
 *                          <b>entity_type_expression {=|{@literal <>}} entity_type_expression}</b></code></pre>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class ComparisonExpression extends CompoundExpression {

    /**
     * Creates a new <code>ComparisonExpression</code>.
     *
     * @param parent The parent of this expression
     * @param identifier The comparator identifier
     */
    public ComparisonExpression(AbstractExpression parent, String identifier) {
        super(parent, identifier);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns the comparison operator, which is either {@literal '=', '>', '>=', '<', '<=' or '<>'}.
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
    public String getLeftExpressionQueryBNFId() {
        return ComparisonExpressionBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(ComparisonExpressionBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRightExpressionQueryBNFId() {
        return ComparisonExpressionBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

        // TODO: This should be handled differently since it has the knowledge of parent expression
        return wordParser.character() == RIGHT_PARENTHESIS ||

               // This happens when parsing a logical expression
               word.equalsIgnoreCase(OR)  ||
               word.equalsIgnoreCase(AND) ||

               // This happens when parsing a conditional expression in a CASE expression
               word.equalsIgnoreCase(WHEN) ||
               word.equalsIgnoreCase(THEN) ||
               word.equalsIgnoreCase(ELSE) ||
               word.equalsIgnoreCase(END)  ||

               // This happens when parsing a conditional expression in a join condition
               word.equalsIgnoreCase(LEFT)  ||
               word.equalsIgnoreCase(OUTER) ||
               word.equalsIgnoreCase(INNER) ||
               word.equalsIgnoreCase(JOIN)  ||

               super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String parseIdentifier(WordParser wordParser) {
        return getText();
    }
}
