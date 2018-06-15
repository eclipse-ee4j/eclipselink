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
public final class CollectionMemberDeclarationTest extends JPQLParserTest {

    private JPQLQueryStringFormatter buildQueryFormatter_09() {
        return new JPQLQueryStringFormatter() {
            public String format(String query) {
                return query.replace("IN(AS", "IN( AS");
            }
        };
    }

    @Test
    public void test_JPQLQuery_01() {

        String query = " SELECT e  FROM  IN ( e.address.street  )  AS emp ";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(fromInAs("e.address.street", "emp"))
        );

        selectStatement.hasSpaceAfterFrom = true;

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM IN(e.address.street) emp";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(fromIn("e.address.street", "emp"))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM IN(e.address.street) emp, IN(e.name) AS name";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                fromIn("e.address.street", "emp"),
                fromInAs("e.name", "name")
            )
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM IN";

        CollectionMemberDeclarationTester in = fromIn(nullExpression(), nullExpression());
        in.hasLeftParenthesis = false;
        in.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(in)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM IN(";
        CollectionMemberDeclarationTester in = fromIn(nullExpression(), nullExpression());
        in.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(in)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM IN()";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(fromIn(nullExpression(), nullExpression()))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e FROM IN() AS";

        CollectionMemberDeclarationTester in = fromInAs(nullExpression(), nullExpression());
        in.hasSpaceAfterAs = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(in)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "SELECT e FROM IN AS";

        CollectionMemberDeclarationTester in = fromInAs(nullExpression(), nullExpression());
        in.hasLeftParenthesis            = false;
        in.hasRightParenthesis           = false;
        in.hasSpaceAfterAs               = false;
        in.hasSpaceAfterIn               = true;
        in.hasSpaceAfterRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(in)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String query = "SELECT e FROM IN( AS";

        CollectionMemberDeclarationTester in = fromInAs(nullExpression(), nullExpression());
        in.hasLeftParenthesis  = true;
        in.hasRightParenthesis = false;
        in.hasSpaceAfterAs     = false;
        in.hasSpaceAfterRightParenthesis = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(in)
        );

        testInvalidQuery(query, selectStatement, buildQueryFormatter_09());
    }

    @Test
    public void test_JPQLQuery_10() {

        String query = "SELECT e FROM IN) AS";

        CollectionMemberDeclarationTester in = fromInAs(nullExpression(), nullExpression());
        in.hasLeftParenthesis  = false;
        in.hasRightParenthesis = true;
        in.hasSpaceAfterAs     = false;
        in.hasSpaceAfterRightParenthesis = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(in)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_11() {

        String query = "SELECT e FROM IN AS employees";

        CollectionMemberDeclarationTester in = fromInAs(nullExpression(), variable("employees"));
        in.hasSpaceAfterIn               = true;
        in.hasLeftParenthesis            = false;
        in.hasRightParenthesis           = false;
        in.hasSpaceAfterAs               = true;
        in.hasSpaceAfterRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(in)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_12() {

        String query = "SELECT e FROM IN() AS ";

        CollectionMemberDeclarationTester in = fromInAs(nullExpression(), nullExpression());
        in.hasLeftParenthesis  = true;
        in.hasRightParenthesis = true;
        in.hasSpaceAfterAs     = true;
        in.hasSpaceAfterRightParenthesis = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(in)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_14() {

        String query = "SELECT a FROM Address a WHERE EXISTS (SELECT e FROM Employee e, IN a.customerList)";

        ExpressionTester selectStatement = selectStatement(
            select(variable("a")),
            from("Address", "a"),
            where(
                exists(
                    subquery(
                        subSelect(variable("e")),
                        subFrom(
                            identificationVariableDeclaration("Employee", "e"),
                            subFromIn("a.customerList")
                        )
                    )
                )
            )
        );

        testQuery(query, selectStatement);
    }
}
