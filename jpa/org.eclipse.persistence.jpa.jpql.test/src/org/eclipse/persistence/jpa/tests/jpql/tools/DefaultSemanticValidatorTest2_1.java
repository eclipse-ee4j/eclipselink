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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractSemanticValidator;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages;
import org.eclipse.persistence.jpa.jpql.tools.DefaultJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.DefaultSemanticValidator;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;
import org.junit.Test;

/**
 * The unit-test class used for testing a JPQL query semantically when the JPA version is 2.1.
 *
 * @version 2.5.1
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DefaultSemanticValidatorTest2_1 extends AbstractSemanticValidatorTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLQueryContext buildQueryContext() {
        return new DefaultJPQLQueryContext(jpqlGrammar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractSemanticValidator buildValidator() {
        return new DefaultSemanticValidator(buildSemanticValidatorHelper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isComparisonTypeChecked() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isPathExpressionToCollectionMappingAllowed() {
        return false;
    }

    @Test
    public final void test_ComplextPathExpression_01() throws Exception {

        String jpqlQuery = "SELECT TREAT(p.project LargeProject) FROM Product p";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComplextPathExpression_02() throws Exception {

        String jpqlQuery = "SELECT TREAT(TREAT(p.project LargeProject).parent AS LargeProject).endDate FROM Product p";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_EntityTypeLiteral_NotResolvable_1() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e TREAT(e.phoneNumbers AS Phone) AS ee";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testDoesNotHaveProblem(
            problems,
            JPQLQueryProblemMessages.EntityTypeLiteral_NotResolvable
        );
    }

    @Test
    public final void test_EntityTypeLiteral_NotResolvable_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e JOIN TREAT(e.phoneNumbers AS Phone2) AS ee";
        int startPosition = "SELECT e FROM Employee e JOIN TREAT(e.phoneNumbers AS ".length();
        int endPosition   = "SELECT e FROM Employee e JOIN TREAT(e.phoneNumbers AS Phone2".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            JPQLQueryProblemMessages.EntityTypeLiteral_NotResolvable,
            startPosition,
            endPosition
        );
    }
}
