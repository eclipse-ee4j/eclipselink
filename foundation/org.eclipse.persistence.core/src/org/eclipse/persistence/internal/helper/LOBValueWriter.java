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
package org.eclipse.persistence.internal.helper;

import java.sql.*;
import java.util.*;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.expressions.ForUpdateClause;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;

/**
 * INTERNAL:
 * <p><b>Purpose</b>:LOBValueWriter is used to write a large size of object into an Oracle
 * CLOB/BLOB column through Oracle LOB Locator. It's a work-around object for the well-known 4k write
 * limits on an Oracle thin driver.
 *
 * <p><b>Responsibilities</b>:<ul>
 * <li> Build the Oracle empty lob method call string for the insert call.
 * <li> Build the minimal SELECT call to retrieve the locator.
 * <li> Write the lob value through the locator.
 * <li> Resolve the multiple table INSERT/SELECT orders.
 * <li> Resolve the nested unit of work commit issue.
 * </ul>
 *
 * @author: King Wang
 * @since TopLink/Java 5.0. July 2002.
 */
public class LOBValueWriter {
    //DatabaseCalls still to be processed
    private Collection calls = null;
    private Accessor accessor;
    private boolean isNativeConnectionRequired;

    /**
     * This is the default constructor for the class.
     *
     * Bug 2804663 - Each DatabaseAccessor will now hold on to its own instance
     * of this class, hence a singleton pattern is not applicable.
     */
    public LOBValueWriter(Accessor accessor) {
        this.accessor = accessor;
        DatabasePlatform platform = ((DatabaseAccessor)accessor).getPlatform();
        this.isNativeConnectionRequired = platform.isOracle() && ((OraclePlatform)platform).isNativeConnectionRequiredForLobLocator(); 
    }

    protected void buildAndExecuteCall(DatabaseCall dbCall, AbstractSession session) {
        DatabaseQuery query = dbCall.getQuery();
        if (!query.isWriteObjectQuery()) {
            //if not writequery, should not go through the locator writing..
            return;
        }
        WriteObjectQuery writeQuery = (WriteObjectQuery)query;
        writeQuery.setAccessor(accessor);
        //build a select statement form the query
        SQLSelectStatement selectStatement = buildSelectStatementForLocator(writeQuery, dbCall, session);

        //then build a call from the statement
        DatabaseCall call = buildCallFromSelectStatementForLocator(selectStatement, writeQuery, dbCall, session);

        accessor.executeCall(call, call.getQuery().getTranslationRow(), session);
    }

    /**
    * Fetch the locator(s) from the result set and write LOB value to the table
    */
    public void fetchLocatorAndWriteValue(DatabaseCall dbCall, Object resultSet) throws SQLException {
        Enumeration enumFields = dbCall.getContexts().getFields().elements();
        Enumeration enumValues = dbCall.getContexts().getValues().elements();
        AbstractSession executionSession = dbCall.getQuery().getSession().getExecutionSession(dbCall.getQuery());
        while (enumFields.hasMoreElements()) {
            DatabaseField field = (DatabaseField)enumFields.nextElement();
            Object value = enumValues.nextElement();

            //write the value through the locator
            executionSession.getPlatform().writeLOB(field, value, (ResultSet)resultSet, executionSession);
        }
    }

    /**
    * Build the select statement for selecting the locator
    */
    private SQLSelectStatement buildSelectStatementForLocator(WriteObjectQuery writeQuery, DatabaseCall call, AbstractSession session) {
        SQLSelectStatement selectStatement = new SQLSelectStatement();
        Vector tables = writeQuery.getDescriptor().getTables();
        selectStatement.setTables(tables);
        //rather than get ALL fields from the descriptor, only use the LOB-related fields to build the minimal SELECT statement.
        selectStatement.setFields(call.getContexts().getFields());
        //the where clause setting here is sufficient if the object does not map to multiple tables.
        selectStatement.setWhereClause(writeQuery.getDescriptor().getObjectBuilder().buildPrimaryKeyExpressionFromObject(writeQuery.getObject(), session));
        //need pessimistic locking for the locator select
        selectStatement.setLockingClause(ForUpdateClause.newInstance(ObjectBuildingQuery.LOCK));

        if (tables.size() > 1) {
            //the primary key expression from the primary table
            Expression expression = selectStatement.getWhereClause();

            //additional join from the non-primary tables
            Expression additionalJoin = writeQuery.getDescriptor().getQueryManager().getAdditionalJoinExpression();
            if (additionalJoin != null) {
                expression = expression.and(additionalJoin);
            }

            //where clause now contains extra joins across all tables
            selectStatement.setWhereClause(expression);
        }

        //normalize the statement at the end, such as assign alias to all tables, and build sorting statement
        selectStatement.normalize(session, writeQuery.getDescriptor());
        return selectStatement;
    }

    /**
    * Build the sql call from the select statement for selecting the locator
    */
    private DatabaseCall buildCallFromSelectStatementForLocator(SQLSelectStatement selectStatement, WriteObjectQuery writeQuery, DatabaseCall dbCall, AbstractSession session) {
        DatabaseCall call = selectStatement.buildCall(session);
        // Locator LOB must not be wrapped (WLS wraps LOBs).
        call.setIsNativeConnectionRequired(this.isNativeConnectionRequired);
        
        //the LOB context must be passed into the new call object
        call.setContexts(dbCall.getContexts());
        //need to explicitly define one row return, otherwise, EL assumes multiple rows return and confuses the accessor
        call.returnOneRow();
        //the query object has to be set in order to access to the platform and login objects
        call.setQuery(writeQuery);
        // prepare it
        call.prepare(session);
        //finally do the translation
        call.translate(writeQuery.getTranslationRow(), writeQuery.getModifyRow(), session);
        return call;
    }

    // Building of SELECT statements is no longer done in DatabaseAccessor.basicExecuteCall
    // for updates because DatabaseCall.isUpdateCall() can't recognize update in case
    // StoredProcedureCall is used. Therefore in all cases: insert(single or multiple tables) 
    // and update the original (insert and update) calls are saved
    // and both building and executing of SELECT statements postponed until
    // buildAndExecuteSelectCalls method is called.

    /**
    * Add original (insert or update) call to the collection
    */
    public void addCall(Call call) {
        if (calls == null) {
            //use lazy initialization
            calls = new ArrayList(2);
        }
        calls.add(call);
    }

    // Bug 3110860: RETURNINGPOLICY-OBTAINED PK CAUSES LOB TO BE INSERTED INCORRECTLY
    // The deferred locator SELECT calls should be generated and executed after ReturningPolicy
    // merges PK obtained from the db into the object held by the query.
    // That's why original (insert or update) calls are saved,
    // and both building and executing of SELECT statements postponed until
    // this method is called.

    /**
    * Build and execute the deferred select calls.
    */
    public void buildAndExecuteSelectCalls(AbstractSession session) {
        if ((calls == null) || calls.isEmpty()) {
            //no deferred select calls (it means no locator is required)
            return;
        }

        //all INSERTs have been executed, time to execute the SELECTs
        try {
            for (Iterator callIt = calls.iterator(); callIt.hasNext();) {
                DatabaseCall dbCall = (DatabaseCall)callIt.next();
                buildAndExecuteCall(dbCall, session);
            }
        } finally {
            //after executing all select calls, need to empty the collection.
            //this is necessary in the nested unit of work cases.
            calls.clear();
        }
    }
}
