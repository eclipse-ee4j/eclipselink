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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractGrammarValidator;
import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages;
import org.eclipse.persistence.jpa.jpql.tools.DefaultGrammarValidator;
import org.eclipse.persistence.jpa.tests.jpql.AbstractGrammarValidatorTest;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * The unit-test class used for testing a JPQL query grammatically when the JPA version is 1.0 and 2.0.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DefaultGrammarValidatorTest2_0 extends AbstractGrammarValidatorTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractGrammarValidator buildValidator() {
        return new DefaultGrammarValidator(jpqlGrammar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isJoinFetchIdentifiable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isSubqueryAllowedAnywhere() {
        return jpqlGrammar.getJPAVersion().isNewerThanOrEqual(JPAVersion.VERSION_2_1);
    }

    @Test
    public void test_InExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE ABS(e.age) IN :age";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE ABS(e.age)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public void test_InExpression_ItemInvalidExpression_01() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN ('JPQL', ABS(e.name))";
        int startPosition = "SELECT e FROM Employee e WHERE e.name IN('JPQL', ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.name IN('JPQL', ABS(e.name)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InExpression_ItemInvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public void test_InExpression_ItemInvalidExpression_02() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN (ABS(e.name), 'JPQL')";
        int startPosition = "SELECT e FROM Employee e WHERE e.name IN(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.name IN(ABS(e.name)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InExpression_ItemInvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public void test_InExpression_ItemInvalidExpression_03() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN(e.address.street)";
        int startPosition = "SELECT e FROM Employee e WHERE e.name IN(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.name IN(e.address.street".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InExpression_ItemInvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public void test_InputParameter_WrongClauseDeclaration_1() throws Exception {

        String query = "SELECT ?1 FROM Employee e";

        int startPosition = "SELECT ".length();
        int endPosition   = "SELECT ?1".length();

        List<JPQLQueryProblem> problems = validate(query);

        testHasOnlyOneProblem(
            problems,
            JPQLQueryProblemMessages.InputParameter_WrongClauseDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public void test_InputParameter_WrongClauseDeclaration_2() throws Exception {

        String query = "SELECT :name FROM Employee e";

        int startPosition = "SELECT ".length();
        int endPosition   = "SELECT :name".length();

        List<JPQLQueryProblem> problems = validate(query);

        testHasOnlyOneProblem(
            problems,
            JPQLQueryProblemMessages.InputParameter_WrongClauseDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public void test_InvalidQuery_001() throws Exception {

        String jpqlQuery = "Select e from Employee e where (e.id1, e.id2) IN ((:id1, :id2), (:id3, :id4))";

        int startPosition1 = "Select e from Employee e where ".length();
        int endPosition1   = "Select e from Employee e where (e.id1, e.id2)".length();

        int startPosition2 = "Select e from Employee e where (e.id1, e.id2) IN(".length();
        int endPosition2   = "Select e from Employee e where (e.id1, e.id2) IN((:id1, :id2)".length();

        int startPosition3 = "Select e from Employee e where (e.id1, e.id2) IN((:id1, :id2), ".length();
        int endPosition3   = "Select e from Employee e where (e.id1, e.id2) IN((:id1, :id2), (:id3, :id4)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyTheseProblems(
            problems,
            new String [] { JPQLQueryProblemMessages.InExpression_InvalidExpression,
                            JPQLQueryProblemMessages.InExpression_ItemInvalidExpression,
                            JPQLQueryProblemMessages.InExpression_ItemInvalidExpression
            },
            new int[] { startPosition1, startPosition2, startPosition3 },
            new int[] { endPosition1,   endPosition2,   endPosition3 }
        );
    }
}
