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
package org.eclipse.persistence.internal.sessions.factories.model.event;

import java.util.Vector;

/**
 * INTERNAL:
 */
public class SessionEventManagerConfig {
    private Vector m_sessionEventListeners;

    public SessionEventManagerConfig() {
        m_sessionEventListeners = new Vector();
    }

    public void addSessionEventListener(String listener) {
        m_sessionEventListeners.add(listener);
    }

    public void setSessionEventListeners(Vector sessionEventListeners) {
        m_sessionEventListeners = sessionEventListeners;
    }

    public Vector getSessionEventListeners() {
        return m_sessionEventListeners;
    }
}
