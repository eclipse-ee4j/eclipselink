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
 * One of the four binary operators. A multiplication is a mathematical operation of scaling one
 * operand with another.
 *
 * <div><b>BNF:</b> <code>arithmetic_expression ::= arithmetic_expression * arithmetic_term</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class MultiplicationExpression extends ArithmeticExpression {

    /**
     * Creates a new <code>MultiplicationExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public MultiplicationExpression(AbstractExpression parent) {
        super(parent, MULTIPLICATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

        char character = word.charAt(0);

        if (character == '+' ||
            character == '-' ||
            character == '/' ||
            character == '*') {

            return (expression != null);
        }

        return super.isParsingComplete(wordParser, word, expression);
    }
}
