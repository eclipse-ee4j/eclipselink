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
package org.eclipse.persistence.jpa.tests.jpql;

import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractGrammarValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkGrammarValidator;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * The unit-test class used for testing a JPQL query grammatically when the JPA version is 1.0 and
 * 2.0 and EclipseLink is the persistence provider. The EclipseLink version supported is 2.0, 2.1,
 * 2.2 and 2.3.
 *
 * @version 2.5.1
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkGrammarValidatorTest extends AbstractGrammarValidatorTest {

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
        return EclipseLinkVersionTools.isNewerThanOrEqual2_4(jpqlGrammar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isSubqueryAllowedAnywhere() {
        return EclipseLinkVersionTools.isNewerThanOrEqual2_4(jpqlGrammar);
    }

    @Test
    public void test_AggregateFunction_WrongClause_5() throws Exception {

        String query = "SELECT e FROM Employee e GROUP BY AVG(e.age)";
        List<JPQLQueryProblem> problems = validate(query);

        if (EclipseLinkVersionTools.isEquals2_0(jpqlGrammar)) {
            int startPosition = "SELECT e FROM Employee e GROUP BY ".length();
            int endPosition   = query.length();
            testHasOnlyOneProblem(problems, BadExpression_InvalidExpression, startPosition, endPosition);
        }
        else {
            testHasNoProblems(problems);
        }
    }

    @Test
    public void test_GroupByClause_GroupByItemIsMissingComma_3() throws Exception {

        String query = "SELECT e FROM Employee e GROUP BY AVG(e.age) e.name";
        int startPosition = "SELECT e FROM Employee e GROUP BY AVG(e.age)".length();
        int endPosition   = "SELECT e FROM Employee e GROUP BY AVG(e.age) ".length();

        List<JPQLQueryProblem> problems = validate(query);

        if (EclipseLinkVersionTools.isEquals2_0(jpqlGrammar)) {

            int startPosition1 = "SELECT e FROM Employee e GROUP BY ".length();
            int endPosition1   = "SELECT e FROM Employee e GROUP BY AVG(e.age)".length();

            testHasProblem(
                problems,
                BadExpression_InvalidExpression,
                startPosition1,
                endPosition1
            );

            testHasProblem(
                problems,
                GroupByClause_GroupByItemIsMissingComma,
                startPosition,
                endPosition
            );
        }
        else {
            testHasOnlyOneProblem(
                problems,
                GroupByClause_GroupByItemIsMissingComma,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public void test_InExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE ABS(e.salary) IN :age";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (EclipseLinkVersionTools.isNewerThanOrEqual2_1(jpqlGrammar)) {
            testHasNoProblems(problems);
        }
        else {
            int startPosition = "SELECT e FROM Employee e WHERE ".length();
            int endPosition   = "SELECT e FROM Employee e WHERE ABS(e.salary)".length();

            testHasOnlyOneProblem(
                problems,
                InExpression_InvalidExpression,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public void test_OrderByClause_GroupByItemIsMissingComma_3() throws Exception {

        String query = "SELECT e FROM Employee e ORDER BY LENGTH(e.age) e.name";
        List<JPQLQueryProblem> problems = validate(query);

        if (EclipseLinkVersionTools.isNewerThanOrEqual2_0(jpqlGrammar)) {

            int startPosition1 = "SELECT e FROM Employee e ORDER BY ".length();
            int endPosition1   = "SELECT e FROM Employee e ORDER BY LENGTH(e.age)be.name".length();

            testHasProblem(
                problems,
                OrderByItem_InvalidExpression,
                startPosition1,
                endPosition1
            );
        }
        else {
            int startPosition = "SELECT e FROM Employee e ORDER BY LENGTH(e.age)".length();
            int endPosition   = "SELECT e FROM Employee e ORDER BY LENGTH(e.age) ".length();

            testHasOnlyOneProblem(
                problems,
                OrderByClause_OrderByItemIsMissingComma,
                startPosition,
                endPosition
            );
        }
    }
}
