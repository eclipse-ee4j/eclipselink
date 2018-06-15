/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sessions.factories.model.log;


/**
 * INTERNAL:
 */
public class DefaultSessionLogConfig extends LogConfig {
    private String m_logLevel;
    private String m_filename;

    public DefaultSessionLogConfig() {
        super();
    }

    public void setLogLevel(String logLevel) {
        m_logLevel = logLevel;
    }

    public String getLogLevel() {
        return m_logLevel;
    }

    public void setFilename(String filename) {
        m_filename = filename;
    }

    public String getFilename() {
        return m_filename;
    }
}
