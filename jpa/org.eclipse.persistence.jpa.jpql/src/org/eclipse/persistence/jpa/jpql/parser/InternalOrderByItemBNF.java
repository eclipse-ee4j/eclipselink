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
 * The query BNF for an order by item.
 * <p>
 * JPA 1.0: <div><b>BNF:</b> <code>orderby_item ::= state_field_path_expression [ ASC | DESC ]</code></div>
 * <p>
 * JPA 2.0 <div><b>BNF:</b> <code>orderby_item ::= state_field_path_expression | result_variable [ ASC | DESC ]</code><p></div>
 *
 * @version 2.5.1
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InternalOrderByItemBNF extends JPQLQueryBNF {

    /**
     * The unique identifier for this <code>InternalOrderByItemBNF</code>.
     */
    public static final String ID = "internal_orderby_item";

    /**
     * Creates a new <code>InternalOrderByItemBNF</code>.
     */
    public InternalOrderByItemBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setHandleAggregate(true); // For invalid queries
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(InternalOrderByItemFactory.ID);
        registerChild(IdentificationVariableBNF.ID);
        registerChild(StateFieldPathExpressionBNF.ID);
    }
}
