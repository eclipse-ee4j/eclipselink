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
 * The query BNF for the order siblings by clause.
 *
 * <div><b>BNF:</b> <code>order_sibling_by_clause ::= ORDER SIBLINGS BY orderby_item {, orderby_item}*</code><p></div>
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class OrderSiblingsByClauseBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "order_sibling_by_clause";

    /**
     * Creates a new <code>OrderSiblingsByClauseBNF</code>.
     */
    public OrderSiblingsByClauseBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setHandleCollection(true);
        registerExpressionFactory(OrderSiblingsByClauseFactory.ID);
    }
}
