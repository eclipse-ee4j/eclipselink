/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
