/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for JPA 2.2, 3.0 and 3.1
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.Test;

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_1.query_Ceiling;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries3_1.query_Floor;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.ceiling;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.selectStatement;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

public class JPQLQueriesTest3_1 extends JPQLParserTest {

    @Test
    public void test_Query_Ceiling() throws Exception {
        // Select CEILING(o.totalPrice), o.totalPrice FROM Order o
        ExpressionTester selectStatement = selectStatement(
                select(path("o.totalPrice"), ceiling("o.totalPrice")),
                from("Order", "o")
        );
        try {
            testQuery(query_Ceiling(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Test
    public void test_Query_Floor() throws Exception {
        // Select FLOOR(o.totalPrice), o.totalPrice FROM Order o
        ExpressionTester selectStatement = selectStatement(
                select(path("o.totalPrice"), floor("o.totalPrice")),
                from("Order", "o")
        );
        try {
            testQuery(query_Floor(), selectStatement);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

}
