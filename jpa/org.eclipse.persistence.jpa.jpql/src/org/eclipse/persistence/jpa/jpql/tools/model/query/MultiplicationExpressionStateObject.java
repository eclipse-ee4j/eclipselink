/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.MultiplicationExpression;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * TODO:
 *
 * <div><b>BNF:</b> <code>arithmetic_expression ::= arithmetic_expression * arithmetic_term</code><p></div>
 *
 * @see MultiplicationExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class MultiplicationExpressionStateObject extends ArithmeticExpressionStateObject {

    /**
     * Creates a new <code>MultiplicationExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public MultiplicationExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>MultiplicationExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param leftStateObject The {@link StateObject} representing the left expression
     * @param rightStateObject The {@link StateObject} representing the right expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public MultiplicationExpressionStateObject(StateObject parent,
                                               StateObject leftStateObject,
                                               StateObject rightStateObject) {

        super(parent, leftStateObject, rightStateObject);
    }

    /**
     * Creates a new <code>MultiplicationExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param leftJpqlFragment The string representation of the left expression to parse and to
     * convert into a {@link StateObject}
     * @param rightJpqlFragment The string representation of the right expression to parse and to
     * convert into a {@link StateObject}
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public MultiplicationExpressionStateObject(StateObject parent,
                                               String leftJpqlFragment,
                                               String rightJpqlFragment) {

        super(parent, leftJpqlFragment, rightJpqlFragment);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(StateObjectVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiplicationExpression getExpression() {
        return (MultiplicationExpression) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return MULTIPLICATION;
    }

    /**
     * Keeps a reference of the {@link MultiplicationExpression parsed object} object, which should
     * only be done when this object is instantiated during the conversion of a parsed JPQL query
     * into {@link StateObject StateObjects}.
     *
     * @param expression The {@link MultiplicationExpression parsed object} representing a
     * subtraction expression
     */
    public void setExpression(MultiplicationExpression expression) {
        super.setExpression(expression);
    }
}
