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
 * The query BNF for a group by item expression.
 *
 * <div><b>BNF:</b> <code>groupby_item ::= single_valued_path_expression | identification_variable</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class GroupByItemBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "groupby_item";

    /**
     * Creates a new <code>GroupByItemBNF</code>.
     */
    public GroupByItemBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();

        // Technically, this BNF does not support collection but it's parent
        // groupby_clause does. But this BNF is used by GroupByClause directly
        // to parse the query so the flag has to be turned on here
        setHandleCollection(true);

        setHandleAggregate(true);
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(GroupByItemFactory.ID);
        registerChild(SingleValuedPathExpressionBNF.ID);
        registerChild(IdentificationVariableBNF.ID);
    }
}
