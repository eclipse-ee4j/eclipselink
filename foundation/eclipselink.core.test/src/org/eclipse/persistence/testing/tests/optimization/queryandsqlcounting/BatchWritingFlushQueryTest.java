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
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.sessions.*;

/**
 * This tests the use of batch writing for a large number of inserts.
 */
public class BatchWritingFlushQueryTest extends TestCase {
    protected QuerySQLTracker tracker = null;
    protected String firstName = "";
    protected boolean usesBatchWriting;
    protected boolean usesJDBCBatchWriting;

    protected DataModifyQuery myDataModifyQueryObj1;
    protected DataModifyQuery myDataModifyQueryObj2;

    protected int initialSQLStatements = 0;
    protected int initialQueries = 0;
    protected int secondSQLStatements = 0;
    protected int secondQueries = 0;

    protected int EXPECTED_INITIAL_STATEMENTS = 0;
    protected int EXPECTED_INITIAL_QUERIES = 1;
    protected int EXPECTED_SECOND_STATEMENTS = 4;
    protected int EXPECTED_SECOND_QUERIES = 2;

    //Tests feature in bug 4104613 "setForceBatchStatementExecution" api with batch writing

    public BatchWritingFlushQueryTest() {
        setDescription("Test for the setForceBatchStatementExecution ModifyQuery option on Dynamic batch writing mechanism.");
    }

    public void setup() {
        //databaseSession needed for transaction begin/rollback
        DatabaseSession session = (DatabaseSession)getSession();
        DatabasePlatform platform = getSession().getPlatform();
        usesBatchWriting = platform.usesBatchWriting();
        usesJDBCBatchWriting = platform.usesJDBCBatchWriting();

        if (platform.isSybase() || platform.isSQLAnywhere() || platform.isOracle() || platform.isSQLServer() || platform.isAttunity()) {
            platform.setUsesBatchWriting(true);
            //Test TopLink batch Writing
            platform.setUsesJDBCBatchWriting(false);
            // Note: Batch writing does work in Oracle now, as of 9.0.1.
        } else {
            throw new TestWarningException("Batch writing not supported on platform " + platform);
        }

        Employee e = (Employee)session.readObject(Employee.class, (new ExpressionBuilder()).get("lastName").equal("Smith"));
        firstName = e.firstName;

        session.beginTransaction();
        tracker = new QuerySQLTracker(session);
    }

    public void reset() {
        DatabaseSession session = (DatabaseSession)getSession();
        DatabasePlatform platform = getSession().getPlatform();
        if (platform.isSybase() || platform.isSQLAnywhere() || platform.isOracle() || platform.isSQLServer() || platform.isAttunity()) {
            platform.setUsesBatchWriting(usesBatchWriting);
            platform.setUsesJDBCBatchWriting(usesJDBCBatchWriting);
        }
        tracker.remove();
        session.rollbackTransaction();
        session.getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        myDataModifyQueryObj1 = new DataModifyQuery("UPDATE EMPLOYEE SET F_NAME = 'Fatima?' WHERE L_NAME = 'Smith'");
        myDataModifyQueryObj2 = new DataModifyQuery("UPDATE EMPLOYEE SET F_NAME = 'Fatima2?' WHERE L_NAME = 'Smith'");
        uow.executeQuery(myDataModifyQueryObj1);

        initialSQLStatements = tracker.getSqlStatements().size();
        initialQueries = tracker.getQueries().size();

        //feature to be tested:
        myDataModifyQueryObj2.setForceBatchStatementExecution(true);

        uow.executeQuery(myDataModifyQueryObj2);

        secondSQLStatements = tracker.getSqlStatements().size();
        secondQueries = tracker.getQueries().size();

        uow.commit();
    }


    public void verify() {
        if (initialSQLStatements != EXPECTED_INITIAL_STATEMENTS) {
            throw new TestErrorException("A DataModifyQuery with batchWriting executed resulting in incorrect number of SQL Statements. " + 
                                         " expected: " + EXPECTED_INITIAL_STATEMENTS + " got: " + initialSQLStatements);
        }
        if (initialQueries != EXPECTED_INITIAL_QUERIES) {
            throw new TestErrorException("A DataModifyQuery with batchWriting executed resulting in incorrect number of Queries. " + 
                                         " expected: " + EXPECTED_INITIAL_QUERIES + " got: " + (initialQueries));
        }
        if (secondSQLStatements != EXPECTED_SECOND_STATEMENTS) {
            throw new TestErrorException("A DataModifyQuery with batchWriting executed resulting in the incorrect number of SQL statements. " + 
                                         " expected: " + (EXPECTED_SECOND_STATEMENTS - EXPECTED_INITIAL_STATEMENTS) + 
                                         " got: " + (secondSQLStatements - initialSQLStatements));
        }
        if (secondQueries != EXPECTED_SECOND_QUERIES) {
            throw new TestErrorException("A DataModifyQuery with batchWriting executed resulting in an incorrect number of Queries. " + 
                                         " expected: " + (EXPECTED_SECOND_QUERIES - EXPECTED_INITIAL_QUERIES) + " got: " + 
                                         (secondQueries - initialQueries));
        }
    }
}
