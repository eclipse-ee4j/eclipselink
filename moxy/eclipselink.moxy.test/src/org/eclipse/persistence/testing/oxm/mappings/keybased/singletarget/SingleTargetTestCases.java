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
package org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.SingleKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.compositekey.CompositeKeyTestCases;

public class SingleTargetTestCases extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Single target test suite");
        suite.addTest(SingleKeyTestCases.suite());
        suite.addTest(CompositeKeyTestCases.suite());
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.SingleTargetTestCases"};
        junit.textui.TestRunner.main(arguments);
    }
}
