/*
 * Copyright (c) 2006, 2024 Oracle and/or its affiliates. All rights reserved.
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
public final class UpdateItemTest extends JPQLParserTest {

    private JPQLQueryStringFormatter buildQueryFormatter_1() {
        return new JPQLQueryStringFormatter() {
            @Override
            public String format(String query) {
                return query.replace("AVG", "avg");
            }
        };
    }

    @Test
    public void test_JPQLQuery_01() {

        String jpqlQuery = "UPDATE Employee e SET e.name = 'Pascal'";

        UpdateStatementTester updateStatement = updateStatement(
            update(
                "Employee", "e",
                set("e.name", string("'Pascal'"))
            )
        );

        testQuery(jpqlQuery, updateStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String jpqlQuery = "UPDATE Employee e SET e.name = 'Pascal', e.manager.salary = 100000";

        UpdateStatementTester updateStatement = updateStatement(
            update(
                "Employee", "e",
                set("e.name", string("'Pascal'")),
                set("e.manager.salary", numeric(100000))
            )
        );

        testQuery(jpqlQuery, updateStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String jpqlQuery = "UPDATE Employee SET AVG(2) = 'Pascal'";

        UpdateStatementTester updateStatement = updateStatement(
            update(
                "Employee", "this",
                set(bad(avg(numeric(2))), string("'Pascal'")), false
            )
        );

        testInvalidQuery(jpqlQuery, updateStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String jpqlQuery = "UPDATE Employee SET avg = 'Pascal'";

        UpdateStatementTester updateStatement = updateStatement(
            update(
                "Employee", "this",
                set("{this}.avg", string("'Pascal'")), false
            )
        );

        testInvalidQuery(jpqlQuery, updateStatement, buildQueryFormatter_1());
    }
}
