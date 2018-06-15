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
public final class KeywordExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "UPDATE Employee e SET e.isEnrolled = TRUE";

        UpdateStatementTester updateStatement = updateStatement(
            update("Employee", "e", set("e.isEnrolled", TRUE()))
        );

        testQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "UPDATE Employee e SET e.isEnrolled = FALSE";

        UpdateStatementTester updateStatement = updateStatement(
            update("Employee", "e", set("e.isEnrolled", FALSE()))
        );

        testQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "UPDATE Employee e SET e.manager = NULL";

        UpdateStatementTester updateStatement = updateStatement(
            update("Employee", "e", set("e.manager", NULL()))
        );

        testQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE e.hired = TRUE";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.hired").equal(TRUE()))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e WHERE TRUE";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(TRUE())
        );

        testQuery(query, selectStatement);
    }
}
