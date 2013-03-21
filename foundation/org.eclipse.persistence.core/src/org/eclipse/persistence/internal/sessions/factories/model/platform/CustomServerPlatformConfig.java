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
