/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
import javax.crypto.spec.SecretKeySpec;
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
    // Legacy decrypt cipher used for backwards compatibility only.
    private static final String DES = "DES/ECB/PKCS5Padding";
    private Cipher decryptCipherDES;
    
    // All encryption is done through the AES cipher.
    private static final String AES = "AES/ECB/PKCS5Padding"; 
    private Cipher encryptCipherAES;
    private Cipher decryptCipherAES;

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
        
        encryptCipherAES = Cipher.getInstance(AES);
        encryptCipherAES.init(Cipher.ENCRYPT_MODE, Synergizer.getAESMultitasker());
        
        decryptCipherAES = Cipher.getInstance(AES);
        decryptCipherAES.init(Cipher.DECRYPT_MODE, Synergizer.getAESMultitasker());
    }

    /**
     * Encrypts a string. Will throw a validation exception.
     */
    public synchronized String encryptPassword(String password) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CipherOutputStream cos = new CipherOutputStream(baos, encryptCipherAES);
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
    public synchronized String decryptPassword(String encryptedPswd) {
        String password = null;
        
        if (encryptedPswd != null) {
            ObjectInputStream ois = null;
            
            try {
                byte[] bytePassword = Helper.buildBytesFromHexString(encryptedPswd);
                
                ByteArrayInputStream bais = new ByteArrayInputStream(bytePassword);
                CipherInputStream cis = new CipherInputStream(bais, decryptCipherAES);
                ois = new ObjectInputStream(cis);
    
                password = (String)ois.readObject();
            } catch (Exception ex) {
                // Catch all exceptions when trying to decrypt using AES and try the
                // old DES decryptor before deciding what to do.
                try {
                    byte[] bytePassword = Helper.buildBytesFromHexString(encryptedPswd);
        
                    ByteArrayInputStream bais = new ByteArrayInputStream(bytePassword);
                    CipherInputStream cis = new CipherInputStream(bais, decryptCipherDES);
                    ois = new ObjectInputStream(cis);
        
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
            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException ioexception) {
                    // swallow it
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
    }
}
