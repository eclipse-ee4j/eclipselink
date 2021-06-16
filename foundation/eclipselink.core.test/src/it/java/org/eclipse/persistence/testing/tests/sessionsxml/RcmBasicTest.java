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

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * Load a Session from an xml file that conforms to TopLink XML Schema Session
 */
public abstract class RcmBasicTest extends TestCase {
    public String sessionsXmlFileName = "sessions xml file name";
    public String sessionName = "rcm_test_session";
    public DatabaseSession loadedSession;

    /**
     * Load the session
     */
    protected void setup() {
        SessionManager.getManager().getSessions().remove(sessionName);
        loadedSession = (DatabaseSession)SessionManager.getManager().getSession(new XMLSessionConfigLoader(sessionsXmlFileName), sessionName, this.getClass().getClassLoader(), false, true);
    }

    /**
     * Remove the session from the singleton SessionManager
     */
    public void reset() {
        SessionManager.getManager().destroySession(sessionName);
    }
}
