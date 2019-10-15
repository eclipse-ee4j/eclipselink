/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.ConversionManager;

/**
 * Holder of a SecurableObject. Securable objects should not be held onto
 * directly, instead they should be accessed via this holder.
 *
 * @author Guy Pelletier
 */
public class SecurableObjectHolder {

    /** The JCE encryption class name */
    private final static String JCE_ENCRYPTION_CLASS_NAME = "org.eclipse.persistence.internal.security.JCEEncryptor";

    /** The encryption class name **/
    private String m_securableClassName;

    /** The actual encryption object **/
    private Securable m_securableObject;

    public SecurableObjectHolder() {
        this(null);
    }

    public SecurableObjectHolder(String securableClassName) {
        m_securableObject = null;
        m_securableClassName = securableClassName;
    }

    public String getEncryptionClassName() {
        return m_securableClassName;
    }

    public void setEncryptionClassName(String securableClassName) {
        m_securableClassName = securableClassName;
    }

    public Securable getSecurableObject() {
        if (m_securableObject == null) {
            initSecurableObject();
        }

        return m_securableObject;
    }

    public boolean hasSecurableObject() {
        return m_securableObject != null;
    }

    /**
     * Convert a String into a Securable object
     * Class name must be fully qualified, eg. org.eclipse.persistence.internal.security.JCEEncryptor
     * Default is the JCEEncryptor
     */
    private void initSecurableObject() {
        boolean initPassThroughEncryptor = false;

        if (m_securableClassName == null) {
            // Since we are defaulting, hence, assuming they can initialize the JCE
            // libraries, if the init fails, this flag tells us to assume no encryption.
            // However, if the JCE init does work, the JCEEncryptor will need to
            // determine that a password was not encrypted by it, therefore, assume
            // clear text. See JCEEncryptor.
            initPassThroughEncryptor = true;
            m_securableClassName = JCE_ENCRYPTION_CLASS_NAME;
        }

        try {
            ConversionManager cm = ConversionManager.getDefaultManager();
            Class securableClass = (Class)cm.convertObject(m_securableClassName, Class.class);
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    m_securableObject = (Securable)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(securableClass));
                } catch (PrivilegedActionException exception) {
                    throw exception.getException();
                }
            } else {
                m_securableObject = (Securable)PrivilegedAccessHelper.newInstanceFromClass(securableClass);
            }
        } catch (Throwable e) {
            if (initPassThroughEncryptor) {// default failed, so perform no encryption.
                m_securableObject = new PassThroughEncryptor();
            } else {
                throw ValidationException.invalidEncryptionClass(m_securableClassName, e);
            }
        }
    }

    // Made static final for performance reasons.
    /*
     * If we default to JCE and the initialization fails, our fall back is to do
     * no encryption. This covers the case where the user is running against JDK 1.3
     * At runtime, no encryption will be made and the passwords will be assummed to
     * be clear text.
     */
    private static final class PassThroughEncryptor implements Securable {
        @Override
        public String encryptPassword(String pswd) {
            return pswd;
        }

        @Override
        public String decryptPassword(String encryptedPswd) {
            return encryptedPswd;
        }
    }
}
