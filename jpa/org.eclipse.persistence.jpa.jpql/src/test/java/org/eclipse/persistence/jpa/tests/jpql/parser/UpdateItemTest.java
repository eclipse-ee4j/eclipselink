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
public final class UpdateItemTest extends JPQLParserTest {

    private JPQLQueryStringFormatter buildQueryFormatter_1() {
        return new JPQLQueryStringFormatter() {
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
                "Employee",
                set(bad(avg(numeric(2))), string("'Pascal'"))
            )
        );

        testInvalidQuery(jpqlQuery, updateStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String jpqlQuery = "UPDATE Employee SET avg = 'Pascal'";

        UpdateStatementTester updateStatement = updateStatement(
            update(
                "Employee",
                set("{employee}.avg", string("'Pascal'"))
            )
        );

        testInvalidQuery(jpqlQuery, updateStatement, buildQueryFormatter_1());
    }
}
