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

import org.eclipse.persistence.jpa.jpql.parser.SimpleArithmeticExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SqrtExpression;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>SQRT</b></code> function takes a numeric argument and returns a <code>Double</code>.
 *
 * <div><b>BNF:</b> <code>expression ::= SQRT(simple_arithmetic_expression)</code><p></div>
 *
 * @see SqrtExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class SqrtExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

    /**
     * Creates a new <code>SqrtExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public SqrtExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>SqrtExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param stateObject The {@link StateObject} representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public SqrtExpressionStateObject(StateObject parent, StateObject stateObject) {
        super(parent, stateObject);
    }

    /**
     * Creates a new <code>SqrtExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param jpqlFragment The portion of the query representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public SqrtExpressionStateObject(StateObject parent, String jpqlFragment) {
        super(parent, jpqlFragment);
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
    public SqrtExpression getExpression() {
        return (SqrtExpression) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return SQRT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getQueryBNFId() {
        return SimpleArithmeticExpressionBNF.ID;
    }

    /**
     * Keeps a reference of the {@link SqrtExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link SqrtExpression parsed object} representing a <code><b>SQRT</b></code>
     * expression
     */
    public void setExpression(SqrtExpression expression) {
        super.setExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStateObject(StateObject stateObject) {
        super.setStateObject(stateObject);
    }
}
