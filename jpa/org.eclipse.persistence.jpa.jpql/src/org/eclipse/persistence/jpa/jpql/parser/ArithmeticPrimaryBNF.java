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
 * The query BNF for an arithmetic primary expression.
 * <p>
 * JPA 1.0
 * <div><b>BNF:</b> <code>arithmetic_primary ::= state_field_path_expression |
 *                                                      numeric_literal |
 *                                                      (simple_arithmetic_expression) |
 *                                                      input_parameter |
 *                                                      functions_returning_numerics |
 *                                                      aggregate_expression</code><p></div>
 *
 * JPA 2.0:
 * <div><b>BNF:</b> <code>arithmetic_primary ::= state_field_path_expression |
 *                                                      numeric_literal |
 *                                                      (simple_arithmetic_expression) |
 *                                                      input_parameter |
 *                                                      functions_returning_numerics |
 *                                                      aggregate_expression |
 *                                                      case_expression</code><p></div>
 *
 * JPA 2.1:
 * <div><b>BNF:</b> <code>arithmetic_primary ::= state_field_path_expression |
 *                                                      numeric_literal |
 *                                                      (arithmetic_expression) |
 *                                                      input_parameter |
 *                                                      functions_returning_numerics |
 *                                                      aggregate_expression |
 *                                                      case_expression |
 *                                                      function_invocation |
 *                                                      (subquery)</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ArithmeticPrimaryBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "arithmetic_primary";

    /**
     * Creates a new <code>ArithmeticPrimaryBNF</code>.
     */
    public ArithmeticPrimaryBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(LiteralExpressionFactory.ID);
        registerChild(StateFieldPathExpressionBNF.ID);
        registerChild(NumericLiteralBNF.ID);
        registerChild(SimpleArithmeticExpressionBNF.ID);
        registerChild(InputParameterBNF.ID);
        registerChild(FunctionsReturningNumericsBNF.ID);
        registerChild(AggregateExpressionBNF.ID);
    }
}
