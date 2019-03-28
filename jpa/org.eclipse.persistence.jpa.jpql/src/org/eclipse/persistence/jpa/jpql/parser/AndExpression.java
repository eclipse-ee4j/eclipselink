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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The <b>AND</b> logical operator chains multiple criteria together. A valid operand of an
 * <b>AND</b> operator must be one of: <b>TRUE</b>, <b>FALSE</b>, and <b>NULL</b>. The
 * <b>AND</b> operator has a higher precedence than the <b>OR</b> operator.
 * <p>
 * <b>NULL</b> represents unknown. Therefore, if one operand is <b>NULL</b> and the other operand
 * is <b>FALSE</b> the result is <b>FALSE</b>, because one <b>FALSE</b> operand is sufficient for
 * a <b>FALSE</b> result. If one operand is <b>NULL</b> and the other operand is either
 * <b>TRUE</b> or <b>NULL</b>, the result is <b>NULL</b> (unknown).
 * <p>
 *
 * <table border="1" style="border:1px outset darkgrey;">
 * <caption>The following table shows how the <code><b>AND</b></code> operator is evaluated based on its two operands:</caption>
 * <tr><td></td>            <td><b>TRUE</b></td><td><b>FALSE</b></td><td><b>NULL</b></td></tr>
 * <tr><td><b>TRUE</b> </td><td>   TRUE    </td><td>   FALSE    </td><td>   NULL    </td></tr>
 * <tr><td><b>FALSE</b></td><td>   FALSE   </td><td>   FALSE    </td><td>   FALSE   </td></tr>
 * <tr><td><b>NULL</b> </td><td>   NULL    </td><td>   FALSE    </td><td>   NULL    </td></tr>
 * </table>
 *
 * <div><b>BNF:</b> <code>conditional_term ::= conditional_term AND conditional_factor</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class AndExpression extends LogicalExpression {

    /**
     * Creates a new <code>AndExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public AndExpression(AbstractExpression parent) {
        super(parent, AND);
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
        return ConditionalTermBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRightExpressionQueryBNFId() {
        return ConditionalFactorBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
        return word.equalsIgnoreCase(AND) ||
               word.equalsIgnoreCase(OR)  ||
               super.isParsingComplete(wordParser, word, expression);
    }
}
