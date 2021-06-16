/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The query BNF for the items of an <b>IN</b> expression.
 * <p>
 * JPA 1.0:
 * <div><b>BNF:</b> <code>in_item ::= ( actual_in_item {, actual_in_item}* | subquery)</code><p></p></div>
 * <div><b>BNF:</b> <code>actual_in_item ::= literal | input_parameter</code></div>
 * <p>
 * JPA 2.0:
 * <div><b>BNF:</b> <code>in_item ::= ( actual_in_item {, actual_in_item}* ) | (subquery) | collection_valued_input_parameter</code><p></p></div>
 * <div><b>BNF:</b> <code>actual_in_item ::= literal | input_parameter</code></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InExpressionItemBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "in_expression_item";

    /**
     * Creates a new <code>InExpressionItemBNF</code>.
     */
    public InExpressionItemBNF() {
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
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(LiteralExpressionFactory.ID);
        registerChild(LiteralBNF.ID);
        registerChild(InputParameterBNF.ID);
        registerChild(SubqueryBNF.ID);
    }
}
