/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This expression represents an arithmetic expression, which means the first and second expressions
 * are aggregated with an arithmetic sign.
 *
 * @see AdditionExpression
 * @see DivisionExpression
 * @see MultiplicationExpression
 * @see SubtractionExpression
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class ArithmeticExpression extends CompoundExpression {

    /**
     * Creates a new <code>ArithmeticExpression</code>.
     *
     * @param parent The parent of this expression
     * @param identifier The arithmetic sign
     */
    protected ArithmeticExpression(AbstractExpression parent, String identifier) {
        super(parent, identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {
        return getParent().findQueryBNF(expression);
    }

    /**
     * Returns the arithmetic sign this expression is actually representing.
     *
     * @return The single character value of the arithmetic sign
     */
    public final String getArithmeticSign() {
        return getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLeftExpressionQueryBNFId() {
        return ArithmeticExpressionBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(ArithmeticTermBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getRightExpressionQueryBNFId() {
        return ArithmeticTermBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

        char character = word.charAt(0);

        // A comparison symbol needs to continue the parsing
        if (character == '=' || character == '<' || character == '>') {
            return true;
        }

        // Addition/Subtraction will create a chain of operations
        if (character == '+' || character == '-') {
            return false;
        }

        // Multiplication/Division will group the expression together to follow the order of operation
        if (character == '*' || character == '/') {
            return (expression == null);
        }

        return (expression != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final String parseIdentifier(WordParser wordParser) {
        return getText();
    }
}
