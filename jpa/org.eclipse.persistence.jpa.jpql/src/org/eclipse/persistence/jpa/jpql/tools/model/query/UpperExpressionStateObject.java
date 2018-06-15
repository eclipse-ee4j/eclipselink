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

import org.eclipse.persistence.jpa.jpql.parser.StringPrimaryBNF;
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>UPPER</b></code> function converts a string to upper case and it returns a string.
 *
 * <div><b>BNF:</b> <code>expression ::= UPPER(string_primary)</code><p></div>
 *
 * @see UpperExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class UpperExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

    /**
     * Creates a new <code>UpperExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public UpperExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>UpperExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param stateObject The {@link StateObject} representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public UpperExpressionStateObject(StateObject parent, StateObject stateObject) {
        super(parent, stateObject);
    }

    /**
     * Creates a new <code>UpperExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param jpqlFragment The portion of the query representing the encapsulated expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public UpperExpressionStateObject(StateObject parent, String jpqlFragment) {
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
    public UpperExpression getExpression() {
        return (UpperExpression) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return UPPER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getQueryBNFId() {
        return StringPrimaryBNF.ID;
    }

    /**
     * Keeps a reference of the {@link UpperExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link UpperExpression parsed object} representing an <code><b>UPPER</b></code>
     * expression
     */
    public void setExpression(UpperExpression expression) {
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
