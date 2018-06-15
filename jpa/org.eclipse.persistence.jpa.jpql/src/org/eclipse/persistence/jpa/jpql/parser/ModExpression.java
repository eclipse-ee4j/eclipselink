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
 * The modulo operation finds the remainder of division of one number by another.
 * <p>
 * It takes two integer arguments and returns an integer.
 * <p>
 * JPA 1.0, 2.0:
 * <div><b>BNF:</b> <code>expression ::= MOD(simple_arithmetic_expression, simple_arithmetic_expression)</code></div>
 * <p>
 * JPA 2.1:
 * <div><b>BNF:</b> <code>expression ::= MOD(arithmetic_expression, arithmetic_expression)</code></div>
 * <p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class ModExpression extends AbstractDoubleEncapsulatedExpression {

    /**
     * Creates a new <code>ModExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public ModExpression(AbstractExpression parent) {
        super(parent, MOD);
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
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionsReturningNumericsBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String parameterExpressionBNF(int index) {
        return InternalModExpressionBNF.ID;
    }
}
