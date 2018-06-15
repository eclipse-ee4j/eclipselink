/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
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
