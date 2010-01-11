/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

/**
 * <p>
 * <b>Purpose</b>: Used to specify how connection should be pooled in a server session.
 * @see ServerSession
 */
public class ConnectionPool {
    protected boolean isConnected;
    protected int maxNumberOfConnections;
    protected int minNumberOfConnections;
    protected Vector connectionsAvailable;
    protected Vector connectionsUsed;
    protected Login login;
    protected String name;
    protected ServerSession owner;
    protected boolean checkConnections;

    /**
     * PUBLIC:
     * A connection pool is used to specify how connection should be pooled in a server session.
     */
    public ConnectionPool() {
        this.maxNumberOfConnections = 50;
        this.minNumberOfConnections = 3;
        this.checkConnections = false;
        resetConnections();
    }

    /**
     * PUBLIC:
     * A connection pool is used to specify how connection should be pooled in a server session.
     */
    public ConnectionPool(String name, Login login, int minNumberOfConnections, int maxNumberOfConnections, ServerSession owner) {
        this.login = login;
        this.owner = owner;
        this.name = name;
        this.maxNumberOfConnections = maxNumberOfConnections;
        this.minNumberOfConnections = minNumberOfConnections;
        this.checkConnections = false;
        resetConnections();
    }
    
    /**
     * INTERNAL:
     * Wait until a connection is available and allocate the connection for the client.
     */
    public synchronized Accessor acquireConnection() throws ConcurrencyException {
        // PERF: Using direct variable access to minimize concurrency bottleneck.
        while (this.connectionsAvailable.isEmpty()) {
            if ((this.connectionsUsed.size() + this.connectionsAvailable.size()) < this.maxNumberOfConnections) {
                Accessor connection = buildConnection();
                this.connectionsUsed.add(connection);
                return connection;
            }
            try {
                wait();// Notify is called when connections are released.
            } catch (InterruptedException exception) {
                throw ConcurrencyException.waitFailureOnClientSession(exception);
            }
        }

        Accessor connection = (Accessor)this.connectionsAvailable.get(0);
        this.connectionsAvailable.remove(connection);
        if (this.checkConnections){
            int connectionSize = this.connectionsAvailable.size();
            //TopLink has encountered a problem with a connection where the database no longer responded
            //We need to now ensure that the failure was specific to that connection or we need to empty
            //the pool of dead connections in the case of a database failover.
            while (connectionSize >= 0){
                if (this.getOwner().getLogin().isConnectionHealthValidatedOnError() && this.getOwner().getServerPlatform().wasFailureCommunicationBased(null, connection, this.getOwner())){
                    try{
                        //connection failed connect test
                        connection.closeConnection();
                    }catch (Exception ex){
                        //ignore
                    }finally{
                        connection.releaseCustomizer();
                    }
                    if (this.connectionsAvailable.isEmpty()){
                        this.checkConnections = false;
                        //we have emptied out all connections so let's have the connection pool build more
                        return this.acquireConnection();
                    }else{
                        //test next connection
                        connection = (Accessor)this.connectionsAvailable.get(0);
                        this.connectionsAvailable.remove(connection);
                        --connectionSize;
                    }
                }else{
                    //connection was good use it.  And make sure we stop testing connections
                    this.checkConnections = false;
                    break;
                }
            }
        }
        this.connectionsUsed.add(connection);
        if (getOwner().isInProfile()) {
            getOwner().updateProfile(getName(), new Integer(getConnectionsUsed().size()));
        }
        return connection;
    }

    /**
     * INTERNAL:
     * Create a new connection, accessors are used as connections.
     */
    protected Accessor buildConnection() {
        Login localLogin = (Login)getLogin().clone();
        Accessor connection = localLogin.buildAccessor();
        connection.connect(localLogin, getOwner());

        return connection;
    }

    /**
     * INTERNAL:
     * returns the connections currently available for use in the pool
     */
    public Vector getConnectionsAvailable() {
        return connectionsAvailable;
    }

    /**
     *  Return a list of the connections that are being used.
     *  @return java.util.Vector
     **/
    protected Vector getConnectionsUsed() {
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
     * When the max is reached clients must wait for a connection to become available.
     */
    public int getMaxNumberOfConnections() {
        return maxNumberOfConnections;
    }

    /**
     * PUBLIC:
     * Return the minimum number of connections.
     * These connection will be create on startup.
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
        if(connection instanceof DatasourceAccessor){
            ((DatasourceAccessor)connection).resetStatmentsCount();
        }

        this.connectionsUsed.remove(connection);

        if (!connection.isValid()){
            getOwner().setCheckConnections();
            try{
                connection.disconnect(getOwner());
            }catch (DatabaseException ex){
                //this is an invalid connection so expect an exception.
            }
        }else{
            if ( (this.connectionsUsed.size() + this.connectionsAvailable.size() ) < this.minNumberOfConnections) {
                this.connectionsAvailable.add(connection);
            } else {
                connection.disconnect(getOwner());
            }
        }
        if (getOwner().isInProfile()) {
            getOwner().updateProfile(getName(), new Integer(getConnectionsUsed().size()));
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
     * Set the maximum number of connections allowed.
     * When the max is reached clients must wait for a connection to become available.
     */
    public void setMaxNumberOfConnections(int maxNumberOfConnections) {
        this.maxNumberOfConnections = maxNumberOfConnections;
    }

    /**
     * PUBLIC:
     * Set the minimum number of connections.
     * These connection will be create on startup.
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

        for (Enumeration avaiableEnum = getConnectionsAvailable().elements();
                 avaiableEnum.hasMoreElements();) {
            try {
                ((Accessor)avaiableEnum.nextElement()).disconnect(getOwner());
            } catch (DatabaseException exception) {
                // Ignore.
            }
        }

        for (Enumeration usedEnum = getConnectionsUsed().elements(); usedEnum.hasMoreElements();) {
            try {
                ((Accessor)usedEnum.nextElement()).disconnect(getOwner());
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
        for (int index = getMinNumberOfConnections(); index > 0; index--) {
            getConnectionsAvailable().addElement(buildConnection());
        }

        setIsConnected(true);
    }

    /**
     * INTERNAL:
     * return a string representation of this connection pool
     */
    public String toString() {
        Object[] args = { new Integer(getMinNumberOfConnections()), new Integer(getMaxNumberOfConnections()) };
        return Helper.getShortClassName(getClass()) + ToStringLocalization.buildMessage("min_max", args);
    }
}
