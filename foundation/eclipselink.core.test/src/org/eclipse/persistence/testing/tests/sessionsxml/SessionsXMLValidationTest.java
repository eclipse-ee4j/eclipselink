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

// TopLink imports
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.factories.*;

// Testing imports
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.validation.ExceptionTest;

public class SessionsXMLValidationTest extends ExceptionTest {

    private Session loadedSession = null;
    private String sessionName = null;
    private String sessionXmlFileName = null;

    public SessionsXMLValidationTest() {
    }

    public SessionsXMLValidationTest(EclipseLinkException expected) {
        this.expectedException = expected;
        sessionName = "test";
    }

    public static SessionsXMLValidationTest sessionXmlNotFound() {
        SessionsXMLValidationTest test = new SessionsXMLValidationTest(ValidationException.noSessionsXMLFound("SomeDumbName"));

        test.setName("Sessions.xml not found");
        test.setSessionXmlFileName("SomeDumbName");

        return test;
    }

    public static void addTestsTo(TestSuite theSuite) {
        theSuite.addTest(sessionXmlNotFound());
    }

    protected Session getLoadedSession() {
        return loadedSession;
    }

    protected String getSessionName() {
        return sessionName;
    }

    protected String getSessionXmlFileName() {
        return sessionXmlFileName;
    }

    public void test() throws Exception {
        if (getSessionXmlFileName() != null) {
            SessionManager testManager = new SessionManager();
            XMLSessionConfigLoader loader = new XMLSessionConfigLoader(getSessionXmlFileName());
            try {
                Session session = testManager.getSession(loader, getSessionName(), getClass().getClassLoader(), false, true);
                setLoadedSession(session);
            } catch (EclipseLinkException exception) {
                caughtException = exception;
            }
        }
    }

    protected void setLoadedSession(Session loadedSession) {
        this.loadedSession = loadedSession;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void setSessionXmlFileName(String newName) {
        sessionXmlFileName = newName;
    }

    /**
     * Also verify the number of errors for SessionLoaderException.
     */
    public void verify() {
        // This class can also be used to to test loading a session successfully.
        if (this.expectedException == null) {
            if (this.caughtException != null) {
                throw new TestErrorException("Session failed to load properly.  Caught: " + caughtException);
            } else {
                return;
            }
        }
        super.verify();
        if (this.expectedException instanceof SessionLoaderException) {
            if (((SessionLoaderException)expectedException).getExceptionList().size() != ((SessionLoaderException)caughtException).getExceptionList().size()) {
                throw new TestErrorException("Incorrect number of exceptions caught:" + "\nExpected:" + expectedException + "\nCaught:" + caughtException);
            }
        }
    }
}

