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
package org.eclipse.persistence.internal.sessions.factories.model.pool;

import org.eclipse.persistence.internal.sessions.factories.model.login.LoginConfig;

/**
 * INTERNAL:
 */
public class ConnectionPoolConfig {
    protected String m_name;
    private Integer m_maxConnections;
    private Integer m_minConnections;
    private LoginConfig m_loginConfig;

    public ConnectionPoolConfig() {
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void setMaxConnections(Integer maxConnections) {
        m_maxConnections = maxConnections;
    }

    public Integer getMaxConnections() {
        return m_maxConnections;
    }

    public void setMinConnections(Integer minConnections) {
        m_minConnections = minConnections;
    }

    public Integer getMinConnections() {
        return m_minConnections;
    }

    public void setLoginConfig(LoginConfig loginConfig) {
        m_loginConfig = loginConfig;
    }

    public LoginConfig getLoginConfig() {
        return m_loginConfig;
    }
}
