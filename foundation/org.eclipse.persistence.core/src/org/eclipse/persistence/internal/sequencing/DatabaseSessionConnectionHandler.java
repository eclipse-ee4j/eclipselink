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
package org.eclipse.persistence.internal.sequencing;

import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.exceptions.ConcurrencyException;

class DatabaseSessionConnectionHandler implements SequencingConnectionHandler {
    DatabaseSessionConnectionHandler(DatabaseSessionImpl ownerSession, Login login) {
        this.ownerSession = ownerSession;
        this.login = login;
        accessor = login.buildAccessor();
    }

    DatabaseSessionImpl ownerSession;
    Login login;
    Accessor accessor;
    boolean isBusy;

    @Override
    public void onConnect() {
        if (!isConnected()) {
            accessor.connect(login, ownerSession);
        }
    }

    @Override
    public boolean isConnected() {
        return accessor.isConnected();
    }

    @Override
    public synchronized Accessor acquireAccessor() {
        if (isBusy) {
            try {
                wait();// Notify is called when connection is released.
            } catch (InterruptedException exception) {
                throw ConcurrencyException.waitFailureOnSequencingForDatabaseSession(exception);
            }
        }
        isBusy = true;
        return accessor;
    }

    @Override
    public synchronized void releaseAccessor(Accessor accessor) {
        isBusy = false;
        notify();
    }

    @Override
    public void onDisconnect() {
        if (isConnected()) {
            accessor.disconnect(ownerSession);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        onDisconnect();
    }
}
