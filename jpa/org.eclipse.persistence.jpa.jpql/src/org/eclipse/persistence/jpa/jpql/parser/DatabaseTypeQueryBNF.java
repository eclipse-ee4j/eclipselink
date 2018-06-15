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
 * The query BNF for the database type.
 *
 * <div><b>BNF:</b> <code>database_type ::= data_type_literal [( [numeric_literal [, numeric_literal]] )]</code></div>
 *
 * <div><b>BNF:</b> <code>data_type_literal ::= [CHAR, VARCHAR, NUMERIC, INTEGER, DATE, TIME, TIMESTAMP, etc]</code></div>
 * <p>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DatabaseTypeQueryBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "database_type";

    /**
     * Creates a new <code>DatabaseTypeQueryBNF</code>.
     */
    public DatabaseTypeQueryBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(DatabaseTypeFactory.ID);
    }
}
