/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
