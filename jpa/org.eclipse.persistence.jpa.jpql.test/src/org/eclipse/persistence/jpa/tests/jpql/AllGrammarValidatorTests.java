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
package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLGrammarTestHelper;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLGrammarTools;
import org.eclipse.persistence.jpa.tests.jpql.tools.DefaultGrammarValidatorTest2_0;
import org.eclipse.persistence.jpa.tests.jpql.tools.DefaultGrammarValidatorTest2_1;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The test suite containing all the test suites testing the validation of JPQL queries based on
 * the JPQL grammar.
 *
 * @version 2.5.1
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
    AllGrammarValidatorTests.AllDefaultGrammarValidatorTest2_0.class,
    AllGrammarValidatorTests.AllDefaultGrammarValidatorTest2_1.class,
    AllGrammarValidatorTests.AllEclipseLinkGrammarValidatorTest.class,
    AllGrammarValidatorTests.AllEclipseLinkGrammarValidatorTest2_4.class,
    AllGrammarValidatorTests.AllEclipseLinkGrammarValidatorTest2_5.class,
})
@RunWith(JPQLTestRunner.class)
public final class AllGrammarValidatorTests {

    private AllGrammarValidatorTests() {
        super();
    }

    /**
     * This test suite tests JPQL queries written following the grammar defined in the JPA 2.0 spec
     * and makes sure the various JPQL grammars that support it parses them correctly.
     */
    @SuiteClasses({
        DefaultGrammarValidatorTest2_0.class,
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllDefaultGrammarValidatorTest2_0 {

        private AllDefaultGrammarValidatorTest2_0() {
            super();
        }

        @JPQLGrammarTestHelper
        static JPQLGrammar[] buildJPQLGrammars() {
            return JPQLGrammarTools.allDefaultJPQLGrammars(JPAVersion.VERSION_2_0);
        }
    }

    /**
     * This test suite tests JPQL queries written following the grammar defined in the JPA 2.1 spec
     * and makes sure the various JPQL grammars that support it parses them correctly.
     */
    @SuiteClasses({
        DefaultGrammarValidatorTest2_1.class,
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllDefaultGrammarValidatorTest2_1 {

        private AllDefaultGrammarValidatorTest2_1() {
            super();
        }

        @JPQLGrammarTestHelper
        static JPQLGrammar[] buildJPQLGrammars() {
            return JPQLGrammarTools.allDefaultJPQLGrammars(JPAVersion.VERSION_2_1);
        }
    }

    /**
     * This test suite tests JPQL queries written following the grammar defined in the JPA 2.0 spec
     * with the extension provided by EclipseLink 2.0, 2.1, 2.2 and 2.3 and makes sure the various
     * JPQL grammars that support it parses them correctly.
     */
    @SuiteClasses({
        EclipseLinkGrammarValidatorTest.class,
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllEclipseLinkGrammarValidatorTest {

        private AllEclipseLinkGrammarValidatorTest() {
            super();
        }

        @JPQLGrammarTestHelper
        static JPQLGrammar[] buildJPQLGrammars() {
            return JPQLGrammarTools.allEclipseLinkJPQLGrammars(EclipseLinkVersion.VERSION_2_0);
        }
    }

    /**
     * This test suite tests JPQL queries written following the grammar defined in the JPA 2.1 spec
     * with the extension provided by EclipseLink 2.4 and makes sure the various JPQL grammars that
     * support it parses them correctly.
     */
    @SuiteClasses({
        EclipseLinkGrammarValidatorTest2_4.class,
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllEclipseLinkGrammarValidatorTest2_4 {

        private AllEclipseLinkGrammarValidatorTest2_4() {
            super();
        }

        @JPQLGrammarTestHelper
        static JPQLGrammar[] buildJPQLGrammars() {
            return JPQLGrammarTools.allEclipseLinkJPQLGrammars(EclipseLinkVersion.VERSION_2_4);
        }
    }

    /**
     * This test suite tests JPQL queries written following the grammar defined in the JPA 2.1 spec
     * with the extension provided by EclipseLink 2.5 and makes sure the various JPQL grammars that
     * support it parses them correctly.
     */
    @SuiteClasses({
        EclipseLinkGrammarValidatorTest2_5.class,
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllEclipseLinkGrammarValidatorTest2_5 {

        private AllEclipseLinkGrammarValidatorTest2_5() {
            super();
        }

        @JPQLGrammarTestHelper
        static JPQLGrammar[] buildJPQLGrammars() {
            return JPQLGrammarTools.allEclipseLinkJPQLGrammars(EclipseLinkVersion.VERSION_2_5);
        }
    }
}
