/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * An identification variable qualified by the <code><b>ENTRY</b></code> operator is a path
 * expression. The <code><b>ENTRY</b></code> operator may only be applied to identification
 * variables that correspond to map-valued associations or map-valued element collections.
 * The type of the path expression is the type computed as the result of the operation; that
 * is, the abstract schema type of the field that is the value of the <code><b>ENTRY</b></code>
 * operator (the map entry).
 * <p>
 * This is part of JPA 2.0.
 * </p>
 * <div><b>BNF:</b> <code>expression ::= ENTRY(identification_variable)</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class EntryExpression extends EncapsulatedIdentificationVariableExpression {

    /**
     * Creates a new <code>EntryExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public EntryExpression(AbstractExpression parent) {
        super(parent, ENTRY);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
