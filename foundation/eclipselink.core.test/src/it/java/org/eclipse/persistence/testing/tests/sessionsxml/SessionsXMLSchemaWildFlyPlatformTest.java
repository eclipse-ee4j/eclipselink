/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.platform.server.wildfly.WildFlyPlatform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


/**
 * Tests server platform tag for WildFly.
 *
 * @version 1.0
 */
public class SessionsXMLSchemaWildFlyPlatformTest extends AutoVerifyTestCase {
    Exception m_exceptionCaught;
    DatabaseSession m_employeeSession;

    public SessionsXMLSchemaWildFlyPlatformTest() {
        setDescription("Tests loading a WildFly platform from the schema.");
    }

    @Override
    public void reset() {
        if ((m_employeeSession != null) && m_employeeSession.isConnected()) {
            m_employeeSession.logout();
            SessionManager.getManager().getSessions().remove(m_employeeSession);
            m_employeeSession = null;
        }
    }

    @Override
    protected void setup() {
        m_exceptionCaught = null;
    }

    @Override
    public void test() {
        try {
            XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSessionWildFlyPlatform.xml");

            m_employeeSession = (DatabaseSession)SessionManager.getManager().getSession(loader, "EmployeeSessionWildFly", getClass().getClassLoader(), false, true);
        } catch (Exception e) {
            m_exceptionCaught = e;
        }
    }

    @Override
    protected void verify() {
        if (m_exceptionCaught != null) {
            throw new TestErrorException("Loading of the session failed: " + m_exceptionCaught);
        }

        if (m_employeeSession == null) {
            throw new TestErrorException("Loaded session was null");
        }

        if (!(m_employeeSession.getServerPlatform() instanceof WildFlyPlatform)) {
            throw new TestErrorException("The loaded server platform was not the correct type.");
        }
    }
}
