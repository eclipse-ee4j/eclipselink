/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sessions.factories.model.transport.naming;

import java.util.Vector;
import org.eclipse.persistence.internal.security.SecurableObjectHolder;

/**
 * INTERNAL:
 */
public class JNDINamingServiceConfig {
    private String m_url;
    private String m_username;
    private String m_encryptionClass;
    private char[] m_encryptedPassword;
    private SecurableObjectHolder m_securableObjectHolder;
    private String m_initialContextFactoryName;
    private Vector m_propertyConfigs;

    public JNDINamingServiceConfig() {
        // Without setting the encryption class name the object holder will 
        // default to JCE.
        m_securableObjectHolder = new SecurableObjectHolder();
    }

    public void setURL(String url) {
        m_url = url;
    }

    public String getURL() {
        return m_url;
    }

    public void setUsername(String username) {
        m_username = username;
    }

    public String getUsername() {
        return m_username;
    }

    public void setEncryptionClass(String encryptionClass) {
        m_encryptionClass = encryptionClass;
        m_securableObjectHolder.setEncryptionClassName(m_encryptionClass);
    }

    public String getEncryptionClass() {
        return m_encryptionClass;
    }

    /**
     * This method will always encrypt the password regardless. Should only
     * ever be called from the Mapping Workbench.
     * It can be called before setting an encryption class, therefore, the
     * securable object initialization remains in the constructor with default
     * to JCE and changes only if an encryption class is set.
     *
     * @param password
     */
    public void setPassword(String password) {
        // Bug 4117441 - Secure programming practices, store password in char[]
        String passwordString = m_securableObjectHolder.getSecurableObject().encryptPassword(password);
        if (passwordString != null) {
            m_encryptedPassword = passwordString.toCharArray();
        } else {
            // ensure explicit de-referencing of password
            m_encryptedPassword = null;
        }
    }

    /**
     * This method should never be called from the Mapping Workbench. Instead it
     * is called only at load time of a schema formatted sessions.xml file. It
     * assumes the password is encrypted.
     *
     * @param encryptedPassword
     */
    public void setEncryptedPassword(String encryptedPassword) {
        // Bug 4117441 - Secure programming practices, store password in char[]
        if (encryptedPassword != null) {
            m_encryptedPassword = encryptedPassword.toCharArray();
        } else {
            // ensure explicit de-referencing of password
            m_encryptedPassword = null;
        }
    }

    /**
     * We must decrypt the password. Once the password is decrypted, check to
     * see if it was 'actually' decrypted. If not, store it encrypted by calling
     * setPassword(). This will cover the case that the password was not
     * actually encrypted when we read it in. On read in we always assume it
     * is encrypted so only setEncryptedPassword() is ever called.
     *
     * @return decryptedPassword
     */
    public String getPassword() {
        String decryptedPassword = null;
        
        if (m_encryptedPassword != null) {
            // Bug 4117441 - Secure programming practices, unwrap password from char[]
            String passwordString = new String(m_encryptedPassword);
            decryptedPassword = m_securableObjectHolder.getSecurableObject().decryptPassword(passwordString);
            // compare against the String passwordString
            if (decryptedPassword.equals(passwordString)) {
                // Password was never encrypted so encrypt it.
                setPassword(decryptedPassword);
            }
        }

        return decryptedPassword;
    }

    /**
     * Assume the password has been encrypted and return it.
     *
     * @return encryptedPassword
     */
    public String getEncryptedPassword() {
        // Bug 4117441 - Secure programming practices, create password String from char[]
        if (m_encryptedPassword != null) {
            return new String(m_encryptedPassword);
        } else {
            // respect explicit de-referencing of password
            return null;
        }
    }

    public void setInitialContextFactoryName(String initialContextFactoryName) {
        m_initialContextFactoryName = initialContextFactoryName;
    }

    public String getInitialContextFactoryName() {
        return m_initialContextFactoryName;
    }

    public void setPropertyConfigs(Vector propertyConfigs) {
        m_propertyConfigs = propertyConfigs;
    }

    public Vector getPropertyConfigs() {
        return m_propertyConfigs;
    }
}
