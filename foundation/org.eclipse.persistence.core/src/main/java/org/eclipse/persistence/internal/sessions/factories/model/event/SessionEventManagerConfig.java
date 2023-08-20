/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sessions.factories.model.event;

import java.util.ArrayList;
import java.util.List;

/**
 * INTERNAL:
 */
public class SessionEventManagerConfig {
    private List<String> m_sessionEventListeners;

    public SessionEventManagerConfig() {
        m_sessionEventListeners = new ArrayList<>();
    }

    public void addSessionEventListener(String listener) {
        m_sessionEventListeners.add(listener);
    }

    public void setSessionEventListeners(List<String> sessionEventListeners) {
        m_sessionEventListeners = sessionEventListeners;
    }

    public List<String> getSessionEventListeners() {
        return m_sessionEventListeners;
    }
}
