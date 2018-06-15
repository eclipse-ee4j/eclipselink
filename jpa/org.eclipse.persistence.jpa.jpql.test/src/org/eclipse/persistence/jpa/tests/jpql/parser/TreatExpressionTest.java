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

import org.eclipse.persistence.jpa.tests.jpql.UniqueSignature;
import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries2_1.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

@UniqueSignature
@SuppressWarnings("nls")
public final class TreatExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() throws Exception {

        // Select e
        // From Employee e Join TREAT(e.projects AS LargeProject) lp
        // Where lp.budget = :value

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", join(treatAs("e.projects", "LargeProject"), "lp")),
            where(path("lp.budget").equal(inputParameter(":value")))
        );

        testQuery(query_001(), selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() throws Exception {

        // Select e
        // From Employee e Join TREAT(e.projects LargeProject) lp

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", join(treat("e.projects", "LargeProject"), "lp"))
        );

        testQuery(query_002(), selectStatement);
    }
}
