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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;

/**
 * The abstract definition of a range declaration, which is used to navigate to a "root" object.
 *
 * @see DerivedDeclaration
 * @see RangeDeclaration
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
abstract class AbstractRangeDeclaration extends Declaration {

    /**
     * The list of <b>JOIN</b> expressions that are declared in the same declaration than the range
     * variable declaration represented by this declaration.
     */
    protected List<Join> joins;

    /**
     * Creates a new <code>AbstractRangeDeclaration</code>.
     *
     * @param queryContext The context used to query information about the application metadata and
     * cached information
     */
    AbstractRangeDeclaration(JPQLQueryContext queryContext) {
        super(queryContext);
    }

    /**
     * Adds the given {@link Join}.
     *
     * @param join The {@link Join} that is declared in the range variable declaration
     */
    void addJoin(Join join) {
        if (joins == null) {
            joins = new LinkedList<Join>();
        }
        joins.add(join);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RangeVariableDeclaration getBaseExpression() {
        return (RangeVariableDeclaration) super.getBaseExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IdentificationVariableDeclaration getDeclarationExpression() {
        return (IdentificationVariableDeclaration) super.getDeclarationExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Join> getJoins() {
        if (joins == null) {
            return Collections.emptyList();
        }
        return joins;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasJoins() {
        return joins != null;
    }
}
