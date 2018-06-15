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
import org.eclipse.persistence.jpa.tests.jpql.tools.DefaultSemanticValidatorTest2_0;
import org.eclipse.persistence.jpa.tests.jpql.tools.DefaultSemanticValidatorTest2_1;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public final class AllSemanticValidatorTests {

    private AllSemanticValidatorTests() {
        super();
    }

    /**
     * This test suite tests JPQL queries written following the grammar defined in the JPA 2.0 spec
     * and makes sure the various JPQL grammars that support it parses them correctly.
     */
    @SuiteClasses({
        DefaultSemanticValidatorTest2_0.class,
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllDefaultSemanticValidatorTest2_0 {

        private AllDefaultSemanticValidatorTest2_0() {
            super();
        }

        @JPQLGrammarTestHelper
        static JPQLGrammar[] buildJPQLGrammars() {
            return JPQLGrammarTools.allJPQLGrammars(JPAVersion.VERSION_2_0);
        }
    }

    /**
     * This test suite tests JPQL queries written following the grammar defined in the JPA 2.1 spec
     * and makes sure the various JPQL grammars that support it parses them correctly.
     */
    @SuiteClasses({
        DefaultSemanticValidatorTest2_1.class,
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllDefaultSemanticValidatorTest2_1 {

        private AllDefaultSemanticValidatorTest2_1() {
            super();
        }

        @JPQLGrammarTestHelper
        static JPQLGrammar[] buildJPQLGrammars() {
            return JPQLGrammarTools.allJPQLGrammars(JPAVersion.VERSION_2_1);
        }
    }

    /**
     * This test suite tests JPQL queries written following the grammar defined in the JPA 2.0 spec
     * with the extension provided by EclipseLink 2.0, 2.1, 2.2 and 2.3 and makes sure the various
     * JPQL grammars that support it parses them correctly.
     */
    @SuiteClasses({
        EclipseLinkSemanticValidatorTest.class,
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllEclipseLinkSemanticValidatorTest {

        private AllEclipseLinkSemanticValidatorTest() {
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
        EclipseLinkSemanticValidatorTest2_4.class,
        EclipseLinkSemanticValidatorExtensionTest2_4.class
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllEclipseLinkSemanticValidatorTest2_4 {

        private AllEclipseLinkSemanticValidatorTest2_4() {
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
        EclipseLinkSemanticValidatorTest2_5.class,
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllEclipseLinkSemanticValidatorTest2_5 {

        private AllEclipseLinkSemanticValidatorTest2_5() {
            super();
        }

        @JPQLGrammarTestHelper
        static JPQLGrammar[] buildJPQLGrammars() {
            return JPQLGrammarTools.allEclipseLinkJPQLGrammars(EclipseLinkVersion.VERSION_2_5);
        }
    }
}
