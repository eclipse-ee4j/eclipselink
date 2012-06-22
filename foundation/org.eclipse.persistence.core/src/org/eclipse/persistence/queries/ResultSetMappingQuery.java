/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.queries;
import java.util.*;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.DatabaseException;
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
    
    protected String resultSetMappingName;
    
    protected SQLResultSetMapping resultSetMapping;
    
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
        this.resultSetMappingName = sqlResultSetMappingName;
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
        this.resultSetMapping.convertClassNamesToClasses(classLoader);
    }

    /**
     * PUBLIC:
     * This will be the SQLResultSetMapping that is used by this query to process
     * the database results
     */
    public void setSQLResultSetMapping(SQLResultSetMapping resultSetMapping){
        this.resultSetMapping = resultSetMapping;
        this.resultSetMappingName = resultSetMapping.getName();
    }

    /**
     * PUBLIC:
     * This will be the SQLResultSetMapping that is used by this query to process
     * the database results
     */
    public void setSQLResultSetMappingName(String name){
        if (name == null && this.resultSetMapping == null){
            //throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_sqlresultsetmapping_in_query"));
        }
        this.resultSetMappingName = name;
        
    }
    
    /**
     * INTERNAL:
     * This method is used to build the results.  Interpreting the 
     * SQLResultSetMapping.
     */
    protected List buildObjectsFromRecords(List databaseRecords){
        List results = new ArrayList(databaseRecords.size() );
        SQLResultSetMapping mapping = this.getSQLResultSetMapping();
        
        if (mapping == null) {
	        for (Iterator iterator = databaseRecords.iterator(); iterator.hasNext();){
                DatabaseRecord record = (DatabaseRecord)iterator.next();
                
                results.add(record.values().toArray());
	        }
        } else {
	        for (Iterator iterator = databaseRecords.iterator(); iterator.hasNext();){
	            if (mapping.getResults().size()>1){
	                Object[] resultElement = new Object[mapping.getResults().size()];
	                DatabaseRecord record = (DatabaseRecord)iterator.next();
	                for (int i = 0;i<mapping.getResults().size();i++){
	                    resultElement[i] = ((SQLResult)mapping.getResults().get(i)).getValueFromRecord(record, this);
	                }
	                results.add(resultElement);
	            }else if (mapping.getResults().size()==1) {
	                DatabaseRecord record = (DatabaseRecord)iterator.next();
	                results.add( ((SQLResult)mapping.getResults().get(0)).getValueFromRecord(record, this));
	            }else {
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

        Vector rows = getQueryMechanism().executeSelect();
        setExecutionTime(System.currentTimeMillis());
        // If using 1-m joins, must set all rows.
        return buildObjectsFromRecords(rows);
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

        getQueryMechanism().prepareExecuteSelect();
    }

    /**
     * PUBLIC:
     * This will be the SQLResultSetMapping that is used by this query to process
     * the database results
     */
    public SQLResultSetMapping getSQLResultSetMapping(){
        if (this.resultSetMapping == null && this.resultSetMappingName != null){
            this.resultSetMapping = this.getSession().getProject().getSQLResultSetMapping(this.resultSetMappingName);
        }
        return this.resultSetMapping;
    }

    /**
     * PUBLIC:
     * Return the result set mapping name.
     */
    public String getSQLResultSetMappingName() {
        return this.resultSetMappingName;
    }
}
