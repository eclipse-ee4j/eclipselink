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
package org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.attributekey.SingleAttributeKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.elementkey.SingleElementKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.elementkey.SingleElementKeyWithGroupingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.elementkey.SingleElementNullReferenceClassTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.elementkey.maptests.SingleElementKeyUsingMapTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.nestedattributekey.NestedAttributeKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.nonstringkeytype.NonStringKeyTypeTestCases;

public class SingleKeyTestCases extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Multiple targets with single key test suite");
        suite.addTestSuite(SingleAttributeKeyTestCases.class);
        suite.addTestSuite(SingleElementKeyTestCases.class);
        suite.addTestSuite(SingleElementKeyWithGroupingTestCases.class);
        suite.addTestSuite(SingleElementKeyUsingMapTestCases.class);
        suite.addTestSuite(NonStringKeyTypeTestCases.class);
        suite.addTestSuite(NestedAttributeKeyTestCases.class);
        suite.addTestSuite(SingleElementNullReferenceClassTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.SingleKeyTestCases"};
        junit.textui.TestRunner.main(arguments);
    }
}
