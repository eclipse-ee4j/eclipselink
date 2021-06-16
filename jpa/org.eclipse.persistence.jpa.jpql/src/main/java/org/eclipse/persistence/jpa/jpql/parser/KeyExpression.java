/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
 * <div><b>BNF:</b> <code>KEY(identification_variable)</code><p></p></div>
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
