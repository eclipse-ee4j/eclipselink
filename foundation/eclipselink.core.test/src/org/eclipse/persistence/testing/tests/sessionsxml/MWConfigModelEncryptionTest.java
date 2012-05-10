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

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.sessions.factories.SessionManager;


/**
 * Test that a getPassword() call from the Mapping Workbench to the config
 * model detects unencrypted passwords and then re-stores encrypted on the model.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date Septmeber 28, 2004
 */
public class MWConfigModelEncryptionTest extends AutoVerifyTestCase {
    DatabaseSessionConfig m_sessionConfig1;
    DatabaseSessionConfig m_sessionConfig2;


    public MWConfigModelEncryptionTest() {
        setDescription("Tests the detection and encryption of a unencrypted password on the config model");
    }

    public void test() throws Exception {
        // Read a session with an unencrypted password
        SessionConfigs m_sessions = SessionManager.getManager().getInternalMWConfigObjects("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSession.xml", getClass().getClassLoader());

        // There are 2 sessions in this session.xml file with unencrypted 
        // passwords
        // The first one will preserve the password and test the forced 
        // encryption on the getPassword() call
        m_sessionConfig1 = (DatabaseSessionConfig)m_sessions.getSessionConfigs().firstElement();

        // The second one will blank out the password and test that the 
        // getPassword() call doesn't throw a null pointer exception
        m_sessionConfig2 = (DatabaseSessionConfig)m_sessions.getSessionConfigs().lastElement();
        m_sessionConfig2.getLoginConfig().setEncryptedPassword(null);
    }

    protected void verify() {
        // For the first config test the encryption with an actual password stored
        String password1 = m_sessionConfig1.getLoginConfig().getEncryptedPassword();
        String password2 = m_sessionConfig1.getLoginConfig().getPassword();
        String password3 = m_sessionConfig1.getLoginConfig().getEncryptedPassword();

        if (password1.equals(password3)) {
            throw new TestErrorException("The password was not encrypted on the getPassword() call. Either the password was already encrypted or the detection failed.");
        }

        // For the second config test the getPassword() call with a null password
        try {
            m_sessionConfig2.getLoginConfig().getPassword();
        } catch (NullPointerException e) {
            throw new TestErrorException("Call to getPassword() with a null password caused a NPE");
        }

    }
}
