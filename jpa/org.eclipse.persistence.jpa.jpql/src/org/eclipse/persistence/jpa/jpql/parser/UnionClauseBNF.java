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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The query BNF for the order by clause.
 *
 * <div><b>BNF:</b> <code>union_clause ::= { UNION | INTERSECT | EXCEPT } [ALL] subquery</code></div>
 * <p>
 *
 * @version 2.4
 * @since 2.4
 * @author James Sutherland
 */
@SuppressWarnings("nls")
public final class UnionClauseBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "union_clause";

    /**
     * Creates a new <code>UnionClauseBNF</code>.
     */
    public UnionClauseBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setHandleAggregate(true);
        setHandleCollection(true); // For invalid queries
        registerExpressionFactory(UnionClauseFactory.ID);
    }
}
