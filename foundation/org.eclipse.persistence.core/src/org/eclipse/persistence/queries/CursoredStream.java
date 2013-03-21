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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
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
        if ((this.position + 1) <= this.objectCollection.size()) {
            return false;
        }
        if (this.nextRow != null) {
            return false;
        }
        if (isClosed()) {
            return true;
        }
        int oldSize = this.objectCollection.size();
        retrieveNextPage();
        return this.objectCollection.size() == oldSize;
    }

    /**
     * PUBLIC:
     * Returns the number of objects that can be read from this input without blocking.
     */
    public int available() throws DatabaseException {
        //For CR#2570/CR#2571.
        return this.objectCollection.size() - this.position;
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
        databaseStrings.add("COUNT(DISTINCT ");
        databaseStrings.add(")");
        countOperator.printsAs(databaseStrings);
        countOperator.bePrefix();
        countOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        Expression firstFieldExpression = builder.getField(((DatabaseField)includeFields.get(0)).getQualifiedName());
        return countOperator.expressionForArguments(firstFieldExpression, new ArrayList(0));

    }

    /**
     * INTERNAL:
     * Answer a list of the elements of the receiver's collection from startIndex to endIndex.
     */
    protected List<Object> copy(int startIndex, int endIndex) throws QueryException {
        while (this.objectCollection.size() < endIndex) {
            if (retrieveNextObject() == null) {
                throw QueryException.readBeyondStream(this.query);
            }
        }
        return Helper.copyVector(this.objectCollection, startIndex, endIndex);
    }

    /**
     * INTERNAL:
     * Retrieve the size of the open cursor by executing a count on the same query as the cursor.
     */
    protected int getCursorSize() throws DatabaseException, QueryException {
        ValueReadQuery query;
        if (!((CursoredStreamPolicy)this.policy).hasSizeQuery()) {
            if (this.query.isCallQuery()) {
                throw QueryException.additionalSizeQueryNotSpecified(this.query);
            }
            
            if (!this.query.isExpressionQuery()) {
                throw QueryException.sizeOnlySupportedOnExpressionQueries(this.query);
            }

            // Construct the select statement
            SQLSelectStatement selectStatement = new SQLSelectStatement();

            // 2612538 - the default size of Map (32) is appropriate
            Map clonedExpressions = new IdentityHashMap();
            selectStatement.setWhereClause(((ExpressionQueryMechanism)this.query.getQueryMechanism()).buildBaseSelectionCriteria(false, clonedExpressions));

            ClassDescriptor descriptor = this.query.getDescriptor();
            // Case, normal read for branch inheritance class that reads subclasses all in its own table(s).
            if (descriptor.hasInheritance() && (descriptor.getInheritancePolicy().getWithAllSubclassesExpression() != null)) {
                Expression branchIndicator = descriptor.getInheritancePolicy().getWithAllSubclassesExpression();
                if ((branchIndicator != null) && (selectStatement.getWhereClause() != null)) {
                    selectStatement.setWhereClause(selectStatement.getWhereClause().and(branchIndicator));
                } else if (branchIndicator != null) {
                    selectStatement.setWhereClause((Expression)branchIndicator.clone());
                }
            }

            selectStatement.setTables((Vector)descriptor.getTables().clone());

            // Count * cannot be used with distinct.
            // Count * cannot be used with distinct.
            // CR 2900 if the original query used distinct only on one field then perform the count
            // on that field.
            if (((ReadAllQuery)this.query).shouldDistinctBeUsed() && (this.query.getCall().getFields().size() == 1)) {
                selectStatement.addField(buildCountDistinctExpression(this.query.getCall().getFields(), ((ReadAllQuery)this.query).getExpressionBuilder()));
            } else {
                selectStatement.computeDistinct();
                if (selectStatement.shouldDistinctBeUsed() && (descriptor.getPrimaryKeyFields().size() == 1)) {
                    // Can only do this with a singleton primary keys.
                    selectStatement.addField(buildCountDistinctExpression(descriptor.getPrimaryKeyFields(), ((ReadAllQuery)this.query).getExpressionBuilder()));
                } else {
                    selectStatement.addField(((ReadAllQuery)this.query).getExpressionBuilder().count());
                }
                selectStatement.dontUseDistinct();
            }
            selectStatement.normalize(getSession(), descriptor, clonedExpressions);

            // Construct the query
            query = new ValueReadQuery();
            query.setSQLStatement(selectStatement);
        } else {
            query = ((CursoredStreamPolicy)this.policy).getSizeQuery();
        }

        Number value = (Number)getSession().executeQuery(query, this.query.getTranslationRow());
        if (value == null) {
            throw QueryException.incorrectSizeQueryForCursorStream(this.query);
        }

        return value.intValue();
    }

    /**
     * INTERNAL:
     * Return the threshold for the stream.
     */
    protected int getInitialReadSize() {
        return ((CursoredStreamPolicy)this.policy).getInitialReadSize();
    }

    /**
     * INTERNAL:
     * Return the marker used for mark() & reset() operations.
     */
    protected int getMarker() {
        return marker;
    }

    /**
     * INTERNAL:
     * Return the page size for the stream.
     */
    public int getPageSize() {
        return ((CursoredStreamPolicy)this.policy).getPageSize();
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
        return !atEnd();
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
        this.marker = this.position;
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
        return read();
    }

    /**
     * PUBLIC:
     * Return a Vector of at most numberOfElements of the next objects from the collection. If there
     * aren't that many objects left to read, just return what is available.
     * @return the next objects in stream
     */
    public Vector nextElements(int numberOfElements) {
        Vector nextElements = new Vector(numberOfElements);
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
    public List<Object> next(int numberOfElements) {
        return nextElements(numberOfElements);
    }

    /**
     * PUBLIC:
     * Return the next object in the stream, without incrementing the stream's position.
     */
    public Object peek() throws DatabaseException {
        Object object = read();
        this.position = this.position - 1;
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
        if (this.objectCollection.size() == this.position) {
            retrieveNextPage();
        }
        if (atEnd()) {
            throw QueryException.readBeyondStream(this.query);
        }
        Object object = this.objectCollection.get(this.position);
        this.position = this.position + 1;
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
    public List<Object> read(int number) throws DatabaseException {        
        List<Object> result = copy(this.position, this.position + number);
        this.position = this.position + result.size();
        return result;
    }

    /**
      * PUBLIC:
      * Release all objects read in so far.
      * This should be performed when reading in a large collection of
      * objects in order to preserve memory.
      */
    public void clear() {
        super.clear();
        if (this.position == 0) {
            return;
        }
        this.objectCollection = Helper.copyVector(this.objectCollection, this.position, this.objectCollection.size());
        this.position = 0;        
    }
    
    /**
      * PUBLIC:
      * Release all objects read in so far.
      * This should be performed when reading in a large collection of
      * objects in order to preserve memory.
      */
    public void releasePrevious() {
        clear();
    }

    /**
     * PUBLIC:
     * Repositions this stream to the position at the time the
     * mark method was last called on this stream.
     */
    public void reset() {
        this.position = this.marker;
    }

    protected Object retrieveNextObject() throws DatabaseException {
        while (true) {
            AbstractRecord row = null;
            if (this.nextRow == null) {
                if (isClosed()) {
                    return null;
                }
                row = getAccessor().cursorRetrieveNextRow(this.fields, this.resultSet, this.executionSession);
            } else {
                row = this.nextRow;
                this.nextRow = null;
            }
            if (row == null) {
                close();
                return null;
            }
            // If using 1-m joining need to fetch 1-m rows as well.
            if (this.query.isObjectLevelReadQuery() && ((ObjectLevelReadQuery)this.query).hasJoining()) {
                if (!isClosed()) {
                    JoinedAttributeManager joinManager = ((ObjectLevelReadQuery)this.query).getJoinedAttributeManager();
                    if (joinManager.isToManyJoin()) {
                        this.nextRow = joinManager.processDataResults(row, this, true);
                        if (this.nextRow == null) {
                            close();
                        }
                    }
                }
            }
            Object object = buildAndRegisterObject(row);
            if (object == InvalidObject.instance) {
                continue;
            }
            if (object != null) {
                this.objectCollection.add(object);
            }
            return object;
        }
    }

    /**
     * INTERNAL:
     * Retrieve and add the next page size of rows to the vector.
     * Return the last object, or null if at end.
     */
    protected Object retrieveNextPage() throws DatabaseException {
        Object last = null;
        int pageSize = getPageSize();
        for (int index = 0; index < pageSize; index++) {
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
        this.position = 0;
        this.marker = 0;
        int readSize = getInitialReadSize();
        for (int index = 0; index < readSize; index++) {
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
