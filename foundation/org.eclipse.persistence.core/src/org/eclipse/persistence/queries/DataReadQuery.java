/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>:
 * Concrete class to perform read using raw SQL.
 * <p>
 * <p><b>Responsibilities</b>:
 * Execute a selecting raw SQL string.
 * This returns a Collection of the Records representing the result set.
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public class DataReadQuery extends ReadQuery {
    protected ContainerPolicy containerPolicy;
    
    //** For EJB 3 support returns results without using the AbstractRecord */
    protected boolean useAbstractRecord = true;

    /**
     * PUBLIC:
     * Initialize the state of the query.
     */
    public DataReadQuery() {
        super();
        this.shouldMaintainCache = false;
        this.useAbstractRecord = true;
        setContainerPolicy(ContainerPolicy.buildDefaultPolicy());
    }

    /**
     * PUBLIC:
     * Initialize the query to use the specified SQL string.
	 * Warning: Allowing an unverified SQL string to be passed into this 
	 * method makes your application vulnerable to SQL injection attacks. 
     */
    public DataReadQuery(String sqlString) {
        this();
        setSQLString(sqlString);
    }

    /**
     * PUBLIC:
     * Initialize the query to use the specified call.
     */
    public DataReadQuery(Call call) {
        this();
        setCall(call);
    }

    /**
     * INTERNAL:
     * <P> This method is called by the object builder when building an original.
     * It will cause the original to be cached in the query results if the query
     * is set to do so.
     */
    public void cacheResult(Object results) {
        setTemporaryCachedQueryResults(results);
    }

    /**
     * INTERNAL:
     * Clone the query.
     */
    public Object clone() {
        DataReadQuery cloneQuery = (DataReadQuery)super.clone();
        cloneQuery.setContainerPolicy(getContainerPolicy().clone(cloneQuery));
        return cloneQuery;
    }

    /**
     * INTERNAL:
     * Execute the query. If there are cached results return those.
     * This must override the super to support result caching.
     *
     * @param aSession - the session in which the receiver will be executed.
     * @return An object or vector, the result of executing the query.
     * @exception DatabaseException - an error has occurred on the database
     */
    public Object execute(AbstractSession session, AbstractRecord row) throws DatabaseException {
        if (shouldCacheQueryResults()) {
            if (getContainerPolicy().overridesRead()) {
                throw QueryException.cannotCacheCursorResultsOnQuery(this);
            }
            if (isPrepared()) {// only prepared queries can have cached results.
                Object results = getQueryResults(session, row, true);
                // Bug6138532 - if results are "cached no results", return null immediately
                if (results == InvalidObject.instance) {
                    return null;
                }
                if (results != null) {
                    return results;
                }
            }
        }
        return super.execute(session, row);
    }

    /**
     * INTERNAL:
     * Execute the query.
     * Perform the work to execute the SQL string.
     * @exception DatabaseException an error has occurred on the database
     * @return a collection or cursor of Records representing the result set
     */
    public Object executeDatabaseQuery() throws DatabaseException {
        if (getContainerPolicy().overridesRead()) {
            return getContainerPolicy().execute();
        }
        return executeNonCursor();
    }

    /**
     * INTERNAL:
     * The results are *not* in a cursor, build the collection.
     * Cache the results in temporaryCachedQueryResults.
     */
    protected Object executeNonCursor() throws DatabaseException {
        Vector rows = getQueryMechanism().executeSelect();
        ContainerPolicy containerPolicy = getContainerPolicy();
        Object results = null;
        if (useAbstractRecord) {
            results = containerPolicy.buildContainerFromVector(rows, getSession());
        } else {
            results = containerPolicy.containerInstance(rows.size());
            for (Iterator rowsEnum = rows.iterator(); rowsEnum.hasNext();) {
                containerPolicy.addInto(((AbstractRecord)rowsEnum.next()).getValues(), results, getSession());
            }
        }
        // Bug 6135563 - cache DataReadQuery results verbatim, as ObjectBuilder is not invoked
        cacheResult(results);
        return results;
    }

    /**
     * PUBLIC:
     * Return the query's ContainerPolicy.
     * @return org.eclipse.persistence.internal.queries.ContainerPolicy
     */
    public ContainerPolicy getContainerPolicy() {
        return containerPolicy;
    }

    /**
     * PUBLIC:
     * Return if this is a data read query.
     */
    public boolean isDataReadQuery() {
        return true;
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    protected void prepare() {
        super.prepare();
        getContainerPolicy().prepare(this, getSession());
        if (getContainerPolicy().overridesRead()) {
            return;
        }
        getQueryMechanism().prepareExecuteSelect();
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    public void prepareForExecution() throws QueryException {
        super.prepareForExecution();
        getContainerPolicy().prepareForExecution();
    }

    /**
     * INTERNAL:
     */
    public Object remoteExecute() {
        if (getContainerPolicy().overridesRead()) {
            return getContainerPolicy().remoteExecute();
        }
        return super.remoteExecute();
    }

    /**
     * PUBLIC:
     * Set the container policy.
     */
    public void setContainerPolicy(ContainerPolicy containerPolicy) {
        // Fix for BUG 3337003 - TopLink OX will try to set this to null if
        // it is not set in the deployment XML. So don't allow it to do that.
        if (containerPolicy == null) {
            return;
        }

        this.containerPolicy = containerPolicy;
    }

    /**
     * PUBLIC:
     * Configure the query to use an instance of the specified container class
     * to hold the target objects.
     * The container class must implement (directly or indirectly) the Collection interface.
     */
    public void useCollectionClass(Class concreteClass) {
        setContainerPolicy(ContainerPolicy.buildPolicyFor(concreteClass));
    }

    /**
     * PUBLIC:
     * Use a CursoredStream as the result collection.
     * The initial read size is 10 and page size is 5.
     */
    public void useCursoredStream() {
        useCursoredStream(10, 5);
    }

    /**
     * INTERNAL:
     * Allow changing the default behavior so that AbstractRecords are not returned as query results.  
     */
    public void setUseAbstractRecord(boolean useAbstractRecord){
        this.useAbstractRecord = useAbstractRecord;
    }

    /**
     * PUBLIC:
     * Use a CursoredStream as the result collection.
     * @param initialReadSize the initial number of objects to read
     * @param pageSize the number of objects to read when more objects
     * are needed from the database
     */
    public void useCursoredStream(int initialReadSize, int pageSize) {
        setContainerPolicy(new CursoredStreamPolicy(this, initialReadSize, pageSize));
    }

    /**
     * PUBLIC:
     * Use a CursoredStream as the result collection.
     * @param initialReadSize the initial number of objects to read
     * @param pageSize the number of objects to read when more objects
     * are needed from the database
     * @param sizeQuery a query that will return the size of the result set;
     * this must be set if an expression is not used (i.e. custom SQL)
     */
    public void useCursoredStream(int initialReadSize, int pageSize, ValueReadQuery sizeQuery) {
        setContainerPolicy(new CursoredStreamPolicy(this, initialReadSize, pageSize, sizeQuery));
    }

    /**
     * PUBLIC:
     * Use a ScrollableCursor as the result collection.
     */
    public void useScrollableCursor() {
        useScrollableCursor(10);
    }

    /**
     * PUBLIC:
     * Use a ScrollableCursor as the result collection.
     * @param pageSize the number of elements to be read into a the cursor
     * when more elements are needed from the database.
     */
    public void useScrollableCursor(int pageSize) {
        setContainerPolicy(new ScrollableCursorPolicy(this, pageSize));
    }

    /**
     * PUBLIC:
     * Use a ScrollableCursor as the result collection.
     * @param policy the scrollable cursor policy allows for additional result set options.
     * Example:<p>
     * ScrollableCursorPolicy policy = new ScrollableCursorPolicy()<p>
     * policy.setResultSetType(ScrollableCursorPolicy.TYPE_SCROLL_INSENSITIVE);<p>
     * query.useScrollableCursor(policy);<p>
     */
    public void useScrollableCursor(ScrollableCursorPolicy policy) {
        policy.setQuery(this);
        setContainerPolicy(policy);
    }
}