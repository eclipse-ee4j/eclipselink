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

import org.eclipse.persistence.testing.tests.sessionsxml.SessionsXMLValidationTest;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


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
        if (!(new SessionManager().getSession(new XMLSessionConfigLoader(getSessionXmlFileName()), getSessionName(), this.getClass().getClassLoader(), false, false).getSessionLog() instanceof org.eclipse.persistence.logging.JavaLog)) {
            throw new TestErrorException("Failed to create TopLink Session Log");
        }
    }
}
