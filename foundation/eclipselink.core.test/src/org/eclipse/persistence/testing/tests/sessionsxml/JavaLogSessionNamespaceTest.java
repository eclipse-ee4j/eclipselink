/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.testing.tests.sessionsxml.SessionsXMLValidationTest;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.JavaLog;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;

/**
 * Test a complete sessions.xml which contains all elements.
 */
public class JavaLogSessionNamespaceTest extends SessionsXMLValidationTest {

    public JavaLogSessionNamespaceTest() {
        setName("JavaLogCategoryNameValidationTest");
        setSessionXmlFileName("org/eclipse/persistence/testing/models/sessionsxml/javalogsessions.xml");
        setSessionName("JavaLogSession");
    }

    public void verify() {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader(getSessionXmlFileName());
        Session session = SessionManager.getManager().getSession(loader, getSessionName(), this.getClass().getClassLoader(), false, false);
        if (!(session.getSessionLog() instanceof JavaLog)) {
            throw new TestErrorException("Failed to create TopLink Session Log");
        }
        
        String sessionName = session.getName(); // JavaLogSession
        String sessionNamespace = JavaLog.SESSION_TOPLINK_NAMESPACE + "." + sessionName;
     
        JavaLog log = (JavaLog)session.getSessionLog();
        if (!log.getCategoryLoggers().containsKey(sessionNamespace)) {
            throw new TestErrorException("JavaLog did not contain expected session namespace: [" + sessionNamespace + "] for session name: [" + sessionName + "]");
        }
    }
}
