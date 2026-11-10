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
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar4_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar5_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar3_2;
import org.eclipse.persistence.jpa.jpql.tools.DefaultGrammarValidator;
import org.eclipse.persistence.jpa.jpql.tools.model.EclipseLinkActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.model.EclipseLinkJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.tools.model.EclipseLinkJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.model.IJPQLQueryFormatter.IdentifierStyle;
import org.eclipse.persistence.jpa.jpql.tools.model.query.JPQLQueryStateObject;
import org.junit.Test;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;
import static org.junit.Assert.*;

/**
 * Tests the provider grammar boundary for optional ELSE clauses in CASE expressions.
 */
@SuppressWarnings("nls")
public class CaseWithoutElseGrammarTest {

    private static final String[] CASE_EXPRESSIONS = {
        "CASE WHEN e.age > 18 THEN 'adult' END",
        "CASE e.rating WHEN 1 THEN 'one' END"
    };

    @Test
    public void test_DefaultProviderAllowsOmittedElse() {
        assertOptionalElse(DefaultEclipseLinkJPQLGrammar.instance(), true, false);
    }

    @Test
    public void test_Provider5AllowsOmittedElse() {
        assertOptionalElse(EclipseLinkJPQLGrammar5_0.instance(), true, false);
    }

    @Test
    public void test_Provider4RequiresElse() {
        assertOptionalElse(EclipseLinkJPQLGrammar4_0.instance(), false, false);
    }

    @Test
    public void test_StandardGrammarRequiresElseWithProviderValidator() {
        assertOptionalElse(JPQLGrammar3_2.instance(), false, false);
    }

    @Test
    public void test_StandardGrammarRequiresElseWithDefaultValidator() {
        assertOptionalElse(JPQLGrammar3_2.instance(), false, true);
    }

    @Test
    public void test_ExplicitElseRemainsValid() {
        for (JPQLGrammar grammar : new JPQLGrammar[] {
            DefaultEclipseLinkJPQLGrammar.instance(), EclipseLinkJPQLGrammar5_0.instance(),
            EclipseLinkJPQLGrammar4_0.instance(), JPQLGrammar3_2.instance()
        }) {
            for (String expression : CASE_EXPRESSIONS) {
                String query = "SELECT " + expression.replace(" END", " ELSE 'other' END") + " FROM Employee e";
                for (boolean tolerant : new boolean[] {true, false}) {
                    assertTrue(validate(query, grammar, false, tolerant).isEmpty());
                }
            }
        }
    }

    @Test
    public void test_OmittedElseStillRequiresEnd() {
        for (String expression : CASE_EXPRESSIONS) {
            String query = "SELECT " + expression.replace(" END", "") + " FROM Employee e";
            List<JPQLQueryProblem> problems = validate(query, DefaultEclipseLinkJPQLGrammar.instance(), false, true);
            JPQLQueryProblem problem = findProblem(problems, CaseExpression_MissingEndIdentifier);
            assertEquals(query.indexOf("FROM"), problem.getStartPosition());
            assertEquals(problem.getStartPosition(), problem.getEndPosition());
            assertFalse(problems.stream().anyMatch(p -> CaseExpression_MissingElseIdentifier.equals(p.getMessageKey())));
        }
    }

    @Test
    public void test_MalformedCasesRemainInvalid() {
        String[][] invalidCases = {
            {"CASE END", CaseExpression_MissingWhenClause},
            {"CASE WHEN THEN 1 END", WhenClause_MissingWhenExpression},
            {"CASE WHEN e.age > 18 END", WhenClause_MissingThenIdentifier},
            {"CASE WHEN e.age > 18 THEN END", WhenClause_MissingThenExpression},
            {"CASE e.rating WHEN 1 THEN END", WhenClause_MissingThenExpression},
            {"CASE WHEN e.age > 18 THEN 1 ELSE END", CaseExpression_MissingElseExpression},
            {"CASE e.rating WHEN 1 THEN 1 ELSE END", CaseExpression_MissingElseExpression},
            {"CASE WHEN e.age > 18 THEN 1 ELSE 2", CaseExpression_MissingEndIdentifier}
        };
        for (String[] invalidCase : invalidCases) {
            String query = "SELECT " + invalidCase[0] + " FROM Employee e";
            findProblem(validate(query, DefaultEclipseLinkJPQLGrammar.instance(), false, true), invalidCase[1]);
        }
    }

    @Test
    public void test_NormalizedFormatterPreservesOmittedElse() {
        assertCaseRoundTrip(new EclipseLinkJPQLQueryFormatter(IdentifierStyle.UPPERCASE));
    }

    @Test
    public void test_ActualFormatterPreservesOmittedElse() {
        assertCaseRoundTrip(new EclipseLinkActualJPQLQueryFormatter(false));
    }

    @Test
    public void test_ExactFormatterPreservesOmittedElse() {
        assertCaseRoundTrip(new EclipseLinkActualJPQLQueryFormatter(true));
    }

    @Test
    public void test_FormattersPreserveExplicitElse() {
        for (String expression : CASE_EXPRESSIONS) {
            String query = "SELECT " + expression.replace(" END", " ELSE 'other' END") + " FROM Employee e";
            for (IJPQLQueryFormatter formatter : formatters()) {
                assertFormattedQuery(query, query, formatter, true);
            }
        }
    }

    @Test
    public void test_FormattersPreserveIncompleteCase() {
        for (String expression : CASE_EXPRESSIONS) {
            String query = "SELECT " + expression.replace(" END", "") + " FROM Employee e";
            String normalized = "SELECT " + expression.replace(" END", " ELSE END") + " FROM Employee e";
            String actual = "SELECT " + expression.replace(" END", " ELSE  END") + " FROM Employee e";
            assertFormattedQuery(query, normalized, new EclipseLinkJPQLQueryFormatter(IdentifierStyle.UPPERCASE), false);
            assertFormattedQuery(query, actual, new EclipseLinkActualJPQLQueryFormatter(false), false);
            assertFormattedQuery(query, query.replace(" FROM", "  FROM"),
                    new EclipseLinkActualJPQLQueryFormatter(true), false);
        }
    }

    @Test
    public void test_FormattersPreserveExplicitEmptyElse() {
        for (String expression : CASE_EXPRESSIONS) {
            String query = "SELECT " + expression.replace(" END", " ELSE END") + " FROM Employee e";
            String actual = query.replace(" ELSE END", " ELSE  END");
            assertFormattedQuery(query, query, new EclipseLinkJPQLQueryFormatter(IdentifierStyle.UPPERCASE), false);
            assertFormattedQuery(query, actual, new EclipseLinkActualJPQLQueryFormatter(false), false);
            assertFormattedQuery(query, query, new EclipseLinkActualJPQLQueryFormatter(true), false);
        }
    }

    private IJPQLQueryFormatter[] formatters() {
        return new IJPQLQueryFormatter[] {
            new EclipseLinkJPQLQueryFormatter(IdentifierStyle.UPPERCASE),
            new EclipseLinkActualJPQLQueryFormatter(false),
            new EclipseLinkActualJPQLQueryFormatter(true)
        };
    }

    private void assertCaseRoundTrip(IJPQLQueryFormatter formatter) {
        for (String expression : CASE_EXPRESSIONS) {
            String query = "SELECT " + expression + " FROM Employee e";
            assertFormattedQuery(query, query, formatter, true);
        }
    }

    private void assertFormattedQuery(String query, String expected, IJPQLQueryFormatter formatter, boolean valid) {
        JPQLGrammar grammar = DefaultEclipseLinkJPQLGrammar.instance();
        EclipseLinkJPQLQueryBuilder builder = new EclipseLinkJPQLQueryBuilder(grammar);
        JPQLQueryStateObject state = builder.buildStateObject(
                new EclipseLinkJavaJPQLQueryTestHelper().getPersistenceUnit(), query, true);
        String formatted = formatter.toString(state);
        assertEquals(expected, formatted);
        EclipseLinkGrammarValidator validator = new EclipseLinkGrammarValidator(grammar);
        List<JPQLQueryProblem> problems = new ArrayList<>();
        validator.setProblems(problems);
        try {
            new JPQLExpression(formatted, grammar, true).accept(validator);
            assertEquals(problems.toString(), valid, problems.isEmpty());
        }
        finally {
            validator.dispose();
        }
        if (valid) {
            assertTrue(validate(formatted, grammar, false, false).isEmpty());
        }
    }

    private void assertOptionalElse(JPQLGrammar grammar, boolean allowed, boolean standardValidator) {
        for (String expression : CASE_EXPRESSIONS) {
            String query = "SELECT " + expression + " FROM Employee e";
            for (boolean tolerant : new boolean[] {true, false}) {
                List<JPQLQueryProblem> problems = validate(query, grammar, standardValidator, tolerant);
                if (allowed) {
                    assertTrue(problems.toString(), problems.isEmpty());
                }
                else {
                    findProblem(problems, CaseExpression_MissingElseIdentifier);
                }
            }
        }
    }

    private List<JPQLQueryProblem> validate(String query, JPQLGrammar grammar,
                                          boolean standardValidator, boolean tolerant) {
        JPQLExpression expression = new JPQLExpression(query, grammar, tolerant);
        assertEquals(query, expression.toParsedText());
        AbstractGrammarValidator validator = standardValidator
                ? new DefaultGrammarValidator(grammar) : new EclipseLinkGrammarValidator(grammar);
        List<JPQLQueryProblem> problems = new ArrayList<>();
        validator.setProblems(problems);
        try {
            expression.accept(validator);
            return problems;
        }
        finally {
            validator.dispose();
        }
    }

    private JPQLQueryProblem findProblem(List<JPQLQueryProblem> problems, String key) {
        return problems.stream().filter(problem -> key.equals(problem.getMessageKey())).findFirst()
                .orElseThrow(() -> new AssertionError("Missing " + key + " in " + problems));
    }
}
