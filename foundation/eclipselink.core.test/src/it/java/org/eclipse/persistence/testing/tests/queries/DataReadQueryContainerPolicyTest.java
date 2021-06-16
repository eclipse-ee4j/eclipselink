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
package org.eclipse.persistence.testing.tests.queries;

import java.io.IOException;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.insurance.*;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;

/**
 * From the deployment XML, if a direct collection mapping did not specify
 * a container policy on the direct read query (with no indirection) then
 * a null would be set as the container policy and at execution time (prepare)
 * a null pointer exception would be thrown.
 *
 * See BUG# 3337003
 *
 * @auther Guy Pelletier
 * @version 1.0 January 12/04
 */
public class DataReadQueryContainerPolicyTest extends TestCase {
    private boolean m_exceptionCaught;
    public DatabaseSession m_session;

    public DataReadQueryContainerPolicyTest() {
    }

    protected void setup() throws IOException {
        m_exceptionCaught = false;

        Project project = XMLProjectReader.read("testDeployment.xml", getClass().getClassLoader());
        m_session = project.createDatabaseSession();
        m_session.setSessionLog(getSession().getSessionLog());
        m_session.setLogin(getSession().getLogin());
        m_session.login();
    }

    public void test() {
        m_session.readAllObjects(PolicyHolder.class);
    }

    public void reset() throws IOException {
        m_session.logout();
    }
}
