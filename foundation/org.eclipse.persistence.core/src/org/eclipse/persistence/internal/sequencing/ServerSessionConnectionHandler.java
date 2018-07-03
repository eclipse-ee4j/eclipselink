/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

    public ConnectionPool getPool() {
        return pool;
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
