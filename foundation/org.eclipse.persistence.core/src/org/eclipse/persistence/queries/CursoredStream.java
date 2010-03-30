/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.helper.*;

/**
 * <p><b>Purpose</b>:
 * Stream class which is used to deal with large collections returned
 * from TOPLink queries more efficiently.
 * <p>
 * <p><b>Responsibilities</b>:
 * Wraps a database result set cursor to provide a stream on the resulting selected objects.
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public class CursoredStream extends Cursor {

    /** Marker for backing up. */
    protected int marker;

    /**
     * INTERNAL:
     * Initialize the state of the stream
     */
    public CursoredStream() {
        super();
    }

    /**
     * INTERNAL:
     * Initialize the state of the stream
     */
    public CursoredStream(DatabaseCall call, CursoredStreamPolicy policy) {
        super(call, policy);
        // Must close on exception as stream will not be returned to user.
        try {
            setLimits();
        } catch (RuntimeException exception) {
            try {
                close();
            } catch (RuntimeException ignore) {
            }
            throw exception;
        }
    }

    /**
     * PUBLIC:
     * Return whether the cursored stream is at its end.
     */
    public boolean atEnd() throws DatabaseException {
        if ((getPosition() + 1) <= getObjectCollection().size()) {
            return false;
        }
        if (isClosed()) {
            return true;
        }
        int oldSize = getObjectCollection().size();
        retrieveNextPage();
        return getObjectCollection().size() == oldSize;
    }

    /**
     * PUBLIC:
     * Returns the number of objects that can be read from this input without blocking.
     */
    public int available() throws DatabaseException {
        //For CR#2570/CR#2571.
        return getObjectCollection().size() - (getPosition());
    }

    /**
     * INTERNAL:
     * Must build the count on the primary key fields, not * as * is not allowed if there was a distinct.
     * This require a manually defined operator.
     * added for CR 2900
     */
    public Expression buildCountDistinctExpression(List includeFields, ExpressionBuilder builder) {
        ExpressionOperator countOperator = new ExpressionOperator();
        countOperator.setType(ExpressionOperator.AggregateOperator);
        Vector databaseStrings = new Vector();
        databaseStrings.addElement("COUNT(DISTINCT ");
        databaseStrings.addElement(")");
        countOperator.printsAs(databaseStrings);
        countOperator.bePrefix();
        countOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        Expression firstFieldExpression = builder.getField(((DatabaseField)includeFields.get(0)).getQualifiedName());
        return countOperator.expressionForArguments(firstFieldExpression, new Vector(0));

    }

    /**
     * INTERNAL:
     * Answer a Vector of the elements of the receiver's collection from startIndex to endIndex.
     */
    protected Vector copy(int startIndex, int endIndex) throws QueryException {
        while (getObjectCollection().size() < endIndex) {
            if (retrieveNextObject() == null) {
                throw QueryException.readBeyondStream(getQuery());
            }
        }
        return Helper.copyVector(getObjectCollection(), startIndex, endIndex);
    }

    /**
     * INTERNAL:
     * Retreive the size of the open cursor by executing a count on the same query as the cursor.
     */
    protected int getCursorSize() throws DatabaseException, QueryException {
        ValueReadQuery query;
        if (!((CursoredStreamPolicy)getPolicy()).hasSizeQuery()) {
            if (getQuery().isCallQuery()) {
                throw QueryException.additionalSizeQueryNotSpecified(getQuery());
            }
            
            if (!getQuery().isExpressionQuery()) {
                throw QueryException.sizeOnlySupportedOnExpressionQueries(getQuery());
            }

            // Construct the select statement
            SQLSelectStatement selectStatement = new SQLSelectStatement();

            // 2612538 - the default size of Map (32) is appropriate
            Map clonedExpressions = new IdentityHashMap();
            selectStatement.setWhereClause(((ExpressionQueryMechanism)getQuery().getQueryMechanism()).buildBaseSelectionCriteria(false, clonedExpressions));

            // Case, normal read for branch inheritance class that reads subclasses all in its own table(s).
            if (getQuery().getDescriptor().hasInheritance() && (getQuery().getDescriptor().getInheritancePolicy().getWithAllSubclassesExpression() != null)) {
                Expression branchIndicator = getQuery().getDescriptor().getInheritancePolicy().getWithAllSubclassesExpression();
                if ((branchIndicator != null) && (selectStatement.getWhereClause() != null)) {
                    selectStatement.setWhereClause(selectStatement.getWhereClause().and(branchIndicator));
                } else if (branchIndicator != null) {
                    selectStatement.setWhereClause((Expression)branchIndicator.clone());
                }
            }

            selectStatement.setTables((Vector)getQuery().getDescriptor().getTables().clone());

            // Count * cannot be used with distinct.
            // Count * cannot be used with distinct.
            // CR 2900 if the original query used distinct only on one field then perform the count
            // on that field.
            if (((ReadAllQuery)getQuery()).shouldDistinctBeUsed() && (getQuery().getCall().getFields().size() == 1)) {
                selectStatement.addField(buildCountDistinctExpression(getQuery().getCall().getFields(), ((ReadAllQuery)getQuery()).getExpressionBuilder()));
            } else {
                selectStatement.computeDistinct();
                if (selectStatement.shouldDistinctBeUsed() && (getQuery().getDescriptor().getPrimaryKeyFields().size() == 1)) {
                    // Can only do this with a singleton primary keys.
                    selectStatement.addField(buildCountDistinctExpression(getQuery().getDescriptor().getPrimaryKeyFields(), ((ReadAllQuery)getQuery()).getExpressionBuilder()));
                } else {
                    selectStatement.addField(((ReadAllQuery)getQuery()).getExpressionBuilder().count());
                }
                selectStatement.dontUseDistinct();
            }
            selectStatement.normalize(getSession(), getQuery().getDescriptor(), clonedExpressions);

            // Construct the query
            query = new ValueReadQuery();
            query.setSQLStatement(selectStatement);
        } else {
            query = ((CursoredStreamPolicy)getPolicy()).getSizeQuery();
        }

        Number value = (Number)getSession().executeQuery(query, getQuery().getTranslationRow());
        if (value == null) {
            throw QueryException.incorrectSizeQueryForCursorStream(getQuery());
        }

        return value.intValue();
    }

    /**
     * INTERNAL:
     * Return the threshold for the stream.
     */
    protected int getInitialReadSize() {
        return ((CursoredStreamPolicy)getPolicy()).getInitialReadSize();
    }

    /**
     * INTERNAL:
     * Return the marker used for mark() & reset() operations
     */
    protected int getMarker() {
        return marker;
    }

    /**
     * INTERNAL:
     * Return the page size for the stream
     */
    public int getPageSize() {
        return ((CursoredStreamPolicy)getPolicy()).getPageSize();
    }

    /**
     * INTERNAL:
     * Return the position of the stream inside the object collection
     */
    public int getPosition() {
        return position;
    }

    /**
     * PUBLIC:
     * Return whether the cursored stream has any more elements.
     */
    public boolean hasMoreElements() {
        return !atEnd();
    }

    /**
     * PUBLIC:
     * Return whether the cursored stream has any more elements.
     */
    public boolean hasNext() {
        return hasMoreElements();
    }

    /**
     * PUBLIC:
     * Mark the present position in the stream.
     * Subsequent calls to reset() will attempt to reposition the stream to this point.
     *
     * @param  readAheadLimit  Limit on the number of characters that may be
     *                         read while still preserving the mark.  Because
     *                         the stream's input comes from the database, there
     *                         is no actual limit, so this argument is ignored.
     */
    public void mark(int readAheadLimit) {
        setMarker(getPosition());
    }

    /**
     * PUBLIC:
     * Tests if this input stream supports the <code>mark</code>
     * and <code>reset</code> methods. The <code>markSupported</code>
     * method of <code>InputStream</code> returns <code>false</code>.
     */
    public boolean markSupported() {
        return true;
    }

    /**
     * PUBLIC:
     * Return the next object from the collection, if beyond the read limit read from the cursor.
     * @return the next object in stream
     */
    public Object nextElement() {
        return read();
    }

    /**
     * PUBLIC:
     * Return the next object from the collection, if beyond the read limit read from the cursor.
     * @return the next object in stream
     */
    public Object next() {
        return nextElement();
    }

    /**
     * PUBLIC:
     * Return a Vector of at most numberOfElements of the next objects from the collection. If there
     * aren't that many objects left to read, just return what is available.
     * @return the next objects in stream
     */
    public Vector nextElements(int numberOfElements) {
        java.util.Vector nextElements = new java.util.Vector(numberOfElements);

        while (nextElements.size() < numberOfElements) {
            if (atEnd()) {
                return nextElements;
            }
            nextElements.add(nextElement());
        }
        return nextElements;
    }

    /**
     * PUBLIC:
     * Return a Vector of at most numberOfElements of the next objects from the collection. If there
     * aren't that many objects left to read, just return what is available.
     * @return the next objects in stream
     */
    public Vector next(int numberOfElements) {
        return nextElements(numberOfElements);
    }

    /**
     * PUBLIC:
     * Return the next object in the stream, without incrementing the stream's position.
     */
    public Object peek() throws DatabaseException {
        Object object = read();
        setPosition(getPosition() - 1);
        return object;
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
    public Object read() throws DatabaseException, QueryException {
        // CR#2571.  If no more objects in collection get next page.
        if (getObjectCollection().size() == getPosition()) {
            retrieveNextPage();
        }

        if (atEnd()) {
            throw QueryException.readBeyondStream(getQuery());
        }

        Object object = getObjectCollection().elementAt(getPosition());
        setPosition(getPosition() + 1);
        return object;
    }

    /**
     * PUBLIC:
     * This method differs slightly from conventional read() operation on a Java stream.  This
     * method returns the next number of objects in the collection in a vector.
     *
     * Return the next object from the collection, if beyond the read limit read from the cursor
     * @param number - number of objects to be returned
     * @return - vector containing next number of objects
     * @exception - throws exception if read pass end of stream
     */
    public Vector read(int number) throws DatabaseException {
        Vector result = copy(getPosition(), getPosition() + number);
        setPosition(getPosition() + result.size());
        return result;
    }

    /**
      * PUBLIC:
      * Release all objects read in so far.
      * This should be performed when reading in a large collection of
      * objects in order to preserve memory.
      */
    public void releasePrevious() {
        if (getPosition() == 0) {
            return;
        }

        setObjectCollection(Helper.copyVector(getObjectCollection(), getPosition(), getObjectCollection().size()));

        setPosition(0);
    }

    /**
     * PUBLIC:
     * Repositions this stream to the position at the time the
     * mark method was last called on this stream.
     */
    public void reset() {
        setPosition(getMarker());
    }

    protected Object retrieveNextObject() throws DatabaseException {
        Object next = super.retrieveNextObject();
        if (next != null) {
            getObjectCollection().addElement(next);
        }
        return next;
    }

    /**
     * INTERNAL:
     * Retrieve and add the next page size of rows to the vector.
     * Return the last object, or null if at end.
     */
    protected Object retrieveNextPage() throws DatabaseException {
        Object last = null;
        for (int index = 0; index < getPageSize(); index++) {
            last = retrieveNextObject();
            if (last == null) {
                return null;
            }
        }
        return last;
    }

    /**
     * INTERNAL:
     * Initialize the stream size and position
     */
    protected void setLimits() {
        setPosition(0);
        setMarker(0);

        for (int index = 0; index < getInitialReadSize(); index++) {
            retrieveNextObject();
        }
    }

    /**
     * INTERNAL:
     * Set the marker used for mark() & reset() operations
     */
    protected void setMarker(int value) {
        marker = value;
    }
}
