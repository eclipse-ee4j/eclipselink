/*
 * Copyright (c) 2024, 2025 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private final Map<String, StateFieldPathExpression> stateFieldPathExpressions = new LinkedHashMap<>();

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
     * Returns field paths created as a result of transformation.
     *
     * @return The path expressions that is qualified by the identification variable.
     * There should more items, than one as multiple entity fields should be marked with ${@code @Id} and ${@code @IdClass} is used.
     */
    public Collection<StateFieldPathExpression> getStateFieldPathExpressions() {
        return stateFieldPathExpressions.values();
    }

    /**
     * Add field path created as a result of transformation.
     */
    public void addStateFieldPathExpression(StateFieldPathExpression stateFieldPathExpression) {
        this.stateFieldPathExpressions.put(stateFieldPathExpression.getText(), stateFieldPathExpression);
    }
}
