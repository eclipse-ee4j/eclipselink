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
package org.eclipse.persistence.testing.tests.history;

import java.util.Vector;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.tests.flashback.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;

public class HistoryTestModel extends FlashbackTestModel {

    int mode;
    public static int BASIC = 0;
    public static int PROJECT_XML = 1;
    public static int PROJECT_CLASS_GENERATED = 2;

    public HistoryTestModel(int mode) {
        setDescription("Tests the new flashback query tests, but using a Historical Schema.");
        this.mode = mode;
        if (mode == BASIC) {
            setName("HistoryTestModel");
        } else if (mode == PROJECT_XML) {
            setName("ProjectXMLHistoryTestModel");
        } else if (mode == PROJECT_CLASS_GENERATED) {
            setName("ProjectClassGeneratedHistoryTestModel.");
        }
    }

    public void addTests() {
        super.addTests();

        addTest(new RollbackObjectsTest(Employee.class, getAsOfClause()));
        addTest(EmployeeBasicTestModel.getReadObjectTestSuite());
        addTest(EmployeeBasicTestModel.getReadAllTestSuite());
        addTest(EmployeeBasicTestModel.getInsertObjectTestSuite());
        buildUpdateObjectTestSuite();
        addTest(EmployeeBasicTestModel.getDeleteObjectTestSuite());
        addTest(new IsolatedSessionHistoricalTest(getAsOfClause()));
        addTest(new InsertWithHistoryPolicyTest()); // Bug 319276
    }

    private void configure() throws Exception {

        TestSystem system = new HistoricalEmployeeSystem(mode);

        system.run(getSession());
        buildAsOfClause();
        Thread.sleep(1000);
        depopulate();
        return;
    }

    public void buildAsOfClause() {
        //DatabasePlatform platform = (DatabasePlatform)getSession().getPlatform();
        //ValueReadQuery timestampQuery = platform.getTimestampQuery();
        //asOfClause = new AsOfClause(getSession().executeQuery(timestampQuery));
        asOfClause = new AsOfClause(new java.sql.Timestamp(System.currentTimeMillis()));
    }

    /**
     * Assume setup() is called prior to addTests.  This seems bizarre
     * but is the way it works.
     */
    public void setup() {
        // Must do configuration here...
        if (getTimestamp() != null) {
            return;
        }
        try {
            configure();
        } catch (EclipseLinkException te) {
            throw te;
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    protected void buildUpdateObjectTestSuite() {
        // Wrapper test throws a warning 
        // in case the wrapped test throws a certain exception.
        class WrappedWriteObjectTest extends TestWrapper {
            public WrappedWriteObjectTest(TestCase test) {
                super(test);
            }
            protected void test() throws Throwable {
                try {
                    super.test();
                } catch (DatabaseException databaseException) {
                    Throwable internalException = databaseException.getInternalException();
                    if(getSession().getPlatform().isMySQL()) {
                        if(internalException.getClass().getName().contains("MySQLIntegrityConstraintViolationException")) {
                            // two objects created / updated during the same second will have the same date/time representation
                            // in MySQL database column because on MySQL microseconds cannot be stored into a column of any temporal data type.
                            throw new TestWarningException("on MySQL microseconds cannot be stored into a column of any temporal data type, therefore the following exception occurred: ", internalException);
                        }
                    }
                    throw databaseException;
                }
            }
        }

        TestSuite testSuite = EmployeeBasicTestModel.getUpdateObjectTestSuite();
        if(getSession().getPlatform().isMySQL()) {
            TestSuite newTestSuite = new TestSuite();
            Vector tests = testSuite.getTests();
            for(int i=0; i<tests.size(); i++) {
                TestCase test = (TestCase)tests.elementAt(i);
                if(test.getClass().getName().endsWith(".WriteObjectTest")) {
                    // Bug 210270: HistorySession causing unique constraint violation on MySQL.
                    // To avoid test failure due to this bug,
                    // wrap a test prone to MySQLIntegrityConstraintViolationException
                    // in a wrapper that throws a warning in case this exception occur.
                    test = new WrappedWriteObjectTest(test);
                }
                newTestSuite.addTest(test);
            }
            testSuite = newTestSuite;
        }
        addTest(testSuite);
    }

}
