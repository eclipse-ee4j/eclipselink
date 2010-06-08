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
package org.eclipse.persistence.internal.sessions.factories.model.login;

import java.util.Vector;
import org.eclipse.persistence.internal.security.SecurableObjectHolder;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.SequencingConfig;

/**
 * INTERNAL:
 */
public abstract class LoginConfig {
    private String m_platformClass;
    private String m_username;
    private char[] m_encryptedPassword;
    private SecurableObjectHolder m_securableObjectHolder;
    private String m_tableQualifier;
    private String m_encryptionClass;
    private boolean m_externalConnectionPooling;
    private boolean m_externalTransactionController;
    private SequencingConfig m_sequencingConfig;
    private Vector m_propertyConfigs;

    public LoginConfig() {
        // Without setting the encryption class name the object holder will 
        // default to JCE.
        m_securableObjectHolder = new SecurableObjectHolder();
    }

    public void setPlatformClass(String platformClass) {
        m_platformClass = platformClass;
    }

    public String getPlatformClass() {
        return m_platformClass;
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
        if(password == null){
            //Bug5965531, add null value support and blank string should be encrypted.
            m_encryptedPassword = null;
        }else if(password.length()==0){
            m_encryptedPassword=new char[0];
        }else{
            // Bug 4117441 - Secure programming practices, store password in char[]
            String encryptedPassword=m_securableObjectHolder.getSecurableObject().encryptPassword(password);
            m_encryptedPassword = encryptedPassword.toCharArray();
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
             // respect explicit de-referencing of password
            m_encryptedPassword = null;
        }
    }

    /**
     * We must decrypt the password. Once the password is decrypted, store it
     * again encrypted. This will cover the case that the password was not
     * actually encrypted when we read it in. On read in we always assume it
     * is encrypted so only setEncryptedPassword() is ever called.
     *
     * @return decryptedPassword
     */
    public String getPassword() {
        String decryptedPassword = null;
        
        if (m_encryptedPassword != null) {
            String passwordString = new String(m_encryptedPassword);
            decryptedPassword = m_securableObjectHolder.getSecurableObject().decryptPassword(passwordString);
            
            if (decryptedPassword==null || decryptedPassword.equals(passwordString)) {
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
        // Bug 4117441 - Secure programming practices, store password in char[]
        if (m_encryptedPassword != null) {
            return new String(m_encryptedPassword);
        } else {
            // respect explicit de-referencing of password
            return null;
        }
    }

    public void setTableQualifier(String tableQualifier) {
        m_tableQualifier = tableQualifier;
    }

    public String getTableQualifier() {
        return m_tableQualifier;
    }

    public void setExternalConnectionPooling(boolean externalConnectionPooling) {
        m_externalConnectionPooling = externalConnectionPooling;
    }

    public boolean getExternalConnectionPooling() {
        return m_externalConnectionPooling;
    }

    public void setExternalTransactionController(boolean externalTransactionController) {
        m_externalTransactionController = externalTransactionController;
    }

    public boolean getExternalTransactionController() {
        return m_externalTransactionController;
    }

    public void setSequencingConfig(SequencingConfig sequencingConfig) {
        m_sequencingConfig = sequencingConfig;
    }

    public SequencingConfig getSequencingConfig() {
        return m_sequencingConfig;
    }

    public void setPropertyConfigs(Vector propertyConfigs) {
        m_propertyConfigs = propertyConfigs;
    }

    public Vector getPropertyConfigs() {
        return m_propertyConfigs;
    }
}
