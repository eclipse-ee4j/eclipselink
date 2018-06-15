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
package org.eclipse.persistence.jpa.tests.jpql;

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
 * The unit-test class used for testing a JPQL query semantically when the JPA version is 1.0 and 2.0
 * and EclipseLink is the persistence provider. The EclipseLink version supported is 2.4 only.
 *
 * @version 2.5.1
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkSemanticValidatorTest2_4 extends AbstractSemanticValidatorTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLQueryContext buildQueryContext() {
        return new EclipseLinkJPQLQueryContext(jpqlGrammar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractSemanticValidator buildValidator() {
        return new EclipseLinkSemanticValidator(
            buildSemanticValidatorHelper(),
            EclipseLinkSemanticValidatorExtension.NULL_EXTENSION
        );
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

    @Test
    public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_07() throws Exception {

        String jpqlQuery = "select e from (select e2 from Employee e2) e";
        int startPosition = "select e from ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractFromClause_InvalidFirstIdentificationVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_08() throws Exception {

        String jpqlQuery = "select e3 from Employee e, (select e2 from Employee e2) e3, IN(e.phoneNumbers) p";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_09() throws Exception {

        String jpqlQuery = "select EMP from TABLE('EMPLOYEE') EMP";
        int startPosition = "select EMP from ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractFromClause_InvalidFirstIdentificationVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_10() throws Exception {

        String jpqlQuery = "select e from TABLE('EMPLOYEE') EMP, Employee e";
        int startPosition = "select e from ".length();
        int endPosition   = "select e from TABLE('EMPLOYEE') EMP".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractFromClause_InvalidFirstIdentificationVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotResolvable() throws Exception {

        String query = "SELECT a FROM jpql.query.Address a";
        List<JPQLQueryProblem> problems = validate(query);
        testHasNoProblems(problems);
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
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    /**
     * Check that parsing {@code Employee.id} token returns errors. Column id does not exists in mapping because
     * Employee is not an entity identification variable and ID column is mapped as empId.
     * @throws Exception when test fails.
     */
    @Test
    public final void test_StateFieldPathExpression_NotResolvable_001() throws Exception {

        // Note: EclipseLinkSemanticValidatorExtension not implemented
        final String jpqlQuery  = "select e from Employee e where e.empId in (select Employee.id from Employee e)";
        final int startPosition = "select e from Employee e where e.empId in (select ".length() - 1;
        final int endPositionSFPENotResolvable
                                = "select e from Employee e where e.empId in (select Employee.id".length() - 1;
        final int endPositionIVINotDeclared
                                = "select e from Employee e where e.empId in (select Employee".length() - 1;

        final List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyTheseProblems(
            problems,
            new String[] {IdentificationVariable_Invalid_NotDeclared, StateFieldPathExpression_NotResolvable},
            new int[] {startPosition, startPosition},
            new int[] {endPositionIVINotDeclared, endPositionSFPENotResolvable}
        );
    }

    @Test
    public final void test_StateFieldPathExpression_NotResolvable_002() throws Exception {

        String jpqlQuery  = "Select avg(e3.c) from Employee e, (Select count(e2) as c, e2.name from Employee e2 group by e2.name) e3 where e.name = e3.name";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_StateFieldPathExpression_UnknownColumn_01() throws Exception {

        // Note: EclipseLinkSemanticValidatorExtension not implemented
        String jpqlQuery  = "select e from Employee e, table('EMPLOYEE') EMP where e.name = EMP.LAST_NAME";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_StateFieldPathExpression_UnknownColumn_02() throws Exception {

        // Note: EclipseLinkSemanticValidatorExtension not implemented
        String jpqlQuery  = "select e from Employee e, table('EMPLOYEE') EMP where e.name = EMP.NAME";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_StateFieldPathExpression_UnknownColumn_03() throws Exception {

        // Note: EclipseLinkSemanticValidatorExtension not implemented
        String jpqlQuery  = "select e from Employee e, table('ADDRESS') ADDR where (select a from Address a where a.zip = ADDR.ZIP_CODE) is not null";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_StateFieldPathExpression_UnknownColumn_04() throws Exception {

        // Note: EclipseLinkSemanticValidatorExtension not implemented
        String jpqlQuery  = "select e from Employee e, table('ADDRESS') ADDR where (select a from Address a where a.zip = ADDR.CODE) is not null";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }
}
