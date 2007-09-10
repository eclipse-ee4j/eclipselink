/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sequencing;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.sessions.server.ConnectionPool;

class ServerSessionConnectionHandler implements SequencingConnectionHandler {
    ServerSessionConnectionHandler(ConnectionPool pool) {
        this.pool = pool;
    }

    ConnectionPool pool;

    public void onConnect() {
        if (!isConnected()) {
            pool.startUp();
        }
    }

    public boolean isConnected() {
        return pool.isConnected();
    }

    public Accessor acquireAccessor() {
        return pool.acquireConnection();
    }

    public void releaseAccessor(Accessor accessor) {
        pool.releaseConnection(accessor);
    }

    public void onDisconnect() {
        if (isConnected()) {
            pool.shutDown();
        }
    }

    protected void finalize() throws Throwable {
        onDisconnect();
    }
}