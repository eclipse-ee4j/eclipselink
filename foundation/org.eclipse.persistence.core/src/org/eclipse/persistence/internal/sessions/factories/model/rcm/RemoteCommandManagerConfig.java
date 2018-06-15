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
package org.eclipse.persistence.internal.sessions.factories.model.rcm;

import org.eclipse.persistence.internal.sessions.factories.model.transport.*;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.command.*;

/**
 * INTERNAL:
 */
public class RemoteCommandManagerConfig {
    private String m_channel;
    private CommandsConfig m_commandsConfig;
    private TransportManagerConfig m_transportManager;

    public RemoteCommandManagerConfig() {
    }

    public void setChannel(String channel) {
        m_channel = channel;
    }

    public String getChannel() {
        return m_channel;
    }

    public void setCommandsConfig(CommandsConfig commandsConfig) {
        m_commandsConfig = commandsConfig;
    }

    public CommandsConfig getCommandsConfig() {
        return m_commandsConfig;
    }

    public void setTransportManagerConfig(TransportManagerConfig transportManager) {
        m_transportManager = transportManager;
    }

    public TransportManagerConfig getTransportManagerConfig() {
        return m_transportManager;
    }
}
