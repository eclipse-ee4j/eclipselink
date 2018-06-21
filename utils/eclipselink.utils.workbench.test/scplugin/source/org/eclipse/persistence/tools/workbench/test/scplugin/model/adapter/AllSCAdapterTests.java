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
package org.eclipse.persistence.tools.workbench.test.scplugin.model.adapter;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllSCAdapterTests
{
    private AllSCAdapterTests()
    {
        super();
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite("SCAdapter Tests");

        suite.addTest(DatabaseLoginAdapterTest.suite());
        suite.addTest(DatabaseSessionAdapterTest.suite());
        suite.addTest(DefaultSessionLogAdapterTest.suite());
        suite.addTest(SCPlatformManagerTest.suite());
        suite.addTest(ReadConnectionPoolAdapterTest.suite());
        suite.addTest(SessionBrokerAdapterTest.suite());
        suite.addTest(TopLinkSessionsAdapterTest.suite());

        return suite;
    }
}
