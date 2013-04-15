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
 *     05/28/2008-1.0M8 Andrei Ilitchev 
 *        - 224964: Provide support for Proxy Authentication through JPA.
 *        Added a new constructor that takes Properties. 
 *     14/05/2012-2.4 Guy Pelletier   
 *       - 376603: Provide for table per tenant support for multitenant applications
 *     08/11/2012-2.5 Guy Pelletier  
 *       - 393867: Named queries do not work when using EM level Table Per Tenant Multitenancy.
 ******************************************************************************/  
package org.eclipse.persistence.sessions.server;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sequencing.SequencingFactory;
import org.eclipse.persistence.sessions.coordination.CommandManager;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
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
public class ClientSession extends AbstractSession {
    protected ServerSession parent;
    protected ConnectionPolicy connectionPolicy;
    protected Map<String, Accessor> writeConnections;
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
        // If we have table per tenant descriptors let's clone the project so
        // that we can have a separate jpql parse cache for each tenant.
        if (parent.hasTablePerTenantDescriptors()) {
            this.project = parent.getProject().clone();
            this.project.setJPQLParseCacheMaxSize(parent.getProject().getJPQLParseCache().getMaxSize());
        } else {
            this.project = parent.getProject();
        }
        
        if (connectionPolicy.isUserDefinedConnection()) {
            // PERF: project only requires clone if login is different
            this.setProject(getProject().clone());
            this.setLogin(connectionPolicy.getLogin());
        }
        this.isLoggingOff = parent.isLoggingOff();
        this.isActive = true;
        this.externalTransactionController = parent.getExternalTransactionController();
        this.parent = parent;
        this.connectionPolicy = connectionPolicy;
        this.name = parent.getName();
        this.profiler = parent.getProfiler();
        this.isInProfile = parent.isInProfile();
        this.commitManager = parent.getCommitManager();
        this.partitioningPolicy = parent.getPartitioningPolicy();
        this.sessionLog = parent.getSessionLog();
        if (parent.hasEventManager()) {
            this.eventManager = parent.getEventManager().clone(this);
        }
        this.exceptionHandler = parent.getExceptionHandler();
        this.pessimisticLockTimeoutDefault = parent.getPessimisticLockTimeoutDefault();
        this.queryTimeoutDefault = parent.getQueryTimeoutDefault();
        this.isConcurrent = parent.isConcurrent();
        this.properties = properties;
        this.multitenantContextProperties = parent.getMultitenantContextProperties();
        
        if (this.eventManager != null) {
            this.eventManager.postAcquireClientSession();
        }
        
        // Copy down the table per tenant queries from the parent. These queries
        // must be cloned per client session.
        if (parent.hasTablePerTenantQueries()) {
            for (DatabaseQuery query : parent.getTablePerTenantQueries()) {
                addTablePerTenantQuery((DatabaseQuery) query.clone());
            }
        }
        // If we have table per tenant descriptors, they will need to be
        // cloned as we will be changing the descriptors per tenant.
        if (parent.hasTablePerTenantDescriptors()) {
            this.descriptors = new HashMap<Class, ClassDescriptor>();
            this.descriptors.putAll(parent.getDescriptors());
            
            for (ClassDescriptor descriptor : parent.getTablePerTenantDescriptors()) {
                ClassDescriptor clonedDescriptor = (ClassDescriptor) descriptor.clone();
                addTablePerTenantDescriptor(clonedDescriptor);
                this.descriptors.put(clonedDescriptor.getJavaClass(), clonedDescriptor);
            }  
            
            if (hasProperties()) {
                for (Object propertyName : properties.keySet()) {
                    updateTablePerTenantDescriptors((String) propertyName, properties.get(propertyName));
                }
            }
        } else {
            this.descriptors = parent.getDescriptors();
        }
        
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
    @Override
    public void releaseJTSConnection() {
        if (hasWriteConnection()) {
            for (Accessor accessor : getWriteConnections().values()) {
                accessor.closeJTSConnection();
            }
            releaseWriteConnection();
        }
    }

    /**
     * INTERNAL:
     * This is internal to the unit of work and should not be called otherwise.
     */
    @Override
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
    @Override
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
    public void connect(Accessor accessor) throws DatabaseException {
        accessor.connect(getDatasourceLogin(), this);
    }

    /**
     * INTERNAL:
     * Was PUBLIC: customer will be redirected to {@link org.eclipse.persistence.sessions.Session}.
     * Return true if the pre-defined query is defined on the session.
     */
    @Override
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
    public void disconnect(Accessor accessor) throws DatabaseException {
        accessor.disconnect(this);
    }

    /**
     * INTERNAL:
     * Execute the call on the correct connection accessor.
     * Outside of a transaction the server session's read connection pool is used.
     * In side a transaction, or for exclusive sessions the write connection is used.
     * For partitioning there may be multiple write connections.
     */
    @Override
    public Object executeCall(Call call, AbstractRecord translationRow, DatabaseQuery query) throws DatabaseException {
        if ((!isInTransaction() || (query.isObjectLevelReadQuery() && ((ObjectLevelReadQuery)query).isReadOnly())) && !isExclusiveIsolatedClientSession() ) {
            return this.parent.executeCall(call, translationRow, query);
        }
        boolean shouldReleaseConnection = false;
        if (query.getAccessors() == null) {
            // First check for a partitioning policy.
            // An exclusive session will always use a single connection once allocated.
            if (!hasWriteConnection()  || !isExclusiveIsolatedClientSession()) {
                Collection<Accessor> accessors = getAccessors(call, translationRow, query);
                if (accessors != null && !accessors.isEmpty()) {
                    query.setAccessors(accessors);
                    // the session has been already released and this query is likely instantiates a ValueHolder - 
                    // release exclusive connection immediately after the query is executed, otherwise it may never be released.
                    shouldReleaseConnection = !this.isActive;
                }
            }
        }
        if (query.getAccessors() == null) {
            // If the connection has not yet been acquired then do it here.
            if (!hasWriteConnection()) {
                this.parent.acquireClientConnection(this);
                // The session has been already released and this query is likely instantiates a ValueHolder - 
                // release exclusive connection immediately after the query is executed, otherwise it may never be released.
                shouldReleaseConnection = !this.isActive;
                query.setAccessors(getAccessors());
            } else {
                // Must use the default write connection if there are multiple connections.
                if (!isExclusiveIsolatedClientSession() && this.connectionPolicy.isPooled()) {
                    Accessor defaultWriteConnection = this.writeConnections.get(this.connectionPolicy.getPoolName());
                    if (defaultWriteConnection == null) {
                        // No default connection yet, must acquire it.
                        this.parent.acquireClientConnection(this);                        
                    }
                    if (this.writeConnections.size() == 1) {
                        // Connection is the default, just use it.
                        query.setAccessors(getAccessors());
                    } else {
                        List<Accessor> accessors = new ArrayList(1);
                        accessors.add(defaultWriteConnection);
                        query.setAccessors(accessors);                        
                    }
                } else {
                    query.setAccessors(getAccessors());
                }
            }
        }
        Object result = null;
        RuntimeException exception = null;
        try {
            result = basicExecuteCall(call, translationRow, query);
        } catch (RuntimeException caughtException) {
            exception = caughtException;
        } finally {
            if (call.isFinished() || exception != null) {
                query.setAccessors(null);
                // Note that connection could be release only if it has been acquired by the same query,
                // that allows to execute other queries from postAcquireConnection / preReleaseConnection events
                // without wiping out connection set by the original query or causing stack overflow, see
                // bug 299048 - Triggering indirection on closed ExclusiveIsolatedSession may cause exception 
                if (shouldReleaseConnection && hasWriteConnection()) {
                    try {
                        this.parent.releaseClientSession(this);
                    } catch (RuntimeException releaseException) {
                        if (exception == null) {
                            throw releaseException;
                        }
                        //else ignore
                    }
                }
            } else {
                if (query.isObjectLevelReadQuery()) {
                    ((DatabaseCall)call).setHasAllocatedConnection(shouldReleaseConnection);
                }
            }
            if (exception != null) {
                throw exception;
            }
        }
        return result;
    }

    /**
     * INTERNAL:
     * Release (if required) connection after call.
     * @param query
     * @return
     */
    public void releaseConnectionAfterCall(DatabaseQuery query) {
        if ((!isInTransaction() || (query.isObjectLevelReadQuery() && ((ObjectLevelReadQuery)query).isReadOnly())) && !isExclusiveIsolatedClientSession() ) {
            this.parent.releaseConnectionAfterCall(query);
        } else {
            if (hasWriteConnection()) {
                query.setAccessors(null);
                this.parent.releaseClientSession(this);
            }
        }
    }

    /**
     * INTERNAL:
     * Return the write connections if in a transaction.
     * These may be empty/null until the first query has been executed inside the transaction.
     * This should only be called within a transaction.
     * If outside of a transaction it will return null (unless using an exclusive connection).
     */
    @Override
    public Collection<Accessor> getAccessors() {
        if (isInTransaction()) {
            if (this.writeConnections == null) {
                return null;
            }
            return this.writeConnections.values();
        } else {
            return this.accessors;
        }
    }

    /**
     * INTERNAL:
     * This should normally not be used, getAccessors() should be used to support partitioning.
     * To maintain backward compatibility, and to support certain cases that required a default accessor,
     * if inside a transaction, then a default connection will be allocated.
     * This is required for sequencing, and JPA connection unwrapping, and ordt mappings.
     * Outside of a transaction, to maintain backward compatibility the server session's accessor will be returned.
     */
    @Override
    public Accessor getAccessor() {
        Collection<Accessor> accessors = getAccessors();
        if ((accessors == null) || accessors.isEmpty()) {
            if (isInTransaction()) {
                this.parent.acquireClientConnection(this);
                accessors = getAccessors();
            } else {
                return this.parent.getAccessor();
            }
        }
        if (accessors instanceof List) {
            return ((List<Accessor>)accessors).get(0);
        }
        return accessors.iterator().next();
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
     * ADVANCED:
     * Return all registered descriptors.
     */
    public Map<Class, ClassDescriptor> getDescriptors() {
        // descriptors from the project may have been modified (for table per
        // tenants so make sure to return the updated ones)
        if (hasTablePerTenantDescriptors()) {
            return this.descriptors;
        } else {
            return super.getDescriptors();
        }
    }

    /**
     * INTERNAL:
     * Returns the appropriate IdentityMap session for this descriptor.  Sessions can be 
     * chained and each session can have its own Cache/IdentityMap.  Entities can be stored
     * at different levels based on Cache Isolation.  This method will return the correct Session
     * for a particular Entity class based on the Isolation Level and the attributes provided.
     * <p>
     * @param canReturnSelf true when method calls itself.  If the path
     * starting at <code>this</code> is acceptable.  Sometimes true if want to
     * move to the first valid session, i.e. executing on ClientSession when really
     * should be on ServerSession.
     * @param terminalOnly return the last session in the chain where the Enitity is stored.
     * @return Session with the required IdentityMap
     */
    @Override
    public AbstractSession getParentIdentityMapSession(ClassDescriptor descriptor, boolean canReturnSelf, boolean terminalOnly) {
        // Note could return self as ClientSession shares the same identity map
        // as parent.  This reveals a deep problem, as queries will be cached in
        // the Server identity map but executed here using the write connection.
        return this.parent.getParentIdentityMapSession(descriptor, canReturnSelf, terminalOnly);
    }
    
    /**
     * Search for and return the user defined property from this client session, if it not found then search for the property
     * from parent.
     */
    @Override
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
    @Override
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
    @Override
    public ServerSession getParent() {
        return parent;
    }

    /**
     * INTERNAL:
     * Was PUBLIC: customer will be redirected to {@link org.eclipse.persistence.sessions.Session}.
     * Return the query from the session pre-defined queries with the given name.
     * This allows for common queries to be pre-defined, reused and executed by name.
     */
    @Override
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
    @Override
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
    @Override
    public Sequencing getSequencing() {
        // PERF: lazy init defer from constructor, only created when needed.
        if (this.sequencing == null) {
            initializeSequencing();
        }
        return this.sequencing;
    }

    /**
     * INTERNAL:
     * Marked internal as this is not customer API but helper methods for
     * accessing the server platform from within other sessions types
     * (i.e. not DatabaseSession)
     */
    @Override
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
    @Override
    public String getSessionTypeString() {
        return "ClientSession";
    }

    /**
     * INTERNAL:
     * Return the map of write connections.
     * Multiple connections can be used for data partitioning and replication.
     * The connections are keyed by connection pool name.
     */
    public Map<String, Accessor> getWriteConnections() {
        if (this.writeConnections == null) {
            this.writeConnections = new HashMap(4);
        }
        return this.writeConnections;
    }

    /**
     * INTERNAL:
     * Return the connection to be used for database modification.
     */
    public Accessor getWriteConnection() {
        if ((this.writeConnections == null) || this.writeConnections.isEmpty()) {
            return null;
    }
        return this.writeConnections.values().iterator().next();
    }

    /**
     * INTERNAL:
     * Return if this session has been connected.
     */
    public boolean hasWriteConnection() {
        if (this.writeConnections == null) {
            return false;
        }

        return !this.writeConnections.isEmpty();
    }

    /**
     * INTERNAL:
     * Set up the IdentityMapManager.  This method allows subclasses of Session to override
     * the default IdentityMapManager functionality.
     */
    @Override
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
    @Override
    public boolean isClientSession() {
        return true;
    }

    /**
     * INTERNAL:
     * Was PUBLIC: customer will be redirected to {@link org.eclipse.persistence.sessions.Session}.
     * Return if this session has been connected to the database.
     */
    @Override
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
    @Override
    public void release() throws DatabaseException {
        if (!this.isActive) {
            return;
        }
        if (this.eventManager != null) { 
            this.eventManager.preReleaseClientSession();
        }

        //removed is Lazy check as we should always release the connection once
        //the client session has been released.  It is also required for the 
        //behavior of a subclass ExclusiveIsolatedClientSession
        if (hasWriteConnection()) {
            this.parent.releaseClientSession(this);
        }

        // we are not inactive until the connection is  released
        this.isActive = false;
        log(SessionLog.FINER, SessionLog.CONNECTION, "client_released");
        if (this.eventManager != null) { 
            this.eventManager.postReleaseClientSession();
        }
        incrementProfile(SessionProfiler.ClientSessionReleased);
    }
    
    /**
     * INTERNAL:
     * A query execution failed due to an invalid query.
     * Re-connect and retry the query.
     */
    @Override
    public Object retryQuery(DatabaseQuery query, AbstractRecord row, DatabaseException databaseException, int retryCount, AbstractSession executionSession) {
        // If not in a transaction and has a write connection, must release it if invalid.
        getParent().releaseInvalidClientSession(this);
        return super.retryQuery(query, row, databaseException, retryCount, executionSession);
    }

    /**
     * INTERNAL:
     * This is internal to the unit of work and should not be called otherwise.
     */
    protected void releaseWriteConnection() {
        if (this.connectionPolicy.isLazy() && hasWriteConnection()) {
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
     * Add the connection to the client session.
     * Multiple connections are supported to allow data partitioning and replication.
     * The accessor is returned, as if detected to be dead it may be replaced.
     */
    public Accessor addWriteConnection(String poolName, Accessor writeConnection) {
        getWriteConnections().put(poolName, writeConnection);
        writeConnection.createCustomizer(this);
        //if connection is using external connection pooling then the event will be risen right after it connects.
        if (!writeConnection.usesExternalConnectionPooling()) {
            postAcquireConnection(writeConnection);
        }
        // Transactions are lazily started on connections.
        if (isInTransaction()) {
            basicBeginTransaction(writeConnection);
        }
        return getWriteConnections().get(poolName);
    }
    
    /**
     * INTERNAL:
     * A begin transaction failed.
     * Re-connect and retry the begin transaction.
     */
    @Override
    public DatabaseException retryTransaction(Accessor writeConnection, DatabaseException databaseException, int retryCount, AbstractSession executionSession) {
        if (writeConnection.getPool() == null) {
            return super.retryTransaction(writeConnection, databaseException, retryCount, executionSession);
        }
        String poolName = writeConnection.getPool().getName();
        DatabaseLogin login = getLogin();
        int count = login.getQueryRetryAttemptCount();
        DatabaseException exceptionToThrow = databaseException;
        while (retryCount < count) {
            getWriteConnections().remove(poolName);
            //if connection is using external connection pooling then the event will be risen right after it connects.
            if (!writeConnection.usesExternalConnectionPooling()) {
                preReleaseConnection(writeConnection);
            }
            writeConnection.getPool().releaseConnection(writeConnection);
            try {
                // attempt to reconnect for a certain number of times.
                // servers may take some time to recover.
                ++retryCount;
                writeConnection = writeConnection.getPool().acquireConnection();
                writeConnection.beginTransaction(this);
                //passing the retry count will prevent a runaway retry where
                // we can acquire connections but are unable to execute any queries
                if (retryCount > 1) {
                    // We are retrying more than once lets wait to give connection time to restart.
                    //Give the failover time to recover.
                    Thread.currentThread().sleep(login.getDelayBetweenConnectionAttempts());
                }
                getWriteConnections().put(poolName, writeConnection);
                writeConnection.createCustomizer(this);
                //if connection is using external connection pooling then the event will be risen right after it connects.
                if (!writeConnection.usesExternalConnectionPooling()) {
                    postAcquireConnection(writeConnection);
                }
                return null;
            } catch (DatabaseException ex){
                //replace original exception with last exception thrown
                //this exception could be a data based exception as opposed
                //to a connection exception that needs to go back to the customer.
                exceptionToThrow = ex;
            } catch (InterruptedException ex) {
                //Ignore interrupted exception.
            }
        }
        return exceptionToThrow;
    }
    
    /**
     * INTERNAL:
     * Set the connection to be used for database modification.
     */
    public void setWriteConnections(Map<String, Accessor> writeConnections) {
        // Clear customizers.
        if ((this.writeConnections != null) && (writeConnections == null)) {
            for (Accessor accessor : this.writeConnections.values()) {
                accessor.releaseCustomizer(this);
            }
        }
        this.writeConnections = writeConnections;
    }

    /**
     * INTERNAL:
     * Set the connection to be used for database modification.
     */
    public void setWriteConnection(Accessor writeConnection) {
        if (writeConnection == null) {
            setWriteConnections(null);
            return;
        }
        String poolName = null;
        if (writeConnection.getPool() != null) {
            poolName = writeConnection.getPool().getName();
        } else {
            poolName = ServerSession.NOT_POOLED;
        }
        addWriteConnection(poolName, writeConnection);
    }

    /**
     * INTERNAL:
     * Print the connection status with the session.
     */
    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write(getSessionTypeString());
        writer.write("(");
        writer.write(String.valueOf(getWriteConnections()));
        writer.write(")");
        return writer.toString();
    }
    
    /**
     * INTERNAL:
     * Return the manager that allows this processor to receive or propagate commands from/to TopLink cluster
     * @see CommandManager
     * @return a remote command manager
     */
    @Override
    public CommandManager getCommandManager() {
        return this.parent.getCommandManager();
    }

    /**
     * INTERNAL:
     * Return whether changes should be propagated to TopLink cluster.  This is one of the required
     * cache synchronization setting
     */
    @Override
    public boolean shouldPropagateChanges() {
        return this.parent.shouldPropagateChanges();
    }

    /**
     * INTERNAL:
     * Release the cursor query's connection.
     */
    @Override
    public void releaseReadConnection(Accessor connection) {
        // If the cursor's connection is the write connection, then do not release it.
        if ((this.writeConnections != null) && this.writeConnections.containsValue(connection)) {
            return;
        }
        //bug 4668234 -- used to only release connections on server sessions but should always release
        this.parent.releaseReadConnection(connection);
    }

    /**
     * INTERNAL:
     * This method is called in case externalConnectionPooling is used.
     * If returns true, accessor used by the session keeps its
     * connection open until released by the session. 
     */
    @Override
    public boolean isExclusiveConnectionRequired() {
        return !this.connectionPolicy.isLazy && isActive();
    }
}
