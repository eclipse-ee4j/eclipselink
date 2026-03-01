/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import org.eclipse.persistence.jpa.jpql.AbstractGrammarValidator;
import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.tools.DefaultGrammarValidator;
import org.eclipse.persistence.jpa.tests.jpql.AbstractGrammarValidatorTest;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryStringFormatter;
import org.junit.Test;

import java.util.List;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.CastExpression_InvalidExpression;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.CastExpression_MissingDatabaseType;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.CastExpression_MissingExpression;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.CastExpression_MissingLeftParenthesis;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.CastExpression_MissingRightParenthesis;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.DatabaseType_InvalidFirstExpression;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.DatabaseType_InvalidSecondExpression;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.DatabaseType_MissingComma;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.DatabaseType_MissingFirstExpression;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.DatabaseType_MissingLeftParenthesis;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.DatabaseType_MissingRightParenthesis;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.DatabaseType_MissingSecondExpression;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.UnionClause_MissingExpression;

/**
 * The unit-test class used for testing a JPQL query grammatically when the JPA version is 3.2.
 *
 * @since 4.1
 * @author Radek Felcman
 */
@SuppressWarnings("nls")
public class DefaultGrammarValidatorTest3_2 extends AbstractGrammarValidatorTest {

    @Override
    protected AbstractGrammarValidator buildValidator() {
        return new DefaultGrammarValidator(jpqlGrammar);
    }

    @Override
    protected boolean isJoinFetchIdentifiable() {
        return false;
    }

    @Override
    protected boolean isSubqueryAllowedAnywhere() {
        return jpqlGrammar.getJPAVersion().isNewerThanOrEqual(JPAVersion.VERSION_3_2);
    }

    @Test
    public void test_UnionClause_MissingExpression_01() throws Exception {

        String jpqlQuery = "select e from Employee e intersect all select p from Product p where p.id <> 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public void test_UnionClause_MissingExpression_02() throws Exception {

        String jpqlQuery  = "select e from Employee e intersect";
        int startPosition = "select e from Employee e intersect".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                UnionClause_MissingExpression,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_UnionClause_MissingExpression_03() throws Exception {

        String jpqlQuery  = "select e from Employee e intersect ";
        int startPosition = "select e from Employee e intersect ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                UnionClause_MissingExpression,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_UnionClause_MissingExpression_04() throws Exception {

        String jpqlQuery  = "select e from Employee e intersect all";
        int startPosition = "select e from Employee e intersect all".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                UnionClause_MissingExpression,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_UnionClause_MissingExpression_05() throws Exception {

        String jpqlQuery  = "select e from Employee e intersect all ";
        int startPosition = "select e from Employee e intersect all ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                UnionClause_MissingExpression,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_CastExpression_InvalidExpression_1() throws Exception {

        String jpqlQuery = "Select cast(e.firstName as char) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CastExpression_InvalidExpression);
    }

    @Test
    public void test_CastExpression_MissingDatabaseType_1() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CastExpression_MissingDatabaseType);
    }

    @Test
    public void test_CastExpression_MissingDatabaseType_2() throws Exception {

        String jpqlQuery = "Select cast(e.firstName as char) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CastExpression_MissingDatabaseType);
    }

    @Test
    public void test_CastExpression_MissingDatabaseType_3() throws Exception {

        String jpqlQuery  = "Select cast(e.firstName) from Employee e";
        int startPosition = "Select cast(e.firstName".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                CastExpression_MissingDatabaseType,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_CastExpression_MissingDatabaseType_4() throws Exception {

        String jpqlQuery  = "Select cast(e.firstName ) from Employee e";
        int startPosition = "Select cast(e.firstName ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildStringFormatter_2());

        testHasOnlyOneProblem(
                problems,
                CastExpression_MissingDatabaseType,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_CastExpression_MissingDatabaseType_5() throws Exception {

        String jpqlQuery = "Select cast(e.firstName as) from Employee e";
        int startPosition = "Select cast(e.firstName as".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                CastExpression_MissingDatabaseType,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_CastExpression_MissingDatabaseType_6() throws Exception {

        String jpqlQuery = "Select cast(e.firstName as ) from Employee e";
        int startPosition = "Select cast(e.firstName as ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildStringFormatter_3());

        testHasOnlyOneProblem(
                problems,
                CastExpression_MissingDatabaseType,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_CastExpression_MissingDatabaseType_7() throws Exception {

        String jpqlQuery = "Select cast(e.firstName from Employee e";
        int startPosition = "Select cast(e.firstName ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
                problems,
                CastExpression_MissingDatabaseType,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_CastExpression_MissingDatabaseType_8() throws Exception {

        String jpqlQuery = "Select cast(e.firstName as from Employee e";
        int startPosition = "Select cast(e.firstName as ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
                problems,
                CastExpression_MissingDatabaseType,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_CastExpression_MissingExpression_1() throws Exception {

        String jpqlQuery = "Select cast(e.firstName as char) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CastExpression_MissingExpression);
    }

    @Test
    public void test_CastExpression_MissingExpression_2() throws Exception {

        String jpqlQuery = "Select cast(as char) from Employee e";
        int startPosition = "Select cast(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                CastExpression_MissingExpression,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_CastExpression_MissingLeftParenthesis_1() throws Exception {

        String jpqlQuery = "Select cast(e.firstName as char) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CastExpression_MissingLeftParenthesis);
    }

    @Test
    public void test_CastExpression_MissingLeftParenthesis_2() throws Exception {

        String jpqlQuery = "Select cast from Employee e";
        int startPosition = "Select cast".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                CastExpression_MissingLeftParenthesis,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_CastExpression_MissingLeftParenthesis_3() throws Exception {

        String jpqlQuery = "Select cast e.firstName as char) from Employee e";
        int startPosition = "Select cast".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                CastExpression_MissingLeftParenthesis,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_CastExpression_MissingLeftParenthesis_4() throws Exception {

        String jpqlQuery = "Select cast as from Employee e";
        int startPosition = "Select cast".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
                problems,
                CastExpression_MissingLeftParenthesis,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_CastExpression_MissingRightParenthesis_1() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CastExpression_MissingRightParenthesis);
    }

    @Test
    public void test_CastExpression_MissingRightParenthesis_2() throws Exception {

        String jpqlQuery = "Select cast(e.firstName as char) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CastExpression_MissingRightParenthesis);
    }

    @Test
    public void test_CastExpression_MissingRightParenthesis_3() throws Exception {

        String jpqlQuery  = "Select cast(e.firstName as char(2) from Employee e";
        int startPosition = "Select cast(e.firstName as char(2)".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                CastExpression_MissingRightParenthesis,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_CastExpression_MissingRightParenthesis_4() throws Exception {

        String jpqlQuery = "Select cast(e.firstName as char from Employee e";
        int startPosition = "Select cast(e.firstName as char".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                CastExpression_MissingRightParenthesis,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_InvalidFirstExpression_1() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_InvalidFirstExpression);
    }

    @Test
    public void test_DatabaseType_InvalidFirstExpression_2() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2)) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_InvalidFirstExpression);
    }

    @Test
    public void test_DatabaseType_InvalidFirstExpression_3() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2, 2)) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_InvalidFirstExpression);
    }

    @Test
    public void test_DatabaseType_InvalidFirstExpression_4() throws Exception {

        String jpqlQuery  = "Select cast(e.firstName char(avg(e.age), 2)) from Employee e";
        int startPosition = "Select cast(e.firstName char(".length();
        int endPosition   = "Select cast(e.firstName char(avg(e.age)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                DatabaseType_InvalidFirstExpression,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_InvalidSecondExpression_1() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_InvalidSecondExpression);
    }

    @Test
    public void test_DatabaseType_InvalidSecondExpression_2() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2)) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_InvalidSecondExpression);
    }

    @Test
    public void test_DatabaseType_InvalidSecondExpression_3() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2, 2)) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_InvalidSecondExpression);
    }

    @Test
    public void test_DatabaseType_InvalidSecondExpression_4() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2, avg(e.age))) from Employee e";
        int startPosition = "Select cast(e.firstName char(2, ".length();
        int endPosition   = "Select cast(e.firstName char(2, avg(e.age)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                DatabaseType_InvalidSecondExpression,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_MissingComma_1() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2, 2)) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingComma);
    }

    @Test
    public void test_DatabaseType_MissingComma_2() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2)) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingComma);
    }

    @Test
    public void test_DatabaseType_MissingComma_3() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2 2)) from Employee e";
        int startPosition = "Select cast(e.firstName char(2".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                DatabaseType_MissingComma,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_MissingFirstExpression_1() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingFirstExpression);
    }

    @Test
    public void test_DatabaseType_MissingFirstExpression_2() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2)) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingFirstExpression);
    }

    @Test
    public void test_DatabaseType_MissingFirstExpression_3() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2,)) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingFirstExpression);
    }

    @Test
    public void test_DatabaseType_MissingFirstExpression_4() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(, 2)) from Employee e";
        int startPosition = "Select cast(e.firstName char(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                DatabaseType_MissingFirstExpression,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_MissingFirstExpression_5() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(,)) from Employee e";
        int startPosition = "Select cast(e.firstName char(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
                problems,
                DatabaseType_MissingFirstExpression,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_MissingLeftParenthesis_1() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingLeftParenthesis);
    }

    @Test
    public void test_DatabaseType_MissingLeftParenthesis_2() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char()) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingLeftParenthesis);
    }

    @Test
    public void test_DatabaseType_MissingLeftParenthesis_3() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(3)) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingLeftParenthesis);
    }

    @Test
    public void test_DatabaseType_MissingLeftParenthesis_4() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(3,)) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingLeftParenthesis);
    }

    @Test
    public void test_DatabaseType_MissingLeftParenthesis_5() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(3, )) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildStringFormatter_4());
        testDoesNotHaveProblem(problems, DatabaseType_MissingLeftParenthesis);
    }

    @Test
    public void test_DatabaseType_MissingLeftParenthesis_6() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(3, 3)) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingLeftParenthesis);
    }

    @Test
    public void test_DatabaseType_MissingLeftParenthesis_7() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char 3)) from Employee e";
        int startPosition = "Select cast(e.firstName char".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                DatabaseType_MissingLeftParenthesis,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_MissingLeftParenthesis_8() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char 3, 3)) from Employee e";
        int startPosition = "Select cast(e.firstName char".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                DatabaseType_MissingLeftParenthesis,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_MissingRightParenthesis_1() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingRightParenthesis);
    }

    @Test
    public void test_DatabaseType_MissingRightParenthesis_2() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char()) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingRightParenthesis);
    }

    @Test
    public void test_DatabaseType_MissingRightParenthesis_3() throws Exception {

        String jpqlQuery  = "Select cast(e.firstName char(2 from Employee e";
        int startPosition = "Select cast(e.firstName char(2".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
                problems,
                DatabaseType_MissingRightParenthesis,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_MissingRightParenthesis_4() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2, from Employee e";
        int startPosition = "Select cast(e.firstName char(2, ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
                problems,
                DatabaseType_MissingRightParenthesis,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_MissingRightParenthesis_5() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2, 2 from Employee e";
        int startPosition = "Select cast(e.firstName char(2, 2".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
                problems,
                DatabaseType_MissingRightParenthesis,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_MissingSecondExpression_1() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingFirstExpression);
    }

    @Test
    public void test_DatabaseType_MissingSecondExpression_2() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2, 2)) from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DatabaseType_MissingFirstExpression);
    }

    @Test
    public void test_DatabaseType_MissingSecondExpression_3() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(, 2)) from Employee e";
        int startPosition = "Select cast(e.firstName char(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                DatabaseType_MissingFirstExpression,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_MissingSecondExpression_4() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2,)) from Employee e";
        int startPosition = "Select cast(e.firstName char(2,".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
                problems,
                DatabaseType_MissingSecondExpression,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_MissingSecondExpression_5() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(2, )) from Employee e";
        int startPosition = "Select cast(e.firstName char(2, ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildStringFormatter_4());

        testHasOnlyOneProblem(
                problems,
                DatabaseType_MissingSecondExpression,
                startPosition,
                endPosition
        );
    }

    @Test
    public void test_DatabaseType_MissingSecondExpression_6() throws Exception {

        String jpqlQuery = "Select cast(e.firstName char(,)) from Employee e";
        int startPosition = "Select cast(e.firstName char(,".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
                problems,
                DatabaseType_MissingSecondExpression,
                startPosition,
                endPosition
        );
    }

    private JPQLQueryStringFormatter buildStringFormatter_2() {
        return query -> query.replace("firstName)", "firstName )");
    }

    private JPQLQueryStringFormatter buildStringFormatter_3() {
        return query -> query.replace(")", " )");
    }

    private JPQLQueryStringFormatter buildStringFormatter_4() {
        return query -> query.replace(",", ", ");
    }

}
