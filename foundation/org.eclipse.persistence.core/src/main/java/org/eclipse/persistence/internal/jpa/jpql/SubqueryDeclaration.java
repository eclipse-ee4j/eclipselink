/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
