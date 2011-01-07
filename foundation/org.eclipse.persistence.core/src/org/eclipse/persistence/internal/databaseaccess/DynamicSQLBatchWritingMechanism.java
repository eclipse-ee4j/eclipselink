/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.ModifyQuery;
import org.eclipse.persistence.sessions.SessionProfiler;

/**
 * INTERNAL:
 *    DynamicSQLBatchWritingMechanism is a private class, used by the DatabaseAccessor. 
 *    It provides the required behavior for batching statements, for write, with parameter binding turned off.<p>
 */
public class DynamicSQLBatchWritingMechanism extends BatchWritingMechanism {

    /**
     * This variable is used to store the SQLStrings that are being batched
     */
    protected ArrayList sqlStrings;
    
    /**
     * Stores the statement indexes for statements that are using optimistic locking.  This allows us to check individual
     * statement results on supported platforms
     */

    /**
     * This attribute is used to store the maximum length of all strings batched together
     */
    protected long batchSize;
    
    /**
     * Records if this batch uses optimistic locking.
     */
     protected boolean usesOptimisticLocking;

    public DynamicSQLBatchWritingMechanism(DatabaseAccessor databaseAccessor) {
        this.databaseAccessor = databaseAccessor;
        this.sqlStrings = new ArrayList(10);
        this.batchSize = 0;
        
    }

    /**
     * INTERNAL:
     * This method is called by the DatabaseAccessor to add this statement to the list of statements
     * being batched.  This call may result in the Mechanism executing the batched statements and
     * possibly, switching out the mechanisms
     */
    public void appendCall(AbstractSession session, DatabaseCall dbCall) {
        // Store the largest queryTimeout on a single call for later use by the single statement in prepareJDK12BatchStatement
    	if(dbCall != null) {
        	cacheQueryTimeout(session, dbCall);
        }
    	
        if (!dbCall.hasParameters()) {
            // Bug#3214927-fix - Also the size must be switched back when switch back from param to dynamic.
            // This must also be checked here, as the dynamic is the default, so the mechanism may have not have been switched.
            if (this.databaseAccessor.getLogin().getPlatform().getMaxBatchWritingSize() == DatabasePlatform.DEFAULT_PARAMETERIZED_MAX_BATCH_WRITING_SIZE) {
                this.databaseAccessor.getLogin().getPlatform().setMaxBatchWritingSize(DatabasePlatform.DEFAULT_MAX_BATCH_WRITING_SIZE);
            }
            if ((batchSize + dbCall.getSQLString().length()) > this.databaseAccessor.getLogin().getPlatform().getMaxBatchWritingSize()) {
                executeBatchedStatements(session);
            }
            if (this.usesOptimisticLocking != dbCall.hasOptimisticLock){
                executeBatchedStatements(session);
            }
            this.sqlStrings.add(dbCall.getSQLString());
            this.batchSize += dbCall.getSQLString().length();
            this.usesOptimisticLocking = dbCall.hasOptimisticLock;
            ++this.statementCount;
            // feature for bug 4104613, allows users to force statements to flush on execution
            if (((ModifyQuery) dbCall.getQuery()).forceBatchStatementExecution())
            {
              executeBatchedStatements(session);
            }
        } else {
            executeBatchedStatements(session);
            this.switchMechanisms(session, dbCall);
        }
    }

    /**
     * INTERNAL:
     * This method is used to clear the batched statements without the need to execute the statements first
     * This is used in the case of rollback.
     */
    public void clear() {
        this.sqlStrings.clear();
        statementCount = executionCount  = 0;
        this.usesOptimisticLocking = false;
        this.batchSize = 0;
        clearCacheQueryTimeout();
    }

    /**
     * INTERNAL:
     * This method is used by the DatabaseAccessor to execute and clear the batched statements in the
     * case that a non batchable statement is being executed
     */
    public void executeBatchedStatements(AbstractSession session) {
        if (this.sqlStrings.isEmpty()) {
            return;
        }

        session.log(SessionLog.FINER, SessionLog.SQL, "begin_batch_statements", null, this.databaseAccessor);
        if (session.shouldLog(SessionLog.FINE, SessionLog.SQL)) {
            for (Iterator sqlStringsIterator = this.sqlStrings.iterator();
                     sqlStringsIterator.hasNext();) {
                session.log(SessionLog.FINE, SessionLog.SQL, (String)sqlStringsIterator.next(), null, this.databaseAccessor, false);
            }
        }
        session.log(SessionLog.FINER, SessionLog.SQL, "end_batch_statements", null, this.databaseAccessor);

        try {
            this.databaseAccessor.writeStatementsCount++;
            this.databaseAccessor.incrementCallCount(session);// Decrement occurs in close.
            if (!session.getPlatform().usesJDBCBatchWriting()) {
                PreparedStatement statement = prepareBatchStatement(session);
                this.databaseAccessor.executeBatchedStatement(statement, session);
            } else {
                //lets add optimistic locking support.
                Statement statement = prepareJDK12BatchStatement(session);
                executionCount += this.databaseAccessor.executeJDK12BatchStatement(statement, null, session, false);
                if (this.usesOptimisticLocking && (executionCount!=statementCount)){
                    throw OptimisticLockException.batchStatementExecutionFailure();
                }
            }
        } finally {
            // Reset the batched sql string
            this.clear();
        }
    }

    /**
     * INTERNAL:
     * This method is used to switch from this mechanism to the alternate automatically
     */
    protected void switchMechanisms(AbstractSession session, DatabaseCall dbCall) {
        this.databaseAccessor.setActiveBatchWritingMechanismToParameterizedSQL();
        this.databaseAccessor.getActiveBatchWritingMechanism().appendCall(session, dbCall);
    }

    /**
     * INTERNAL:
     * This method is used to build the batch statement by concatenating the strings
     * together.
     */
    protected PreparedStatement prepareBatchStatement(AbstractSession session) throws DatabaseException {
        PreparedStatement statement = null;
        boolean isDelimiterStringNeeded = false;
        StringWriter writer = new StringWriter();
        DatabasePlatform platform = session.getPlatform();

        writer.write(platform.getBatchBeginString());
        for (Iterator sqlStringsIteration = this.sqlStrings.iterator();
                 sqlStringsIteration.hasNext();) {
            if (isDelimiterStringNeeded) {
                writer.write(platform.getBatchDelimiterString());
            }
            writer.write((String)sqlStringsIteration.next());
            isDelimiterStringNeeded = true;
        }
        writer.write(platform.getBatchDelimiterString());
        writer.write(platform.getBatchEndString());

        try {
            session.startOperationProfile(SessionProfiler.SqlPrepare, null, SessionProfiler.ALL);
            try {
                statement = this.databaseAccessor.getConnection().prepareStatement(writer.toString());
            } finally {
                session.endOperationProfile(SessionProfiler.SqlPrepare, null, SessionProfiler.ALL);
            }
        } catch (SQLException exception) {
            //If this is a connection from an external pool then closeStatement will close the connection.
            //we must test the connection before that happens.
            DatabaseException exceptionToThrow = this.databaseAccessor.processExceptionForCommError(session, exception, null);
            try {// Ensure that the statement is closed, but still ensure that the real exception is thrown.
                this.databaseAccessor.closeStatement(statement, session, null);
            } catch (SQLException closeException) {
            }
            if (exceptionToThrow == null){
                throw DatabaseException.sqlException(exception, this.databaseAccessor, session, false);
            }
            throw exceptionToThrow;
        } catch (RuntimeException exception) {
            try {// Ensure that the statement is closed, but still ensure that the real exception is thrown.
                this.databaseAccessor.closeStatement(statement, session, null);
            } catch (SQLException closeException) {
            }
            throw exception;
        }
        return statement;
    }

    /**
     * INTERNAL:
     * This method is used to build the batch statement for the JDBC2.0 specification
     */
    protected Statement prepareJDK12BatchStatement(AbstractSession session) throws DatabaseException {
        Statement statement = null;

        try {
            session.startOperationProfile(SessionProfiler.SqlPrepare, null, SessionProfiler.ALL);
            try {
                statement = this.databaseAccessor.getConnection().createStatement();
                for (Iterator sqlStringsIterator = this.sqlStrings.iterator();
                         sqlStringsIterator.hasNext();) {
                    statement.addBatch((String)sqlStringsIterator.next());
                }
            	// Set the query timeout that was cached during the multiple calls to appendCall
                if(queryTimeoutCache > DescriptorQueryManager.NoTimeout) {
                	statement.setQueryTimeout(queryTimeoutCache);
                }
            } finally {
                session.endOperationProfile(SessionProfiler.SqlPrepare, null, SessionProfiler.ALL);
            }
        } catch (SQLException exception) {
            //If this is a connection from an external pool then closeStatement will close the connection.
            //we must test the connection before that happens.
            RuntimeException exceptionToThrow = this.databaseAccessor.processExceptionForCommError(session, exception, null);
            try {// Ensure that the statement is closed, but still ensure that the real exception is thrown.
                this.databaseAccessor.closeStatement(statement, session, null);
            } catch (SQLException closeException) {
            }
            if (exceptionToThrow == null){
                throw DatabaseException.sqlException(exception, this.databaseAccessor, session, false);
            }
            throw exceptionToThrow;
        } catch (RuntimeException exception) {
            try {// Ensure that the statement is closed, but still ensure that the real exception is thrown.
                this.databaseAccessor.closeStatement(statement, session, null);
            } catch (SQLException closeException) {
            }
            throw exception;
        }
        return statement;
    }
}
