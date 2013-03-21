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
package org.eclipse.persistence.internal.databaseaccess;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.ModifyQuery;
import org.eclipse.persistence.sessions.SessionProfiler;

/**
 * INTERNAL:
 * ParameterizedSQLBatchWritingMechanism is a private class, used by the DatabaseAccessor. it provides the required
 * behavior for batching statements, for write, with parameter binding turned on.<p>
 *
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public class ParameterizedSQLBatchWritingMechanism extends BatchWritingMechanism {

    /**
     * This member variable is used to keep track of the last SQL string that was executed
     * by this mechanism.  If the current string and previous string match then simply
     * bind in the arguments, otherwise end previous batch and start a new batch
     */
    protected DatabaseCall previousCall;

    /**
     * This variable contains a list of the parameters list passed into the query
     */
    protected List<List> parameters;
    protected DatabaseCall lastCallAppended;
    
    public ParameterizedSQLBatchWritingMechanism() {
        super();
    }
    
    public ParameterizedSQLBatchWritingMechanism(DatabaseAccessor databaseAccessor) {
        this.databaseAccessor = databaseAccessor;
        this.parameters = new ArrayList();
        this.maxBatchSize = this.databaseAccessor.getLogin().getPlatform().getMaxBatchWritingSize();
        if (this.maxBatchSize == 0) {
            // the max size was not set on the platform - use default
            this.maxBatchSize = DatabasePlatform.DEFAULT_PARAMETERIZED_MAX_BATCH_WRITING_SIZE;
        }
    }

    /**
     * INTERNAL:
     * This method is called by the DatabaseAccessor to add this statement to the list of statements
     * being batched.  This call may result in the Mechanism executing the batched statements and
     * possibly, switching out the mechanisms
     */
    public void appendCall(AbstractSession session, DatabaseCall dbCall) {
        if (dbCall.hasParameters()) {
            //make an equality check on the String, because if we are caching statements then
            //we will not have to perform the string comparison multiple times.
            if (this.previousCall == null) {
                this.previousCall = dbCall;
                this.parameters.add(dbCall.getParameters());
            } else {
                if (this.previousCall.getSQLString().equals(dbCall.getSQLString()) && (this.parameters.size() < this.maxBatchSize)) {
                    this.parameters.add(dbCall.getParameters());
                } else {
                    executeBatchedStatements(session);
                    this.previousCall = dbCall;
                    this.parameters.add(dbCall.getParameters());
                }
            }
            // Store the largest queryTimeout on a single call for later use by the single statement in prepareBatchStatements
            if (dbCall != null) {
                cacheQueryTimeout(session, dbCall);
            }
            this.lastCallAppended = dbCall;
            // feature for bug 4104613, allows users to force statements to flush on execution
            if (((ModifyQuery) dbCall.getQuery()).forceBatchStatementExecution())
            {
              executeBatchedStatements(session);
            }
        } else {
            executeBatchedStatements(session);
            switchMechanisms(session, dbCall);
        }
    }

    /**
     * INTERNAL:
     * This method is used to clear the batched statements without the need to execute the statements first
     * This is used in the case of rollback.
     */
    public void clear() {
        this.previousCall = null;
        this.parameters.clear();
        this.statementCount = 0;
        this.executionCount  = 0;
        this.queryTimeoutCache = DescriptorQueryManager.NoTimeout;
        // bug 229831 : BATCH WRITING CAUSES MEMORY LEAKS WITH UOW
        this.lastCallAppended = null;
    }

    /**
     * INTERNAL:
     * This method is used by the DatabaseAccessor to clear the batched statements in the
     * case that a non batchable statement is being executed
     */
    public void executeBatchedStatements(AbstractSession session) {
        if (this.parameters.isEmpty()) {
            return;
        }
        if (this.parameters.size() == 1) {
            // If only one call, just execute normally.
            try {
                int rowCount = (Integer)this.databaseAccessor.basicExecuteCall(this.previousCall, null, session, false);          
                if (this.previousCall.hasOptimisticLock()) {                    
                    if (rowCount != 1) {
                        throw OptimisticLockException.batchStatementExecutionFailure();
                    }
                }
            } finally {
                clear();
            }
            return;
        }

        try {
            this.databaseAccessor.incrementCallCount(session);// Decrement occurs in close.

            if (session.shouldLog(SessionLog.FINE, SessionLog.SQL)) {
                session.log(SessionLog.FINER, SessionLog.SQL, "begin_batch_statements", null, this.databaseAccessor);
                session.log(SessionLog.FINE, SessionLog.SQL, this.previousCall.getSQLString(), null, this.databaseAccessor, false);
                // took this logging part from SQLCall
                for (List callParameters : this.parameters) {
                    StringWriter writer = new StringWriter();
                    DatabaseCall.appendLogParameters(callParameters, this.databaseAccessor, writer, session);                
                    session.log(SessionLog.FINE, SessionLog.SQL, writer.toString(), null, this.databaseAccessor, false);
                }
                session.log(SessionLog.FINER, SessionLog.SQL, "end_batch_statements", null, this.databaseAccessor);
            }
            
            //bug 4241441: need to keep track of rows modified and throw opti lock exception if needed
            PreparedStatement statement = prepareBatchStatements(session);
            // += is used as native batch writing can return a row count before execution.
            this.executionCount += this.databaseAccessor.executeJDK12BatchStatement(statement, this.lastCallAppended, session, true);
            this.databaseAccessor.writeStatementsCount++;
            
            if (this.previousCall.hasOptimisticLock() && (this.executionCount != this.statementCount)) {
                throw OptimisticLockException.batchStatementExecutionFailure();
            }
        } finally {
            // Reset the batched sql string
            //we MUST clear the mechanism here in order to append the new statement.
            this.clear();
        }
    }

    /**
     * INTERNAL:
     * Swaps out the Mechanism for the other Mechanism
     */
    protected void switchMechanisms(AbstractSession session, DatabaseCall dbCall) {
        this.databaseAccessor.setActiveBatchWritingMechanismToDynamicSQL();
        this.databaseAccessor.getActiveBatchWritingMechanism(session).appendCall(session, dbCall);
    }

    /**
     * INTERNAL:
     * This method is used to build the parameterized batch statement for the JDBC2.0 specification
     */
    protected PreparedStatement prepareBatchStatements(AbstractSession session) throws DatabaseException {
        PreparedStatement statement = null;
        try {
            session.startOperationProfile(SessionProfiler.SqlPrepare, null, SessionProfiler.ALL);
            try {
                DatabasePlatform platform = session.getPlatform();
                boolean shouldUnwrapConnection = platform.usesNativeBatchWriting();
                statement = (PreparedStatement)this.databaseAccessor.prepareStatement(this.previousCall, session, shouldUnwrapConnection);
                // Perform platform specific preparations
                platform.prepareBatchStatement(statement, this.maxBatchSize);
               	if (this.queryTimeoutCache > DescriptorQueryManager.NoTimeout) {
                	// Set the query timeout that was cached during the multiple calls to appendCall
               		statement.setQueryTimeout(this.queryTimeoutCache);
                }
               	
                // Iterate over the parameter lists that were batched.
                int statementSize = this.parameters.size();
                for (int statementIndex = 0; statementIndex < statementSize; ++statementIndex) {
                    List parameterList = this.parameters.get(statementIndex);
                    int size = parameterList.size();
                    for (int index = 0; index < size; index++) {
                        platform.setParameterValueInDatabaseCall(parameterList.get(index), statement, index+1, session);
                    }

                    // Batch the parameters to the statement.
                    this.statementCount++;
                    this.executionCount += platform.addBatch(statement);
                }
            } finally {
                session.endOperationProfile(SessionProfiler.SqlPrepare, null, SessionProfiler.ALL);
            }
        } catch (SQLException exception) {
            // If this is a connection from an external pool then closeStatement will close the connection.
            // we must test the connection before that happens.
            RuntimeException exceptionToThrow = this.databaseAccessor.processExceptionForCommError(session, exception, lastCallAppended);
            try {
                // Ensure that the statement is closed, but still ensure that the real exception is thrown.
                this.databaseAccessor.closeStatement(statement, session, null);
            } catch (SQLException closeException) {
            }
            if (exceptionToThrow == null){
                throw DatabaseException.sqlException(exception, this.databaseAccessor, session, false);
            }
            throw exceptionToThrow;
        } catch (RuntimeException exception) {
            try {
                // Ensure that the statement is closed, but still ensure that the real exception is thrown.
                this.databaseAccessor.closeStatement(statement, session, null);
            } catch (SQLException closeException) {
            }
            throw exception;
        }
        return statement;
    }

    public DatabaseCall getPreviousCall() {
        return previousCall;
    }

    public void setPreviousCall(DatabaseCall previousCall) {
        this.previousCall = previousCall;
    }

    public List<List> getParameters() {
        return parameters;
    }

    public void setParameters(List<List> parameters) {
        this.parameters = parameters;
    }

    public DatabaseCall getLastCallAppended() {
        return lastCallAppended;
    }

    public void setLastCallAppended(DatabaseCall lastCallAppended) {
        this.lastCallAppended = lastCallAppended;
    }
}
