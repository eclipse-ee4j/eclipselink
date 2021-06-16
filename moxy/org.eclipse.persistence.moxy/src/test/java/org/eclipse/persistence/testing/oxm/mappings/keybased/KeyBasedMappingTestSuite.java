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
package org.eclipse.persistence.testing.oxm.mappings.keybased;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.SingleTargetTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.MultipleTargetTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.norefclass.NoRefClassTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.norefclass.ObjectRefClassTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.norefclass.SingleAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.norefclass.SingleElementTestCases;

public class KeyBasedMappingTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Key-based Mapping Test Suite");
        suite.addTest(SingleTargetTestCases.suite());
        suite.addTest(MultipleTargetTestCases.suite());
        suite.addTestSuite(NoRefClassTestCases.class);
        suite.addTestSuite(ObjectRefClassTestCases.class);
        suite.addTestSuite(SingleElementTestCases.class);
        suite.addTestSuite(SingleAttributeTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.keybased.KeyBasedMappingTestSuite" };
        //System.setProperty("useDeploymentXML", "true");
        //System.setProperty("useDocPres", "true");
        //System.setProperty("useLogging", "true");
        //System.setProperty("useSAXParsing", "true");
        junit.textui.TestRunner.main(arguments);
    }
}
