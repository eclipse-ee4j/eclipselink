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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.queries.DatabaseQueryMechanism;
import org.eclipse.persistence.internal.queries.JPQLCallQueryMechanism;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <b>Purpose</b>: Used as an abstraction of a database invocation.
 * A call is an JPQL string.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Parse the JPQL String
 * <li> Populate the contained query's selection criteria. Add attributes to ReportQuery (if required).
 * </ul>
 * @author Jon Driscoll and Joel Lucuik
 * @since TopLink 4.0
 */
public class JPQLCall implements Serializable, Call {
    // Back reference to query, unfortunately required for events.
    protected transient DatabaseQuery query;
    protected String jpqlString;

    // Check that we aren't parsing more than once
    protected boolean isParsed;

    /**
     * PUBLIC:
     * Create a new JPQLCall.
     */
    public JPQLCall() {
        super();
    }

    /**
     * PUBLIC:
     * Create a new JPQLCall with an jpqlString.
     */
    public JPQLCall(String jpqlString) {
        this();
        this.jpqlString = jpqlString;
    }

    /**
     * INTERNAL:
     * Return the appropriate mechanism,
     * with the call added as necessary.
     */
    public DatabaseQueryMechanism buildNewQueryMechanism(DatabaseQuery query) {
        return new JPQLCallQueryMechanism(query, this);
    }

    /**
     * INTERNAL:
     * Return the appropriate mechanism,
     * with the call added as necessary.
     */
    public DatabaseQueryMechanism buildQueryMechanism(DatabaseQuery query, DatabaseQueryMechanism mechanism) {
        return buildNewQueryMechanism(query);
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cnse) {
            return null;
        }
    }

    /**
     * INTERNAL:
     * Return the string for the call
     */
    public String getCallString() {
        return getEjbqlString();
    }

    /**
     * INTERNAL:
     * Return the EJBQL string for this call
     */
    public String getEjbqlString() {
        return jpqlString;
    }

    /**
     * INTERNAL:
     * Return the EJBQL string for this call
     */
    public String getJPQLString() {
        return jpqlString;
    }

    /**
     * INTERNAL
     * Return the isParsed state
     */
    private boolean getIsParsed() {
        return isParsed;
    }

    /**
     * Back reference to query, unfortunately required for events.
     */
    public DatabaseQuery getQuery() {
        return query;
    }

    /**
     * INTERNAL:
     * Return the SQL string for this call. Always return null
     * since this is an EJBQL call
     */
    public String getLogString(Accessor accessor) {
        return getSQLString();
    }

    /**
     * INTERNAL:
     * Return the SQL string for this call. Always return null
     * since this is an EJBQL call
     */
    public String getSQLString() {
        return null;
    }

    /**
     * INTERNAL:
     * Yes this is an JPQLCall
     */
    public boolean isJPQLCall() {
        return true;
    }

    /**
     * Return whether all the results of the call have been returned.
     */
    public boolean isFinished() {
        //never used, but required for implementing Call.
        return true;
    }

    /**
     * INTERNAL
     * Is this query Parsed
     */
    public boolean isParsed() {
        return getIsParsed();
    }

    /**
     * Populate the query using the information retrieved from parsing the EJBQL.
     */
    public void populateQuery(AbstractSession session) {
        if (!isParsed()) {
            
            JPAQueryBuilder queryBuilder = session.getQueryBuilder();
            queryBuilder.populateQuery(getEjbqlString(), getQuery(), session);

            // Make sure we don't parse and prepare again.
            this.setIsParsed(true);
        }
    }

    /**
     * INTERNAL:
     * Prepare the JDBC statement, this may be parameterize or a call statement.
     * If caching statements this must check for the pre-prepared statement and re-bind to it.
     */
    public PreparedStatement prepareStatement(DatabaseAccessor accessor, AbstractRecord translationRow, AbstractSession session) throws SQLException {
        return null;
    }

    /**
     * INTERNAL:
     * Set the EJBQL string for this call
     */
    public void setEjbqlString(String jpqlString) {
        this.jpqlString = jpqlString;
    }

    /**
     * INTERNAL:
     * Set the JPQL string for this call
     */
    public void setJPQLString(String jpqlString) {
        this.jpqlString = jpqlString;
    }

    /**
     * INTERNAL
     * Set the isParsed state
     */
    public void setIsParsed(boolean newIsParsed) {
        this.isParsed = newIsParsed;
    }

    /**
     * INTERNAL:
     * Back reference to query, unfortunately required for events.
     */
    public void setQuery(DatabaseQuery query) {
        this.query = query;
    }

    /**
     * INTERNAL:
     * translate method comment.
     */
    public void translate(AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session) {
    }

    /**
     * The return type is one of, NoReturn, ReturnOneRow or ReturnManyRows.
     */
    public boolean isNothingReturned() {
        return false;
    }

    /**
     * The return type is one of, NoReturn, ReturnOneRow or ReturnManyRows.
     */
    public boolean isOneRowReturned() {
        return false;
    }

    /**
     * INTERNAL:
     * Print the JPQL string.
     */
    public String toString() {
        String name = getClass().getSimpleName();
        if (getJPQLString() == null) {
            return name;
        } else {
            return name + "(" + getJPQLString() + ")";
        }
    }
}
