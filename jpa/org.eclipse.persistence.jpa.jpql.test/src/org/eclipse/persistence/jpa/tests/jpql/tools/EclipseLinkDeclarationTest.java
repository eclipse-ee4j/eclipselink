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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPQLQueryDeclaration.Type;
import org.eclipse.persistence.jpa.jpql.tools.EclipseLinkJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.RangeVariableDeclarationTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.SimpleSelectStatementTester;
import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkDeclarationTest extends DeclarationTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLQueryContext buildQueryContext() {
        return new EclipseLinkJPQLQueryContext(jpqlGrammar);
    }

    protected final SubqueryDeclarationTester subqueryDeclaration(SimpleSelectStatementTester subquery,
                                                                  String variableName) {

        SubqueryDeclarationTester declaration = new SubqueryDeclarationTester();

        if (variableName == null) {
            declaration.variableName   = ExpressionTools.EMPTY_STRING;
            declaration.baseExpression = rangeVariableDeclaration(sub(subquery));
        }
        else {

            RangeVariableDeclarationTester rangeVariableDeclaration = rangeVariableDeclaration(
                sub(subquery),
                variable(variableName)
            );

            declaration.baseExpression = rangeVariableDeclaration;

            if (variableName.length() == 0) {
                declaration.variableName = ExpressionTools.EMPTY_STRING;
                rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName = false;
            }
            else {
                declaration.variableName = variableName.toUpperCase().intern();
            }
        }

        declaration.declarationExpression = identificationVariableDeclaration(declaration.baseExpression);
        declaration.rootPath              = ExpressionTools.EMPTY_STRING;

        return declaration;
    }

    protected final TableDeclarationTester tableDeclaration(String tableName,
                                                            String variableName) {

        TableDeclarationTester declaration = new TableDeclarationTester();
        declaration.baseExpression         = table(tableName);
        declaration.rootPath               = tableName;

        if (variableName == null) {
            declaration.variableName           = ExpressionTools.EMPTY_STRING;
            declaration.declarationExpression  = tableVariableDeclaration(tableName);
        }
        else {
            declaration.declarationExpression  = tableVariableDeclaration(tableName, variableName);
            declaration.variableName           = variableName.toUpperCase().intern();
        }

        return declaration;
    }

    private void test_Declaration_001(boolean tolerant) throws Exception {

        String jpqlQuery = "select e from Employee e, TABLE('EMP') EMP";

        testDeclarations(
            jpqlQuery,
            tolerant,
            rangeDeclaration("Employee", "e"),
            tableDeclaration("'EMP'", "EMP")
        );
    }

    @Test
    public void test_Declaration_001_1() throws Exception {
        test_Declaration_001(false);
    }

    @Test
    public void test_Declaration_001_2() throws Exception {
        test_Declaration_001(true);
    }

    @Test
    public void test_Declaration_002() throws Exception {

        String jpqlQuery = "select e from Employee e, TABLE('EMP')";

        testDeclarations(
            jpqlQuery,
            true,
            rangeDeclaration("Employee", "e"),
            tableDeclaration("'EMP'", null)
        );
    }

    private void test_Declaration_003(boolean tolerant) throws Exception {

        String jpqlQuery = "select e from TABLE('EMP') EMP";

        testDeclarations(
            jpqlQuery,
            tolerant,
            tableDeclaration("'EMP'", "EMP")
        );
    }

    @Test
    public void test_Declaration_003_1() throws Exception {
        test_Declaration_003(false);
    }

    @Test
    public void test_Declaration_003_2() throws Exception {
        test_Declaration_003(true);
    }

    private void test_Declaration_004(boolean tolerant) throws Exception {

        String jpqlQuery = "select e from jpql.query.Employee emp";

        testDeclarations(
            jpqlQuery,
            tolerant,
            derivedRangeDeclaration("jpql.query.Employee", "emp")
        );
    }

    @Test
    public void test_Declaration_004_1() throws Exception {
        test_Declaration_004(false);
    }

    @Test
    public void test_Declaration_004_2() throws Exception {
        test_Declaration_004(true);
    }

    @Test
    public void test_Declaration_005() throws Exception {

        String jpqlQuery = "select e from jpql.query.Employee";

        testDeclarations(
            jpqlQuery,
            true,
            derivedRangeDeclaration("jpql.query.Employee", null)
        );
    }

    private void test_Declaration_006(boolean tolerant) throws Exception {

        String jpqlQuery = "select e from (select a from Address a) e";

        SimpleSelectStatementTester subquery = subquery(
            subSelect(variable("a")),
            subFrom("Address", "a")
        );

        testDeclarations(
            jpqlQuery,
            tolerant,
            subqueryDeclaration(subquery, "e")
        );
    }

    @Test
    public void test_Declaration_006_1() throws Exception {
        test_Declaration_006(false);
    }

    @Test
    public void test_Declaration_006_2() throws Exception {
        test_Declaration_006(true);
    }

    private void test_Declaration_007(boolean tolerant) throws Exception {

        String jpqlQuery = "select e from (select a from Address a)";

        SimpleSelectStatementTester subquery = subquery(
            subSelect(variable("a")),
            subFrom("Address", "a")
        );

        testDeclarations(
            jpqlQuery,
            tolerant,
            subqueryDeclaration(subquery, tolerant ? null : ExpressionTools.EMPTY_STRING)
        );
    }

    @Test
    public void test_Declaration_007_1() throws Exception {
        test_Declaration_007(false);
    }

    @Test
    public void test_Declaration_007_2() throws Exception {
        test_Declaration_007(true);
    }

    private void test_Declaration_008(boolean tolerant) throws Exception {

        String jpqlQuery = "select e from Employee e, (select a from Address a) a";

        SimpleSelectStatementTester subquery = subquery(
            subSelect(variable("a")),
            subFrom("Address", "a")
        );

        testDeclarations(
            jpqlQuery,
            tolerant,
            rangeDeclaration("Employee", "e"),
            subqueryDeclaration(subquery, "a")
        );
    }

    @Test
    public void test_Declaration_008_1() throws Exception {
        test_Declaration_008(false);
    }

    @Test
    public void test_Declaration_008_2() throws Exception {
        test_Declaration_008(true);
    }

    protected static class SubqueryDeclarationTester extends DeclarationTester {
        @Override
        protected Type getType() {
            return Type.SUBQUERY;
        }
    }

    protected static class TableDeclarationTester extends DeclarationTester {
        @Override
        protected Type getType() {
            return Type.TABLE;
        }
    }
}
