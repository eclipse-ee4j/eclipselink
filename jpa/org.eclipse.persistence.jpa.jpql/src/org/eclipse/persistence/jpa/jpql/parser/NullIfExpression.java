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

/**
 * <b>NULLIF</b> returns the first expression if the two expressions are not equal. If the
 * expressions are equal, <b>NULLIF</b> returns a null value of the type of the first expression.
 * <p>
 * <b>NULLIF</b> is equivalent to a searched <b>CASE</b> expression in which the two expressions
 * are equal and the resulting expression is <b>NULL</b>.
 * <p>
 * Returns the same type as the first expression.
 *
 * <div><b>BNF:</b> <code>nullif_expression::= NULLIF(scalar_expression, scalar_expression)</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class NullIfExpression extends AbstractDoubleEncapsulatedExpression {

    /**
     * Creates a new <code>NullIfExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public NullIfExpression(AbstractExpression parent) {
        super(parent, NULLIF);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(NullIfExpressionBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String parameterExpressionBNF(int index) {
        return ScalarExpressionBNF.ID;
    }
}
