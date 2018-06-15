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
package org.eclipse.persistence.internal.sessions.factories.model.platform;


/**
 * INTERNAL:
 */
public abstract class ServerPlatformConfig {
    private boolean m_enableRuntimeServices;
    private boolean m_enableJTA;
    private String m_serverClassName;
    protected boolean isSupported;

    public ServerPlatformConfig() {
        isSupported = true;
    }

    public ServerPlatformConfig(String serverClassName) {
        this();
        m_serverClassName = serverClassName;
    }

    public boolean getEnableJTA() {
        return m_enableJTA;
    }

    public boolean getEnableRuntimeServices() {
        return m_enableRuntimeServices;
    }

    public String getServerClassName() {
        return m_serverClassName;
    }

    public void setEnableRuntimeServices(boolean enableRuntimeServices) {
        m_enableRuntimeServices = enableRuntimeServices;
    }

    public void setEnableJTA(boolean enableJTA) {
        m_enableJTA = enableJTA;
    }

    public boolean isSupported() {
        return isSupported;
    }
}
