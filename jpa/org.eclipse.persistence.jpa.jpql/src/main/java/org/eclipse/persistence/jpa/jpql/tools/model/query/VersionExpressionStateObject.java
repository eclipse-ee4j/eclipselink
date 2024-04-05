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

import org.eclipse.persistence.jpa.jpql.parser.VersionExpression;
import org.eclipse.persistence.jpa.jpql.tools.model.Problem;

import java.util.List;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.VERSION;

/**
 * The result of an VERSION function expression is value Entity <code>@Version</code> field.
 * Argument must be an identification variable.
 *
 * <div><b>BNF:</b> <code>expression ::= VERSION(identification_variable)</code></div>
 *
 * @since 5.0
 * @author Radek Felcman
 */
public class VersionExpressionStateObject extends EncapsulatedIdentificationVariableExpressionStateObject {

    /**
     * Creates a new <code>VersionExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public VersionExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>VersionExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param identificationVariable The name of the identification variable
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public VersionExpressionStateObject(StateObject parent, String identificationVariable) {
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
    public VersionExpression getExpression() {
        return (VersionExpression) super.getExpression();
    }

    @Override
    public String getIdentifier() {
        return VERSION;
    }

    /**
     * Keeps a reference of the {@link VersionExpression parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link VersionExpression parsed object} representing an <code><b>ID</b></code>
     * expression
     */
    public void setExpression(VersionExpression expression) {
        super.setExpression(expression);
    }
}
