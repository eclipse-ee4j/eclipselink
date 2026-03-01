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
public final class UpdateClauseTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "UPDATE ";

        UpdateClauseTester updateClause = update(nullExpression(), nullExpression());
        updateClause.hasSet              = false;
        updateClause.hasSpaceAfterSet    = false;
        updateClause.hasSpaceAfterUpdate = true;
        updateClause.hasSpaceAfterRangeVariableDeclaration = false;

        UpdateStatementTester updateStatement = updateStatement(updateClause);
        testInvalidQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "UPDATE SET";

        UpdateClauseTester updateClause = update(nullExpression(), nullExpression());
        updateClause.hasSpaceAfterUpdate = true;
        updateClause.hasSet              = true;
        updateClause.hasSpaceAfterSet    = false;
        updateClause.hasSpaceAfterRangeVariableDeclaration = false;

        UpdateStatementTester updateStatement = updateStatement(updateClause);
        testInvalidQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "UPDATE SET ";

        UpdateClauseTester updateClause = update(nullExpression(), nullExpression());
        updateClause.hasSpaceAfterUpdate = true;
        updateClause.hasSet              = true;
        updateClause.hasSpaceAfterSet    = true;
        updateClause.hasSpaceAfterRangeVariableDeclaration = false;

        UpdateStatementTester updateStatement = updateStatement(updateClause);
        testInvalidQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "UPDATE Employee";

        RangeVariableDeclarationTester rangeVariableDeclaration = rangeVariableDeclaration(
            abstractSchemaName("Employee"),
            virtualVariable("this")
        );
        rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName = false;
        rangeVariableDeclaration.hasSpaceAfterAs                 = false;

        UpdateClauseTester updateClause = update(rangeVariableDeclaration, nullExpression());
        updateClause.hasSet           = false;
        updateClause.hasSpaceAfterSet = false;
        updateClause.hasSpaceAfterRangeVariableDeclaration = false;

        UpdateStatementTester updateStatement = updateStatement(updateClause);
        testInvalidQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "UPDATE Employee ";

        RangeVariableDeclarationTester rangeVariableDeclaration = rangeVariableDeclaration(
            abstractSchemaName("Employee"),
            virtualVariable("this")
        );
        rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName = true;
        rangeVariableDeclaration.hasSpaceAfterAs                 = false;

        UpdateClauseTester updateClause = update(rangeVariableDeclaration, nullExpression());
        updateClause.hasSet              = false;
        updateClause.hasSpaceAfterSet    = false;
        updateClause.hasSpaceAfterRangeVariableDeclaration = false;

        UpdateStatementTester updateStatement = updateStatement(updateClause);
        testInvalidQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "UPDATE Employee AS";

        RangeVariableDeclarationTester rangeVariableDeclaration = rangeVariableDeclarationAs(
            abstractSchemaName("Employee"),
            virtualVariable("this")
        );
        rangeVariableDeclaration.hasSpaceAfterAs = false;

        UpdateClauseTester updateClause = update(rangeVariableDeclaration, nullExpression());
        updateClause.hasSet              = false;
        updateClause.hasSpaceAfterSet    = false;
        updateClause.hasSpaceAfterRangeVariableDeclaration = false;

        UpdateStatementTester updateStatement = updateStatement(updateClause);
        testInvalidQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "UPDATE Employee AS e";

        UpdateClauseTester updateClause = updateAs("Employee", "e", nullExpression());
        updateClause.hasSet              = false;
        updateClause.hasSpaceAfterSet    = false;
        updateClause.hasSpaceAfterRangeVariableDeclaration = false;

        UpdateStatementTester updateStatement = updateStatement(updateClause);
        testInvalidQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "UPDATE Employee AS SET";

        UpdateClauseTester updateClause = updateAs("Employee", "{this}");
        updateClause.hasSet              = true;
        updateClause.hasSpaceAfterSet    = false;
        updateClause.hasSpaceAfterRangeVariableDeclaration = false;

        UpdateStatementTester updateStatement = updateStatement(updateClause);
        testInvalidQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String query = "UPDATE Employee AS e SET";

        UpdateClauseTester updateClause = updateAs("Employee", "e", nullExpression());
        updateClause.hasSet           = true;
        updateClause.hasSpaceAfterSet = false;
        updateClause.hasSpaceAfterRangeVariableDeclaration = true;

        UpdateStatementTester updateStatement = updateStatement(updateClause);
        testInvalidQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_10() {

        String query = "UPDATE Employee AS SET ";

        UpdateClauseTester updateClause = updateAs("Employee", "{this}");
        updateClause.hasSet           = true;
        updateClause.hasSpaceAfterSet = true;
        updateClause.hasSpaceAfterRangeVariableDeclaration = false;

        UpdateStatementTester updateStatement = updateStatement(updateClause);
        testInvalidQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_11() {

        String query = "UPDATE Employee AS e SET e.name = 'Pascal'";

        UpdateStatementTester updateStatement = updateStatement(
            updateAs("Employee", "e", set("e.name", string("'Pascal'")))
        );

        testQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_12() {

        String query = "UPDATE Employee AS e SET e.name = 'Pascal',";

        CollectionExpressionTester collection = collection(
            new ExpressionTester[] { set("e.name", string("'Pascal'")), nullExpression() },
            new Boolean[] { Boolean.TRUE,  Boolean.FALSE},
            new Boolean[] { Boolean.FALSE, Boolean.FALSE}
        );

        UpdateStatementTester updateStatement = updateStatement(
            updateAs("Employee", "e", collection)
        );

        testInvalidQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_13() {

        String query = "UPDATE  SET";

        UpdateClauseTester updateClause = update(nullExpression(), nullExpression());
        updateClause.hasSet              = true;
        updateClause.hasSpaceAfterSet    = false;
        updateClause.hasSpaceAfterUpdate = true;
        updateClause.hasSpaceAfterRangeVariableDeclaration = false;

        UpdateStatementTester updateStatement = updateStatement(updateClause);
        testInvalidQuery(query, updateStatement);
    }

    @Test
    public void test_JPQLQuery_14() {

        String query = "UPDATE SET e.name = 'Pascal'";

        UpdateClauseTester updateClause = update(
            nullExpression(),
            set("e.name", string("'Pascal'"))
        );
        updateClause.hasSet              = true;
        updateClause.hasSpaceAfterSet    = true;
        updateClause.hasSpaceAfterUpdate = true;
        updateClause.hasSpaceAfterRangeVariableDeclaration = false;

        UpdateStatementTester updateStatement = updateStatement(updateClause);
        testInvalidQuery(query, updateStatement);
    }
}
