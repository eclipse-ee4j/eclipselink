/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

//import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.compositekey.CompositeKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.SingleKeyTestCases;

public class MultipleTargetTestCases extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Multiple target test suite");
        suite.addTest(SingleKeyTestCases.suite());

        // Testing fix for bug# 5683210
        //suite.addTest(CompositeKeyTestCases.suite());
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.MultipleTargetTestCases"};
        junit.textui.TestRunner.main(arguments);
    }
}
