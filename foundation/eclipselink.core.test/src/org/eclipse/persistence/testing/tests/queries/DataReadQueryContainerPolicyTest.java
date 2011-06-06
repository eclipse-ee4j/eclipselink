/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
