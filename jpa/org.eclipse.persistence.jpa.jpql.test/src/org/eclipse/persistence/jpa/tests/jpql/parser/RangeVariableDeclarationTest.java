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
public final class RangeVariableDeclarationTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(identificationVariableDeclaration(rangeVariableDeclaration("Employee", "e")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee AS e";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(identificationVariableDeclaration(rangeVariableDeclarationAs("Employee", "e")))
        );

        testQuery(query, selectStatement);
    }
}
