/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories.model.clustering;

/**
 * INTERNAL: 
 */
public class JNDIClusteringServiceConfig extends ClusteringServiceConfig {
    private char[] m_jndiPassword;
    private String m_jndiUsername;
    private String m_namingServiceInitialContextFactoryName;

    protected JNDIClusteringServiceConfig() {
        super();
    }

    public String getJNDIPassword() {
        // Bug 4117441 - Secure programming practices, store password in char[]
        if (m_jndiPassword != null) {
            return new String(m_jndiPassword);
        } else {
            return null;
        }
    }

    public String getJNDIUsername() {
        return m_jndiUsername;
    }

    public String getNamingServiceInitialContextFactoryName() {
        return m_namingServiceInitialContextFactoryName;
    }

    public void setJNDIPassword(String password) {
        // Bug 4117441 - Secure programming practices, store password in char[]
        if (password != null) {
            m_jndiPassword = password.toCharArray();
        } else {
            // must respect dereferencing of the password
            m_jndiPassword = null;
        }
    }

    public void setJNDIUsername(String username) {
        m_jndiUsername = username;
    }

    public void setNamingServiceInitialContextFactoryName(String namingServiceInitialContextFactoryName) {
        m_namingServiceInitialContextFactoryName = namingServiceInitialContextFactoryName;
    }
}