/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

    @Override
    public void onConnect() {
        if (!isConnected()) {
            pool.startUp();
        }
    }

    @Override
    public boolean isConnected() {
        return pool.isConnected();
    }

    @Override
    public Accessor acquireAccessor() {
        return pool.acquireConnection();
    }

    public ConnectionPool getPool() {
        return pool;
    }

    @Override
    public void releaseAccessor(Accessor accessor) {
        pool.releaseConnection(accessor);
    }

    @Override
    public void onDisconnect() {
        if (isConnected()) {
            pool.shutDown();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        onDisconnect();
    }
}
