/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.parser;

import junit.extensions.ActiveTestSuite;
import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_5;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

/**
 * This test suite runs {@link JPQLParserThreadTest} many times and concurrently, this is trying to
 * test using the parser concurrently and making sure there is no deadlock or a {@link java.util.
 * ConcurrentModificationException} thrown by some {@link Collection} classes.
 *
 * @version 2.4.1
 * @since 2.4.1
 * @author Pascal Filion
 */
@RunWith(AllTests.class)
public final class AllJPQLParserConcurrentTests {

    static final JPQLGrammar jpqlGrammar = new EclipseLinkJPQLGrammar2_5();

    private AllJPQLParserConcurrentTests() {
        super();
    }

    public static Test suite() {

        TestSuite suite = new ActiveTestSuite();
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        suite.addTest(new JUnit4TestAdapter(JPQLParserConcurrentTest.class));
        return suite;
    }
}
