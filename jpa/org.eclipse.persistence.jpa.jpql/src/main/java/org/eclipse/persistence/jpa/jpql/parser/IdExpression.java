/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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

/**
 * The result of an ID function expression is the Entity primary
 * key. Argument must be an identification variable.
 *
 * <div><b>BNF:</b> <code>expression ::= ID(identification_variable)</code></div>
 *
 * @since 5.0
 * @author Radek Felcman
 */
public final class IdExpression extends EncapsulatedIdentificationVariableExpression {

    /**
     * The field path created as a result of transformation
     */
    private StateFieldPathExpression stateFieldPathExpression;

    /**
     * Creates a new <code>IdExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public IdExpression(AbstractExpression parent) {
        super(parent, ID);
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        this.getRoot().setIdExpression(true);
        visitor.visit(this);
    }

    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(IdExpressionBNF.ID);
    }

    /**
     * Returns field path created as a result of transformation.
     *
     * @return The path expression that is qualified by the identification variable
     */
    public StateFieldPathExpression getStateFieldPathExpression() {
        return stateFieldPathExpression;
    }

    /**
     * Sets field path created as a result of transformation.
     */
    public void setStateFieldPathExpression(StateFieldPathExpression stateFieldPathExpression) {
        this.stateFieldPathExpression = stateFieldPathExpression;
    }
}
