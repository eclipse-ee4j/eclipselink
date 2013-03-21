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
import java.sql.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.InvalidObject;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.databaseaccess.*;

public class ScrollableCursor extends Cursor implements ListIterator {
    protected transient Object nextObject;
    protected transient Object previousObject;
    /** Store the previous row, for 1-m joining. */
    protected AbstractRecord previousRow;
    /** Internal flag indicating if the end of the cursor has been reached */
    protected boolean atEndOfCursor = false;

    /**
     * INTERNAL:
     * Default constructor.
     */
    public ScrollableCursor() {
        super();
    }

    /**
     * INTERNAL:
     * constructor.
     */
    public ScrollableCursor(DatabaseCall call, ScrollableCursorPolicy policy) {
        super(call, policy);
        setPosition(-1);
    }

    /**
     * PUBLIC:
     * Moves the cursor to the given row number in the result set
     */
    public boolean absolute(int rows) throws DatabaseException {
        clearNextAndPrevious();
        try {
            boolean suceeded = false;
            int initiallyConforming = this.objectCollection.size();
            if ((rows >= 0) && (rows <= initiallyConforming)) {
                this.resultSet.beforeFirst();
                setPosition(rows);
                return true;
            } else if (rows > initiallyConforming) {
                suceeded = this.resultSet.absolute(rows - initiallyConforming);
                if (suceeded) {
                    setPosition(initiallyConforming + rows);
                } else {
                    // Must be afterLast.
                    setPosition(size() + 1);
                }
                return suceeded;
            } else {//  (rows < 0)
                // Need to know how big the result set is...
                return absolute(size() + rows);
            }
        } catch (SQLException exception) {
            DatabaseException commException = getAccessor().processExceptionForCommError(this.session, exception, null);
            if (commException != null) throw commException;
            throw DatabaseException.sqlException(exception, getAccessor(), this.session, false);
        }
    }

    /**
     * PUBLIC:
     * Add is not support for scrollable cursors.
     */
    public void add(Object object) throws QueryException {
        QueryException.invalidOperation("add");
    }

    /**
     * PUBLIC:
     * Moves the cursor to the end of the result set, just after the last row.
     */
    public void afterLast() throws DatabaseException {
        clearNextAndPrevious();
        try {
            this.resultSet.afterLast();
            setPosition(size() + 1);
        } catch (SQLException exception) {
            DatabaseException commException = getAccessor().processExceptionForCommError(this.session, exception, null);
            if (commException != null) throw commException;
            throw DatabaseException.sqlException(exception, getAccessor(), this.session, false);
        }
    }

    /**
     * PUBLIC:
     * Moves the cursor to the front of the result set, just before the first row
     */
    public void beforeFirst() throws DatabaseException {
        clearNextAndPrevious();
        try {
            this.resultSet.beforeFirst();
            setPosition(0);
        } catch (SQLException exception) {
            DatabaseException commException = getAccessor().processExceptionForCommError(this.session, exception, null);
            if (commException != null) throw commException;
            throw DatabaseException.sqlException(exception, getAccessor(), this.session, false);
        }
    }

    /**
     * INTERNAL:
     * Clear the cached next and previous object and row values.
     * This must be called whenever the cursor is re-positioned.
     */
    protected void clearNextAndPrevious() {
        this.nextObject = null;
        this.previousObject = null;
        this.nextRow = null;
        this.previousRow = null;
        this.atEndOfCursor = false;
    }
    
    /**
     * INTERNAL:
     * Clear only the cached next and previous object values.
     * Called by previous() and next() to maintain the cached next
     * and previous row values.
     */
    protected void clearNextAndPreviousObject() {
        this.nextObject = null;
        this.previousObject = null;
    }

    /**
     * PUBLIC:
     * Retrieves the current row index number
     */
    public int currentIndex() throws DatabaseException {
        return getPosition();
    }

    /**
     * PUBLIC:
     * Moves the cursor to the first row in the result set
     */
    public boolean first() throws DatabaseException {
        clearNextAndPrevious();
        try {
            if (this.objectCollection.size() > 0) {
                setPosition(1);
                this.resultSet.beforeFirst();
                return true;
            } else {
                return this.resultSet.first();
            }
        } catch (SQLException exception) {
            DatabaseException commException = getAccessor().processExceptionForCommError(this.session, exception, null);
            if (commException != null) throw commException;
            throw DatabaseException.sqlException(exception, getAccessor(), this.session, false);
        }
    }

    /**
     * INTERNAL:
     * Retrieve the size of the open cursor by executing a count on the same query as the cursor.
     */
    protected int getCursorSize() throws DatabaseException {
        if (getKnownCursorSize() != -1) {
            return getKnownCursorSize();
        }
        int currentPos = 0;

        // If afterLast getRow() will return 0!
        boolean wasAfterLast = false;

        try {
            wasAfterLast = this.resultSet.isAfterLast();
            currentPos = this.resultSet.getRow();
            this.resultSet.last();
        } catch (SQLException exception) {
            DatabaseException commException = getAccessor().processExceptionForCommError(this.session, exception, null);
            if (commException != null) throw commException;
            throw DatabaseException.sqlException(exception, getAccessor(), this.session, false);
        }

        int size = 0;
        try {
            size = this.resultSet.getRow();
            if (wasAfterLast) {// Move the cursor back to where we were before calling last()
                this.resultSet.afterLast();
            } else if (currentPos == 0) {
                this.resultSet.beforeFirst();
            } else {
                this.resultSet.absolute(currentPos);
            }
        } catch (SQLException exception) {
            DatabaseException commException = getAccessor().processExceptionForCommError(this.session, exception, null);
            if (commException != null) throw commException;
            throw DatabaseException.sqlException(exception, getAccessor(), this.session, false);
        }

        return size;
    }

    protected int getKnownCursorSize() {
        if (size == -1) {
            return size;
        } else {
            return size - this.objectCollection.size();
        }
    }

    protected Object getNextObject() {
        return nextObject;
    }

    /**
     * PUBLIC:
     * Retrieves the current cursor position (current row).  The first row is number 1, the second number 2, and so on.
     * Unlike java.sql.ResultSet.getRow(), 0 is not returned if afterLast.
     * Instead size() + 1 is returned.
     * @return the current row number; 0 if there is no current row
     * @exception SQLException if a database access error occurs
     */
    public int getPosition() throws DatabaseException {
        try {
            if (this.position == -1) {
                this.position = this.resultSet.getRow();
                if (this.position == 0) {
                    // This could mean either beforeFirst or afterLast!
                    if (isAfterLast()) {
                        this.position = size() + 1;
                    }
                } else {
                    this.position += this.objectCollection.size();
                }
            }
            return this.position;
        } catch (SQLException exception) {
            DatabaseException commException = getAccessor().processExceptionForCommError(this.session, exception, null);
            if (commException != null) throw commException;
            throw DatabaseException.sqlException(exception, getAccessor(), this.session, false);
        }
    }

    protected Object getPreviousObject() {
        return previousObject;
    }

    /**
     * PUBLIC:
     * Indicates whether the cursor can move to the the next row
     */
    public boolean hasMoreElements() throws DatabaseException {
        return hasNext();
    }

    /**
     * PUBLIC:
     * Indicates whether the cursor can move to the the next row
     */
    public boolean hasNext() throws DatabaseException {
        if (isClosed()) {
            return false;
        }
        loadNext();
        return (this.nextObject != null);
    }

    /**
     * PUBLIC:
     * Indicates whether the cursor can move to the the next row
     */
    public boolean hasNextElement() throws DatabaseException {
        return hasNext();
    }

    /**
     * PUBLIC:
     * Indicates whether the cursor can move to the the previous row
     */
    public boolean hasPrevious() throws DatabaseException {
        if (isClosed()) {
            return false;
        }

        loadPrevious();
        return (getPreviousObject() != null);
    }

    /**
     * PUBLIC:
     * Indicates whether the cursor is after the last row in the result set.
     */
    public boolean isAfterLast() throws DatabaseException {
        try {
            if (this.nextObject != null) {
                return false;
            }
            if ((this.objectCollection.size() > 0) && (getPosition() <= this.objectCollection.size())) {
                return false;
            }
            return this.resultSet.isAfterLast();
        } catch (SQLException exception) {
            DatabaseException commException = getAccessor().processExceptionForCommError(this.session, exception, null);
            if (commException != null) throw commException;
            throw DatabaseException.sqlException(exception, getAccessor(), this.session, false);
        }
    }

    /**
     * PUBLIC:
     * Indicates whether the cursor is before the first row in the result set.
     */
    public boolean isBeforeFirst() throws DatabaseException {
        if (getPreviousObject() != null) {
            return false;
        }
        return getPosition() == 0;
    }

    /**
     * PUBLIC:
     * Indicates whether the cursor is on the first row of the result set.
     */
    public boolean isFirst() throws DatabaseException {
        if (getPreviousObject() != null) {
            return false;
        }
        try {
            if (this.objectCollection.size() > 0) {
                return getPosition() == 1;
            }
            return this.resultSet.isFirst();
        } catch (SQLException exception) {
            DatabaseException commException = getAccessor().processExceptionForCommError(this.session, exception, null);
            if (commException != null) throw commException;
            throw DatabaseException.sqlException(exception, getAccessor(), this.session, false);
        }
    }

    /**
     * PUBLIC:
     * Indicates whether the cursor is on the last row of the result set.
     */
    public boolean isLast() throws DatabaseException {
        if (this.nextObject != null) {
            return false;
        }
        try {
            return this.resultSet.isLast();
        } catch (UnsupportedOperationException ex) {
            // isLast() is not supported by some drivers (specifically JConnect5.0)
            // Do this the hard way instead.
            try {
                return this.resultSet.getRow() == getCursorSize();
            } catch (SQLException ex2) {
                DatabaseException commException = getAccessor().processExceptionForCommError(this.session, ex2, null);
                if (commException != null) throw commException;
                throw DatabaseException.sqlException(ex2, getAccessor(), this.session, false);
            }
        } catch (SQLException exception) {
            DatabaseException commException = getAccessor().processExceptionForCommError(this.session, exception, null);
            if (commException != null) throw commException;
            throw DatabaseException.sqlException(exception, getAccessor(), this.session, false);
        }
    }

    /**
     * PUBLIC:
     * Moves the cursor to the last row in the result set
     */
    public boolean last() throws DatabaseException {
        clearNextAndPrevious();
        try {
            boolean isLast = this.resultSet.last();
            if (!isLast) {
                // cursor must be empty.
                if (this.objectCollection.size() > 0) {
                    setPosition(this.objectCollection.size());
                    isLast = true;
                }
            } else {
                setSize(this.objectCollection.size() + this.resultSet.getRow());
                setPosition(size);
            }
            return isLast;
        } catch (SQLException exception) {
            DatabaseException commException = getAccessor().processExceptionForCommError(this.session, exception, null);
            if (commException != null) throw commException;
            throw DatabaseException.sqlException(exception, getAccessor(), this.session, false);
        }
    }

    /**
     * Load the next object
     */
    protected void loadNext() {
        if (this.nextObject == null) {
            Object next = retrieveNextObject();
            this.nextObject = next;
        }
    }

    /**
     * Load the previous object. This is used solely for scrollable cursor support
     */
    protected void loadPrevious() {
        // CR#4139
        if (this.previousObject == null) {
            this.previousObject = retrievePreviousObject();
        }
    }

    /**
     * PUBLIC:
     * This method differs slightly from conventional read() operation on a Java stream.  This
     * method return the next object in the collection rather than specifying the number of
     * bytes to be read in.
     *
     * Return the next object from the collection, if beyond the read limit read from the cursor
     * @return - next object in stream
     * @exception - throws exception if read pass end of stream
     */
    public Object next() throws DatabaseException, QueryException {
        loadNext();
        if (this.nextObject == null) {
            throw QueryException.readBeyondStream(this.query);
        }
        Object next = this.nextObject;
        clearNextAndPreviousObject();
        return next;
    }

    /**
     * PUBLIC:
     * This method differs slightly from conventional read() operation on a Java stream.  This
     * method returns the next number of objects in the collection in a vector.
     *
     * Return the next specified number of objects from the collection, if beyond the read limit read from the cursor
     * @param number - number of objects to be returned
     * @return - vector containing next number of objects
     * @exception - throws exception if read pass end of stream
     */
    public List<Object> next(int number) throws DatabaseException {
        List<Object> result = new ArrayList(number);
        for (int index = 0; index < number; index++) {
            result.add(next());
        }
        return result;
    }

    /**
     * PUBLIC:
     * Return the next object from the collection, if beyond the read limit read from the cursor.
     * @return next object in stream
     */
    public Object nextElement() throws DatabaseException, QueryException {
        return next();
    }

    /**
     * PUBLIC:
     * Retrieves the next row index (against the current row)
     */
    public int nextIndex() throws DatabaseException {
        return currentIndex() + 1;
    }

    /**
     * PUBLIC:
     * Return the previous object from the collection.
     *
     * @return - previous object in stream
     * @exception - throws exception if read pass first of stream
     */
    public Object previous() throws DatabaseException, QueryException {
        loadPrevious();
        if (this.previousObject == null) {
            throw QueryException.readBeyondStream(this.query);
        }
        Object previous = this.previousObject;
        clearNextAndPreviousObject();
        return previous;
    }

    /**
     * PUBLIC:
     * Retrieves the previous row index (against the current row)
     */
    public int previousIndex() throws DatabaseException {
        return currentIndex() - 1;
    }

    /**
     * PUBLIC:
     * Moves the cursor a relative number of rows, either positive or negative.
     * Attempting to move beyond the first/last row in the result set positions the cursor before/after the
     * the first/last row
     */
    public boolean relative(int rows) throws DatabaseException {
        clearNextAndPrevious();
        try {
            boolean suceeded = false;
            int oldPosition = getPosition();
            int newPosition = getPosition() + rows;
            int initiallyConforming = this.objectCollection.size();
            if (newPosition <= initiallyConforming) {
                setPosition(newPosition);
                if (oldPosition > initiallyConforming) {
                    this.resultSet.beforeFirst();
                }
                if (newPosition < 0) {
                    setPosition(0);
                }
                suceeded = (newPosition > 0);
            } else {
                suceeded = this.resultSet.relative(rows);
                if (!suceeded) {
                    // Must be afterLast now.
                    setPosition(size() + 1);
                } else {
                    setPosition(newPosition);
                }
            }
            return suceeded;
        } catch (SQLException exception) {
            DatabaseException commException = getAccessor().processExceptionForCommError(this.session, exception, null);
            if (commException != null) throw commException;
            throw DatabaseException.sqlException(exception, getAccessor(), this.session, false);
        }
    }

    /**
     * INTERNAL:
     * Read the next row from the result set.
     */
    protected Object retrieveNextObject() throws DatabaseException {
        while (true) {
            int currentPosition = getPosition();
            if (currentPosition < this.objectCollection.size()) {
                this.position = currentPosition + 1;
                return this.objectCollection.get(currentPosition);
            }
            if (isClosed()) {
                return null;
            }
            AbstractRecord row = null;
            // if the end of the cursor has been reached, do not retrieve more rows
            if (!this.atEndOfCursor) {
                if (this.nextRow == null) {
                    row = getAccessor().cursorRetrieveNextRow(this.fields, this.resultSet, this.executionSession);
                } else {
                    row = this.nextRow;
                    this.nextRow = null;
                }
            }
            
            this.position = currentPosition + 1;  // bug 309142
            if (row == null) {
                this.atEndOfCursor = true;
                return null;
            }
            
            // If using 1-m joining need to fetch 1-m rows as well.
            if (this.query.isObjectLevelReadQuery() && ((ObjectLevelReadQuery)this.query).hasJoining()) {
                JoinedAttributeManager joinManager = ((ObjectLevelReadQuery)this.query).getJoinedAttributeManager();
                if (joinManager.isToManyJoin()) {
                    this.nextRow = joinManager.processDataResults(row, this, true);
                    // if the join manager returns a null next row, we are at the end of the cursor
                    if (this.nextRow == null) {
                       this.atEndOfCursor = true;
                    }
                }
            }
            
            Object object = buildAndRegisterObject(row);
            if (object == InvalidObject.instance) {
                continue;
            }
            return object;
        }
    }

    /**
     * INTERNAL:
     * CR#4139
     * Read the previous row from the result set. It is used solely
     * for scrollable cursor support.
     */
    protected Object retrievePreviousObject() throws DatabaseException {
        while (true) {
            int currentPosition = getPosition();
            if (currentPosition <= (this.objectCollection.size() + 1)) {
                // If at first of cursor, move cursor to beforeFirst.
                if ((currentPosition == (this.objectCollection.size() + 1)) && (!isClosed())) {
                    getAccessor().cursorRetrievePreviousRow(this.fields, this.resultSet, this.executionSession);
                }
                if (currentPosition <= 1) {
                    // Cursor can not move back further than beforeFirst.
                    this.position = 0;
                    return null;
                } else {
                    this.position = currentPosition - 1;
                    return this.objectCollection.get(this.position - 1);
                }
            }
            if (isClosed()) {
                return null;
            }
            AbstractRecord row = null;
            if (this.previousRow == null) {
                row = getAccessor().cursorRetrievePreviousRow(this.fields, this.resultSet, this.executionSession);
            } else {
                row = this.previousRow;
                this.previousRow = null;
            }

            this.position = currentPosition - 1;
            if (row == null) {
                return null;
            }
            
            // If using 1-m joining need to fetch 1-m rows as well.
            if (this.query.isObjectLevelReadQuery() && ((ObjectLevelReadQuery)this.query).hasJoining()) {
                JoinedAttributeManager joinManager = ((ObjectLevelReadQuery)this.query).getJoinedAttributeManager();
                if (joinManager.isToManyJoin()) {
                    this.previousRow = joinManager.processDataResults(row, this, false);
                }
            }

            Object object = buildAndRegisterObject(row);

            // keep going until find one that conforms.
            if (object == InvalidObject.instance) {
                continue;
            }
            return object;
        }
    }

    /**
     * PUBLIC:
     * Set is not supported for scrollable cursors.
     */
    public void set(Object object) throws QueryException {
        QueryException.invalidOperation("set");
    }

    protected void setNextObject(Object nextObject) {
        this.nextObject = nextObject;
    }

    protected void setPreviousObject(Object previousObject) {
        this.previousObject = previousObject;
    }
}
