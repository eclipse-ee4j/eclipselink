/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.junit.security;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.security.JCEEncryptor;
import org.eclipse.persistence.internal.security.Securable;
import org.eclipse.persistence.tools.security.JCEEncryptorCmd;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression test suite for the EclipseLink reference implementation of the Securable interface
 * @author dminsky
 */
public class SecurableBackwardsCompatibilityTest {

    /**
     * Test the decryption of a String encrypted with DES ECB.
     */
    @Test
    public void testStringDecryption_DES_ECB() throws Exception {
        String plainTextString = "welcome123_des_ecb";

        String testString = encryptString_DES_ECB(plainTextString);
        Assert.assertNotEquals("Strings should not match.", plainTextString, testString);

        JCEEncryptorCmd jceEncryptorCmd = new JCEEncryptorCmd();
        String decryptedString = jceEncryptorCmd.decryptPassword(testString);
        Assert.assertEquals("Strings should match.", plainTextString, decryptedString);
    }

    /**
     * Test the decryption of a String encrypted with AES GCM.
     */
    @Test
    public void testStringDecryption_AES_GCM() throws Exception {
        String plainTextString = "welcome123_aes_gcm";

        Securable securable = new JCEEncryptor();
        String testString = securable.encryptPassword(plainTextString);
        Assert.assertNotEquals("Strings should not match.", plainTextString, testString);

        String decryptedString = securable.decryptPassword(testString);
        Assert.assertEquals("Strings should match.", plainTextString, decryptedString);
    }

    /**
     * Test the decryption of a String encrypted with AES GCM via JCEEncryptorCmd.
     */
    @Test
    public void testStringDecryption_AES_GCM_via_jceEncryptorCmd() throws Exception {
        String plainTextString = "welcome123_aes_gcm";

        Securable securable = new JCEEncryptor();
        String testString = securable.encryptPassword(plainTextString);
        Assert.assertNotEquals("Strings should not match.", plainTextString, testString);

        JCEEncryptorCmd jceEncryptorCmd = new JCEEncryptorCmd();
        String decryptedString = jceEncryptorCmd.decryptPassword(testString);

        Assert.assertEquals("Strings should match.", plainTextString, decryptedString);
    }

    /**
     * Test the decryption of a String encrypted with AES CBC.
     */
    @Test
    public void testStringDecryption_AES_CBC() throws Exception {
        String plainTextString = "welcome123_aes_cbc";

        String testString = encryptString_AES_CBC(plainTextString);
        Assert.assertNotEquals("Strings should not match.", plainTextString, testString);

        JCEEncryptorCmd jceEncryptorCmd = new JCEEncryptorCmd();
        String decryptedString = jceEncryptorCmd.decryptPassword(testString);
        Assert.assertEquals("Strings should match.", plainTextString, decryptedString);
    }

    /**
     * Test the decryption of a String encrypted with AES ECB.
     */
    @Test
    public void testStringDecryption_AES_ECB() throws Exception {
        String plainTextString = "welcome123_aes_ecb";

        String testString = encryptString_AES_ECB(plainTextString);
        Assert.assertNotEquals("Strings should not match.", plainTextString, testString);

        JCEEncryptorCmd jceEncryptorCmd = new JCEEncryptorCmd();
        String decryptedString = jceEncryptorCmd.decryptPassword(testString);
        Assert.assertEquals("Strings should match.", plainTextString, decryptedString);
    }

    /**
     * Test the decryption/processing of a plaintext String.
     */
    @Test
    public void testStringDecryption_PlainText() throws Exception {
        String plainTextString = "welcome123_plaintext";

        Securable securable = new JCEEncryptor();
        String decryptedString = securable.decryptPassword(plainTextString);
        Assert.assertEquals("Passwords should match.", plainTextString, decryptedString);
    }

    /**
     * Test the decryption/processing of an empty String "".
     */
    @Test
    public void testEmptyStringParameterDecryption() throws Exception {
        Securable securable = new JCEEncryptor();
        String returnValue = securable.decryptPassword("");
        Assert.assertEquals("Empty string \"\" should be returned when decrypting a \"\" (empty string) value", "", returnValue);
    }

    /**
     * Test the decryption/processing of a null parameter.
     */
    @Test
    public void testNullParameterDecryption() throws Exception {
        Securable securable = new JCEEncryptor();
        String returnValue = securable.decryptPassword(null);
        Assert.assertNull("Null should be returned when decrypting a null value", returnValue);
    }

    /**
     * Test the encryption of a null parameter.
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

    /**
     * Test the decryption of a String encrypted with AES ECB and let JCEEncryptor throw Exception
     */
    @Test
    public void testStringDecryptionDeprecatedAlgorithmWithException() throws Exception {
        ValidationException expectedException = ValidationException.errorDecryptingPasswordOldAlgorithm(null);
        String plainTextString = "welcome123_aes_ecb";

        String testString = encryptString_AES_ECB(plainTextString);
        Assert.assertNotEquals("Strings should not match.", plainTextString, testString);

        Securable securable = new JCEEncryptor();
        try {
            String decryptedString = securable.decryptPassword(testString);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains(expectedException.getMessage()));
        }
    }

    /*
     * Internal test utility:
     * Return a DES ECB encrypted version of the String parameter, using the legacy encryption code.
     */
    private String encryptString_DES_ECB(String aString) throws Exception {
        final byte[] bytes = HexFormat.of().parseHex("E60B80C7AEC78038");

        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(bytes)));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CipherOutputStream cos = new CipherOutputStream(baos, cipher);
        ObjectOutputStream oos = new ObjectOutputStream(cos);
        oos.writeObject(aString);
        oos.flush();
        oos.close();

        return HexFormat.of().formatHex(baos.toByteArray());
    }

    /*
     * Internal test utility:
     * Return an AES CBC encrypted version of the String parameter, using the legacy encryption code.
     */
    private String encryptString_AES_CBC(String aString) throws Exception {
        final byte[] bytes = HexFormat.of().parseHex("2DB7354A48F1CA7B48ACA247540FC923");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = getIvSpec();
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(bytes, "AES"), iv);

        return HexFormat.of().formatHex(cipher.doFinal(aString.getBytes(StandardCharsets.UTF_8)));
    }

    /*
     * Internal test utility:
     * Return an AES ECB encrypted version of the String parameter, using the legacy encryption code.
     */
    private String encryptString_AES_ECB(String aString) throws Exception {
        final byte[] bytes = HexFormat.of().parseHex("3E7CFEF156E712906E1F603B59463C67");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(bytes, "AES"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CipherOutputStream cos = new CipherOutputStream(baos, cipher);
        ObjectOutputStream oos = new ObjectOutputStream(cos);
        oos.writeObject(aString);
        oos.flush();
        oos.close();

        return HexFormat.of().formatHex(baos.toByteArray());
    }

    private static IvParameterSpec getIvSpec() {
        byte[] b = new byte[] {
                (byte) -26, (byte) 124, (byte) -99, (byte) 32,
                (byte) -37, (byte) -58, (byte) -93, (byte) 100,
                (byte) 126, (byte) -55, (byte) -21, (byte) 48,
                (byte) -86, (byte) 97, (byte) 12, (byte) 113};
        return new IvParameterSpec(b);
    }
}
