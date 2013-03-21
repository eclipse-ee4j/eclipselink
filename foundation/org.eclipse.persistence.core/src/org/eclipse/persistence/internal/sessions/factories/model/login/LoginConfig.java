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
        m_securableObjectHolder.setEncryptionClassName(encryptionClass);
    }

    public String getEncryptionClass() {
        return m_securableObjectHolder.getEncryptionClassName();
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
        setEncryptedPassword(password);
    }

    /**
     * This method should never be called from the Mapping Workbench. Instead it
     * is called only at load time of a schema formatted sessions.xml file. It
     * assumes the password is encrypted. If it is not we will make sure it is
     * encrypted.
     *
     * @param encryptedPassword
     */
    public void setEncryptedPassword(String encryptedPassword) {
        // Bug 4117441 - Secure programming practices, store password in char[]
        if (encryptedPassword == null) {
            // respect explicit de-referencing of password
            m_encryptedPassword = null;
        } else if (encryptedPassword.length() == 0) {
            m_encryptedPassword = new char[0];
        } else {
            // If the decrypted password is the same as the encrypted one then 
            // it was not encrypted so make sure to store the encrypted password.
            if (encryptedPassword.equals(m_securableObjectHolder.getSecurableObject().decryptPassword(encryptedPassword))) {
                m_encryptedPassword = m_securableObjectHolder.getSecurableObject().encryptPassword(encryptedPassword).toCharArray();
            } else {
                m_encryptedPassword = encryptedPassword.toCharArray();
            }
        }
    }

    /**
     * This method will return the decrypted password. This method should
     * only be called by the Mapping Workbench.
     *
     * @return decryptedPassword
     */
    public String getPassword() {
        return m_securableObjectHolder.getSecurableObject().decryptPassword(getEncryptedPassword());
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
