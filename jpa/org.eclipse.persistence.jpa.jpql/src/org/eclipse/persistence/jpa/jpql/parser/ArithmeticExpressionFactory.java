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
 * This {@link ExpressionFactory} creates a new expression when the portion of the query to parse
 * starts with an arithmetic identifier. It is possible the expression to parse is also a {@link
 * NumericLiteral} or an {@link ArithmeticFactor}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ArithmeticExpressionFactory extends ExpressionFactory {

    /**
     * This {@link ExpressionVisitor} is used to check if the {@link Expression} passed to this
     * factory is an {@link AdditionExpression} or {@link SubtractionExpression}.
     */
    private ArithmeticExpressionVisitor visitor;

    /**
     * The unique identifier of this {@link ArithmeticExpressionFactory}.
     */
    public static final String ID = "*/-+";

    /**
     * Creates a new <code>AbstractArithmeticExpressionFactory</code>.
     */
    public ArithmeticExpressionFactory() {
        super(ID, Expression.PLUS,
                  Expression.MINUS,
                  Expression.DIVISION,
                  Expression.MULTIPLICATION);
    }

    /**
     * Creates the {@link Expression} this factory for which it is responsible.
     *
     * @param parent The parent of the new {@link Expression}
     * @param character The arithmetic character
     * @return A new {@link CompoundExpression}
     */
    private CompoundExpression buildExpression(AbstractExpression parent, char character) {
        if (character == '*') {
            return new MultiplicationExpression(parent);
        }
        return new DivisionExpression(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final AbstractExpression buildExpression(AbstractExpression parent,
                                                       WordParser wordParser,
                                                       String word,
                                                       JPQLQueryBNF queryBNF,
                                                       AbstractExpression expression,
                                                       boolean tolerant) {

        Boolean type = wordParser.startsWithDigit();

        // Return right away the number literal
        // "1 + 1" can't be parsed as "1, +1"
        if ((type == Boolean.TRUE) && (expression == null)) {
            expression = new NumericLiteral(parent);
            expression.parse(wordParser, tolerant);
            return expression;
        }

        // When the text starts with either '+' or '-' and the expression passed
        // is null, then it means an ArithmeticFactor has to be used.
        // In any other cases, the expression would be for instance an AND
        // expression, a function, etc
        if ((type == Boolean.FALSE) && (expression == null)) {
            expression = new ArithmeticFactor(parent, word);
            expression.parse(wordParser, tolerant);
            return expression;
        }

        char character = word.charAt(0);

        // Subtraction
        if (character == '-') {
            SubtractionExpression substractionExpression = new SubtractionExpression(parent);
            substractionExpression.setLeftExpression(expression);
            substractionExpression.parse(wordParser, tolerant);
            return substractionExpression;
        }

        // Addition
        if (character == '+') {
            AdditionExpression additionExpression = new AdditionExpression(parent);
            additionExpression.setLeftExpression(expression);
            additionExpression.parse(wordParser, tolerant);
            return additionExpression;
        }

        if (expression != null) {

            // Determine whether the Expression that is already parsed
            // is an addition or subtraction expression, if that is the case,
            // then we have to follow the mathematical precedence: * and / takes
            // precedence over + and -
            expression.accept(visitor());

            if (visitor.found) {
                visitor.found = false;

                // [a + b] * [c] will become [a + [b * c]]
                ArithmeticExpression arithmeticException = (ArithmeticExpression) expression;

                CompoundExpression compoundExpression = buildExpression(parent, character);
                compoundExpression.setLeftExpression((AbstractExpression) arithmeticException.getRightExpression());
                compoundExpression.parse(wordParser, tolerant);
                arithmeticException.setRightExpression(compoundExpression);

                return arithmeticException;
            }
        }

        // Create the ArithmeticExpression
        CompoundExpression compoundExpression = buildExpression(parent, character);
        compoundExpression.setLeftExpression(expression);
        compoundExpression.parse(wordParser, tolerant);
        return compoundExpression;
    }

    private ArithmeticExpressionVisitor visitor() {
        if (visitor == null) {
            visitor = new ArithmeticExpressionVisitor();
        }
        return visitor;
    }

    // Made static final for performance reasons.
    /**
     * This {@link ExpressionVisitor} is used to check if the {@link Expression} passed to this
     * factory is an {@link AdditionExpression} or {@link SubtractionExpression}.
     */
    private static final class ArithmeticExpressionVisitor extends AbstractExpressionVisitor {

        /**
         * This flag is turned on if the {@link Expression} visited is {@link OrExpression}.
         */
        boolean found;

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AdditionExpression expression) {
            found = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubtractionExpression expression) {
            found = true;
        }
    }
}
