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
 * The query BNF for a function expression returning a numeric value.
 * <p>
 * JPA 1.0:
 * <div><b>BNF:</b> <code>functions_returning_numerics::= LENGTH(string_primary) |
 *                                                               LOCATE(string_primary, string_primary[, simple_arithmetic_expression]) |
 *                                                               ABS(simple_arithmetic_expression) | SQRT(simple_arithmetic_expression) |
 *                                                               MOD(simple_arithmetic_expression, simple_arithmetic_expression) |
 *                                                               SIZE(collection_valued_path_expression)</code><p></p></div>
 *
 * JPA 2.0:
 * <div><b>BNF:</b> <code>functions_returning_numerics::= LENGTH(string_primary) |
 *                                                               LOCATE(string_primary, string_primary[, simple_arithmetic_expression]) |
 *                                                               ABS(simple_arithmetic_expression) | SQRT(simple_arithmetic_expression) |
 *                                                               MOD(simple_arithmetic_expression, simple_arithmetic_expression) |
 *                                                               SIZE(collection_valued_path_expression) |
 *                                                               INDEX(identification_variable)</code><p></p></div>
 *
 * @version 2.4
 * @since 2.3
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

    /**
     * {@inheritDoc}
     */
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
