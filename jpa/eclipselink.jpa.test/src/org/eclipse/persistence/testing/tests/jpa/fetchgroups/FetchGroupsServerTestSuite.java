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
package org.eclipse.persistence.testing.tests.jpa.fetchgroups;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * <p><b>Purpose</b>: To collect the tests that will run against Application Server only.
 */
public class FetchGroupsServerTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("FetchGroups ServerTestSuite");
        suite.addTest(FetchGroupAPITests.suite());
        suite.addTest(FetchGroupMergeWithCacheTests.suite());
        suite.addTest(FetchGroupTrackerWeavingTests.suite());
        suite.addTest(NestedDefaultFetchGroupTests.suite());
        suite.addTest(NestedFetchGroupTests.suite());
        suite.addTest(NestedNamedFetchGroupTests.suite());
        suite.addTest(SimpleDefaultFetchGroupTests.suite());
        suite.addTest(SimpleFetchGroupTests.suite());
        suite.addTest(SimpleNamedFetchGroupTests.suite());
        suite.addTest(SimpleSerializeFetchGroupTests.suite());
        return suite;
    }
}
