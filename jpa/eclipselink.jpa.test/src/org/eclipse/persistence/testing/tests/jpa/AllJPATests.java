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
//     04/22/2015 - Will Dazey
//       - 465235 : Find Bugs Fix: removed null variable args
package org.eclipse.persistence.testing.tests.jpa;

import junit.framework.Test;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.tests.jpa.advanced.JPAAdvancedTestModel;
import org.eclipse.persistence.testing.tests.jpa.remote.RemoteEntityManagerTestSuite;

/**
 * <p><b>Purpose</b>: Test run for all JPA tests.
 */
public class AllJPATests extends TestModel {
        /**
         * Return the JUnit suite to allow JUnit runner to find it.
         * Unfortunately JUnit only allows suite methods to be static,
         * so it is not possible to generically do this.
         */
        public static junit.framework.TestSuite suite()
        {
            return new AllJPATests();
        }

        public AllJPATests() {
            addTest(new JPAAdvancedTestModel());
            addTest(FullRegressionTestSuite.suite());
            addTest(RemoteEntityManagerTestSuite.suite());
            try {
                addTest((Test)Class.forName("org.eclipse.persistence.testing.tests.jpa21.advanced.JPA21TestSuite").getMethod("suite").invoke(null));
            } catch (Exception exception) {
                System.out.println("WARNING: " + exception);
            }
        }
}
