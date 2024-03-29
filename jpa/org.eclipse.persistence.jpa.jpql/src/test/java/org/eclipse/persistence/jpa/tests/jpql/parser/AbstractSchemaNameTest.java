/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * Unit-tests for {@link org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName AbstractSchemaName}.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class AbstractSchemaNameTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String jpqlQuery = "SELECT e FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                identificationVariableDeclaration(rangeVariableDeclaration(abstractSchemaName("Employee"), variable("e")))
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String jpqlQuery = "SELECT e FROM Employee e, Address a";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                identificationVariableDeclaration(rangeVariableDeclaration(abstractSchemaName("Employee"), variable("e"))),
                identificationVariableDeclaration(rangeVariableDeclaration(abstractSchemaName("Address"), variable("a")))
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String jpqlQuery = "select o from Order o";

        ExpressionTester selectStatement = selectStatement(
            select(variable("o")),
            from("Order", "o")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String jpqlQuery = "select o from Order o order by o.name";

        ExpressionTester selectStatement = selectStatement(
            select(variable("o")),
            from("Order", "o"),
            orderBy(orderByItem("o.name"))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String jpqlQuery = "select o from Order o where o.age > 18";

        ExpressionTester selectStatement = selectStatement(
            select(variable("o")),
            from("Order", "o"),
            where(path("o.age").greaterThan(numeric(18)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String jpqlQuery = "select g from Group g";

        ExpressionTester selectStatement = selectStatement(
            select(variable("g")),
            from("Group", "g")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String jpqlQuery = "select g from Group g where g.age > 18";

        ExpressionTester selectStatement = selectStatement(
            select(variable("g")),
            from("Group", "g"),
            where(path("g.age").greaterThan(numeric(18)))
        );

        testQuery(jpqlQuery, selectStatement);
    }
}
