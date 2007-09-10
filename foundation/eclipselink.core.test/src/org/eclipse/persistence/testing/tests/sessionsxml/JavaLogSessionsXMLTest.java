/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.exceptions.SessionLoaderException;
import org.eclipse.persistence.testing.tests.sessionsxml.SessionsXMLValidationTest;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigLoader;
import org.eclipse.persistence.sessions.factories.SessionManager;


// TopLink imports


// Testing imports

/**
 * Test a complete sessions.xml which contains all elements.
 * 
 * Requires that sessions_4_5.dtd should be in classpath
 *
 * @author Edwin Tang
 */
public class JavaLogSessionsXMLTest extends SessionsXMLValidationTest {

    public JavaLogSessionsXMLTest() {
        setName("JavaLogSessionsXMLTest");
        setSessionXmlFileName("org/eclipse/persistence/testing/models/sessionsxml/javalogsessions.xml");
        setSessionName("JavaLogSession");
    }

    public void verify() {
        if (org.eclipse.persistence.Version.isJDK13()) {
            int expectedExceptionNum = 2;
            if (((SessionLoaderException)caughtException).getExceptionList().size() != expectedExceptionNum) {
                throw new TestErrorException("Incorrect number of exceptions caught:" + "\nExpected: " + expectedExceptionNum + "\nCaught:" + caughtException);
            }
        } else {
            if (!(new SessionManager().getSession(new XMLSessionConfigLoader(getSessionXmlFileName()), getSessionName(), this.getClass().getClassLoader()).getSessionLog() instanceof org.eclipse.persistence.logging.JavaLog)) {
                throw new TestErrorException("Failed to create TopLink Session Log");
            }
        }
    }
}
