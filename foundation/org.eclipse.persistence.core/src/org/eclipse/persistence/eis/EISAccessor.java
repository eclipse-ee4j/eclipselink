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
package org.eclipse.persistence.eis;

import javax.resource.*;
import javax.resource.cci.*;
import org.eclipse.persistence.internal.databaseaccess.DatasourceAccessor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.eis.interactions.*;

/**
 * <p><code>EISAccessor</code> is an implementation of the <code>Accessor</code>
 * interface.  It is responsible for:
 * <ul>
 * <li>Connecting via connection factory
 * <li>Local transactions
 * <li>Interaction execution
 * <li>Record translation
 * </ul>
 *
 * @see EISInteraction
 * @see EISLogin
 * 
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class EISAccessor extends DatasourceAccessor {
    protected Connection cciConnection;
    protected RecordFactory recordFactory;

    /**
     *    Default Constructor.
     */
    public EISAccessor() {
        super();
    }

    /**
     *    Begin a local transaction.
     */
    protected void basicBeginTransaction(AbstractSession session) throws EISException {
        try {
            if (getEISPlatform().supportsLocalTransactions()) {
                getCCIConnection().getLocalTransaction().begin();
            }
        } catch (ResourceException exception) {
            throw EISException.resourceException(exception, this, session);
        }
    }

    /**
     * Close the connection.
     */
    protected void closeDatasourceConnection() {
        try {
            getCCIConnection().close();
        } catch (ResourceException exception) {
            throw EISException.resourceException(exception, this, null);
        }
    }

    /**
     * Commit the local transaction.
     */
    protected void basicCommitTransaction(AbstractSession session) throws EISException {
        try {
            if (getEISPlatform().supportsLocalTransactions()) {
                getCCIConnection().getLocalTransaction().commit();
            }
        } catch (ResourceException exception) {
            throw EISException.resourceException(exception, this, session);
        }
    }

    /**
     * If logging is turned on and the CCI implementation supports meta data then display connection info.
     */
    protected void buildConnectLog(AbstractSession session) {
        try {
            // Log connection information.
            if (session.shouldLog(SessionLog.CONFIG, SessionLog.CONNECTION)) {// Avoid printing if no logging required.
                ConnectionMetaData metaData = getCCIConnection().getMetaData();
                Object[] args = { metaData.getUserName(), metaData.getEISProductName(), metaData.getEISProductVersion(), Helper.cr(), "\t" };
                session.log(SessionLog.CONFIG, SessionLog.CONNECTION, "connected_user_database", args, this);
            }
        } catch (ResourceException exception) {
            // Some databases do not support metadata, ignore exception.
            session.warning("JDBC_driver_does_not_support_meta_data", SessionLog.CONNECTION);
        }
    }

    /**
     * Avoid super to have logging occur after possible manual auto-commit.
     */
    public Object executeCall(Call call, AbstractRecord translationRow, AbstractSession session) throws DatabaseException {
        return basicExecuteCall(call, translationRow, session);
    }

    /**
     * Execute the interaction.
     * The execution can differ slightly depending on the type of interaction.
     * The call may be parameterized where the arguments are in the translation row.
     * The row will be empty if there are no parameters.
     * @return depending of the type either the row count, row or vector of rows.
     */
    public Object basicExecuteCall(Call call, AbstractRecord translationRow, AbstractSession session) throws DatabaseException {
        // If the login is null, then this accessor has never been connected.
        if (getLogin() == null) {
            throw DatabaseException.databaseAccessorNotConnected();
        }

        Interaction interaction = null;
        Object result = null;
        EISInteraction eisCall = null;
        try {
            eisCall = (EISInteraction)call;
        } catch (ClassCastException e) {
            throw QueryException.invalidDatabaseCall(call);
        }

        // Record and check if auto-commit is required.
        // Some platforms may require this (AQ).
        boolean autoCommit = (!isInTransaction()) && getEISPlatform().requiresAutoCommit();
        if (autoCommit) {
            beginTransaction(session);
        }
        try {
            if (session.shouldLog(SessionLog.FINE, SessionLog.SQL)) {// pre-check to improve performance
                session.log(SessionLog.FINE, SessionLog.SQL, call.getLogString(this), (Object[])null, this, false);
            }
            incrementCallCount(session);
            session.startOperationProfile(SessionProfiler.SqlPrepare, eisCall.getQuery(), SessionProfiler.ALL);
            Record input = null;
            Record output = null;
            try {
                interaction = getCCIConnection().createInteraction();
                input = getEISPlatform().createInputRecord(eisCall, this);
                output = getEISPlatform().createOutputRecord(eisCall, translationRow, this);
            } finally {
                session.endOperationProfile(SessionProfiler.SqlPrepare, eisCall.getQuery(), SessionProfiler.ALL);
            }
            session.startOperationProfile(SessionProfiler.StatementExecute, eisCall.getQuery(), SessionProfiler.ALL);
            try {
                boolean success = true;
                InteractionSpec interactionSpec = getEISPlatform().buildInteractionSpec(eisCall);
                if (output == null) {
                    output = interaction.execute(interactionSpec, input);
                } else {
                    success = interaction.execute(interactionSpec, input, output);
                }
                session.log(SessionLog.FINEST, SessionLog.QUERY, "adapter_result", output);
                if (eisCall.isNothingReturned()) {
                    if (success) {
                        result = Integer.valueOf(1);
                    } else {
                        result = Integer.valueOf(0);                        
                    }
                    // Fire the output parameter row to allow app to handle return value.
                    if (output != null) {
                        AbstractRecord outputRow = getEISPlatform().buildRow(output, eisCall, this);
                        if (outputRow != null) {
                            eisCall.getQuery().setProperty("output", outputRow);
                            if (session.hasEventManager()) {
                                session.getEventManager().outputParametersDetected(outputRow, eisCall);
                            }
                        }
                    }
                } else if (eisCall.isOneRowReturned()) {
                    result = getEISPlatform().buildRow(output, eisCall, this);
                } else {
                    result = getEISPlatform().buildRows(output, eisCall, this);
                }
                session.log(SessionLog.FINEST, SessionLog.QUERY, "data_access_result", output);
            } finally {
                session.endOperationProfile(SessionProfiler.StatementExecute, eisCall.getQuery(), SessionProfiler.ALL);
            }
        } catch (ResourceException exception) {
            // Ensure each resource is released, but still ensure that the real exception is thrown.
            try {
                interaction.close();
            } catch (Exception closeException) {
                // Ignore error to avoid masking real exception.
            }
            try {
                decrementCallCount();
            } catch (Exception closeException) {
                // Ignore error to avoid masking real exception.
            }
            try {
                if (autoCommit) {
                    commitTransaction(session);
                }
            } catch (Exception closeException) {
                // Ignore error to avoid masking real exception.
            }
            throw EISException.resourceException(exception, call, this, session);
        } catch (RuntimeException exception) {
            try {// Ensure that the statement is closed, but still ensure that the real exception is thrown.
                try {
                    interaction.close();
                } finally {
                    if (autoCommit) {
                        commitTransaction(session);
                    }
                }
            } catch (Exception closeException) {
            }
            throw exception;
        }

        boolean transactionCommitted = false;
        boolean countDecremented = false;
        // This is in separate try block to ensure that the real exception is not masked by the close exception.
        try {
            interaction.close();
            if (autoCommit) {
                commitTransaction(session);
            }
            transactionCommitted = true;
            decrementCallCount();
            countDecremented = true;
        } catch (ResourceException exception) {
            try {
                if (!transactionCommitted) {
                    if (autoCommit) {
                        commitTransaction(session);
                    }                
                }
            } catch (Exception ignore) {
                // Ignore error to avoid masking real exception.
            }
            try {
                if (!countDecremented) {
                    decrementCallCount();
                }
            } catch (Exception ignore) {
                // Ignore error to avoid masking real exception.
            }
            throw EISException.resourceException(exception, this, session);
        }

        return result;
    }

    /**
     * Return the CCI connection to the EIS resource adapter.
     */
    public Connection getCCIConnection() {
        return (Connection)getDatasourceConnection();
    }

    /**
     * Return and cast the platform.
     */
    public EISPlatform getEISPlatform() {
        return (EISPlatform)getDatasourcePlatform();
    }

    /**
     * Return the RecordFactory.
     * The record factory is acquired from the ConnectionManager,
     * and used to create record to pass to interactions.
     */
    public RecordFactory getRecordFactory() {
        return recordFactory;
    }

    /**
     * Set the RecordFactory.
     * The record factory is acquired from the ConnectionManager,
     * and used to create record to pass to interactions.
     */
    public void setRecordFactory(RecordFactory recordFactory) {
        this.recordFactory = recordFactory;
    }

    /**
     * Rollback the local transaction on the datasource.
     */
    public void basicRollbackTransaction(AbstractSession session) throws DatabaseException {
        try {
            if (getEISPlatform().supportsLocalTransactions()) {
                getCCIConnection().getLocalTransaction().rollback();
            }
        } catch (ResourceException exception) {
            throw EISException.resourceException(exception, this, session);
        }
    }

    /**
     * Return if the connection to the "data source" is connected.
     */
    protected boolean isDatasourceConnected() {
        return isConnected;
    }
}
