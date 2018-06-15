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

import org.eclipse.persistence.jpa.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringPrimaryBNF;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>LENGTH</b></code> function returns the length of the string in characters as an integer.
 *
 * <div><b>BNF:</b> <code>expression ::= LENGTH(string_primary)</code><p></div>
 *
 * @see LengthExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class LengthExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

    /**
     * Creates a new <code>LengthExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public LengthExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>LengthExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param stateObject The {@link StateObject} representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public LengthExpressionStateObject(StateObject parent, StateObject stateObject) {
        super(parent, stateObject);
    }

    /**
     * Creates a new <code>LengthExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param jpqlFragment The portion of the query representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public LengthExpressionStateObject(StateObject parent, String jpqlFragment) {
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
    public LengthExpression getExpression() {
        return (LengthExpression) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return LENGTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getQueryBNFId() {
        return StringPrimaryBNF.ID;
    }

    /**
     * Keeps a reference of the {@link LengthExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link LengthExpression parsed object} representing a <code><b>LENGTH</b></code>
     * expression
     */
    public void setExpression(LengthExpression expression) {
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
