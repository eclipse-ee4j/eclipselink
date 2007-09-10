/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories.model;

import java.util.Vector;
import java.util.Enumeration;
import org.eclipse.persistence.internal.sessions.factories.model.session.SessionConfig;

/**
 * INTERNAL:
 */
public class SessionConfigs {
    private Vector m_sessionConfigs;
    private String m_version;

    public SessionConfigs() {
        m_sessionConfigs = new Vector();
    }

    public void addSessionConfig(SessionConfig sessionConfig) {
        m_sessionConfigs.add(sessionConfig);
    }

    public void setSessionConfigs(Vector sessionConfigs) {
        m_sessionConfigs = sessionConfigs;
    }

    public Vector getSessionConfigs() {
        return m_sessionConfigs;
    }

    public void setVersion(String version) {
        m_version = version;
    }

    public String getVersion() {
        return m_version;
    }
}