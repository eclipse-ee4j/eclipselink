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
package org.eclipse.persistence.tools.workbench.test.utility.iterators;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

public class AllIteratorTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllIteratorTests.class));

        suite.addTest(ArrayIteratorTests.suite());
        suite.addTest(ArrayListIteratorTests.suite());
        suite.addTest(ChainIteratorTests.suite());
        suite.addTest(CloneIteratorTests.suite());
        suite.addTest(CloneListIteratorTests.suite());
        suite.addTest(CompositeIteratorTests.suite());
        suite.addTest(CompositeListIteratorTests.suite());
        suite.addTest(EnumerationIteratorTests.suite());
        suite.addTest(FilteringIteratorTests.suite());
        suite.addTest(GraphIteratorTests.suite());
        suite.addTest(IteratorEnumerationTests.suite());
        suite.addTest(NullEnumerationTests.suite());
        suite.addTest(NullIteratorTests.suite());
        suite.addTest(NullListIteratorTests.suite());
        suite.addTest(PeekableIteratorTests.suite());
        suite.addTest(ReadOnlyIteratorTests.suite());
        suite.addTest(ReadOnlyListIteratorTests.suite());
        suite.addTest(SingleElementIteratorTests.suite());
        suite.addTest(SingleElementListIteratorTests.suite());
        suite.addTest(TransformationIteratorTests.suite());
        suite.addTest(TransformationListIteratorTests.suite());
        suite.addTest(TreeIteratorTests.suite());

        return suite;
    }

    private AllIteratorTests() {
        super();
    }

}
