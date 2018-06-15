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
 * The query BNF for the expression following the <b>WHEN</b> identifier.
 *
 * <div><b>BNF:</b> <code>when_clause ::= WHEN conditional_expression THEN scalar_expression</code><p></div>
 * or
 * <div><b>BNF:</b> <code>simple_when_clause ::= WHEN scalar_expression THEN scalar_expression</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InternalWhenClauseBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "internal_when_clause*";

    /**
     * Creates a new <code>InternalWhenClauseBNF</code>.
     */
    public InternalWhenClauseBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setFallbackBNFId(EntityTypeExpressionBNF.ID);
        registerChild(ConditionalExpressionBNF.ID);
        registerChild(ScalarExpressionBNF.ID);
    }
}
