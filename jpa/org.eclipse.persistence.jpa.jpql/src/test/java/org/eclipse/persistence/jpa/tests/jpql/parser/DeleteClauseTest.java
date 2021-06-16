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
public final class DeleteClauseTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "DELETE FROM";

        DeleteClauseTester deleteClause = delete(nullExpression());
        deleteClause.hasSpaceAfterFrom = false;

        DeleteStatementTester deleteStatement = deleteStatement(deleteClause);
        testInvalidQuery(query, deleteStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "DELETE FROM ";

        DeleteStatementTester deleteStatement = deleteStatement(delete(nullExpression()));
        testInvalidQuery(query, deleteStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "DELETE FROM WHERE ";

        WhereClauseTester whereClause = where(nullExpression());
        whereClause.hasSpaceAfterIdentifier = true;

        DeleteStatementTester deleteStatement = deleteStatement(
            delete(nullExpression()),
            whereClause
        );

        deleteStatement.hasSpaceAfterDeleteClause = false;

        testInvalidQuery(query, deleteStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "DELETE FROM Employee";

        RangeVariableDeclarationTester rangeVariableDeclaration = rangeVariableDeclaration(
            abstractSchemaName("Employee"),
            virtualVariable("employee")
        );
        rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName = false;

        DeleteStatementTester deleteStatement = deleteStatement(
            delete(rangeVariableDeclaration)
        );

        testInvalidQuery(query, deleteStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "DELETE FROM Employee ";

        DeleteStatementTester deleteStatement = deleteStatement(
            delete(
                rangeVariableDeclaration(
                    abstractSchemaName("Employee"),
                    virtualVariable("employee")
                )
            )
        );

        testInvalidQuery(query, deleteStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "DELETE FROM Employee AS";

        RangeVariableDeclarationTester rangeVariableDeclaration = rangeVariableDeclarationAs(
            abstractSchemaName("Employee"),
            virtualVariable("employee")
        );
        rangeVariableDeclaration.hasSpaceAfterAs = false;

        DeleteStatementTester deleteStatement = deleteStatement(
            delete(rangeVariableDeclaration)
        );

        testInvalidQuery(query, deleteStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "DELETE FROM Employee AS e";

        DeleteStatementTester deleteStatement = deleteStatement(
            deleteAs("Employee", "e")
        );

        testInvalidQuery(query, deleteStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "DELETE FROM Employee AS WHERE";

        DeleteStatementTester deleteStatement = deleteStatement(
            deleteAs(abstractSchemaName("Employee"), virtualVariable("employee")),
            where(nullExpression())
        );

        deleteStatement.hasSpaceAfterDeleteClause = false;

        testInvalidQuery(query, deleteStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String query = "DELETE FROM Employee AS e WHERE";

        DeleteStatementTester deleteStatement = deleteStatement(
            deleteAs("Employee", "e"),
            where(nullExpression())
        );

        testInvalidQuery(query, deleteStatement);
    }

    @Test
    public void test_JPQLQuery_10() {

        String query = "DELETE FROM Employee AS WHERE ";

        WhereClauseTester whereClause = where(nullExpression());
        whereClause.hasSpaceAfterIdentifier = true;

        DeleteStatementTester deleteStatement = deleteStatement(
            deleteAs(abstractSchemaName("Employee"), virtualVariable("employee")),
            whereClause
        );

        deleteStatement.hasSpaceAfterDeleteClause = false;

        testInvalidQuery(query, deleteStatement);
    }

    @Test
    public void test_JPQLQuery_11() {

        String query = "DELETE FROM Employee AS e WHERE e.name = 'Pascal'";

        DeleteStatementTester deleteStatement = deleteStatement(
            deleteAs("Employee", "e"),
            where(path("e.name").equal(string("'Pascal'")))
        );

        testQuery(query, deleteStatement);
    }

    @Test
    public void test_JPQLQuery_12() {

        String query = "DELETE FROM  WHERE";

        WhereClauseTester whereClause = where(nullExpression());
        whereClause.hasSpaceAfterIdentifier = false;

        DeleteStatementTester deleteStatement = deleteStatement(
            delete(nullExpression()),
            whereClause
        );

        deleteStatement.hasSpaceAfterDeleteClause = false;

        testInvalidQuery(query, deleteStatement);
    }

    @Test
    public void test_JPQLQuery_13() {

        String query = "DELETE FROM WHERE e.name = 'Pascal'";

        DeleteStatementTester deleteStatement = deleteStatement(
            delete(nullExpression()),
            where(path("e.name").equal(string("'Pascal'")))
        );

        deleteStatement.hasSpaceAfterDeleteClause = false;

        testInvalidQuery(query, deleteStatement);
    }
}
