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

import org.eclipse.persistence.jpa.jpql.parser.UnknownExpression;

/**
 * This {@link StateObject} holds onto an unknown portion of a JPQL query that could not be parsed.
 *
 * @see UnknownExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class UnknownExpressionStateObject extends SimpleStateObject {

    /**
     * Creates a new <code>UnknownExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param text The unknown expression
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public UnknownExpressionStateObject(StateObject parent, String text) {
        super(parent, text);
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
    public UnknownExpression getExpression() {
        return (UnknownExpression) super.getExpression();
    }


    /**
     * Keeps a reference of the {@link UnknownExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link UnknownExpression parsed object} representing an unknown
     * expression
     */
    public void setExpression(UnknownExpression expression) {
        super.setExpression(expression);
    }
}
