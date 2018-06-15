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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractGrammarValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkGrammarValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * The unit-test class used for testing a JPQL query grammatically when the JPA version is 2.1 and
 * EclipseLink is the persistence provider. The EclipseLink version supported is only 2.5.
 *
 * @see EclipseLinkGrammarValidator
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkGrammarValidatorTest2_5 extends AbstractGrammarValidatorTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractGrammarValidator buildValidator() {
        return new EclipseLinkGrammarValidator(jpqlGrammar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isJoinFetchIdentifiable() {
        return true;
    }

    protected boolean isNewerThanOrEqual(EclipseLinkVersion version) {
        EclipseLinkVersion currentVersion = EclipseLinkVersion.value(jpqlGrammar.getProviderVersion());
        return currentVersion.isNewerThanOrEqual(version);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isSubqueryAllowedAnywhere() {
        return isNewerThanOrEqual(EclipseLinkVersion.VERSION_2_4);
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
    public void test_InExpression_InvalidExpression_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE ABS(e.age) IN :age";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public void test_InExpression_InvalidExpression_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE UPPER(e.department) IN :age";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public void test_InExpression_InvalidExpression_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE CURRENT_DATE IN :age";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE CURRENT_DATE".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public void test_NestedArray_01() throws Exception {

        String jpqlQuery = "Select e from Employee e where (e.empId, e.empId) IN ((:id1, :id2), (:id3, :id4))";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public void test_NestedArray_02() throws Exception {

        String jpqlQuery  = "Select e from Employee e where (e.firstName, e.lastName) IN :args";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public void test_NestedArray_03() throws Exception {

        String jpqlQuery  = "Select e from Employee e where (e.firstName, e.lastName) IN (Select e2.firstName, e2.lastName from Employee e2)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public void test_ValidQuery_001() throws Exception {

        String jpqlQuery = "Select e from Employee e where (e.id1, e.id2) IN ((:id1, :id2), (:id3, :id4))";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }
}
