/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2024 Contributors to the Eclipse Foundation. All rights reserved.
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

import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.exceptions.ValidationException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.HexFormat;

/**
 * EclipseLink reference implementation for password encryption.
 *
 * @author Guy Pelletier
 */
public final class JCEEncryptor implements org.eclipse.persistence.security.Securable {

    // All encryption is done through the AES GCM cipher.
    private static final byte IV_GCM_LENGTH = 16;
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private final Cipher encryptCipherAES_GCM;
    private final Cipher decryptCipherAES_GCM;


    public JCEEncryptor() throws Exception {
        /*
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
            byte[] bytePassword = encryptCipherAES_GCM.doFinal(password.getBytes(StandardCharsets.UTF_8));
            byte[] result = Arrays.copyOf(ivGCM, IV_GCM_LENGTH + bytePassword.length);
            System.arraycopy(bytePassword, 0, result, IV_GCM_LENGTH, bytePassword.length);
            return HexFormat.of().formatHex(result);
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
            input = HexFormat.of().parseHex(encryptedPswd);
            SecretKey skGCM = Synergizer.getAESGCMMultitasker();
            byte[] ivGCM = new byte[IV_GCM_LENGTH];
            System.arraycopy(input, 0, ivGCM, 0, IV_GCM_LENGTH);
            AlgorithmParameterSpec parameterSpecGCM = new GCMParameterSpec(128, ivGCM);
            bytePassword = new byte[input.length - IV_GCM_LENGTH];
            System.arraycopy(input, IV_GCM_LENGTH, bytePassword, 0, input.length - IV_GCM_LENGTH);
            decryptCipherAES_GCM.init(Cipher.DECRYPT_MODE, skGCM, parameterSpecGCM);
            // try AES/GCM first
            password = new String(decryptCipherAES_GCM.doFinal(bytePassword), StandardCharsets.UTF_8);
        } catch (ArrayIndexOutOfBoundsException | IllegalBlockSizeException | NumberFormatException ce) {
            // buildBytesFromHexString failed, assume clear text
            password = encryptedPswd;
        } catch (Exception u) {
                throw ValidationException.errorDecryptingPasswordOldAlgorithm(u);
        }
        return password;
    }

    /**
     * Returns multitaskers for the ciphers. :-)
     */
    private static class Synergizer {

        private static SecretKey getAESGCMMultitasker() throws Exception {
            return new SecretKeySpec(HexFormat.of().parseHex("64EF2D9B738ACA254A48F14754030FC2"), "AES");
        }

        private static byte[] getIvGCM() {
            byte[] ivGCM = new byte[IV_GCM_LENGTH];
            SecureRandom random = null;
            String useStrongRNG = PrivilegedAccessHelper.getSystemProperty(SystemProperties.SECURITY_ENCRYPTOR_USE_STRONG_RANDOM_NUMBER_GENERATOR);
            if (useStrongRNG == null || useStrongRNG.equalsIgnoreCase("false")) {
                random = new SecureRandom();
            } else if (useStrongRNG.equalsIgnoreCase("true")) {
                try {
                    random = SecureRandom.getInstanceStrong();
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw ValidationException.invalidBooleanValueForProperty(useStrongRNG, SystemProperties.SECURITY_ENCRYPTOR_USE_STRONG_RANDOM_NUMBER_GENERATOR);
            }
            random.nextBytes(ivGCM);
            return ivGCM;
        }
    }
}
