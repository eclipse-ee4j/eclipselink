/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sessions.remote;

import java.rmi.server.ObjID;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.eclipse.persistence.sessions.remote.DistributedSession;

/**
 * <p><b>Purpose</b>:
 * The object is used as a wrapper to the ScrollableCursor object in the remote transaction (via RMI, CORBA, etc.)
 * <p>
 * <p><b>Responsibilities</b>:
 * Wraps a database result set cursor to provide a stream to the remote client side on the resulting selected objects.
 * <p>
 * Note: Most of the APIs only work with JDBC 2.0
 *
 * @author King (Yaoping) Wang
 * @since TOPLink/Java 3.0
 */
public class RemoteScrollableCursor extends ScrollableCursor {

    /** This is a unique id for remote cursored stream */
    protected ObjID id;

    /** Return if the stream is closed */
    protected boolean isClosed;

    public RemoteScrollableCursor(ScrollableCursor serverScrollableCursor) {
        this.id = new ObjID();
        this.isClosed = serverScrollableCursor.isClosed();
    }

    /**
     * Moves the cursor to the given row number in the result set
     */
    @Override
    public boolean absolute(int rows) throws DatabaseException {
        clearNextAndPrevious();
        return ((DistributedSession)getSession()).getRemoteConnection().scrollableCursorAbsolute(getID(), rows);
    }

    /**
     * Moves the cursor to the end of the result set, just after the last row.
     */
    @Override
    public void afterLast() throws DatabaseException {
        clearNextAndPrevious();
        ((DistributedSession)getSession()).getRemoteConnection().scrollableCursorAfterLast(getID());
    }

    /**
     * Moves the cursor to the front of the result set, just before the first row
     */
    @Override
    public void beforeFirst() throws DatabaseException {
        clearNextAndPrevious();
        ((DistributedSession)getSession()).getRemoteConnection().scrollableCursorBeforeFirst(getID());
    }

    /**
     * Close the wrapped cursored stream
     * This should be performed whenever the user has finished with the stream.
     */
    @Override
    public void close() throws DatabaseException {
        if (isClosed()) {
            return;
        }

        (((DistributedSession)getSession()).getRemoteConnection()).scrollableCursorClose(getID());
        // Added for bug 2797683.
        this.isClosed = true;
    }

    /**
     * Retrieves the current row index number
     */
    @Override
    public int currentIndex() throws DatabaseException {
        return ((DistributedSession)getSession()).getRemoteConnection().scrollableCursorCurrentIndex(getID());
    }

    /**
     * Moves the cursor to the first row in the result set
     */
    @Override
    public boolean first() throws DatabaseException {
        clearNextAndPrevious();
        return ((DistributedSession)getSession()).getRemoteConnection().scrollableCursorFirst(getID());
    }

    /**
     * INTERNAL:
     * Retreive the size of the wrapped cursored stream.
     */
    @Override
    protected int getCursorSize() {
        return (((DistributedSession)getSession()).getRemoteConnection()).scrollableCursorSize(getID());
    }

    /**
     * INTERNAL:
     * Retreive the OID of the wrapped cursored stream.
     */
    public ObjID getID() {
        return this.id;
    }

    /**
     * Indicates whether the cursor is after the last row in the result set.
     */
    @Override
    public boolean isAfterLast() throws DatabaseException {
        return ((DistributedSession)getSession()).getRemoteConnection().scrollableCursorIsAfterLast(getID());
    }

    /**
     * Indicates whether the cursor is before the first row in the result set.
     */
    @Override
    public boolean isBeforeFirst() throws DatabaseException {
        return ((DistributedSession)getSession()).getRemoteConnection().scrollableCursorIsBeforeFirst(getID());
    }

    /**
     * INTERNAL:
     * Return if the stream is closed.
     */
    @Override
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Indicates whether the cursor is on the first row of the result set.
     */
    @Override
    public boolean isFirst() throws DatabaseException {
        return ((DistributedSession)getSession()).getRemoteConnection().scrollableCursorIsFirst(getID());
    }

    /**
     * Indicates whether the cursor is on the last row of the result set.
     */
    @Override
    public boolean isLast() throws DatabaseException {
        return ((DistributedSession)getSession()).getRemoteConnection().scrollableCursorIsLast(getID());
    }

    /**
     * Moves the cursor to the last row in the result set
     */
    @Override
    public boolean last() throws DatabaseException {
        clearNextAndPrevious();
        return ((DistributedSession)getSession()).getRemoteConnection().scrollableCursorLast(getID());
    }

    /**
     * Moves the cursor a relative number of rows, either positive or negative.
     * Attempting to move beyond the first/last row in the result set positions the cursor before/after the
     * the first/last row
     */
    @Override
    public boolean relative(int rows) throws DatabaseException {
        clearNextAndPrevious();
        return ((DistributedSession)getSession()).getRemoteConnection().scrollableCursorRelative(getID(), rows);
    }

    /**
     * INTERNAL:
     * This method differs slightly from conventional read() operation on a Java stream.  This
     * method return the next object in the collection rather than specifying the number of
     * bytes to be read in.
     *
     * Return the next object from the collection, if beyond the read limit read from the cursor
     * @return next object in stream
     * @exception DatabaseException if read pass end of stream
     */
    @Override
    protected Object retrieveNextObject() throws DatabaseException, QueryException {
        return ((DistributedSession)getSession()).getRemoteConnection().scrollableCursorNextObject(getID(), getPolicy().getQuery(), (DistributedSession)getSession());
    }

    /**
     * INTERNAL:
     * CR#4139
     * Read the previous row from the result set. It is used solely
     * for scrollable cursor support.
     */
    @Override
    protected Object retrievePreviousObject() throws DatabaseException {
        return ((DistributedSession)getSession()).getRemoteConnection().scrollableCursorPreviousObject(getID(), getPolicy().getQuery(), (DistributedSession)getSession());
    }
}
