/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;
import org.eclipse.persistence.testing.framework.TestErrorException;

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

    @Override
    public void verify() {
        SessionLog sessionLog = new SessionManager().getSession(new XMLSessionConfigLoader(getSessionXmlFileName()),
                                                                getSessionName(),
                                                                this.getClass().getClassLoader(),
                                                                false,
                                                                false).getSessionLog();
        if (!"org.eclipse.persistence.logging.jul.JavaLog".equals(sessionLog.getClass().getName())) {
            throw new TestErrorException("Failed to create TopLink Session Log");
        }
    }
}
