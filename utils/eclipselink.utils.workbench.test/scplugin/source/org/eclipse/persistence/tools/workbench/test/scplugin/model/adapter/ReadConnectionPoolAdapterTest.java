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
package org.eclipse.persistence.tools.workbench.test.scplugin.model.adapter;

import org.eclipse.persistence.tools.workbench.test.scplugin.AllSCTests;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DataSource;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ReadConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;

public class ReadConnectionPoolAdapterTest extends AbstractAdapterTest
{
    public ReadConnectionPoolAdapterTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(ReadConnectionPoolAdapterTest.class, "ReadConnectionPoolAdapter Test");
    }

    public void testUseNonTransactionalReadLogin() throws Exception
    {
        TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
        DataSource ds = buildOracleDataSource();
        ServerSessionAdapter session = sessions.addServerSessionNamed("MyServerSession", noServerPlatform(), ds);
        session.addReadConnectionPool();
        ReadConnectionPoolAdapter pool = session.getReadConnectionPool();

        pool.setUseNonTransactionalReadLogin(true);

        assertTrue("useNonTransactionalReadLogin - Should not have problem", pool.usesNonTransactionalReadLogin());

    }
}
