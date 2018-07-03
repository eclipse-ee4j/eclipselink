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
package org.eclipse.persistence.tools.workbench.test.utility.diff;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

public class AllDiffTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllDiffTests.class));

        suite.addTest(ArrayDiffTests.suite());
        suite.addTest(CollectionDiffTests.suite());
        suite.addTest(DiffableCircularReferenceDiffTests.suite());
        suite.addTest(DiffEngineCircularReferenceDiffTests.suite());
        suite.addTest(EqualityDiffTests.suite());
        suite.addTest(IdentityDiffTests.suite());
        suite.addTest(InheritanceReflectiveDiffTests.suite());
        suite.addTest(ListDiffTests.suite());
        suite.addTest(MapDiffTests.suite());
        suite.addTest(MultiClassReflectiveDiffTests.suite());
        suite.addTest(NullDiffTests.suite());
        suite.addTest(SimpleReflectiveDiffTests.suite());
        suite.addTest(UnorderedArrayDiffTests.suite());

        return suite;
    }

    private AllDiffTests() {
        super();
        throw new UnsupportedOperationException();
    }

}
