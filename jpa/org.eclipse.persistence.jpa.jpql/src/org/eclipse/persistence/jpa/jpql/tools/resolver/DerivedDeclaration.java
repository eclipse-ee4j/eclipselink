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

import org.eclipse.persistence.jpa.jpql.ExpressionTools;

/**
 * This <code>DerivedDeclaration</code> represents an identification variable declaration that was
 * declared in the <code><b>FROM</b></code> clause of a <code><b>SELECT</b></code> subquery. The
 * "root" object is not an entity name but a derived path expression.
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration IdentificationVariableDeclaration
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public class DerivedDeclaration extends AbstractRangeDeclaration {

    /**
     * Creates a new <code>DerivedDeclaration</code>.
     */
    public DerivedDeclaration() {
        super();
    }

    /**
     * If {@link org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration#isDerived()} is
     * <code>true</code>, then returns the identification variable used in the derived path expression
     * that is defined in the superquery, otherwise returns an empty string.
     *
     * @return The identification variable from the superquery if the root path is a derived path
     * expression
     */
    public String getSuperqueryVariableName() {
        int index = rootPath.indexOf('.');
        return (index > -1) ? rootPath.substring(0, index) : ExpressionTools.EMPTY_STRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getType() {
        return Type.DERIVED;
    }
}
