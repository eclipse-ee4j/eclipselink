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
package org.eclipse.persistence.tools.workbench.test.framework;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.framework.resources.AllFrameworkResourcesTests;
import org.eclipse.persistence.tools.workbench.test.framework.ui.tools.AllFrameworkUIToolsTests;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


public class AllFrameworkTests {

    public static Test suite() {
        return suite(true);
    }

    public static Test suite(boolean all) {
        TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllFrameworkTests.class));

        suite.addTest(AllFrameworkResourcesTests.suite());

        suite.addTest(AbstractApplicationTests.suite());
        suite.addTest(AllFrameworkUIToolsTests.suite());

        return suite;
    }

    private AllFrameworkTests() {
        super();
        throw new UnsupportedOperationException();
    }

}
