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

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

@SuppressWarnings("nls")
public final class LikeExpressionTest extends JPQLParserTest {

    protected boolean isEclipseLink2_1OrNewer() {

        String version = getGrammar().getProviderVersion();

        if (ExpressionTools.stringIsEmpty(version)) {
            return false;
        }

        EclipseLinkVersion currentVersion = EclipseLinkVersion.value(getGrammar().getProviderVersion());
        return currentVersion.isNewerThanOrEqual(EclipseLinkVersion.VERSION_2_1);
    }

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' LIKE 'Pascal'";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(string("'Pascal''s code'").like(string("'Pascal'")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' NOT LIKE 'Pascal'";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(string("'Pascal''s code'").notLike(string("'Pascal'")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' LIKE 'Pascal' ESCAPE 'p'";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(string("'Pascal''s code'").like(string("'Pascal'"), string('p')))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' NOT LIKE 'Pascal' ESCAPE 'p'";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(string("'Pascal''s code'").notLike(string("'Pascal'"), string('p')))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' NOT LIKE 'Pascal' ESCAPE";

        LikeExpressionTester notLikeExpression = notLike(
            string("'Pascal''s code'"),
            string("'Pascal'")
        );
        notLikeExpression.hasEscape = true;
        notLikeExpression.hasSpaceAfterEscape = false;
        notLikeExpression.hasSpaceAfterPatternValue = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(notLikeExpression)
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' NOT LIKE 'Pascal' 'p'";

        LikeExpressionTester notLikeExpression = notLike(
            string("'Pascal''s code'"),
            string("'Pascal'"),
            string('p')
        );
        notLikeExpression.hasEscape = false;
        notLikeExpression.hasSpaceAfterEscape = false;
        notLikeExpression.hasSpaceAfterPatternValue = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(notLikeExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' LIKE 'Pascal' ESCAPE TRUE GROUP BY e";

        LikeExpressionTester likeExpression = like(
            string("'Pascal''s code'"),
            string("'Pascal'"),
            isEclipseLink2_1OrNewer() ? TRUE() : bad(TRUE())
        );

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(likeExpression),
            groupBy(variable("e"))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' LIKE 'Pascal' ESCAPE TRUE AND e.name = 'Pascal' GROUP BY e";

        LikeExpressionTester likeExpression = like(
            string("'Pascal''s code'"),
            string("'Pascal'"),
            isEclipseLink2_1OrNewer() ? TRUE() : bad(TRUE())
        );

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    likeExpression
                .and(
                    path("e.name").equal(string("'Pascal'"))
                )
            ),
            groupBy(variable("e"))
        );

        testInvalidQuery(query, selectStatement);
    }
}
