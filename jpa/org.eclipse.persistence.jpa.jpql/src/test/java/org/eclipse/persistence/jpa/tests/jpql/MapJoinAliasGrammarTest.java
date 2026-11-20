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
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar4_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar1_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar3_2;
import org.eclipse.persistence.jpa.jpql.tools.DefaultGrammarValidator;
import org.junit.Test;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;
import static org.junit.Assert.*;

/**
 * A bare map join alias and VALUE(alias) have the same scalar grammar role.
 */
@SuppressWarnings("nls")
public class MapJoinAliasGrammarTest {

    private static final String FROM = " FROM ExampleEntity p JOIN p.environment e";
    private static final String WHERE = "SELECT p" + FROM + " WHERE KEY(e) = 'osName' AND ";

    @Test
    public void test_DirectComparisonRemainsValid() {
        assertAccepted(WHERE + "e = 'Unix'");
    }

    @Test
    public void test_ExplicitValueControlsRemainValid() {
        assertAccepted(WHERE + "LOWER(VALUE(e)) = 'unix'");
        assertAccepted(WHERE + "NULLIF(VALUE(e), '') = 'Unix'");
    }

    @Test
    public void test_LowerAcceptsBareAlias() {
        assertAccepted(WHERE + "LOWER(e) = 'unix'");
        assertAccepted("SELECT LOWER(e)" + FROM);
    }

    @Test
    public void test_NullIfAcceptsBareFirstArgument() {
        assertAccepted(WHERE + "NULLIF(e, '') = 'Unix'");
        assertAccepted("SELECT NULLIF(e, 'Windows')" + FROM);
    }

    @Test
    public void test_NullIfAcceptsBareSecondArgument() {
        assertAccepted("SELECT NULLIF('Windows', e)" + FROM);
        assertAccepted(WHERE + "NULLIF(:value, e) IS NULL");
    }

    @Test
    public void test_RelatedScalarFunctionsAcceptBothForms() {
        for (String value : new String[] {"e", "VALUE(e)"}) {
            for (String expression : new String[] {
                "UPPER(" + value + ")", "LENGTH(" + value + ")",
                "CONCAT(" + value + ", '!')", "CONCAT('!', " + value + ")",
                "TRIM(" + value + ")", "SUBSTRING(" + value + ", 1, 2)",
                "LOCATE('i', " + value + ")", "COALESCE(" + value + ", 'other')",
                "COALESCE(NULLIF(" + value + ", 'Windows'), 'other')"
            }) {
                assertAccepted("SELECT " + expression + FROM);
            }
        }
    }

    @Test
    public void test_NumericScalarFunctionsAcceptBothForms() {
        for (String value : new String[] {"n", "VALUE(n)"}) {
            for (String function : new String[] {"ABS", "AVG", "MAX", "MIN", "SUM"}) {
                assertAccepted("SELECT " + function + "(" + value + ") FROM ExampleEntity p JOIN p.numbers n");
            }
            assertAccepted("SELECT NULLIF(" + value + ", 0) FROM ExampleEntity p JOIN p.numbers n");
            assertAccepted("SELECT LOCATE('A', 'ABC', " + value + ") FROM ExampleEntity p JOIN p.numbers n");
            assertAccepted("SELECT SUBSTRING('ABC', " + value + ", 1) FROM ExampleEntity p JOIN p.numbers n");
            assertAccepted("SELECT SUBSTRING('ABC', 1, " + value + ") FROM ExampleEntity p JOIN p.numbers n");
        }
    }

    @Test
    public void test_MissingArgumentsRemainInvalid() {
        assertRejected("SELECT LOWER()" + FROM, LowerExpression_MissingExpression);
        assertRejected("SELECT NULLIF(, 'Unix')" + FROM, NullIfExpression_MissingFirstExpression);
        assertRejected("SELECT NULLIF(e,)" + FROM, NullIfExpression_MissingSecondExpression);
        assertRejected("SELECT NULLIF(e 'Unix')" + FROM, NullIfExpression_MissingComma);
    }

    @Test
    public void test_ConditionalArgumentsRemainInvalid() {
        // Strict parsing and error recovery may reject these at different positions.
        assertRejected("SELECT LOWER((e = 'Unix'))" + FROM, null);
        assertRejected("SELECT NULLIF((e = 'Unix'), 'other')" + FROM, null);
        assertRejected("SELECT NULLIF('other', (e = 'Unix'))" + FROM, null);
    }

    @Test
    public void test_ExtraArgumentsRemainInvalid() {
        assertRejected("SELECT LOWER(e, 'Unix')" + FROM, null);
        assertRejected("SELECT LOWER(VALUE(e), 'Unix')" + FROM, LowerExpression_MissingRightParenthesis);
        assertRejected("SELECT NULLIF(e, 'Unix', 'other')" + FROM, NullIfExpression_MissingRightParenthesis);
        assertRejected("SELECT VALUE(e, p)" + FROM, ValueExpression_MissingRightParenthesis);
    }

    @Test
    public void test_CollectionContextsStillRequireCollectionPaths() {
        assertRejected("SELECT SIZE(e)" + FROM, SizeExpression_InvalidExpression);
        assertRejected("SELECT p" + FROM + " WHERE :value MEMBER OF e", CollectionValuedPathExpression_NotCollectionType);
        assertRejected("SELECT p" + FROM + " WHERE e IS EMPTY", CollectionValuedPathExpression_NotCollectionType);
    }

    @Test
    public void test_QualifiedCollectionPathsRemainValid() {
        String from = " FROM ExampleEntity p JOIN p.members e";
        assertAccepted("SELECT SIZE(e.children)" + from);
        assertAccepted("SELECT p" + from + " WHERE e.children IS EMPTY");
        for (String qualifier : new String[] {"e", "VALUE(e)"}) {
            assertAccepted("SELECT p" + from + " WHERE :child MEMBER OF " + qualifier + ".children");
        }
    }

    @Test
    public void test_QualifiedSizePathRemainsValidInTolerantMode() {
        // The existing strict SIZE parser does not support a VALUE-qualified operand.
        assertAcrossGrammars("SELECT SIZE(VALUE(e).children) FROM ExampleEntity p JOIN p.members e",
                true, null, new boolean[] {true});
    }

    @Test
    public void test_JpaOneGrammarRemainsUnchanged() {
        assertGrammar("SELECT e FROM ExampleEntity e WHERE LOWER(e) = 'unix'", JPQLGrammar1_0.instance(), true,
                false, LowerExpression_InvalidExpression, new boolean[] {true, false});
        assertGrammar("SELECT e FROM ExampleEntity e WHERE LOWER(e.name) = 'unix'", JPQLGrammar1_0.instance(), true,
                true, null, new boolean[] {true, false});
    }

    private void assertAccepted(String query) {
        assertAcrossGrammars(query, true, null);
    }

    private void assertRejected(String query, String key) {
        assertAcrossGrammars(query, false, key);
    }

    private void assertAcrossGrammars(String query, boolean valid, String errorKey) {
        assertAcrossGrammars(query, valid, errorKey, new boolean[] {true, false});
    }

    private void assertAcrossGrammars(String query, boolean valid, String errorKey, boolean[] tolerantModes) {
        for (JPQLGrammar grammar : new JPQLGrammar[] {
            DefaultEclipseLinkJPQLGrammar.instance(), EclipseLinkJPQLGrammar4_0.instance(),
            EclipseLinkJPQLGrammar2_0.instance()
        }) {
            assertGrammar(query, grammar, false, valid, errorKey, tolerantModes);
        }
        for (JPQLGrammar grammar : new JPQLGrammar[] {JPQLGrammar3_2.instance(), JPQLGrammar2_0.instance()}) {
            assertGrammar(query, grammar, true, valid, errorKey, tolerantModes);
        }
    }

    private void assertGrammar(String query, JPQLGrammar grammar, boolean standardValidator, boolean valid,
                               String errorKey, boolean[] tolerantModes) {
        for (boolean tolerant : tolerantModes) {
            JPQLExpression expression = new JPQLExpression(query, grammar, tolerant);
            AbstractGrammarValidator validator = standardValidator
                    ? new DefaultGrammarValidator(grammar) : new EclipseLinkGrammarValidator(grammar);
            List<JPQLQueryProblem> problems = new ArrayList<>();
            validator.setProblems(problems);
            try {
                expression.accept(validator);
                String message = grammar.getClass().getSimpleName() + ", tolerant=" + tolerant
                        + ": " + query + " -> " + problems;
                if (valid) {
                    assertEquals(query, expression.toParsedText());
                    assertTrue(message, problems.isEmpty());
                } else {
                    assertFalse(message, problems.isEmpty());
                    if (tolerant && errorKey != null) {
                        assertTrue(message, problems.stream().anyMatch(p -> errorKey.equals(p.getMessageKey())));
                    }
                }
            } finally {
                validator.dispose();
            }
        }
    }
}
