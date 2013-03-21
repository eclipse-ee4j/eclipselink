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
 *     07/13/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     08/24/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.internal.queries;

import java.util.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
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

    public CallQueryMechanism() {
    }
    
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
     * Unprepare the call if required.
     * Clone and unprepare stored procedure calls, so they can be reprepared with possible different optional arguments.
     */
    public void unprepare() {
        DatabaseQuery query = this.query;
        if (hasMultipleCalls()) {
            this.calls = ((Vector)this.calls.clone());
            int size = this.calls.size();
            for (int index = 0; index < size; index++) {
                DatabaseCall call = (DatabaseCall)this.calls.get(index);
                if (call.isPrepared() && call.isStoredProcedureCall()
                        && ((StoredProcedureCall)call).hasOptionalArguments()) {
                    call = (DatabaseCall)call.clone();
                    call.setIsPrepared(false);
                    call.setQuery(query);
                    this.calls.set(index, call);
                }
            }
        } else if (this.call != null) {
            if (this.call.isPrepared() && this.call.isStoredProcedureCall()
                    && ((StoredProcedureCall)this.call).hasOptionalArguments()) {
                this.call = (DatabaseCall)this.call.clone();
                this.call.setIsPrepared(false);
                this.call.setQuery(query);
            }
        }
    }
    
    /**
     * INTERNAL:
     * This is different from 'prepareForExecution' in that this is called on the original query,
     * and the other is called on the copy of the query.
     * This query is copied for concurrency so this prepare can only setup things that
     * will apply to any future execution of this query.
     */
    public void prepareCall() throws QueryException {
        DatabaseQuery query = this.query;
        AbstractSession executionSession = query.getExecutionSession();
        if (hasMultipleCalls()) {
            if (query.shouldCloneCall()) {
                this.calls = ((Vector)this.calls.clone());
            }
            int size = this.calls.size();
            for (int index = 0; index < size; index++) {
                DatasourceCall call = (DatasourceCall)this.calls.get(index);
                if (query.shouldCloneCall()) {
                    // Need to clone the call if setting query specific properties on it as the call may be shared.
                    call = (DatabaseCall)call.clone();
                    call.setQuery(query);
                    this.calls.set(index, call);
                }
                if (call instanceof DatabaseCall) {
                    configureDatabaseCall((DatabaseCall)call);
                }
                call.prepare(executionSession);
            }
        } else if (this.call != null) {
            if (query.shouldCloneCall()) {
                // Need to clone the call if setting query specific properties on it as the call may be shared.
                this.call = (DatasourceCall)this.call.clone();
                this.call.setQuery(query);
            }
            DatasourceCall call = this.call;
            if (call instanceof DatabaseCall) {
                configureDatabaseCall((DatabaseCall)call);
            }
            this.call.prepare(executionSession);
        }
    }

    /**
     * Set the call level query options into the call.
     */
    protected void configureDatabaseCall(DatabaseCall call) {
        if (!this.query.shouldIgnoreBindAllParameters()) {
            call.setUsesBinding(this.query.shouldBindAllParameters());
        }
        if (!this.query.shouldIgnoreCacheStatement()) {
            call.setShouldCacheStatement(this.query.shouldCacheStatement());
        }
        call.setQueryTimeout(this.query.getQueryTimeout());
        if (this.query.isNativeConnectionRequired()) {
            call.setIsNativeConnectionRequired(true);
        }
        if (this.query.isReadQuery()) {
            ReadQuery readQuery = (ReadQuery)this.query;
            // Some DB don't support FirstRow in SELECT statements in spite of supporting MaxResults(Symfoware).
            // We should check FirstRow and MaxResults separately.
            if (!call.shouldIgnoreFirstRowSetting()){
                if (readQuery.getFirstResult() != 0) {
                    call.setFirstResult(readQuery.getFirstResult());
                    call.setIsResultSetScrollable(true);
                    call.setResultSetType(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE);
                    call.setResultSetConcurrency(java.sql.ResultSet.CONCUR_READ_ONLY);
                }
            }
            if (!call.shouldIgnoreMaxResultsSetting()){
                call.setMaxRows(readQuery.getMaxRows());
            }
            call.setResultSetFetchSize(readQuery.getFetchSize());
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
            }
        } else {
            getCall().returnNothing();
        }

        prepareCall();
    }

    /**
     * Pre-build configure the SQL call.
     */
    public void prepareDeleteObject() {
        boolean usesOptimisticLocking = ((DeleteObjectQuery)getQuery()).usesOptimisticLocking();
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatabaseCall call = (DatabaseCall)callsEnum.nextElement();
                call.returnNothing();
                if (usesOptimisticLocking) {
                    call.setHasOptimisticLock(true);
                }
            }
        } else {
            getCall().returnNothing();
            if (usesOptimisticLocking) {
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
     * Pre-build configure the SQL call for an execute call.
     */
    public void prepareExecute() {
        DatabaseCall call = getDatabaseCall();
        call.setExecuteUpdate();
        call.setIsFieldMatchingRequired(isCallQueryMechanism());
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
        ClassDescriptor descriptor = getDescriptor();
        for (DatabaseTable table : descriptor.getTables()) {
            AbstractRecord row = descriptor.getObjectBuilder().buildRowForUpdateAfterShallowInsert(writeQuery.getObject(), writeQuery.getSession(), table);
            if (!row.isEmpty()) {
                SQLUpdateStatement updateStatement = new SQLUpdateStatement();
                updateStatement.setModifyRow(row);
                updateStatement.setTranslationRow(getTranslationRow());
                updateStatement.setTable(table);
                updateStatement.setWhereClause(descriptor.getObjectBuilder().buildPrimaryKeyExpression(table));// Must not check version, ok as just inserted it.
                // Bug 2996585
                StatementQueryMechanism updateMechanism = new StatementQueryMechanism(writeQuery, updateStatement);
                writeQuery.setModifyRow(row);
                updateMechanism.updateObject();
            }
        }
    }

    /**
     * Update the foreign key fields to null when resolving a deletion cycle.
     * This must always be dynamic as it is called within an delete query and is really part of the delete
     * and does not fire update events or worry about locking.
     */
    @Override
    public void updateForeignKeyFieldBeforeDelete() {
        ClassDescriptor descriptor = getDescriptor();
        DeleteObjectQuery deleteQuery = (DeleteObjectQuery)getQuery();
        for (DatabaseTable table : descriptor.getTables()) {
            // need nullify the same fields that would be updated after shallow insert
            AbstractRecord row = descriptor.getObjectBuilder().buildRowForUpdateBeforeShallowDelete(deleteQuery.getObject(), deleteQuery.getSession(), table);
            if (!row.isEmpty()) {
                SQLUpdateStatement updateStatement = new SQLUpdateStatement();
                updateStatement.setModifyRow(row);
                updateStatement.setTranslationRow(getTranslationRow());
                updateStatement.setTable(table);
                updateStatement.setWhereClause(descriptor.getObjectBuilder().buildPrimaryKeyExpression(table));// Must not check version, ok as delete will.
                StatementQueryMechanism updateMechanism = new StatementQueryMechanism(deleteQuery, updateStatement);
                deleteQuery.setModifyRow(row);
                updateMechanism.updateObject();
            }
        }
    }
}
