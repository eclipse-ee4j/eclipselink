/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

// Java imports
import java.sql.*;
import java.io.*;

// TopLink imports
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.jpa.parsing.jpql.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <b>Purpose</b>: Used as an abstraction of a database invocation.
 * A call is an EJBQL string.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Parse the EJBQL String
 * <li> Populate the contained query's selection criteria. Add attributes to ReportQuery (if required).
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
/**
 * <b>Purpose</b>: Used as an abstraction of a database invocation.
 * A call is an EJBQL string.
 */
public class JPQLCall implements Serializable, Call {
    // Back reference to query, unfortunately required for events.
    protected DatabaseQuery query;
    protected String ejbqlString;

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
     * Create a new JPQLCall with an ejbqlString
     */
    public JPQLCall(String ejbqlString) {
        this();
        this.ejbqlString = ejbqlString;
    }

    /**
     * INTERNAL:
     * Return the appropriate mechanism,
     * with the call added as necessary.
     */
    public DatabaseQueryMechanism buildNewQueryMechanism(DatabaseQuery query) {
        return new JPQLCallQueryMechanism(query, this);
    }

	//bug4324564: removed buildParserFor to remove Antlr.jar compile dependency -CD
	//   See class org.eclipse.persistence.internal.parsing.ejbql.JPQLParserFactory.buildParserFor


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
        return ejbqlString;
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

	//bug4324564: removed parseEJBQLString to remove Antlr.jar compile dependency -CD
	//  See class org.eclipse.persistence.internal.parsing.ejbql.JPQLParserFactory.parseEJBQLString

    /**
     * Populate the query using the information retrieved from parsing the EJBQL.
     */
    public void populateQuery(AbstractSession session) {
        if (!isParsed()) {
			//bug4324564: moved code to JPQLParserFactory.populateQuery -CD
            (new JPQLParserFactory()).populateQuery(getEjbqlString(), (ObjectLevelReadQuery)getQuery(), session);
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
    public void setEjbqlString(java.lang.String newEjbqlString) {
        ejbqlString = newEjbqlString;
    }

    /**
     * INTERNAL
     * Set the isParsed state
     */
    public void setIsParsed(boolean newIsParsed) {
        isParsed = newIsParsed;
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
}
