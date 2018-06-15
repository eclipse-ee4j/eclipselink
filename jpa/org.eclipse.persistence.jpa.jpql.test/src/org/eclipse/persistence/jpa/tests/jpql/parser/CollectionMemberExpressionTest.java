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
public final class CollectionMemberExpressionTest extends JPQLParserTest {
    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e, f FROM Employee e, IN(e.employees) f WHERE e.name MEMBER f.offices";

        ExpressionTester selectStatement = selectStatement(
            select(
                variable("e"),
                variable("f")
            ),
            from(
                identificationVariableDeclaration("Employee", "e"),
                fromIn("e.employees", "f")
            ),
            where(
                    path("e.name")
                .member(
                    collectionPath("f.offices")
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e, f FROM Employee e, IN(e.employees) f WHERE e.name MEMBER OF e.employees";

        ExpressionTester selectStatement = selectStatement(
            select(
                variable("e"),
                variable("f")
            ),
            from(
                identificationVariableDeclaration("Employee", "e"),
                fromIn("e.employees", "f")
            ),
            where(
                    path("e.name")
                .memberOf(
                    collectionPath("e.employees")
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e, f FROM Employee e, IN(e.employees) f WHERE e.name NOT MEMBER OF e.employees";

        ExpressionTester selectStatement = selectStatement(
            select(
                variable("e"),
                variable("f")
            ),
            from(
                identificationVariableDeclaration("Employee", "e"),
                fromIn("e.employees", "f")
            ),
            where(
                    path("e.name")
                .notMemberOf(
                    collectionPath("e.employees")
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE MEMBER";

        CollectionMemberExpressionTester member = member(nullExpression(), nullExpression());
        member.hasSpaceAfterMember = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(member)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e WHERE NOT MEMBER";

        CollectionMemberExpressionTester member = notMember(nullExpression(), nullExpression());
        member.hasSpaceAfterMember = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(member)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM Employee e WHERE MEMBER OF";

        CollectionMemberExpressionTester member = memberOf(nullExpression(), nullExpression());
        member.hasSpaceAfterOf = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(member)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e FROM Employee e WHERE NOT MEMBER OF";

        CollectionMemberExpressionTester member = notMemberOf(nullExpression(), nullExpression());
        member.hasSpaceAfterOf = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(member)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "SELECT e FROM Employee e WHERE NOT MEMBER OF ";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(notMemberOf(nullExpression(), nullExpression()))
        );

        testInvalidQuery(query, selectStatement);
    }
}
