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
import java.io.*;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sequencing.SequencingFactory;
import org.eclipse.persistence.sessions.coordination.CommandManager;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <b>Purpose</b>: Acts as a client to the server session.
 * <p>
 * <b>Description</b>: This session is brokered by the server session for use in three-tiered applications.
 * It is used to store the context of the connection, i.e. the login to be used for this client.
 * This allows each client connected to the server to contain its own user login.
 * <p>
 * <b>Responsibilities</b>:
 *    <ul>
 *    <li> Allow units of work to be acquired and pass them the client login's exclusive connection.
 *    <li> Forward all requests and queries to its parent server session.
 *    </ul>
 *  <p>
 * This class is an implementation of {@link org.eclipse.persistence.sessions.Session}.
 * Please refer to that class for a full API.  The public interface should be used.
 * @see Server
 * @see org.eclipse.persistence.sessions.Session
 * @see org.eclipse.persistence.sessions.UnitOfWork
 */
/*
 *  05/28/2008-1.0M8 Andrei Ilitchev 
 *        - 224964: Provide support for Proxy Authentication through JPA.
 *        Added a new constructor that takes Properties. 
 */
public class ClientSession extends AbstractSession {
    protected ServerSession parent;
    protected ConnectionPolicy connectionPolicy;
    protected Accessor writeConnection;
    protected boolean isActive;
    protected Sequencing sequencing;

    /**
     * INTERNAL:
     * Create and return a new client session.
     */
    public ClientSession(ServerSession parent, ConnectionPolicy connectionPolicy) {
        this(parent, connectionPolicy, null);
    }
    
    public ClientSession(ServerSession parent, ConnectionPolicy connectionPolicy, Map properties) {
        super();
        this.project = parent.getProject();
        if (connectionPolicy.isUserDefinedConnection()) {
            // PERF: project only requires clone if login is different
            this.setProject((Project)getProject().clone());
            this.setLogin(connectionPolicy.getLogin());
        }
        this.isLoggingOff = parent.isLoggingOff();
        this.isActive = true;
        this.externalTransactionController = parent.getExternalTransactionController();
        this.parent = parent;
        this.connectionPolicy = connectionPolicy;
        this.writeConnection = this.accessor;// Uses accessor as write unless is pooled.
        this.accessor = parent.getAccessor();// This is used for reading only.
        this.name = parent.getName();
        this.profiler = parent.getProfiler();
        this.isInProfile = parent.isInProfile();
        this.commitManager = parent.getCommitManager();
        this.sessionLog = parent.getSessionLog();
        if (parent.hasEventManager()) {
            this.eventManager = parent.getEventManager().clone(this);
        }
        this.exceptionHandler = parent.getExceptionHandler();
        this.pessimisticLockTimeoutDefault = parent.getPessimisticLockTimeoutDefault();
        this.queryTimeoutDefault = parent.getQueryTimeoutDefault();
        this.properties = properties;
        if (this.eventManager != null) {
            this.eventManager.postAcquireClientSession();
        }
        this.descriptors = parent.getDescriptors();
        incrementProfile(SessionProfiler.ClientSessionCreated);
    }

    protected ClientSession(org.eclipse.persistence.sessions.Project project) {
        super(project);
    }

    /**
     * INTERNAL:
     * Called in the end of beforeCompletion of external transaction synchronization listener.
     * Close the managed sql connection corresponding to the external transaction
     * and releases accessor.
     */
    public void releaseJTSConnection() {
        if (hasWriteConnection()) {
            getWriteConnection().closeJTSConnection();
            releaseWriteConnection();
        }
    }

    /**
     * INTERNAL:
     * This is internal to the unit of work and should never be called otherwise.
     */
    public void basicBeginTransaction() {
        // if an exclusive connection is use this client session may have 
        // a connection already
        if (getWriteConnection() == null) {
            // Ensure that the client is logged in for lazy clients.
            if (getConnectionPolicy().isLazy()) {
                this.parent.acquireClientConnection(this);
            }
        }
        try {
            super.basicBeginTransaction();
        } catch (RuntimeException ex) {
            if(getWriteConnection() != null && !getWriteConnection().isInTransaction()) {
                if (getConnectionPolicy().isLazy()) {
                    // If not released right away the accessor
                    // may never be released (in case of repeated attempt to begin transaction).
                    // In case of internal connection pool it would mean less connections available.
                    this.parent.releaseClientSession(this);
                }
            }
            throw ex;
        }
    }

    /**
     * INTERNAL:
     * This is internal to the unit of work and should not be called otherwise.
     */
    public void basicCommitTransaction() {
        //Only release connection when transaction succeeds.  
        //If not, connection will be released in rollback.
        super.basicCommitTransaction();

        // if synchronized then the connection will be released in external transaction callback.
        if (hasExternalTransactionController()) {
            if(!isSynchronized()) {
                releaseJTSConnection();
            }
        } else {
            releaseWriteConnection();
        }
    }

    /**
     * INTERNAL:
     * This is internal to the unit of work and should not be called otherwise.
     */
    public void basicRollbackTransaction() {
        try {
            //BUG 2660471: Make sure there is an accessor (moved here from Session)
            //BUG 2846785: EXCEPTION THROWN IN PREBEGINTRANSACTION EVENT CAUSES NPE
            if (hasWriteConnection()) {
                super.basicRollbackTransaction();
            }
        } finally {
            // if synchronized then the connection will be released in external transaction callback.
            if (hasExternalTransactionController()) {
                if(!isSynchronized()) {
                    releaseJTSConnection();
                }
            } else {
                releaseWriteConnection();
            }
        }
    }

    /**
     * INTERNAL:
     * Connect the session only (this must be the write connection as the read is shared).
     */
    public void connect() throws DatabaseException {
        getWriteConnection().connect(getDatasourceLogin(), this);
    }

    /**
     * INTERNAL:
     * Was PUBLIC: customer will be redirected to {@link org.eclipse.persistence.sessions.Session}.
     * Return true if the pre-defined query is defined on the session.
     */
    public boolean containsQuery(String queryName) {
        boolean containsQuery = getQueries().containsKey(queryName);
        if (containsQuery == false) {
            containsQuery = this.parent.containsQuery(queryName);
        }
        return containsQuery;
    }

    /**
     * INTERNAL:
     * Disconnect the accessor only (this must be the write connection as the read is shared).
     */
    public void disconnect() throws DatabaseException {
        getWriteConnection().disconnect(this);
    }

    /**
     * INTERNAL:
     * Return the read or write connection depending on the transaction state.  Will throw a DatabaseException if the Accessor cannot be returned.
     */
    public Accessor getAccessor() {
        Accessor accessor = null;
        if (isInTransaction()) {
            accessor = getWriteConnection();
        } else {
            accessor = super.getAccessor();
        }
        if (accessor==null){
            throw DatabaseException.databaseAccessorConnectionIsNull(null, this);
        }
        return accessor;
    }


    /**
     * ADVANCED:
     * This method will return the connection policy that was used during the
     * acquisition of this client session.  The properties within the ConnectionPolicy
     * may be used when acquiring an exclusive connection for an IsolatedSession.
     */
    public ConnectionPolicy getConnectionPolicy() {
        return connectionPolicy;
    }

    /**
     * INTERNAL:
     * Gets the next link in the chain of sessions followed by a query's check
     * early return, the chain of sessions with identity maps all the way up to
     * the root session.
     * <p>
     * Used for session broker which delegates to registered sessions, or UnitOfWork
     * which checks parent identity map also.
     * @param canReturnSelf true when method calls itself.  If the path
     * starting at <code>this</code> is acceptable.  Sometimes true if want to
     * move to the first valid session, i.e. executing on ClientSession when really
     * should be on ServerSession.
     * @param terminalOnly return the session we will execute the call on, not
     * the next step towards it.
     * @return this if there is no next link in the chain
     */
    public AbstractSession getParentIdentityMapSession(DatabaseQuery query, boolean canReturnSelf, boolean terminalOnly) {
        // Note could return self as ClientSession shares the same identity map
        // as parent.  This reveals a deep problem, as queries will be cached in
        // the Server identity map but executed here using the write connection.
        return this.parent.getParentIdentityMapSession(query, canReturnSelf, terminalOnly);
    }
    
    /**
     * Search for and return the user defined property from this client session, if it not found then search for the property
     * from parent.
     */
    public Object getProperty(String name){
        Object propertyValue = super.getProperty(name);
        if (propertyValue == null) {
           propertyValue = this.parent.getProperty(name);
        }
        return propertyValue;
    }

    /**
     * INTERNAL:
     * Gets the session which this query will be executed on.
     * Generally will be called immediately before the call is translated,
     * which is immediately before session.executeCall.
     * <p>
     * Since the execution session also knows the correct datasource platform
     * to execute on, it is often used in the mappings where the platform is
     * needed for type conversion, or where calls are translated.
     * <p>
     * Is also the session with the accessor.  Will return a ClientSession if
     * it is in transaction and has a write connection.
     * @return a session with a live accessor
     * @param query may store session name or reference class for brokers case
     */
    public AbstractSession getExecutionSession(DatabaseQuery query) {
        // For CR#4334 if in transaction stay on client session.
        // That way client's write accessor will be used for all queries.
        // This is to preserve transaction isolation levels.
        // For bug 3602222 if a query is executed directly on a client session when
        // in transaction, then dirty data could be put in the shared cache for the
        // client session uses the identity map of its parent.
        // However beginTransaction() is not public API on ClientSession.
        // if fix this could add: && (query.getSession() != this).
        if (isInTransaction()) {
            return this;
        }
        return this.parent.getExecutionSession(query);
    }

    /**
     * INTERNAL:
     * Return the parent.
     * This is a server session.
     */
    public ServerSession getParent() {
        return parent;
    }

    /**
     * INTERNAL:
     * Was PUBLIC: customer will be redirected to {@link org.eclipse.persistence.sessions.Session}.
     * Return the query from the session pre-defined queries with the given name.
     * This allows for common queries to be pre-defined, reused and executed by name.
     */
    public DatabaseQuery getQuery(String name) {
        DatabaseQuery query = super.getQuery(name);
        if (query == null) {
            query = this.parent.getQuery(name);
        }

        return query;
    }

    /**
     * INTERNAL:
     */
    public DatabaseQuery getQuery(String name, Vector args) {// CR3716; Predrag;
        DatabaseQuery query = super.getQuery(name, args);
        if (query == null) {
            query = this.parent.getQuery(name, args);
        }
        return query;
    }

    /**
     * INTERNAL:
     * was ADVANCED:
     * Creates sequencing object for the session.
     * Typically there is no need for the user to call this method -
     * it is called from the constructor.
     */
    public void initializeSequencing() {
        this.sequencing = SequencingFactory.createSequencing(this);
    }

    /**
     * INTERNAL:
     * Return the Sequencing object used by the session.
     * Lazy init sequencing to defer from client session creation to improve creation performance.
     */
    public Sequencing getSequencing() {
        // PERF: lazy init defer from constructor, only created when needed.
        if (sequencing == null) {
            initializeSequencing();
        }
        return sequencing;
    }

    /**
     * INTERNAL:
     * Marked internal as this is not customer API but helper methods for
     * accessing the server platform from within other sessions types
     * (i.e. not DatabaseSession)
     */
    public ServerPlatform getServerPlatform() {
        return this.parent.getServerPlatform();
    }

    /**
     * INTERNAL:
     * Returns the type of session, its class.
     * <p>
     * Override to hide from the user when they are using an internal subclass
     * of a known class.
     * <p>
     * A user does not need to know that their UnitOfWork is a
     * non-deferred UnitOfWork, or that their ClientSession is an
     * IsolatedClientSession.
     */
    public String getSessionTypeString() {
        return "ClientSession";
    }

    /**
     * INTERNAL:
     * Return the connection to be used for database modification.
     */
    public Accessor getWriteConnection() {
        return writeConnection;
    }

    /**
     * INTERNAL:
     * Return if this session has been connected.
     */
    protected boolean hasWriteConnection() {
        if (getWriteConnection() == null) {
            return false;
        }

        return getWriteConnection().isConnected();
    }

    /**
     * INTERNAL:
     * Set up the IdentityMapManager.  This method allows subclasses of Session to override
     * the default IdentityMapManager functionality.
     */
    public void initializeIdentityMapAccessor() {
        this.identityMapAccessor = new ClientSessionIdentityMapAccessor(this);
    }

    /**
     * INTERNAL:
     * Was PUBLIC: customer will be redirected to {@link org.eclipse.persistence.sessions.Session}.
     * Return if the client session is active (has not been released).
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * INTERNAL:
     * Return if this session is a client session.
     */
    public boolean isClientSession() {
        return true;
    }

    /**
     * INTERNAL:
     * Was PUBLIC: customer will be redirected to {@link org.eclipse.persistence.sessions.Session}.
     * Return if this session has been connected to the database.
     */
    public boolean isConnected() {
        return this.parent.isConnected();
    }

    /**
     * INTERNAL:
     * Was PUBLIC: customer will be redirected to {@link org.eclipse.persistence.sessions.Session}.
     * Release the client session.
     * This releases the client session back to it server.
     * Normally this will logout of the client session's connection,
     * and allow the client session to garbage collect.
     */
    public void release() throws DatabaseException {
        if (!isActive()) {
            return;
        }
        if (this.eventManager != null) { 
            this.eventManager.preReleaseClientSession();
        }

        //removed is Lazy check as we should always release the connection once
        //the client session has been released.  It is also required for the 
        //behavior of a subclass ExclusiveIsolatedClientSession
        if (getWriteConnection() != null) {
            this.parent.releaseClientSession(this);
        }

        // we are not inactive until the connection is  released
        setIsActive(false);
        log(SessionLog.FINER, SessionLog.CONNECTION, "client_released");
        if (this.eventManager != null) { 
            this.eventManager.postReleaseClientSession();
        }
    }

    /**
     * INTERNAL:
     * This is internal to the unit of work and should not be called otherwise.
     */
    protected void releaseWriteConnection() {
        if (getConnectionPolicy().isLazy() && getWriteConnection()!= null) {
            this.parent.releaseClientSession(this);
        }
    }

    /**
     * INTERNAL:
     * Set the connection policy.
     */
    public void setConnectionPolicy(ConnectionPolicy connectionPolicy) {
        this.connectionPolicy = connectionPolicy;
    }

    /**
     * INTERNAL:
     * Set if the client session is active (has not been released).
     */
    protected void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * INTERNAL:
     * Set the parent.
     * This is a server session.
     */
    protected void setParent(ServerSession parent) {
        this.parent = parent;
    }

    /**
     * INTERNAL:
     * Set the connection to be used for database modification.
     */
    public void setWriteConnection(Accessor writeConnection) {
        if(writeConnection != null) {
            // new writeConnection is set
            this.writeConnection = writeConnection;
            this.writeConnection.createCustomizer(this);
        } else {
            // existing writeConnection is released; the not null check in case the method called twice.
            if(this.writeConnection != null) {
                this.writeConnection.releaseCustomizer(this);
                this.writeConnection = null;
            }
        }
    }

    /**
     * INTERNAL:
     * Print the connection status with the session.
     */
    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write(getSessionTypeString());
        writer.write("(");
        writer.write(String.valueOf(getWriteConnection()));
        writer.write(")");
        return writer.toString();
    }

    /**
     * INTERNAL:
     * Return the manager that allows this processor to receive or propagate commands from/to TopLink cluster
     * @see CommandManager
     * @return a remote command manager
     */
    public CommandManager getCommandManager() {
        return this.parent.getCommandManager();
    }

    /**
     * INTERNAL:
     * Return whether changes should be propagated to TopLink cluster.  This is one of the required
     * cache synchronization setting
     */
    public boolean shouldPropagateChanges() {
        return this.parent.shouldPropagateChanges();
    }

    /**
     * INTERNAL:
     */
    public void releaseReadConnection(Accessor connection) {
        //bug 4668234 -- used to only release connections on server sessions but should always release
        this.parent.releaseReadConnection(connection);
    }

    /**
     * INTERNAL:
     * This method is called in case externalConnectionPooling is used
     * right after the accessor is connected. 
     * Used by the session to rise an appropriate event.
     */
    public void postConnectExternalConnection(Accessor accessor) {
        if (this.eventManager != null) { 
            this.eventManager.postAcquireConnection(accessor);
        }
    }

    /**
     * INTERNAL:
     * This method is called in case externalConnectionPooling is used
     * right before the accessor is disconnected. 
     * Used by the session to rise an appropriate event.
     */
    public void preDisconnectExternalConnection(Accessor accessor) {
        if (this.eventManager != null) { 
            this.eventManager.preReleaseConnection(accessor);
        }
    }

    /**
     * INTERNAL:
     * This method is called in case externalConnectionPooling is used.
     * If returns true, accessor used by the session keeps its
     * connection open until released by the session. 
     */
    public boolean isExclusiveConnectionRequired() {
        return !getConnectionPolicy().isLazy && isActive();
    }
}
