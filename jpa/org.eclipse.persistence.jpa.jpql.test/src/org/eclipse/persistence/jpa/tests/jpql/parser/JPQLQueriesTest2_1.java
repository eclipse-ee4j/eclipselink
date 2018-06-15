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
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries2_1.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLQueriesTest2_1 extends JPQLParserTest {

    @Test
    public void test_Query_001() throws Exception {

        // Select e
        // From Employee e Join TREAT(e.projects AS LargeProject) lp
        // Where lp.budget = :value

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", joinTreatAs("e.projects", "LargeProject", "lp")),
            where(path("lp.budget").equal(inputParameter(":value")))
        );

        testQuery(query_001(), selectStatement);
    }

    @Test
    public void test_Query_002() throws Exception {

        // Select e
        // From Employee e Join TREAT(e.projects LargeProject) lp

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", joinTreat("e.projects", "LargeProject", "lp"))
        );

        testQuery(query_002(), selectStatement);
    }

    @Test
    public void test_Query_003() throws Exception {

        // SELECT TREAT(TREAT(p.project LargeProject).parent AS LargeProject).endDate
      // FROM Product p

        ExpressionTester selectStatement = selectStatement(
            select(
                path(
                    treatAs(
                        path(treat("p.project", "LargeProject"), "parent"),
                        entity("LargeProject")
                    ),
                    "endDate"
                )
            ),
            from("Product", "p")
        );

        testQuery(query_003(), selectStatement);
    }
}
