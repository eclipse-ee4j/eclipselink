/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.collections;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CollectionsTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Collections Test Suite");
        suite.addTestSuite(CollectionHolderTestCases.class);
        suite.addTestSuite(CollectionHolderPopulatedTestCases.class);
        suite.addTestSuite(CollectionHolderPopulatedSingleItemTestCases.class);
        suite.addTestSuite(CollectionHolderPopulatedSingleItemReducedTestCases.class);
        suite.addTestSuite(CollectionHolderInitializedTestCases.class);
        suite.addTestSuite(CollectionHolderInitializedWithNullsTestCases.class);
        suite.addTestSuite(CollectionHolderNillableTestCases.class);
        suite.addTestSuite(CollectionHolderNillableWithNullsTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersInitializedTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersNillableTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersNillableInitializedTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersOverrideTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersPopulatedTestCases.class);
        suite.addTestSuite(CollectionHolderInitializedELTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersNillableInitializedELTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersInitializedELTestCases.class);
        suite.addTestSuite(CollectionHolderELTestCases.class);
        suite.addTestSuite(DequeHolderTestCases.class);
        suite.addTestSuite(NavigableSetHolderTestCases.class);
        suite.addTestSuite(QueueHolderTestCases.class);
        suite.addTestSuite(SetHolderTestCases.class);
        suite.addTestSuite(SortedSetHolderTestCases.class);
        return suite;
    }

}
