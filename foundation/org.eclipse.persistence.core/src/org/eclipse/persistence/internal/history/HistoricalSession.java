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
package org.eclipse.persistence.internal.history;

import java.io.*;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.*;

/**
 * INTERNAL:
 * <b>Purpose</b>: Allows the reading of objects as of a past time.
 * <p>
 * <b>Description</b>: All queries executed through this special lightweight
 * session will return results as of a past time.  Objects read will be cached
 * in a special isolated cache.
 * <p>
 * <b>Responsibilities</b>:
 *    <ul>
 *    <li> Execute all read queries as of a past time.
 *    <li> Insure that all objects read are cached in an Identity map completely isolated from that of its parent.
 *    <li> Once a query has been uniquely prepared to read past objects, execute the call on the parent session.
 *    </ul>
 * <p>
 * @author Stephen McRitchie
 * @since OracleAS TopLink 10<i>g</i> (10.1.3)
 * @see org.eclipse.persistence.sessions.Session#acquireHistoricalSession
 */
public class HistoricalSession extends AbstractSession {
    protected final AbstractSession parent;
    protected final AsOfClause asOfClause;

    /**
     * INTERNAL:
     * Create and return a new Historical Session.
     */
    public HistoricalSession(AbstractSession parent, AsOfClause clause) {
        super();
        this.asOfClause = clause;
        this.parent = parent;
        // As a lightweight session copy over all fields, do not clone them.
        this.project = parent.getProject();
        this.queries = parent.getQueries();
        this.profiler = parent.getProfiler();
        this.isInProfile = parent.isInProfile();
        this.isLoggingOff = parent.isLoggingOff();
        this.sessionLog = parent.getSessionLog();
        if (parent.hasEventManager()) {
            this.eventManager = parent.getEventManager().clone(this);
        }
        this.exceptionHandler = parent.getExceptionHandler();
        this.descriptors = parent.getDescriptors();
    }
    
    /**
     * INTERNAL:
     * Acquires a special historical session for reading objects as of a past time.
     */
    public org.eclipse.persistence.sessions.Session acquireHistoricalSession(org.eclipse.persistence.history.AsOfClause clause) throws ValidationException {
        throw ValidationException.cannotAcquireHistoricalSession();
    }

    /**
     * INTERNAL:
     * A UnitOfWork can not be acquired from a Historical Session.
     */
    public UnitOfWorkImpl acquireUnitOfWork() {
        throw ValidationException.operationNotSupported(Helper.getShortClassName(getClass()) + ".acquireUnitOfWork");
    }

    /**
     * INTERNAL:
     * No transactions should be used inside a HistoricalSession.
     */
    public void beginTransaction() throws DatabaseException, ConcurrencyException {
        throw ValidationException.operationNotSupported(Helper.getShortClassName(getClass()) + ".beginTransaction");
    }

    /**
     * INTERNAL:
     * No transactions should be used inside a HistoricalSession.
     */
    public void commitTransaction() throws DatabaseException, ConcurrencyException {
        throw ValidationException.operationNotSupported(Helper.getShortClassName(getClass()) + ".commitTransaction");
    }

    /**
     * INTERNAL:
     * Gets the session which this query will be executed on.
     * Generally will be called immediately before the call is translated,
     * which is immediately before session.executeCall.
     * <p>
     * Since the execution session also knows the correct datasource platform
     * to execute on, it is often used in the mappings where the platform is
     * needed for type conversion, or where calls are translated.
     * <p>
     * Is also the session with the accessor.  Will return a ClientSession if
     * it is in transaction and has a write connection.
     * @return a session with a live accessor
     * @param query may store session name or reference class for brokers case
     */
    public AbstractSession getExecutionSession(DatabaseQuery query) {
        return getParent().getExecutionSession(query);
    }

    /**
     * ADVANCED:
     * Answers a read-only data object, which knows whether it is
     * a wall-clock time or a system change number.
     */
    public AsOfClause getAsOfClause() {
        return asOfClause;
    }

    /**
     * PUBLIC:
     * Answers the value this Session is As Of.
     * Equivalent to getAsOfClause().getValue().
     */
    public Object getAsOfValue() {
        return getAsOfClause().getValue();
    }

    /**
     * INTERNAL:
     * Returns the parent Session.
     */
    public AbstractSession getParent() {
        return parent;
    }

    /**
     * INTERNAL:
     * Marked internal as this is not customer API but helper methods for
     * accessing the server platform from within TopLink's other sessions types
     * (ie not DatabaseSession)
     */
    public ServerPlatform getServerPlatform(){
        return getParent().getServerPlatform();
    }

    /**
     * ADVANCED:
     * Answers if all objects are to be read as of a past time.  Only true if
     * this is a special historical session.
     * @see #getAsOfClause
     */
    public boolean hasAsOfClause() {
        return ((asOfClause != null) && (asOfClause.getValue() != null));
    }

    /**
     * INTERNAL:
     * Return the results from executing the database query.
     * the arguments should be a database row with raw data values.
     * No modify queries are allowed through a HistoricalSession.
     */
    public Object internalExecuteQuery(DatabaseQuery query, AbstractRecord databaseRow) throws DatabaseException {
        if (!query.isReadQuery()) {
            throw QueryException.invalidQueryOnHistoricalSession(query);
        } else {
            return super.internalExecuteQuery(query, databaseRow);
        }
    }

    /**
     * INTERNAL:
     * Historical session are never in a transaction.
     */
    public boolean isInTransaction() {
        return false;
    }

    /**
     * INTERNAL:
     * Return if this session is a historical session.
     */
    public boolean isHistoricalSession() {
        return true;
    }

    /**
     * INTERNAL:
     * A call back to do session specific preparation of a query.
     * <p>
     * The call back occurs immediately before we clone the query for execution,
     * meaning that if this method needs to clone the query then the caller will
     * determine that it doesn't need to clone the query twice.
     */
    public DatabaseQuery prepareDatabaseQuery(DatabaseQuery query) {
        DatabaseQuery clonedQuery = (DatabaseQuery)query.clone();
        clonedQuery.setIsExecutionClone(true);
        clonedQuery.setIsPrepared(false);
        return clonedQuery;
    }

    /**
     * INTERNAL:
     * No transactions should be used inside a HistoricalSession.
     */
    public void rollbackTransaction() throws DatabaseException, ConcurrencyException {
        throw ValidationException.operationNotSupported(Helper.getShortClassName(getClass()) + ".rollbackTransaction");
    }

    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write(getSessionTypeString());
        writer.write("(");
        writer.write(getAsOfClause().toString());
        writer.write(")");
        return writer.toString();
    }
}
