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
package org.eclipse.persistence.internal.sessions.factories.model.transport;

import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.*;
import org.eclipse.persistence.internal.sessions.factories.model.transport.discovery.*;

/**
 * INTERNAL:
 */
public class RMITransportManagerConfig extends TransportManagerConfig {
    private String m_sendMode;
    private DiscoveryConfig m_discoveryConfig;
    private JNDINamingServiceConfig m_jndiNamingServiceConfig;
    private RMIRegistryNamingServiceConfig m_rmiRegistryNamingServiceConfig;

    public RMITransportManagerConfig() {
        super();
    }

    public void setSendMode(String sendMode) {
        m_sendMode = sendMode;
    }

    public String getSendMode() {
        return m_sendMode;
    }

    public void setDiscoveryConfig(DiscoveryConfig discoveryConfig) {
        m_discoveryConfig = discoveryConfig;
    }

    public DiscoveryConfig getDiscoveryConfig() {
        return m_discoveryConfig;
    }

    public void setJNDINamingServiceConfig(JNDINamingServiceConfig jndiNamingServiceConfig) {
        m_jndiNamingServiceConfig = jndiNamingServiceConfig;
    }

    public JNDINamingServiceConfig getJNDINamingServiceConfig() {
        return m_jndiNamingServiceConfig;
    }

    public void setRMIRegistryNamingServiceConfig(RMIRegistryNamingServiceConfig rmiRegistryNamingServiceConfig) {
        m_rmiRegistryNamingServiceConfig = rmiRegistryNamingServiceConfig;
    }

    public RMIRegistryNamingServiceConfig getRMIRegistryNamingServiceConfig() {
        return m_rmiRegistryNamingServiceConfig;
    }
}
