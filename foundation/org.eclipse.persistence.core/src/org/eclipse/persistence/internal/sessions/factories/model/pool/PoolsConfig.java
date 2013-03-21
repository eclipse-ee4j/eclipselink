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
package org.eclipse.persistence.internal.sessions.factories.model.pool;

import java.util.Vector;

/**
 * INTERNAL:
 */
public class PoolsConfig {
    private ReadConnectionPoolConfig m_readConnectionPoolConfig;
    private WriteConnectionPoolConfig m_writeConnectionPoolConfig;
    private ConnectionPoolConfig m_sequenceConnectionPoolConfig;
    private Vector m_connectionPoolConfigs;

    public PoolsConfig() {
        m_connectionPoolConfigs = new Vector();
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

    public void setConnectionPoolConfigs(Vector connectionPoolConfigs) {
        m_connectionPoolConfigs = connectionPoolConfigs;
    }

    public Vector getConnectionPoolConfigs() {
        return m_connectionPoolConfigs;
    }
}
