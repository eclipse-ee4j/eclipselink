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
 * One of the four binary operators. An addition is a mathematical operation representing the
 * combination of two operands together.
 *
 * <div><b>BNF:</b> <code>arithmetic_expression ::= arithmetic_expression + arithmetic_term</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class AdditionExpression extends ArithmeticExpression {

    /**
     * Creates a new <code>AdditionExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public AdditionExpression(AbstractExpression parent) {
        super(parent, PLUS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
