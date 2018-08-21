/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * <p><b>Purpose</b>: To collect the tests that will run against Application Server only.
 */
public class FAServerTestSuite extends TestSuite {
    public static Test suite() {
        JUnitTestCase.initializePlatform();
        TestSuite suite = new TestSuite();
        suite.setName("Fieldaccess Advanced ServerTestSuite");
        suite.addTest(AdvancedJPAJunitTest.suite());
        suite.addTest(AdvancedJunitTest.suite());
        suite.addTest(CallbackEventJUnitTestSuite.suite());
        suite.addTest(EntityManagerJUnitTestSuite.suiteSpring());
        suite.addTest(ExtendedPersistenceContextJUnitTestSuite.suite());
        suite.addTest(JoinedAttributeAdvancedJunitTest.suiteSpring());
        suite.addTest(NamedNativeQueryJUnitTest.suite());
        suite.addTest(OptimisticConcurrencyJUnitTestSuite.suite());
        suite.addTest(ReportQueryAdvancedJUnitTest.suite());
        suite.addTest(ReportQueryConstructorExpressionTestSuite.suite());
        suite.addTest(ReportQueryMultipleReturnTestSuite.suite());
        suite.addTest(SQLResultSetMappingTestSuite.suite());
        suite.addTest(UpdateAllQueryAdvancedJunitTest.suite());

        return suite;
    }
}
