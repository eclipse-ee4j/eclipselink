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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.tools;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllSCUIToolsTests
{
    private AllSCUIToolsTests()
    {
        super();
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite("SC UI Tools Tests");

        suite.addTest(BooleanCellRendererAdapterTest.suite());
        suite.addTest(IPAddressTest.suite());

        return suite;
    }
}
