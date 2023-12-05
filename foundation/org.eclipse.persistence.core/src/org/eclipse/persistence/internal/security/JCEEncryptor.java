/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

/**
 * EclipseLink reference implementation for password encryption.
 *
 * @author Guy Pelletier
 */
public final class JCEEncryptor implements org.eclipse.persistence.security.Securable {

    private String sessionName;

    // Legacy DES ECB cipher used for backwards compatibility decryption only.
    private static final String DES_ECB = "DES/ECB/PKCS5Padding";
    private final Cipher decryptCipherDES_ECB;

    // Legacy AES ECB cipher used for backwards compatibility decryption only.
    private static final String AES_ECB = "AES/ECB/PKCS5Padding";
    private final Cipher decryptCipherAES_ECB;

    // Legacy AES CBC cipher used for backwards compatibility decryption only.
    private static final String AES_CBC = "AES/CBC/PKCS5Padding";
    private final Cipher decryptCipherAES_CBC;

    // All encryption is done through the AES GCM cipher.
    private static final byte IV_GCM_LENGTH = 16;
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private final Cipher encryptCipherAES_GCM;
    private final Cipher decryptCipherAES_GCM;


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
        decryptCipherAES_CBC = Cipher.getInstance(AES_CBC);
        decryptCipherAES_CBC.init(Cipher.DECRYPT_MODE, sk, iv);

        encryptCipherAES_GCM = Cipher.getInstance(AES_GCM);

        decryptCipherAES_GCM = Cipher.getInstance(AES_GCM);
    }

    /**
     * Encrypts a string. Will throw a validation exception.
     */
    @Override
    public synchronized String encryptPassword(String password) {
        try {
            byte[] ivGCM = Synergizer.getIvGCM();
            AlgorithmParameterSpec parameterSpecGCM = new GCMParameterSpec(128, ivGCM);
            SecretKey skGCM = Synergizer.getAESGCMMultitasker();
            encryptCipherAES_GCM.init(Cipher.ENCRYPT_MODE, skGCM, parameterSpecGCM);
            byte[] bytePassword = encryptCipherAES_GCM.doFinal(password.getBytes("UTF-8"));
            byte[] result = Arrays.copyOf(ivGCM, IV_GCM_LENGTH + bytePassword.length);
            System.arraycopy(bytePassword, 0, result, IV_GCM_LENGTH, bytePassword.length);
            return Helper.buildHexStringFromBytes(result);
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
        byte[] input = null;
        byte[] bytePassword = null;

        try {
            input = Helper.buildBytesFromHexString(encryptedPswd);
            SecretKey skGCM = Synergizer.getAESGCMMultitasker();
            byte[] ivGCM = new byte[IV_GCM_LENGTH];
            System.arraycopy(input, 0, ivGCM, 0, IV_GCM_LENGTH);
            AlgorithmParameterSpec parameterSpecGCM = new GCMParameterSpec(128, ivGCM);
            bytePassword = new byte[input.length - IV_GCM_LENGTH];
            System.arraycopy(input, IV_GCM_LENGTH, bytePassword, 0, input.length - IV_GCM_LENGTH);
            decryptCipherAES_GCM.init(Cipher.DECRYPT_MODE, skGCM, parameterSpecGCM);
            // try AES/GCM first
            password = new String(decryptCipherAES_GCM.doFinal(bytePassword), "UTF-8");
        } catch (ConversionException | IllegalBlockSizeException ce) {
            // buildBytesFromHexString failed, assume clear text
            password = encryptedPswd;
        } catch (Exception u) {
            try {
                if (sessionName != null) {
                    AbstractSessionLog.getLog().log(SessionLog.WARNING, SessionLog.JPA, "encryptor_decrypt_old_algorithm", new Object[] {sessionName});
                } else {
                    AbstractSessionLog.getLog().log(SessionLog.WARNING, SessionLog.JPA, "encryptor_decrypt_old_algorithm_without_session_name", null);
                }
                // try AES/CBC second
                bytePassword = Helper.buildBytesFromHexString(encryptedPswd);
                password = new String(decryptCipherAES_CBC.doFinal(bytePassword), "UTF-8");
            } catch (Exception w) {
                ObjectInputStream oisAes = null;
                try {
                    // try AES/ECB third
                    oisAes = new ObjectInputStream(new CipherInputStream(new ByteArrayInputStream(bytePassword), decryptCipherAES_ECB));
                    password = (String) oisAes.readObject();
                } catch (Exception x) {
                    ObjectInputStream oisDes = null;
                    try {
                        // try DES/ECB fourth
                        oisDes = new ObjectInputStream(new CipherInputStream(new ByteArrayInputStream(bytePassword), decryptCipherDES_ECB));
                        password = (String) oisDes.readObject();
                    } catch (ArrayIndexOutOfBoundsException y) {
                        // JCE 1.2.1 couldn't decrypt it, assume clear text
                        password = encryptedPswd;
                    } catch (Exception z) {
                        if (z.getCause() instanceof IllegalBlockSizeException) {
                            // JCE couldn't decrypt it, assume clear text
                            password = encryptedPswd;
                        } else {
                            throw ValidationException.errorDecryptingPassword(z);
                        }
                    } finally {
                        if (oisDes != null) {
                            try {
                                oisDes.close();
                            } catch (IOException e2) {
                            }
                        }
                    }
                } finally {
                    if (oisAes != null) {
                        try {
                            oisAes.close();
                        } catch (IOException e1) {
                        }
                    }
                }
            }
        }
        return password;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
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

        private static SecretKey getAESGCMMultitasker() throws Exception {
            return new SecretKeySpec(Helper.buildBytesFromHexString("64EF2D9B738ACA254A48F14754030FC2"), "AES");
        }

        private static IvParameterSpec getIvSpec() {
            byte[] b = new byte[] {
                    (byte) -26, (byte) 124, (byte) -99, (byte) 32,
                    (byte) -37, (byte) -58, (byte) -93, (byte) 100,
                    (byte) 126, (byte) -55, (byte) -21, (byte) 48,
                    (byte) -86, (byte) 97, (byte) 12, (byte) 113};
            return new IvParameterSpec(b);
        }

        private static byte[] getIvGCM() {
            byte[] ivGCM = new byte[IV_GCM_LENGTH];
            SecureRandom random = null;
            try {
                random = SecureRandom.getInstanceStrong();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            random.nextBytes(ivGCM);
            return ivGCM;
        }
    }
}
