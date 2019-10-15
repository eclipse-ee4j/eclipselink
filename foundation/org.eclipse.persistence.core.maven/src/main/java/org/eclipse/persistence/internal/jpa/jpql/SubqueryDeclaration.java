/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * This {@link Declaration} uses a subquery as the "root" object.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
final class SubqueryDeclaration extends Declaration {

    /**
     * Creates a new <code>SubqueryDeclaration</code>.
     *
     * @param queryContext The context used to query information about the application metadata and
     * cached information
     */
    public SubqueryDeclaration(JPQLQueryContext queryContext) {
        super(queryContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Expression buildQueryExpression() {
        RangeVariableDeclaration declaration = (RangeVariableDeclaration) getBaseExpression();
        Expression expressoin = queryContext.buildExpression(declaration.getRootObject());
        return queryContext.getBaseExpression().getAlias(expressoin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getType() {
        return Type.SUBQUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    ClassDescriptor resolveDescriptor() {
        // A subquery used as a declaration does not have a descriptor
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    DatabaseMapping resolveMapping() {
        // Does not resolve to a mapping
        return null;
    }
}
