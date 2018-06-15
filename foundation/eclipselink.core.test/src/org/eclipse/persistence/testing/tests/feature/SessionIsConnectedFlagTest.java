/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     David Minsky - initial API and implementation
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;

import org.eclipse.persistence.testing.framework.TestCase;

/**
 * EL bug 332456
 * Test that a DatabaseSession responds correctly to the isConnected() call.
 * Test each Accessor isConnected() == false after a disconnection and true upon
 * reconnection.
 */
public class SessionIsConnectedFlagTest extends TestCase {

    public SessionIsConnectedFlagTest() {
        super();
        setDescription("Test Session isConnected flag pre-logout, post-logout and post-login");
    }

    public void test() {
        if (!getSession().isDatabaseSession()) {
            throwWarning("Test is designed to be run with DatabaseSession");
        }
        DatabaseSession session = (DatabaseSession)getSession();

        // session needs to be verified as connected
        assertTrue("Session isConnected should be true before logout", session.isConnected());
        // test session's accessors
        for (Accessor accessor : ((DatabaseSessionImpl)session).getAccessors()) {
            assertTrue(accessor.isConnected());
        }

        // logout session and test that the isConnected flag is false
        session.logout();
        assertFalse("Session isConnected should be false after logout", session.isConnected());
        // test session's accessors
        for (Accessor accessor : ((DatabaseSessionImpl)session).getAccessors()) {
            assertFalse(accessor.isConnected());
        }

        // login session and test that the isConnected flag is true
        session.login();
        assertTrue("Session isConnected should be true after login", session.isConnected());
        // test session's accessors
        for (Accessor accessor : ((DatabaseSessionImpl)session).getAccessors()) {
            assertTrue(accessor.isConnected());
        }
    }

}
