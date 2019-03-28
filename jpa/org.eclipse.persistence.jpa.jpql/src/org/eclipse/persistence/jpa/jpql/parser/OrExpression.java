/*
 * Copyright (c) 2006, 2019 Oracle and/or its affiliates. All rights reserved.
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
 * The <b>OR</b> logical operator chains multiple criteria together. A valid operand of an <b>OR</b>
 * operator must be one of: <b>TRUE</b>, <b>FALSE</b>, and <b>NULL</b>. The <b>OR</b> operator has
 * a lower precedence than the <b>AND</b> operator.
 * <p>
 * <b>NULL</b> represents unknown. Therefore, if one operand is <b>NULL</b> and the other operand is
 * <b>TRUE</b> the result is <b>TRUE</b>, because one <b>TRUE</b> operand is sufficient for a
 * <b>TRUE</b> result. If one operand is <b>NULL</b> and the other operand is either <b>FALSE</b> or
 * <b>NULL</b>, the result is <b>NULL</b> (unknown).
 * <p>
 *
 * <table border="1" style="border:1px outset darkgrey;">
 * <caption>The following table shows how the <code><b>OR</b></code> operator is evaluated based on its two operands:</caption>
 * <tr><td></td><td><b>TRUE</b></td><td><b>FALSE</b></td><td><b>NULL</b></td></tr>
 * <tr><td><b>TRUE</b></td><td>TRUE</td><td>TRUE</td><td>TRUE</td></tr>
 * <tr><td><b>FALSE</b></td><td>TRUE</td><td>FALSE</td><td>NULL</td></tr>
 * <tr><td><b>NULL</b></td><td>TRUE</td><td>NULL</td><td>NULL</td></tr>
 * </table>
 *
 * <div><b>BNF:</b> <code>conditional_expression ::= conditional_expression OR conditional_term</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class OrExpression extends LogicalExpression {

    /**
     * Creates a new <code>OrExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public OrExpression(AbstractExpression parent) {
        super(parent, OR);
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
    public String getLeftExpressionQueryBNFId() {
        return ConditionalExpressionBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRightExpressionQueryBNFId() {
        return ConditionalTermBNF.ID;
    }
}
