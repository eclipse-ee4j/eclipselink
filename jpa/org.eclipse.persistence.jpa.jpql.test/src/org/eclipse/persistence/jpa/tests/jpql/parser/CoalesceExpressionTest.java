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
public final class CoalesceExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {
        String query = "SELECT COALESCE(e.age, e.name) FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(coalesce(path("e.age"), path("e.name"))),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {
        String query = "SELECT e FROM Employee e WHERE COALESCE(e.age, 20) + 20";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    coalesce(path("e.age"), numeric(20))
                .add(
                    numeric(20)
                )
            )
        );

        testQuery(query, selectStatement);
    }
}
