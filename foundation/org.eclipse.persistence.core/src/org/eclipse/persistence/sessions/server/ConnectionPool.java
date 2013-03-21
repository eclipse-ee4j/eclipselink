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

import java.util.*;

import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p>
 * <b>Purpose</b>: Used to specify how connection should be pooled in a server session.
 * @see ServerSession
 */
public class ConnectionPool {
    protected static final String MONITOR_HEADER = "Info:ConnectionPool:";
    
    protected boolean isConnected;
    protected int maxNumberOfConnections;
    protected int minNumberOfConnections;
    protected int initialNumberOfConnections;
    protected int waitTimeout;
    protected List<Accessor> connectionsAvailable;
    protected List<Accessor> connectionsUsed;
    protected Login login;
    protected String name;
    protected ServerSession owner;
    protected volatile boolean checkConnections;
    protected volatile long timeOfDeath;
    protected volatile long deadCheckTime;
    protected volatile boolean isDead;
    protected List<String> failoverConnectionPools;

    public static final long DEAD_CHECK_TIME = 1000 * 60 * 10; // 10 minutes.
    public static final int MAX_CONNECTIONS = 32;
    public static final int MIN_CONNECTIONS = 32;
    public static final int INITIAL_CONNECTIONS = 1;
    public static final int WAIT_TIMEOUT = 180000; // 3 minutes.

    /**
     * PUBLIC:
     * A connection pool is used to specify how connection should be pooled in a server session.
     */
    public ConnectionPool() {
        this(null, null, null);
    }

    /**
     * PUBLIC:
     * A connection pool is used to specify how connection should be pooled in a server session.
     */
    public ConnectionPool(String name, Login login, ServerSession owner) {
        this(name, login, INITIAL_CONNECTIONS, MIN_CONNECTIONS, MAX_CONNECTIONS, owner);
    }

    /**
     * PUBLIC:
     * A connection pool is used to specify how connection should be pooled in a server session.
     */
    public ConnectionPool(String name, Login login, int minNumberOfConnections, int maxNumberOfConnections, ServerSession owner) {
        this(name, login, Math.min(INITIAL_CONNECTIONS, minNumberOfConnections), minNumberOfConnections, maxNumberOfConnections, owner);
    }

    /**
     * PUBLIC:
     * A connection pool is used to specify how connection should be pooled in a server session.
     */
    public ConnectionPool(String name, Login login, int initialNumberOfConnections, int minNumberOfConnections, int maxNumberOfConnections, ServerSession owner) {
        this.login = login;
        this.owner = owner;
        this.name = name;
        this.maxNumberOfConnections = maxNumberOfConnections;
        this.minNumberOfConnections = minNumberOfConnections;
        this.initialNumberOfConnections = initialNumberOfConnections;
        this.deadCheckTime = DEAD_CHECK_TIME;
        this.waitTimeout = WAIT_TIMEOUT;
        this.checkConnections = false;
        this.failoverConnectionPools = new ArrayList<String>();
        resetConnections();
    }
    
    /**
     * INTERNAL:
     * The connection pool is dead fail over to the fail-over pool.
     */
    public Accessor failover() {
        if ((this.timeOfDeath + this.deadCheckTime) < System.currentTimeMillis()) {
            // Retry database to see if it is back up.
            this.isDead = false;
            return acquireConnection();
        } else {
            for (String poolName : this.failoverConnectionPools) {
                ConnectionPool pool = this.owner.getConnectionPool(poolName);
                if (!pool.isDead()) {
                    if (this.owner.shouldLog(SessionLog.FINEST, SessionLog.CONNECTION)) {
                        Object[] args = new Object[2];
                        args[0] = this.name;
                        args[1] = poolName;
                        this.owner.log(SessionLog.FINEST, SessionLog.CONNECTION, "failover", args);
                    }
                    return pool.acquireConnection();
                }
            }
            throw QueryException.failoverFailed(this.name);
        }        
    }
    
    /**
     * INTERNAL:
     * Wait until a connection is available and allocate the connection for the client.
     */
    public synchronized Accessor acquireConnection() throws ConcurrencyException {
        // Check for dead database and fail-over.
        if (this.isDead) {
            return failover();
        }
        // PERF: Using direct variable access to minimize concurrency bottleneck.
        while (this.connectionsAvailable.isEmpty()) {
            if ((this.connectionsUsed.size() + this.connectionsAvailable.size()) < this.maxNumberOfConnections) {
                Accessor connection = null;
                try {
                    connection = buildConnection();
                } catch (RuntimeException failed) {
                    if (!this.failoverConnectionPools.isEmpty()) {
                        this.isDead = true;
                        this.timeOfDeath = System.currentTimeMillis();
                        this.owner.logThrowable(SessionLog.WARNING, SessionLog.SQL, failed);
                        return acquireConnection();
                    } else {
                        throw failed;
                    }
                }
                this.connectionsUsed.add(connection);
                if (this.owner.isInProfile()) {
                    this.owner.updateProfile(MONITOR_HEADER + this.name, Integer.valueOf(this.connectionsUsed.size()));
                }
                if (this.owner.shouldLog(SessionLog.FINEST, SessionLog.CONNECTION)) {
                    Object[] args = new Object[1];
                    args[0] = this.name;
                    this.owner.log(SessionLog.FINEST, SessionLog.CONNECTION, "acquire_connection", args, connection);
                }
                return connection;
            }
            try {
                wait(this.waitTimeout);// Notify is called when connections are released.
            } catch (InterruptedException exception) {
                throw ConcurrencyException.waitFailureOnClientSession(exception);
            }
        }

        int connectionSize = this.connectionsAvailable.size();
        // Always used the last connection to avoid shift list and to use "hot" connection.
        Accessor connection = this.connectionsAvailable.remove(connectionSize-1);
        if (this.checkConnections) {
            // EclipseLink has encountered a problem with a connection where the database no longer responded
            // We need to now ensure that the failure was specific to that connection or we need to empty
            // the pool of dead connections in the case of a database failover.
            while (connectionSize >= 0) {
                if (this.owner.getLogin().isConnectionHealthValidatedOnError() && this.owner.getServerPlatform().wasFailureCommunicationBased(null, connection, this.owner)) {
                    try {
                        //connection failed connect test
                        connection.closeConnection();
                    } catch (Exception ex){
                        //ignore
                    } finally {
                        connection.releaseCustomizer();
                    }
                    if (this.connectionsAvailable.isEmpty()) {
                        this.checkConnections = false;
                        //we have emptied out all connections so let's have the connection pool build more
                        return acquireConnection();
                    } else {
                        //test next connection
                        --connectionSize;
                        connection = this.connectionsAvailable.remove(connectionSize-1);
                    }
                } else {
                    //connection was good use it.  And make sure we stop testing connections
                    this.checkConnections = false;
                    break;
                }
            }
        }
        this.connectionsUsed.add(connection);
        if (this.owner.isInProfile()) {
            this.owner.updateProfile(MONITOR_HEADER + this.name, Integer.valueOf(this.connectionsUsed.size()));
        }
        if (this.owner.shouldLog(SessionLog.FINEST, SessionLog.CONNECTION)) {
            Object[] args = new Object[1];
            args[0] = this.name;
            this.owner.log(SessionLog.FINEST, SessionLog.CONNECTION, "acquire_connection", args, connection);
        }
        return connection;
    }

    /**
     * INTERNAL:
     * Create a new connection, accessors are used as connections.
     */
    protected Accessor buildConnection() {
        Accessor connection = this.login.buildAccessor();
        connection.setPool(this);
        connection.connect(this.login, this.owner);

        return connection;
    }

    /**
     * INTERNAL:
     * returns the connections currently available for use in the pool
     */
    public List<Accessor> getConnectionsAvailable() {
        return connectionsAvailable;
    }

    /**
     *  Return a list of the connections that are being used.
     *  @return java.util.Vector
     **/
    protected List<Accessor> getConnectionsUsed() {
        return connectionsUsed;
    }

    /**
     * PUBLIC:
     * Return the login used to create connections.
     */
    public Login getLogin() {
        return login;
    }
    
    /**
     * PUBLIC:
     * Return the maximum number of connections allowed.
     * If all connections are in use, a new connection will be created until the maximum size is reach.
     * Only the minimum number of connections will be pooled, if the pool is between the min and max size
     * the connection will be disconnected when returned to the pool.
     * Typically it is desirable to have the min and max connections the same to avoid connects and disconnects.
     * When the max is reached clients must wait for a connection to become available.
     */
    public int getMaxNumberOfConnections() {
        return maxNumberOfConnections;
    }

    /**
     * PUBLIC:
     * Return the minimum number of connections.
     * If all connections are in use, a new connection will be created until the maximum size is reach.
     * Only the minimum number of connections will be pooled, if the pool is between the min and max size
     * the connection will be disconnected when returned to the pool.
     * Typically it is desirable to have the min and max connections the same to avoid connects and disconnects.
     */
    public int getMinNumberOfConnections() {
        return minNumberOfConnections;
    }

    /**
     * PUBLIC:
     * Return the name of this pool.
     * Pools are identified by name to allow multiple connection pools.
     */
    public String getName() {
        return name;
    }

    /**
     *  Return the ServerSession that is the owner of this connection pool.
     *  @return org.eclipse.persistence.sessions.server.ServerSession
     */
    protected ServerSession getOwner() {
        return owner;
    }

    /**
     * INTERNAL:
     * Return the total number of connections currently in use.
     */
    public int getTotalNumberOfConnections() {
        return getConnectionsUsed().size() + getConnectionsAvailable().size();
    }

    /**
     * INTERNAL:
     * Wait until a connection is avaiable and allocate the connection for the client.
     */
    public boolean hasConnectionAvailable() {
        return !getConnectionsAvailable().isEmpty();
    }

    /**
     * INTERNAL:
     * Return if this pool has been connected to the database.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * INTERNAL:
     * Checks for a conflict between pool's type and pool's login
     */
    public boolean isThereConflictBetweenLoginAndType() {
        return getLogin().shouldUseExternalConnectionPooling();
    }

    /**
     * INTERNAL:
     * Add the connection as single that a new connection is available.
     */
    public synchronized void releaseConnection(Accessor connection) throws DatabaseException {
        if (this.owner.shouldLog(SessionLog.FINEST, SessionLog.CONNECTION)) {
            Object[] args = new Object[1];
            args[0] = this.name;
            this.owner.log(SessionLog.FINEST, SessionLog.CONNECTION, "release_connection", args, connection);
        }
        connection.reset();

        this.connectionsUsed.remove(connection);

        if (!connection.isValid()) {
            this.checkConnections = true;
            try {
                connection.disconnect(this.owner);
            } catch (DatabaseException ex) {
                //this is an invalid connection so expect an exception.
            }
        } else {
            if ((this.connectionsUsed.size() + this.connectionsAvailable.size()) < this.minNumberOfConnections) {
                this.connectionsAvailable.add(connection);
            } else {
                connection.disconnect(getOwner());
            }
        }
        if (this.owner.isInProfile()) {
            this.owner.updateProfile(MONITOR_HEADER + this.name, Integer.valueOf(this.connectionsUsed.size()));
        }
        notify();
    }

    /**
     * INTERNAL:
     * Reset the connections on shutDown and when the pool is started.
     */
    public void resetConnections() {
        this.connectionsUsed = new Vector();
        this.connectionsAvailable = new Vector();
        this.checkConnections = false;
        this.isDead = false;
        this.timeOfDeath = 0;
    }

    /**
     * INTERNAL:
     * This method is called to indicate that all available connections should be checked.
     */
    public void setCheckConnections() {
        this.checkConnections = true; 
    }
    
    /**
     *  INTERNAL:
     *  Set this list of connections available
     *  @param java.util.Vector
     */
    protected void setConnectionsAvailable(Vector connectionsAvailable) {
        this.connectionsAvailable = connectionsAvailable;
    }

    /**
     *  INTERNAL:
     *  Set the list of connections being used.
     *  @param java.util.Vector
     */
    protected void setConnectionsUsed(Vector connectionsUsed) {
        this.connectionsUsed = connectionsUsed;
    }

    /**
     * INTERNAL:
     * Set if this pool has been connected to the database.
     */
    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    /**
     * PUBLIC:
     * Set the login used to create connections.
     */
    public void setLogin(Login login) {
        this.login = login;
    }

    /**
     * PUBLIC:
     * Return the initial number of connections allowed.
     * This is the number of connections connected on startup.
     */
    public int getInitialNumberOfConnections() {
        return initialNumberOfConnections;
    }
    
    /**
     * PUBLIC:
     * Set the initial number of connections allowed.
     * This is the number of connections connected on startup.
     * The default is 1.
     */
    public void setInitialNumberOfConnections(int initialNumberOfConnections) {
        this.initialNumberOfConnections = initialNumberOfConnections;
    }

    /**
     * PUBLIC:
     * Set the maximum number of connections allowed.
     * If all connections are in use, a new connection will be created until the maximum size is reach.
     * Only the minimum number of connections will be pooled, if the pool is between the min and max size
     * the connection will be disconnected when returned to the pool.
     * Typically it is desirable to have the min and max connections the same to avoid connects and disconnects.
     * When the max is reached clients must wait for a connection to become available.
     * The default is 32.
     */
    public void setMaxNumberOfConnections(int maxNumberOfConnections) {
        this.maxNumberOfConnections = maxNumberOfConnections;
    }

    /**
     * PUBLIC:
     * Set the minimum number of connections.
     * If all connections are in use, a new connection will be created until the maximum size is reach.
     * Only the minimum number of connections will be pooled, if the pool is between the min and max size
     * the connection will be disconnected when returned to the pool.
     * Typically it is desirable to have the min and max connections the same to avoid connects and disconnects.
     * The default is 32.
     */
    public void setMinNumberOfConnections(int minNumberOfConnections) {
        this.minNumberOfConnections = minNumberOfConnections;
    }

    /**
     * PUBLIC:
     * Set the name of this pool.
     * Pools are identified by name to allow multiple connection pools.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *  Set the ServerSession that owns this connection pool
     *  @param org.eclipse.persistence.sessions.server.ServerSession
     */
    protected void setOwner(ServerSession owner) {
        this.owner = owner;
    }

    /**
     * INTERNAL:
     * Disconnect all connections.
     */
    public synchronized void shutDown() {
        setIsConnected(false);

        for (Iterator iterator = getConnectionsAvailable().iterator(); iterator.hasNext();) {
            try {
                ((Accessor)iterator.next()).disconnect(getOwner());
            } catch (DatabaseException exception) {
                // Ignore.
            }
        }

        for (Iterator iterator = getConnectionsUsed().iterator(); iterator.hasNext();) {
            try {
                ((Accessor)iterator.next()).disconnect(getOwner());
            } catch (DatabaseException exception) {
                // Ignore.
            }
        }
        resetConnections();
    }

    /**
     * INTERNAL:
     * Allocate the minimum connections.
     */
    public synchronized void startUp() {
        if (isConnected()) {
            return;
        }
        for (int index = getInitialNumberOfConnections(); index > 0; index--) {
            getConnectionsAvailable().add(buildConnection());
        }

        setIsConnected(true);
    }

    /**
     * INTERNAL:
     * return a string representation of this connection pool
     */
    public String toString() {
        Object[] args = { Integer.valueOf(getMinNumberOfConnections()), Integer.valueOf(getMaxNumberOfConnections()) };
        return Helper.getShortClassName(getClass()) + ToStringLocalization.buildMessage("min_max", args);
    }
    
    /**
     * PUBLIC:
     * Return the time in milliseconds to wait for a available connection.
     * If the wait time is exceeded and exception will occur.
     * The default is 180000 or 3 minutes.
     * A value of 0 means wait forever.
     */
    public int getWaitTimeout() {
        return waitTimeout;
    }
    
    /**
     * PUBLIC:
     * Set the time in milliseconds to wait for an available connection.
     * If the wait time is exceeded an exception will occur.
     * The default is 180000 or 3 minutes.
     * A value of 0 means wait forever.
     */
    public void setWaitTimeout(int waitTimeout) {
        this.waitTimeout = waitTimeout;
    }
    
    /**
     * ADVANCED:
     * Return if the connection pool's database is down, and failover should be used.
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * ADVANCED:
     * Set if the connection pool's database is down, and failover should be used.
     */
    public void setIsDead(boolean isDead) {
        this.isDead = isDead;
    }

    /**
     * PUBLIC:
     * Return the list of connection pools to used if this pool database goes down.
     * The failover pools should be a clustered, replicated or backuped database.
     */
    public List<String> getFailoverConnectionPools() {
        return failoverConnectionPools;
    }

    /**
     * PUBLIC:
     * Set the list of connection pools to used if this pool database goes down.
     * The failover pools should be a clustered, replicated or backuped database.
     */
    public void setFailoverConnectionPools(List<String> failoverConnectionPools) {
        this.failoverConnectionPools = failoverConnectionPools;
    }

    /**
     * PUBLIC:
     * Add the connection pool to used if this pool database goes down.
     * The failover pools should be a clustered, replicated or backuped database.
     */
    public boolean addFailoverConnectionPool(String poolName) {
        return this.failoverConnectionPools.add(poolName);
    }
}
