/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.coordination.TransportManager;
import org.eclipse.persistence.testing.framework.TestErrorException;


public class RcmEncryptedPasswordTest extends RcmBasicTest {
    public RcmEncryptedPasswordTest() {
        sessionsXmlFileName = "org/eclipse/persistence/testing/models/sessionsxml/sessions_rcm_encrypted_password.xml";
    }

    public void verify() {

        TransportManager transportMgr = ((AbstractSession)loadedSession).getCommandManager().getTransportManager();
        String encryptedPassword = transportMgr.getPassword();
        if (!encryptedPassword.equals("do-not-encrypt-me")) {
            throw new TestErrorException("Encrypted password [" + encryptedPassword + "] does not match.");
        }

        // test if encryption-class-name is processed
        transportMgr.setPassword("encrypt-me");
        CustomEncryption encryptionObject = new CustomEncryption();

        if (!encryptionObject.decryptPassword(transportMgr.getPassword()).equals("encrypt-me")) {
            throw new TestErrorException("Custom encryption class is not used.");
        }
    }

}
