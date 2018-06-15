/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

/**
 * This test suite contains a series of unit-tests that test parsing JPQL queries that follows the
 * JPQL grammar defined in JPA 2.0 and the additional support provider by EclipseLink version 2.1.
 *
 * @version 2.5.1
 * @since 2.5.1
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkJPQLParserTests2_1 extends JPQLParserTest {

    @Test
    public void test_Query_001() {

        String jpqlQuery = "Select e from Employee e order by e.age + 2";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            orderBy(
                orderByItem(path("e.age").add(numeric(2)))
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_Query_002() {

        String jpqlQuery = "Select e from Employee e order by SUBSTRING(e.age, 1) + 2";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            orderBy(
                orderByItem(
                    substring(path("e.age"), numeric(1)).add(numeric(2))
                )
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_Query_003() {

        String jpqlQuery = "Select e from Employee e order by e.age + 2 ASC";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            orderBy(
                orderByItemAsc(path("e.age").add(numeric(2)))
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }
}
