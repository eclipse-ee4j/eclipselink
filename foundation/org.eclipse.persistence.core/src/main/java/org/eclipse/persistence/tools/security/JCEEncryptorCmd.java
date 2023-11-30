/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.tools.security;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.internal.security.JCEEncryptor;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public final class JCEEncryptorCmd {

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    JCEEncryptor jceEncryptor;

    // Legacy DES ECB cipher used for backwards compatibility decryption only.
    private static final String DES_ECB = "DES/ECB/PKCS5Padding";
    private final Cipher decryptCipherDES_ECB;

    // Legacy AES ECB cipher used for backwards compatibility decryption only.
    private static final String AES_ECB = "AES/ECB/PKCS5Padding";
    private final Cipher decryptCipherAES_ECB;

    // Legacy AES CBC cipher used for backwards compatibility decryption only.
    private static final String AES_CBC = "AES/CBC/PKCS5Padding";
    private final Cipher decryptCipherAES_CBC;

    public JCEEncryptorCmd() throws Exception {
        decryptCipherDES_ECB = Cipher.getInstance(DES_ECB);
        decryptCipherDES_ECB.init(Cipher.DECRYPT_MODE, JCEEncryptorCmd.Synergizer.getDESMultitasker());

        decryptCipherAES_ECB = Cipher.getInstance(AES_ECB);
        decryptCipherAES_ECB.init(Cipher.DECRYPT_MODE, JCEEncryptorCmd.Synergizer.getAESMultitasker());

        SecretKey sk = JCEEncryptorCmd.Synergizer.getAESCBCMultitasker();
        IvParameterSpec iv = JCEEncryptorCmd.Synergizer.getIvSpec();
        decryptCipherAES_CBC = Cipher.getInstance(AES_CBC);
        decryptCipherAES_CBC.init(Cipher.DECRYPT_MODE, sk, iv);
    }

    public static void main(String[] args) throws Exception {
        JCEEncryptorCmd encryptorCmd = new JCEEncryptorCmd();
        encryptorCmd.start(args);
    }

    public void start(String[] args) throws Exception {
        if (args.length < 2 || !args[0].equals("-ip")) {
            System.out.println(LoggingLocalization.buildMessage("encryptor_script_usage", null) +
                    "\n" + LoggingLocalization.buildMessage("encryptor_script_description", null));
        } else {
            System.out.println(LoggingLocalization.buildMessage("encryptor_script_output", new Object[]{jceEncryptor.encryptPassword(decryptPassword(args[1]))}));
        }
    }

    public String decryptPassword(String encryptedPswd) throws Exception {
        String password = null;
        byte[] bytePassword = null;
        // try default AES/GCM algorithm first
        try {
            jceEncryptor = new JCEEncryptor();
            password = jceEncryptor.decryptPassword(encryptedPswd);
        }  catch (Exception u) {
            try {
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
            byte[] b = new byte[]{
                    (byte) -26, (byte) 124, (byte) -99, (byte) 32,
                    (byte) -37, (byte) -58, (byte) -93, (byte) 100,
                    (byte) 126, (byte) -55, (byte) -21, (byte) 48,
                    (byte) -86, (byte) 97, (byte) 12, (byte) 113};
            return new IvParameterSpec(b);
        }
    }
}
