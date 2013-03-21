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
package org.eclipse.persistence.queries;

import java.util.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.config.ResultType;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.converters.Converter;

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
    
    /**
     * Allow return type to be configured, MAP, ARRAY, VALUE, ATTRIBUTE (MAP is the default, i.e. DatabaseRecord).
     */
    protected int resultType;
    
    /** A Map (DatabaseRecord) is returned for each row. */
    public static final int MAP = 0;
    /** An Object[] of values is returned for each row. */
    public static final int ARRAY = 1;
    /** A single value is returned. */
    public static final int VALUE = 2;
    /** A single value is returned for each row. */
    public static final int ATTRIBUTE = 3;
    /** Auto, a single value if a single field is selected, otherwise an Object[] (JPA default). */
    public static final int AUTO = 4;

    /**
     * PUBLIC:
     * Initialize the state of the query.
     */
    public DataReadQuery() {
        super();
        this.shouldMaintainCache = false;
        this.resultType = MAP;
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
    @Override
    public void cacheResult(Object results) {
        setTemporaryCachedQueryResults(results);
    }

    /**
     * INTERNAL:
     * Clone the query.
     */
    @Override
    public Object clone() {
        DataReadQuery cloneQuery = (DataReadQuery)super.clone();
        cloneQuery.containerPolicy = this.containerPolicy.clone(cloneQuery);
        return cloneQuery;
    }

    /**
     * INTERNAL:
     * Execute the query. If there are cached results return those.
     * This must override the super to support result caching.
     *
     * @param session - the session in which the receiver will be executed.
     * @return An object or collection, the result of executing the query.
     * @exception DatabaseException - an error has occurred on the database
     */
    @Override
    public Object execute(AbstractSession session, AbstractRecord row) throws DatabaseException {
        if (shouldCacheQueryResults()) {
            if (this.containerPolicy.overridesRead()) {
                throw QueryException.cannotCacheCursorResultsOnQuery(this);
            }
            if (this.isPrepared) {// only prepared queries can have cached results.
                Object results = getQueryResults(session, row, true);
                // Bug6138532 - if results are "cached no results", return null or an empty collection.
                if (results == InvalidObject.instance) {
                    if (this.resultType == VALUE) {
                        return null;
                    } else {
                        return this.containerPolicy.containerInstance(0);
                    }
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
    @Override
    public Object executeDatabaseQuery() throws DatabaseException {
        if (getContainerPolicy().overridesRead()) {
            return getContainerPolicy().execute();
        }
        return executeNonCursor();
    }


    /**
     * INTERNAL:
     * Conversion not supported.
     */
    public Converter getValueConverter() {
        return null;
    }


    /**
     * INTERNAL:
     * Build the result value for the row.
     */
    @Override
    public Object buildObject(AbstractRecord row) {
        if (this.resultType == AUTO) {
            List values = row.getValues();
            if (values.size() == 1) {
                return row.getValues().get(0);
            } else {
                return row.getValues().toArray();
            }
        } else if (this.resultType == ARRAY) {
            return row.getValues().toArray();
        } else if (this.resultType == ATTRIBUTE) {
            // Use get with field for XML records.
            Object value = row.get(row.getFields().get(0));
            if (getValueConverter() != null) {
                value = getValueConverter().convertDataValueToObjectValue(value, this.session);
            }
            return value;
        }
        return row;
    }
    
    /**
     * INTERNAL:
     * The results are *not* in a cursor, build the collection.
     * Cache the results in temporaryCachedQueryResults.
     */
    protected Object executeNonCursor() throws DatabaseException {
        Vector rows = getQueryMechanism().executeSelect();
        Object results = null;
        if (this.resultType == VALUE) {
            if (!rows.isEmpty()) {
                AbstractRecord record = (AbstractRecord)rows.get(0);
                // Use get with field for XML records.
                results = record.get(record.getFields().get(0));
                if (getValueConverter() != null) {
                    results = getValueConverter().convertDataValueToObjectValue(results, this.session);
                }
            }
        } else {
            int size = rows.size();
            ContainerPolicy containerPolicy = getContainerPolicy();
            results = containerPolicy.containerInstance(size);
            if(containerPolicy.shouldAddAll()) {
                if(size > 0) {
                    List values = new ArrayList(size);
                    for (int index = 0;  index < size; index++) {
                        AbstractRecord row = (AbstractRecord)rows.get(index);
                        Object value = buildObject(row);
                        values.add(value);
                    }
                    containerPolicy.addAll(values, results, this.session, rows, this, null, true);
                }
            } else {
                for (int index = 0;  index < size; index++) {
                    AbstractRecord row = (AbstractRecord)rows.get(index);
                    Object value = buildObject(row);
                    containerPolicy.addInto(value, results, this.session, row, this, null, true);
                }
            }
        }
        // Bug 6135563 - cache DataReadQuery results verbatim, as ObjectBuilder is not invoked
        cacheResult(results);
        return results;
    }

    /**
     * PUBLIC:
     * Return the query's ContainerPolicy.
     */
    public ContainerPolicy getContainerPolicy() {
        return containerPolicy;
    }

    /**
     * PUBLIC:
     * Return if this is a data read query.
     */
    @Override
    public boolean isDataReadQuery() {
        return true;
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    @Override
    protected void prepare() {
        super.prepare();
        this.containerPolicy.prepare(this, this.session);
        if (this.containerPolicy.overridesRead()) {
            return;
        }
        getQueryMechanism().prepareExecuteSelect();
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    @Override
    public void prepareForExecution() throws QueryException {
        super.prepareForExecution();
        this.containerPolicy.prepareForExecution();
    }

    /**
     * INTERNAL:
     * Used by RemoteSession.
     */
    @Override
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
     * Return the result type to be configured, MAP, ARRAY, VALUE, ATTRIBUTE (MAP is the default, DatabaseRecord).
     * @see ResultType
     */
    public int getResultType() {
        return resultType;
    }

    /**
     * Set the result type to be configured, MAP, ARRAY, VALUE, ATTRIBUTE (MAP is the default, DatabaseRecord).
     */
    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    /**
     * Set the result type to be configured, Map, Array, Value, Attribute (Map is the default, DatabaseRecord).
     * @see ResultType
     */
    public void setResultType(String resultType) {
        if (ResultType.Map.equals(resultType)) {
            this.resultType = MAP;
        } else if (ResultType.Array.equals(resultType)) {
            this.resultType = ARRAY;
        } else if (ResultType.Value.equals(resultType)) {
            this.resultType = VALUE;
        } else if (ResultType.Attribute.equals(resultType)) {
            this.resultType = ATTRIBUTE;
        }
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
