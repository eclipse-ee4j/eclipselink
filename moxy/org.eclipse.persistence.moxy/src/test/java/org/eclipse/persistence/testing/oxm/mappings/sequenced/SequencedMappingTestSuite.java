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
package org.eclipse.persistence.testing.oxm.mappings.sequenced;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SequencedMappingTestSuite  extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Sequenced Test Suite");
        suite.addTestSuite(GroupingElementDistinctTestCases.class);
        suite.addTestSuite(GroupingElementSharedTestCases.class);
        suite.addTestSuite(SimpleAnyTestCases.class);
        suite.addTestSuite(AttributeTestCases.class);
        suite.addTestSuite(MixedTextFirstTestCases.class);
        suite.addTestSuite(MixedTextMiddleTestCases.class);
        suite.addTestSuite(MixedTextLastTestCases.class);

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.sequenced.SequencedMappingTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

}
