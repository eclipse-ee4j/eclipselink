/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.exceptions.ConversionException;

/**
 * TopLink reference implementation for password encryption.
 *
 * @author Guy Pelletier
 */
public class JCEEncryptor implements Securable {
    private Cipher encryptCipher;
    private Cipher decryptCipher;

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
        encryptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, Synergizer.getMultitasker("DES"));
        decryptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, Synergizer.getMultitasker("DES"));
    }

    /**
     * Encrypts a string. Will throw a validation exception.
     */
    public String encryptPassword(String password) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CipherOutputStream cos = new CipherOutputStream(baos, encryptCipher);
            ObjectOutputStream oos = new ObjectOutputStream(cos);

            oos.writeObject(password);
            oos.flush();
            oos.close();

            return Helper.buildHexStringFromBytes(baos.toByteArray());
        } catch (Exception e) {
            throw ValidationException.errorEncryptingPassword(e);
        }
    }

    /**
     * Decrypts a string. Will throw a validation exception.
     * Handles backwards compatibility for older encrypted strings.
     */
    public String decryptPassword(String encryptedPswd) {
        String password = "";

        try {
            byte[] bytePassword = Helper.buildBytesFromHexString(encryptedPswd);

            ByteArrayInputStream bais = new ByteArrayInputStream(bytePassword);
            CipherInputStream cis = new CipherInputStream(bais, decryptCipher);
            ObjectInputStream ois = new ObjectInputStream(cis);

            password = (String)ois.readObject();
            ois.close();
        } catch (IOException e) {
            // JCE 1.2.2 couldn't decrypt it, assume clear text
            password = encryptedPswd;
        } catch (ArrayIndexOutOfBoundsException e) {
            // JCE 1.2.1 couldn't decrypt it, assume clear text
            password = encryptedPswd;
        } catch (ConversionException e) {
            // Never prepared (buildBytesFromHexString failed), assume clear text
            password = encryptedPswd;
        } catch (Exception e) {
            throw ValidationException.errorDecryptingPassword(e);
        }

        return password;
    }

    private static class Synergizer {
        private static SecretKey getMultitasker(String algorithm) throws Exception {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
            return factory.generateSecret(new DESKeySpec(Helper.buildBytesFromHexString("E60B80C7AEC78038")));
        }
    }
}
