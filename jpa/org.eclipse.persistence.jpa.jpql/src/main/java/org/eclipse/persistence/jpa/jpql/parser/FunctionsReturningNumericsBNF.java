/*
 * Copyright (c) 2006, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for Jakarta Persistence 2.2, 3.0 and 3.1
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The query BNF for a function expression returning a numeric value.
 * <p>
 * JPA 1.0:
 * <div><b>BNF:</b> <code>functions_returning_numerics::= LENGTH(string_primary) |
 *                                                               LOCATE(string_primary, string_primary[, simple_arithmetic_expression]) |
 *                                                               ABS(simple_arithmetic_expression) | SQRT(simple_arithmetic_expression) |
 *                                                               MOD(simple_arithmetic_expression, simple_arithmetic_expression) |
 *                                                               SIZE(collection_valued_path_expression)</code></div>
 *
 * JPA 2.0:
 * <div><b>BNF:</b> <code>functions_returning_numerics::= LENGTH(string_primary) |
 *                                                               LOCATE(string_primary, string_primary[, simple_arithmetic_expression]) |
 *                                                               ABS(simple_arithmetic_expression) | SQRT(simple_arithmetic_expression) |
 *                                                               MOD(simple_arithmetic_expression, simple_arithmetic_expression) |
 *                                                               SIZE(collection_valued_path_expression) |
 *                                                               INDEX(identification_variable)</code></div>
 * Jakarta Persistence 3.1:
 * <div><b>BNF:</b> <code>functions_returning_numerics ::= ABS(arithmetic_expression) |
 *                                                               CEILING(arithmetic_expression) |
 *                                                               EXP(arithmetic_expression) |
 *                                                               FLOOR(arithmetic_expression) |
 *                                                               LN(arithmetic_expression) |
 *                                                               MOD(arithmetic_expression, arithmetic_expression) |
 *                                                               POWER(arithmetic_expression, arithmetic_expression) |
 *                                                               ROUND(arithmetic_expression, arithmetic_expression) |
 *                                                               SIGN(arithmetic_expression) |
 *                                                               SQRT(arithmetic_expression) |
 *                                                               SIZE(collection_valued_path_expression) |
 *                                                               INDEX(identification_variable) |
 *                                                               extract_datetime_field</code>
 *                  <code>extract_datetime_field := EXTRACT(datetime_field FROM datetime_expression)
 *                        datetime_field := identification_variable</code></div>
 *
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class FunctionsReturningNumericsBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "functions_returning_numerics";

    /**
     * Creates a new <code>FunctionsReturningNumericsBNF</code>.
     */
    public FunctionsReturningNumericsBNF() {
        super(ID);
    }

    @Override
    protected void initialize() {
        super.initialize();

        registerExpressionFactory(LengthExpressionFactory.ID);
        registerExpressionFactory(LocateExpressionFactory.ID);
        registerExpressionFactory(AbsExpressionFactory.ID);
        registerExpressionFactory(SqrtExpressionFactory.ID);
        registerExpressionFactory(ModExpressionFactory.ID);
        registerExpressionFactory(SizeExpressionFactory.ID);
    }
}
