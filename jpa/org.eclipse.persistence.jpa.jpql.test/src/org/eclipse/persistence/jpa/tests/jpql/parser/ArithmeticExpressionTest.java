/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * Unit-tests for {@link org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpression ArithmeticExpression}.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ArithmeticExpressionTest extends JPQLParserTest {

    @Test
    public void testOrderOfOperations_01() {

        String query = "select e from Employee e where 10 + 2 * 10 / 2 = 20";

        // (10) + ((2 * 10) / (2))
        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    numeric(10)
                .add(
                        numeric(2).multiply(numeric(10))
                    .divide(
                        numeric(2)
                    )
                )
                .equal(numeric(20))
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void testOrderOfOperations_02() {

        String query = "select e from Employee e where 10 + 2 * 10 / 2 - 2 = 18";

        // (10 + ((2 * 10) / 2)) - (2)
        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    numeric(10)
                .add(
                            numeric(2)
                        .multiply(
                            numeric(10)
                        )
                    .divide(
                        numeric(2)
                    )
                    .subtract(
                        numeric(2)
                    )
                )
                .equal(numeric(18))
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void testOrderOfOperations_03() {

        String query = "SELECT OBJECT(e) FROM Employee e WHERE e.salary + 10000 - 10000 / ?1 * ?2 >= 50000";

        // (emp.salary) + ((10000) - ((10000 / ?1) * (?2))))
        ExpressionTester selectStatement = selectStatement(
            select(object("e")),
            from("Employee", "e"),
            where(
                    path("e.salary")
                .add(
                        numeric(10000)
                    .subtract(
                                numeric(10000)
                            .divide(
                                inputParameter("?1")
                            )
                        .multiply(
                            inputParameter("?2")
                        )
                    )
                )
                .greaterThanOrEqual(numeric(50000))
            )
        );

        testQuery(query, selectStatement);
    }
}
