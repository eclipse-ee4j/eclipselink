/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_2;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_3;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_5;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.tools.AbstractJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.tools.DefaultJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.DefaultJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.tools.EclipseLinkJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.EclipseLinkJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;
import org.eclipse.persistence.jpa.tests.jpql.JPQLQueryHelperTestHelper;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.eclipse.persistence.jpa.tests.jpql.UniqueSignature;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLGrammarTestHelper;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLGrammarTools;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The test suite testing {@link AbstractJPQLQueryHelper} and its concrete classes.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
//    ORMEntityJPQLQueryHelperTest.class,
//    ORMJPQLQueryHelperTest.class
public final class AllJPQLQueryHelperTests {

    private AllJPQLQueryHelperTests() {
        super();
    }

    @SuiteClasses({
        DefaultDeclarationTest.class
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllDefaultDeclarationTest {

        private AllDefaultDeclarationTest() {
            super();
        }

        @JPQLGrammarTestHelper
        static JPQLGrammar[] buildGrammars() {
            return JPQLGrammarTools.allJPQLGrammars();
        }
    }

    @SuiteClasses({
        DefaultJPQLQueryHelperTest.class
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllDefaultJPQLQueryHelperTests {

        private AllDefaultJPQLQueryHelperTests() {
            super();
        }

        private static JPQLQueryContext buildEclipseLinkJPQLQueryContext2_4() {
            return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_4.instance());
        }

        private static JPQLQueryContext buildEclipseLinkJPQLQueryContext2_5() {
            return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_5.instance());
        }

        private static JPQLQueryContext buildJPQLQueryContext2_0() {
            return new DefaultJPQLQueryContext(JPQLGrammar2_0.instance());
        }

        private static JPQLQueryContext buildJPQLQueryContext2_1() {
            return new DefaultJPQLQueryContext(JPQLGrammar2_1.instance());
        }

        @JPQLQueryHelperTestHelper
        static AbstractJPQLQueryHelper[] buildJPQLQueryHelpers() {
            return new AbstractJPQLQueryHelper[] {
                new DefaultJPQLQueryHelper(buildJPQLQueryContext2_0()),
                new DefaultJPQLQueryHelper(buildJPQLQueryContext2_1()),
                new EclipseLinkJPQLQueryHelper(buildEclipseLinkJPQLQueryContext2_4()),
                new EclipseLinkJPQLQueryHelper(buildEclipseLinkJPQLQueryContext2_5())
            };
        }
    }

    @SuiteClasses({
        DefaultJPQLQueryHelperTest2_1.class,
    })
    @RunWith(JPQLTestRunner.class)
    @UniqueSignature
    public static class AllDefaultJPQLQueryHelperTests2_1 {

        private AllDefaultJPQLQueryHelperTests2_1() {
            super();
        }

        private static JPQLQueryContext buildEclipseLinkJPQLQueryContext2_4() {
            return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_4.instance());
        }

        private static JPQLQueryContext buildEclipseLinkJPQLQueryContext2_5() {
            return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_5.instance());
        }

        private static JPQLQueryContext buildJPQLQueryContext2_1() {
            return new DefaultJPQLQueryContext(JPQLGrammar2_1.instance());
        }

        @JPQLQueryHelperTestHelper
        static AbstractJPQLQueryHelper[] buildJPQLQueryHelpers() {
            return new AbstractJPQLQueryHelper[] {
                new DefaultJPQLQueryHelper(buildJPQLQueryContext2_1()),
                new EclipseLinkJPQLQueryHelper(buildEclipseLinkJPQLQueryContext2_4()),
                new EclipseLinkJPQLQueryHelper(buildEclipseLinkJPQLQueryContext2_5())
            };
        }
    }

    @SuiteClasses({
        EclipseLinkDeclarationTest.class
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllEclipseLinkDeclarationTest {

        private AllEclipseLinkDeclarationTest() {
            super();
        }

        @JPQLGrammarTestHelper
        static JPQLGrammar[] buildGrammars() {
            return JPQLGrammarTools.allEclipseLinkJPQLGrammars(EclipseLinkVersion.VERSION_2_4);
        }
    }

    @SuiteClasses({
        EclipseLinkJPQLQueryHelperTest.class
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllEclipseLinkJPQLQueryHelperTests {

        private AllEclipseLinkJPQLQueryHelperTests() {
            super();
        }

        private static JPQLQueryContext buildJPQLQueryContext2_1() {
            return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_1.instance());
        }

        private static JPQLQueryContext buildJPQLQueryContext2_2() {
            return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_2.instance());
        }

        private static JPQLQueryContext buildJPQLQueryContext2_3() {
            return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_3.instance());
        }

        private static JPQLQueryContext buildJPQLQueryContext2_4() {
            return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_4.instance());
        }

        private static JPQLQueryContext buildJPQLQueryContext2_5() {
            return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_5.instance());
        }

        @JPQLQueryHelperTestHelper
        static AbstractJPQLQueryHelper[] buildJPQLQueryHelpers() {
            return new AbstractJPQLQueryHelper[] {
                new EclipseLinkJPQLQueryHelper(buildJPQLQueryContext2_1()),
                new EclipseLinkJPQLQueryHelper(buildJPQLQueryContext2_2()),
                new EclipseLinkJPQLQueryHelper(buildJPQLQueryContext2_3()),
                new EclipseLinkJPQLQueryHelper(buildJPQLQueryContext2_4()),
                new EclipseLinkJPQLQueryHelper(buildJPQLQueryContext2_5())
            };
        }
    }

    @SuiteClasses({
        EclipseLinkJPQLQueryHelperTest2_4.class,
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllEclipseLinkJPQLQueryHelperTests2_4 {

        private AllEclipseLinkJPQLQueryHelperTests2_4() {
            super();
        }

        private static JPQLQueryContext buildJPQLQueryContext2_4() {
            return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_4.instance());
        }

        private static JPQLQueryContext buildJPQLQueryContext2_5() {
            return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_5.instance());
        }

        @JPQLQueryHelperTestHelper
        static AbstractJPQLQueryHelper[] buildJPQLQueryHelpers() {
            return new AbstractJPQLQueryHelper[] {
                new EclipseLinkJPQLQueryHelper(buildJPQLQueryContext2_4()),
                new EclipseLinkJPQLQueryHelper(buildJPQLQueryContext2_5())
            };
        }
    }

    @SuiteClasses({
        EclipseLinkJPQLQueryHelperTest2_5.class,
    })
    @RunWith(JPQLTestRunner.class)
    public static class AllEclipseLinkJPQLQueryHelperTests2_5 {

        private AllEclipseLinkJPQLQueryHelperTests2_5() {
            super();
        }

        private static JPQLQueryContext buildJPQLQueryContext2_5() {
            return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_5.instance());
        }

        @JPQLQueryHelperTestHelper
        static AbstractJPQLQueryHelper[] buildJPQLQueryHelpers() {
            return new AbstractJPQLQueryHelper[] {
                new EclipseLinkJPQLQueryHelper(buildJPQLQueryContext2_5())
            };
        }
    }
}
