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
public final class EclipseLinkLikeExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "Select e from Employee e where UPPER(e.firstName) like UPPER('b%')";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    upper(path("e.firstName"))
                .like(
                    upper(string("'b%'"))
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "Select e from Employee e where UPPER(e.firstName) like UPPER('b%') ESCAPE ' '";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    upper(path("e.firstName"))
                .like(
                    upper(string("'b%'")),
                    string("' '")
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "Select e from Employee e where UPPER(e.firstName) like UPPER('b%') + 2";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    upper(path("e.firstName"))
                .like(
                    upper(string("'b%'")).add(numeric(2))
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "Select e from Employee e where UPPER(e.firstName) like UPPER('b%') + 2 ESCAPE ' '";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    upper(path("e.firstName"))
                .like(
                    upper(string("'b%'")).add(numeric(2)),
                    string("' '")
                )
            )
        );

        testQuery(query, selectStatement);
    }
}
