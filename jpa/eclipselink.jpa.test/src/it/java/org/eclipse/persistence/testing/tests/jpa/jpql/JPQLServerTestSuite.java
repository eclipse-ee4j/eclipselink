/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
