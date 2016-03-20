/*******************************************************************************
 * Copyright (c) 1998, 2016 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.Helper;

/**
 * TopLink reference implementation for password encryption.
 *
 * @author Guy Pelletier
 */
public class JCEEncryptor implements Securable {
    // Legacy decrypt cipher used for backwards compatibility only.
    private static final String DES = "DES/ECB/PKCS5Padding";
    private final Cipher decryptCipherDES;

    // Legacy AES cipher used for backwards compatibility only.
    private static final String AES = "AES/ECB/PKCS5Padding";
    private final Cipher decryptCipherAES;

    // All encryption is done through the AES cipher.
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
        decryptCipherDES = Cipher.getInstance(DES);
        decryptCipherDES.init(Cipher.DECRYPT_MODE, Synergizer.getDESMultitasker());

        decryptCipherAES = Cipher.getInstance(AES);
        decryptCipherAES.init(Cipher.DECRYPT_MODE, Synergizer.getAESMultitasker());

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
        String password = null;

        if (encryptedPswd != null) {
            byte[] bytePassword = new byte[0];
            try {
                bytePassword = Helper.buildBytesFromHexString(encryptedPswd);
                password = new String(decryptCipherAES_CBC.doFinal(bytePassword), "UTF-8");
            } catch (ConversionException ce) {
                // Never prepared (buildBytesFromHexString failed), assume clear text
                password = encryptedPswd;
            } catch (Exception f) {
                try {
                    // try AES/ECB
                    password = new String(decryptCipherAES.doFinal(bytePassword));
                } catch (Exception exc) {
                    // Catch all exceptions when trying to decrypt using AES and try the
                    // old DES decryptor before deciding what to do.
                    try {
                        password = new String(decryptCipherDES.doFinal(bytePassword));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        // JCE 1.2.1 couldn't decrypt it, assume clear text
                        password = encryptedPswd;
                    } catch (Exception e) {
                        throw ValidationException.errorDecryptingPassword(e);
                    }
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
