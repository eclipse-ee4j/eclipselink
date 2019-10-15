/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.Helper;

/**
 * EclipseLink reference implementation for password encryption.
 *
 * @author Guy Pelletier
 */
public class JCEEncryptor implements Securable {
    
    // Legacy DES ECB cipher used for backwards compatibility decryption only.
    private static final String DES_ECB = "DES/ECB/PKCS5Padding";
    private final Cipher decryptCipherDES_ECB;

    // Legacy AES ECB cipher used for backwards compatibility decryption only.
    private static final String AES_ECB = "AES/ECB/PKCS5Padding";
    private final Cipher decryptCipherAES_ECB;

    // All encryption is done through the AES CBC cipher.
    private static final String AES_CBC = "AES/CBC/PKCS5Padding";
    private final Cipher encryptCipherAES_CBC;
    private final Cipher decryptCipherAES_CBC;

    public JCEEncryptor() throws Exception {
        /**
         * We want to force the initialization of the cipher here. This is a fix
         * for bug #2696486.
         * JDev with JDK 1.3 in some cases will allow a JCE object to be created
         * when it shouldn't. That is, JDev includes an incompletely configured JCE
         * library for JDK 1.3, meaning JCE will not run properly in the VM. So, JDev
         * allows you to create a JCEEncryptor object, but eventually throw's
         * errors when trying to make JCE library calls from encryptPassword.
         *
         * Confusing??? Well, don't move this code before talking to Guy first!
         */
        decryptCipherDES_ECB = Cipher.getInstance(DES_ECB);
        decryptCipherDES_ECB.init(Cipher.DECRYPT_MODE, Synergizer.getDESMultitasker());

        decryptCipherAES_ECB = Cipher.getInstance(AES_ECB);
        decryptCipherAES_ECB.init(Cipher.DECRYPT_MODE, Synergizer.getAESMultitasker());

        SecretKey sk = Synergizer.getAESCBCMultitasker();
        IvParameterSpec iv = Synergizer.getIvSpec();
        encryptCipherAES_CBC = Cipher.getInstance(AES_CBC);
        encryptCipherAES_CBC.init(Cipher.ENCRYPT_MODE, sk, iv);

        decryptCipherAES_CBC = Cipher.getInstance(AES_CBC);
        decryptCipherAES_CBC.init(Cipher.DECRYPT_MODE, sk, iv);
    }

    /**
     * Encrypts a string. Will throw a validation exception.
     */
    @Override
    public synchronized String encryptPassword(String password) {
        try {
            return Helper.buildHexStringFromBytes(encryptCipherAES_CBC.doFinal(password.getBytes("UTF-8")));
        } catch (Exception e) {
            throw ValidationException.errorEncryptingPassword(e);
        }
    }

    /**
     * Decrypts a string. Will throw a validation exception.
     * Handles backwards compatibility for older encrypted strings.
     */
    @Override
    public synchronized String decryptPassword(String encryptedPswd) {
        if (encryptedPswd == null) { 
            return null;
        }

        String password = null;
        byte[] bytePassword = new byte[0];
        
        try {
            bytePassword = Helper.buildBytesFromHexString(encryptedPswd);
            // try AES/CBC first
            password = new String(decryptCipherAES_CBC.doFinal(bytePassword), "UTF-8");
        } catch (ConversionException | IllegalBlockSizeException ce) {
            // buildBytesFromHexString failed, assume clear text
            password = encryptedPswd;
        } catch (Exception e) {
            ObjectInputStream oisAes = null;
            try {
                // try AES/ECB second
                oisAes = new ObjectInputStream(new CipherInputStream(new ByteArrayInputStream(bytePassword), decryptCipherAES_ECB));
                password = (String)oisAes.readObject();
            } catch (Exception f) {
                ObjectInputStream oisDes = null;
                try {
                    // try DES/ECB third
                    oisDes = new ObjectInputStream(new CipherInputStream(new ByteArrayInputStream(bytePassword), decryptCipherDES_ECB));
                    password = (String)oisDes.readObject();
                } catch (ArrayIndexOutOfBoundsException g) {
                    // JCE 1.2.1 couldn't decrypt it, assume clear text
                    password = encryptedPswd;
                } catch (Exception h) {
                    if (h.getCause() instanceof IllegalBlockSizeException) {
                        // JCE couldn't decrypt it, assume clear text
                        password = encryptedPswd;
                    } else {
                        throw ValidationException.errorDecryptingPassword(h);
                    }
                } finally {
                    if (oisDes != null) {
                        try {
                            oisDes.close();
                        } catch (IOException e2) {} 
                    }
                }
            } finally {
                if (oisAes != null) {
                    try {
                        oisAes.close();
                    } catch (IOException e1) {} 
                }
            }
        }

        return password;
    }

    /**
     * Returns multitaskers for the ciphers. :-)
     */
    private static class Synergizer {
        private static SecretKey getDESMultitasker() throws Exception {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            return factory.generateSecret(new DESKeySpec(Helper.buildBytesFromHexString("E60B80C7AEC78038")));
        }

        private static SecretKey getAESMultitasker() throws Exception {
            return new SecretKeySpec(Helper.buildBytesFromHexString("3E7CFEF156E712906E1F603B59463C67"), "AES");
        }

        private static SecretKey getAESCBCMultitasker() throws Exception {
            return new SecretKeySpec(Helper.buildBytesFromHexString("2DB7354A48F1CA7B48ACA247540FC923"), "AES");
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
}
