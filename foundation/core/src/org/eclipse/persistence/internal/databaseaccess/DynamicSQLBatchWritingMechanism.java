/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.databaseaccess;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.ModifyQuery;
import org.eclipse.persistence.sessions.SessionProfiler;

/**
 * INTERNAL:
 */
public class DynamicSQLBatchWritingMechanism implements BatchWritingMechanism {

    /**
     * This memeber variable stores the reference to the DatabaseAccessor that is
     * using this Mechanism to handle the batch writing
     */
    protected DatabaseAccessor databaseAccessor;

    /**
     * This variable is used to store the SQLStrings that are being batched
     */
    protected ArrayList sqlStrings;

    /**
     * This attribute is uesed to store the maximum length of all strings batched together
     */
    protected long batchSize;

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
        if (!dbCall.hasParameters()) {
            // Bug#3214927-fix - Also the size must be switched back when switch back from param to dynamic.
            // This must also be checked here, as the dynamic is the default, so the mechanism may have not have been switched.
            if (this.databaseAccessor.getLogin().getPlatform().getMaxBatchWritingSize() == DatabasePlatform.DEFAULT_PARAMETERIZED_MAX_BATCH_WRITING_SIZE) {
                this.databaseAccessor.getLogin().getPlatform().setMaxBatchWritingSize(DatabasePlatform.DEFAULT_MAX_BATCH_WRITING_SIZE);
            }
            if ((batchSize + dbCall.getSQLString().length()) > this.databaseAccessor.getLogin().getPlatform().getMaxBatchWritingSize()) {
                executeBatchedStatements(session);
            }
            this.sqlStrings.add(dbCall.getSQLString());
            this.batchSize += dbCall.getSQLString().length();
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
        this.batchSize = 0;
    }

    /**
     * INTERNAL:
     * This method is used by the DatabaseAccessor to execute and clear the batched statements in the
     * case that a non batchable statement is being execute
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
                Statement statement = prepareJDK12BatchStatement(session);
                this.databaseAccessor.executeJDK12BatchStatement(statement, null, session, false);
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
     * This method is used to build the batch statement by concatinating the strings
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
            session.startOperationProfile(SessionProfiler.SQL_PREPARE, null, SessionProfiler.ALL);
            try {
                statement = this.databaseAccessor.getConnection().prepareStatement(writer.toString());
            } finally {
                session.endOperationProfile(SessionProfiler.SQL_PREPARE, null, SessionProfiler.ALL);
            }
        } catch (SQLException exception) {
            try {// Ensure that the statement is closed, but still ensure that the real exception is thrown.
                this.databaseAccessor.closeStatement(statement, session, null);
            } catch (SQLException closeException) {
            }
            throw DatabaseException.sqlException(exception, this.databaseAccessor, session);
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
            session.startOperationProfile(SessionProfiler.SQL_PREPARE, null, SessionProfiler.ALL);
            try {
                statement = this.databaseAccessor.getConnection().createStatement();
                for (Iterator sqlStringsIterator = this.sqlStrings.iterator();
                         sqlStringsIterator.hasNext();) {
                    statement.addBatch((String)sqlStringsIterator.next());
                }
            } finally {
                session.endOperationProfile(SessionProfiler.SQL_PREPARE, null, SessionProfiler.ALL);
            }
        } catch (SQLException exception) {
            try {// Ensure that the statement is closed, but still ensure that the real exception is thrown.
                this.databaseAccessor.closeStatement(statement, session, null);
            } catch (SQLException closeException) {
            }
            throw DatabaseException.sqlException(exception, this.databaseAccessor, session);
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
     * Sets the accessor that this mechanism will be used
     */
    public void setAccessor(DatabaseAccessor accessor) {
        this.databaseAccessor = accessor;
    }
}
