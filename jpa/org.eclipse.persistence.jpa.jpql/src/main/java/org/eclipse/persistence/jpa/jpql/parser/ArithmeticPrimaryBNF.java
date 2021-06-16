/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * The query BNF for an arithmetic primary expression.
 * <p>
 * JPA 1.0
 * <div><b>BNF:</b> <code>arithmetic_primary ::= state_field_path_expression |
 *                                                      numeric_literal |
 *                                                      (simple_arithmetic_expression) |
 *                                                      input_parameter |
 *                                                      functions_returning_numerics |
 *                                                      aggregate_expression</code><p></p></div>
 *
 * JPA 2.0:
 * <div><b>BNF:</b> <code>arithmetic_primary ::= state_field_path_expression |
 *                                                      numeric_literal |
 *                                                      (simple_arithmetic_expression) |
 *                                                      input_parameter |
 *                                                      functions_returning_numerics |
 *                                                      aggregate_expression |
 *                                                      case_expression</code><p></p></div>
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
 *                                                      (subquery)</code><p></p></div>
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
