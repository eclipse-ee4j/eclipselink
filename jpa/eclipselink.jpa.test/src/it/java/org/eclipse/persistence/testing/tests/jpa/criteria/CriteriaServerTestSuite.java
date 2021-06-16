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
package org.eclipse.persistence.testing.tests.jpa.criteria;

import junit.framework.TestSuite;
import junit.framework.Test;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * <p><b>Purpose</b>: To collect the tests that will run against Application Server only.
 */
public class CriteriaServerTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Criteria ServerTestSuite");
        if (! JUnitTestCase.isJPA10()) {
            suite.addTest(AdvancedCompositePKJunitTest.suite());
            suite.addTest(AdvancedCriteriaQueryTestSuite.suite());
            suite.addTest(AdvancedQueryTestSuite.suite());
            suite.addTest(JUnitCriteriaSimpleTestSuite.suite());
            suite.addTest(JUnitCriteriaUnitTestSuite.suite());
            suite.addTest(JUnitCriteriaMetamodelTestSuite.suite());
        }
        return suite;
    }
}
