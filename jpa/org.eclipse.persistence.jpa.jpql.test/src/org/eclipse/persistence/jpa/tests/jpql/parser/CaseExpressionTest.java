/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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

@SuppressWarnings("nls")
public final class CaseExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() throws Exception {

        String query = "UPDATE Employee e " +
                       "SET e.salary = " +
                       "    CASE WHEN e.rating = 1 THEN e.salary * 1.1 " +
                       "         WHEN e.rating = 2 THEN e.salary * 1.05 " +
                       "         ELSE e.salary * 1.01 " +
                       "    END";

        ExpressionTester updateStatement = updateStatement(
            update(
                "Employee", "e",
                set(
                    path("e.salary"),
                    case_(
                        new ExpressionTester[] {
                            when(path("e.rating").equal(numeric(1)), path("e.salary").multiply(numeric("1.1"))),
                            when(path("e.rating").equal(numeric(2)), path("e.salary").multiply(numeric("1.05")))
                        },
                        path("e.salary").multiply(numeric(1.01))
                    )
                )
            )
        );

        testQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_02() throws Exception {
        String query = "SELECT e.name, " +
                       "       f.name, " +
                       "       CONCAT(CASE WHEN f.annualMiles > 50000 THEN 'Platinum ' " +
                       "                   WHEN f.annualMiles > 25000 THEN 'Gold ' " +
                       "                   ELSE '' " +
                       "                   END, " +
                       "              'Frequent Flyer') " +
                       "FROM Employee e JOIN e.frequentFlierPlan f";

        ExpressionTester selectStatement = selectStatement(
            select(
                path("e.name"),
                path("f.name"),
                concat(
                    case_(
                        new ExpressionTester[] {
                            when(path("f.annualMiles").greaterThan(numeric(50000)), string("'Platinum '")),
                            when(path("f.annualMiles").greaterThan(numeric(25000)), string("'Gold '"))
                        },
                        string("''")
                    ),
                    string("'Frequent Flyer'")
                )
            ),
            from("Employee", "e", join("e.frequentFlierPlan", "f"))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() throws Exception {
        String query = "SELECT CASE WHEN f.annualMiles > 50000 THEN 'Platinum ' " +
                       "            WHEN f.annualMiles > 25000 THEN 'Gold ' " +
                       "            ELSE '' " +
                       "       END " +
                       "FROM Employee e JOIN e.frequentFlierPlan f";

        ExpressionTester selectStatement = selectStatement(
            select(
                case_(
                    new ExpressionTester[] {
                        when(path("f.annualMiles").greaterThan(numeric(50000)), string("'Platinum '")),
                        when(path("f.annualMiles").greaterThan(numeric(25000)), string("'Gold '"))
                    },
                    string("''")
                )
            ),
            from("Employee", "e", join("e.frequentFlierPlan", "f"))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() throws Exception {
        String query = "SELECT CASE WHEN e.age > 17 THEN 0 " +
                       "            WHEN e.age > 39 THEN 1 " +
                       "            WHEN e.age > 64 THEN 2 " +
                       "            ELSE 3 " +
                       "       END " +
                       "       + :input " +
                       "FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(
                    case_(
                        new ExpressionTester[] {
                            when(path("e.age").greaterThan(numeric(17)), numeric(0)),
                            when(path("e.age").greaterThan(numeric(39)), numeric(1)),
                            when(path("e.age").greaterThan(numeric(64)), numeric(2)),
                        },
                        numeric(3)
                    )
                .add(
                    inputParameter(":input")
                )
            ),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {
        String query = "SELECT e.name," +
                       "       CASE TYPE(e) WHEN Exempt THEN 'Exempt'" +
                       "                    WHEN Contractor THEN 'Contractor'" +
                       "                    WHEN Intern THEN 'Intern'" +
                       "                    ELSE 'NonExempt'" +
                       "       END " +
                       "FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(
                path("e.name"),
                case_(type("e"),
                    new ExpressionTester[] {
                        when(entity("Exempt"),     string("'Exempt'")),
                        when(entity("Contractor"), string("'Contractor'")),
                        when(entity("Intern"),     string("'Intern'")),
                    },
                    string("'NonExempt'")
                )
            ),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }
}
