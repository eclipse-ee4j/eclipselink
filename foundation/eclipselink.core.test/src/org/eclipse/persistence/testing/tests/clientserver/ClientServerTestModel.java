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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.models.insurance.InsuranceSystem;
import org.eclipse.persistence.testing.tests.identitymaps.ConcurrentIdentityMapKeyEnumerationTest;
import org.eclipse.persistence.testing.tests.unitofwork.*;

/**
 * Test the TopLink ClientSession and ServerSession under concurrent,
 * multi-thread use cases.
 * Any concurrent tests should be put here.
 */
public class ClientServerTestModel extends TestModel {
    public ClientServerTestModel() {
        setDescription("This suite tests updating objects through various clients session.");
    }

    public void addRequiredSystems() {
        try {
            getSession().getLog().write("WARNING, some JDBC drivers may fail if they are not thread safe." + 
                                        Helper.cr() + "JDBC-ODBC will not be run for this test." + Helper.cr() + 
                                        "Oracle OCI may fail." + Helper.cr() + "DB2 IBM JDBC may fail." + Helper.cr());
            getSession().getLog().flush();
        } catch (java.io.IOException e) {
        }

        if (getSession().getLogin().isJDBCODBCBridge()) {
            throw new TestWarningException("JDBC-ODBC cannot support concurrent connections.");
        }
        addRequiredSystem(new EmployeeSystem());
        addRequiredSystem(new InsuranceSystem());
        addRequiredSystem(new ClientServerEmployeeSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.tests.unitofwork.UOWSystem());
    }

    public void addTests() {
        addTest(getClientServerTestSuite());
        addTest(getClientServerReadingTestSuite());
    }

    public TestSuite getClientServerReadingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Reading Tests");
        suite.addTest(new ClientServerExclusiveReadingTest());
        suite.addTest(new ClientServerConcurrentReadingTest_Case1());
        suite.addTest(new ClientServerConcurrentReadingTest_Case2());

        //useStreams
        suite.addTest(new ClientServerConcurrentReadingTest_Case2(true));
        suite.addTest(new ClientServerOptimisticLockingTest());

        //test deadlock in the cache key level locking
        suite.addTest(new ClientServerReadingDeadlockTest());
        suite.addTest(new ClientServerReadingNonDeadlockTest());
        //Run deadlock on readlock test for approx. 3 minutes
        suite.addTest(new ConcurrentTestWithReadLocks(180000));
        suite.addTest(new ConcurrentBatchReadingTest(70000));

        //bug 3388383
        suite.addTest(new ConcurrentTestRefreshWithOptimisticLocking(100000));

        return suite;

    }

    public static TestSuite getClientServerTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ClientServerTestSuite");
        suite.addTest(new ClientServerTest());
        suite.addTest(new ConcurrencyManagerTest());
        suite.addTest(new ClientServerConcurrentWriteTest());
        suite.addTest(new ClientServerSequenceDeadlockTest());
        suite.addTest(new ClientServerSequenceDeadlockTest2());
        suite.addTest(new ClientLoginTest());
        suite.addTest(new PreBeginTransactionFailureTest());
        suite.addTest(new NPEIsThrownWhenWeTryToWriteNullToANullableField());
        suite.addTest(new DonotAliaseTheTableWhenWeHaveSubSelectExpression());
        //bug 3590333
        suite.addTest(new ConcurrentIdentityMapKeyEnumerationTest());

        // Moved concurrent client/server tests from UnitOfWork test suite here,
        // as all concurrent tests should be here, and they can take a really long time to run.

        //bug 3656068
        suite.addTest(new ConcurrentReadOnUpdateWithEarlyTransTest());
        //bug 4071929
        suite.addTest(new UnitOfWorkConcurrentRevertTest());
        //bug 3582102
        suite.addTest(new LockOnCloneTest());
        suite.addTest(new LockOnCloneDeadlockAvoidanceTest());
        
        suite.addTest(new ConcurrentNewObjectTest());
        suite.addTest(new ConcurrentReadOnInsertTest());
        suite.addTest(new ConcurrentRefreshOnUpdateTest());
        suite.addTest(new ConcurrentRefreshOnCloneTest());
        
        //bug 4438127
        suite.addTest(new NewObjectIdentityTest());
        // Failover connection management EclipseLink bug 211100
        suite.addTest(new CommunicationFailureTest());
        
        suite.addTest(new ClientServerUpdateDeadlockTest());
        suite.addTest(new UnitOfWorkDeleteOrderTest()); // bug 331064
        
        return suite;
    }

    /**
     * Because this changes the database it must put it back to a valid state.
     */
    public void reset() {
        //getExecutor().removeConfigureSystem(new EmployeeSystem());
        getExecutor().removeConfigureSystem(new ClientEmployeeSystem());
        getExecutor().getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
