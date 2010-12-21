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
package org.eclipse.persistence.internal.queries;

import java.util.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;

/**
 * <p><b>Purpose</b>:
 * Mechanism used for custom SQL and stored procedure queries.
 * <p>
 * <p><b>Responsibilities</b>:
 * Executes the appropriate call.
 *
 * @author James Sutherland
 * @since TOPLink/Java 2.0
 */
public class CallQueryMechanism extends DatasourceCallQueryMechanism {

    /**
     * Initialize the state of the query
     * @param query - owner of mechanism
     */
    public CallQueryMechanism(DatabaseQuery query) {
        super(query);
    }

    /**
     * Initialize the state of the query
     * @param query - owner of mechanism
     * @param call - sql call
     */
    public CallQueryMechanism(DatabaseQuery query, DatabaseCall call) {
        super(query, call);
        call.setIsFieldMatchingRequired(true);
    }

    /**
     * Return the call.
     */
    public DatabaseCall getDatabaseCall() {
        return (DatabaseCall)call;
    }

    /**
     * INTERNAL:
     * This is different from 'prepareForExecution' in that this is called on the original query,
     * and the other is called on the copy of the query.
     * This query is copied for concurrency so this prepare can only setup things that
     * will apply to any future execution of this query.
     */
    public void prepareCall() throws QueryException {
        DatabaseQuery query = getQuery();
        AbstractSession executionSession = query.getExecutionSession();
        if (hasMultipleCalls()) {
            if(getQuery().shouldCloneCall()){
                //For glassFish bug2689, the call needs to be cloned when query asks to do so. 
                calls = ((Vector)getCalls().clone());
            }
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatabaseCall call = (DatabaseCall)callsEnum.nextElement();
                if (!query.shouldIgnoreBindAllParameters()) {
                    call.setUsesBinding(query.shouldBindAllParameters());
                }
                if (!query.shouldIgnoreCacheStatement()) {
                    call.setShouldCacheStatement(query.shouldCacheStatement());
                }
                call.setQueryTimeout(query.getQueryTimeout());
                if (query.isNativeConnectionRequired()) {
                    call.setIsNativeConnectionRequired(true);
                }
                if (query.isReadQuery()) {
                    ReadQuery readQuery = (ReadQuery)query;
                    if (!call.shouldIgnoreMaxResultsSetting()){
                        call.setMaxRows(readQuery.getMaxRows());
                        if (readQuery.getFirstResult() != 0) {
                            call.setFirstResult(readQuery.getFirstResult());
                            call.setIsResultSetScrollable(true);
                            call.setResultSetType(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE);
                            call.setResultSetConcurrency(java.sql.ResultSet.CONCUR_READ_ONLY);
                        }
                    }
                    call.setResultSetFetchSize(readQuery.getFetchSize());
                }
                call.prepare(executionSession);
            }
        } else if (getCall() != null) {
            if (getQuery().shouldCloneCall()){
                //For glassFish bug2689, the call needs to be cloned when query asks to do so. 
                call = (DatabaseCall)getDatabaseCall().clone();
                setCall(call);
            } 
            DatabaseCall call = getDatabaseCall();
            if (!query.shouldIgnoreBindAllParameters()) {
                call.setUsesBinding(query.shouldBindAllParameters());
            }
            if (!query.shouldIgnoreCacheStatement()) {
                call.setShouldCacheStatement(query.shouldCacheStatement());
            }
            call.setQueryTimeout(query.getQueryTimeout());
            if (query.isNativeConnectionRequired()) {
                call.setIsNativeConnectionRequired(true);
            }
            if (query.isReadQuery()) {
                ReadQuery readQuery = (ReadQuery)query;
                if (!call.shouldIgnoreMaxResultsSetting()){
                    call.setMaxRows(readQuery.getMaxRows());
                    if (readQuery.getFirstResult() != 0) {
                        call.setFirstResult(readQuery.getFirstResult());
                        call.setIsResultSetScrollable(true);
                        call.setResultSetType(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE);
                        call.setResultSetConcurrency(java.sql.ResultSet.CONCUR_READ_ONLY);
                    }
                }
                call.setResultSetFetchSize(((ReadQuery)query).getFetchSize());
            }
            call.prepare(executionSession);
        }
    }

    /**
     * Pre-build configure the SQL call.
     */
    public void prepareCursorSelectAllRows() throws QueryException {
        getCall().returnCursor();

        ContainerPolicy cp;
        DatabaseQuery query = getQuery();
        if (query.isReadAllQuery()) {
            cp = ((ReadAllQuery)query).getContainerPolicy();
        } else {
            cp = ((DataReadQuery)query).getContainerPolicy();
        }
        if (cp.isScrollableCursorPolicy()) {
            ScrollableCursorPolicy scp = (ScrollableCursorPolicy)cp;
            DatabaseCall call = getDatabaseCall();
            call.setIsResultSetScrollable(true);
            call.setResultSetType(scp.getResultSetType());
            call.setResultSetConcurrency(scp.getResultSetConcurrency());
            // Only set the fetch size to be the page size, if the fetch size was not set on the query.
            if (((ReadQuery)getQuery()).getFetchSize() == 0) {
                call.setResultSetFetchSize(scp.getPageSize());
            }
        }
        if (getQuery().isReportQuery()){
            prepareReportQueryItems();
        }
        prepareCall();
    }

    /**
     * Pre-build configure the SQL call.
     */
    public void prepareDeleteAll() {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatabaseCall call = (DatabaseCall)callsEnum.nextElement();
                call.returnNothing();
                if (getQuery().getDescriptor().usesOptimisticLocking()) {
                    call.setHasOptimisticLock(true);
                }
            }
        } else {
            getCall().returnNothing();
            if (getQuery().getDescriptor().usesOptimisticLocking()) {
                getDatabaseCall().setHasOptimisticLock(true);
            }
        }

        prepareCall();
    }

    /**
     * Pre-build configure the SQL call.
     */
    public void prepareDeleteObject() {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatabaseCall call = (DatabaseCall)callsEnum.nextElement();
                call.returnNothing();
                if (getQuery().getDescriptor().usesOptimisticLocking()) {
                    call.setHasOptimisticLock(true);
                }
            }
        } else {
            getCall().returnNothing();
            if (getQuery().getDescriptor().usesOptimisticLocking()) {
                getDatabaseCall().setHasOptimisticLock(true);
            }
        }
        prepareCall();
    }

    /**
     * Pre-build configure the SQL call.
     */
    public void prepareDoesExist(DatabaseField field) {
        getCall().returnOneRow();
        Vector fields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        fields.addElement(field);
        getDatabaseCall().setFields(fields);
        prepareCall();
    }

    /**
     * Pre-build configure the SQL call.
     */
    public void prepareExecuteSelect() {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatabaseCall databseCall = (DatabaseCall)callsEnum.nextElement();
                databseCall.returnManyRows();
                databseCall.setIsFieldMatchingRequired(isCallQueryMechanism());
            }
        } else {
            DatabaseCall call = getDatabaseCall();
            call.returnManyRows();
            call.setIsFieldMatchingRequired(isCallQueryMechanism());
        }
        prepareCall();
    }

    /**
     * Pre-build configure the SQL call.
     */
    public void prepareSelectAllRows() {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatabaseCall call = (DatabaseCall)callsEnum.nextElement();
                call.returnManyRows();
                if (isCallQueryMechanism()) {
                    call.setIsFieldMatchingRequired(true);
                    // Set the fields including joined and partial fields and compute joined indexes,
                    // this requires and assume that the custom SQL returns the fields in the correct order.
                    call.setFields(((ObjectLevelReadQuery)getQuery()).getSelectionFields());
                    prepareJoining((ObjectLevelReadQuery)getQuery());
                }
            }
        } else {
            getCall().returnManyRows();
            if (isCallQueryMechanism()) {
                DatabaseCall call = getDatabaseCall();
                call.setIsFieldMatchingRequired(true);
                // Set the fields including joined and partial fields and compute joined indexes,
                // this requires and assume that the custom SQL returns the fields in the correct order.
                call.setFields(((ObjectLevelReadQuery)getQuery()).getSelectionFields());
                prepareJoining((ObjectLevelReadQuery)getQuery());
            }
        }
        prepareCall();
    }
    
    /**
     * Prepare the joining indexes if joining.
     */
    protected void prepareJoining(ObjectLevelReadQuery query) {
        if (query.hasJoining()) {
            query.getJoinedAttributeManager().computeJoiningMappingIndexes(true, getSession(), 0);
        }
    }

    /**
     * Pre-build configure the SQL call.
     */
    public void prepareSelectOneRow() {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatabaseCall call = (DatabaseCall)callsEnum.nextElement();
                call.returnOneRow();
                if (isCallQueryMechanism()) {
                    call.setIsFieldMatchingRequired(true);
                    // Set the fields including joined and partial fields and compute joined indexes,
                    // this requires and assume that the custom SQL returns the fields in the correct order.
                    call.setFields(((ObjectLevelReadQuery)getQuery()).getSelectionFields());
                    prepareJoining((ObjectLevelReadQuery)getQuery());
                }
            }
        } else {
            getCall().returnOneRow();
            if (isCallQueryMechanism()) {
                DatabaseCall call = getDatabaseCall();
                call.setIsFieldMatchingRequired(true);
                // Set the fields including joined and partial fields and compute joined indexes,
                // this requires and assume that the custom SQL returns the fields in the correct order.
                call.setFields(((ObjectLevelReadQuery)getQuery()).getSelectionFields());
                prepareJoining((ObjectLevelReadQuery)getQuery());
            }
        }
        prepareCall();
    }

    /**
     * Pre-build configure the SQL call.
     */
    public void prepareUpdateObject() {
        if (hasMultipleCalls()) {
            int size = this.calls.size();
            for (int index = 0; index < size; index++) {
                DatabaseCall call = (DatabaseCall)this.calls.get(index);
                if (!call.isReturnSet()) {
                    call.returnNothing();
                }
                if (this.query.getDescriptor().usesOptimisticLocking()) {
                    call.setHasOptimisticLock(true);
                }
            }
        } else if (this.call != null) {
            if (!call.isReturnSet()) {
                this.call.returnNothing();
            }
            if (this.query.getDescriptor().usesOptimisticLocking()) {
                ((DatabaseCall)this.call).setHasOptimisticLock(true);
            }
        }
        prepareCall();
    }

    /**
     * INTERNAL:
     * Configure the call to be a dynamic custom SQL call, so that it ignores the # token.
     */
    public void setCallHasCustomSQLArguments() {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatabaseCall databaseCall = (DatabaseCall)callsEnum.nextElement();
                if (databaseCall.isSQLCall()) {
                    ((SQLCall)databaseCall).setHasCustomSQLArguments(true);
                }
            }
        } else if (getCall().isSQLCall()) {
            ((SQLCall)getCall()).setHasCustomSQLArguments(true);
        }
    }

    /**
     * Update the foreign key fields when resolving a bi-directional reference in a UOW.
     * This must always be dynamic as it is called within an insert query and is really part of the insert
     * and does not fire update events or worry about locking.
     */
    protected void updateForeignKeyFieldAfterInsert(WriteObjectQuery writeQuery) {
        for (Enumeration tablesEnum = getDescriptor().getTables().elements();
                 tablesEnum.hasMoreElements();) {
            DatabaseTable table = (DatabaseTable)tablesEnum.nextElement();
            SQLUpdateStatement updateStatement = new SQLUpdateStatement();
            updateStatement.setModifyRow(getDescriptor().getObjectBuilder().buildRowForUpdate(writeQuery));
            updateStatement.setTranslationRow(getTranslationRow());
            updateStatement.setTable(table);
            updateStatement.setWhereClause(getDescriptor().getObjectBuilder().buildPrimaryKeyExpression(table));// Must not check version, ok as just inserted it.
            // Bug 2996585
            StatementQueryMechanism updateMechanism = new StatementQueryMechanism(writeQuery, updateStatement);
            writeQuery.setModifyRow(updateStatement.getModifyRow());
            updateMechanism.updateObject();

        }
    }
}
