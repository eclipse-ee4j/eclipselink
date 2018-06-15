/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * An identification variable qualified by the <code><b>KEY</b></code> operator is a path
 * expression. The <code><b>KEY</b></code> operator may only be applied to identification
 * variables that correspond to map-valued associations or map-valued element collections.
 * The type of the path expression is the type computed as the result of the operation; that
 * is, the abstract schema type of the field that is the value of the <code><b>KEY</b></code>
 * operator (the map key).
 * <p>
 * This is part of JPA 2.0.
 *
 * <div><b>BNF:</b> <code>KEY(identification_variable)</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class KeyExpression extends EncapsulatedIdentificationVariableExpression {

    /**
     * Creates a new <code>KeyExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public KeyExpression(AbstractExpression parent) {
        super(parent, KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
