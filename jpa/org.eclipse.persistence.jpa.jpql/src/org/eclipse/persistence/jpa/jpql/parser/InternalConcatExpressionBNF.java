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
 * The query BNF for the <b>COUNT</b> expression's encapsulated expressions.
 * <p>
 * JPA 1.0:
 * <div><b>BNF:</b> <code>expression ::= CONCAT(string_primary, string_primary)</code></div>
 * <p>
 * JPA 2.0:
 * <div><b>BNF:</b> <code>expression ::= CONCAT(string_primary, string_primary {, string_primary}*)</code></div>
 * <p>
 * JPA 2.1:
 * <div><b>BNF:</b> <code>expression ::= CONCAT(string_expression, string_expression {, string_expression}*)</code></div>
 * <p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InternalConcatExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this <code>InternalConcatExpressionBNF</code>.
     */
    public final static String ID = "internal_concat";

    /**
     * Creates a new <code>InternalConcatExpressionBNF</code>.
     */
    public InternalConcatExpressionBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setHandleAggregate(true); // To support invalid queries
        setHandleCollection(true);
        setFallbackBNFId(LiteralExpressionFactory.ID);
        registerChild(StringPrimaryBNF.ID);
    }
}
