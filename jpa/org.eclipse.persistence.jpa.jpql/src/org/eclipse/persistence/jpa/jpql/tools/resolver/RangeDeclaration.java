/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.resolver;


/**
 * This <code>RangeDeclaration</code> represents an identification variable declaration that was
 * declared in the <code><b>FROM</b></code> clause of a <code><b>SELECT</b></code> top-level query
 * or subquery.
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration IdentificationVariableDeclaration
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public class RangeDeclaration extends AbstractRangeDeclaration {

    /**
     * Creates a new <code>RangeDeclaration</code>.
     */
    public RangeDeclaration() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getType() {
        return Type.RANGE;
    }
}
