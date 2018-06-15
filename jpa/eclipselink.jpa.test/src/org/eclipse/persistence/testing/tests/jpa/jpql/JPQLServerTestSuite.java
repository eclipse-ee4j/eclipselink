/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.jpql;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p><b>Purpose</b>: To collect the tests that will run against Application Server only.
 */
public class JPQLServerTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPQL ServerTestSuite");
        suite.addTest(AdvancedQueryTestSuite.suite());
        suite.addTest(JUnitJPQLComplexAggregateTestSuite.suite());
        suite.addTest(JUnitJPQLComplexTestSuite.suite());
        suite.addTest(JUnitJPQLDateTimeTestSuite.suite());
        suite.addTest(JUnitJPQLExamplesTestSuite.suite());
        suite.addTest(JUnitJPQLInheritanceTestSuite.suite());
        suite.addTest(JUnitJPQLModifyTestSuite.suite());
        suite.addTest(JUnitJPQLParameterTestSuite.suite());
        suite.addTest(JUnitJPQLSimpleTestSuite.suite());
        suite.addTest(JUnitJPQLUnitTestSuite.suite());
        suite.addTest(JUnitJPQLValidationTestSuite.suite());
        suite.addTest(JUnitJPQLQueryHelperTestSuite.suite());
        suite.addTest(JUnitNativeQueryTestSuite.suite());

        return suite;
    }
}
