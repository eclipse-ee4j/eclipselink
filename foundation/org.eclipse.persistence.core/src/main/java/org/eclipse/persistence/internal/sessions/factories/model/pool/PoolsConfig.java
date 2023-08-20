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
package org.eclipse.persistence.internal.sessions.factories.model.pool;

import java.util.ArrayList;
import java.util.List;

/**
 * INTERNAL:
 */
public class PoolsConfig {
    private ReadConnectionPoolConfig m_readConnectionPoolConfig;
    private WriteConnectionPoolConfig m_writeConnectionPoolConfig;
    private ConnectionPoolConfig m_sequenceConnectionPoolConfig;
    private List<ConnectionPoolConfig> m_connectionPoolConfigs;

    public PoolsConfig() {
        m_connectionPoolConfigs = new ArrayList<>();
    }

    public void setReadConnectionPoolConfig(ReadConnectionPoolConfig readConnectionPoolConfig) {
        m_readConnectionPoolConfig = readConnectionPoolConfig;
    }

    public ReadConnectionPoolConfig getReadConnectionPoolConfig() {
        return m_readConnectionPoolConfig;
    }

    public void setWriteConnectionPoolConfig(WriteConnectionPoolConfig writeConnectionPoolConfig) {
        m_writeConnectionPoolConfig = writeConnectionPoolConfig;
    }

    public WriteConnectionPoolConfig getWriteConnectionPoolConfig() {
        return m_writeConnectionPoolConfig;
    }

    public void setSequenceConnectionPoolConfig(ConnectionPoolConfig sequenceConnectionPoolConfig) {
        m_sequenceConnectionPoolConfig = sequenceConnectionPoolConfig;
    }

    public ConnectionPoolConfig getSequenceConnectionPoolConfig() {
        return m_sequenceConnectionPoolConfig;
    }

    public void addConnectionPoolConfig(ConnectionPoolConfig connectionPoolConfig) {
        m_connectionPoolConfigs.add(connectionPoolConfig);
    }

    public void setConnectionPoolConfigs(List<ConnectionPoolConfig> connectionPoolConfigs) {
        m_connectionPoolConfigs = connectionPoolConfigs;
    }

    public List<ConnectionPoolConfig> getConnectionPoolConfigs() {
        return m_connectionPoolConfigs;
    }
}
