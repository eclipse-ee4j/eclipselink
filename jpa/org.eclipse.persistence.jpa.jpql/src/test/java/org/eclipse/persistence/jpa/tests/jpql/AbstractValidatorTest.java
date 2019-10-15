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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.eclipse.persistence.jpa.jpql.AbstractValidator;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblemResourceBundle;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLGrammarTestHelper;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryBuilder;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryStringFormatter;
import static org.junit.Assert.*;

/**
 * The abstract definition of a unit-test testing an {@link AbstractValidator}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractValidatorTest extends JPQLCoreTest {

    /**
     * The {@link JPQLGrammar} that is injected by the test suite, which is used to parse the JPQL query.
     */
    @JPQLGrammarTestHelper
    protected JPQLGrammar jpqlGrammar;

    /**
     * The {@link ResourceBundle} that contains the key-value pairs coming from the .properties file.
     */
    private ResourceBundle propertiesFile;

    /**
     * The {@link ResourceBundle} that contains the key-value pairs coming from {@link JPQLQueryProblemResourceBundle}.
     */
    private ResourceBundle resourceBundle;

    /**
     * The validator being tested.
     */
    private AbstractValidator validator;

    private CharSequence buildProblemMessages(List<JPQLQueryProblem> problems) {

        StringBuilder sb = new StringBuilder();

        for (int index = 0, count = problems.size(); index < count; index++) {

            JPQLQueryProblem problem = problems.get(index);
            sb.append(problem.getMessageKey());

            if (index + 1 < count) {
                sb.append(", ");
            }
        }

        return sb;
    }

    protected abstract AbstractValidator buildValidator();

    /**
     * Retrieves the BNF object that was registered for the given unique identifier.
     *
     * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF} to retrieve
     * @return The {@link JPQLQueryBNF} representing a section of the grammar
     */
    protected JPQLQueryBNF getQueryBNF(String queryBNFId) {
        return jpqlGrammar.getExpressionRegistry().getQueryBNF(queryBNFId);
    }

    protected AbstractValidator getValidator() {
        return validator;
    }

    private ResourceBundle loadPropertiesFile() {

        // Running from plug-in project in Eclipse
        ClassLoader classLoader = AbstractValidator.class.getClassLoader();
        URL url = classLoader.getResource("/resource/org/eclipse/persistence/jpa/jpql/jpa_jpql_validation.properties");

        if (url != null) {
            return ResourceBundle.getBundle(
                "resource.org.eclipse.persistence.jpa.jpql.jpa_jpql_validation",
                Locale.getDefault(),
                classLoader
            );
        }
        // Running within the plug-in jar or from Hermes JUnit test
        else {
            return ResourceBundle.getBundle(
                JPQLQueryProblemResourceBundle.PROPERTIES_FILE_NAME,
                Locale.getDefault(),
                classLoader
            );
        }
    }

    private ResourceBundle loadResourceBundle() {
        return ResourceBundle.getBundle(JPQLQueryProblemResourceBundle.class.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUpClass() throws Exception {
        super.setUpClass();

        // Load the resource bundles
        resourceBundle = loadResourceBundle();
        propertiesFile = loadPropertiesFile();

        // Create the validator that is used by this unit-test
        validator = buildValidator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        if (validator != null) {
            validator.dispose();
        }
        super.tearDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDownClass() throws Exception {
        jpqlGrammar    = null;
        validator      = null;
        resourceBundle = null;
        propertiesFile = null;
        super.tearDownClass();
    }

    protected final void testDoesNotHaveProblem(List<JPQLQueryProblem> problems, String messageKey) {

        for (JPQLQueryProblem problem : problems) {
            assertFalse(
                messageKey + " should not be part of the list",
                problem.getMessageKey() == messageKey
            );
        }
    }

    protected final void testHasNoProblems(List<JPQLQueryProblem> problems) {
        assertTrue(
            "The list of problems should be empty: " + buildProblemMessages(problems),
            problems.isEmpty()
        );
    }

    protected final void testHasOnlyOneProblem(List<JPQLQueryProblem> problems,
                                               String messageKey,
                                               int startPosition,
                                               int endPosition) {

        assertFalse(
            "There are no problems in the list: " + messageKey,
            problems.isEmpty()
        );

        assertEquals(
            "More than one problem was found: " + buildProblemMessages(problems),
            1,
            problems.size()
        );

        JPQLQueryProblem problem = problems.get(0);

        assertSame(
            "The problem was not added to the list: " + messageKey,
            messageKey,
            problem.getMessageKey()
        );

        testProblem(problem, startPosition, endPosition);
    }

    protected final void testHasOnlyTheseProblems(List<JPQLQueryProblem> problems,
                                                  String[] messageKeys,
                                                  int[] startPositions,
                                                  int[] endPositions) {

        List<String> problemsNotFound = new ArrayList<String>();
        List<String> extraProblems = new ArrayList<String>();

        for (String messageKey : messageKeys) {
            problemsNotFound.add(messageKey);
        }

        for (JPQLQueryProblem problem : problems) {
            extraProblems.add(problem.getMessageKey());
        }

        // Iterate through the problems that should be part of the list
        for (int index = 0; index < messageKeys.length; index++) {

            String messageKey = messageKeys[index];

            // Iterator through the list that was generated from validation
            for (JPQLQueryProblem problem : problems) {

                if (problem.getMessageKey()    == messageKey &&
                    problem.getStartPosition() == startPositions[index] &&
                    problem.getEndPosition()   == endPositions  [index]) {

                    extraProblems.remove(messageKey);
                    problemsNotFound.remove(messageKey);
                    testResourceBundle(messageKey);
                    break;
                }
            }
        }

        assertTrue(
            "Some problems were not added to the list or has wrong positions: " + problemsNotFound,
            problemsNotFound.isEmpty()
        );

        assertTrue(
            "Some problems were incorrectly added to the list: " + extraProblems,
            extraProblems.isEmpty()
        );
    }

    protected final void testHasProblem(List<JPQLQueryProblem> problems,
                                        String messageKey,
                                        int startPosition,
                                        int endPosition) {

        JPQLQueryProblem problem = null;

        for (JPQLQueryProblem queryProblem : problems) {
            if (messageKey == queryProblem.getMessageKey()) {
                problem = queryProblem;
                break;
            }
        }

        assertNotNull(
            "The problem was not added to the list: " + messageKey,
            problem
        );

        testProblem(problem, startPosition, endPosition);
    }

    protected final void testHasProblems(List<JPQLQueryProblem> problems,
                                         String[] messageKeys,
                                         int[] startPositions,
                                         int[] endPositions) {

        List<String> problemsNotFound = new ArrayList<String>();

        for (String messageKey : messageKeys) {
            problemsNotFound.add(messageKey);
        }

        // Iterate through the problems that should be part of the list
        for (int index = 0; index < messageKeys.length; index++) {

            String messageKey = messageKeys[index];

            // Iterator through the list that was generated from validation
            for (JPQLQueryProblem problem : problems) {

                if (problem.getMessageKey()    == messageKey &&
                    problem.getStartPosition() == startPositions[index] &&
                    problem.getEndPosition()   == endPositions[index]) {

                    problemsNotFound.remove(messageKey);
                    testResourceBundle(messageKey);
                    break;
                }
            }
        }

        assertTrue(
            "Some problems were not added to the list: " + problemsNotFound,
            problemsNotFound.isEmpty()
        );
    }

    private void testProblem(JPQLQueryProblem problem, int startPosition, int endPosition) {

        assertEquals(
            "The start position was not calculating correctly",
            startPosition,
            problem.getStartPosition()
        );

        assertEquals(
            "The end position was not calculating correctly",
            endPosition,
            problem.getEndPosition()
        );

        testResourceBundle(problem.getMessageKey());
    }

    protected void testResourceBundle(String messageKey) {

        // JPQLQueryProblemResourceBundle
        assertNotNull(
            messageKey + " was not added to the resource bundle clas",
            resourceBundle.getString(messageKey)
        );

        // jpa_jpql_validation.properties
        assertNotNull(
            messageKey + " was not added to the .properties file",
            propertiesFile.getString(messageKey)
        );
    }

    /**
     * Validates the given named query and returns the list of {@link JPQLQueryProblem problems}.
     *
     * @param jpqlQuery The JPQL query to validate
     * @return Either an empty list if validation didn't find any problem or the list of
     * {@link JPQLQueryProblem problems}
     */
    protected List<JPQLQueryProblem> validate(String jpqlQuery) throws Exception {
        return validate(jpqlQuery, JPQLQueryStringFormatter.DEFAULT);
    }

    /**
     * Validates the JPQL query and returns the list of {@link JPQLQueryProblem problems}.
     *
     * @param jpqlQuery The JPQL query to validate
     * @param jpqlExpression The parsed tree representation of the JPQL query
     * @return Either an empty list if validation didn't find any problem or the list of {@link
     * JPQLQueryProblem problems}
     */
    protected List<JPQLQueryProblem> validate(String jpqlQuery, JPQLExpression jpqlExpression) throws Exception {
        List<JPQLQueryProblem> problems = new ArrayList<JPQLQueryProblem>();
        validator.setProblems(problems);
        jpqlExpression.accept(validator);
        return problems;
    }

    /**
     * Validates the given named query and returns the list of {@link JPQLQueryProblem problems}.
     *
     * @param jpqlQuery The JPQL query to validate
     * @param formatter The formatter used to update the format of the query when validating the parsed tree
     * @return Either an empty list if validation didn't find any problem or the list of {@link
     * JPQLQueryProblem problems}
     */
    protected List<JPQLQueryProblem> validate(String jpqlQuery, JPQLQueryStringFormatter formatter) throws Exception {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(jpqlQuery, jpqlGrammar, formatter, true);
        return validate(jpqlQuery, jpqlExpression);
    }
}
