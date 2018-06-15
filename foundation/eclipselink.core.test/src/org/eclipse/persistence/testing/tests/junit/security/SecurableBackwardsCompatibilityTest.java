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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.junit.security;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.JCEEncryptor;
import org.eclipse.persistence.internal.security.Securable;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression test suite for the EclipseLink reference implementation of the Securable interface
 * @author dminsky
 */
public class SecurableBackwardsCompatibilityTest {
    
    /**
     * Test the decryption of a String encrypted with DES ECB.
     * @throws Exception
     */
    @Test
    public void testStringDecryption_DES_ECB() throws Exception {
        String plainTextString = "welcome123_des_ecb";
        
        String testString = encryptString_DES_ECB(plainTextString);
        Assert.assertFalse("Strings should not match.", plainTextString.equals(testString));
        
        Securable securable = new JCEEncryptor();
        String decryptedString = securable.decryptPassword(testString);
        Assert.assertEquals("Strings should match.", plainTextString, decryptedString);
    }
    
    /**
     * Test the decryption of a String encrypted with AES CBC.
     * @throws Exception
     */
    @Test
    public void testStringDecryption_AES_CBC() throws Exception {
        String plainTextString = "welcome123_aes_cbc";

        Securable securable = new JCEEncryptor();
        String testString = securable.encryptPassword(plainTextString);
        Assert.assertFalse("Strings should not match.", plainTextString.equals(testString));
        
        String decryptedString = securable.decryptPassword(testString);
        Assert.assertEquals("Strings should match.", plainTextString, decryptedString);
    }
    
    /**
     * Test the decryption of a String encrypted with AES ECB.
     * @throws Exception
     */
    @Test
    public void testStringDecryption_AES_ECB() throws Exception {
        String plainTextString = "welcome123_aes_ecb";
        
        String testString = encryptString_AES_ECB(plainTextString);
        Assert.assertFalse("Strings should not match.", plainTextString.equals(testString));
        
        Securable securable = new JCEEncryptor();
        String decryptedString = securable.decryptPassword(testString);
        Assert.assertEquals("Strings should match.", plainTextString, decryptedString);
    }
    
    /**
     * Test the decryption/processing of a plaintext String.
     * @throws Exception
     */
    @Test
    public void testStringDecryption_PlainText() throws Exception {
        String plainTextString = "welcome123_plaintext";
        
        Securable securable = new JCEEncryptor();
        String decryptedString = securable.decryptPassword(plainTextString);
        Assert.assertEquals("Passwords should match.", plainTextString, decryptedString);
    }
    
    /**
     * Test the decryption/processing of a null parameter.
     * @throws Exception
     */
    @Test
    public void testNullParameterDecryption() throws Exception {
        Securable securable = new JCEEncryptor();
        String returnValue = securable.decryptPassword(null);
        Assert.assertNull("Null should be returned when decrypting a null value", returnValue);
    }
    
    /**
     * Test the encryption of a null parameter.
     * @throws Exception
     */
    @Test
    public void testNullParameterEncryption() throws Exception {
        ValidationException expectedException = null;
        try {
            Securable securable = new JCEEncryptor();
            securable.encryptPassword(null);
        } catch (ValidationException ve) {
            expectedException = ve;
        }
        Assert.assertNotNull("A ValidationException should be thrown when encrypting a null value", expectedException);
    }
    
    /*
     * Internal test utility:
     * Return a DES ECB encrypted version of the String parameter, using the legacy encryption code.
     */
    private String encryptString_DES_ECB(String aString) throws Exception {
        final byte[] bytes = Helper.buildBytesFromHexString("E60B80C7AEC78038");
        
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(bytes)));
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CipherOutputStream cos = new CipherOutputStream(baos, cipher);
        ObjectOutputStream oos = new ObjectOutputStream(cos);
        oos.writeObject(aString);
        oos.flush();
        oos.close();

        return Helper.buildHexStringFromBytes(baos.toByteArray());
    }
    
    /*
     * Internal test utility:
     * Return an AES ECB encrypted version of the String parameter, using the legacy encryption code.
     */
    private String encryptString_AES_ECB(String aString) throws Exception {
        final byte[] bytes = Helper.buildBytesFromHexString("3E7CFEF156E712906E1F603B59463C67");
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(bytes, "AES"));
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CipherOutputStream cos = new CipherOutputStream(baos, cipher);
        ObjectOutputStream oos = new ObjectOutputStream(cos);
        oos.writeObject(aString);
        oos.flush();
        oos.close();

        return Helper.buildHexStringFromBytes(baos.toByteArray());
    }
    
}
