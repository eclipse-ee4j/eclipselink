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
public final class NullComparisonExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NULL";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(isNull(path("e.name")))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IS NOT NULL";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(isNotNull(path("e.name")))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE IS NULL";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(isNull(nullExpression()))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE IS NULL HAVING e.adult";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(isNull(nullExpression())),
            having(path("e.adult"))
        );

        testQuery(jpqlQuery, selectStatement);
    }
}
