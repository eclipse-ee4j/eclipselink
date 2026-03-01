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
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import org.eclipse.persistence.jpa.jpql.parser.IdExpression;
import org.eclipse.persistence.jpa.jpql.tools.model.Problem;

import java.util.List;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.OBJECT;

/**
 * The result of an ID function expression is the Entity primary
 * key. Argument must be an identification variable.
 *
 * <div><b>BNF:</b> <code>expression ::= ID(identification_variable)</code></div>
 *
 * @since 5.0
 * @author Radek Felcman
 */
public class IdExpressionStateObject extends EncapsulatedIdentificationVariableExpressionStateObject {

    /**
     * Creates a new <code>IdExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public IdExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>IdExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param identificationVariable The name of the identification variable
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public IdExpressionStateObject(StateObject parent, String identificationVariable) {
        super(parent, identificationVariable);
    }

    @Override
    public void accept(StateObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected void addProblems(List<Problem> problems) {
        super.addProblems(problems);
    }

    @Override
    public IdExpression getExpression() {
        return (IdExpression) super.getExpression();
    }

    @Override
    public String getIdentifier() {
        return OBJECT;
    }

    /**
     * Keeps a reference of the {@link IdExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link IdExpression parsed object} representing an <code><b>ID</b></code>
     * expression
     */
    public void setExpression(IdExpression expression) {
        super.setExpression(expression);
    }
}
