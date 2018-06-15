/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticTermBNF;

/**
 * This expression represents an arithmetic expression, which means the first and second expressions
 * are aggregated with an arithmetic sign.
 *
 * <div><b>BNF:</b> <code>arithmetic_expression ::= arithmetic_expression &lt;identifier&gt; arithmetic_term</code><p></div>
 *
 * @see AdditionExpressionStateObject
 * @see DivisionExpressionStateObject
 * @see MultiplicationExpressionStateObject
 * @see SubtractionExpressionStateObject
 *
 * @see ArithmeticExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class ArithmeticExpressionStateObject extends CompoundExpressionStateObject {

    /**
     * Creates a new <code>ArithmeticExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    protected ArithmeticExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>ArithmeticExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param leftStateObject The {@link StateObject} representing the left expression
     * @param rightStateObject The {@link StateObject} representing the right expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    protected ArithmeticExpressionStateObject(StateObject parent,
                                              StateObject leftStateObject,
                                              StateObject rightStateObject) {

        super(parent, leftStateObject, rightStateObject);
    }

    /**
     * Creates a new <code>ArithmeticExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param leftJpqlFragment The string representation of the left expression to parse and to
     * convert into a {@link StateObject}
     * @param rightJpqlFragment The string representation of the right expression to parse and to
     * convert into a {@link StateObject}
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    protected ArithmeticExpressionStateObject(StateObject parent,
                                              String leftJpqlFragment,
                                              String rightJpqlFragment) {

        super(parent, leftJpqlFragment, rightJpqlFragment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArithmeticExpression getExpression() {
        return (ArithmeticExpression) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLeftQueryBNFId() {
        return ArithmeticExpressionBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRightQueryBNFId() {
        return ArithmeticTermBNF.ID;
    }
}
