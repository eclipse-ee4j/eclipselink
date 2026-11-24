/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.tests.jpql;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.jpa.jpql.AbstractGrammarValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkGrammarValidator;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar4_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar5_0;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar1_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar3_2;
import org.eclipse.persistence.jpa.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.jpql.parser.QueryPosition;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.jpql.parser.WhereClause;
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistProposals;
import org.eclipse.persistence.jpa.jpql.tools.DefaultGrammarValidator;
import org.eclipse.persistence.jpa.jpql.tools.DefaultJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.tools.model.DefaultActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.model.DefaultJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryFormatter.IdentifierStyle;
import org.eclipse.persistence.jpa.jpql.tools.model.JPQLQueryBuilder2_1;
import org.eclipse.persistence.jpa.jpql.tools.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.tests.jpql.tools.spi.java.JavaQuery;
import org.junit.Test;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.ArithmeticExpression_MissingRightExpression;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.ArithmeticFactor_MissingExpression;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/** Regression coverage for the arithmetic operand boundary, including its tooling consumers. */
@SuppressWarnings("nls")
public class ArithmeticAssociativityTest {

    private static final String PREFIX = "SELECT e FROM Employee e WHERE ";

    private static final JPQLGrammar[] GRAMMARS = {
        JPQLGrammar1_0.instance(), JPQLGrammar2_0.instance(), JPQLGrammar3_2.instance(),
        EclipseLinkJPQLGrammar4_0.instance(), EclipseLinkJPQLGrammar5_0.instance(),
        DefaultEclipseLinkJPQLGrammar.instance()
    };

    @Test
    public void subtractionChainsAssociateLeft() {
        assertShape("100 - 0 - 30 - 20", "(- (- (- 100 0) 30) 20)");
        assertShape("100 - 30 - 20", "(- (- 100 30) 20)");
        assertShape("100 - 1 - 2 - 3 - 4 - 5 - 6 - 7 - 8",
                "(- (- (- (- (- (- (- (- 100 1) 2) 3) 4) 5) 6) 7) 8)");
    }

    @Test
    public void mixedAdditiveChainsAssociateLeft() {
        assertShape("100 - 30 + 20", "(+ (- 100 30) 20)");
        assertShape("100 + 30 - 20", "(- (+ 100 30) 20)");
        assertShape("100 + 30 + 20", "(+ (+ 100 30) 20)");
        assertShape("e.salary - :first + :second", "(+ (- e.salary :first) :second)");
        assertShape("e.salary - ?1 - ?2", "(- (- e.salary ?1) ?2)");
    }

    @Test
    public void multiplicativePrecedenceAndAssociationArePreserved() {
        assertShape("100 - 2 * 3 - 4", "(- (- 100 (* 2 3)) 4)");
        assertShape("10 + 2 * 10 / 2 - 2", "(- (+ 10 (/ (* 2 10) 2)) 2)");
    }

    @Test
    public void existingMultiplicativeAndUnaryControlsRemainValid() {
        assertShape("120 / 3 / 2", "(/ (/ 120 3) 2)");
        assertShape("120 / 3 * 2", "(* (/ 120 3) 2)");
        assertShape("2 * 3 * 4", "(* (* 2 3) 4)");
        assertShape("100 - -30", "(- 100 -30)");
        assertShape("100 + +30", "(+ 100 +30)");
        assertShape("-100 + 30", "(+ -100 30)");
        assertShape("+100 - 30", "(- +100 30)");
        assertShape("1.0E+2 - 3.0E-1", "(- 1.0E+2 3.0E-1)");
    }

    @Test
    public void explicitParenthesesRemainBarriers() {
        assertShape("100 - (0 - (30 - 20))", "(- 100 (group (- 0 (group (- 30 20)))))");
        assertShape("((100 - 0) - 30) - 20", "(- (group (- (group (- 100 0)) 30)) 20)");
        assertShape("100 / (10 / 2)", "(/ 100 (group (/ 10 2)))");
    }

    @Test
    public void additiveChainsInsideGroupsAssociateLeft() {
        assertShape("(100 - 30 + 20) * 2", "(* (group (+ (- 100 30) 20)) 2)");
    }

    @Test
    public void unarySignsAndExponentSignsRemainOperands() {
        assertShape("-100 - 30 + 20", "(+ (- -100 30) 20)");
        assertShape("+100 - +30 + -20", "(+ (- +100 +30) -20)");
        assertShape("100 - -30 - 20", "(- (- 100 -30) 20)");
        assertShape("100 + +30 - 20", "(- (+ 100 +30) 20)");
        assertShape("1.0E+2 - 3.0E+1 + 2.0E-1", "(+ (- 1.0E+2 3.0E+1) 2.0E-1)");
    }

    @Test
    public void unaryFactorsRemainOperandsWithinAdditiveChains() {
        assertShape("100 - -e.salary - 20", "(- (- 100 (unary- e.salary)) 20)");
        assertShape("100 - +:first - 20", "(- (- 100 (unary+ :first)) 20)");
        assertShape("100 - -(30 - 20) - 5", "(- (- 100 (unary- (group (- 30 20)))) 5)");
        assertShape("100 - -e.salary * 2 - 20", "(- (- 100 (* (unary- e.salary) 2)) 20)");
    }

    @Test
    public void compactDoubledSignsRetainTheirAcceptedTrees() {
        assertShape("100--30", "(- 100 -30)");
        assertShape("100+-30", "(+ 100 -30)");
        assertShape("100++30", "(+ 100 +30)");
        assertShape("100-+30", "(- 100 +30)");
    }

    @Test
    public void compactBinaryOperatorsRetainTheirTokenBoundaries() {
        assertShape("100-30+20", "(+ (- 100 30) 20)");
        assertShape("100-30-20", "(- (- 100 30) 20)");
    }

    @Test
    public void textParentsAndPositionsFollowTheArithmeticTree() {
        for (JPQLGrammar grammar : GRAMMARS) {
            for (boolean tolerant : new boolean[]{false, true}) {
                for (String arithmetic : new String[]{"100 - 0 - 30 - 20", "100 - (0 - (30 - 20))"}) {
                    String query = PREFIX + arithmetic + " = 50";
                    JPQLExpression expression = new JPQLExpression(query, grammar, tolerant);
                    assertEquals(query, expression.toActualText());
                    assertEquals(query, expression.toParsedText());
                    assertTree(expression, query, expression);
                }
            }
        }
    }

    @Test
    public void stateObjectFormattersPreserveAssociationAndGrouping() {
        var provider = new DefaultJavaJPQLQueryTestHelper().getPersistenceUnit();
        var builder = new JPQLQueryBuilder2_1();
        for (boolean tolerant : new boolean[]{false, true}) {
            for (String arithmetic : new String[]{"100 - 0 - 30 - 20", "100 - (0 - (30 - 20))"}) {
                String query = PREFIX + arithmetic + " = 50";
                JPQLQueryStateObject state = builder.buildStateObject(provider, query, tolerant);
                String expected = arithmetic.startsWith("100 - (")
                        ? "(- 100 (group (- 0 (group (- 30 20)))))" : "(- (- (- 100 0) 30) 20)";
                String formatted = new DefaultJPQLQueryFormatter(IdentifierStyle.UPPERCASE).toString(state);
                String actual = new DefaultActualJPQLQueryFormatter(true).toString(state);
                assertEquals(query, formatted);
                assertEquals(query, actual);
                assertEquals(expected, shape(arithmetic(new JPQLExpression(formatted, builder.getGrammar(), tolerant))));
                assertEquals(expected, shape(arithmetic(new JPQLExpression(actual, builder.getGrammar(), tolerant))));
            }
        }
    }

    @Test(timeout = 10000)
    public void incompleteChainsRetainOperandProposals() {
        var helper = new DefaultJPQLQueryHelper(JPQLGrammar3_2.instance());
        var provider = new DefaultJavaJPQLQueryTestHelper().getPersistenceUnit();
        for (String arithmetic : new String[]{"e.salary - 30 - ", "e.salary - (30 - ", "e.salary - 30 + -"}) {
            String query = PREFIX + arithmetic;
            helper.setQuery(new JavaQuery(provider, query));
            ContentAssistProposals proposals = helper.buildContentAssistProposals(query.length());
            List<String> identifiers = new ArrayList<>();
            proposals.identifiers().forEach(identifiers::add);
            assertTrue(query + ": " + identifiers, identifiers.contains("ABS"));
            QueryPosition position = helper.getJPQLExpression().buildPosition(query, query.length());
            assertEquals(helper.getJPQLExpression().toActualText().length(), position.getPosition());
        }
    }

    @Test(timeout = 10000)
    public void malformedChainsRetainOperandDiagnostics() {
        assertMissingOperand("100 - 30 -", ArithmeticExpression_MissingRightExpression);
        assertMissingOperand("100 - (30 -", ArithmeticExpression_MissingRightExpression);
        assertMissingOperand("100 - 30 + -", ArithmeticFactor_MissingExpression);
    }

    private static void assertShape(String arithmetic, String expected) {
        String query = PREFIX + arithmetic + " = 50";
        for (JPQLGrammar grammar : GRAMMARS) {
            for (boolean tolerant : new boolean[]{false, true}) {
                JPQLExpression expression = new JPQLExpression(query, grammar, tolerant);
                assertEquals(grammar + ", tolerant=" + tolerant + ": " + query,
                        expected, shape(arithmetic(expression)));
                assertTrue(query, problems(expression, grammar).isEmpty());
            }
        }
    }

    private static Expression arithmetic(JPQLExpression expression) {
        SelectStatement statement = (SelectStatement) expression.getQueryStatement();
        WhereClause where = (WhereClause) statement.getWhereClause();
        return ((ComparisonExpression) where.getConditionalExpression()).getLeftExpression();
    }

    private static String shape(Expression expression) {
        if (expression instanceof ArithmeticExpression operation) {
            return "(" + operation.getIdentifier() + " " + shape(operation.getLeftExpression())
                    + " " + shape(operation.getRightExpression()) + ")";
        }
        if (expression instanceof SubExpression group) {
            return "(group " + shape(group.getExpression()) + ")";
        }
        if (expression instanceof ArithmeticFactor factor) {
            return "(unary" + (factor.isNegative() ? "-" : "+") + " " + shape(factor.getExpression()) + ")";
        }
        return expression.toParsedText();
    }

    private static void assertTree(Expression expression, String query, JPQLExpression root) {
        if (expression instanceof NumericLiteral) {
            int offset = expression.getOffset();
            String text = expression.toActualText();
            assertEquals(text, query.substring(offset, offset + text.length()));
            QueryPosition position = root.buildPosition(query, offset + 1);
            assertSame(expression, position.getExpression());
            assertEquals(1, position.getPosition(expression));
        }
        StringBuilder orderedText = new StringBuilder();
        for (Expression child : expression.orderedChildren()) {
            assertSame(expression, child.getParent());
            orderedText.append(child.toActualText());
            assertTree(child, query, root);
        }
        if (!orderedText.isEmpty()) {
            assertEquals(expression.toActualText(), orderedText.toString());
        }
    }

    private static void assertMissingOperand(String arithmetic, String message) {
        String query = PREFIX + arithmetic;
        for (JPQLGrammar grammar : GRAMMARS) {
            for (boolean tolerant : new boolean[]{false, true}) {
                JPQLExpression expression = new JPQLExpression(query, grammar, tolerant);
                List<JPQLQueryProblem> problems = problems(expression, grammar);
                assertFalse(query, problems.isEmpty());
                assertTrue(query + ": " + problems, problems.stream().anyMatch(problem ->
                        message.equals(problem.getMessageKey())
                        && problem.getStartPosition() == query.length()
                        && problem.getEndPosition() == query.length()));
            }
        }
    }

    private static List<JPQLQueryProblem> problems(JPQLExpression expression, JPQLGrammar grammar) {
        List<JPQLQueryProblem> problems = new ArrayList<>();
        AbstractGrammarValidator validator = grammar.getProvider() == null
                ? new DefaultGrammarValidator(grammar) : new EclipseLinkGrammarValidator(grammar);
        validator.setProblems(problems);
        try {
            expression.accept(validator);
        } finally {
            validator.dispose();
        }
        return problems;
    }
}
