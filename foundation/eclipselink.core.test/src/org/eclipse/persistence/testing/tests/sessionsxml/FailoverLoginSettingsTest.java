/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class FailoverLoginSettingsTest extends AutoVerifyTestCase {
    Exception m_exceptionCaught;
    DatabaseSession m_employeeSession;

    public FailoverLoginSettingsTest() {
        setDescription("Tests loading of failover Login settings.");
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
            XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/FailoverLoginSessionsXML.xml");

            m_employeeSession = (DatabaseSession)SessionManager.getManager().getSession(loader, "FailoverLoginSessions", getClass().getClassLoader(), false, true);
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

        if (m_employeeSession.getLogin().isConnectionHealthValidatedOnError()) {
            throw new TestErrorException("Failed to disable connection validation through XML");
        }
        if (m_employeeSession.getLogin().getDelayBetweenConnectionAttempts() != 0){
            throw new TestErrorException("Failed to set retry delay to 0.");
        }
        if (m_employeeSession.getLogin().getQueryRetryAttemptCount() != 0){
            throw new TestErrorException("Failed to set retry attempt count to 0.");
        }
        if (!m_employeeSession.getLogin().getPingSQL().equals("SELECT BLAHHA")){
            throw new TestErrorException("Failed to override Ping SQL from Sessions.xml");
        }
    }

}
