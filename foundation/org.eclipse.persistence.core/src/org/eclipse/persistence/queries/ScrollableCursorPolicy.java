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

import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.sessions.remote.*;

/**
 * PUBLIC:
 * ScrollableCursorPolicy allows for additional options to be specified for ReadAllQuery or DataReadQuery.
 * These options are passed through to the JDBC result set,
 * the JDBC driver must support JDBC2 scrolling cursors and the options used.
 * Example:<p>
 * ScrollableCursorPolicy policy = new ScrollableCursorPolicy()<p>
 * policy.setResultSetType(ScrollableCursorPolicy.TYPE_SCROLL_INSENSITIVE);<p>
 * query.useScrollableCursor(policy);<p>
 */
public class ScrollableCursorPolicy extends CursorPolicy {
    protected int resultSetType;
    protected int resultSetConcurrency;

    /** RESULT SET TYPES **/
    /** JDBC 2.0 The rows in a result set will be processed in a forward direction; first-to-last. */
    public static final int FETCH_FORWARD = 1000;

    /** JDBC 2.0 The rows in a result set will be processed in a reverse direction; last-to-first. */
    public static final int FETCH_REVERSE = 1001;

    /** JDBC 2.0 The order in which rows in a result set will be processed is unknown. */
    public static final int FETCH_UNKNOWN = 1002;

    /** JDBC 2.0 The type for a ResultSet object whose cursor may move only forward. */
    public static final int TYPE_FORWARD_ONLY = 1003;

    /** JDBC 2.0 The type for a ResultSet object that is scrollable but generally not sensitive to changes made by others. */
    public static final int TYPE_SCROLL_INSENSITIVE = 1004;

    /** JDBC 2.0 The type for a ResultSet object that is scrollable and generally sensitive to changes made by others. */
    public static final int TYPE_SCROLL_SENSITIVE = 1005;

    /** RESULT SET CONCURRENCY */
    /** JDBC 2.0 The concurrency mode for a ResultSet object that may NOT be updated. */
    public static final int CONCUR_READ_ONLY = 1007;

    /** JDBC 2.0 The concurrency mode for a ResultSet object that may be updated. */
    public static final int CONCUR_UPDATABLE = 1008;

    /**
     * PUBLIC:
     * Create a new scrollable cursor policy.
     */
    public ScrollableCursorPolicy() {
        super();
        setResultSetType(TYPE_SCROLL_INSENSITIVE);
        setResultSetConcurrency(CONCUR_UPDATABLE);
    }

    /**
     * INTERNAL:
     * Create a cursor policy with the pagesize.
     */
    public ScrollableCursorPolicy(ReadQuery query, int pageSize) {
        super(query, pageSize);
        setResultSetType(TYPE_SCROLL_INSENSITIVE);
        setResultSetConcurrency(CONCUR_UPDATABLE);
    }

    /**
     * INTERNAL:
     * Execute the cursored select and build the stream.
     */
    public Object execute() {
        DatabaseCall call = getQuery().getQueryMechanism().cursorSelectAllRows();

        // Create cursored stream	
        ScrollableCursor cursor = new ScrollableCursor(call, this);
        return cursor;
    }

    /**
     * PUBLIC:
     * The ResultSetConcurrency specifies if the resultset is updatable.
     * It is one of,
     * CONCUR_READ_ONLY,
     * CONCUR_UPDATABLE
     */
    public int getResultSetConcurrency() {
        return resultSetConcurrency;
    }

    /**
     * PUBLIC:
     * The ResultSetType specifies if the resultset is sensitive to changes made by others.
     * It is one of,
     * TYPE_SCROLL_INSENSITIVE,
     * TYPE_SCROLL_SENSITIVE
     */
    public int getResultSetType() {
        return resultSetType;
    }

    public boolean isScrollableCursorPolicy() {
        return true;
    }

    /**
     * INTERNAL:
     * Execute the cursored select and build the stream.
     */
    public Object remoteExecute() {
        return ((DistributedSession)getQuery().getSession()).cursorSelectObjects(this);
    }

    /**
     * PUBLIC:
     * The ResultSetConcurrency specifies if the resultset is updatable.
     * It is one of,
     * CONCUR_READ_ONLY,
     * CONCUR_UPDATABLE
     */
    public void setResultSetConcurrency(int resultSetConcurrency) {
        this.resultSetConcurrency = resultSetConcurrency;
    }

    /**
     * PUBLIC:
     * The ResultSetType specifies if the resultset is sensitive to changes made by others.
     * It is one of,
     * TYPE_SCROLL_INSENSITIVE,
     * TYPE_SCROLL_SENSITIVE
     */
    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }
}
