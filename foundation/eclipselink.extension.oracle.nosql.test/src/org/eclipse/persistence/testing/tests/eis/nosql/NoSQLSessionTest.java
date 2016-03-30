/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/30/2016-2.7 Tomas Kraus
 *       - 490677: Initial API and implementation.
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.eis.nosql;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.tests.nosql.LogTestExecution;
import org.eclipse.persistence.testing.tests.nosql.SessionHelper;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test EclipseLink EIS sessions creation and login with the Oracle NoSQL database.
 */
public class NoSQLSessionTest {

    /** Log the test being currently executed. */
    @Rule public LogTestExecution logExecution = new LogTestExecution();

    /**
     * Test {@link DatabaseSession} creation and login.
     */
    @Test
    public void testDatabaseSession() throws Exception {
        final DatabaseSession session = SessionHelper.createDatabaseSession(NoSQLTestSuite.project);
        session.logout();
    }

    /**
     * Test {@link Server} session creation and login.
     */
    @Test
    public void testServerSession() throws Exception {
        final Server session = SessionHelper.createServerSession(NoSQLTestSuite.project);
        session.logout();
    }

}
