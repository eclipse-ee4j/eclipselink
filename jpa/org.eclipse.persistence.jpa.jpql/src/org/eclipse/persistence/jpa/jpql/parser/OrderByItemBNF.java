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
 * The query BNF for the order by item expression.
 *
 * <div><b>BNF:</b> <code>orderby_item ::= state_field_path_expression | result_variable [ ASC | DESC ]</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class OrderByItemBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "orderby_item";

    /**
     * Creates a new <code>OrderByItemBNF</code>.
     */
    public OrderByItemBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();

        // Technically, this BNF does not support collection but it's parent
        // orderby_clause does. But this BNF is used by OrderByClause directly
        // to parse the query so the flag has to be turned on here
        setHandleCollection(true);

        // Bug 506512 - Syntax error parsing JPQL with ORDER BY clause, using parentheses
        setHandleSubExpression(true);
        
        setHandleAggregate(true);
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(OrderByItemFactory.ID);
    }
}
