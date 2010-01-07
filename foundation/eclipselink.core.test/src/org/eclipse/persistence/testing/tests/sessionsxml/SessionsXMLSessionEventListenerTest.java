/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.io.File;

import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * Tests the session event listeners on sessions and a session broker. Ensures
 * the preLogin event fires on not only the session broker but its sessions
 * as well.
 * 
 * @author Guy Pelletier
 * @version 1.0
 * @date July 15, 2004
 */
public class SessionsXMLSessionEventListenerTest extends AutoVerifyTestCase {
    SessionBroker m_sessionBroker;

    public SessionsXMLSessionEventListenerTest() {
        setDescription("Test the event listeners set on a session broker and its sessions");
    }

    public void reset() {
        if (m_sessionBroker != null && m_sessionBroker.isConnected()) {
            m_sessionBroker.logout(); // If session is logged in, log it out
            SessionManager.getManager().getSessions().remove(m_sessionBroker);
            m_sessionBroker = null;
        }
    }

    public void test() {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSessionEventListenerSession.xml");

        // log in the session
            m_sessionBroker = (SessionBroker)SessionManager.getManager().getSession(loader, "SessionBroker", getClass().getClassLoader(), true, true); // refresh the session  
    }

    protected void verify() {
        File f1 = new File(SessionBrokerEventListener.FILENAME);

        if (f1.exists()) {
            f1.delete();
        } else {
            throw new TestErrorException("Pre-login event did not fire on the session broker");
        }

        File f2 = new File(ServerSessionEventListener.FILENAME);

        if (f2.exists()) {
            f2.delete();
        } else {
            throw new TestErrorException("Pre-login event did not fire on the server session");
        }
    }
}
