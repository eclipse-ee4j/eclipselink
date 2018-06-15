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

/**
 * One of the four binary operators. A subtraction is the inverse of addition, it is a mathematical
 * operation representing the removal of the second operand from the first operand.
 *
 * <div><b>BNF:</b> <code>arithmetic_expression ::= arithmetic_expression - arithmetic_term</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class SubtractionExpression extends ArithmeticExpression {

    /**
     * Creates a new <code>SubtractionExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public SubtractionExpression(AbstractExpression parent) {
        super(parent, MINUS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
