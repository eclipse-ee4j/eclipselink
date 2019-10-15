/*
 * Copyright (c) 2011, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.AND;

import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConditionalFactorBNF;
import org.eclipse.persistence.jpa.jpql.parser.ConditionalTermBNF;

/**
 * The <code><b>AND</b></code> logical operator chains multiple criteria together. A valid operand
 * of an <code><b>AND</b></code> operator must be one of: <code><b>TRUE</b></code>,
 * <code><b>FALSE</b></code>, and <code><b>NULL</b></code>. The <code><b>AND</b></code> operator has
 * a higher precedence than the <code><b>OR</b></code> operator.
 * <p>
 * <code><b>NULL</b></code> represents unknown. Therefore, if one operand is <code><b>NULL</b></code>
 * and the other operand is <code><b>FALSE</b></code> the result is <code><b>FALSE</b></code>,
 * because one <code><b>FALSE</b></code> operand is sufficient for a <code><b>FALSE</b></code>
 * result. If one operand is <code><b>NULL</b></code> and the other operand is either
 * <code><b>TRUE</b></code> or <code><b>NULL</b></code>, the result is <code><b>NULL</b></code>
 * (unknown).
 * <p>
 *
 * <table border="1" style="border:1px outset darkgrey;">
 * <caption>The following table shows how the <code><b>AND</b></code> operator is evaluated based on its two operands:</caption>
 * <tr><td>            </td><td><b>TRUE</b></td><td><b>FALSE</b></td><td><b>NULL</b></td></tr>
 * <tr><td><b>TRUE</b> </td><td>   TRUE    </td><td>   FALSE    </td><td>   NULL    </td></tr>
 * <tr><td><b>FALSE</b></td><td>   FALSE   </td><td>   FALSE    </td><td>   FALSE   </td></tr>
 * <tr><td><b>NULL</b> </td><td>   NULL    </td><td>   FALSE    </td><td>   NULL    </td></tr>
 * </table>
 *
 * <div><b>BNF:</b> <code>conditional_term ::= conditional_term AND conditional_factor</code><p></div>
 *
 * @see AndExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class AndExpressionStateObject extends LogicalExpressionStateObject {

    /**
     * Creates a new <code>AndExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public AndExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>AndExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param leftStateObject The {@link StateObject} representing the left expression
     * @param rightStateObject The {@link StateObject} representing the right expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public AndExpressionStateObject(StateObject parent,
                                    StateObject leftStateObject,
                                    StateObject rightStateObject) {

        super(parent, leftStateObject, rightStateObject);
    }

    /**
     * Creates a new <code>AndExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param leftJpqlFragment The string representation of the left expression to parse and to
     * convert into a {@link StateObject}
     * @param rightJpqlFragment The string representation of the right expression to parse and to
     * convert into a {@link StateObject}
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public AndExpressionStateObject(StateObject parent,
                                    String leftJpqlFragment,
                                    String rightJpqlFragment) {

        super(parent, leftJpqlFragment, rightJpqlFragment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(StateObjectVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AndExpression getExpression() {
        return (AndExpression) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return AND;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLeftQueryBNFId() {
        return ConditionalTermBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRightQueryBNFId() {
        return ConditionalFactorBNF.ID;
    }

    /**
     * Keeps a reference of the {@link AndExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link AndExpression parsed object} representing a logical
     * <code><b>AND</b></code> expression
     */
    public void setExpression(AndExpression expression) {
        super.setExpression(expression);
    }
}
