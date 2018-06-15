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
 * The query BNF for a simple select expression.
 *
 * <div><b>BNF:</b> <code>simple_select_expression ::= single_valued_path_expression |
 *                                                            scalar_expression |
 *                                                            aggregate_expression |
 *                                                            identification_variable</code></div>
 * <p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class SimpleSelectExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "simple_select_expression";

    /**
     * Creates a new <code>SimpleSelectExpressionBNF</code>.
     */
    public SimpleSelectExpressionBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setHandleCollection(true); // To support invalid queries
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(LiteralExpressionFactory.ID);
        registerChild(SingleValuedPathExpressionBNF.ID);
        registerChild(ScalarExpressionBNF.ID);
        registerChild(AggregateExpressionBNF.ID);
        registerChild(IdentificationVariableBNF.ID);
    }
}
