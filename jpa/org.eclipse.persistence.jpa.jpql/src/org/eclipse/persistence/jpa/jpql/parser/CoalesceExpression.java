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
 * A <b>COALESCE</b> expression returns <code>null</code> if all its arguments evaluate to
 * <code>null</code>, and the value of the first non-<code>null</code> argument otherwise.
 * <p>
 * The return type is the type returned by the arguments if they are all of the same type, otherwise
 * it is undetermined.
 *
 * <div><b>BNF:</b> <code>coalesce_expression::= COALESCE(scalar_expression {, scalar_expression}+)</code></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class CoalesceExpression extends AbstractSingleEncapsulatedExpression {

    /**
     * Creates a new <code>CoalesceExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public CoalesceExpression(AbstractExpression parent) {
        super(parent, COALESCE);
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
    public String getEncapsulatedExpressionQueryBNFId() {
        return InternalCoalesceExpressionBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(CoalesceExpressionBNF.ID);
    }
}
