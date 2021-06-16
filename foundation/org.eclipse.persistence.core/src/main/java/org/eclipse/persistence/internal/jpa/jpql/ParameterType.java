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
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.AbstractEclipseLinkParameterTypeVisitor;

/**
 * This visitor's responsibility is to find the type of an input parameter.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
final class ParameterTypeVisitor extends AbstractEclipseLinkParameterTypeVisitor {

    /**
     * The context used to query information about the query.
     */
    private final JPQLQueryContext queryContext;

    /**
     * Creates a new <code>ParameterTypeVisitor</code>.
     *
     * @param queryContext The context used to query information about the application metadata and
     * cached information
     */
    ParameterTypeVisitor(JPQLQueryContext queryContext) {
        super();
        this.queryContext = queryContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getType() {

        // The type should be ignored, use the special constant
        if (ignoreType) {
            return Object.class;
        }

        // The type name was set
        if (typeName != null) {
            return queryContext.getType(typeName);
        }

        // The calculation couldn't find an expression with a type
        if (expression == null) {
            if (type == null) {
                type = Object.class;
            }
            return type;
        }

        return queryContext.getType(expression);
    }
}
