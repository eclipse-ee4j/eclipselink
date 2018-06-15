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
public final class SimpleSelectStatementTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e WHERE (SELECT e.age FROM Employee e) > 10";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    sub(subquery(subSelect(path("e.age")), subFrom("Employee", "e")))
                .greaterThan(
                    numeric(10)
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e WHERE EXISTS(SELECT ";

        SimpleSelectClauseTester subSelectClause = subSelect(nullExpression());
        subSelectClause.hasSpaceAfterSelect = true;

        SimpleSelectStatementTester subquery = subquery(subSelectClause, nullExpression());

        ExistsExpressionTester existsExpression = exists(subquery);
        existsExpression.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(existsExpression)
        );

        testInvalidQuery(query, selectStatement);
    }
}
