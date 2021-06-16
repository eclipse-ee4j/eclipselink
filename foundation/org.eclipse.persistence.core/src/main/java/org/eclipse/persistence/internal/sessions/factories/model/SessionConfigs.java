/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sessions.factories.model;

import java.util.Vector;
import org.eclipse.persistence.internal.sessions.factories.model.session.SessionConfig;

/**
 * INTERNAL:
 */
public class SessionConfigs {
    private Vector<SessionConfig> m_sessionConfigs;
    private String m_version;

    public SessionConfigs() {
        m_sessionConfigs = new Vector<>();
    }

    public void addSessionConfig(SessionConfig sessionConfig) {
        m_sessionConfigs.add(sessionConfig);
    }

    public void setSessionConfigs(Vector<SessionConfig> sessionConfigs) {
        m_sessionConfigs = sessionConfigs;
    }

    public Vector<SessionConfig> getSessionConfigs() {
        return m_sessionConfigs;
    }

    public void setVersion(String version) {
        m_version = version;
    }

    public String getVersion() {
        return m_version;
    }
}
