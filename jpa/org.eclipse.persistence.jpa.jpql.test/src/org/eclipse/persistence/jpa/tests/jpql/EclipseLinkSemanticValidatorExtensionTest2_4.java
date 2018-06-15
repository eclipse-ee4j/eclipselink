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
package org.eclipse.persistence.jpa.tests.jpql;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractSemanticValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkSemanticValidatorExtension;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.tools.EclipseLinkJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.EclipseLinkSemanticValidator;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;
import org.eclipse.persistence.jpa.tests.jpql.tools.AbstractSemanticValidatorTest;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * The unit-test class used for testing a JPQL query semantically when EclipseLink is the
 * persistence provider and with an {@link EclipseLinkSemanticValidatorExtension}.
 *
 * @version 2.5.1
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkSemanticValidatorExtensionTest2_4 extends AbstractSemanticValidatorTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLQueryContext buildQueryContext() {
        return new EclipseLinkJPQLQueryContext(jpqlGrammar);
    }

    private EclipseLinkSemanticValidatorExtension buildSemanticExtension() {
        return new EclipseLinkSemanticValidatorExtension() {
            public boolean columnExists(String tableName, String columnName) {
                return columnNames(tableName).contains(columnName);
            }
            public String getEntityTable(String entityName) {
                if ("Employee".equals(entityName)) {
                    return "EMPLOYEE";
                }
                return null;
            }
            public boolean tableExists(String tableName) {
                return tableNames().contains(tableName);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractSemanticValidator buildValidator() {
        return new EclipseLinkSemanticValidator(
            buildSemanticValidatorHelper(),
            buildSemanticExtension()
        );
    }

    private List<String> columnNames(String tableName) {

        List<String> columnNames = new ArrayList<String>();

        if ("EMPLOYEE".equals(tableName)) {
            columnNames.add("ADDRESS");
            columnNames.add("EMPLOYEE_ID");
            columnNames.add("FIRST_NAME");
            columnNames.add("LAST_NAME");
            columnNames.add("MANAGER_ID");
        }
        else if ("ADDRESS".equals(tableName)) {
            columnNames.add("ADDRESS_ID");
            columnNames.add("APT_NUMBER");
            columnNames.add("COUNTRY");
            columnNames.add("STREET");
            columnNames.add("ZIP_CODE");
        }

        return columnNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isComparisonTypeChecked() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isPathExpressionToCollectionMappingAllowed() {
        return true;
    }

    private List<String> tableNames() {

        List<String> tableNames = new ArrayList<String>();
        tableNames.add("ADDRESS");
        tableNames.add("EMPLOYEE");
        tableNames.add("EMPLOYEE_SEQ");
        tableNames.add("MANAGER");
        tableNames.add("DEPARTMENT");

        return tableNames;
    }

    @Test
    public final void test_FunctionExpression_UnknownColumn_01() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE COLUMN('MANAGER_ID', e) = :id";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_FunctionExpression_UnknownColumn_02() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE COLUMN('MGR', e) = :id";
        int startPosition = "SELECT e FROM Employee e WHERE COLUMN(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE COLUMN('MGR'".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            FunctionExpression_UnknownColumn,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_UnknownColumn_01() throws Exception {

        String jpqlQuery  = "select e from Employee e, table('EMPLOYEE') EMP where e.name = EMP.LAST_NAME";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_StateFieldPathExpression_UnknownColumn_02() throws Exception {

        String jpqlQuery  = "select e from Employee e, table('EMPLOYEE') EMP where e.name = EMP.NAME";
        int startPosition = "select e from Employee e, table('EMPLOYEE') EMP where e.name = ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            StateFieldPathExpression_UnknownColumn,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_UnknownColumn_03() throws Exception {

        String jpqlQuery  = "select e from Employee e, table('ADDRESS') ADDR where (select a from Address a where a.zip = ADDR.ZIP_CODE) is not null";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_StateFieldPathExpression_UnknownColumn_04() throws Exception {

        String jpqlQuery  = "select e from Employee e, table('ADDRESS') ADDR where (select a from Address a where a.zip = ADDR.CODE) is not null";
        int startPosition = "select e from Employee e, table('ADDRESS') ADDR where (select a from Address a where a.zip = ".length();
        int endPosition   = "select e from Employee e, table('ADDRESS') ADDR where (select a from Address a where a.zip = ADDR.CODE".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            StateFieldPathExpression_UnknownColumn,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_TableExpression_InvalidTableName_01() throws Exception {

        String jpqlQuery  = "select e from Employee e, table('EMPLOYEE') EMP";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_TableExpression_InvalidTableName_02() throws Exception {

        String jpqlQuery  = "select e from Employee e, table('EMP') EMP";
        int startPosition = "select e from Employee e, table(".length();
        int endPosition   = "select e from Employee e, table('EMP'".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            TableExpression_InvalidTableName,
            startPosition,
            endPosition
        );
    }
}
