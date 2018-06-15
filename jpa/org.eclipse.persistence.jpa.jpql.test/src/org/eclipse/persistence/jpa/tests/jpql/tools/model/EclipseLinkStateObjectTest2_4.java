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
package org.eclipse.persistence.jpa.tests.jpql.tools.model;

import org.eclipse.persistence.jpa.tests.jpql.EclipseLinkJPQLQueries2_4;
import org.eclipse.persistence.jpa.tests.jpql.JPQLQueries2_1;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

@SuppressWarnings("nls")
public final class EclipseLinkStateObjectTest2_4 extends EclipseLinkJPQLStateObjectTest {

    @Test
    public void test_Query_001() throws Exception {

        // SELECT FUNC('NVL', e.firstName, 'NoFirstName'),
        //        func('NVL', e.lastName,  'NoLastName')
        // FROM Employee e

        testQuery(EclipseLinkJPQLQueries2_4.query_001(), stateObject_224());
    }

    @Test
    public void test_Query_002() throws Exception {

        // SELECT a
        // FROM Asset a, Geography selectedGeography
      // WHERE selectedGeography.id = :id AND
      //       a.id IN (:id_list) AND
      //       FUNC('ST_Intersects', a.geometry, selectedGeography.geometry) = 'TRUE'

        StateObjectTester selectStatement = selectStatement(
            select(variable("a")),
            from("Asset", "a", "Geography", "selectedGeography"),
            where(
                    path("selectedGeography.id").equal(inputParameter(":id"))
                .and(
                    path("a.id").in(inputParameter(":id_list"))
                ).and(
                        function(FUNC, "ST_Intersects", path("a.geometry"), path("selectedGeography.geometry"))
                    .equal(
                        string("'TRUE'"))
                )
            )
        );

        testQuery(EclipseLinkJPQLQueries2_4.query_002(), selectStatement);
    }

    @Test
    public void test_Query_JPQL_2_1_001() throws Exception {

        // Select e
        // From Employee e Join TREAT(e.projects AS LargeProject) lp
        // Where lp.budget = :value

        StateObjectTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", join(treatAs("e.projects", "LargeProject"), "lp")),
            where(path("lp.budget").equal(inputParameter(":value")))
        );

        testQuery(JPQLQueries2_1.query_001(), selectStatement);
    }

    @Test
    public void test_Query_JPQL_2_1_002() throws Exception {

        // Select e
        // From Employee e Join TREAT(e.projects LargeProject) lp

        StateObjectTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", join(treat("e.projects", "LargeProject"), "lp"))
        );

        testQuery(JPQLQueries2_1.query_002(), selectStatement);
    }
}
