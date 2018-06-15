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
 * The query BNF for an aggregate expression.
 * <p>
 * JPA 1.0, 2.0:
 * <div><b>BNF:</b> <code>aggregate_expression ::= { AVG | MAX | MIN | SUM } ([DISTINCT] state_field_path_expression) |
 *                                                        COUNT ([DISTINCT] identification_variable |
 *                                                                          state_field_path_expression |
 *                                                                          single_valued_object_path_expression)</code>
 * <p></div>
 * JPA 2.1:
 * <div><b>BNF:</b> <code>aggregate_expression ::= { AVG | MAX | MIN | SUM } ([DISTINCT] state_field_path_expression) |
 *                                                        COUNT ([DISTINCT] identification_variable |
 *                                                                          state_field_path_expression |
 *                                                                          single_valued_object_path_expression) |
 *                                                        function_invocation</code>
 * <p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class AggregateExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "aggregate_expression";

    /**
     * Creates a new <code>AggregateExpressionBNF</code>.
     */
    public AggregateExpressionBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();

        registerExpressionFactory(AvgFunctionFactory.ID);
        registerExpressionFactory(MaxFunctionFactory.ID);
        registerExpressionFactory(MinFunctionFactory.ID);
        registerExpressionFactory(SumFunctionFactory.ID);
        registerExpressionFactory(CountFunctionFactory.ID);
    }
}
