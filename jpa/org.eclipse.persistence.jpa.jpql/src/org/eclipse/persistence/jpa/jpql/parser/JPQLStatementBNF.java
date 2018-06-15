/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * The query BNF for the JPQL expression.
 *
 * <div><b>BNF:</b> QL_statement ::= <code>select_statement | update_statement | delete_statement</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLStatementBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "ql_statement";

    /**
     * Creates a new <code>JPQLStatementBNF</code>.
     */
    public JPQLStatementBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(UnknownExpressionFactory.ID);
        registerChild(SelectStatementBNF.ID);
        registerChild(UpdateStatementBNF.ID);
        registerChild(DeleteStatementBNF.ID);
    }
}
