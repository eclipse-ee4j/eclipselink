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
package org.eclipse.persistence.testing.tests.jpa.inheritance;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * <p><b>Purpose</b>: To collect the tests that will run against Application Server only.
 */
public class InheritanceServerTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Inheritance ServerTestSuite");
        suite.addTest(LifecycleCallbackJunitTest.suite());
        suite.addTest(EntityManagerJUnitTestCase.suite());
        suite.addTest(MixedInheritanceJUnitTestCase.suite());
        suite.addTest(JoinedAttributeInheritanceJunitTest.suite());
        suite.addTest(TablePerClassInheritanceJUnitTest.suite());
        suite.addTest(ReportQueryMultipleReturnInheritanceTestSuite.suite());

        return suite;
    }
}
