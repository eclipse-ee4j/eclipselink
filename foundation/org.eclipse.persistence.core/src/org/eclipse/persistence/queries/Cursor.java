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

import java.sql.*;
import java.util.*;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

/**
 * <p><b>Purpose</b>:
 * Abstract class for CursoredStream and ScrolableCursor
 */
public abstract class Cursor implements Enumeration, Iterator, java.io.Serializable {

    /** The preparedStatement that holds the handle to the database that the results are read from. */
    protected transient Statement statement;

    /** The result set (cursor) that holds the handle to the database that the results are read from. */
    protected transient ResultSet resultSet;

    /** The session that executed the query for the stream. */
    protected transient AbstractSession session;

    /** The root session that executed the call for the query.  Knows the database platform. */
    protected transient AbstractSession executionSession;

    /** The fields expected in the result set. */
    protected transient Vector<DatabaseField> fields;

    /** Cached size of the stream. */
    protected int size = -1;

    /** Read query that initialize the stream. */
    public transient ReadQuery query;
    
    /** Query policy that initialize the stream. */
    public transient CursorPolicy policy;

    /** Internal collection of objects. */
    protected List<Object> objectCollection;

    /** Conforming instances found in memory when building the result. */
    protected Map<Object, Object> initiallyConformingIndex;

    /** SelectionCriteria & translation row ready for incremental conforming. */
    protected Expression selectionCriteriaClone;
    protected AbstractRecord translationRow;
    /** Store the next row, for 1-m joining. */
    protected AbstractRecord nextRow;

    /** Current position in the objectCollection of the stream. */
    protected int position;

    /**
     * INTERNAL:
     * Default constructor.
     */
    public Cursor() {
        super();
    }

    /**
     * INTERNAL:
     */
    public Cursor(DatabaseCall call, CursorPolicy policy) {
        ReadQuery query = policy.getQuery();
        this.query = query;
        this.session = query.getSession();
        this.executionSession = session.getExecutionSession(query);
        this.statement = call.getStatement();
        this.fields = call.getFields();
        this.resultSet = call.getResult();
        this.policy = policy;
        this.objectCollection = new Vector();

        if (query.getSession().isUnitOfWork() && query.isObjectLevelReadQuery()) {
            // Call register on the cursor itself.  This will set up 
            // incremental conforming by setting the 
            // selection criteria clone and arguments, and building the
            // intially conforming index (scans the UOW cache).
            // The incremental registration/conforming is done 
            // in retrieveNext/PreviousObject -> buildAndRegisterObject
            ((ObjectLevelReadQuery)query).registerResultInUnitOfWork(this, (UnitOfWorkImpl)this.session, query.getTranslationRow(), false);// object collection is empty, so setting irrelevant.
        }
    }

    /**
     * PUBLIC:
     * Closes the stream.
     * This should be performed whenever the user has finished with the stream.
     */
    public void close() throws DatabaseException {
        RuntimeException exception = null;
        try {
            if (isClosed()) {
                return;
            }
            try {
                getAccessor().closeCursor(this.resultSet, this.session);
                getAccessor().closeStatement(this.statement, this.session, null);
            } catch (RuntimeException caughtException) {
                exception = caughtException;
            } finally {
                //release the connection (back into the pool if Three tier)
                try {
                    //bug 4668234 -- used to only release connections on server sessions but should always release
                    this.session.releaseReadConnection(this.query.getAccessor());
                } catch (RuntimeException releaseException) {
                    if (exception == null) {
                        throw releaseException;
                    }

                    //else ignore
                }
                if (exception != null) {
                    throw exception;
                }
            }
            this.statement = null;
            this.resultSet = null;
            this.nextRow = null;
        } catch (SQLException sqlException) {
            throw DatabaseException.sqlException(sqlException, getAccessor(), getSession(), false);
        }
    }

    /**
     * Close in case not closed.
     */
    protected void finalize() throws DatabaseException {
        close();
    }

    /**
     * INTERNAL:
     * Return the accessor associated with the cursor.
     */
    public DatabaseAccessor getAccessor() {
        // Assume we have a JDBC accessor
        try {
            return (DatabaseAccessor)this.query.getAccessor();
        } catch (ClassCastException e) {
            throw QueryException.invalidDatabaseAccessor(this.query.getAccessor());
        }
    }

    /**
     * INTERNAL:
     * Retrieve the size of the open cursor by executing a count on the same query as the cursor.
     */
    protected abstract int getCursorSize() throws DatabaseException, QueryException;

    /**
     * INTERNAL:
     * Return the fields for the stream.
     */
    public Vector<DatabaseField> getFields() {
        return fields;
    }

    /**
     * INTERNAL:
     * Conforming instances found in memory when building the result.
     * These objects are returned first by the cursor, and a fast lookup
     * is needed to make sure the same objects appearing in the cursor are
     * filtered out.
     */
    public Map<Object, Object> getInitiallyConformingIndex() {
        return initiallyConformingIndex;
    }

    /**
     * INTERNAL:
     * Return the internal object collection that stores the objects.
     */
    public List<Object> getObjectCollection() {
        return objectCollection;
    }

    /**
     * INTERNAL:
     * Return the number of items to be faulted in for the stream.
     */
    public int getPageSize() {
        return this.policy.getPageSize();
    }

    /**
     * INTERNAL:
     * Return the cursor policy.
     */
    public CursorPolicy getPolicy() {
        return policy;
    }

    /**
     * INTERNAL:
     * Return the position of the stream inside the object collection.
     */
    public abstract int getPosition();

    /**
     * INTERNAL:
     * Return the query associated with the stream.
     */
    public ReadQuery getQuery() {
        return this.query;
    }

    /**
     * INTERNAL:
     * Return the result set (cursor).
     */
    public ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * INTERNAL:
     * The clone of the selection criteria is needed for in-memory conforming
     * each object read from the Cursor.
     */
    public Expression getSelectionCriteriaClone() {
        return selectionCriteriaClone;
    }

    /**
     * INTERNAL:
     * Return the handle to the session
     */
    public AbstractSession getSession() {
        return session;
    }

    /**
     * INTERNAL:
     * Returns the session the underlying call was executed on.  This root
     * session knows the database platform.
     */
    public AbstractSession getExecutionSession() {
        return executionSession;
    }

    /**
     * INTERNAL:
     * Return the Statement.
     */
    protected Statement getStatement() {
        return statement;
    }

    /**
     * INTERNAL:
     * Gets the translation row the query was executed with, used for incremental
     * conforming.
     */
    protected AbstractRecord getTranslationRow() {
        return translationRow;
    }

    /**
     * PUBLIC:
     * Return if the stream is closed.
     */
    public boolean isClosed() {
        return (this.resultSet == null);
    }

    /**
     * INTERNAL:
     * builds and registers an object from a row for cursors.
     * Behavior is different from the query version in that refreshing is not
     * supported.
     */
    protected Object buildAndRegisterObject(AbstractRecord row) {
        ReadQuery query = this.query;
        if (query.isObjectLevelReadQuery()) {
            ObjectLevelReadQuery objectQuery = (ObjectLevelReadQuery)query;
            if (objectQuery.hasBatchReadAttributes() && objectQuery.getBatchFetchPolicy().isIN()) {
                objectQuery.getBatchFetchPolicy().addDataResults(row);
            }
            if (this.session.isUnitOfWork() && (!query.isReportQuery()) && query.shouldMaintainCache()
                    && (objectQuery.shouldConformResultsInUnitOfWork() || objectQuery.getDescriptor().shouldAlwaysConformResultsInUnitOfWork())) {
                Object object = objectQuery.conformIndividualResult(
                        objectQuery.buildObject(row), (UnitOfWorkImpl)this.session, this.translationRow, this.selectionCriteriaClone, this.initiallyConformingIndex);
                // Notifies caller to continue until conforming instance found
                if (object == null) {
                    return InvalidObject.instance;
                }
                return object;
            }
        }
        return query.buildObject(row);
    }

    /**
     * INTERNAL:
     * Read the next row from the result set.
     */
    protected abstract Object retrieveNextObject() throws DatabaseException;

    /**
     * INTERNAL:
     * Set the fields for the stream.
     */
    protected void setFields(Vector<DatabaseField> fields) {
        this.fields = fields;
    }

    /**
     * INTERNAL:
     * Conforming instances found in memory when building the result.
     * These objects are returned first by the cursor, and a fast lookup
     * is needed to make sure the same objects appearing in the cursor are
     * filtered out.
     */
    public void setInitiallyConformingIndex(Map<Object, Object> index) {
        this.initiallyConformingIndex = index;
    }

    /**
     * INTERNAL:
     * Set the internal object collection
     */
    public void setObjectCollection(List<Object> collection) {
        objectCollection = collection;
    }

    /**
     * INTERNAL:
     * Set the cursor policy.
     */
    public void setPolicy(CursorPolicy policy) {
        this.policy = policy;
    }

    /**
     * INTERNAL:
     * Set the current position of the stream
     */
    protected void setPosition(int value) {
        position = value;
    }

    /**
     * INTERNAL:
     * Set the result set (cursor)
     */
    protected void setResultSet(ResultSet result) {
        resultSet = result;
    }

    /**
     * INTERNAL:
     * The clone of the selection criteria is needed for in-memory conforming
     * each object read from the Cursor.
     */
    public void setSelectionCriteriaClone(Expression expression) {
        this.selectionCriteriaClone = expression;
    }

    /**
     * INTERNAL:
     * Set the session handle
     */
    public void setSession(AbstractSession databaseSession) {
        session = databaseSession;
    }

    /**
     * INTERNAL:
     * Sets the session the underlying call was executed on.  This root
     * session knows the database platform.
     */
    protected void setExecutionSession(AbstractSession executionSession) {
        this.executionSession = executionSession;
    }

    /**
     * INTERNAL:
     * Set the cache size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * INTERNAL:
     * Sets the translation row this query was executed with.  Used for
     * incremental conforming.
     */
    public void setTranslationRow(AbstractRecord row) {
        this.translationRow = row;
    }

    /**
     * PUBLIC:
     * Retrieve the size of the open cursor by executing a count on the same query as the cursor.
     *
     * If this cursor is conforming size() can only be an estimate.  cursor size
     * plus number of conforming instances found in memory will be returned.  The
     * union (actual result) may be smaller than this.
     */
    public int size() throws DatabaseException {
        if (this.size == -1) {
            this.size = getCursorSize();
            if (this.initiallyConformingIndex != null) {
                this.size += this.initiallyConformingIndex.size();
            }
        }
        return this.size;
    }

    /**
     * PUBLIC:
     * Remove is not support with cursors.
     */
    public void remove() throws QueryException {
        QueryException.invalidOperation("remove");
    }

    /**
      * PUBLIC:
      * Release all objects read in so far.
      * This should be performed when reading in a large collection of
      * objects in order to preserve memory.
      */
    public void clear() {
        // If using 1-m joining need to release 1-m rows as well.
        if ((this.query != null) && this.query.isObjectLevelReadQuery() && ((ObjectLevelReadQuery)this.query).hasJoining()) {
            ((ObjectLevelReadQuery)this.query).getJoinedAttributeManager().clearDataResults();
        }
    }
}
