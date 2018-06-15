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

import org.eclipse.persistence.jpa.tests.jpql.parser.AllJPQLParserTests;
import org.eclipse.persistence.jpa.tests.jpql.tools.AllContentAssistTests;
import org.eclipse.persistence.jpa.tests.jpql.tools.AllJPQLQueryHelperTests;
import org.eclipse.persistence.jpa.tests.jpql.tools.AllRefactoringToolTests;
import org.eclipse.persistence.jpa.tests.jpql.tools.model.AllEclipseLinkStateObjectTest2_1;
import org.eclipse.persistence.jpa.tests.jpql.tools.model.AllStateObjectTest1_0;
import org.eclipse.persistence.jpa.tests.jpql.tools.model.AllStateObjectTest2_0;
import org.eclipse.persistence.jpa.tests.jpql.tools.model.AllStateObjectTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The root test suite that includes the entire functionality provided by Hermes.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuiteClasses({
    AllUtilityTests.class,
    AllJPQLParserTests.class,
    ValidateJPQLVersionTest.class,
    AllGrammarValidatorTests.class,
    AllHermesTests.HermesDefaultTestSuite.class,
    AllHermesTests.HermesEclipseLinkTestSuite.class
})
@RunWith(JPQLTestRunner.class)
public final class AllHermesTests {

    private AllHermesTests() {
        super();
    }

    /**
     * The suite to be used by Hermes and third-party when testing with generic JPA as the provider.
     */
    @SuiteClasses({

        // Test JPQLQueryHelper
        AllJPQLQueryHelperTests.AllDefaultDeclarationTest.class,
        AllJPQLQueryHelperTests.AllDefaultJPQLQueryHelperTests.class,
        AllJPQLQueryHelperTests.AllDefaultJPQLQueryHelperTests2_1.class,

        // Unit-Test testing validating a JPQL query that was written following the JPA 2.0 spec
        AllSemanticValidatorTests.AllDefaultSemanticValidatorTest2_0.class,
        // Unit-Test testing validating a JPQL query that was written following the JPA 2.1 spec
        AllSemanticValidatorTests.AllDefaultSemanticValidatorTest2_1.class,

        // Content assist support
        AllContentAssistTests.AllDefaultContentAssistTests.class,

        // Refactoring support
        AllRefactoringToolTests.class,

        // Testing the creation of the state model representation of a JPQL query
        AllStateObjectTests.class,
        AllStateObjectTest1_0.class,
        AllStateObjectTest2_0.class
    })
    @RunWith(JPQLTestRunner.class)
    public static class DefaultTestSuite {
    }

    /**
     * The suite to be used by Hermes and third-party when testing with EclipseLink as the provider.
     */
    @SuiteClasses({

        // Test JPQLQueryHelper
        AllJPQLQueryHelperTests.AllEclipseLinkDeclarationTest.class,
        AllJPQLQueryHelperTests.AllEclipseLinkJPQLQueryHelperTests.class,
        AllJPQLQueryHelperTests.AllEclipseLinkJPQLQueryHelperTests2_4.class,
        AllJPQLQueryHelperTests.AllEclipseLinkJPQLQueryHelperTests2_5.class,

        // Unit-Test testing validating a JPQL query that was written following EclipseLink 2.0, 2.1, 2.2, 2.3
        AllSemanticValidatorTests.AllEclipseLinkSemanticValidatorTest.class,
        // Unit-Test testing validating a JPQL query that was written following EclipseLink 2.4
        AllSemanticValidatorTests.AllEclipseLinkSemanticValidatorTest2_4.class,
        // Unit-Test testing validating a JPQL query that was written following EclipseLink 2.5
        AllSemanticValidatorTests.AllEclipseLinkSemanticValidatorTest2_5.class,

        // Content assist support
        AllContentAssistTests.AllEclipseLinkContentAssistTests.class,

        // Refactoring support
        AllRefactoringToolTests.class,

        // Testing the creation of the state model representation of a JPQL query
        AllStateObjectTests.class,
        AllEclipseLinkStateObjectTest2_1.class
    })
    @RunWith(JPQLTestRunner.class)
    public static class EclipseLinkTestSuite {
    }

    public static class HermesDefaultTestSuite extends DefaultTestSuite {
        @JPQLQueryTestHelperTestHelper
        static JPQLQueryTestHelper buildQueryTestHelper() {
            return new DefaultJavaJPQLQueryTestHelper();
        }
    }

    public static final class HermesEclipseLinkTestSuite extends EclipseLinkTestSuite {
        @JPQLQueryTestHelperTestHelper
        static JPQLQueryTestHelper buildQueryTestHelper() {
            return new EclipseLinkJavaJPQLQueryTestHelper();
        }
    }
}
