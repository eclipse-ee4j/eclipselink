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
 *     12/24/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     01/11/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 ******************************************************************************/  
package org.eclipse.persistence.sessions.server;

import java.util.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sequencing.SequencingServer;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.internal.sessions.*;

/**
 * Implementation of Server
 * INTERNAL:
 * The public interface should be used.
 * <p>
 * <b>Purpose</b>: A single session that supports multiple user/clients connection at the same time.
 * <p>
 * <b>Description</b>: This session supports a shared session that can be used by multiple users
 * or clients in a three-tiered application.  It brokers client sessions to allow read and write access
 * through a unified object cache.  The server session uses a single connection pool by default, but allows multiple connection
 * pools and separate read/write pools to be configured.  All changes to objects and the database must be done through
 * a unit of work acquired from the client session, this allows the changes to occur in a transactional object
 * space and under a exclusive database connection.
 * <p>
 * <b>Responsibilities</b>:
 *    <ul>
 *    <li> Connection pooling.
 *    <li> Reading objects and maintaining the object cache.
 *    <li> Brokering client sessions.
 *    <li> Requiring the UnitOfWork to be used for modification.
 *    </ul>
 *    
 * @see Server
 * @see ClientSession
 * @see UnitOfWork
 */
public class ServerSession extends DatabaseSessionImpl implements Server {
    protected ConnectionPool readConnectionPool;
    protected Map<String, ConnectionPool> connectionPools;
    protected ConnectionPolicy defaultConnectionPolicy;
    protected int numberOfNonPooledConnectionsUsed;
    protected int maxNumberOfNonPooledConnections;
    
    public static final int NO_MAX = -1;
    public static final String DEFAULT_POOL = "default";
    public static final String NOT_POOLED = "not-pooled";
    
    /**
     * INTERNAL:
     * Create and return a new default server session.
     * @see Project#createServerSession()
     */
    public ServerSession() {
        super();
        this.connectionPools = new HashMap<String, ConnectionPool>(10);
    }

    /**
     * INTERNAL:
     * Create and return a new server session.
     * @see Project#createServerSession()
     */
    public ServerSession(Login login) {
        this(new Project(login));
    }

    /**
     * INTERNAL:
     * Create and return a new server session.
     * @see Project#createServerSession(int, int)
     */
    public ServerSession(Login login, int minNumberOfPooledConnection, int maxNumberOfPooledConnection) {
        this(new Project(login), minNumberOfPooledConnection, maxNumberOfPooledConnection);
    }

    /**
     * INTERNAL:
     * Create and return a new default server session.
     * @see Project#createServerSession(ConnectionPolicy)
     */
    public ServerSession(Login login, ConnectionPolicy defaultConnectionPolicy) {
        this(new Project(login), defaultConnectionPolicy);
    }

    /**
     * INTERNAL:
     * Create and return a new server session.
     * @see Project#createServerSession()
     * 
     * This is used by JPA, and SessionManager.
     */
    public ServerSession(Project project) {
        this(project, ConnectionPool.MIN_CONNECTIONS, ConnectionPool.MAX_CONNECTIONS);
    }

    /**
     * INTERNAL:
     * Create and return a new server session.
     * @see Project#createServerSession(int, int)
     */
    public ServerSession(Project project, int minNumberOfPooledConnection, int maxNumberOfPooledConnection) {
        this(project, ConnectionPool.INITIAL_CONNECTIONS, minNumberOfPooledConnection, maxNumberOfPooledConnection);
    }

    /**
     * INTERNAL:
     * Create and return a new server session.
     * @see Project#createServerSession(int, int, int)
     */
    public ServerSession(Project project, int initialNumberOfPooledConnection, int minNumberOfPooledConnection, int maxNumberOfPooledConnection) {
        this(project, new ConnectionPolicy(DEFAULT_POOL), initialNumberOfPooledConnection, minNumberOfPooledConnection, maxNumberOfPooledConnection, null, null);
    }

    /**
     * INTERNAL:
     * Create and return a new server session.
     * @see Project#createServerSession(int, int)
     *
     * @param project the project associated with this session
     * @param minNumberOfPooledConnection the minimum number of connections in the pool
     * @param maxNumberOfPooledConnection the maximum number of connections in the pool
     * @param readLogin the login used to create the read connection pool
     */
    public ServerSession(Project project, int minNumberOfPooledConnection, int maxNumberOfPooledConnection, Login readLogin) {
        this(project, minNumberOfPooledConnection, maxNumberOfPooledConnection, readLogin, null);
    }

    /**
     * INTERNAL:
     * Create and return a new server session.
     * @see Project#createServerSession(int, int)
     */
    public ServerSession(Project project, int minNumberOfPooledConnection, int maxNumberOfPooledConnection, Login readLogin, Login sequenceLogin) {
        this(project, new ConnectionPolicy(DEFAULT_POOL), ConnectionPool.INITIAL_CONNECTIONS, minNumberOfPooledConnection, maxNumberOfPooledConnection, readLogin, sequenceLogin);
    }

    /**
     * INTERNAL:
     * Create and return a new server session.
     * <p>
     * Configure the initial, min and max number of connections for the default pool.
     * <p>
     * Configure the default connection policy to be used.
     * This policy is used on the "acquireClientSession()" protocol.
     * <p>
     * Use the login from the project for the write pool. Use the passed
     * in login for the read pool, if specified, or the project login if not.
     * Use the sequenceLogin, if specified, for creating a connection pool
     * to be used by sequencing through SequencingConnectionHandler
     * sequenceLogin *MUST*:
     * <br>1. specify *NON-JTS* connections (such as NON_JTS driver or read-only datasource);
     * <br>2. sequenceLogin.shouldUseExternalTransactionController()==false
     *
     * @param project the project associated with this session
     * @param defaultConnectionPolicy the default connection policy to be used
     * @param initialNumberOfPooledConnections the minimum number of connections in the pool
     * @param minNumberOfPooledConnections the minimum number of connections in the pool
     * @param maxNumberOfPooledConnections the maximum number of connections in the pool
     * @param readLogin the login used to create the read connection pool
     * @param sequenceLogin the login used to create a connection pool for sequencing
     * 
     * @see Project#createServerSession(int, int)
     */
    public ServerSession(Project project, ConnectionPolicy defaultConnectionPolicy, int initialNumberOfPooledConnections, int minNumberOfPooledConnections, int maxNumberOfPooledConnections, Login readLogin, Login sequenceLogin) {
        super(project);
        this.connectionPools = new HashMap(10);
        this.defaultConnectionPolicy = defaultConnectionPolicy;
        this.maxNumberOfNonPooledConnections = 50;
        this.numberOfNonPooledConnectionsUsed = 0;

        // Configure the default write connection pool.
        ConnectionPool pool = null;
        if (project.getDatasourceLogin().shouldUseExternalConnectionPooling()) {
            pool = new ExternalConnectionPool(DEFAULT_POOL, project.getDatasourceLogin(), this);
        } else {
            pool = new ConnectionPool(DEFAULT_POOL, project.getDatasourceLogin(), initialNumberOfPooledConnections, minNumberOfPooledConnections, maxNumberOfPooledConnections, this);
        }
        this.connectionPools.put(DEFAULT_POOL, pool);
        
        // If a read login was not used, then share the same connection pool for reading and writing.
        if (readLogin != null) {
            setReadConnectionPool(readLogin);
        } else {
            setReadConnectionPool(pool);
        }

        if (sequenceLogin != null) {
            // Even if getSequencingControl().setShouldUseSeparateConnection(true) is specified,
            // SequencingConnectionPool is NOT created unless the session has at least one Sequence object
            // that requires transaction.
            getSequencingControl().setShouldUseSeparateConnection(true);
            getSequencingControl().setLogin(sequenceLogin);
        }
    }

    /**
     * INTERNAL:
     * Create and return a new server session.
     * @see Project#createServerSession(ConnectionPolicy)
     */
    public ServerSession(Project project, ConnectionPolicy defaultConnectionPolicy) {
        this(project, defaultConnectionPolicy, null);
    }

    /**
     * INTERNAL:
     * Create and return a new server session.
     * @see Project#createServerSession(ConnectionPolicy)
     */
    public ServerSession(Project project, ConnectionPolicy defaultConnectionPolicy, Login readLogin) {
        this(project, defaultConnectionPolicy, readLogin, null);
    }

    /**
     * INTERNAL:
     * Create and return a new server session.
     * @see Project#createServerSession(ConnectionPolicy)
     */
    public ServerSession(Project project, ConnectionPolicy defaultConnectionPolicy, Login readLogin, Login sequenceLogin) {
        this(project, defaultConnectionPolicy, ConnectionPool.INITIAL_CONNECTIONS, ConnectionPool.MIN_CONNECTIONS, ConnectionPool.MAX_CONNECTIONS, readLogin, sequenceLogin);
    }

    /**
     * INTERNAL:
     * Allocate the client's connection resource.
     */
    public void acquireClientConnection(ClientSession clientSession) throws DatabaseException, ConcurrencyException {
        if (clientSession.getConnectionPolicy().isPooled()) {
            ConnectionPool pool = this.connectionPools.get(clientSession.getConnectionPolicy().getPoolName());
            Accessor accessor = pool.acquireConnection();
            clientSession.addWriteConnection(pool.getName(), accessor);
        } else {
            if (this.maxNumberOfNonPooledConnections != NO_MAX) {
                synchronized (this) {
                    while (this.numberOfNonPooledConnectionsUsed >= this.maxNumberOfNonPooledConnections) {
                        try {
                            wait();// Notify is called when connections are released.
                        } catch (InterruptedException exception) {
                            throw ConcurrencyException.waitFailureOnServerSession(exception);
                        }
                    }
                    this.numberOfNonPooledConnectionsUsed++;
                }
            }
            Accessor accessor = clientSession.getLogin().buildAccessor();
            clientSession.connect(accessor);
            clientSession.addWriteConnection(ServerSession.NOT_POOLED, accessor);
        }
    }

    /**
     * PUBLIC:
     * Return a client session for this server session.
     * Each user/client connected to this server session must acquire there own client session
     * to communicate to the server through.
     * This method allows for a client session to be acquired sharing the same login as the server session.
     */
    public ClientSession acquireClientSession() throws DatabaseException {
        return acquireClientSession(getDefaultConnectionPolicy());
    }

    /**
     * PUBLIC:
     * Return a client session for this server session.
     * Each user/client connected to this server session must acquire there own client session
     * to communicate to the server through.
     * This method allows for a client session to be acquired sharing the same login as the server session.
     * The properties set into the client session at construction time, before postAcquireClientSession is risen.
     */
    public ClientSession acquireClientSession(Map properties) throws DatabaseException {
        return acquireClientSession(getDefaultConnectionPolicy(), properties);
    }

    /**
     * PUBLIC:
     * Return a client session for this server session.
     * Each user/client connected to this server session must acquire there own client session
     * to communicate to the server through.
     * This method allows for a client session to be acquired sharing its connection from a pool
     * of connection allocated on the server session.
     * By default this uses a lazy connection policy.
     */
    public ClientSession acquireClientSession(String poolName) throws DatabaseException {
        return acquireClientSession(new ConnectionPolicy(poolName));
    }

    /**
     * PUBLIC:
     * Return a client session for this server session.
     * Each user/client connected to this server session must acquire there own client session
     * to communicate to the server through.
     * This method allows for a client session to be acquired sharing its connection from a pool
     * of connection allocated on the server session.
     * By default this uses a lazy connection policy.
     * The properties set into the client session at construction time, before postAcquireClientSession is risen.
     */
    public ClientSession acquireClientSession(String poolName, Map properties) throws DatabaseException {
        return acquireClientSession(new ConnectionPolicy(poolName), properties);
    }

    /**
     * PUBLIC:
     * Return a client session for this server session.
     * Each user/client connected to this server session must acquire there own client session
     * to communicate to the server through.
     * The client must provide its own login to use, and the client session returned
     * will have its own exclusive database connection.  This connection will be used to perform
     * all database modification for all units of work acquired from the client session.
     * By default this does not use a lazy connection policy.
     */
    public ClientSession acquireClientSession(Login login) throws DatabaseException {
        return acquireClientSession(new ConnectionPolicy(login));
    }

    /**
     * PUBLIC:
     * Return a client session for this server session.
     * Each user/client connected to this server session must acquire there own client session
     * to communicate to the server through.
     * The client must provide its own login to use, and the client session returned
     * will have its own exclusive database connection.  This connection will be used to perform
     * all database modification for all units of work acquired from the client session.
     * By default this does not use a lazy connection policy.
     * The properties set into the client session at construction time, before postAcquireClientSession is risen.
     */
    public ClientSession acquireClientSession(Login login, Map properties) throws DatabaseException {
        return acquireClientSession(new ConnectionPolicy(login), properties);
    }

    /**
     * PUBLIC:
     * Return a client session for this server session.
     * The connection policy specifies how the client session's connection will be acquired.
     */
    public ClientSession acquireClientSession(ConnectionPolicy connectionPolicy) throws DatabaseException, ValidationException {
        return acquireClientSession(connectionPolicy, null);
    }
    
    /**
     * PUBLIC:
     * Return a client session for this server session.
     * The connection policy specifies how the client session's connection will be acquired.
     * The properties set into the client session at construction time, before postAcquireClientSession is risen.
     */
    public ClientSession acquireClientSession(ConnectionPolicy connectionPolicy, Map properties) throws DatabaseException, ValidationException {
        if (!isConnected()) {
            throw ValidationException.loginBeforeAllocatingClientSessions();
        }
        if (!connectionPolicy.isPooled() && (connectionPolicy.getLogin() == null)) {
            //the user has passed in a connection policy with no login info. Use the 
            //default info from the default connection policy
            connectionPolicy.setPoolName(getDefaultConnectionPolicy().getPoolName());
            connectionPolicy.setLogin(getDefaultConnectionPolicy().getLogin());
        }
        if (connectionPolicy.isPooled()) {
            ConnectionPool pool = this.connectionPools.get(connectionPolicy.getPoolName());
            if (pool == null) {
                throw ValidationException.poolNameDoesNotExist(connectionPolicy.getPoolName());
            }
            connectionPolicy.setLogin(pool.getLogin());
        }
        ClientSession client = null;
        if (getProject().hasIsolatedClasses()) {
            if (connectionPolicy.isExclusive()) {
                client = new ExclusiveIsolatedClientSession(this, connectionPolicy, properties);
            } else {
                client = new IsolatedClientSession(this, connectionPolicy, properties);
            }
        } else {
            if (connectionPolicy.isExclusiveIsolated()) {
                throw ValidationException.clientSessionCanNotUseExclusiveConnection();
            } else if(connectionPolicy.isExclusiveAlways()) {
                client = new ExclusiveIsolatedClientSession(this, connectionPolicy, properties);
            } else {
                client = new ClientSession(this, connectionPolicy, properties);
            }
        }
        if (isFinalizersEnabled()) {
            client.registerFinalizer();
        }
        if (!connectionPolicy.isLazy()) {
            acquireClientConnection(client);
        }
        if (shouldLog(SessionLog.FINER, SessionLog.CONNECTION)) {
            log(SessionLog.FINER, SessionLog.CONNECTION, "client_acquired", String.valueOf(System.identityHashCode(client)));
        }

        return client;
    }

    /**
     * INTERNAL:
     * Acquires a special historical session for reading objects as of a past time.
     */
    public org.eclipse.persistence.sessions.Session acquireHistoricalSession(org.eclipse.persistence.history.AsOfClause clause) throws ValidationException {
        throw ValidationException.cannotAcquireHistoricalSession();
    }

    /**
     * PUBLIC:
     * Return a unit of work for this session.
     * The unit of work is an object level transaction that allows
     * a group of changes to be applied as a unit.
     * First acquire a client session as server session does not allow direct units of work.
     *
     * @see UnitOfWorkImpl
     */
    public UnitOfWorkImpl acquireUnitOfWork() {
        return acquireClientSession().acquireUnitOfWork();
    }

    /**
     * PUBLIC:
     * Add the connection pool.
     * Connections are pooled to share and restrict the number of database connections.
     */
    public void addConnectionPool(String poolName, Login login, int minNumberOfConnections, int maxNumberOfConnections) throws ValidationException {
        if (minNumberOfConnections > maxNumberOfConnections) {
            throw ValidationException.maxSizeLessThanMinSize();
        }
        if (isConnected()) {
            throw ValidationException.poolsMustBeConfiguredBeforeLogin();
        }
        ConnectionPool pool = null;
        if (login.shouldUseExternalConnectionPooling()) {
            pool = new ExternalConnectionPool(poolName, login, this);
        } else {
            pool = new ConnectionPool(poolName, login, minNumberOfConnections, maxNumberOfConnections, this);
        }
        addConnectionPool(pool);
    }

    /**
     * PUBLIC:
     * Connection are pooled to share and restrict the number of database connections.
     */
    public void addConnectionPool(ConnectionPool pool) {
        pool.setOwner(this);
        getConnectionPools().put(pool.getName(), pool);

    }

    /**
     * INTERNAL:
     * Return a read connection from the read pool.
     * Note that depending on the type of pool this may be a shared or exclusive connection.
     * Each query execution is assigned a read connection.
     */
    public Accessor allocateReadConnection() {
        Accessor connection = this.readConnectionPool.acquireConnection();
        //if connection is using external connection pooling then the event will be risen right after it connects.
        if (!connection.usesExternalConnectionPooling()) {
            if (this.eventManager != null) {
                this.eventManager.postAcquireConnection(connection);
            }
        }
        return connection;
    }

    /**
     * INTERNAL:
     * Startup the server session, also startup all of the connection pools.
     */
    @Override
    public void connect() {
        // make sure pools correspond to their logins
        updateStandardConnectionPools();
        // Configure the read pool
        this.readConnectionPool.startUp();
        setAccessor(allocateReadConnection());
        releaseReadConnection(getAccessor());
    
        for (ConnectionPool pool : getConnectionPools().values()) {
            pool.startUp();
        }
    }

    /**
     * INTERNAL:
     * Disconnect the accessor only.
     */
    @Override
    public void disconnect() throws DatabaseException {
        try {
            super.disconnect();
        } catch (DatabaseException ex) {
            // the exception caused by attempt to disconnect session's accessor - ignore it.
        }
    }
    
    /**
     * INTERNAL:
     * Return the connections to use for the query execution.
     */
    @Override
    public List<Accessor> getAccessors(Call call, AbstractRecord translationRow, DatabaseQuery query) {
        // Check for partitioning.
        List<Accessor> accessors = null;
        if (query.getPartitioningPolicy() != null) {
            accessors = query.getPartitioningPolicy().getConnectionsForQuery(this, query, translationRow);
            if (accessors != null) {
                return accessors;
            }
        }
        if ((query.getDescriptor() != null) && (query.getDescriptor().getPartitioningPolicy() != null)) {
            accessors = query.getDescriptor().getPartitioningPolicy().getConnectionsForQuery(this, query, translationRow);  
            if (accessors != null) {
                return accessors;
            }              
        }
        if (this.partitioningPolicy != null) {
            accessors = this.partitioningPolicy.getConnectionsForQuery(this, query, translationRow);
            if (accessors != null) {
                return accessors;
            }
        }
        if (accessors == null) {
            accessors = new ArrayList(1);
            accessors.add(this.readConnectionPool.acquireConnection());
        }
        return accessors;
    }
    
    /**
     * INTERNAL:
     * Execute the call on the correct connection accessor.
     * By default the server session executes calls using is read connection pool.
     * A connection is allocated for the execution of the query, then released back to the pool.
     * If partitioning is used the partition policy can use a different connection pool, or even
     * execute the call on multiple connections.
     */
    @Override
    public Object executeCall(Call call, AbstractRecord translationRow, DatabaseQuery query) throws DatabaseException {
        RuntimeException exception = null;
        Object result = null;
        boolean accessorAllocated = false;
        if (query.getAccessors() == null) {
            List<Accessor> accessors = getAccessors(call, translationRow, query);
            query.setAccessors(accessors);
            if (this.eventManager != null) {
                for (Accessor accessor : accessors) {
                    //if connection is using external connection pooling then the event will be risen right after it connects.
                    if (!accessor.usesExternalConnectionPooling()) {
                        this.eventManager.postAcquireConnection(accessor);
                    }
                }
            }
            accessorAllocated = true;
        }
        try {
            result = basicExecuteCall(call, translationRow, query);
        } catch (RuntimeException caughtException) {
            exception = caughtException;
        } finally {
            // EL Bug 244241 - connection not released on query timeout when cursor used
            // Don't release the cursoredStream connection until Stream is closed 
            // or unless an exception occurred executing the call.
            if (call.isFinished() || exception != null) {
                if (accessorAllocated) {
                    try {
                        releaseConnectionAfterCall(query);
                    } catch (RuntimeException releaseException) {
                        if (exception == null) {
                            throw releaseException;
                        }
                        //else ignore
                    }
                }
            } else {
                if (query.isObjectLevelReadQuery()) {
                    ((DatabaseCall)call).setHasAllocatedConnection(accessorAllocated);
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
        RuntimeException exception = null;
        for (Accessor accessor : query.getAccessors()) {
            //if connection is using external connection pooling then the event has been risen right before it disconnected.
            try {
                if (!accessor.usesExternalConnectionPooling()) {
                    preReleaseConnection(accessor);
                }
                accessor.getPool().releaseConnection(accessor);
            } catch (RuntimeException ex) {
                if (exception == null) {
                    exception = ex;
                }
            }
        }
        query.setAccessors(null);
        if (exception != null) {
            throw exception;
        }
    }

    /**
     * PUBLIC:
     * Return the results from executing the database query.
     * The query arguments are passed in as a List of argument values in the same order as the query arguments.
     */
    public Object executeQuery(DatabaseQuery query, List argumentValues) throws DatabaseException {
        if (query == null) {
            throw QueryException.queryNotDefined();
        }
        query.checkDescriptor(this);
        ClassDescriptor descriptor = query.getDescriptor();
        AbstractRecord row = query.rowFromArguments(argumentValues, this);
        if (query.isObjectBuildingQuery() && descriptor != null && !descriptor.getCachePolicy().isSharedIsolation()) {
            ClientSession client = acquireClientSession();
            Object result = null;
            try {
                result = client.executeQuery(query, row);
            } finally {
                client.release();
            }
            return result;
        }
        return super.executeQuery( query,  row);
    }

    /**
     * PUBLIC:
     * Return the pool by name.
     */
    public ConnectionPool getConnectionPool(String poolName) {
        return this.connectionPools.get(poolName);
    }

    /**
     * INTERNAL:
     * Connection are pooled to share and restrict the number of database connections.
     */
    public Map<String, ConnectionPool> getConnectionPools() {
        return connectionPools;
    }

    /**
     * PUBLIC:
     * The default connection policy is used by default by the acquireClientConnection() protocol.
     * By default it is a connection pool with min 5 and max 10 lazy pooled connections.
     */
    public ConnectionPolicy getDefaultConnectionPolicy() {
        if (this.defaultConnectionPolicy == null) {
            this.defaultConnectionPolicy = new ConnectionPolicy(DEFAULT_POOL);
        }
        return this.defaultConnectionPolicy;
    }

    /**
     * PUBLIC:
     * Return the default connection pool.
     */
    public ConnectionPool getDefaultConnectionPool() {
        return getConnectionPool(DEFAULT_POOL);
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
        if (query.isObjectLevelModifyQuery()) {
            throw QueryException.invalidQueryOnServerSession(query);
        }
        return this;
    }

    /**
     * PUBLIC:
     * Return the number of non-pooled database connections allowed.
     * This can be enforced to make up for the resource limitation of most JDBC drivers and database clients.
     * By default this is 50.
     */
    public int getMaxNumberOfNonPooledConnections() {
        return maxNumberOfNonPooledConnections;
    }

    /**
     * INTERNAL:
     * Return the current number of non-pooled connections in use.
     */
    public int getNumberOfNonPooledConnectionsUsed() {
        return numberOfNonPooledConnectionsUsed;
    }

    /**
     * INTERNAL:
     * Return the login for the read connection.  Used by the platform autodetect feature
     */
    protected Login getReadLogin(){
        return this.readConnectionPool.getLogin();
    }


    /**
     * PUBLIC:
     * Return the read connection pool.
     * The read connection pool handles allocating connection for read queries.
     */
    public ConnectionPool getReadConnectionPool() {
        return readConnectionPool;
    }

    /**
     * PUBLIC:
     * Return if this session has been connected to the database.
     */
    @Override
    public boolean isConnected() {
        if (this.readConnectionPool == null) {
            return false;
        }

        return this.readConnectionPool.isConnected();
    }

    /**
     * INTERNAL:
     * Return if this session is a server session.
     */
    @Override
    public boolean isServerSession() {
        return true;
    }

    /**
     * PUBLIC:
     * Shutdown the server session, also shutdown all of the connection pools.
     */
    @Override
    public void logout() {
        try {
            super.logout();
        } finally {    
            this.readConnectionPool.shutDown();
    
            for (Iterator poolsEnum = getConnectionPools().values().iterator(); poolsEnum.hasNext();) {
                ((ConnectionPool)poolsEnum.next()).shutDown();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Release any invalid connection in the client session.
     */
    public void releaseInvalidClientSession(ClientSession clientSession) throws DatabaseException {
        for (Iterator<Accessor> accessors = clientSession.getWriteConnections().values().iterator(); accessors.hasNext(); ) {
            Accessor accessor = accessors.next();
            if (!accessor.isValid()) {
                if (clientSession.getConnectionPolicy().isPooled()) {
                    try {
                        accessor.getPool().releaseConnection(accessor);
                    } catch (Exception ignore) {}
                } else {
                    if (!accessor.usesExternalConnectionPooling()) {
                        clientSession.disconnect(accessor);
                    } else {
                        accessor.closeConnection();
                    }
                    if (this.maxNumberOfNonPooledConnections != NO_MAX) {
                        synchronized (this) {
                            this.numberOfNonPooledConnectionsUsed--;
                            notify();
                        }
                    }
                }
                accessors.remove();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Release the clients connection resource.
     */
    public void releaseClientSession(ClientSession clientSession) throws DatabaseException {
        if (clientSession.getConnectionPolicy().isPooled()) {
            for (Accessor accessor : clientSession.getWriteConnections().values()) {
                //if connection is using external connection pooling then the event has been risen right before it disconnected.
                if(!accessor.usesExternalConnectionPooling()) {
                    clientSession.preReleaseConnection(accessor);
                }
                accessor.getPool().releaseConnection(accessor);
            }
            clientSession.setWriteConnections(null);
        } else {
            for (Accessor accessor : clientSession.getWriteConnections().values()) {
                //if connection is using external connection pooling then the event has been risen right before it disconnected.
                if(!accessor.usesExternalConnectionPooling()) {
                    clientSession.preReleaseConnection(accessor);
                    try {
                        clientSession.disconnect(accessor);
                    } catch (DatabaseException ex) {
                        // ignore - connection is thrown away.
                    }
                } else {
                    // should be already closed - but just in case it's still connected (and the event will risen before connection is closed).
                    accessor.closeConnection();
                }
            }
            clientSession.setWriteConnections(null);
            if (this.maxNumberOfNonPooledConnections != NO_MAX) {
                synchronized (this) {
                    this.numberOfNonPooledConnectionsUsed--;
                    notify();
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Release the read connection back into the read pool.
     */
    public void releaseReadConnection(Accessor connection) {
        //if connection is using external connection pooling then the event has been risen right before it disconnected.
        if (!connection.usesExternalConnectionPooling()) {
            if (this.eventManager != null) {
                this.eventManager.preReleaseConnection(connection);
            }
        }
        this.readConnectionPool.releaseConnection(connection);
    }
    
    /**
     * INTERNAL:
     * Connection are pooled to share and restrict the number of database connections.
     */
    public void setConnectionPools(Map<String, ConnectionPool> connectionPools) {
        this.connectionPools = connectionPools;
    }

    /**
     * PUBLIC:
     * The default connection policy is used by default by the acquireClientConnection() protocol.
     * By default it is a connection pool with min 5 and max 10 lazy pooled connections.
     */
    public void setDefaultConnectionPolicy(ConnectionPolicy defaultConnectionPolicy) {
        this.defaultConnectionPolicy = defaultConnectionPolicy;
    }

    /**
     * PUBLIC:
     * Creates and adds "default" connection pool using default parameter values
     */
    public void setDefaultConnectionPool() {
        addConnectionPool(DEFAULT_POOL, getDatasourceLogin(), ConnectionPool.MIN_CONNECTIONS, ConnectionPool.MAX_CONNECTIONS);
    }

    /**
     * PUBLIC:
     * Set the number of non-pooled database connections allowed.
     * This can be enforced to make up for the resource limitation of most JDBC drivers and database clients.
     * By default this is 50.
     */
    public void setMaxNumberOfNonPooledConnections(int maxNumberOfNonPooledConnections) {
        this.maxNumberOfNonPooledConnections = maxNumberOfNonPooledConnections;
    }

    /**
     * INTERNAL:
     * Set the current number of connections being used that are not from a connection pool.
     * @param int
     */
    public void setNumberOfNonPooledConnectionsUsed(int numberOfNonPooledConnectionsUsed) {
        this.numberOfNonPooledConnectionsUsed = numberOfNonPooledConnectionsUsed;
    }

    /**
     * PUBLIC:
     * Set the read connection pool.
     * The read connection pool handles allocating connection for read queries.
     * If external connection pooling is used, an external connection pool will be used by default.
     */
    public void setReadConnectionPool(ConnectionPool readConnectionPool) {
        if (isConnected()) {
            throw ValidationException.cannotSetReadPoolSizeAfterLogin();
        }
        this.readConnectionPool = readConnectionPool;
        this.readConnectionPool.setOwner(this);
    }

    /**
     * PUBLIC:
     * Creates and sets the new read connection pool.
     * By default the same connection pool is used for read and write,
     * this allows a different login/pool to be used for reading.
     * By default 32 min/max connections are used in the pool with an initial of 1 connection.
     */
    public void setReadConnectionPool(Login readLogin) throws ValidationException {
        if (isConnected()) {
            throw ValidationException.poolsMustBeConfiguredBeforeLogin();
        }
        ConnectionPool pool = null;
        if (readLogin.shouldUseExternalConnectionPooling()) {
            pool = new ExternalConnectionPool("read", readLogin, this);
        } else {
            pool = new ConnectionPool("read", readLogin, this);
        }
        this.readConnectionPool = pool;
    }

    /**
     * INTERNAL:
     * Set isSynchronized flag to indicate that this session is synchronized.
     * The method is ignored on ServerSession and should never be called.
     */
    @Override
    public void setSynchronized(boolean synched) {
    }

    /**
     * INTERNAL:
     * Updates standard connection pools. Should not be called after session is connected.
     * This is needed in case of pools' logins been altered after the pool has been created
     * (SessionManager does that)
     * All pools should be re-created in case their type doesn't match their login.
     * In addition, sequenceConnectionPool should be removed in case its login
     * has shouldUseExternaltransactionController()==true (see setSequenceConnectionPool)
     */
    protected void updateStandardConnectionPools() {
        if (getDefaultConnectionPool() != null) {
            if (getDefaultConnectionPool().isThereConflictBetweenLoginAndType()) {
                setDefaultConnectionPool();
            }
        }

        if (this.readConnectionPool != null) {
            if (this.readConnectionPool.isThereConflictBetweenLoginAndType()) {
                setReadConnectionPool(this.readConnectionPool.getLogin());
            }
        }
    }

    /**
     * PUBLIC:
     * Configure the read connection pool.
     * The read connection pool handles allocating connection for read queries.
     */
    public void useExclusiveReadConnectionPool(int minNumerOfConnections, int maxNumerOfConnections) {
        setReadConnectionPool(new ConnectionPool("read", getDatasourceLogin(), minNumerOfConnections, maxNumerOfConnections, this));
    }

    /**
     * PUBLIC:
     * Configure the read connection pool.
     * The read connection pool handles allocating connection for read queries.
     */
    public void useExclusiveReadConnectionPool(int initialNumberOfConnections, int minNumerOfConnections, int maxNumerOfConnections) {
        setReadConnectionPool(new ConnectionPool("read", getDatasourceLogin(), initialNumberOfConnections, minNumerOfConnections, maxNumerOfConnections, this));
    }

    /**
     * PUBLIC:
     * Configure the read connection pool.
     * The read connection pool handles allocating connection for read queries.
     */
    public void useExternalReadConnectionPool() {
        setReadConnectionPool(new ExternalConnectionPool("read", getDatasourceLogin(), this));
    }

    /**
     * PUBLIC:
     * Configure the read connection pool.
     * The read connection pool handles allocating connection for read queries.
     * If external connection pooling is used, an external connection pool will be used by default.
     * This API uses a ReadConnectionPool which shares read connections.
     * Some JDBC drivers may not support concurrent access to a connection, or have poor concurrency,
     * so an exclusive read connection pool is normally recommended.
     * @see #useExclusiveReadConnectionPool(int, int)
     */
    public void useReadConnectionPool(int minNumerOfConnections, int maxNumerOfConnections) {
        setReadConnectionPool(new ReadConnectionPool("read", getDatasourceLogin(), minNumerOfConnections, maxNumerOfConnections, this));
    }

    /**
     * PUBLIC:
     * Configure the read connection pool.
     * The read connection pool handles allocating connection for read queries.
     * If external connection pooling is used, an external connection pool will be used by default.
     * This API uses a ReadConnectionPool which shares read connections.
     * Some JDBC drivers may not support concurrent access to a connection, or have poor concurrency,
     * so an exclusive read connection pool is normally recommended.
     * @see #useExclusiveReadConnectionPool(int, int, int)
     */
    public void useReadConnectionPool(int initialNumerOfConnections, int minNumerOfConnections, int maxNumerOfConnections) {
        setReadConnectionPool(new ReadConnectionPool("read", getDatasourceLogin(), initialNumerOfConnections,
                minNumerOfConnections, maxNumerOfConnections, this));
    }

    /**
     * INTERNAL:
     * This method will be used to update the query with any settings required
     * For this session.  It can also be used to validate execution.
     */
    @Override
    public void validateQuery(DatabaseQuery query) {
        if (query.isObjectLevelReadQuery() && ((query.getDescriptor().getCachePolicy().isIsolated())
                || ((ObjectLevelReadQuery)query).shouldUseExclusiveConnection())) {
            throw QueryException.isolatedQueryExecutedOnServerSession();
        }
    }

    /**
     * INTERNAL:
     * Return SequencingServer object owned by the session.
     */
    public SequencingServer getSequencingServer() {
        return getSequencingHome().getSequencingServer();
    }
}
