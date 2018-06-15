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
//     04/01/2016-2.7 Tomas Kraus
//       - 490677: Database connection properties made configurable in test.properties
package org.eclipse.persistence.testing.tests.eis.aq;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.framework.junit.LogTestExecution;
import org.eclipse.persistence.testing.tests.nosql.SessionHelper;
import org.junit.Rule;
import org.junit.Test;

/**
 * Simple connect test. This tests connecting TopLink EIS to the native AQ JCA connector.
 */
public class ConnectTest {

    /** Log the test being currently executed. */
    @Rule public LogTestExecution logExecution = new LogTestExecution();

    /**
     * Test database session creation.
     */
    @Test
    public void testDatabaseSession() throws Exception {
        DatabaseSession session = SessionHelper.createDatabaseSession(AQTestSuite.project);
        session.logout();
    }

    /**
     * Test server session creation.
     */
    @Test
    public void testServerSession() throws Exception {
        Server session = SessionHelper.createServerSession(AQTestSuite.project);
        session.logout();
    }

}
