/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
