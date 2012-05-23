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
package org.eclipse.persistence.testing.tests.security;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.security.Securable;
import org.eclipse.persistence.internal.helper.ConversionManager;

/**
 * Test the actual JCE encryption and decryption. Main reason why this test case 
 * would fail is because something changed with the secret key.
 * 
 * A warning will be issued if JCE could not be found, that is, we couldn't 
 * instantiate a JCE object through the conversion manager.
 *
 * @author Guy Pelletier
 */
public class JCEEncryptionTest extends AutoVerifyTestCase {
    String toEncrypt;
    String decrypted;
    Securable encryptor;

    public JCEEncryptionTest() {
        setDescription("Test the encryption/decryption using JCE");
    }

    public void reset() {
    }

    protected void setup() throws Exception {
        toEncrypt = "testString";
        encryptor = convertToEncryptionObject("org.eclipse.persistence.internal.security.JCEEncryptor");
    }

    public void test() {
        if (encryptor != null) {
            decrypted = encryptor.decryptPassword(encryptor.encryptPassword(toEncrypt));
        }
    }

    protected void verify() {
        if (encryptor == null) {
            throw new TestWarningException("JCE object could not be created.");
        } else if (!decrypted.equals(toEncrypt)) {
            throw new TestErrorException("The JCE encryption --> decryption failed");
        }
    }

    /**
     * Convert a String into a Securable object
     * Class name must be fully qualified, eg. org.eclipse.persistence.internal.security.JCEEncryptor
     */
    private Securable convertToEncryptionObject(String encryptionClassName) {
        try {
            ConversionManager cm = ConversionManager.getDefaultManager();
            Class securableClass = (Class)cm.convertObject(encryptionClassName, Class.class);
            return (Securable)securableClass.newInstance();
        } catch (Throwable e) {
            return null;
        }
    }
}
