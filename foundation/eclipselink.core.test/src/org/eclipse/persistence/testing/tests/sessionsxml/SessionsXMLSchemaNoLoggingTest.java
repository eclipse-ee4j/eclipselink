/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * Tests a basic session xml file that is built and validated against the
 * XML Schema
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date February 13, 2004
 */
public class SessionsXMLSchemaNoLoggingTest extends AutoVerifyTestCase {
    boolean m_exceptionCaught;

    public SessionsXMLSchemaNoLoggingTest() {
        setDescription("Test loading of a basic session xml with no logging tag");
    }

    public void reset() {
    }

    protected void setup() {
        m_exceptionCaught = false;
    }

    public void test() {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSessionNoLogging.xml");

        try {
            SessionManager.getManager().getSession(loader, "EmployeeSession", getClass().getClassLoader(), false, false);
        } catch (Exception e) {
            // Previously if the logging tag was not set in the session.xml file than
            // we would try to set a null SessionLog on the Session. This will cause
            // a null pointer exception
            m_exceptionCaught = true;
        }
    }

    protected void verify() {
        if (m_exceptionCaught) {
            throw new TestErrorException("Null pointer exception was caught while loading");
        }
    }
}
