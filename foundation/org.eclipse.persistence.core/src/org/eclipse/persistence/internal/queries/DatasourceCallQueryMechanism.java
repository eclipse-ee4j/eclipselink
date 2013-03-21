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
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;

/**
 * <p><b>Purpose</b>:
 * Mechanism used for call queries.
 * <p>
 * <p><b>Responsibilities</b>:
 * Executes the appropriate call.
 *
 * @author James Sutherland
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class DatasourceCallQueryMechanism extends DatabaseQueryMechanism {
    protected DatasourceCall call;

    /** Normally only a single call is used, however multiple table may require multiple calls on write. */
    protected Vector calls;

    public DatasourceCallQueryMechanism() {
    }
    
    /**
     * Initialize the state of the query
     * @param query - owner of mechanism
     */
    public DatasourceCallQueryMechanism(DatabaseQuery query) {
        super(query);
    }

    /**
     * Initialize the state of the query
     * @param query - owner of mechanism
     */
    public DatasourceCallQueryMechanism(DatabaseQuery query, DatasourceCall call) {
        super(query);
        this.call = call;
        call.setQuery(query);
    }

    /**
     * Add the call.
     */
    public void addCall(DatasourceCall call) {
        getCalls().addElement(call);
        call.setQuery(getQuery());
    }

    /**
     * Read all rows from the database using a cursored stream.
     * @exception  DatabaseException - an error has occurred on the database
     */
    public DatabaseCall cursorSelectAllRows() throws DatabaseException {
        try {
            return (DatabaseCall)executeCall();
        } catch (java.lang.ClassCastException e) {
            throw QueryException.mustUseCursorStreamPolicy();
        }
    }
    
    /**
     * Read all rows from the database, return ResultSet
     * @exception  DatabaseException - an error has occurred on the database
     */
    public DatabaseCall selectResultSet() throws DatabaseException {
        try {
            // For CR 2923 must move to session we will execute call on now
            // so correct DatasourcePlatform used by translate.        
            AbstractSession sessionToUse = this.query.getExecutionSession();
            DatabaseCall clonedCall = (DatabaseCall)this.call.clone();
            clonedCall.setQuery(this.query);
            clonedCall.translate(this.query.getTranslationRow(), getModifyRow(), sessionToUse);
            clonedCall.returnCursor();
            return (DatabaseCall)sessionToUse.executeCall(clonedCall, this.query.getTranslationRow(), this.query);
        } catch (java.lang.ClassCastException e) {
            throw QueryException.invalidDatabaseCall(this.call);
        }
    }

    /**
     * INTERNAL:
     * Delete a collection of objects. Assume call is correct.
     * @exception  DatabaseException - an error has occurred on the database
     */
    public Integer deleteAll() throws DatabaseException {
        if(((DeleteAllQuery)this.query).isPreparedUsingTempStorage()) {
            return deleteAllUsingTempTables();
        } else {
            if (hasMultipleCalls()) {
                Integer returnedRowCount = null;
                
                // Deletion must occur in reverse order.
                for (int index = getCalls().size() - 1; index >= 0; index--) {
                    DatasourceCall databseCall = (DatasourceCall)getCalls().elementAt(index);
                    returnedRowCount = (Integer)executeCall(databseCall);
                }
                // returns the number of rows removed from the first table in insert order
                return returnedRowCount;
            } else {
                return (Integer)executeCall();
            }
        }
    }

    /**
     * Execute deleteAll using temp tables
     * @exception  DatabaseException - an error has occurred on the database.
     * @return the row count.
     */
    public Integer deleteAllUsingTempTables() throws DatabaseException {
        DatabaseException ex = null;
        Integer returnedRowCount = null;
        
        // Deletion must occur in reverse order.

        // first call - crete temp table.
        // may fail in case global temp table already exists.
        try {
            DatasourceCall databseCall = (DatasourceCall)getCalls().elementAt(getCalls().size() - 1);
            executeCall(databseCall);
        } catch (DatabaseException databaseEx) {
            // ignore
        }                

        // second call - populate temp table.
        // if that fails save the exception and untill cleanup
        if(ex == null) {
            try {
                DatasourceCall databseCall = (DatasourceCall)getCalls().elementAt(getCalls().size() - 2);
                executeCall(databseCall);
            } catch (DatabaseException databaseEx) {
                ex = databaseEx;
            }
        }
        
        // third (a call per table) - delete from original tables calls.
        // if that fails save the exception untill cleanup
        for (int index = getCalls().size() - 3; index >= 1 && ex == null; index--) {
            DatasourceCall databseCall = (DatasourceCall)getCalls().elementAt(index);
            try {
                // returns the number of rows removed from the first table in insert order
                returnedRowCount = (Integer)executeCall(databseCall);
            } catch (DatabaseException databaseEx) {
                ex = databaseEx;
            }
        }

        // last call - cleanup temp table.
        // ignore exceptions here.
        try {
            DatasourceCall databseCall = (DatasourceCall)getCalls().elementAt(0);
            executeCall(databseCall);
        } catch (DatabaseException databaseEx) {
            // ignore
        }

        if(ex != null) {
            throw ex;
        }
        
        return returnedRowCount;
    }

    /**
     * INTERNAL:
     * Delete an object.  Assume call is correct
     * @exception  DatabaseException - an error has occurred on the database
     */
    public Integer deleteObject() throws DatabaseException {
        if (hasMultipleCalls()) {
            Integer returnedRowCount = null;

            // Deletion must occur in reverse order.
            for (int index = getCalls().size() - 1; index >= 0; index--) {
                DatasourceCall databseCall = (DatasourceCall)getCalls().elementAt(index);
                Integer rowCount = (Integer)executeCall(databseCall);
                if ((index == (getCalls().size() - 1)) || (rowCount.intValue() <= 0)) {// Row count returned must be from first table or zero if any are zero.
                    returnedRowCount = rowCount;
                }
            }
            return returnedRowCount;
        } else {
            return (Integer)executeCall();
        }
    }

    /**
     * INTERNAL:
     * Execute a call.
     * @exception  DatabaseException - an error has occurred on the database
     */
    public Object execute() throws DatabaseException {
        return executeCall();
    }
    
    /**
     * Execute the call.  It is assumed the call has been fully prepared.
     * @exception  DatabaseException - an error has occurred on the database.
     */
    protected Object executeCall() throws DatabaseException {
        return executeCall(this.call);
    }

    /**
     * Execute the call.  It is assumed the call has been fully prepared.
     * @exception  DatabaseException - an error has occurred on the database.
     */
    protected Object executeCall(DatasourceCall databaseCall) throws DatabaseException {
        // For CR 2923 must move to session we will execute call on now
        // so correct DatasourcePlatform used by translate.        
        AbstractSession sessionToUse = this.query.getExecutionSession();
        DatasourceCall clonedCall = (DatasourceCall)databaseCall.clone();
        clonedCall.setQuery(this.query);
        clonedCall.translate(this.query.getTranslationRow(), getModifyRow(), sessionToUse);
        return sessionToUse.executeCall(clonedCall, this.query.getTranslationRow(), this.query);
    }

    /**
     * Execute a non selecting call.
     * @exception  DatabaseException - an error has occurred on the database.
     * @return the row count.
     */
    public Integer executeNoSelect() throws DatabaseException {
        return executeNoSelectCall();
    }

    /**
     * Execute a non selecting call.
     * @exception  DatabaseException - an error has occurred on the database.
     * @return the row count.
     */
    public Integer executeNoSelectCall() throws DatabaseException {
        if (hasMultipleCalls()) {
            Integer returnedRowCount = null;
            for (int index = 0; index < getCalls().size(); index++) {
                DatasourceCall databseCall = (DatasourceCall)getCalls().elementAt(index);
                Integer rowCount = (Integer)executeCall(databseCall);
                if ((index == 0) || (rowCount.intValue() <= 0)) {// Row count returned must be from first table or zero if any are zero.
                    returnedRowCount = rowCount;
                }
            }
            return returnedRowCount;
        } else {
            return (Integer)executeCall();
        }
    }

    /**
     * INTERNAL:
     * Execute a selecting call.
     * @exception  DatabaseException - an error has occurred on the database
     */
    public Vector executeSelect() throws DatabaseException {
        return executeSelectCall();
    }

    /**
     * INTERNAL:
     * Execute a selecting call.
     * @exception  DatabaseException - an error has occurred on the database
     */
    public Vector executeSelectCall() throws DatabaseException {
        if (hasMultipleCalls()) {
            Vector results = new Vector();
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatasourceCall databseCall = (DatasourceCall)callsEnum.nextElement();
                Helper.addAllToVector(results, (Vector)executeCall(databseCall));
            }

            return results;
        } else {
            return (Vector)executeCall();
        }
    }

    /**
     * Return the call.
     */
    public DatasourceCall getCall() {
        return call;
    }

    /**
     * Normally only a single call is used, however multiple table may require multiple calls on write.
     * This is lazy initialised to conserve space.
     */
    public Vector getCalls() {
        if (calls == null) {
            calls = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(3);
        }
        return calls;
    }

    /**
     * Normally only a single call is used, however multiple table may require multiple calls on write.
     * This is lazy initialised to conserve space.
     */
    public boolean hasMultipleCalls() {
        return (this.calls != null) && (!this.calls.isEmpty());
    }

    /**
     * Insert the object.  Assume the call is correct.
     * @exception  DatabaseException - an error has occurred on the database
     */
    public void insertObject() throws DatabaseException {
        ClassDescriptor descriptor = getDescriptor();
        boolean usesSequencing = descriptor.usesSequenceNumbers();
        boolean shouldAcquireValueAfterInsert = false;
        if (usesSequencing) {
            shouldAcquireValueAfterInsert = descriptor.getSequence().shouldAcquireValueAfterInsert();
        }
        Collection returnFields = null;
        if (descriptor.hasReturningPolicy()) {
            returnFields = descriptor.getReturningPolicy().getFieldsToMergeInsert();
        }

        // Check to see if sequence number should be retrieved after insert
        if (usesSequencing && !shouldAcquireValueAfterInsert) {
            // PERF: Unit of work always assigns sequence, so only need to check it here for non unit of work/change set query.
            if (getWriteObjectQuery().getObjectChangeSet() == null) {
                // This is the normal case.  Update object with sequence number before insert.
                updateObjectAndRowWithSequenceNumber();
            }
        }

        if (hasMultipleCalls()) {
            int size = this.calls.size();
            for (int index = 0; index < size; index++) {
                DatasourceCall databseCall = (DatasourceCall)this.calls.get(index);
                if ((index > 0) && isExpressionQueryMechanism()
                        && this.query.shouldCascadeOnlyDependentParts() && !descriptor.hasMultipleTableConstraintDependecy()) {
                    DatabaseTable table = descriptor.getMultipleTableInsertOrder().get(index);
                    this.query.getSession().getCommitManager().addDeferredCall(table, databseCall, this);
                } else {
                    Object result = executeCall(databseCall);
                    // Set the return row if one was returned (Postgres).
                    if (result instanceof AbstractRecord) {
                        this.query.setProperty("output", result);
                    }
                    if (returnFields != null) {
                        updateObjectAndRowWithReturnRow(returnFields, index == 0);
                    }
                    if ((index == 0) && usesSequencing && shouldAcquireValueAfterInsert) {
                        updateObjectAndRowWithSequenceNumber();
                    }
                }
            }
        } else {
            Object result = executeCall();
            // Set the return row if one was returned (Postgres).
            if (result instanceof AbstractRecord) {
                this.query.setProperty("output", result);
            }
            if (returnFields != null) {
                updateObjectAndRowWithReturnRow(returnFields, true);
            }
            if (usesSequencing && shouldAcquireValueAfterInsert) {
                updateObjectAndRowWithSequenceNumber();
            }
        }

        // Bug 3110860: RETURNINGPOLICY-OBTAINED PK CAUSES LOB TO BE INSERTED INCORRECTLY
        // The deferred locator SELECT calls should be generated and executed after ReturningPolicy
        // merges PK obtained from the db into the object held by the query.
        //
        //Oracle thin driver handles LOB differently. During the insert, empty lob would be
        //insert first, and then the LOb locator is retrieved and LOB data are written through
        //the locator.
        // 
        // Bug 2804663 - LOBValueWriter is no longer a singleton, so we execute any deferred
        // select calls through the DatabaseAccessor which holds the writer instance
        AbstractSession executionSession = this.query.getExecutionSession();
        for (Accessor accessor : executionSession.getAccessors()) {
            accessor.flushSelectCalls(executionSession);
        }
    }

    /**
     * Execute the call that was deferred to the commit manager.
     * This is used to allow multiple table batching and deadlock avoidance.
     */
    public void executeDeferredCall(DatasourceCall call) {
        Object result = executeCall(call);
        // Set the return row if one was returned (Postgres).
        if (result instanceof AbstractRecord) {
            this.query.setProperty("output", result);
        }
        Collection returnFields = null;
        if (this.query.getDescriptor().hasReturningPolicy()) {
            returnFields = this.query.getDescriptor().getReturningPolicy().getFieldsToMergeInsert();
        }
        if (returnFields != null) {
            updateObjectAndRowWithReturnRow(returnFields, false);
        }
    }
    
    /**
     * Return true if this is a call query mechanism
     */
    public boolean isCallQueryMechanism() {
        return true;
    }

    /**
     * INTERNAL:
     * This is different from 'prepareForExecution' in that this is called on the original query,
     * and the other is called on the copy of the query.
     * This query is copied for concurrency so this prepare can only setup things that
     * will apply to any future execution of this query.
     */
    public void prepare() {
        if ((!hasMultipleCalls()) && (getCall() == null)) {
            throw QueryException.sqlStatementNotSetProperly(getQuery());
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
        DatabaseQuery query = getQuery();
        AbstractSession executionSession = query.getExecutionSession();
        if (hasMultipleCalls()) {
            for (DatasourceCall call : (List<DatasourceCall>)getCalls()) {
                call.prepare(executionSession);
            }
        } else if (getCall() != null) {
            getCall().prepare(executionSession);
        }
    }
    
    /**
     * Pre-build configure the call.
     */
    public void prepareCursorSelectAllRows() throws QueryException {
        getCall().returnCursor();
        prepareCall();
    }

    /**
     * Pre-build configure the call.
     */
    public void prepareDeleteAll() {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatasourceCall call = (DatasourceCall)callsEnum.nextElement();
                call.returnNothing();
            }
        } else {
            getCall().returnNothing();
        }
        prepareCall();
    }

    /**
     * Pre-build configure the call.
     */
    public void prepareDeleteObject() {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatasourceCall call = (DatasourceCall)callsEnum.nextElement();
                call.returnNothing();
            }
        } else {
            getCall().returnNothing();
        }
        prepareCall();
    }

    /**
     * Pre-build configure the call.
     */
    public void prepareDoesExist(DatabaseField field) {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                ((DatasourceCall)callsEnum.nextElement()).returnOneRow();
            }
        } else {
            getCall().returnOneRow();
        }
        prepareCall();
    }

    /**
     * Pre-build configure the call.
     */
    public void prepareExecuteNoSelect() {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                ((DatasourceCall)callsEnum.nextElement()).returnNothing();
            }
        } else {
            getCall().returnNothing();
        }
        prepareCall();
    }

    /**
     * Pre-build configure the call. This method assumes the query was built
     * using a stored procedure query which is a single call.
     * 
     * The return type on the call will already be set and 
     */
    public void prepareExecute() {
        getCall().setExecuteUpdate();
        prepareCall();
    }
    
    /**
     * Pre-build configure the call.
     */
    public void prepareExecuteSelect() {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatasourceCall databseCall = (DatasourceCall)callsEnum.nextElement();
                databseCall.returnManyRows();
            }
        } else {
            getCall().returnManyRows();
        }
        prepareCall();
    }

    /**
     * Pre-build configure the call.
     */
    public void prepareInsertObject() {
        if (hasMultipleCalls()) {
            int size = this.calls.size();
            for (int index = 0; index < size; index++) {
                DatabaseCall call = (DatabaseCall)this.calls.get(index);
                if (!call.isReturnSet()) {
                    call.returnNothing();
                }
            }
        } else {
            if (!this.call.isReturnSet()) {
                this.call.returnNothing();
            }
        }
        prepareCall();
    }

    /**
     * Prepare the report items.
     * Indexes of results need to be calculated.
     */
    protected void prepareReportQueryItems(){
        //calculate indexes after normalize to insure expressions are set up correctly
        //take into account any field expressions added to the ReportQuery
        ReportQuery query = (ReportQuery)getQuery();
        int itemOffset = query.getQueryExpressions().size();
        for (Iterator items = query.getItems().iterator(); items.hasNext();) {
            ReportItem item = (ReportItem) items.next();
            item.setResultIndex(itemOffset);
            if (item.getAttributeExpression() != null) {
                if (item.hasJoining()){
                    itemOffset = item.getJoinedAttributeManager().computeJoiningMappingIndexes(true, getSession(), itemOffset);
                } else {
                    if (item.getDescriptor() != null) {
                        itemOffset += item.getDescriptor().getAllFields().size();
                    } else {
                        ++itemOffset; //only a single attribute can be selected
                    }
                }
            }
        }        
    }
    
    /**
     * Pre-build configure the call.
     */
    public void prepareReportQuerySelectAllRows() {
        prepareReportQueryItems();
        prepareExecuteSelect();
    }

    /**
     * Prepare for a sub select using a call.
     */
    public void prepareReportQuerySubSelect() {
        prepareReportQueryItems();
        prepareCall();
    }

    /**
     * Pre-build configure the call.
     */
    public void prepareSelectAllRows() {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatasourceCall databseCall = (DatasourceCall)callsEnum.nextElement();
                databseCall.returnManyRows();
            }
        } else {
            getCall().returnManyRows();
        }
        prepareCall();
    }

    /**
     * Pre-build configure the call.
     */
    public void prepareSelectOneRow() {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatasourceCall databseCall = (DatasourceCall)callsEnum.nextElement();
                databseCall.returnOneRow();
            }
        } else {
            getCall().returnOneRow();
        }
        prepareCall();
    }

    /**
     * Pre-build configure the call.
     */
    public void prepareUpdateObject() {
        if (hasMultipleCalls()) {
            int size = this.calls.size();
            for (int index = 0; index < size; index++) {
                DatabaseCall call = (DatabaseCall)this.calls.get(index);
                if (!call.isReturnSet()) {
                    call.returnNothing();
                }
            }
        } else if (getCall() != null) {
            if (!call.isReturnSet()) {
                this.call.returnNothing();
            }
        }
        prepareCall();
    }

    /**
       * Pre-build configure the call.
       */
    public void prepareUpdateAll() {
        if (getCall() != null) {
            getCall().returnNothing();
        }

        prepareCall();
    }

    /**
     * Read all rows from the database. Assume call is correct returns the required fields.
     * @return Vector containing the database rows
     * @exception  DatabaseException - an error has occurred on the database
     */
    public Vector selectAllReportQueryRows() throws DatabaseException {
        return executeSelect();
    }

    /**
     * Read all rows from the database. Assume call is correct returns the required fields.
     * @return Vector containing the database rows
     * @exception  DatabaseException - an error has occurred on the database
     */
    public Vector selectAllRows() throws DatabaseException {
        return executeSelectCall();
    }

    /**
     * Read a single row from the database. Assume call is correct.
     * @return row containing data
     * @exception  DatabaseException - an error has occurred on the database
     */
    public AbstractRecord selectOneRow() throws DatabaseException {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatasourceCall databaseCall = (DatasourceCall)callsEnum.nextElement();
                AbstractRecord result = (AbstractRecord)executeCall(databaseCall);
                if (result != null) {
                    return result;
                }
            }

            return null;
        } else {
            return (AbstractRecord)executeCall();
        }
    }

    /**
     * Perform a does exist check
     * @param field - the field used for does exist check
     * @return  the associated row from the database
     * @exception  DatabaseException - an error has occurred on the database
     */
    public AbstractRecord selectRowForDoesExist(DatabaseField field) throws DatabaseException {
        if (hasMultipleCalls()) {
            for (Enumeration callsEnum = getCalls().elements(); callsEnum.hasMoreElements();) {
                DatasourceCall databaseCall = (DatasourceCall)callsEnum.nextElement();
                AbstractRecord result = (AbstractRecord)executeCall(databaseCall);
                if (result != null) {
                    return result;
                }
            }

            return null;
        } else {
            return (AbstractRecord)executeCall();
        }
    }

    /**
     * Set the call.
     */
    public void setCall(DatasourceCall call) {
        this.call = call;
        if (call != null) {
            call.setQuery(getQuery());
        }
    }

    /**
     * Normally only a single call is used, however multiple table may require multiple calls on write.
     * This is lazy initialised to conserve space.
     */
    protected void setCalls(Vector calls) {
        this.calls = calls;
    }

    /**
     * Update the object.  Assume the call is correct.
     * @exception  DatabaseException - an error has occurred on the database.
     * @return the row count.
     */
    public Integer updateObject() throws DatabaseException {
        Collection returnFields = null;
        ClassDescriptor descriptor = getDescriptor();
        if (descriptor.hasReturningPolicy()) {
            returnFields = descriptor.getReturningPolicy().getFieldsToMergeUpdate();
        }
        Integer returnedRowCount = null;
        if (hasMultipleCalls()) {
            int size = this.calls.size();
            for (int index = 0; index < size; index++) {
                DatasourceCall databseCall = (DatasourceCall)this.calls.get(index);
                if ((index > 0) && isExpressionQueryMechanism()
                        && this.query.shouldCascadeOnlyDependentParts() && !descriptor.hasMultipleTableConstraintDependecy()) {
                    DatabaseTable table = descriptor.getMultipleTableInsertOrder().get(index);
                    this.query.getSession().getCommitManager().addDeferredCall(table, databseCall, this);
                } else {
                    Object result = executeCall(databseCall);
                    // Set the return row if one was returned (Postgres).
                    Integer rowCount;
                    if (result instanceof AbstractRecord) {
                        this.query.setProperty("output", result);
                        rowCount = Integer.valueOf(1);
                    } else {
                        rowCount = (Integer)result;
                    }
                    if ((index == 0) || (rowCount.intValue() <= 0)) {// Row count returned must be from first table or zero if any are zero.
                        returnedRowCount = rowCount;
                    }
                    if (returnFields != null) {
                        updateObjectAndRowWithReturnRow(returnFields, false);
                    }
                }
            }
        } else {
            Object result = executeCall();
            // Set the return row if one was returned (Postgres).
            if (result instanceof AbstractRecord) {
                this.query.setProperty("output", result);
                returnedRowCount = Integer.valueOf(1);
            } else {
                returnedRowCount = (Integer)result;
            }
            if (returnFields != null) {
                updateObjectAndRowWithReturnRow(returnFields, false);
            }
        }

        //Oracle thin driver handles LOB differently. During the insert, empty lob would be
        //insert first, and then the LOb locator is retrieved and LOB data are written through
        //the locator.
        // 
        // Bug 2804663 - LOBValueWriter is no longer a singleton, so we execute any deferred
        // select calls through the DatabaseAccessor which holds the writer instance
        //
        // Building of SELECT statements is no longer done in DatabaseAccessor.basicExecuteCall
        // because DatabaseCall.isUpdateCall() can't recognize update in case StoredProcedureCall
        // is used.
        AbstractSession executionSession = this.query.getExecutionSession();
        for (Accessor accessor : executionSession.getAccessors()) {
            accessor.flushSelectCalls(executionSession);
        }
        return returnedRowCount;
    }

    /**
       * Update the rows on the database.  Assume the call is correct.
       * @exception  DatabaseException - an error has occurred on the database.
       */
    public Integer updateAll() throws DatabaseException {
        if(((UpdateAllQuery)this.query).isPreparedUsingTempStorage() && getExecutionSession().getPlatform().supportsTempTables()) {
            return updateAllUsingTempTables();
        } else {
            Integer rowCount = executeNoSelectCall();
            if(((UpdateAllQuery)this.query).isPreparedUsingTempStorage()) {
                // the query was prepared using Oracle anonymous block 
                AbstractRecord outputRow = (AbstractRecord)this.query.getProperty("output");
                rowCount = (Integer)outputRow.get("ROW_COUNT");
            }
            return rowCount;
        }
    }

    /**
     * Execute updateAll using temp tables
     * @exception  DatabaseException - an error has occurred on the database.
     * @return the row count.
     */
    public Integer updateAllUsingTempTables() throws DatabaseException {
        int nTables = getCalls().size() / 4;
        DatabaseException ex = null;
        Integer returnedRowCount = null;
        
        // first quarter - crete temp tables calls.
        // may fail in case global temp table already exists.
        for (int index = 0; index < nTables; index++) {
            try {
                DatasourceCall databseCall = (DatasourceCall)getCalls().elementAt(index);
                executeCall(databseCall);
            } catch (DatabaseException databaseEx) {
                // ignore
            }
        }

        // second quarter - populate temp tables calls.
        // if that fails save the exception and until cleanup
        for (int index = nTables; index < nTables*2 && ex == null; index++) {
            try {
                DatasourceCall databseCall = (DatasourceCall)getCalls().elementAt(index);
                executeCall(databseCall);
            } catch (DatabaseException databaseEx) {
                ex = databaseEx;
            }
        }
        
        // third quarter - update original tables calls.
        // if that fails save the exception and until cleanup
        for (int index = nTables*2; index < nTables*3 && ex == null; index++) {
            try {
                DatasourceCall databseCall = (DatasourceCall)getCalls().elementAt(index);
                Integer rowCount = (Integer)executeCall(databseCall);
                if ((index == nTables*2) || (rowCount.intValue() <= 0)) {// Row count returned must be from first table or zero if any are zero.
                    returnedRowCount = rowCount;
                }
            } catch (DatabaseException databaseEx) {
                ex = databaseEx;
            }
        }
        
        // last quarter - cleanup temp tables calls.
        // ignore exceptions here.
        for (int index = nTables*3; index < nTables*4; index++) {
            try {
                DatasourceCall databseCall = (DatasourceCall)getCalls().elementAt(index);
                executeCall(databseCall);
                } catch (DatabaseException databaseEx) {
                    // ignore
                }
        }

        if(ex != null) {
            throw ex;
        }
        
        return returnedRowCount;
    }

    /**
     * Update the foreign key fields when resolving a bi-directional reference in a UOW.
     * This is rare to occur for non-relational, however if it does each of the calls must be re-executed.
     */
    protected void updateForeignKeyFieldAfterInsert(WriteObjectQuery writeQuery) {
        writeQuery.setModifyRow(this.getDescriptor().getObjectBuilder().buildRow(writeQuery.getObject(), this.getSession(), WriteType.INSERT));

        // For CR 2923 must move to session we will execute call on now
        // so correct DatasourcePlatform used by translate. 
        AbstractSession sessionToUse = this.query.getExecutionSession();

        // yes - this is a bit ugly...
        Vector calls = ((DatasourceCallQueryMechanism)this.getDescriptor().getQueryManager().getUpdateQuery().getQueryMechanism()).getCalls();
        for (Enumeration stream = calls.elements(); stream.hasMoreElements();) {
            DatasourceCall call = (DatasourceCall)((DatasourceCall)stream.nextElement()).clone();
            call.setQuery(writeQuery);
            sessionToUse.executeCall(call, this.getTranslationRow(), writeQuery);
        }
    }
}
