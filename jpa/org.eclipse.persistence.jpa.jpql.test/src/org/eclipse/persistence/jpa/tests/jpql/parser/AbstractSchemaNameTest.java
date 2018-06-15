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
    public void test_JPQLQuery_01() throws Exception {

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
    public void test_JPQLQuery_02() throws Exception {

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
    public void test_JPQLQuery_03() throws Exception {

        String jpqlQuery = "select o from Order o";

        ExpressionTester selectStatement = selectStatement(
            select(variable("o")),
            from("Order", "o")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() throws Exception {

        String jpqlQuery = "select o from Order o order by o.name";

        ExpressionTester selectStatement = selectStatement(
            select(variable("o")),
            from("Order", "o"),
            orderBy(orderByItem("o.name"))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() throws Exception {

        String jpqlQuery = "select o from Order o where o.age > 18";

        ExpressionTester selectStatement = selectStatement(
            select(variable("o")),
            from("Order", "o"),
            where(path("o.age").greaterThan(numeric(18)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() throws Exception {

        String jpqlQuery = "select g from Group g";

        ExpressionTester selectStatement = selectStatement(
            select(variable("g")),
            from("Group", "g")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() throws Exception {

        String jpqlQuery = "select g from Group g where g.age > 18";

        ExpressionTester selectStatement = selectStatement(
            select(variable("g")),
            from("Group", "g"),
            where(path("g.age").greaterThan(numeric(18)))
        );

        testQuery(jpqlQuery, selectStatement);
    }
}
