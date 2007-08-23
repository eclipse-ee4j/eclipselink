/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories.model.session;

import java.util.Vector;

/**
 * INTERNAL:
 */
public class SessionBrokerConfig extends SessionConfig {
    private Vector m_sessionNames;

    public SessionBrokerConfig() {
        super();
        m_sessionNames = new Vector();
    }

    public void addSessionName(String sessionName) {
        m_sessionNames.add(sessionName);
    }

    public void setSessionNames(Vector sessionNames) {
        m_sessionNames = sessionNames;
    }

    public Vector getSessionNames() {
        return m_sessionNames;
    }
}