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

import org.eclipse.persistence.jpa.jpql.parser.LowerExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringPrimaryBNF;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>LOWER</b></code> function converts a string to lower case and it returns a string.
 *
 * <div><b>BNF:</b> <code>expression ::= LOWER(string_primary)</code><p></div>
 *
 * @see LowerExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class LowerExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

    /**
     * Creates a new <code>LowerExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public LowerExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>LowerExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param stateObject The {@link StateObject} representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public LowerExpressionStateObject(StateObject parent, StateObject stateObject) {
        super(parent, stateObject);
    }

    /**
     * Creates a new <code>LowerExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param jpqlFragment The portion of the query representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public LowerExpressionStateObject(StateObject parent, String jpqlFragment) {
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
    public LowerExpression getExpression() {
        return (LowerExpression) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return LOWER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getQueryBNFId() {
        return StringPrimaryBNF.ID;
    }

    /**
     * Keeps a reference of the {@link LowerExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link LowerExpression parsed object} representing a <code><b>LOWER</b></code>
     * expression
     */
    public void setExpression(LowerExpression expression) {
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
