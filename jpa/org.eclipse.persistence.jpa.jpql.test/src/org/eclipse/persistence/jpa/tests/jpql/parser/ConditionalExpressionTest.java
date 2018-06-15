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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

@SuppressWarnings("nls")
public final class ConditionalExpressionTest extends JPQLParserTest {

    private JPQLQueryStringFormatter buildQueryFormatter_09() {
        return new JPQLQueryStringFormatter() {
            public String format(String query) {
                return query.replace("+", " + ");
            }
        };
    }

    private JPQLQueryStringFormatter buildQueryFormatter_10() {
        return new JPQLQueryStringFormatter() {
            public String format(String query) {
                return query.replace("-", " - ");
            }
        };
    }

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e WHERE e.name NOT LIKE 'Pascal'";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").notLike(string("'Pascal'")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e WHERE e.name > 'Pascal' AND e.name <> e.lastName";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                        path("e.name")
                    .greaterThan(
                        string("'Pascal'"))
                .and(
                        path("e.name")
                    .different(
                        path("e.lastName"))
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM Employee e WHERE e.name > 'Pascal' OR e.name <> e.lastName";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                        path("e.name")
                    .greaterThan(
                        string("'Pascal'"))
                .or(
                        path("e.name")
                    .different(
                        path("e.lastName"))
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE e.name > 'Pascal' OR e.name <> e.lastName AND e.age = 26";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                        path("e.name")
                    .greaterThan(
                        string("'Pascal'"))
                .or(
                        path("e.name").different(path("e.lastName"))
                    .and(
                        path("e.age").equal(numeric(26))
                    )
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e WHERE " +
                       "e.name > 'Pascal' AND e.manager >= 'code' OR " +
                       "e.name <> e.lastName AND e.age = 26";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                        path("e.name").greaterThan(string("'Pascal'"))
                .and(
                        path("e.manager").greaterThanOrEqual(string("'code'"))
                )
                .or(
                        path("e.name").different(path("e.lastName"))
                    .and(
                        path("e.age").equal(numeric(26))
                    )
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e " +
                       "FROM Employee e " +
                       "WHERE     e.name > 'Pascal' " +
                       "      AND    e.manager >= 'code' " +
                       "          OR " +
                       "             e.age < 21 " +
                       "          OR " +
                       "             e.name <> e.lastName " +
                       "      AND " +
                       "          e.age = 26";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    path("e.name").greaterThan(string("'Pascal'"))
                .and(
                        path("e.manager").greaterThanOrEqual(string("'code'"))
                )
                .or(
                    path("e.age").lowerThan(numeric(21))
                )
                .or(
                        path("e.name").different(path("e.lastName"))
                    .and(
                        path("e.age").equal(numeric(26))
                    )
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e FROM Employee e WHERE AVG(e.age)/mag.salary";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(avg("e.age").divide(path("mag.salary")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "SELECT e FROM Employee e WHERE AVG(e.age)*mag.salary";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(avg("e.age").multiply(path("mag.salary")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String query = "SELECT e FROM Employee e WHERE AVG(e.age)+mag.salary";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(avg("e.age").add(path("mag.salary")))
        );

        testQuery(query, selectStatement, buildQueryFormatter_09());
    }

    @Test
    public void test_JPQLQuery_10() {

        String query = "SELECT e FROM Employee e WHERE AVG(e.age)-mag.salary";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(avg("e.age").subtract(path("mag.salary")))
        );

        testQuery(query, selectStatement, buildQueryFormatter_10());
    }

    @Test
    public void test_JPQLQuery_11() {

        String query = "SELECT e FROM Employee e WHERE AVG(e.age) * mag.salary";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(avg("e.age").multiply(path("mag.salary")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_12() {

        String query = "SELECT e FROM Employee e WHERE AVG(e.age) / mag.salary";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(avg("e.age").divide(path("mag.salary")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_13() {

        String query = "SELECT e FROM Employee e WHERE -AVG(e.age) / mag.salary";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(minus(avg("e.age")).divide(path("mag.salary")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_14() {

        String query = "SELECT e FROM Employee e WHERE +AVG(e.age) / mag.salary";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(plus(avg("e.age")).divide(path("mag.salary")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_15() {

        String query = "SELECT e FROM Employee e WHERE +AVG(e.age) / -mag.salary";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(plus(avg("e.age")).divide(minus(path("mag.salary"))))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_16() {

        String query = "SELECT e FROM Employee e WHERE +AVG(e.age) - -mag.salary";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(plus(avg("e.age")).subtract(minus(path("mag.salary"))))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_17() {

        String query = "SELECT e FROM Employee e WHERE +AVG(e.age) = 2 AND -mag.salary";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    plus(avg("e.age")).equal(numeric(2))
                .and(
                    minus(path("mag.salary"))
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_18() {

        String query = "SELECT e FROM Employee e WHERE +(SQRT(e.age) + e.age) >= -21";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    plus(
                        sub(
                                sqrt(path("e.age"))
                            .add(
                                path("e.age")
                            )
                        )
                    )
                .greaterThanOrEqual(
                    numeric(-21)
                )
            )
        );

        testQuery(query, selectStatement);
    }
}
