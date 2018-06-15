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
public class CustomServerPlatformConfig extends ServerPlatformConfig {
    private String m_serverClassName;
    private String m_externalTransactionControllerClass;

    public CustomServerPlatformConfig() {
        super();
    }

    @Override
    public String getServerClassName() {
        return m_serverClassName;
    }

    public String getExternalTransactionControllerClass() {
        return m_externalTransactionControllerClass;
    }

    public void setExternalTransactionControllerClass(String externalTransactionControllerClass) {
        m_externalTransactionControllerClass = externalTransactionControllerClass;
    }

    public void setServerClassName(String serverClassName) {
        m_serverClassName = serverClassName;
    }
}
