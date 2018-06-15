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
package org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.compositekey;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.compositekey.attributekey.CompositeAttributeKeysTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.compositekey.elementkey.CompositeElementKeysMissingIdTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.compositekey.elementkey.CompositeElementKeysTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.compositekey.mixedkey.CompositeMixedKeysTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.compositekey.nonstringkeytype.CompositeNonStringKeyTypeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.compositekey.nestedattributekey.NestedAttributeKeyTestCases;

public class CompositeKeyTestCases extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Single target with composite key test suite");
        suite.addTestSuite(CompositeAttributeKeysTestCases.class);
        suite.addTestSuite(CompositeElementKeysTestCases.class);
        suite.addTestSuite(CompositeElementKeysMissingIdTestCases.class);
        suite.addTestSuite(CompositeMixedKeysTestCases.class);
        suite.addTestSuite(CompositeNonStringKeyTypeTestCases.class);
        suite.addTestSuite(NestedAttributeKeyTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.compositekey.CompositeKeyTestCases"};
        junit.textui.TestRunner.main(arguments);
    }
}
