/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.exceptions.SessionLoaderException;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * Tests Sessions XML schema with invalid tag.
 *
 * @author Edwin Tang
 * @version 1.0
 * @date December 2, 2004
 */
public class SessionsXMLSchemaIncorrectTagValuesTest extends TestCase {
    Exception exception = null;

    public SessionsXMLSchemaIncorrectTagValuesTest() {
        setDescription("Test Sessions XML schema with incorrect tag values.");
    }

    public void test() {
        SessionManager.getManager().getSessions().remove("ServerSession");
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaIncorrectTagValues.xml");
        try {
            ServerSession serverSession = (ServerSession)SessionManager.getManager().getSession(loader, "ServerSession", getClass().getClassLoader(), false, false);
        } catch (Exception e) {
            exception = e;
        }
    }

    protected void verify() {
        if (exception == null || ((SessionLoaderException)exception).getErrorCode() != SessionLoaderException.FINAL_EXCEPTION) {
            throw new TestErrorException("SessionsXMLSchemaIncorrectTagValuesTest failed.", exception);
        }
    }
}
