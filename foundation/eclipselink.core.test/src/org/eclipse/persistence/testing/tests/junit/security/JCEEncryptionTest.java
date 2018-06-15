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
package org.eclipse.persistence.testing.tests.junit.security;

import org.junit.Assert;
import org.junit.Test;
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
public class JCEEncryptionTest {

    @Test
    public void test() {
        String toEncrypt = "testString";
        Securable encryptor = convertToEncryptionObject("org.eclipse.persistence.internal.security.JCEEncryptor");
        Assert.assertNotNull("JCE object could not be created.", encryptor);
        String decrypted = encryptor.decryptPassword(encryptor.encryptPassword(toEncrypt));
        Assert.assertEquals("The JCE encryption --> decryption failed", toEncrypt, decrypted);
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
