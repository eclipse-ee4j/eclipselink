/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql;

import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractGrammarValidator;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunctionFactory;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_2;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_3;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_5;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.InternalCountBNF;
import org.eclipse.persistence.jpa.jpql.parser.InternalOrderByItemBNF;
import org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.jpql.parser.ScalarExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SubqueryBNF;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryStringFormatter;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * The unit-tests testing {@link AbstractGrammarValidator}.
 *
 * @version 2.5.2
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractGrammarValidatorTest extends AbstractValidatorTest {

    private JPQLQueryStringFormatter buildFormatter_1() throws Exception {
        return new JPQLQueryStringFormatter() {
            public String format(String jpqlQuery) {
                return jpqlQuery.replace("SELECT)", "SELECT )");
            }
        };
    }

    private JPQLQueryStringFormatter buildFormatter_10(final String jpqlQuery) {
        return new JPQLQueryStringFormatter() {
            public String format(String query) {
                return jpqlQuery;
            }
        };
    }

    private JPQLQueryStringFormatter buildFormatter_2() throws Exception {
        return new JPQLQueryStringFormatter() {
            public String format(String jpqlQuery) {
                return jpqlQuery.replace("(DISTINCT)", "(DISTINCT )");
            }
        };
    }

    private JPQLQueryStringFormatter buildFormatter_4() throws Exception {
        return new JPQLQueryStringFormatter() {
            public String format(String jpqlQuery) {
                return jpqlQuery.replace(",)", ", )");
            }
        };
    }

    private JPQLQueryStringFormatter buildFormatter_5() throws Exception {
        return new JPQLQueryStringFormatter() {
            public String format(String jpqlQuery) {
                return jpqlQuery.replace(",)", ", )");
            }
        };
    }

    private JPQLQueryStringFormatter buildFormatter_6() throws Exception {
        return new JPQLQueryStringFormatter() {
            public String format(String jpqlQuery) {
                return jpqlQuery.replace(",)", ", )");
            }
        };
    }

    private JPQLQueryStringFormatter buildFormatter_7() {
        return new JPQLQueryStringFormatter() {
            public String format(String jpqlQuery) {
                return jpqlQuery.replace("0,)", "0, )");
            }
        };
    }

    private JPQLQueryStringFormatter buildFormatter_8() {
        return new JPQLQueryStringFormatter() {
            public String format(String jpqlQuery) {
                return jpqlQuery.replace(",,", ", ,");
            }
        };
    }

    private JPQLQueryStringFormatter buildFormatter_9() {
        return new JPQLQueryStringFormatter() {
            public String format(String query) {
                return query.endsWith("NULLS ORDER") ?
                       query.replace("NULLS ORDER", "NULLS order") :
                       query.replace("nulls ORDER", "NULLS order");
            }
        };
    }

    @Override
    protected abstract AbstractGrammarValidator buildValidator();

    @Override
    protected AbstractGrammarValidator getValidator() {
        return (AbstractGrammarValidator) super.getValidator();
    }

    protected abstract boolean isJoinFetchIdentifiable();

    private boolean isScalarExpressionInOrderByClauseSupported() {
        JPQLQueryBNF queryBNF = jpqlGrammar.getExpressionRegistry().getQueryBNF(InternalOrderByItemBNF.ID);
        return queryBNF.hasChild(ScalarExpressionBNF.ID);
    }

    protected abstract boolean isSubqueryAllowedAnywhere();

    private boolean isSubqueryInOrderByClauseSupported() {
        JPQLQueryBNF queryBNF = jpqlGrammar.getExpressionRegistry().getQueryBNF(InternalOrderByItemBNF.ID);
        return queryBNF.hasChild(SubqueryBNF.ID);
    }

    @Test
    public final void test_AbsExpression_InvalidExpression() throws Exception {

        String jpqlQuery = "SELECT ABS(SELECT o FROM Order o) FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isSubqueryAllowedAnywhere()) {
            testDoesNotHaveProblem(
                problems,
                AbsExpression_InvalidExpression
            );
        }
        else {
            int startPosition = "SELECT ABS(".length();
            int endPosition   = "SELECT ABS(SELECT o FROM Order o".length();

            testHasOnlyOneProblem(
                problems,
                AbsExpression_InvalidExpression,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public final void test_AbsExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT ABS() FROM Employee e";
        int startPosition = "SELECT ABS(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbsExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbsExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT ABS 4 + 5) FROM Employee e";
        int startPosition = "SELECT ABS".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbsExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbsExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT ABS(e.age + 100 FROM Employee e";
        int startPosition = "SELECT ABS(e.age + 100".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbsExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractFromClause_IdentificationVariableDeclarationEndWithComma_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.managers m,";
        int startPosition = "SELECT e FROM Employee e JOIN e.managers m".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractFromClause_IdentificationVariableDeclarationEndsWithComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractFromClause_IdentificationVariableDeclarationEndWithComma_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE EXISTS(SELECT o FROM Order o,)";
        int startPosition = "SELECT e FROM Employee e WHERE EXISTS(SELECT o FROM Order o".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractFromClause_IdentificationVariableDeclarationEndsWithComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractFromClause_IdentificationVariableDeclarationIsMissingComma() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e Address a";
        int startPosition = "SELECT e FROM Employee e".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractFromClause_IdentificationVariableDeclarationIsMissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractFromClause_MissingIdentificationVariableDeclaration_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractFromClause_MissingIdentificationVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractFromClause_MissingIdentificationVariableDeclaration_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractFromClause_MissingIdentificationVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractFromClause_MissingIdentificationVariableDeclaration_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM WHERE e.name = 'JPQL'";
        int startPosition = "SELECT e FROM ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractFromClause_MissingIdentificationVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractFromClause_MissingIdentificationVariableDeclaration_4() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e, Order o WHERE EXISTS(SELECT o FROM)";
        int startPosition = "SELECT e FROM Employee e, Order o WHERE EXISTS(SELECT o FROM".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractFromClause_MissingIdentificationVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractFromClause_MissingIdentificationVariableDeclaration_5() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE EXISTS(SELECT o FROM WHERE o.price > 2000)";
        int startPosition = "SELECT e FROM Employee e WHERE EXISTS(SELECT o FROM ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractFromClause_MissingIdentificationVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractPathExpression_CannotEndWithComma() throws Exception {

        String jpqlQuery  = "SELECT e. FROM Employee e";
        int startPosition = "SELECT ".length();
        int endPosition   = "SELECT e.".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractPathExpression_CannotEndWithComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractPathExpression_InvalidIdentificationVariable_1() throws Exception {

        String jpqlQuery  = "SELECT e.name FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_AbstractPathExpression_InvalidIdentificationVariable_2() throws Exception {

        String jpqlQuery  = "SELECT KEY(e).name FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_AbstractPathExpression_MissingIdentificationVariable() throws Exception {

        String jpqlQuery  = "SELECT .name FROM Employee e";
        int startPosition = "SELECT ".length();
        int endPosition   = "SELECT .name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractPathExpression_MissingIdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSelectClause_InvalidSelectExpression_1() throws Exception {

        String jpqlQuery  = "SELECT (SELECT e FROM Employee e) FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isSubqueryAllowedAnywhere()) {
            testDoesNotHaveProblem(
                problems,
                AbstractSelectClause_InvalidSelectExpression
            );
        }
        else {
            int startPosition = "SELECT ".length();
            int endPosition   = "SELECT (SELECT e FROM Employee e)".length();

            testHasOnlyOneProblem(
                problems,
                AbstractSelectClause_InvalidSelectExpression,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public final void test_AbstractSelectClause_InvalidSelectExpression_2() throws Exception {

        String jpqlQuery  = "SELECT (SELECT e F) FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isSubqueryAllowedAnywhere()) {
            testDoesNotHaveProblem(
                problems,
                AbstractSelectClause_InvalidSelectExpression
            );
        }
        else {
            int startPosition = "SELECT ".length();
            int endPosition   = "SELECT (SELECT e F)".length();

            testHasOnlyOneProblem(
                problems,
                AbstractSelectClause_InvalidSelectExpression,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public final void test_AbstractSelectClause_MissingSelectExpression_1() throws Exception {

        String jpqlQuery  = "SELECT";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            AbstractSelectClause_MissingSelectExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSelectClause_MissingSelectExpression_2() throws Exception {

        String jpqlQuery  = "SELECT ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            AbstractSelectClause_MissingSelectExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSelectClause_MissingSelectExpression_3() throws Exception {

        String jpqlQuery  = "SELECT FROM Employee e";
        int startPosition = "SELECT ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractSelectClause_MissingSelectExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSelectClause_MissingSelectExpression_4() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE EXISTS(SELECT )";
        int startPosition = "SELECT e FROM Employee e WHERE EXISTS(SELECT ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_1());

        testHasProblem(
            problems,
            AbstractSelectClause_MissingSelectExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSelectClause_MissingSelectExpression_5() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE EXISTS(SELECT FROM Order o)";
        int startPosition = "SELECT e FROM Employee e WHERE EXISTS(SELECT ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractSelectClause_MissingSelectExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSelectClause_SelectExpressionEndsWithComma_1() throws Exception {

        String jpqlQuery  = "SELECT e, AVG(e.age), FROM Employee e";
        int startPosition = "SELECT e, AVG(e.age)".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractSelectClause_SelectExpressionEndsWithComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSelectClause_SelectExpressionIsMissingComma_1() throws Exception {

        String jpqlQuery  = "SELECT e AVG(e.age) FROM Employee e";
        int startPosition = "SELECT e".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractSelectClause_SelectExpressionIsMissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSelectClause_SelectExpressionIsMissingComma_2() throws Exception {

        String jpqlQuery  = "SELECT e, AVG(e.age) e.name FROM Employee e";
        int startPosition = "SELECT e, AVG(e.age)".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractSelectClause_SelectExpressionIsMissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSelectStatement_FromClauseMissing_1() throws Exception {

        String jpqlQuery  = "SELECT e";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractSelectStatement_FromClauseMissing,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSelectStatement_FromClauseMissing_2() throws Exception {

        String jpqlQuery  = "SELECT e WHERE e.name = 'JPQL'";
        int startPosition = "SELECT e ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractSelectStatement_FromClauseMissing,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSelectStatement_FromClauseMissing_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE ALL(SELECT E WHERE e.name = 'JPQL') < e";
        int startPosition = "SELECT e FROM Employee e WHERE ALL(SELECT E ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractSelectStatement_FromClauseMissing,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AggregateFunction_WrongClause_1() throws Exception {

        String jpqlQuery  = "SELECT AVG(e.age) FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_AggregateFunction_WrongClause_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e HAVING AVG(e.age) > 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_AggregateFunction_WrongClause_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE AVG(e.age) > 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE AVG(e.age)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, AggregateFunction_WrongClause, startPosition, endPosition);
    }

    @Test
    public final void test_AggregateFunction_WrongClause_4() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE AVG() > 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE AVG()".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, AggregateFunction_WrongClause, startPosition, endPosition);
    }

    @Test
    public final void test_AllOrAnyExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SOME(AVG(e.name)) = TRUE";
        int startPosition = "SELECT e FROM Employee e WHERE SOME(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE SOME(AVG(e.name)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AllOrAnyExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AllOrAnyExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.addresses a WHERE ALL() = TRUE";
        int startPosition = "SELECT e FROM Employee e JOIN e.addresses a WHERE ALL(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AllOrAnyExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AllOrAnyExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.addresses a WHERE SOME SELECT p FROM Order p) = TRUE";
        int startPosition = "SELECT e FROM Employee e JOIN e.addresses a WHERE SOME".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AllOrAnyExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AllOrAnyExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.addresses a WHERE SOME(SELECT p FROM Order p = TRUE";
        int startPosition = "SELECT e FROM Employee e JOIN e.addresses a WHERE SOME(SELECT p FROM Order p".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AllOrAnyExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AllOrAnyExpression_NotPartOfComparisonExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE ALL(SELECT a FROM Address a)";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            AllOrAnyExpression_NotPartOfComparisonExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ArithmeticExpression_InvalidLeftExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE FROM Employee e + LENGTH(e.name) < 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE FROM Employee e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ArithmeticExpression_InvalidLeftExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ArithmeticExpression_InvalidRightExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LENGTH(e.name) + FROM Employee e < 2";
        int startPosition = "SELECT e FROM Employee e WHERE LENGTH(e.name) + ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LENGTH(e.name) + FROM Employee e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ArithmeticExpression_InvalidRightExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ArithmeticExpression_MissingLeftExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE + +LENGTH(e.name) < 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ArithmeticExpression_MissingLeftExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ArithmeticExpression_MissingRightExpression_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LENGTH(e.name) +";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ArithmeticExpression_MissingRightExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ArithmeticFactor_MissingExpression_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE -";
        int startPosition = "SELECT e FROM Employee e WHERE -".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ArithmeticFactor_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ArithmeticFactor_MissingExpression_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.age + -";
        int startPosition = "SELECT e FROM Employee e WHERE e.age + -".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ArithmeticFactor_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AvgFunction_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT AVG(e) FROM Employee e";
        int startPosition = "SELECT AVG(".length();
        int endPosition   = "SELECT AVG(e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AvgFunction_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AvgFunction_MissingExpression_1() throws Exception {

        String jpqlQuery  = "SELECT AVG() FROM Employee e";
        int startPosition = "SELECT AVG(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AvgFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AvgFunction_MissingExpression_2() throws Exception {

        String jpqlQuery  = "SELECT AVG(DISTINCT) FROM Employee e";
        int startPosition = "SELECT AVG(DISTINCT".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AvgFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AvgFunction_MissingExpression_3() throws Exception {

        String jpqlQuery  = "SELECT AVG(DISTINCT ) FROM Employee e";
        int startPosition = "SELECT AVG(DISTINCT ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_2());

        testHasOnlyOneProblem(
            problems,
            AvgFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AvgFunction_MissingLeftParenthesis_1() throws Exception {

        String jpqlQuery  = "SELECT AVG e.age) FROM Employee e";
        int startPosition = "SELECT AVG".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AvgFunction_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AvgFunction_MissingLeftParenthesis_2() throws Exception {

        String jpqlQuery  = "SELECT AVG DISTINCT e.age) FROM Employee e";
        int startPosition = "SELECT AVG".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AvgFunction_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AvgFunction_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT AVG(DISTINCT e.age FROM Employee e";
        int startPosition = "SELECT AVG(DISTINCT e.age".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AvgFunction_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_BadExpression_InvalidExpression_2() throws Exception {

        String jpqlQuery = "SELECT (SELECT e FROM Employee e), AVG(e.age) FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isSubqueryAllowedAnywhere()) {
            testDoesNotHaveProblem(
                problems,
                BadExpression_InvalidExpression
            );
        }
        else {
            int startPosition = "SELECT (".length();
            int endPosition   = "SELECT (SELECT e FROM Employee e".length();

            testHasOnlyOneProblem(
                problems,
                BadExpression_InvalidExpression,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public final void test_BadExpression_InvalidExpression_3() throws Exception {

        String jpqlQuery = "SELECT AVG(e.age), (SELECT e FROM Employee e) FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isSubqueryAllowedAnywhere()) {
            testDoesNotHaveProblem(
                problems,
                BadExpression_InvalidExpression
            );
        }
        else {
            int startPosition = "SELECT AVG(e.age), (".length();
            int endPosition   = "SELECT AVG(e.age), (SELECT e FROM Employee e".length();

            testHasOnlyOneProblem(
                problems,
                BadExpression_InvalidExpression,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public final void test_BetweenExpression_MissingAnd_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.age BETWEEN 20";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            BetweenExpression_MissingAnd,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_BetweenExpression_MissingAnd_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.age BETWEEN 20 ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            BetweenExpression_MissingAnd,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_BetweenExpression_MissingAnd_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.age BETWEEN 20 40";
        int startPosition = "SELECT e FROM Employee e WHERE e.age BETWEEN 20 ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            BetweenExpression_MissingAnd,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_BetweenExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE BETWEEN 20 AND 40";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            BetweenExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_BetweenExpression_MissingLowerBoundExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.age BETWEEN AND 40";
        int startPosition = "SELECT e FROM Employee e WHERE e.age BETWEEN ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            BetweenExpression_MissingLowerBoundExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_BetweenExpression_MissingUpperBoundExpression_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.age BETWEEN 20 AND";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            BetweenExpression_MissingUpperBoundExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_BetweenExpression_MissingUpperBoundExpression_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.age BETWEEN 20 AND ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            BetweenExpression_MissingUpperBoundExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CaseExpression_MissingElseExpression_1() throws Exception {

        String jpqlQuery = "UPDATE Employee e " +
                           "SET e.salary = " +
                           "    CASE WHEN e.rating = 1 THEN e.salary * 1.1 " +
                           "         WHEN e.rating = 2 THEN e.salary * 1.05 " +
                           "         ELSE " +
                           "    END";

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        jpqlQuery = jpqlQuery.replaceAll("\\s+", " ");

        int startPosition = jpqlQuery.length() - Expression.END.length();
        int endPosition   = startPosition;

        testHasOnlyOneProblem(
            problems,
            CaseExpression_MissingElseExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CaseExpression_MissingElseExpression_2() throws Exception {

        String jpqlQuery = "SELECT e.name," +
                           "       CASE TYPE(e) WHEN Exempt THEN 'Exempt'" +
                           "                    WHEN Contractor THEN 'Contractor'" +
                           "                    WHEN Intern THEN 'Intern'" +
                           "                    ELSE " +
                           "       END " +
                           "FROM Employee e";

        jpqlQuery = jpqlQuery.replaceAll("\\s+", " ");

        int startPosition = jpqlQuery.length() - "END FROM Employee e".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CaseExpression_MissingElseExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CaseExpression_MissingElseIdentifier_1() throws Exception {

        String jpqlQuery = "UPDATE Employee e " +
                           "SET e.salary = " +
                           "    CASE WHEN e.rating = 1 THEN e.salary * 1.1 " +
                           "         WHEN e.rating = 2 THEN e.salary * 1.05" +
                           "    END";

        jpqlQuery = jpqlQuery.replaceAll("\\s+", " ");

        int startPosition = jpqlQuery.length() - Expression.END.length();
        int endPosition = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CaseExpression_MissingElseIdentifier,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CaseExpression_MissingElseIdentifier_2() throws Exception {

        String jpqlQuery = "UPDATE Employee e " +
                           "SET e.salary = " +
                           "    CASE WHEN e.rating = 1 THEN e.salary * 1.1 " +
                           "         WHEN e.rating = 2 THEN e.salary * 1.05 " +
                           "    END";

        jpqlQuery = jpqlQuery.replaceAll("\\s+", " ");

        int startPosition = jpqlQuery.length() - Expression.END.length();
        int endPosition = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CaseExpression_MissingElseIdentifier,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CaseExpression_MissingEndIdentifier() throws Exception {

        String jpqlQuery = "UPDATE Employee e " +
                           "SET e.salary = " +
                           "    CASE WHEN e.rating = 1 THEN e.salary * 1.1 " +
                           "         WHEN e.rating = 2 THEN e.salary * 1.05 " +
                           "         ELSE e.salary * 1.01";

        jpqlQuery  = jpqlQuery.replaceAll("\\s+", " ");

        int startPosition = jpqlQuery.length();
        int endPosition = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CaseExpression_MissingEndIdentifier,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CaseExpression_MissingWhenClause() throws Exception {

        String jpqlQuery = "UPDATE Employee e " +
                           "SET e.salary = " +
                           "    CASE TYPE(e)" +
                           "    ELSE 'NonExempt'" +
                           "    END";

        jpqlQuery = jpqlQuery.replaceAll("\\s+", " ");

        int startPosition = "UPDATE Employee e SET e.salary = CASE TYPE(e) ".length();
        int endPosition = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CaseExpression_MissingWhenClause,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CoalesceExpression_InvalidExpression() throws Exception {

        String jpqlQuery = "SELECT COALESCE(SELECT e) FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isSubqueryAllowedAnywhere()) {
            testDoesNotHaveProblem(
                problems,
                CoalesceExpression_InvalidExpression
            );
        }
        else {
            int startPosition = "SELECT COALESCE(".length();
            int endPosition   = "SELECT COALESCE(SELECT e".length();

            testHasOnlyOneProblem(
                problems,
                CoalesceExpression_InvalidExpression,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public final void test_CoalesceExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT COALESCE() FROM Employee e";
        int startPosition = "SELECT COALESCE(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CoalesceExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CoalesceExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT COALESCE 4 + 5) FROM Employee e";
        int startPosition = "SELECT COALESCE".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CoalesceExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CoalesceExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT COALESCE(e.age + 100 FROM Employee e";
        int startPosition = "SELECT COALESCE(e.age + 100".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CoalesceExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionMemberDeclaration_MissingCollectionValuedPathExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e, IN() AS m";
        int startPosition = "SELECT e FROM Employee e, IN(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CollectionMemberDeclaration_MissingCollectionValuedPathExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionMemberDeclaration_MissingIdentificationVariable_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e, IN(e.employees) AS";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CollectionMemberDeclaration_MissingIdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionMemberDeclaration_MissingIdentificationVariable_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e, IN(e.employees) AS ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CollectionMemberDeclaration_MissingIdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionMemberDeclaration_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e, IN e.managers) AS m";
        int startPosition = "SELECT e FROM Employee e, IN".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CollectionMemberDeclaration_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionMemberDeclaration_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e, IN(e.employees AS m";
        int startPosition = "SELECT e FROM Employee e, IN(e.employees".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CollectionMemberDeclaration_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionMemberExpression_MissingCollectionValuedPathExpression() throws Exception {

        String jpqlQuery  = "SELECT e, f FROM Employee e WHERE e.name MEMBER OF ";
        int startPosition = "SELECT e, f FROM Employee e WHERE e.name MEMBER OF ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CollectionMemberExpression_MissingCollectionValuedPathExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionMemberExpression_MissingEntityExpression() throws Exception {

        String jpqlQuery  = "SELECT e, f FROM Employee e WHERE MEMBER f.offices";
        int startPosition = "SELECT e, f FROM Employee e WHERE ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CollectionMemberExpression_MissingEntityExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotCollectionType_1() throws Exception {

        String jpqlQuery  = "SELECT c FROM Customer c WHERE c.lastName MEMBER 6";
        int startPosition = "SELECT c FROM Customer c WHERE c.lastName MEMBER ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CollectionValuedPathExpression_NotCollectionType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotCollectionType_2() throws Exception {

        String jpqlQuery  = "SELECT c FROM Customer c WHERE 6 IS EMPTY";
        int startPosition = "SELECT c FROM Customer c WHERE ".length();
        int endPosition   = "SELECT c FROM Customer c WHERE 6".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CollectionValuedPathExpression_NotCollectionType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_MissingLeftExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM EMPLOYEE e WHERE > 20";
        int startPosition = "SELECT e FROM EMPLOYEE e WHERE ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ComparisonExpression_MissingLeftExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_MissingLeftRightExpression() throws Exception {

        String jpqlQuery   = "SELECT e FROM EMPLOYEE e WHERE >";

        int startPosition1 = "SELECT e FROM EMPLOYEE e WHERE ".length();
        int endPosition1   = startPosition1;

        int startPosition2 = "SELECT e FROM EMPLOYEE e WHERE >".length();
        int endPosition2   = startPosition2;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblems(
            problems,
            new String [] { ComparisonExpression_MissingLeftExpression,
                            ComparisonExpression_MissingRightExpression },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1,   endPosition2   }
        );
    }

    @Test
    public final void test_ComparisonExpression_MissingRightExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM EMPLOYEE e WHERE e.age >";
        int startPosition = "SELECT e FROM EMPLOYEE e WHERE e.age >".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ComparisonExpression_MissingRightExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConcatExpression_InvalidFirstExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE CONCAT(ALL(SELECT d FROM Dept d), e.lastName)";
        int startPosition = "SELECT e FROM Employee e WHERE CONCAT(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE CONCAT(ALL(SELECT d FROM Dept d)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ConcatExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConcatExpression_InvalidSecondExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE CONCAT(e.firstName, ALL(SELECT d FROM Dept d))";
        int startPosition = "SELECT e FROM Employee e WHERE CONCAT(e.firstName, ".length();
        int endPosition   = jpqlQuery.length() - 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ConcatExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConcatExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE CONCAT e.firstName, e.lastName)";
        int startPosition = "SELECT e FROM Employee e WHERE CONCAT".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ConcatExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConcatExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE CONCAT(e.firstName, e.lastName";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ConcatExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConstructorExpression_ConstructorItemEndsWithComma() throws Exception {

        String jpqlQuery  = "SELECT NEW project1.Employee(e.name,) FROM Employee e";
        int startPosition = "SELECT NEW project1.Employee(e.name".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ConstructorExpression_ConstructorItemEndsWithComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConstructorExpression_ConstructorItemIsMissingComma() throws Exception {

        String jpqlQuery  = "SELECT NEW project1.Employee(e.name e.age) FROM Employee e";
        int startPosition = "SELECT NEW project1.Employee(e.name".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ConstructorExpression_ConstructorItemIsMissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConstructorExpression_MissingConstructorItem() throws Exception {

        String jpqlQuery  = "SELECT NEW project1.Employee() FROM Employee e";
        int startPosition = "SELECT NEW project1.Employee(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ConstructorExpression_MissingConstructorItem,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConstructorExpression_MissingConstructorName() throws Exception {

        String jpqlQuery  = "SELECT NEW (e.name) FROM Employee e";
        int startPosition = "SELECT NEW ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ConstructorExpression_MissingConstructorName,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConstructorExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT NEW project1.Employee e.name) FROM Employee e";
        int startPosition = "SELECT NEW project1.Employee".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ConstructorExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConstructorExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT NEW project1.Employee(e.name FROM Employee e";
        int startPosition = "SELECT NEW project1.Employee(e.name".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ConstructorExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CountFunction_InvalidExpression() throws Exception {

        String jpqlQuery = "SELECT COUNT(AVG(e.age)) FROM Employee e";

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        JPQLQueryBNF queryBNF = getQueryBNF(InternalCountBNF.ID);

        if (queryBNF.getExpressionFactory(AvgFunctionFactory.ID) == null) {

            int startPosition = "SELECT COUNT(".length();
            int endPosition   = "SELECT COUNT(AVG(e.age)".length();

            testHasOnlyOneProblem(
                problems,
                CountFunction_InvalidExpression,
                startPosition,
                endPosition
            );
        }
        else {
            testHasNoProblems(problems);
        }
    }

    @Test
    public final void test_CountFunction_MissingExpression_1() throws Exception {

        String jpqlQuery  = "SELECT COUNT() FROM Employee e";
        int startPosition = "SELECT COUNT(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CountFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CountFunction_MissingExpression_2() throws Exception {

        String jpqlQuery  = "SELECT COUNT(DISTINCT) FROM Employee e";
        int startPosition = "SELECT COUNT(DISTINCT".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CountFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CountFunction_MissingExpression_3() throws Exception {

        String jpqlQuery  = "SELECT COUNT(DISTINCT ) FROM Employee e";
        int startPosition = "SELECT COUNT(DISTINCT ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_2());

        testHasOnlyOneProblem(
            problems,
            CountFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CountFunction_MissingLeftParenthesis_1() throws Exception {

        String jpqlQuery  = "SELECT COUNT e.age) FROM Employee e";
        int startPosition = "SELECT COUNT".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CountFunction_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CountFunction_MissingLeftParenthesis_2() throws Exception {

        String jpqlQuery  = "SELECT COUNT DISTINCT e.age) FROM Employee e";
        int startPosition = "SELECT COUNT".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CountFunction_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CountFunction_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT COUNT(DISTINCT e.age FROM Employee e";
        int startPosition = "SELECT COUNT(DISTINCT e.age".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            CountFunction_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DateTime_JDBCEscapeFormat_InvalidSpecification() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.startDate < {jdbc '2008-12-31'}";
        int startPosition = "SELECT e FROM Employee e WHERE e.startDate < {".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.startDate < {jdbc".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            DateTime_JDBCEscapeFormat_InvalidSpecification,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DateTime_JDBCEscapeFormat_MissingCloseQuote() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.startDate < {d '2008-12-31}";
        int startPosition = "SELECT e FROM Employee e WHERE e.startDate < {d '2008-12-31".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            DateTime_JDBCEscapeFormat_MissingCloseQuote,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DateTime_JDBCEscapeFormat_MissingOpenQuote() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.startDate < {d 2008-12-31'}";
        int startPosition = "SELECT e FROM Employee e WHERE e.startDate < {d ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            DateTime_JDBCEscapeFormat_MissingOpenQuote,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DateTime_JDBCEscapeFormat_MissingRightCurlyBrace() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.startDate < {d '2008-12-31'";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            DateTime_JDBCEscapeFormat_MissingRightCurlyBrace,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DeleteClause_FromMissing_1() throws Exception {

        String jpqlQuery  = "DELETE";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            DeleteClause_FromMissing,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DeleteClause_FromMissing_2() throws Exception {

        String jpqlQuery  = "DELETE ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            DeleteClause_FromMissing,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DeleteClause_FromMissing_3() throws Exception {

        String jpqlQuery  = "DELETE Employee e WHERE e.name = 'Pascal'";
        int startPosition = "DELETE ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            DeleteClause_FromMissing,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DeleteClause_MultipleRangeVariableDeclaration() throws Exception {

        String jpqlQuery  = "DELETE FROM Employee e, Address a WHERE e.name = 'Pascal'";
        int startPosition = "DELETE FROM Employee e".length();
        int endPosition   = "DELETE FROM Employee e, Address a".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            DeleteClause_MultipleRangeVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DeleteClause_RangeVariableDeclarationMalformed_1() throws Exception {

        String jpqlQuery  = "DELETE FROM Employee e a WHERE e.name = 'Pascal'";
        int startPosition = "DELETE FROM Employee e".length();
        int endPosition   = "DELETE FROM Employee e a".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            DeleteClause_RangeVariableDeclarationMalformed,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DeleteClause_RangeVariableDeclarationMalformed_2() throws Exception {

        String jpqlQuery  = "Delete from Employee e join e.address a where e.id < 0";
        int startPosition = "Delete from Employee e".length();
        int endPosition   = "Delete from Employee e join e.address a".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            DeleteClause_RangeVariableDeclarationMalformed,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DeleteClause_RangeVariableDeclarationMalformed_FromMissing() throws Exception {

        String jpqlQuery   = "DELETE Employee e a WHERE e.name = 'Pascal'";
        int startPosition1 = "DELETE ".length();
        int endPosition1   = startPosition1;
        int startPosition2 = "DELETE Employee e".length();
        int endPosition2   = "DELETE Employee e a".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblems(
            problems,
            new String[] {
                DeleteClause_FromMissing,
                DeleteClause_RangeVariableDeclarationMalformed,
            },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1,   endPosition2   }
        );
    }

    @Test
    public final void test_DeleteClause_RangeVariableDeclarationMissing_1() throws Exception {

        String jpqlQuery = "DELETE ";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DeleteClause_RangeVariableDeclarationMissing);
    }

    @Test
    public final void test_DeleteClause_RangeVariableDeclarationMissing_2() throws Exception {

        String jpqlQuery  = "DELETE FROM";
        int startPosition = jpqlQuery.length() + 1;
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            DeleteClause_RangeVariableDeclarationMissing,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DeleteClause_RangeVariableDeclarationMissing_3() throws Exception {

        String jpqlQuery  = "DELETE FROM ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            DeleteClause_RangeVariableDeclarationMissing,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DeleteClause_RangeVariableDeclarationMissing_4() throws Exception {

        String jpqlQuery  = "DELETE FROM WHERE e.name = 'Pascal'";
        int startPosition = "DELETE FROM ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            DeleteClause_RangeVariableDeclarationMissing,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_EmptyCollectionComparisonExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE IS NOT EMPTY";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            EmptyCollectionComparisonExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_EntryExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT ENTRY(e.name) FROM Employee e";
        int startPosition = "SELECT ENTRY(".length();
        int endPosition   = "SELECT ENTRY(e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            EntryExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_EntryExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT ENTRY() FROM Employee e JOIN e.addresses a";
        int startPosition = "SELECT ENTRY(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            EntryExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_EntryExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT ENTRY a) FROM Employee e JOIN e.addresses a";
        int startPosition = "SELECT ENTRY".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            EntryExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_EntryExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT ENTRY(a FROM Employee e JOIN e.addresses a";
        int startPosition = "SELECT ENTRY(a".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            EntryExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ExistsExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE EXISTS(e.name)";
        int startPosition = "SELECT e FROM Employee e WHERE EXISTS(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE EXISTS(e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ExistsExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ExistsExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.addresses a WHERE EXISTS()";
        int startPosition = "SELECT e FROM Employee e JOIN e.addresses a WHERE EXISTS(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ExistsExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ExistsExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.addresses a WHERE EXISTS SELECT p FROM Order p)";
        int startPosition = "SELECT e FROM Employee e JOIN e.addresses a WHERE EXISTS".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ExistsExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ExistsExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.addresses a WHERE EXISTS(SELECT p FROM Order p";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ExistsExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_GroupByClause_GroupByItemEndsWithComma() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e GROUP BY e.name, e.age,";
        int startPosition = jpqlQuery.length() - 1;
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            GroupByClause_GroupByItemEndsWithComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_GroupByClause_GroupByItemIsMissingComma_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e GROUP BY e.name e.age";
        int startPosition = "SELECT e FROM Employee e GROUP BY e.name".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            GroupByClause_GroupByItemIsMissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_GroupByClause_GroupByItemIsMissingComma_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e GROUP BY e.name, e.age e";
        int startPosition = "SELECT e FROM Employee e GROUP BY e.name, e.age".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            GroupByClause_GroupByItemIsMissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_GroupByClause_GroupByItemMissing() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e GROUP BY";
        int startPosition = jpqlQuery.length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            GroupByClause_GroupByItemMissing,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_HavingClause_ConditionalExpressionMissing_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e HAVING";
        int startPosition = "SELECT e FROM Employee e ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            HavingClause_MissingConditionalExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_HavingClause_ConditionalExpressionMissing_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e HAVING ";
        int startPosition = "SELECT e FROM Employee e ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            HavingClause_MissingConditionalExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_HavingClause_InvalidConditionalExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e HAVING LENGTH(e.name)";
        int startPosition = "SELECT e FROM Employee e HAVING ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            HavingClause_InvalidConditionalExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IdentificationVariable_Invalid_JavaIdentifier_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee 2e";
        int startPosition = "SELECT e FROM Employee ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            IdentificationVariable_Invalid_JavaIdentifier,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IdentificationVariable_Invalid_JavaIdentifier_2() throws Exception {

        String jpqlQuery  = "SELECT e!q FROM Employee e";
        int startPosition = "SELECT ".length();
        int endPosition   = "SELECT e!q".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            IdentificationVariable_Invalid_JavaIdentifier,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IdentificationVariable_Invalid_ReservedWord() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e, Adress AVG";
        int startPosition = "SELECT e FROM Employee e, Adress ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            IdentificationVariable_Invalid_ReservedWord,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IdentificationVariableDeclaration_JoinsEndWithComma_1() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            IdentificationVariableDeclaration_JoinsEndWithComma
        );
    }

    @Test
    public final void test_IdentificationVariableDeclaration_MissingRangeVariableDeclaration1() throws Exception {

        String jpqlQuery  = "SELECT e FROM JOIN e.managers m";
        int startPosition = "SELECT e FROM ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            IdentificationVariableDeclaration_MissingRangeVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IdentificationVariableDeclaration_MissingRangeVariableDeclaration2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.managers m, JOIN e.addresses a, Address add";
        int startPosition = "SELECT e FROM Employee e JOIN e.managers m, ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            IdentificationVariableDeclaration_MissingRangeVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IndexExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT INDEX(e.name) FROM Employee e";
        int startPosition = "SELECT INDEX(".length();
        int endPosition   = "SELECT INDEX(e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            IndexExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IndexExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT INDEX() FROM Employee e";
        int startPosition = "SELECT INDEX(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            IndexExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IndexExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT INDEX e) FROM Employee e";
        int startPosition = "SELECT INDEX".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            IndexExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IndexExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT INDEX(e FROM Employee e";
        int startPosition = "SELECT INDEX(e".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            IndexExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InExpression_ItemEndWithComma() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN(?1, ?2,)";
        int startPosition = "SELECT e FROM Employee e WHERE e.name IN(?1, ?2".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InExpression_ItemEndsWithComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InExpression_ItemInvalidExpression_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN :age";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            InExpression_ItemInvalidExpression
        );
    }

    @Test
    public final void test_InExpression_ItemInvalidExpression_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN (:age)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            InExpression_ItemInvalidExpression
        );
    }

    @Test
    public final void test_InExpression_ItemInvalidExpression_4() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN (SELECT a FROM Address a)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            InExpression_ItemInvalidExpression
        );
    }

    @Test
    public final void test_InExpression_ItemIsMissingComma() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN(?1 ?2)";
        int startPosition = "SELECT e FROM Employee e WHERE e.name IN(?1".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InExpression_ItemIsMissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE IN :age";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InExpression_MissingInItems() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN()";
        int startPosition = "SELECT e FROM Employee e WHERE e.name IN(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InExpression_MissingInItems,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InExpression_MissingLeftParenthesis_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN e.address.street)";
        int startPosition = "SELECT e FROM Employee e WHERE e.name IN".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InExpression_MissingLeftParenthesis_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IN :name";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, InExpression_MissingLeftParenthesis);
    }

    @Test
    public final void test_InExpression_MissingRightParenthesis_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN('JPQL', 'JPA'";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InExpression_MissingRightParenthesis_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN :name";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, InExpression_MissingRightParenthesis);
    }

    @Test
    public final void test_InputParameter_JavaIdentifier_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE :2AVG = 'Pascal'";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE :2AVG".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InputParameter_JavaIdentifier,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InputParameter_JavaIdentifier_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE :e!qb = 'Pascal'";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE :e!qb".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InputParameter_JavaIdentifier,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InputParameter_MissingParameter_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE ? = 'Pascal'";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InputParameter_MissingParameter,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InputParameter_MissingParameter_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE : = 'Pascal'";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InputParameter_MissingParameter,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InputParameter_Mixture() throws Exception {

        String jpqlQuery   = "SELECT e FROM Employee e WHERE :name = 'Pascal' AND e.age < ?1";

        int startPosition1 = "SELECT e FROM Employee e WHERE ".length();
        int endPosition1   = "SELECT e FROM Employee e WHERE :name".length();

        int startPosition2 = "SELECT e FROM Employee e WHERE :name = 'Pascal' AND e.age < ".length();
        int endPosition2   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblems(
            problems,
            new String[] { InputParameter_Mixture, InputParameter_Mixture },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1,   endPosition2 }
        );
    }

    @Test
    public final void test_InputParameter_NotInteger_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE ?1a = 'Pascal'";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE ?1a".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InputParameter_NotInteger,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InputParameter_NotInteger_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE ?1.1 = 'Pascal'";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE ?1.1".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InputParameter_NotInteger,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InputParameter_SmallerThanOne_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE ?0 = 'Pascal'";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE ?0".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            InputParameter_SmallerThanOne,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InvalidQuery_01() throws Exception {

        String jpqlQuery   = "delete aaa aaa a";

        int startPosition1 = "delete ".length();
        int endPosition1   = "delete ".length();

        int startPosition2 = "delete aaa aaa".length();
        int endPosition2   = "delete aaa aaa a".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyTheseProblems(
            problems,
            new String[] { DeleteClause_FromMissing, DeleteClause_RangeVariableDeclarationMalformed },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1,   endPosition2 }
        );
    }

    @Test
    public final void test_Join_InvalidIdentifier_01() throws Exception {

        String jpqlQuery  = "SELECT r FROM Employee r LEFT OUTER JOIN r.phoneNumbers c WHERE c.phoneNumber = :major AND c.area = :name AND r.working = true";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_Join_InvalidIdentifier_02() throws Exception {

        String jpqlQuery  = "SELECT r FROM Employee r OUTER JOIN r.phoneNumbers c WHERE c.phoneNumber = :major AND c.area = :name AND r.working = true";
        int startPosition = "SELECT r FROM Employee r ".length();
        int endPosition   = "SELECT r FROM Employee r OUTER JOIN".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            Join_InvalidIdentifier,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_Join_InvalidIdentifier_03() throws Exception {

        String jpqlQuery  = "SELECT r FROM Employee r OUTER INNER JOIN r.phoneNumbers c WHERE c.phoneNumber = :major AND c.area = :name AND r.working = true";
        int startPosition = "SELECT r FROM Employee r ".length();
        int endPosition   = "SELECT r FROM Employee r OUTER INNER JOIN".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            Join_InvalidIdentifier,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_Join_InvalidIdentifier_04() throws Exception {

        String jpqlQuery  = "SELECT r FROM Employee r LEFT INNER JOIN r.phoneNumbers c WHERE c.phoneNumber = :major AND c.area = :name AND r.working = true";
        int startPosition = "SELECT r FROM Employee r ".length();
        int endPosition   = "SELECT r FROM Employee r LEFT INNER JOIN".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            Join_InvalidIdentifier,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_Join_InvalidIdentifier_05() throws Exception {

        String jpqlQuery  = "SELECT r FROM Employee r LEFT OUTER INNER JOIN r.phoneNumbers c WHERE c.phoneNumber = :major AND c.area = :name AND r.working = true";
        int startPosition = "SELECT r FROM Employee r ".length();
        int endPosition   = "SELECT r FROM Employee r LEFT OUTER INNER JOIN".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            Join_InvalidIdentifier,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_Join_MissingIdentificationVariable_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.employees ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            Join_MissingIdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_Join_MissingIdentificationVariable_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.employees AS";
        int startPosition = "SELECT e FROM Employee e JOIN e.employees AS".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            Join_MissingIdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_Join_MissingIdentificationVariable_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.employees AS ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            Join_MissingIdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_Join_MissingJoinAssociationPath_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN ";
        int startPosition = "SELECT e FROM Employee e JOIN ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            Join_MissingJoinAssociationPath,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_Join_MissingJoinAssociationPath_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN AS p";
        int startPosition = "SELECT e FROM Employee e JOIN ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            Join_MissingJoinAssociationPath,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_JoinFetch_MissingIdentificationVariable_1() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e JOIN e.employees ";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, JoinFetch_MissingIdentificationVariable);
    }

    @Test
    public final void test_JoinFetch_MissingIdentificationVariable_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e JOIN FETCH e.employees AS";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isJoinFetchIdentifiable()) {
            int startPosition = jpqlQuery.length();
            int endPosition   = startPosition;

            testHasOnlyOneProblem(
                problems,
                JoinFetch_MissingIdentificationVariable,
                startPosition,
                endPosition
            );
        }
        else {
            int startPosition = "SELECT e FROM Employee e JOIN FETCH e.employees ".length();
            int endPosition   = jpqlQuery.length();

            testHasOnlyOneProblem(
                problems,
                JoinFetch_InvalidIdentification,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public final void test_JoinFetch_MissingIdentificationVariable_3() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e JOIN FETCH e.employees AS ";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isJoinFetchIdentifiable()) {
            int startPosition = jpqlQuery.length();
            int endPosition   = startPosition;

            testHasOnlyOneProblem(
                problems,
                JoinFetch_MissingIdentificationVariable,
                startPosition,
                endPosition
            );
        }
        else {
            int startPosition = "SELECT e FROM Employee e JOIN FETCH e.employees ".length();
            int endPosition   = jpqlQuery.length();

            testHasOnlyOneProblem(
                problems,
                JoinFetch_InvalidIdentification,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public final void test_JoinFetch_MissingIdentificationVariable_4() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e JOIN FETCH e.employees AS emp";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isJoinFetchIdentifiable()) {
            testDoesNotHaveProblem(
                problems,
                JoinFetch_MissingIdentificationVariable
            );
        }
        else {
            int startPosition = "SELECT e FROM Employee e JOIN FETCH e.employees ".length();
            int endPosition   = jpqlQuery.length();

            testHasOnlyOneProblem(
                problems,
                JoinFetch_InvalidIdentification,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public final void test_JoinFetch_MissingJoinAssociationPath_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN FETCH";
        int startPosition = "SELECT e FROM Employee e JOIN FETCH".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JoinFetch_MissingJoinAssociationPath,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_JoinFetch_MissingJoinAssociationPath_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN FETCH ";
        int startPosition = "SELECT e FROM Employee e JOIN FETCH ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JoinFetch_MissingJoinAssociationPath,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_JoinFetch_WrongClauseDeclaration() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE EXISTS(SELECT o FROM Order o JOIN FETCH o.stores)";
        int startPosition = "SELECT e FROM Employee e WHERE EXISTS(SELECT o FROM Order o ".length();
        int endPosition   = jpqlQuery.length() - 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JoinFetch_WrongClauseDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_JPQLExpression_InvalidQuery_1() throws Exception {

        String jpqlQuery  = ExpressionTools.EMPTY_STRING;
        int startPosition = 0;
        int endPosition   = 0;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JPQLExpression_InvalidQuery,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_JPQLExpression_InvalidQuery_2() throws Exception {

        String jpqlQuery  = "S";
        int startPosition = 0;
        int endPosition   = 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JPQLExpression_InvalidQuery,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_JPQLExpression_InvalidQuery_3() throws Exception {

        String jpqlQuery  = "JPA version 2.0";
        int startPosition = 0;
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JPQLExpression_InvalidQuery,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_JPQLExpression_InvalidQuery_4() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_JPQLExpression_InvalidQuery_5() throws Exception {

        String jpqlQuery = "UPDATE Employee e SET e.salary = e.salary * 1.20";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_JPQLExpression_InvalidQuery_6() throws Exception {

        String jpqlQuery = "DELETE FROM Employee e WHERE e.name = 'Pascal'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_JPQLExpression_UnknownEnding_1() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_JPQLExpression_UnknownEnding_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e FROM";
        int startPosition = "SELECT e FROM Employee e ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JPQLExpression_UnknownEnding,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_KeyExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT KEY(a.street) FROM Employee e JOIN e.addresses a";
        int startPosition = "SELECT KEY(".length();
        int endPosition   = "SELECT KEY(a.street".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            KeyExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_KeyExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT KEY() FROM Employee e JOIN e.addresses a";
        int startPosition = "SELECT KEY(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            KeyExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_KeyExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT KEY a) FROM Employee e JOIN e.addresses a";
        int startPosition = "SELECT KEY".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            KeyExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_KeyExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT KEY(a FROM Employee e JOIN e.addresses a";
        int startPosition = "SELECT KEY(a".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            KeyExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LengthExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LENGTH(ALL(SELECT d FROM Dept d)) = 2";
        int startPosition = "SELECT e FROM Employee e WHERE LENGTH(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LENGTH(ALL(SELECT d FROM Dept d)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LengthExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LengthExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LENGTH() = 2";
        int startPosition = "SELECT e FROM Employee e WHERE LENGTH(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LengthExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LengthExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LENGTH e.name) = 2";
        int startPosition = "SELECT e FROM Employee e WHERE LENGTH".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LengthExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LengthExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LENGTH(e.name = 2";
        int startPosition = "SELECT e FROM Employee e WHERE LENGTH(e.name".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LengthExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LikeExpression_InvalidEscapeCharacter_1() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE 'C'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            LikeExpression_InvalidEscapeCharacter
        );
    }

    @Test
    public final void test_LikeExpression_InvalidEscapeCharacter_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE :name";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            LikeExpression_InvalidEscapeCharacter
        );
    }

    @Test
    public final void test_LikeExpression_InvalidEscapeCharacter_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE 'CHAR'";
        int startPosition = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LikeExpression_InvalidEscapeCharacter,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LikeExpression_InvalidEscapeCharacter_4() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE UPPER('_')";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (jpqlGrammar == EclipseLinkJPQLGrammar2_1.instance() ||
            jpqlGrammar == EclipseLinkJPQLGrammar2_2.instance() ||
            jpqlGrammar == EclipseLinkJPQLGrammar2_3.instance() ||
            jpqlGrammar == EclipseLinkJPQLGrammar2_4.instance() ||
             jpqlGrammar == EclipseLinkJPQLGrammar2_5.instance()) {

            testDoesNotHaveProblem(
                problems,
                LikeExpression_InvalidEscapeCharacter
            );
        }
        else {
            int startPosition = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE ".length();
            int endPosition   = jpqlQuery.length();

            testHasProblem(
                problems,
                LikeExpression_InvalidEscapeCharacter,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public final void test_LikeExpression_MissingEscapeCharacter_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LikeExpression_MissingEscapeCharacter,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LikeExpression_MissingEscapeCharacter_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LikeExpression_MissingEscapeCharacter,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LikeExpression_MissingPatternValue_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name LIKE";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LikeExpression_MissingPatternValue,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LikeExpression_MissingPatternValue_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name LIKE ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LikeExpression_MissingPatternValue,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LikeExpression_MissingStringExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LIKE 'Pascal'";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LikeExpression_MissingStringExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_InvalidFirstExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(ALL(SELECT d FROM Dept d), 'JPQL', 2)";
        int startPosition = "SELECT e FROM Employee e WHERE LOCATE(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LOCATE(ALL(SELECT d FROM Dept d)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_InvalidFirstExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_InvalidSecondExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE('JPQL', ALL(SELECT d FROM Dept d), 2)";
        int startPosition = "SELECT e FROM Employee e WHERE LOCATE('JPQL', ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LOCATE('JPQL', ALL(SELECT d FROM Dept d)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_InvalidSecondExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_InvalidThirdExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(e.name, 'JPQL', e)";
        int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name, 'JPQL', ".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_InvalidThirdExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_MissingFirstComma_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(e.name";

        int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_MissingFirstComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_MissingFirstComma_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(e.name e.age, 2 + e.startDate)";
        int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_MissingFirstComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_MissingFirstExpression_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(";
        int startPosition = "SELECT e FROM Employee e WHERE LOCATE(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_MissingFirstExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_MissingFirstExpression_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(, e.age)";
        int startPosition = "SELECT e FROM Employee e WHERE LOCATE(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_MissingFirstExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_MissingFirstSecondExpression() throws Exception {

        String jpqlQuery   = "SELECT e FROM Employee e WHERE LOCATE(, ";

        int startPosition1 = "SELECT e FROM Employee e WHERE LOCATE(".length();
        int endPosition1   = startPosition1;

        int startPosition2 = "SELECT e FROM Employee e WHERE LOCATE(, ".length();
        int endPosition2   = startPosition2;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblems(
            problems,
            new String[] { LocateExpression_MissingFirstExpression,
                           LocateExpression_MissingSecondExpression },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1,   endPosition2 }
        );
    }

    @Test
    public final void test_LocateExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE e.name, e.age)";
        int startPosition = "SELECT e FROM Employee e WHERE LOCATE".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_MissingSecondComma() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age 2 + e.startDate)";
        int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_MissingSecondComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_MissingSecondExpression_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(e.name,";
        int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name,".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_MissingSecondExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_MissingSecondExpression_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(e.name, ";
        int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name, ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_MissingSecondExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_MissingThirdExpression_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age,";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_MissingThirdExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_MissingThirdExpression_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age, ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_MissingThirdExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_MissingThirdExpression_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age, )";
        int startPosition = jpqlQuery.length() - 1;
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_4());

        testHasProblem(
            problems,
            LocateExpression_MissingThirdExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LogicalExpression_InvalidLeftExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LENGTH(e.name) AND e.name = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LENGTH(e.name)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LogicalExpression_InvalidLeftExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LogicalExpression_InvalidRightExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name = 'JPQL' AND LENGTH(e.name)";
        int startPosition = "SELECT e FROM Employee e WHERE e.name = 'JPQL' AND ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LogicalExpression_InvalidRightExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LogicalExpression_MissingLeftExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE AND e.name = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LogicalExpression_MissingLeftExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LogicalExpression_MissingRightExpression_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name = 'JPQL' AND";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LogicalExpression_MissingRightExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LogicalExpression_MissingRightExpression_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name = 'JPQL' AND ";

        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LogicalExpression_MissingRightExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LowerExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOWER(ALL(SELECT d FROM Dept d)) = 'PASCAL'";
        int startPosition = "SELECT e FROM Employee e WHERE LOWER(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LOWER(ALL(SELECT d FROM Dept d)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LowerExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LowerExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOWER() = 'PASCAL'";
        int startPosition = "SELECT e FROM Employee e WHERE LOWER(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LowerExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LowerExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOWER e.name) = 'PASCAL'";
        int startPosition = "SELECT e FROM Employee e WHERE LOWER".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LowerExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LowerExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOWER(e.name = 'PASCAL'";
        int startPosition = "SELECT e FROM Employee e WHERE LOWER(e.name".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            LowerExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MaxFunction_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT MAX(e) FROM Employee e";
        int startPosition = "SELECT MAX(".length();
        int endPosition   = "SELECT MAX(e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            MaxFunction_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MaxFunction_MissingExpression_1() throws Exception {

        String jpqlQuery  = "SELECT MAX() FROM Employee e";
        int startPosition = "SELECT MAX(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            MaxFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MaxFunction_MissingExpression_2() throws Exception {

        String jpqlQuery  = "SELECT MAX(DISTINCT) FROM Employee e";
        int startPosition = "SELECT MAX(DISTINCT".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            MaxFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MaxFunction_MissingExpression_3() throws Exception {

        String jpqlQuery  = "SELECT MAX(DISTINCT ) FROM Employee e";
        int startPosition = "SELECT MAX(DISTINCT ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_2());

        testHasOnlyOneProblem(
            problems,
            MaxFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MaxFunction_MissingLeftParenthesis_1() throws Exception {

        String jpqlQuery  = "SELECT MAX e.age) FROM Employee e";
        int startPosition = "SELECT MAX".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            MaxFunction_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MaxFunction_MissingLeftParenthesis_2() throws Exception {

        String jpqlQuery  = "SELECT MAX DISTINCT e.age) FROM Employee e";
        int startPosition = "SELECT MAX".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            MaxFunction_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MaxFunction_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT MAX(DISTINCT e.age FROM Employee e";
        int startPosition = "SELECT MAX(DISTINCT e.age".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            MaxFunction_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MinFunction_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT MIN(e) FROM Employee e";
        int startPosition = "SELECT MIN(".length();
        int endPosition   = "SELECT MIN(e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            MinFunction_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MinFunction_MissingExpression_1() throws Exception {

        String jpqlQuery  = "SELECT MIN() FROM Employee e";
        int startPosition = "SELECT MIN(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            MinFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MinFunction_MissingExpression_2() throws Exception {

        String jpqlQuery  = "SELECT MIN(DISTINCT) FROM Employee e";
        int startPosition = "SELECT MIN(DISTINCT".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            MinFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MinFunction_MissingExpression_3() throws Exception {

        String jpqlQuery  = "SELECT MIN(DISTINCT ) FROM Employee e";
        int startPosition = "SELECT MIN(DISTINCT ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_2());

        testHasOnlyOneProblem(
            problems,
            MinFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MinFunction_MissingLeftParenthesis_1() throws Exception {

        String jpqlQuery  = "SELECT MIN e.age) FROM Employee e";
        int startPosition = "SELECT MIN".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            MinFunction_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MinFunction_MissingLeftParenthesis_2() throws Exception {

        String jpqlQuery  = "SELECT MIN DISTINCT e.age) FROM Employee e";
        int startPosition = "SELECT MIN".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            MinFunction_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MinFunction_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT MIN(DISTINCT e.age FROM Employee e";
        int startPosition = "SELECT MIN(DISTINCT e.age".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            MinFunction_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_InvalidFirstExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD(ALL(SELECT d FROM Dept d), e.age)";
        int startPosition = "SELECT e FROM Employee e WHERE MOD(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE MOD(ALL(SELECT d FROM Dept d)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ModExpression_InvalidFirstExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_InvalidSecondExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD(e.name, ALL(SELECT d FROM Dept d))";
        int startPosition = "SELECT e FROM Employee e WHERE MOD(e.name, ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE MOD(e.name, ALL(SELECT d FROM Dept d)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ModExpression_InvalidSecondExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_MissingComma_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD(e.name";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ModExpression_MissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_MissingComma_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD(e.name e.age)";
        int startPosition = "SELECT e FROM Employee e WHERE MOD(e.name".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ModExpression_MissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_MissingFirstExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD(, e.age)";
        int startPosition = "SELECT e FROM Employee e WHERE MOD(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ModExpression_MissingFirstExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_MissingLeftParenthesis_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ModExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_MissingLeftParenthesis_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD e.name, e.age)";
        int startPosition = "SELECT e FROM Employee e WHERE MOD".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ModExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD(e.name, e.age";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ModExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_MissingSecondExpression_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD(e.name,";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ModExpression_MissingSecondExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_MissingSecondExpression_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD(e.name, ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ModExpression_MissingSecondExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_MissingSecondExpression_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD(e.name, )";
        int startPosition = jpqlQuery.length() - 1;
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_5());

        testHasProblem(
            problems,
            ModExpression_MissingSecondExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NotExpression_MissingExpression_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE NOT";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            NotExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NotExpression_MissingExpression_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE NOT ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            NotExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NullIfExpression_InvalidFirstExpression() throws Exception {

        String jpqlQuery  = "SELECT NULLIF(e, 4 + e.age) FROM Employee e";
        int startPosition = "SELECT NULLIF(".length();
        int endPosition   = "SELECT NULLIF(e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            NullIfExpression_InvalidFirstExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NullIfExpression_InvalidSecondExpression() throws Exception {

        String jpqlQuery  = "SELECT NULLIF('JPQL', e) FROM Employee e";
        int startPosition = "SELECT NULLIF('JPQL', ".length();
        int endPosition   = "SELECT NULLIF('JPQL', e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            NullIfExpression_InvalidSecondExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NullIfExpression_MissingComma() throws Exception {

        String jpqlQuery  = "SELECT NULLIF('JPQL' 4 + e.age) FROM Employee e";
        int startPosition = "SELECT NULLIF('JPQL'".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            NullIfExpression_MissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NullIfExpression_MissingFirstExpression_1() throws Exception {

        String jpqlQuery  = "SELECT NULLIF( FROM Employee e";
        int startPosition = "SELECT NULLIF(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            NullIfExpression_MissingFirstExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NullIfExpression_MissingFirstExpression_2() throws Exception {

        String jpqlQuery  = "SELECT NULLIF(, 4 + e.age) FROM Employee e";
        int startPosition = "SELECT NULLIF(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            NullIfExpression_MissingFirstExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NullIfExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT NULLIF 'JPQL', 4 + e.age) FROM Employee e";
        int startPosition = "SELECT NULLIF".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            NullIfExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NullIfExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT NULLIF('JPQL', 4 + e.age FROM Employee e";
        int startPosition = "SELECT NULLIF('JPQL', 4 + e.age".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            NullIfExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NullIfExpression_MissingSecondExpression_1() throws Exception {

        String jpqlQuery  = "SELECT NULLIF('JPQL', FROM Employee e";
        int startPosition = "SELECT NULLIF('JPQL', ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            NullIfExpression_MissingSecondExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NullIfExpression_MissingSecondExpression_2() throws Exception {

        String jpqlQuery  = "SELECT NULLIF('JPQL',) FROM Employee e";
        int startPosition = "SELECT NULLIF('JPQL',".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            NullIfExpression_MissingSecondExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NullIfExpression_MissingSecondExpression_3() throws Exception {

        String jpqlQuery  = "SELECT NULLIF('JPQL', ) FROM Employee e";
        int startPosition = "SELECT NULLIF('JPQL', ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_6());

        testHasOnlyOneProblem(
            problems,
            NullIfExpression_MissingSecondExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NumericLiteral_Invalid_01() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.age > 1.2FF";
        int startPosition = "SELECT e FROM Employee e WHERE e.age > ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            NumericLiteral_Invalid,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NumericLiteral_Invalid_02() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            NumericLiteral_Invalid
        );
    }

    @Test
    public final void test_NumericLiteral_Invalid_03() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > 2.2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            NumericLiteral_Invalid
        );
    }

    @Test
    public final void test_NumericLiteral_Invalid_04() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > 2.2D";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            NumericLiteral_Invalid
        );
    }

    @Test
    public final void test_NumericLiteral_Invalid_05() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > 2.2E10";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            NumericLiteral_Invalid
        );
    }

    @Test
    public final void test_NumericLiteral_Invalid_06() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > 2.2E-10";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            NumericLiteral_Invalid
        );
    }

    @Test
    public final void test_NumericLiteral_Invalid_07() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > 0.34999999999999997779553950749686919152736663818359375";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            NumericLiteral_Invalid
        );
    }

    @Test
    public final void test_NumericLiteral_Invalid_08() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > 2.2F";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            NumericLiteral_Invalid
        );
    }

    @Test
    public final void test_NumericLiteral_Invalid_09() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > 2L";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, NumericLiteral_Invalid);
    }

    @Test
    public final void test_NumericLiteral_Invalid_10() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > 2l";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, NumericLiteral_Invalid);
    }

    @Test
    public final void test_NumericLiteral_Invalid_11() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > 2D";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, NumericLiteral_Invalid);
    }

    @Test
    public final void test_NumericLiteral_Invalid_12() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > 2d";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, NumericLiteral_Invalid);
    }

    @Test
    public final void test_NumericLiteral_Invalid_13() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > 2.2d";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, NumericLiteral_Invalid);
    }

    @Test
    public final void test_NumericLiteral_Invalid_14() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > .2d";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, NumericLiteral_Invalid);
    }

    @Test
    public final void test_NumericLiteral_Invalid_15() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.age > 0x1.02ADP+2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, NumericLiteral_Invalid);
    }

    @Test
    public final void test_NumericLiteral_Invalid_16() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.age > 0x1.02ADP+2d";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, NumericLiteral_Invalid);
    }

    @Test
    public final void test_ObjectExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT OBJECT(e.name) FROM Employee e";
        int startPosition = "SELECT OBJECT(".length();
        int endPosition   = "SELECT OBJECT(e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ObjectExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ObjectExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT OBJECT() FROM Employee e";
        int startPosition = "SELECT OBJECT(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ObjectExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ObjectExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT OBJECT e) FROM Employee e";
        int startPosition = "SELECT OBJECT".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ObjectExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ObjectExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT OBJECT(e FROM Employee e";
        int startPosition = "SELECT OBJECT(e".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ObjectExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByClause_OrderByItemEndsWithComma() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY e.name, e.age,";
        int startPosition = jpqlQuery.length() - 1;
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByClause_OrderByItemEndsWithComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByClause_OrderByItemMissing() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY";
        int startPosition = jpqlQuery.length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByClause_OrderByItemMissing,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_01() throws Exception {
        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY e.age";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_02() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY e.age ORDER";
        int startPosition = "SELECT e FROM Employee e ORDER BY ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByItem_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_03() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY e.age GROUP";
        int startPosition = "SELECT e FROM Employee e ORDER BY ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByItem_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_04() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY e.age SELECT";
        int startPosition = "SELECT e FROM Employee e ORDER BY ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByItem_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_05() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY e.age nulls order";

        int startPosition = "SELECT e FROM Employee e ORDER BY e.age nulls".length();
        int endPosition   = "SELECT e FROM Employee e ORDER BY e.age nulls ".length();
        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_9());

        testHasOnlyOneProblem(
            problems,
            OrderByClause_OrderByItemIsMissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_06() throws Exception {

        String jpqlQuery  = "SELECT d FROM DiscountCode d ORDER BY d.rate ORDER BY";
        int startPosition = "SELECT d FROM DiscountCode d ORDER BY d.rate ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            JPQLExpression_UnknownEnding,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_07() throws Exception {

        String jpqlQuery  = "SELECT d FROM DiscountCode d ORDER BY d.rate SUBSTRING";
        int startPosition = "SELECT d FROM DiscountCode d ORDER BY ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByItem_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_08() throws Exception {

        String jpqlQuery  = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC ORDER, f.address DESC, g.phone";

        int startPosition = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC".length();
        int endPosition   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC ".length();
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByClause_OrderByItemIsMissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_09() throws Exception {

        String jpqlQuery  = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS ORDER, f.address DESC, g.phone";
        int startPosition = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS".length();
        int endPosition   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS ".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByClause_OrderByItemIsMissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_10() throws Exception {

        String jpqlQuery  = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS SELECT, f.address DESC, g.phone";

        int startPosition1 = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS".length();
        int endPosition1   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS ".length();
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isSubqueryInOrderByClauseSupported()) {

            int startPosition2 = jpqlQuery.length();
            int endPosition2   = jpqlQuery.length();

            int startPosition3 = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS SELECT".length();
            int endPosition3   = jpqlQuery.length();

            testHasOnlyTheseProblems(
                problems,
                new String[] {
                    OrderByClause_OrderByItemIsMissingComma,
                    AbstractSelectStatement_FromClauseMissing,
                    SimpleSelectClause_NotSingleExpression
                },
                new int[] {
                    startPosition1,
                    startPosition2,
                    startPosition3
                },
                new int[] {
                    endPosition1,
                    endPosition2,
                    endPosition3
                }
            );
        }
        else {
            int startPosition2 = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS ".length();
            int endPosition2   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS SELECT, f.address DESC, g.phone".length();

            testHasOnlyTheseProblems(
                problems,
                new String[] { OrderByClause_OrderByItemIsMissingComma, OrderByItem_InvalidExpression },
                new int[] { startPosition1, startPosition2 },
                new int[] { endPosition1, endPosition2 }
            );
        }
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_11() throws Exception {

        String jpqlQuery   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS ORDER FIRST, f.address DESC, g.phone";

        int startPosition1 = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS".length();
        int endPosition1   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS ".length();

        int startPosition2 = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS ".length();
        int endPosition2   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS ORDER FIRST".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyTheseProblems(
            problems,
            new String[] { OrderByClause_OrderByItemIsMissingComma, OrderByItem_InvalidExpression },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1, endPosition2 }
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_12() throws Exception {

        String jpqlQuery = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC ORDER NULLS FIRST, f.address DESC, g.phone";

        int startPosition = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC".length();
        int endPosition   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC ".length();
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByClause_OrderByItemIsMissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_13() throws Exception {

        String jpqlQuery   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC SUBSTRING NULLS FIRST, f.address DESC, g.phone";

        int startPosition1 = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC".length();
        int endPosition1   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC ".length();
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isScalarExpressionInOrderByClauseSupported()) {
            int startPosition2 = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC SUBSTRING".length();
            int endPosition2   = startPosition2;

            testHasOnlyTheseProblems(
                problems,
                new String[] { OrderByClause_OrderByItemIsMissingComma, SubstringExpression_MissingLeftParenthesis },
                new int[] { startPosition1, startPosition2 },
                new int[] { endPosition1, endPosition2 }
            );
        }
        else {
            int startPosition2 = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC ".length();
            int endPosition2   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC SUBSTRING".length();

            testHasOnlyTheseProblems(
                problems,
                new String[] { OrderByClause_OrderByItemIsMissingComma, OrderByItem_InvalidExpression },
                new int[] { startPosition1, startPosition2 },
                new int[] { endPosition1, endPosition2 }
            );
        }
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_14() throws Exception {

        String jpqlQuery   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS SUBSTRING FIRST, f.address DESC, g.phone";

        int startPosition1 = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS".length();
        int endPosition1   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS ".length();

        int startPosition2 = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS ".length();
        int endPosition2   = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC NULLS SUBSTRING FIRST".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyTheseProblems(
            problems,
            new String[] { OrderByClause_OrderByItemIsMissingComma, OrderByItem_InvalidExpression },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1, endPosition2 }
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_15() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY ALL(SELECT d FROM Dept d)";
        int startPosition = "SELECT e FROM Employee e ORDER BY ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByItem_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_16() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY e.name e.age";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        int startPosition = "SELECT e FROM Employee e ORDER BY ".length();
        int endPosition   = "SELECT e FROM Employee e ORDER BY e.name e.age".length();

        testHasOnlyOneProblem(
            problems,
            OrderByItem_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_17() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY e.name, e.age e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        int startPosition = "SELECT e FROM Employee e ORDER BY e.name, ".length();
        int endPosition   = jpqlQuery.length();

        testHasOnlyOneProblem(
            problems,
            OrderByItem_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_InvalidExpression_18() throws Exception {
        String jpqlQuery  = "SELECT order FROM Order order ORDER BY order";
        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_10(jpqlQuery));
        testHasNoProblems(problems);
    }

    @Test
    public final void test_OrderByItem_MissingExpression_01() throws Exception {
        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY e.age";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_OrderByItem_MissingExpression_02() throws Exception {
        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY e.age ASC";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_OrderByItem_MissingExpression_03() throws Exception {
        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY e.age DESC";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_OrderByItem_MissingExpression_04() throws Exception {
        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY e.age NULLS FIRST";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_OrderByItem_MissingExpression_05() throws Exception {
        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY e.age NULLS LAST";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_OrderByItem_MissingExpression_06() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY ASC";
        int startPosition = "SELECT e FROM Employee e ORDER BY ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByItem_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_MissingExpression_07() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY DESC";
        int startPosition = "SELECT e FROM Employee e ORDER BY ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByItem_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_MissingExpression_08() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY NULLS FIRST";
        int startPosition = "SELECT e FROM Employee e ORDER BY ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByItem_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_OrderByItem_MissingExpression_09() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e ORDER BY NULLS LAST";
        int startPosition = "SELECT e FROM Employee e ORDER BY ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            OrderByItem_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_RangeVariableDeclaration_MissingIdentificationVariable_1() throws Exception {

        String jpqlQuery  = "SELECT o FROM Order o, Employee";
        int startPosition = "SELECT o FROM Order o, Employee".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            RangeVariableDeclaration_MissingIdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_RangeVariableDeclaration_MissingIdentificationVariable_2() throws Exception {

        String jpqlQuery  = "SELECT o FROM Order o, Employee ";
        int startPosition = "SELECT o FROM Order o, Employee ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            RangeVariableDeclaration_MissingIdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_RangeVariableDeclaration_MissingIdentificationVariable_3() throws Exception {

        String jpqlQuery  = "SELECT o FROM Order o, Employee AS";
        int startPosition = "SELECT o FROM Order o, Employee AS".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            RangeVariableDeclaration_MissingIdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_RangeVariableDeclaration_MissingIdentificationVariable_4() throws Exception {

        String jpqlQuery  = "SELECT o FROM Order o, Employee AS ";
        int startPosition = "SELECT o FROM Order o, Employee AS ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            RangeVariableDeclaration_MissingIdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_RangeVariableDeclaration_MissingRootObject_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM AS e";
        int startPosition = "SELECT e FROM ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            RangeVariableDeclaration_MissingRootObject,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_RangeVariableDeclaration_MissingRootObject_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee AS e, AS d";
        int startPosition = "SELECT e FROM Employee AS e, ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            RangeVariableDeclaration_MissingRootObject,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_RangeVariableDeclaration_MissingRootObject_3() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET e.salary = e.salary * (1 / 100) WHERE EXISTS (SELECT p FROM e.projects p WHERE p.name LIKE :projectName)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ResultVariable_MissingResultVariable() throws Exception {

        String jpqlQuery  = "SELECT AVG(e.age) AS FROM Employee e";
        int startPosition = "SELECT AVG(e.age) AS ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ResultVariable_MissingResultVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ResultVariable_MissingSelectExpression() throws Exception {

        String jpqlQuery  = "SELECT AS e1 FROM Employee e";
        int startPosition = "SELECT ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ResultVariable_MissingSelectExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SimpleSelectClause_NotSingleExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE EXISTS(SELECT AVG(o.price), e FROM Order o)";
        int startPosition = "SELECT e FROM Employee e WHERE EXISTS(SELECT ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE EXISTS(SELECT AVG(o.price), e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SimpleSelectClause_NotSingleExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SizeExpression_InvalidMissing() throws Exception {

        String jpqlQuery  = "SELECT SIZE(e) FROM Employee e";
        int startPosition = "SELECT SIZE(".length();
        int endPosition   = "SELECT SIZE(e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SizeExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SizeExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT SIZE() FROM Employee e JOIN e.addresses a";
        int startPosition = "SELECT SIZE(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SizeExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SizeExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT SIZE a) FROM Employee e JOIN e.addresses a";
        int startPosition = "SELECT SIZE".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SizeExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SizeExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT SIZE(a.street FROM Employee e JOIN e.addresses a";
        int startPosition = "SELECT SIZE(a.street".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SizeExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SqrtExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT SQRT(EXISTS()) FROM Employee e";
        int startPosition = "SELECT SQRT(".length();
        int endPosition   = "SELECT SQRT(EXISTS()".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SqrtExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SqrtExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT SQRT() FROM Employee e";
        int startPosition = "SELECT SQRT(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SqrtExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SqrtExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT SQRT 4 + 5) FROM Employee e";
        int startPosition = "SELECT SQRT".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SqrtExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SqrtExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT SQRT(e.age + 100 FROM Employee e";
        int startPosition = "SELECT SQRT(e.age + 100".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SqrtExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE () = 2";
        int startPosition = "SELECT e FROM Employee e WHERE (".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SubExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE (2 + e.age";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubstringExpression_InvalidFirstExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUBSTRING(e, 0, 1)";
        int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubstringExpression_InvalidFirstExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubstringExpression_InvalidSecondExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, e, 1)";
        int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubstringExpression_InvalidSecondExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubstringExpression_InvalidThirdExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0, e)";
        int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0, ".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubstringExpression_InvalidThirdExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubstringExpression_MissingFirstComma() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUBSTRING(e.name 0, 1)";
        int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubstringExpression_MissingFirstComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubstringExpression_MissingFirstExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUBSTRING(, 0, 1)";
        int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubstringExpression_MissingFirstExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubstringExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUBSTRING e.name, 0, 1)";
        int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubstringExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubstringExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0, 1";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubstringExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubstringExpression_MissingSecondComma() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0 1)";
        int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubstringExpression_MissingSecondComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubstringExpression_MissingSecondExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, , 1)";
        int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_8());

        testHasProblem(
            problems,
            SubstringExpression_MissingSecondExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubstringExpression_MissingThirdExpression_1() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0,";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (jpqlGrammar.getJPAVersion().isNewerThan(JPAVersion.VERSION_1_0)) {
            int startPosition = jpqlQuery.length();
            int endPosition   = startPosition;

            testHasProblem(
                problems,
                SubstringExpression_MissingThirdExpression,
                startPosition,
                endPosition
            );
        }
        else {
            testDoesNotHaveProblem(
                problems,
                SubstringExpression_MissingThirdExpression
            );
        }
    }

    @Test
    public final void test_SubstringExpression_MissingThirdExpression_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0, ";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (jpqlGrammar.getJPAVersion().isNewerThan(JPAVersion.VERSION_1_0)) {
            int startPosition = jpqlQuery.length();
            int endPosition   = startPosition;

            testHasProblem(
                problems,
                SubstringExpression_MissingThirdExpression,
                startPosition,
                endPosition
            );
        }
        else {
            testDoesNotHaveProblem(
                problems,
                SubstringExpression_MissingThirdExpression
            );
        }
    }

    @Test
    public final void test_SubstringExpression_MissingThirdExpression_3() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0, )";
        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_7());

        if (jpqlGrammar.getJPAVersion().isNewerThan(JPAVersion.VERSION_1_0)) {
            int startPosition = jpqlQuery.length() - 1;
            int endPosition   = startPosition;

            testHasProblem(
                problems,
                SubstringExpression_MissingThirdExpression,
                startPosition,
                endPosition
            );
        }
        else {
            testDoesNotHaveProblem(
                problems,
                SubstringExpression_MissingThirdExpression
            );
        }
    }

    @Test
    public final void test_SubstringExpression_MissingThirdExpression_4() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0)";

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, SubstringExpression_MissingThirdExpression);
    }

    @Test
    public final void test_SumFunction_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT SUM(e) FROM Employee e";
        int startPosition = "SELECT SUM(".length();
        int endPosition   = "SELECT SUM(e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SumFunction_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SumFunction_MissingExpression_1() throws Exception {

        String jpqlQuery  = "SELECT SUM() FROM Employee e";
        int startPosition = "SELECT SUM(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SumFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SumFunction_MissingExpression_2() throws Exception {

        String jpqlQuery  = "SELECT SUM(DISTINCT) FROM Employee e";
        int startPosition = "SELECT SUM(DISTINCT".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SumFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SumFunction_MissingExpression_3() throws Exception {

        String jpqlQuery  = "SELECT SUM(DISTINCT ) FROM Employee e";
        int startPosition = "SELECT SUM(DISTINCT ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery, buildFormatter_2());

        testHasOnlyOneProblem(
            problems,
            SumFunction_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SumFunction_MissingLeftParenthesis_1() throws Exception {

        String jpqlQuery  = "SELECT SUM e.age) FROM Employee e";
        int startPosition = "SELECT SUM".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SumFunction_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SumFunction_MissingLeftParenthesis_2() throws Exception {

        String jpqlQuery  = "SELECT SUM DISTINCT e.age) FROM Employee e";
        int startPosition = "SELECT SUM".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SumFunction_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SumFunction_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT SUM(DISTINCT e.age FROM Employee e";
        int startPosition = "SELECT SUM(DISTINCT e.age".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            SumFunction_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_TrimExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE TRIM(e) = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE TRIM(".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            TrimExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_TrimExpression_InvalidTrimCharacter() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE TRIM(e FROM ' JPQL ') = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE TRIM(".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            TrimExpression_InvalidTrimCharacter,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_TrimExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE TRIM() = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE TRIM(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            TrimExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_TrimExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE TRIM ' JPQL ') = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE TRIM".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            TrimExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_TrimExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE TRIM(' JPQL ' = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE TRIM(' JPQL '".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            TrimExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_TrimExpression_NotSingleStringLiteral() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE TRIM('u2' FROM ' JPQL ') = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE TRIM(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE TRIM('u2'".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            TrimExpression_NotSingleStringLiteral,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_TypeExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT TYPE(e.name) FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, TypeExpression_InvalidExpression);
    }

    @Test
    public final void test_TypeExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT TYPE() FROM Employee e";
        int startPosition = "SELECT TYPE(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            TypeExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_TypeExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT TYPE e) FROM Employee e";
        int startPosition = "SELECT TYPE".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            TypeExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_TypeExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT TYPE(e FROM Employee e";
        int startPosition = "SELECT TYPE(e".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            TypeExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

//    @Test
//    public final void test_UpdateItem_InvalidNewValue()
//    {
//        String jpqlQuery  = "UPDATE Employee e SET e.name = LENGTH(e.age)";
//
//        int startPosition = "UPDATE Employee e SET e.name = ".length();
//        int endPosition   = jpqlQuery.length();
//
//        List<QueryProblem> problems = validate(jpqlQuery);
//
//        testHasOnlyOneProblem
//        (
//            problems,
//            QueryProblemMessages.UpdateItem_InvalidNewValue,
//            startPosition,
//            endPosition
//        );
//    }

    @Test
    public final void test_UpdateClause_MissingRangeVariableDeclaration() throws Exception {

        String jpqlQuery  = "UPDATE SET e.name = 'Pascal'";
        int startPosition = "UPDATE ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpdateClause_MissingRangeVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateClause_MissingSet() throws Exception {

        String jpqlQuery  = "UPDATE Employee e e.name = 'Pascal'";
        int startPosition = "UPDATE Employee e ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpdateClause_MissingSet,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateClause_MissingUpdateItems_1() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpdateClause_MissingUpdateItems,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateClause_MissingUpdateItems_2() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpdateClause_MissingUpdateItems,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateClause_UpdateItemEndsWithComma_1() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET e.name = 'JPQL',";
        int startPosition = jpqlQuery.length() - 1;
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpdateClause_UpdateItemEndsWithComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateClause_UpdateItemEndsWithComma_2() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET e.name = 'JPQL', ";
        int startPosition = "UPDATE Employee e SET e.name = 'JPQL'".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpdateClause_UpdateItemEndsWithComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateClause_UpdateItemIsMissingComma() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET e.name = 'JPQL' e.age = 20";
        int startPosition = "UPDATE Employee e SET e.name = 'JPQL'".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpdateClause_UpdateItemIsMissingComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_MissingEqualSign_1() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET e.name";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpdateItem_MissingEqualSign,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_MissingEqualSign_2() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET e.name ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpdateItem_MissingEqualSign,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_MissingNewValue_1() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET e.name =";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpdateItem_MissingNewValue,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_MissingNewValue_2() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET e.name = ";
        int startPosition = jpqlQuery.length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpdateItem_MissingNewValue,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_MissingStateFieldPathExpression() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET = 'Pascal'";
        int startPosition = "UPDATE Employee e SET ".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpdateItem_MissingStateFieldPathExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpperExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE UPPER(ALL(SELECT d FROM Dept d)) = 'PASCAL'";
        int startPosition = "SELECT e FROM Employee e WHERE UPPER(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE UPPER(ALL(SELECT d FROM Dept d)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpperExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpperExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE UPPER() = 'PASCAL'";
        int startPosition = "SELECT e FROM Employee e WHERE UPPER(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpperExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpperExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE UPPER e.name) = 'PASCAL'";
        int startPosition = "SELECT e FROM Employee e WHERE UPPER".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpperExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpperExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE UPPER(e.name = 'PASCAL'";
        int startPosition = "SELECT e FROM Employee e WHERE UPPER(e.name".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            UpperExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ValidQuery_01() throws Exception {

        String jpqlQuery = "UPDATE Product " +
                           "SET partNumber = CASE TYPE(project) WHEN com.titan.domain.EnumType.FIRST_NAME THEN '1' " +
                           "                                    WHEN com.titan.domain.EnumType.LAST_NAME  THEN '2' " +
                           "                                    ELSE '3' " +
                           "                 END";

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ValidQuery_02() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE EXISTS(SELECT o.date FROM Order o)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ValidQuery_03() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e HAVING ALL(SELECT o.name FROM Order o) = 'JPQL'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ValidQuery_04() throws Exception {

        String jpqlQuery = "SELECT r FROM RuleCondition r WHERE r.ruleType = :ruleType AND r.operator = :operator AND (SELECT Count(rcc) FROM r.components rcc ) = :componentCount  AND (SELECT Count(rc2) FROM r.components rc2 WHERE rc2.componentId IN :componentIds) = :componentCount";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ValidQuery_05() throws Exception {

        String jpqlQuery = "SELECT p FROM Product p WHERE TYPE(p.project) <> SmallProject";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ValueExpression_InvalidExpression() throws Exception {

        String jpqlQuery  = "SELECT VALUE(e.name) FROM Employee e";
        int startPosition = "SELECT VALUE(".length();
        int endPosition   = "SELECT VALUE(e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ValueExpression_InvalidExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ValueExpression_MissingExpression() throws Exception {

        String jpqlQuery  = "SELECT VALUE() FROM Employee e JOIN e.addresses a";
        int startPosition = "SELECT VALUE(".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ValueExpression_MissingExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ValueExpression_MissingLeftParenthesis() throws Exception {

        String jpqlQuery  = "SELECT VALUE a) FROM Employee e JOIN e.addresses a";
        int startPosition = "SELECT VALUE".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ValueExpression_MissingLeftParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ValueExpression_MissingRightParenthesis() throws Exception {

        String jpqlQuery  = "SELECT VALUE(a FROM Employee e JOIN e.addresses a";
        int startPosition = "SELECT VALUE(a".length();
        int endPosition   = startPosition;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ValueExpression_MissingRightParenthesis,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_WhenClause_MissingThenExpression() throws Exception {

        String jpqlQuery = "UPDATE Employee e " +
                           "SET e.salary = " +
                           "    CASE WHEN e.rating = 1 THEN " +
                           "    ELSE e.salary * 1.01" +
                           "    END";

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        int startPosition = "UPDATE Employee e SET e.salary = CASE WHEN e.rating = 1 THEN ".length();
        int endPosition   = startPosition;

        testHasOnlyOneProblem(
            problems,
            WhenClause_MissingThenExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_WhenClause_MissingThenIdentifier() throws Exception {

        String jpqlQuery  = "UPDATE Employee e " +
                            "SET e.salary = " +
                            "    CASE WHEN e.rating = 1 " +
                            "    ELSE e.salary * 1.01" +
                            "    END";

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        jpqlQuery = jpqlQuery.replaceAll("\\s+", " ");

        int startPosition = "UPDATE Employee e SET e.salary = CASE WHEN e.rating = 1 ".length();
        int endPosition = startPosition;

        testHasOnlyOneProblem(
            problems,
            WhenClause_MissingThenIdentifier,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_WhenClause_MissingWhenExpression() throws Exception {

        String jpqlQuery = "UPDATE Employee e " +
                           "SET e.salary = " +
                           "    CASE WHEN" +
                           "    ELSE 'NonExempt'" +
                           "    END";

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        jpqlQuery = jpqlQuery.replaceAll("\\s+", " ");

        int startPosition = "UPDATE Employee e SET e.salary = CASE WHEN ".length();
        int endPosition = startPosition;

        testHasOnlyOneProblem(
            problems,
            WhenClause_MissingWhenExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_WhereClause_ConditionalExpressionMissing_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE";
        int startPosition = "SELECT e FROM Employee e ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            WhereClause_MissingConditionalExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_WhereClause_ConditionalExpressionMissing_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE ";
        int startPosition = "SELECT e FROM Employee e ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            WhereClause_MissingConditionalExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_WhereClause_InvalidConditionalExpression_1() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name <> 'JPQL'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            WhereClause_InvalidConditionalExpression
        );
    }

    @Test
    public final void test_WhereClause_InvalidConditionalExpression_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LENGTH(e.name)";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            WhereClause_InvalidConditionalExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_WhereClause_InvalidConditionalExpression_3() throws Exception {

        String jpqlQuery  = "select e from Employee e join e.address a where foo() = 1";
        int startPosition = "select e from Employee e join e.address a where ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            WhereClause_InvalidConditionalExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_WhereClause_InvalidConditionalExpression_4() throws Exception {

        String jpqlQuery  = "delete from Address as a where current_date ";
        int startPosition = "delete from Address as a where ".length();
        int endPosition   = "delete from Address as a where current_date".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            WhereClause_InvalidConditionalExpression,
            startPosition,
            endPosition
        );
    }
}
