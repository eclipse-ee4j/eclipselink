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

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;

/**
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
 * <ul>
 *    <li> Connection pooling.
 *    <li> Reading objects and maintaining the object cache.
 *    <li> Brokering client sessions.
 *    <li> Requiring the UnitOfWork to be used for modification.
 * </ul>
 * 
 * @see Server
 * @see ClientSession
 * @see UnitOfWork
 */
public interface Server extends org.eclipse.persistence.sessions.DatabaseSession {

    /**
     * PUBLIC:
     * Return a client session for this server session.
     * Each user/client connected to this server session must acquire there own client session
     * to communicate to the server through.
     * This method allows for a client session to be acquired sharing the same login as the server session.
     */
    public ClientSession acquireClientSession() throws DatabaseException;

    /**
     * PUBLIC:
     * Return a client session for this server session.
     * Each user/client connected to this server session must acquire there own client session
     * to communicate to the server through.
     * This method allows for a client session to be acquired sharing its connection from a pool
     * of connection allocated on the server session.
     * By default this uses a lazy connection policy.
     */
    public ClientSession acquireClientSession(String poolName);

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
    public ClientSession acquireClientSession(Login login);

    /**
     * PUBLIC:
     * Return a client session for this server session.
     * The connection policy specifies how the client session's connection will be acquired.
     */
    public ClientSession acquireClientSession(ConnectionPolicy connectionPolicy);

    /**
     * PUBLIC:
     * Add the connection pool.
     * Connections are pooled to share and restrict the number of database connections.
     */
    public void addConnectionPool(String poolName, Login login, int minNumberOfConnections, int maxNumberOfConnections);

    /**
     * PUBLIC:
     * Connection are pooled to share and restrict the number of database connections.
     */
    public void addConnectionPool(ConnectionPool pool);

    /**
     * PUBLIC:
     * Return the pool by name.
     */
    public ConnectionPool getConnectionPool(String poolName);

    /**
     * PUBLIC:
     * The default connection policy is used by default by the acquireClientConnection() protocol.
     * By default it uses the default connection pool.
     */
    public ConnectionPolicy getDefaultConnectionPolicy();

    /**
     * PUBLIC:
     * Return the default connection pool.
     */
    public ConnectionPool getDefaultConnectionPool();

    /**
     * PUBLIC:
     * Return the number of non-pooled database connections allowed.
     * This can be enforced to make up for the resource limitation of most JDBC drivers and database clients.
     * By default this is 50.
     */
    public int getMaxNumberOfNonPooledConnections();


    /**
     * PUBLIC:
     * Handles allocating connections for read queries.
     * <p>
     * By default a read connection pool is not used, the default connection pool is used for reading.
     * <p> The read connection pool is not used while in transaction.
     * @see #setReadConnectionPool(ConnectionPool)
     * @see #useExclusiveReadConnectionPool
     * @see #useExternalReadConnectionPool
     * @see #useReadConnectionPool
     */
    public ConnectionPool getReadConnectionPool();

    /**
     * PUBLIC:
     * Set the login.
     */
    public void setDatasourceLogin(Login login);

    /**
     * PUBLIC:
     * The default connection policy is used by default by the acquireClientConnection() protocol.
     * By default it uses the default connection pool.
     */
    public void setDefaultConnectionPolicy(ConnectionPolicy defaultConnectionPolicy);

    /**
     * PUBLIC:
     * Set the number of non-pooled database connections allowed.
     * This can be enforced to make up for the resource limitation of most JDBC drivers and database clients.
     * By default this is 50.
     */
    public void setMaxNumberOfNonPooledConnections(int maxNumberOfNonPooledConnections);


    /**
     * PUBLIC:
     * Sets the read connection pool directly.
     * <p>
     * Either {@link #useExclusiveReadConnectionPool} or {@link #useExternalReadConnectionPool} is
     * called in the constructor.  For a connection pool using concurrent reading
     * {@link #useReadConnectionPool} should be called on a new instance of <code>this</code>.
     *
     * @throws ValidationException if already connected
     */
    public void setReadConnectionPool(ConnectionPool readConnectionPool);

    /**
     * PUBLIC:
     * Sets the read connection pool to be a separate exclusive <code>ConnectionPool</code>
     * with the minimum and maximum number of connections.
     * <p>
     * A separate read connection pool is not used by default, by default the default connection pool is used for reading.
     * A separate read connection pool can be used to dedicate a pool of connections only for reading.
     * It can also be used to use a non-JTA DataSource for reading to avoid JTA overhead,
     * or to use a different user login for reading.
     * 
     * @see #getReadConnectionPool
     * @see #setReadConnectionPool(ConnectionPool)
     * @see #useReadConnectionPool
     * @see #useExternalReadConnectionPool
     */
    public void useExclusiveReadConnectionPool(int minNumberOfConnections, int maxNumberOfConnections);

    /**
     * PUBLIC:
     * Sets the read connection pool to be a separate exclusive <code>ConnectionPool</code>
     * with the initial, minimum and maximum number of connections.
     * <p>
     * A separate read connection pool is not used by default, by default the default connection pool is used for reading.
     * A separate read connection pool can be used to dedicate a pool of connections only for reading.
     * It can also be used to use a non-JTA DataSource for reading to avoid JTA overhead,
     * or to use a different user login for reading.
     * 
     * @see #getReadConnectionPool
     * @see #setReadConnectionPool(ConnectionPool)
     * @see #useReadConnectionPool
     * @see #useExternalReadConnectionPool
     */
    public void useExclusiveReadConnectionPool(int initialNumberOfConnections, int minNumberOfConnections, int maxNumberOfConnections);

    /**
     * PUBLIC:
     * Sets the read connection pool to be an <code>ExternalConnectionPool</code>.
     * <p>
     * This type of connection pool will be created and configured automatically if
     * an external connection pooling is used.
     * 
     * @see #getReadConnectionPool
     * @see #setReadConnectionPool(ConnectionPool)
     * @see #useReadConnectionPool
     * @see #useExclusiveReadConnectionPool
     */
    public void useExternalReadConnectionPool();

    /**
     * PUBLIC:
     * Sets the read connection pool to be a separate shared <code>ConnectionPool</code>
     * with the minimum and maximum number of connections.
     * <p>
     * A separate read connection pool is not used by default, by default the default connection pool is used for reading.
     * A separate read connection pool can be used to dedicate a pool of connections only for reading.
     * It can also be used to use a non-JTA DataSource for reading to avoid JTA overhead,
     * or to use a different user login for reading.
     * <p>
     * Since read connections are not used for writing, multiple users can
     * theoretically use the same connection at the same time.
     * However some JDBC drivers do not allow this, or have poor concurrency when this is done.
     * <p>
     * Use this read connection pool to take advantage of concurrent reading.
     * <p>
     * @param minNumberOfConnections
     * @param maxNumberOfConnections As multiple readers can use the same connection
     * concurrently fewer connections are needed.
     * @see #getReadConnectionPool
     * @see #setReadConnectionPool(ConnectionPool)
     * @see #useExternalReadConnectionPool
     * @see #useExclusiveReadConnectionPool
     */
    public void useReadConnectionPool(int minNumberOfConnections, int maxNumberOfConnections);
    
    /**
     * PUBLIC:
     * Sets the read connection pool to be a separate shared <code>ConnectionPool</code>
     * with the minimum and maximum number of connections.
     * <p>
     * A separate read connection pool is not used by default, by default the default connection pool is used for reading.
     * A separate read connection pool can be used to dedicate a pool of connections only for reading.
     * It can also be used to use a non-JTA DataSource for reading to avoid JTA overhead,
     * or to use a different user login for reading.
     * <p>
     * Since read connections are not used for writing, multiple users can
     * theoretically use the same connection at the same time.
     * However some JDBC drivers do not allow this, or have poor concurrency when this is done.
     * <p>
     * Use this read connection pool to take advantage of concurrent reading.
     * <p>
     * @param initialNumberOfConnections connections connected at startup
     * @param minNumberOfConnections connections that are pooled
     * @param maxNumberOfConnections As multiple readers can use the same connection
     * concurrently fewer connections are needed.
     * @see #getReadConnectionPool
     * @see #setReadConnectionPool(ConnectionPool)
     * @see #useExternalReadConnectionPool
     * @see #useExclusiveReadConnectionPool
     */
    public void useReadConnectionPool(int initialNumberOfConnections, int minNumberOfConnections, int maxNumberOfConnections);

}
