/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.platform.server.jboss.JBossPlatform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * Tests server platform tag for JBoss.
 *
 * @version 1.0
 * @date July 22, 2005
 */
public class SessionsXMLSchemaJBossPlatformTest extends AutoVerifyTestCase {
    Exception m_exceptionCaught;
    DatabaseSession m_employeeSession;

    public SessionsXMLSchemaJBossPlatformTest() {
        setDescription("Tests loading a JBoss platform from the schema.");
    }

    public void reset() {
        if ((m_employeeSession != null) && m_employeeSession.isConnected()) {
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
            XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSessionJBossPlatform.xml");

            m_employeeSession = (DatabaseSession)SessionManager.getManager().getSession(loader, "EmployeeSessionJBoss", getClass().getClassLoader(), false, true);
        } catch (Exception e) {
            m_exceptionCaught = e;
        }
    }

    protected void verify() {
        if (m_exceptionCaught != null) {
            throw new TestErrorException("Loading of the session failed: " + m_exceptionCaught);
        }

        if (m_employeeSession == null) {
            throw new TestErrorException("Loaded session was null");
        }

        if (!(m_employeeSession.getServerPlatform() instanceof JBossPlatform)) {
            throw new TestErrorException("The loaded server platform was not the correct type.");
        }
    }
}
