/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * A <code>JoinDeclaration</code> represents a single <code><b>JOIN</b></code> expression.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
final class JoinDeclaration extends Declaration {

    /**
     * Creates a new <code>JoinDeclaration</code>.
     *
     * @param queryContext The context used to query information about the application metadata and
     * cached information
     */
    JoinDeclaration(JPQLQueryContext queryContext) {
        super(queryContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Expression buildQueryExpression() {
        return queryContext.buildExpression(baseExpression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Join getBaseExpression() {
        return (Join) super.getBaseExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getType() {
        return Type.UNKNOWN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    ClassDescriptor resolveDescriptor() {
        return queryContext.resolveDescriptor(getBaseExpression().getJoinAssociationPath());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    DatabaseMapping resolveMapping() {
        return queryContext.resolveMapping(getBaseExpression().getJoinAssociationPath());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return baseExpression.toParsedText();
    }
}
