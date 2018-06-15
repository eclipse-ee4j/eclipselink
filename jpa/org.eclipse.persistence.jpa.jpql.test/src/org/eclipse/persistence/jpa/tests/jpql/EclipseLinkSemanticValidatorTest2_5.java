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
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages;
import org.eclipse.persistence.jpa.jpql.tools.EclipseLinkJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.EclipseLinkSemanticValidator;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;
import org.eclipse.persistence.jpa.tests.jpql.tools.AbstractSemanticValidatorTest;
import org.junit.Test;

/**
 * The unit-test class used for testing a JPQL query semantically when the JPA version is 1.0 and 2.0
 * and EclipseLink is the persistence provider. The EclipseLink version supported is 2.5 only.
 *
 * @version 2.5.1
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkSemanticValidatorTest2_5 extends AbstractSemanticValidatorTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLQueryContext buildQueryContext() {
        return new EclipseLinkJPQLQueryContext(jpqlGrammar);
    }

    protected EclipseLinkSemanticValidatorExtension buildSemanticExtension() {
        return EclipseLinkSemanticValidatorExtension.NULL_EXTENSION;
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
    public void test_HQL_Query_001() throws Exception {

        String jpqlQuery = "FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public void test_HQL_Query_002() throws Exception {

        String jpqlQuery = "FROM Employee e WHERE e.name = 'JPQL'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public void test_NestedArray_01() throws Exception {

        String jpqlQuery = "Select e from Employee e where (e.empId, e.empId) IN ((:id1, :id2), (:id3, :id4))";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public void test_NestedArray_02() throws Exception {

        String jpqlQuery = "Select e from Employee e where (e.empId, e.empId) IN :arg";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public void test_NestedArray_03() throws Exception {

        String jpqlQuery  = "Select e from Employee e where (e.id, e.empId) IN ((:id1, :id2), (:id3, :id4))";
        int startPosition = "Select e from Employee e where (".length();
        int endPosition   = "Select e from Employee e where (e.id".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JPQLQueryProblemMessages.StateFieldPathExpression_NotResolvable,
            startPosition,
            endPosition
        );
    }

    @Test
    public void test_NestedArray_04() throws Exception {

        String jpqlQuery  = "Select e from Employee e where (e.empId, e.id) IN ((:id1, :id2), (:id3, :id4))";
        int startPosition = "Select e from Employee e where (e.empId, ".length();
        int endPosition   = "Select e from Employee e where (e.empId, e.id".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JPQLQueryProblemMessages.StateFieldPathExpression_NotResolvable,
            startPosition,
            endPosition
        );
    }

    @Test
    public void test_NestedArray_05() throws Exception {

        String jpqlQuery  = "Select e from Employee e where (e.empId, e.empId) IN ((e.id, :id2), (:id3, :id4))";
        int startPosition = "Select e from Employee e where (e.empId, e.empId) IN((".length();
        int endPosition   = "Select e from Employee e where (e.empId, e.empId) IN((e.id".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JPQLQueryProblemMessages.StateFieldPathExpression_NotResolvable,
            startPosition,
            endPosition
        );
    }

    @Test
    public void test_NestedArray_06() throws Exception {

        String jpqlQuery  = "Select e from Employee e where (e.empId, e.empId) IN ((:id1, :id2), (:id3, id4))";
        int startPosition = "Select e from Employee e where (e.empId, e.empId) IN((:id1, :id2), (:id3, ".length();
        int endPosition   = "Select e from Employee e where (e.empId, e.empId) IN((:id1, :id2), (:id3, id4".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JPQLQueryProblemMessages.EntityTypeLiteral_NotResolvable,
            startPosition,
            endPosition
        );
    }

    @Test
    public void test_NestedArray_07() throws Exception {

        String jpqlQuery  = "Select d from Dept d where (d.loc, d.role) IN(Select d2.loc, d2.role from Dept d2)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public void test_NestedArray_08() throws Exception {

        String jpqlQuery  = "Select d from Dept d where (d.loc, d.role) IN(Select d2.loc from Dept d2)";
        int startPosition = "Select d from Dept d where ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JPQLQueryProblemMessages.InExpression_InvalidItemCount,
            startPosition,
            endPosition
        );
    }

    @Test
    public void test_NestedArray_09() throws Exception {

        String jpqlQuery  = "Select d from Dept d where d.loc IN(Select d2.loc, d2.role from Dept d2)";
        int startPosition = "Select d from Dept d where ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JPQLQueryProblemMessages.InExpression_InvalidItemCount,
            startPosition,
            endPosition
        );
    }

    @Test
    public void test_NestedArray_10() throws Exception {

        String jpqlQuery  = "Select d from Dept d where (d.loc, d.role, 'JPQL') IN(Select d2.loc, d2.role from Dept d2)";
        int startPosition = "Select d from Dept d where ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JPQLQueryProblemMessages.InExpression_InvalidItemCount,
            startPosition,
            endPosition
        );
    }
}
