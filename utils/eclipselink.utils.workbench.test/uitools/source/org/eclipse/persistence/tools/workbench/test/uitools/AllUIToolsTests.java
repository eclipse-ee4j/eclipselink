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
package org.eclipse.persistence.tools.workbench.test.uitools;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.uitools.app.AllUIToolsAppTests;
import org.eclipse.persistence.tools.workbench.test.uitools.swing.AllUIToolsSwingTests;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllUIToolsTests {

    public static Test suite() {
        return suite(true);
    }

    public static Test suite(boolean all) {
        TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllUIToolsTests.class));

        suite.addTest(AllUIToolsAppTests.suite());
        suite.addTest(AllUIToolsSwingTests.suite());

        suite.addTest(ComponentEnablerTest.suite());
        suite.addTest(DisplayableTests.suite());
        suite.addTest(PreferencesRecentFilesManagerTests.suite());
        suite.addTest(PropertyValueModelDisplayableAdapterTests.suite());
        suite.addTest(SimpleDisplayableTests.suite());

        // remove the following test suite because it opens a window and
        // modifies the focus when running the tests  ~bjv
        // suite.addTest(SwitcherPanelTests.suite());

        return suite;
    }

    private AllUIToolsTests() {
        super();
        throw new UnsupportedOperationException();
    }

}
