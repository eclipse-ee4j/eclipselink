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
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * Unit-tests that make sure nested array are properly parsed.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkInExpressionTest extends JPQLParserTest {

    @Test
    public final void test_JPQLQuery_01() {

        String jpqlQuery = "Select e from Employee e where (e.id1, e.id2) IN ((:id1, :id2), (:id3, :id4))";

        ExpressionTester select = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    array(path("e.id1"), path("e.id2")).
                in(
                    array(
                        inputParameter(":id1"),
                        inputParameter(":id2")
                    ),
                    array(
                        inputParameter(":id3"),
                        inputParameter(":id4")
                    )
                )
            )
        );

        testQuery(jpqlQuery, select);
    }

    @Test
    public final void test_JPQLQuery_02() {

        String jpqlQuery = "Select e from Employee e where (e.id1, e.id2) IN :ids";

        ExpressionTester select = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                array(path("e.id1"), path("e.id2")).in(":ids")
            )
        );

        testQuery(jpqlQuery, select);
    }
}
