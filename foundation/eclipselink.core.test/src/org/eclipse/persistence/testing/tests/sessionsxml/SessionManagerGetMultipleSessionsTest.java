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

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * Tests bug#5685604, ensuring that multiple sessions.xml files can be loaded,
 * without the second load corrupting the first session.
 */
public class SessionManagerGetMultipleSessionsTest extends TestCase {

    public SessionManagerGetMultipleSessionsTest() {
        setDescription("Tests bug#5685604, ensuring that multiple sessions.xml files can be loaded.");
    }

    public void test() {
        SessionManager.getManager().getSessions().remove("EmployeeSession");
        SessionManager.getManager().getSessions().remove("ServerSession");
        XMLSessionConfigLoader employeeLoader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSession.xml");
        employeeLoader.setSessionName("EmployeeSession");
        employeeLoader.setClassLoader(getClass().getClassLoader());
        XMLSessionConfigLoader serverLoader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaServerSession.xml");
        serverLoader.setSessionName("ServerSession");
        serverLoader.setClassLoader(getClass().getClassLoader());
        serverLoader.setShouldLogin(false);
        Session session1 = SessionManager.getManager().getSession(employeeLoader);
        Session session2 = SessionManager.getManager().getSession(serverLoader);
        Session session3 = SessionManager.getManager().getSession(employeeLoader);
        if ((session1 != session3) || (!session1.isConnected())) {
            throwError("SessionManager corrupted old sessions when loading new sessions.xml.");
        }
    }

    public void reset() {
        SessionManager.getManager().destroySession("EmployeeSession");
        SessionManager.getManager().destroySession("ServerSession");
    }
}
