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
package org.eclipse.persistence.internal.sessions.factories.model.session;

import java.util.ArrayList;
import java.util.List;

/**
 * INTERNAL:
 */
public class SessionBrokerConfig extends SessionConfig {
    private List<String> m_sessionNames;

    public SessionBrokerConfig() {
        super();
        m_sessionNames = new ArrayList<>();
    }

    public void addSessionName(String sessionName) {
        m_sessionNames.add(sessionName);
    }

    public void setSessionNames(List<String> sessionNames) {
        m_sessionNames = sessionNames;
    }

    public List<String> getSessionNames() {
        return m_sessionNames;
    }
}
