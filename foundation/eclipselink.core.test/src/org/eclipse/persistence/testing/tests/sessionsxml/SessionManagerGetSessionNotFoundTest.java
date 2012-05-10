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

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * Test that a NO_SESSION_FOUND exception is caught on a getSession call to the
 * SessionManager for a session that does not exist.
 *
 * Must be using the new loader, that is the XMLSessionConfigLoader and not
 * the deprecated XMLLoader.
 *
 * Bug #3821922
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date September 27, 2004
 */
public class SessionManagerGetSessionNotFoundTest extends AutoVerifyTestCase {
    DatabaseSession m_session;
    ValidationException m_exception;

    public SessionManagerGetSessionNotFoundTest() {
        setDescription("Tests that the proper exception is thrown when a session is not found in the session.xml file");
    }

    public void reset() {
        if ((m_session != null) && m_session.isConnected()) {
            m_session.logout();
            SessionManager.getManager().getSessions().remove("SessionShouldNotBeFound");
            m_session = null;
        }
    }

    protected void setup() {
        m_session = null;
        m_exception = null;
    }

    public void test() {
        try {
            XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSession.xml");
            m_session = (DatabaseSession)SessionManager.getManager().getSession(loader, "SessionShouldNotBeFound", getClass().getClassLoader(), false, false);
        } catch (ValidationException e) {
            m_exception = e;
        }
    }

    protected void verify() {
        if (m_exception == null) {
            throw new TestErrorException("No exception was caught on the getSession call");
        } else if (m_exception.getErrorCode() != ValidationException.NO_SESSION_FOUND) {
            throw new TestErrorException("The wrong exception was caught. Caught (" + m_exception.getErrorCode() + "), wanted (" + ValidationException.NO_SESSION_FOUND + ")");
        }
    }
}
