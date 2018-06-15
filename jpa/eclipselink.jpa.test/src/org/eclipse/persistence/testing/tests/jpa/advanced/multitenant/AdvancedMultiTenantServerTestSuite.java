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
package org.eclipse.persistence.testing.tests.jpa.advanced.multitenant;

import org.eclipse.persistence.testing.tests.jpa.advanced.multitenant.AdvancedMultiTenantSharedEMFJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.multitenant.AdvancedMultiTenant123JunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.multitenant.AdvancedMultiTenantTableJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.multitenant.AdvancedMultiTenantTableCJunitTest;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * <p><b>Purpose</b>: To collect the tests that will run against Application Server only.
 */
public class AdvancedMultiTenantServerTestSuite extends TestSuite {

    public static Test suite() {
        JUnitTestCase.initializePlatform();
        TestSuite suite = new TestSuite();
        suite.setName("Advanced Multitenant ServerTestSuite");
        suite.addTest(AdvancedMultiTenantSharedEMFJunitTest.suite());
        suite.addTest(AdvancedMultiTenant123JunitTest.suite());
        suite.addTest(AdvancedMultiTenantTableJunitTest.suite());
        suite.addTest(AdvancedMultiTenantTableCJunitTest.suite());
        return suite;
    }
}
