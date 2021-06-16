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
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * Tests server platform tag for websphere 6.0.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date July 22, 2005
 */
public class SessionsXMLSchemaWebsphere61PlatformTest extends AutoVerifyTestCase {
    Exception m_exceptionCaught;
    DatabaseSession m_employeeSession;

    public SessionsXMLSchemaWebsphere61PlatformTest() {
        setDescription("Tests loading a websphere 6.0 platform from the schema.");
    }

    public void reset() {
        if (m_employeeSession != null && m_employeeSession.isConnected()) {
            m_employeeSession.logout();
            SessionManager.getManager().getSessions().remove(m_employeeSession);
            m_employeeSession = null;
        }
    }

    protected void setup() {
        m_exceptionCaught = null;
    }

    public void test() {
        try {
            XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSessionWAS61Platform.xml");

            m_employeeSession = (DatabaseSession)SessionManager.getManager().getSession(loader, "EmployeeSession", getClass().getClassLoader(), false, true);
        } catch (Exception e) {
            m_exceptionCaught = e;
        }
    }

    protected void verify() {
        if (m_exceptionCaught != null) {
            throw new TestErrorException("Loading of the session failed: " + m_exceptionCaught, m_exceptionCaught);
        }

        if (m_employeeSession == null) {
            throw new TestErrorException("Loaded session was null");
        }

    }
}
