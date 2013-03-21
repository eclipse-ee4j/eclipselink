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
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     06/20/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     07/13/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     08/24/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     09/27/2012-2.5 Guy Pelletier
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import java.util.*;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose</b>:
 * Concrete class to perform read using raw SQL and the SQLResultSetMapping.
 * <p>
 * <p><b>Responsibilities</b>:
 * Execute a selecting raw SQL string.
 * Returns a List of results.  Each item in the list will be another list
 * consisting of the expected populated return types in the order they were
 * specified in the SQLResultSetMapping
 *
 * @see SQLResultSetMapping
 * @author Gordon Yorke
 * @since TopLink Java Essentials
 */
public class ResultSetMappingQuery extends ObjectBuildingQuery {
    protected boolean isExecuteCall;
    protected Vector resultRows;
    
    protected List<String> resultSetMappingNames = new ArrayList<String>();
    protected List<SQLResultSetMapping> resultSetMappings = new ArrayList<SQLResultSetMapping>();
    
    /**
     * PUBLIC:
     * Initialize the state of the query.
     */
    public ResultSetMappingQuery() {
        super();
   }

    /**
     * PUBLIC:
     * Initialize the query to use the specified call.
     */
    public ResultSetMappingQuery(Call call) {
        this();
        setCall(call);
    }

    /**
     * PUBLIC:
     * Initialize the query to use the specified call and SQLResultSetMapping
     */
    public ResultSetMappingQuery(Call call, String sqlResultSetMappingName) {
        this();
        setCall(call);
        this.resultSetMappingNames.add(sqlResultSetMappingName);
    }

    /**
     * PUBLIC:
     * This will be the SQLResultSetMapping that is used by this query to process
     * the database results
     */
    public void addSQLResultSetMapping(SQLResultSetMapping resultSetMapping){
        this.resultSetMappings.add(resultSetMapping);
        this.resultSetMappingNames.add(resultSetMapping.getName());
    }
    
    /**
     * PUBLIC:
     * Add a SQLResultSetMapping that is used by this query to process the 
     * database results.
     */
    public void addSQLResultSetMappingName(String name){
        if (name == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_sqlresultsetmapping_in_query"));
        }
        
        this.resultSetMappingNames.add(name);
    }
    
    /**
     * INTERNAL:
     * <P> This method is called by the object builder when building an original.
     * It will cause the original to be cached in the query results if the query
     * is set to do so.
     */
    @Override
    public void cacheResult(Object unwrappedOriginal) {
        Object cachableObject = unwrappedOriginal;
        if (shouldUseWrapperPolicy()){
            cachableObject = getSession().wrapObject(unwrappedOriginal);
        }
        setTemporaryCachedQueryResults(cachableObject);
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this ResultSetMapping to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     */
    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader){
        for (SQLResultSetMapping mapping : this.resultSetMappings) {
            mapping.convertClassNamesToClasses(classLoader);
        }
    }

    /**
     * PUBLIC:
     * This will be the SQLResultSetMapping that is used by this query to process
     * the database results
     */
    public void setSQLResultSetMapping(SQLResultSetMapping resultSetMapping) {
        addSQLResultSetMapping(resultSetMapping);
    }
    
    /**
     * PUBLIC:
     * This will be the SQLResultSetMappings that are used by this query to 
     * process the database results
     */
    public void setSQLResultSetMappings(List<SQLResultSetMapping> resultSetMappings) {
        this.resultSetMappings = resultSetMappings;
    }

    /**
     * PUBLIC:
     * This will be the SQLResultSetMapping that is used by this query to process
     * the database results
     */
    public void setSQLResultSetMappingName(String name) {
        addSQLResultSetMappingName(name);
    }
    
    /**
     * PUBLIC:
     * This will be the SQLResult
     * @param names
     */
    public void setSQLResultSetMappingNames(List<String> names) {
        if (names.isEmpty()) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_sqlresultsetmapping_in_query"));
        }
        
        this.resultSetMappingNames = names;
    }
    
    /**
     * INTERNAL:
     * This method is used to build the results. Interpreting the 
     * SQLResultSetMapping(s).
     */
    public List buildObjectsFromRecords(List databaseRecords){
        if (getSQLResultSetMappings().size() > 1) {
            int numberOfRecords = databaseRecords.size();
            List results = new ArrayList(numberOfRecords);
        
            for (int recordIndex = 0; recordIndex < numberOfRecords; recordIndex++) {
                Object records = databaseRecords.get(recordIndex);
                
                if (records instanceof Map) {
                    // We have a map keyed on named ref_cursors
                    Map recordsMap = (Map) records;
                    
                    for (Object cursor : recordsMap.keySet()) {
                        results.add(buildObjectsFromRecords((List) recordsMap.get(cursor), getSQLResultSetMappings().get(recordIndex)));
                        recordIndex++;
                    }
                } else {
                    // Regular list of records, iterate through them.
                    results.add(buildObjectsFromRecords((List) records, getSQLResultSetMappings().get(recordIndex)));
                }
            }
        
            return results;
        } else {
            return buildObjectsFromRecords(databaseRecords, getSQLResultSetMapping());
        }
    }
    
    /**
     * INTERNAL:
     * This method is used to build the results with the SQLResultSetMapping
     * at the given index.
     */
    public List buildObjectsFromRecords(List databaseRecords, int index){
        if (getSQLResultSetMappings().isEmpty()) {
            return buildObjectsFromRecords(databaseRecords, null);
        } else {
            return buildObjectsFromRecords(databaseRecords, getSQLResultSetMappings().get(index));
        }
    }
    
    /**
     * INTERNAL:
     * This method is used to build the results. Interpreting the SQLResultSetMapping.
     */
    protected List buildObjectsFromRecords(List databaseRecords, SQLResultSetMapping mapping) {
        int numberOfRecords = databaseRecords.size();
        List results = new ArrayList(numberOfRecords);
        
        if (mapping == null) {
            for (Iterator iterator = databaseRecords.iterator(); iterator.hasNext();) {
                DatabaseRecord record = (DatabaseRecord)iterator.next();                
                results.add(record.values().toArray());
            }
        } else {
            for (Iterator iterator = databaseRecords.iterator(); iterator.hasNext();) {
                if (mapping.getResults().size() > 1) {
                    Object[] resultElement = new Object[mapping.getResults().size()];
                    DatabaseRecord record = (DatabaseRecord)iterator.next();
                    for (int i = 0; i < mapping.getResults().size(); i++) {
                        resultElement[i] = ((SQLResult)mapping.getResults().get(i)).getValueFromRecord(record, this);
                    }
                    results.add(resultElement);
                } else if (mapping.getResults().size() == 1) {
                    DatabaseRecord record = (DatabaseRecord)iterator.next();
                    results.add(((SQLResult)mapping.getResults().get(0)).getValueFromRecord(record, this));
                } else {
                    return results;
                }
            }
        }
        
        return results;
    }

    /**
     * INTERNAL:
     * Executes the prepared query on the datastore.
     */
    @Override
    public Object executeDatabaseQuery() throws DatabaseException {
        if (getSession().isUnitOfWork()) {
            UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl)getSession();

            // Note if a nested unit of work this will recursively start a
            // transaction early on the parent also.
            if (isLockQuery()) {
                if ((!unitOfWork.getCommitManager().isActive()) && (!unitOfWork.wasTransactionBegunPrematurely())) {
                    unitOfWork.beginTransaction();
                    unitOfWork.setWasTransactionBegunPrematurely(true);
                }
            }
            if (unitOfWork.isNestedUnitOfWork()) {
                // execute in parent UOW then register normally here.
                UnitOfWorkImpl nestedUnitOfWork = (UnitOfWorkImpl)getSession();
                setSession(nestedUnitOfWork.getParent());
                Object result = executeDatabaseQuery();
                setSession(nestedUnitOfWork);
                Object clone = registerIndividualResult(result, null, unitOfWork, null, null);

                if (shouldUseWrapperPolicy()) {
                    clone = getDescriptor().getObjectBuilder().wrapObject(clone, unitOfWork);
                }
                return clone;
            }
        }
        session.validateQuery(this);// this will update the query with any settings

        if (getQueryId() == 0) {
            setQueryId(getSession().getNextQueryId());
        }

        if (getCall().isExecuteUpdate()) {
            DatabaseCall call = ((StoredProcedureCall) getQueryMechanism().execute());
            setExecutionTime(System.currentTimeMillis());
            return call;
        } else {
            Vector rows = getQueryMechanism().executeSelect();
            setExecutionTime(System.currentTimeMillis());
            // If using 1-m joins, must set all rows.
            return buildObjectsFromRecords(rows);
        }
    }
    
    /**
     * PUBLIC:
     * Return true if there are results set mappings associated with this query.
     */
    public boolean hasResultSetMappings() {
        return ! getSQLResultSetMappings().isEmpty();
    }
    
    /**
     * PUBLIC: Return true if this is a result set mapping query.
     */
    public boolean isResultSetMappingQuery() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    @Override
    protected void prepare() {
        if ((!shouldMaintainCache()) && shouldRefreshIdentityMapResult()) {
            throw QueryException.refreshNotPossibleWithoutCache(this);
        }

        getQueryMechanism().prepare();

        if (isExecuteCall) {
            getQueryMechanism().prepareExecute();
        } else {
            getQueryMechanism().prepareExecuteSelect();
        }
    }

    /**
     * PUBLIC:
     * This will be the SQLResultSetMapping that is used by this query to process
     * the database results
     */
    public SQLResultSetMapping getSQLResultSetMapping() {
        if (resultSetMappings.isEmpty()) {
            if (resultSetMappingNames.isEmpty()) {
                return null;
            } else {
                return getSession().getProject().getSQLResultSetMapping(resultSetMappingNames.get(0));
            }
        }
        
        return resultSetMappings.get(0);
    }
    
    /**
     * PUBLIC:
     * This will be the SQLResultSetMapping that is used by this query to process
     * the database results
     */
    public List<SQLResultSetMapping> getSQLResultSetMappings() {
        if (this.resultSetMappings.isEmpty()) {
            ArrayList<SQLResultSetMapping> list = new ArrayList<SQLResultSetMapping>();
            for (String resultSetMappingName : this.resultSetMappingNames) {
                list.add(getSession().getProject().getSQLResultSetMapping(resultSetMappingName));
            }
            
            return list;
        } else {
            return resultSetMappings;
        }
    }
    
    /**
     * PUBLIC:
     * Return the result set mapping name.
     */
    public String getSQLResultSetMappingName() {
        return this.resultSetMappingNames.get(0);
    }
    
    /**
     * PUBLIC:
     * Return the result set mapping name.
     */
    public List<String> getSQLResultSetMappingNames() {
        return this.resultSetMappingNames;
    }
    
    /**
     * PUBLIC:
     * Set to true if you the actual jdbc result set returned from query
     * execution. This will unprepare the query in case it was executed
     * previously for a getResultList() call instead (or vice versa)
     */
    public void setIsExecuteCall(boolean isExecuteCall) {
        this.isExecuteCall = isExecuteCall;
        
        // Force the query to prepare.
        setIsPrepared(false);
    }
}
