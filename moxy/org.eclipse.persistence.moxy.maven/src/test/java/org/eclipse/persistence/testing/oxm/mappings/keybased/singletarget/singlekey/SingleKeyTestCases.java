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
package org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.attributekey.SingleAttributeEmptyKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.attributekey.SingleAttributeInvalidKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.attributekey.SingleAttributeNullKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.attributekey.SingleAttributeKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.elementkey.SingleElementEmptyKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.elementkey.SingleElementInvalidKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.elementkey.SingleElementNullKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.elementkey.SingleElementKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.elementkey.SingleElementNullReferenceClassTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.multiplesource.MultipleSourceTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.nonstringkeytype.NonStringKeyTypeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.nestedattributekey.NestedAttributeKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.attributekey.threadsafety.MultithreadedTestCases;

public class SingleKeyTestCases extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Single target with single key test suite");
        suite.addTestSuite(SingleAttributeEmptyKeyTestCases.class);
        suite.addTestSuite(SingleAttributeInvalidKeyTestCases.class);
        suite.addTestSuite(SingleAttributeNullKeyTestCases.class);
        suite.addTestSuite(SingleAttributeKeyTestCases.class);
        suite.addTestSuite(SingleElementEmptyKeyTestCases.class);
        suite.addTestSuite(SingleElementInvalidKeyTestCases.class);
        suite.addTestSuite(SingleElementNullKeyTestCases.class);
        suite.addTestSuite(SingleElementKeyTestCases.class);
        suite.addTestSuite(NonStringKeyTypeTestCases.class);
        suite.addTestSuite(MultipleSourceTestCases.class);
        suite.addTestSuite(NestedAttributeKeyTestCases.class);
        suite.addTestSuite(MultithreadedTestCases.class);
        suite.addTestSuite(SingleElementNullReferenceClassTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.SingleKeyTestCases"};
        junit.textui.TestRunner.main(arguments);
    }
}
