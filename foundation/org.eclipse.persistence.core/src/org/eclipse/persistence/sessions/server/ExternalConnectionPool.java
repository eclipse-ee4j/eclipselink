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
package org.eclipse.persistence.sessions.server;

import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.exceptions.*;

/**
 * <p>
 * <b>Purpose</b>: This subclass is intended to be used with external connection pools.
 * For these pools, TopLink does not control the pooling behavior.
 * The login should have the usesExternalConnectionPooling set to "true".
 */
public class ExternalConnectionPool extends ConnectionPool {
    protected Accessor cachedConnection;

    /**
     * PUBLIC:
     * Build a new external connection pool.  The JDBC driver is responsible for pooling the connections.
     */
    public ExternalConnectionPool() {
        super();
    }

    /**
     * PUBLIC:
     * Build a new external connection pool.  The JDBC driver is responsible for pooling the connections.
     */
    public ExternalConnectionPool(String name, Login login, ServerSession owner) {
        super(name, login, 0, 0, 0, owner);
    }

    /**
     * INTERNAL:
     * When we acquire a connection from an ExternalConnectionPool we build
     * a new connection (retrieve it from the external pool).
     */
    public Accessor acquireConnection() throws ConcurrencyException {
        // Check for dead database and fail-over.
        if (this.isDead) {
            return failover();
        }
        Accessor connection = (Accessor)this.cachedConnection.clone();
        if (this.owner.shouldLog(SessionLog.FINEST, SessionLog.CONNECTION)) {
            Object[] args = new Object[1];
            args[0] = this.name;
            this.owner.log(SessionLog.FINEST, SessionLog.CONNECTION, "acquire_connection", args, connection);
        }
        return connection;
    }

    /**
     *  INTERNAL:
     *  Return the currently cached connection to the external connection pool
     *  @return org.eclipse.persistence.internal.databaseaccess.Accessor
     */
    protected Accessor getCachedConnection() {
        return cachedConnection;
    }

    /**
     * INTERNAL:
     * Assume true as the driver is responsible for blocking.
     */
    public boolean hasConnectionAvailable() {
        return true;
    }

    /**
     * INTERNAL:
     * Checks for a conflict between pool's type and pool's login
     */
    public boolean isThereConflictBetweenLoginAndType() {
        return !getLogin().shouldUseExternalConnectionPooling();
    }

    /**
     * INTERNAL:
     * When you release an external connection, you simply let it go.
     */
    public void releaseConnection(Accessor connection) throws DatabaseException {
        if (this.owner.shouldLog(SessionLog.FINEST, SessionLog.CONNECTION)) {
            Object[] args = new Object[1];
            args[0] = this.name;
            this.owner.log(SessionLog.FINEST, SessionLog.CONNECTION, "release_connection", args, connection);
        }
        if (!this.failoverConnectionPools.isEmpty()) {
            if (!connection.isValid()) {
                if (this.checkConnections) {
                    // If 2nd dead connection, then assume dead.
                    this.isDead = true;
                } else {
                    this.checkConnections = true;
                }
            } else {
                this.checkConnections = false;            
            }
        }
        connection.closeConnection();
        connection.releaseCustomizer();
    }

    /**
     *  Set the currently cached connection to the external connection pool.
     *  @param org.eclipse.persistence.internal.databaseaccess.Accessor
     */
    protected void setCachedConnection(Accessor cachedConnection) {
        this.cachedConnection = cachedConnection;
    }

    /**
     * INTERNAL:
     * This method is called to indicate that all available connections should be checked.
     * Noop on external connection pool.
     */
    public void setCheckConnections() {
    }
    
    /**
     * INTERNAL:
     * This mehtod is a no-op for external pools.
     */
    public synchronized void shutDown() {
        //do nothing
        setIsConnected(false);
    }

    /**
     * INTERNAL:
     * Build the default connection.
     * This validates that connect will work and sets up the parent accessor to clone.
     */
    public synchronized void startUp() {
        setCachedConnection(buildConnection());
        setIsConnected(true);
    }
}
