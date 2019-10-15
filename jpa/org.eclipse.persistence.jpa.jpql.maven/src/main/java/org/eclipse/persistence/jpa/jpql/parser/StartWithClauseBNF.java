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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The query BNF for the <code><b>START WITH</b></code> clause.
 *
 * <div><b>BNF:</b> <code>start_with_clause ::= START WITH conditional_expression</code><p></div>
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class StartWithClauseBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = Expression.START_WITH;

    /**
     * Creates a new <code>StartWithClauseBNF</code>.
     */
    public StartWithClauseBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        registerExpressionFactory(StartWithClauseFactory.ID);
    }
}
