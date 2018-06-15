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
public final class StateFieldPathExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT SUM(e.manager.name) FROM Employee e";

        SelectStatementTester selectStatement = selectStatement(
            select(sum("e.manager.name")),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT SUM(e.manager.) FROM Employee e";

        SelectStatementTester selectStatement = selectStatement(
            select(sum("e.manager.")),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT c.creditCard.creditCompany.address.city FROM Customer c";

        SelectStatementTester selectStatement = selectStatement(
            select(path("c.creditCard.creditCompany.address.city")),
            from("Customer", "c")
        );

        testQuery(query, selectStatement);
    }
}
