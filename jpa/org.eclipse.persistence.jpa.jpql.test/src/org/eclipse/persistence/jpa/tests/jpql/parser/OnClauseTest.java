/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
public final class OnClauseTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e JOIN e.projects p ON p.budget > 10000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e",
                join("e.projects", "p",
                    on(
                            path("p.budget")
                        .greaterThan(
                            numeric("10000")
                        )
                    )
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e JOIN e.projects p ON";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", join("e.projects", "p", on(nullExpression())))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM Employee e JOIN e.projects p ON ";

        OnClauseTester onClause = on(nullExpression());
        onClause.hasSpaceAfterIdentifier = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", join("e.projects", "p", onClause))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e JOIN e.projects p ON p.budget > 10000 JOIN FETCH p.managers";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e",
                join("e.projects", "p",
                    on(
                            path("p.budget")
                        .greaterThan(
                            numeric("10000")
                        )
                    )
                ),
                joinFetch("p.managers")
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e " +
                       "FROM Employee e JOIN e.projects p ON p.budget > 10000 " +
                       "                JOIN FETCH p.managers ON " +
                       "WHERE e.id > -1";

        OnClauseTester onClause = on(nullExpression());
        onClause.hasSpaceAfterIdentifier = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e",
                join("e.projects", "p",
                    on(
                            path("p.budget")
                        .greaterThan(
                            numeric("10000")
                        )
                    )
                ),
                joinFetch(collectionPath("p.managers"), nullExpression(),
                    onClause
                )
            ),
            where(path("e.id").greaterThan(numeric(-1)))
        );

        selectStatement.hasSpaceAfterFrom = false;
        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e " +
                       "FROM Employee e JOIN e.projects p ON p.budget > 10000 " +
                       "                JOIN FETCH p.managers ON SQRT(e.age) = 2 " +
                       "WHERE e.id > -1";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e",
                join("e.projects", "p",
                    on(
                            path("p.budget")
                        .greaterThan(
                            numeric("10000")
                        )
                    )
                ),
                joinFetch(collectionPath("p.managers"), nullExpression(),
                    on(sqrt(path("e.age")).equal(numeric(2)))
                )
            ),
            where(path("e.id").greaterThan(numeric(-1)))
        );

        testQuery(query, selectStatement);
    }
}
