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

import org.eclipse.persistence.jpa.jpql.parser.IndexExpression;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>INDEX</b></code> function returns an integer value corresponding to the position of
 * its argument in an ordered list. The <code><b>INDEX</b></code> function can only be applied to
 * identification variables denoting types for which an order column has been specified.
 *
 * <div><b>BNF:</b> <code>expression ::= INDEX(identification_variable)</code><p></div>
 *
 * @see IndexExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class IndexExpressionStateObject extends EncapsulatedIdentificationVariableExpressionStateObject {

    /**
     * Creates a new <code>IndexExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public IndexExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>IndexExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param identificationVariable The name of the identification variable
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public IndexExpressionStateObject(StateObject parent, String identificationVariable) {
        super(parent, identificationVariable);
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
    public IndexExpression getExpression() {
        return (IndexExpression) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return INDEX;
    }

    /**
     * Keeps a reference of the {@link IndexExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link IndexExpression parsed object} representing an <code><b>INDEX</b></code>
     * expression
     */
    public void setExpression(IndexExpression expression) {
        super.setExpression(expression);
    }
}
