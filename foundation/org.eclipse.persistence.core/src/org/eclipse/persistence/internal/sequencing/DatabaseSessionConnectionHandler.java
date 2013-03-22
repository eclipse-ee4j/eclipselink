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

    public void onConnect() {
        if (!isConnected()) {
            accessor.connect(login, ownerSession);
        }
    }

    public boolean isConnected() {
        return accessor.isConnected();
    }

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

    public synchronized void releaseAccessor(Accessor accessor) {
        isBusy = false;
        notify();
    }

    public void onDisconnect() {
        if (isConnected()) {
            accessor.disconnect(ownerSession);
        }
    }

    protected void finalize() throws Throwable {
        onDisconnect();
    }
}
