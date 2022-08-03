/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/22/2015 - Will Dazey
//       - 465235 : Find Bugs Fix: removed null variable args
package org.eclipse.persistence.testing.tests.jpa;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.tests.jpa.advanced.JPAAdvancedTestModel;
import org.eclipse.persistence.testing.tests.jpa22.advanced.CriteriaQueryCastTestSuite;
import org.eclipse.persistence.testing.tests.jpa22.jta.JTATestSuite;
import org.eclipse.persistence.testing.tests.jpa22.metadata.MetadataASMFactoryTest;

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
        }
}
