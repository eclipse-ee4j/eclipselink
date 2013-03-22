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
 *     GYorke - non-bug update to set accessor in case of connection failure.  Thi
 *              will allow the retry code to function.
 ******************************************************************************/  
package org.eclipse.persistence.internal.databaseaccess;

import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.internal.sequencing.SequencingCallback;
import org.eclipse.persistence.internal.sequencing.SequencingCallbackFactory;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.sessions.server.ConnectionPool;

/**
 * INTERNAL:
 * <code>DatasourceAccessor</code> is an abstract implementation
 * of the <code>Accessor</code> interface providing common functionality to the concrete database and EIS accessors.
 * It is responsible for
 * connecting,
 * transactions,
 * call execution
 *
 * @see Call
 * @see Login
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 * 
 * 05/28/2008-1.0M8 Andrei Ilitchev. 
 *   - 224964: Provide support for Proxy Authentication through JPA.
 *     Added ConnectionCustomizer, also fixed  postConnect/preDisconnect ExternalConnection calls so that they called in case of reads, too.
 */
public abstract class DatasourceAccessor implements Accessor {

    /** Store the reference to the driver level connection. */
    protected Object datasourceConnection;

    /** Store the login information that connected this accessor. */
    protected Login login;

    /**
     * Keep track of the number of concurrent active calls.
     * This is used for connection pooling for loadbalancing and for external connection pooling.
     */
    protected int callCount;
    
    /**
     * Keep track of the number of the storedprocedure statement that being executed.
     */
    public int storedProcedureStatementsCount;
    /**
     * Keep track of the number of the read statement that being executed.
     */
    public int readStatementsCount;
    
    /**
     * Keep track of the number of the write statement that being executed.
     */
    public int writeStatementsCount;
    
    //Stores the number of executed read SQL statements
    public static final String READ_STATEMENTS_COUNT_PROPERTY = "Read_Statements_Count_Property";

    //Stores the number of executed write SQL statements
    public static final String WRITE_STATEMENTS_COUNT_PROPERTY = "Write_Statements_Count_Property";
    
    //Stores the number of executed store procedure statements
    public static final String STOREDPROCEDURE_STATEMENTS_COUNT_PROPERTY = "StoredProcedure_Statements_Count_Property";
    

    /** Keep track if the accessor is within a transaction context */
    protected boolean isInTransaction;

    /** Keep track of whether the accessor is "connected". */
    protected boolean isConnected;

    /** PERF: Cache platform to avoid gets (small but can add up). */
    /** This is also required to ensure all accessors for a session are using the same platform. */
    protected DatasourcePlatform platform;
    
    /**
     *  This attribute is used to determine if the connection should be returned to the pool or
     *  removed from the pool and closed.  It will be set to false if an exception occurs during
     *  Call execution.
     */
    protected boolean isValid;
    
    /**
     *  During (not external) transaction, SequencingManager may set SequencingCallback on the accessor,
     *  The callback's only method is called when transaction commits,
     *  after transaction is completed the callback is discarded.
     */
    protected transient SequencingCallback sequencingCallback;
    
    /**
     *  Used only in externalConnectionPooling case. Indicates which session is currently using connection.
     *  Allows to rise appropriate session's event when connection is already/still alive.
     *  Events will be risen when ClientSession or DatabaseSession acquires connection in the beginning
     *  and releases connection in the end of transaction (in afterCompletion in jta case).
     *  In case the session requires exclusive connection (ExclusiveIsolatedClientSession) the events
     *  will be risen every time the session acquires or releases connection -
     *  which is a rare event - the connection is kept for the duration of jta transaction,
     *  after jta transaction is completed the new connection is acquired on a first query execution
     *  and kept until the next beginTransaction call.
     *  In non-jta case the connection is acquired for session's life and release only by session's release.
     *  Note that the attribute is nullified only in one place - by closeConnection method.
     */
    protected transient AbstractSession currentSession;

    /**
     * PERF: Cache connection pooling flag.
     */
    protected boolean usesExternalConnectionPooling;
    
    /**
     * Back-door to allow isConnect checks.
     * Since we now support fail-over and retry, removing old isConnected usage which can
     * cause major performance issues (on Sybase), and minor ones in general.
     */
    public static boolean shouldCheckConnection = false;
    
    /**
     * Allows session-specific connection customization.
     */
    protected ConnectionCustomizer customizer;

    protected ConnectionPool pool;
    
    /**
     *    Default Constructor.
     */
    public DatasourceAccessor() {
        this.isInTransaction = false;
        this.callCount = 0;
        this.isConnected = false;
        this.isValid = true;
    }

    /**
     * Clone the accessor.
     */
    public Object clone() {
        try {
            DatasourceAccessor accessor = (DatasourceAccessor)super.clone();
            if(accessor.customizer != null) {
                accessor.customizer.setAccessor(accessor);
            }
            return accessor;
        } catch (CloneNotSupportedException exception) {
            throw new InternalError("clone not supported");
        }
    }

    /**
     * Called from beforeCompletion external transaction synchronization listener callback
     * to close the external connection corresponding to the completing external transaction.
     * Final sql calls could be sent through the connection by this method
     * before it closes the connection.
     */
    public void closeJTSConnection() {
        if (usesExternalTransactionController()) {
            this.isInTransaction = false;
            if (this.usesExternalConnectionPooling) {
                closeConnection();
            }
        }
    }

    /**
     * Set the transaction transaction status of the receiver.
     */
    protected void setIsInTransaction(boolean value) {
        isInTransaction = value;
    }

    /**
     * This should be set to false if a communication failure occurred during a call execution.  
     * In the case of an invalid accessor the Accessor will not be returned to the pool.
     */
    public void setIsValid(boolean isValid){
        this.isValid = isValid;
    }
    
    /**
     * Return the transaction status of the receiver.
     */
    public boolean isInTransaction() {
        return isInTransaction;
    }

    /**
     * Returns true if this Accessor can continue to be used.  This will be false if a communication
     * failure occurred during a call execution.  In the case of an invalid accessor the Accessor
     * will not be returned to the pool.
     */
    public boolean isValid(){
        return this.isValid;
    }
    
    /**
     * Return true if some external connection pool is in use.
     */
    public boolean usesExternalConnectionPooling() {
        return usesExternalConnectionPooling;
    }

    /**
     *    Begin a transaction on the database. If not using managed transaction begin a local transaction.
     */
    public void beginTransaction(AbstractSession session) throws DatabaseException {
        if (usesExternalTransactionController()) {
            if (session.isExclusiveConnectionRequired() && !this.isInTransaction && this.usesExternalConnectionPooling) {
                closeConnection();
            }
            this.isInTransaction = true;
            return;
        }

        session.log(SessionLog.FINER, SessionLog.TRANSACTION, "begin_transaction", (Object[])null, this);

        try {
            session.startOperationProfile(SessionProfiler.Transaction);
            incrementCallCount(session);
            basicBeginTransaction(session);
            this.isInTransaction = true;
        } finally {
            decrementCallCount();
            session.endOperationProfile(SessionProfiler.Transaction);
        }
    }

    /**
     * Begin the driver level transaction.
     */
    protected abstract void basicBeginTransaction(AbstractSession session);

    /**
     * Commit the driver level transaction.
     */
    protected abstract void basicCommitTransaction(AbstractSession session);

    /**
     * Rollback the driver level transaction.
     */
    protected abstract void basicRollbackTransaction(AbstractSession session);

    /**
     * Used for load balancing and external pooling.
     */
    public synchronized void decrementCallCount() {
        int count = this.callCount;
        // Avoid decrementing count if already zero, (failure before increment).
        if (count <= 0) {
            return;
        }
        this.callCount--;
        if (this.usesExternalConnectionPooling && (!this.isInTransaction) && (currentSession == null || !currentSession.isExclusiveConnectionRequired()) && (count == 1)) {
            try {
                closeConnection();
            } catch (DatabaseException ignore) {
                // Don't allow for errors to be masked by disconnect.
            }
        }
    }

    /**
     * Used for load balancing and external pooling.
     */
    public synchronized void incrementCallCount(AbstractSession session) {
        this.callCount++;

        if (this.callCount == 1) {
            // If the login is null, then this accessor has never been connected.
            if (this.login == null) {
                throw DatabaseException.databaseAccessorNotConnected();
            }

            // If the connection is no longer connected, it may have timed out.
            if (this.datasourceConnection != null) {
                if (shouldCheckConnection && !isConnected()) {
                    if (this.isInTransaction) {
                        throw DatabaseException.databaseAccessorNotConnected();
                    } else {
                        reconnect(session);
                    }
                }
            } else {
                // If ExternalConnectionPooling is used, the connection can be re-established.
                if (this.usesExternalConnectionPooling) {
                    reconnect(session);
                    session.postAcquireConnection(this);
                    currentSession = session;
                } else {
                    throw DatabaseException.databaseAccessorNotConnected();
                }
            }
        }
    }

    /**
     * Reset statement count.
     */
    public void reset() {
        this.readStatementsCount = 0;
        this.writeStatementsCount = 0;
        this.storedProcedureStatementsCount = 0;
    }

    /**
     * Connect to the database.
     * Exceptions are caught and re-thrown as EclipseLink exceptions.
     */
    protected void connectInternal(Login login, AbstractSession session) throws DatabaseException {
        try{
            this.datasourceConnection = login.connectToDatasource(this, session);
            this.isConnected = true;
            if(this.customizer != null) {
                customizer.customize();
            }
        }catch (DatabaseException ex){
            //Set the accessor to ensure the retry code has an opportunity to retry.
            ex.setAccessor(this);
            throw ex;
        }
    }

    /**
     * Set whether the accessor has a connection to the "data store".
     */
    protected void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    /**
     * Used for load balancing and external pooling.
     */
    protected void setCallCount(int callCount) {
        this.callCount = callCount;
    }

    /**
     * Used for load balancing and external pooling.
     */
    public int getCallCount() {
        return callCount;
    }

    /**
     * Commit a transaction on the database. If using non-managed transaction commit the local transaction.
     */
    public void commitTransaction(AbstractSession session) throws DatabaseException {
        if (usesExternalTransactionController()) {
            // if there is no external TX controller, then that means we are currently not synchronized
            // with a global JTS transaction.  In this case, there won't be any 'afterCompletion'
            // callbacks so we have to release the connection here.  It is possible (WLS 5.1) to choose
            // 'usesExternalTransactionController' on the login, but still acquire a uow that WON'T be
            // synchronized with a global TX.
            if (!session.isSynchronized()) {
                this.isInTransaction = false;
                if (this.usesExternalConnectionPooling) {
                    // closeConnection method uses currentSession and then sets it to null.
                    currentSession = session;
                    closeConnection();
                }
            }
            return;
        }

        session.log(SessionLog.FINER, SessionLog.TRANSACTION, "commit_transaction", (Object[])null, this);

        try {
            session.startOperationProfile(SessionProfiler.Transaction);
            incrementCallCount(session);
            basicCommitTransaction(session);

            if(sequencingCallback != null) {
                sequencingCallback.afterCommit(this);
            }
            this.isInTransaction = false;
        } finally {
            sequencingCallback = null;
            decrementCallCount();
            session.endOperationProfile(SessionProfiler.Transaction);
        }
    }

    /**
     * Connect to the datasource.  Through using a CCI ConnectionFactory.
     * Catch exceptions and re-throw as EclipseLink exceptions.
     */
    public void connect(Login login, AbstractSession session) throws DatabaseException {
        session.startOperationProfile(SessionProfiler.ConnectionManagement);
        session.incrementProfile(SessionProfiler.Connects);

        try {
            if (session.shouldLog(SessionLog.CONFIG, SessionLog.CONNECTION)) {// Avoid printing if no logging required.
                session.log(SessionLog.CONFIG, SessionLog.CONNECTION, "connecting", new Object[] { login }, this);
            }
            setLogin(login);
            this.setDatasourcePlatform((DatasourcePlatform)session.getDatasourceLogin().getDatasourcePlatform());
            createCustomizer(session);
            try {
                connectInternal(login, session);
                this.isInTransaction = false;
            } catch (RuntimeException exception) {
                session.handleSevere(exception);
            }
            if (session.hasEventManager()) {
                session.getEventManager().postConnect(this);
            }
            incrementCallCount(session);
            try {
                buildConnectLog(session);
            } finally {
                decrementCallCount();
            }
        } finally {
            session.endOperationProfile(SessionProfiler.ConnectionManagement);
        }
    }

    /**
     * Close the connection to the driver level datasource.
     */
    protected abstract void closeDatasourceConnection();

    /**
     * Execute the call to driver level datasource.
     */
    protected abstract Object basicExecuteCall(Call call, AbstractRecord row, AbstractSession session);

    /**
     * Build a log string of any driver metadata that can be obtained.
     */
    protected abstract void buildConnectLog(AbstractSession session);

    /**
     * Return the login
     */
    public Login getLogin() {
        return login;
    }

    /**
     * SECURE:
     * set the login
     */
    protected void setLogin(Login login) {
        this.login = login;
        this.usesExternalConnectionPooling = login.shouldUseExternalConnectionPooling();        
    }

    /**
     * Disconnect from the datasource.
     */
    public void disconnect(AbstractSession session) throws DatabaseException {
        session.log(SessionLog.CONFIG, SessionLog.CONNECTION, "disconnect", (Object[])null, this);

        if (this.datasourceConnection == null) {
            return;
        }
        session.incrementProfile(SessionProfiler.Disconnects);
        session.startOperationProfile(SessionProfiler.ConnectionManagement);
        try {
            releaseCustomizer();
            closeDatasourceConnection();
            this.datasourceConnection = null;
            this.isInTransaction = true;
        } finally {
            session.endOperationProfile(SessionProfiler.ConnectionManagement);
        }
    }

    /**
     * Close the accessor's connection.
     * This is used only for external connection pooling
     * when it is intended for the connection to be reconnected in the future.
     */
    public void closeConnection() {
        try {
            if (this.datasourceConnection != null) {
                if (isDatasourceConnected()) {
                    if(currentSession != null) {
                        currentSession.preReleaseConnection(this);
                    }
                    if(customizer != null && customizer.isActive()) {
                        customizer.clear();
                    }
                    closeDatasourceConnection();
                }
                this.datasourceConnection = null;
            }
        } catch (DatabaseException exception) {
            // Ignore
            this.datasourceConnection = null;
        } finally {
            currentSession = null;
        }
    }

    /**
     * Execute the call.
     * @return depending of the type either the row count, row or vector of rows.
     */
    public Object executeCall(Call call, AbstractRecord translationRow, AbstractSession session) throws DatabaseException {
        // If the login is null, then this accessor has never been connected.
        if (this.login == null) {
            throw DatabaseException.databaseAccessorNotConnected();
        }

        if (session.shouldLog(SessionLog.FINE, SessionLog.SQL)) {// pre-check to improve performance
            session.log(SessionLog.FINE, SessionLog.SQL, call.getLogString(this), (Object[])null, this, false);
        }

        Object result = basicExecuteCall(call, translationRow, session);

        return result;
    }

    /**
     * PUBLIC:
     * Reconnect to the database.  This can be used if the connection was disconnected or timedout.
     * This ensures that the security is checked as it is public.
     * Because the messages can take a long time to build,
     * pre-check whether messages should be logged.
     */
    public void reestablishConnection(AbstractSession session) throws DatabaseException {
        if (session.shouldLog(SessionLog.CONFIG, SessionLog.CONNECTION)) {// Avoid printing if no logging required.		
            Object[] args = { getLogin() };
            session.log(SessionLog.CONFIG, SessionLog.CONNECTION, "reconnecting", args, this);
        }
        reestablishCustomizer();
        reconnect(session);
        this.isInTransaction = false;
        this.isValid = true;
        if (session.hasEventManager()) {
            session.getEventManager().postConnect(this);
        }
    }

    /**
     * Attempt to save some of the cost associated with getting a fresh connection.
     * Assume the DatabaseDriver has been cached, if appropriate.
     * Note: Connections that are participating in transactions will not be refreshed.^M
     */
    protected void reconnect(AbstractSession session) throws DatabaseException {
        session.log(SessionLog.FINEST, SessionLog.CONNECTION, "reconnecting_to_external_connection_pool");
        session.startOperationProfile(SessionProfiler.ConnectionManagement);
        try {
            connectInternal(this.login, session);
        } finally {
            session.endOperationProfile(SessionProfiler.ConnectionManagement);
        }
    }

    /**
     * Return the platform.
     */
    public DatasourcePlatform getDatasourcePlatform() {
        return platform;
    }

    /**
     * Set the platform.
     * This should be set to the session's platform, not the connections
     * which may not be configured correctly.
     */
    public void setDatasourcePlatform(DatasourcePlatform platform) {
        this.platform = platform;
    }

    /**
     * Return the driver level connection.
     */
    public Object getDatasourceConnection() {
        return datasourceConnection;
    }

    /**
     * Helper method to return the JDBC connection for DatabaseAccessor.
     * Was going to deprecate this, but since most clients are JDBC this is useful.
     */
    public java.sql.Connection getConnection() {
        return (java.sql.Connection)this.datasourceConnection;
    }

    /**
     * Return column information for the specified
     * database objects.
     */
    public Vector getColumnInfo(String catalog, String schema, String tableName, String columnName, AbstractSession session) throws DatabaseException {
        return new Vector();
    }

    /**
     * Return the number of read statements.
     */
    public int getReadStatementsCount() {
        return readStatementsCount;
    }
    
    /**
     * Return the number of write statements.
     */
    public int getWriteStatementsCount() {
        return writeStatementsCount;
    }

    /**
     * Return the number of stored procedure call.
     */
    public int getStoredProcedureStatementsCount() {
        return storedProcedureStatementsCount;
    }
    
    /**
     * Return table information for the specified
     * database objects.
     */
    public Vector getTableInfo(String catalog, String schema, String tableName, String[] types, AbstractSession session) throws DatabaseException {
        return new Vector();
    }

    /**
     * If client requires to manually set connection they can use the connection manager.
     */
    protected void setDatasourceConnection(Object connection) {
        this.datasourceConnection = connection;
    }

    /**
     * Rollback the transaction on the datasource. If not using managed transaction rollback the local transaction.
     */
    public void rollbackTransaction(AbstractSession session) throws DatabaseException {
        if (usesExternalTransactionController()) {
            // if there is no external TX controller, then that means we are currently not synchronized
            // with a global JTS transaction.  In this case, there won't be any 'afterCompletion'
            // callbacks so we have to release the connection here.  It is possible (WLS 5.1) to choose
            // 'usesExternalTransactionController' on the login, but still acquire a uow that WON'T be
            // synchronized with a global TX.
            if (!session.isSynchronized()) {
                this.isInTransaction = false;
                if (this.usesExternalConnectionPooling) {
                    // closeConnection method uses currentSession and then sets it to null.
                    currentSession = session;
                    closeConnection();
                }
            }
            return;
        }

        session.log(SessionLog.FINER, SessionLog.TRANSACTION, "rollback_transaction", (Object[])null, this);

        try {
            session.startOperationProfile(SessionProfiler.Transaction);
            incrementCallCount(session);
            basicRollbackTransaction(session);
        } finally {
            this.isInTransaction = false;
            sequencingCallback = null;
            decrementCallCount();
            session.endOperationProfile(SessionProfiler.Transaction);
        }
    }

    /**
     * Return true if some external transaction service is controlling transactions.
     */
    public boolean usesExternalTransactionController() {
        if (this.login == null) {
            throw DatabaseException.databaseAccessorNotConnected();
        }
        return this.login.shouldUseExternalTransactionController();
    }

    /**
     * Return true if the accessor is currently connected to a data source.
     * Return false otherwise.
     */
    public boolean isConnected() {
        if ((this.datasourceConnection == null) && (this.login == null)) {
            return false;
        }
        if (this.usesExternalConnectionPooling) {
            return true;// As can always reconnect.
        }

        if (this.datasourceConnection == null) {
            return false;
        }

        return isDatasourceConnected();
    }

    /**
     * Return if the driver level connection is connected.
     */
    protected abstract boolean isDatasourceConnected();

    /**
     * Added as a result of Bug 2804663 - satisfy the Accessor interface
     * implementation.
     */
    public void flushSelectCalls(AbstractSession session) {
        // By default do nothing.
    }

    /**
     * This method will be called after a series of writes have been issued to
     * mark where a particular set of writes has completed.  It will be called
     * from commitTransaction and may be called from writeChanges.   Its main
     * purpose is to ensure that the batched statements have been executed
     */
    public void writesCompleted(AbstractSession session) {
        //this is a no-op in this method as we do not batch on this accessor
    }

    /**
     * Return sequencing callback.
     */
    public SequencingCallback getSequencingCallback(SequencingCallbackFactory sequencingCallbackFactory) {
        if(sequencingCallback == null) {
            sequencingCallback = sequencingCallbackFactory.createSequencingCallback();
        }
        return sequencingCallback;
    }

    /**
     * Attempts to create ConnectionCustomizer. If created the customizer is cached by the accessor.
     * Called by the owner of accessor (DatabaseSession, ServerSession through ConnectionPool) just once, 
     * typically right after the accessor is created. 
     * Also called by ClientSession when it acquires write accessor.
     * If accessor already has a customizer set by ConnectionPool then ClientSession's customizer
     * compared with the existing one and if they are not equal (don't produce identical customization)
     * then the new customizer set onto accessor, caching the old customizer so that it could be restored later.
     */
    public void createCustomizer(AbstractSession session) {
        ConnectionCustomizer newCustomizer;        
        if(customizer == null) {
            // Create a new customizer. The platform may be null if the accessor hasn't yet been connected. 
            if(platform != null) {
                newCustomizer = platform.createConnectionCustomizer(this, session);
            } else {
                newCustomizer = ((DatasourcePlatform)session.getDatasourcePlatform()).createConnectionCustomizer(this, session);
            }
            if(newCustomizer == null) {
                // Neither old nor new exists - nothing to do.
            } else {
                // Old customizer doesn't exist - just set the new one.
                setCustomizer(newCustomizer);
            }
        } else {
            // the passed session has built the old customizer - no need to build the new one.
            if(customizer.getSession() == session) {
                return;
            }
            // Create a new customizer. The platform may be null if the accessor hasn't yet been connected. 
            if(platform != null) {
                newCustomizer = platform.createConnectionCustomizer(this, session);
            } else {
                newCustomizer = ((DatasourcePlatform)session.getDatasourcePlatform()).createConnectionCustomizer(this, session);
            }
            if(newCustomizer == null) {
                // New customizer doesn't exist - but the old one does.
                if(customizer.isActive()) {
                    customizer.clear();
                }
                // The only reason for setting empty customizer is to preserve the previous customizer
                // until releaseCustomizer(session) is called - where session is the one set in empty customizer.
                // Happens when ServerSession defines customization but ClientSession explicitly demands no customization.
                newCustomizer = ConnectionCustomizer.createEmptyCustomizer(session);
                newCustomizer.setPrevCustomizer(customizer);
                // No need to call customize on Empty customizer - it does nothing.
                customizer = newCustomizer;
            } else {
                // Both old and new customizers exist.
                if(newCustomizer.equals(customizer)) {
                    // The equality of customizers means they customize connection in exactly the same way.
                    // Therefore clearing the old customization followed by application of the new one could be skipped:
                    // just keep the old customizer.
                    // Happens when ServerSession and ClientSession define equivalent customizers.
                } else {
                    // The old customizer substituted for the new one.
                    if(customizer.isActive()) {
                        customizer.clear();
                    }
                    // Note that the old one is cached in the new one and will be restored
                    // when releaseCustomizer(session( is called - where session is the one set in the new customizer.
                    // Happens when ClientSession customizer overrides ServerSession's customizer.
                    newCustomizer.setPrevCustomizer(customizer);
                    setCustomizer(newCustomizer);
                }
            }
        } 
    }
    
    /**
     * Set customizer, customize the connection if it's available.
     */
    protected void setCustomizer(ConnectionCustomizer newCustomizer) {
        this.customizer = newCustomizer;
        if(getDatasourceConnection() != null) {
            customizer.customize();
        }
    }
  
    /**
     * Clear customizer if it's active and set it to null.
     * Called by the same object that has created customizer (DatabaseSession, ConnectionPool) when
     * the latter is no longer required, typically before releasing the accessor.
     * Ignored if there's no customizer. 
     */
    public void releaseCustomizer() {
        if(customizer != null) {
            if(customizer.isActive()) {
                customizer.clear();
            }
            customizer = null;
        }
    }
   
   /**
    * Clear and remove customizer if its session is the same as the passed one;
    * in case prevCustomizer exists set it as a new customizer.
    * Called when ClientSession releases write accessor:
    * if the customizer was created by the ClientSession it's removed, and
    * the previous customizer (that ConnectionPool had set) is brought back;
    * otherwise the customizer (created by ConnectionPool) is kept.
    * Ignored if there's no customizer. 
    */
   public void releaseCustomizer(AbstractSession session) {
       if(customizer != null) {
           if(customizer.getSession() == session) {
               if(customizer.isActive()) {
                   customizer.clear();
               }
               if(customizer.getPrevCustomizer() == null) {
                   customizer = null;
               } else {
                   setCustomizer(customizer.getPrevCustomizer());
               }
           }
       }
   }
  
   /**
    * This method is called by reestablishConnection.
    * Nothing needs to be done in case customize is not active (customization hasn't been applied yet). 
    * to repair existing customizer after connection became invalid.
    * However if connection has been customized then
    * if connection is still there and deemed to be valid - clear customization.
    * Otherwise (or if clear fails) remove customizer and set its prevCustomizer as a new customizer,
    * then re-create customizer using the same session as the original one.
    */
   protected void reestablishCustomizer() {
       if(customizer != null && customizer.isActive()) {
           if(isValid()) {
               // the method eats SQLException in case of a failure.   
               customizer.clear();
           } else {
               // It's an invalid connection - don't bother trying to clear customization.
               AbstractSession customizerSession = (AbstractSession)customizer.getSession();
               // need this so that the new customizer has the same prevCustomizer as the old one.
               customizer = customizer.getPrevCustomizer();
               // customizer recreated - it's the same as the original one, but not active.
               createCustomizer(customizerSession);
           }
       }
   }

   /**
    * Return the associated connection pool this connection was obtained from.
    */
   public ConnectionPool getPool() {
       return pool;
   }

   /**
    * Set the associated connection pool this connection was obtained from.
    */
   public void setPool(ConnectionPool pool) {
       this.pool = pool;
   }
}
