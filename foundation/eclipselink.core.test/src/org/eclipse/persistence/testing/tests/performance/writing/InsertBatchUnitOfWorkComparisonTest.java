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
package org.eclipse.persistence.testing.tests.performance.writing;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.models.performance.Address;
import org.eclipse.persistence.testing.framework.*;

/**
 * Compares the various writing modes for insertion of 10 addresses.
 */
public class InsertBatchUnitOfWorkComparisonTest extends PerformanceComparisonTestCase {
    public InsertBatchUnitOfWorkComparisonTest() {
        // Needs to run for a long time.
        setTestRunTime(50000);
        setDescription("Compares the various writing modes for insertion of 10 addresses.");
        addParameterizedSQLTest();
        addBatchWritingTest();
        addParameterizedBatchWritingTest();
        addNativeBatchWritingTest();
        addBufferedBatchWritingTest();
        addParameterizedStatementsTest();
        addDynamicStatementsTest();
    }

    /**
     * Insert 10 addresses and then reset database.
     */
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        for (int index = 0; index < 10; index++) {
            Address address = new Address();
            address.setCity("NewCity");
            address.setPostalCode("N5J2N5");
            address.setProvince("ON");
            address.setStreet("1111 Mountain Blvd. Floor 13, suite " + index);
            address.setCountry("Canada");
            uow.registerObject(address);
        }
        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        uow = getSession().acquireUnitOfWork();
        uow.executeNonSelectingCall(new SQLCall("Delete from ADDRESS where CITY = 'NewCity'"));
        uow.commit();
    }

    /**
     * Insert the batch with parameterized SQL on.
     */
    public void addParameterizedSQLTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().getLogin().cacheAllStatements();
                InsertBatchUnitOfWorkComparisonTest.this.test();
                getSession().getLogin().dontCacheAllStatements();
            }
        };
        test.setName("ParameterizedSQLInsertBatchTest");
        test.setAllowableDecrease(10);
        addTest(test);
    }

    /**
     * Insert the batch with parameterized batch writing on.
     */
    public void addParameterizedBatchWritingTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().getLogin().cacheAllStatements();
                getSession().getLogin().useBatchWriting();
                InsertBatchUnitOfWorkComparisonTest.this.test();
                getSession().getLogin().dontCacheAllStatements();
                getSession().getLogin().dontUseBatchWriting();
            }
        };
        test.setName("ParameterizedBatchWritingInsertBatchTest");
        test.setAllowableDecrease(25);
        addTest(test);
    }

    /**
     * Insert the batch with Native batch writing on.
     */
    public void addNativeBatchWritingTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().getLogin().cacheAllStatements();
                getSession().getLogin().useBatchWriting();
                getSession().getLogin().getPlatform().setUsesNativeBatchWriting(true);
                InsertBatchUnitOfWorkComparisonTest.this.test();
                getSession().getLogin().getPlatform().setUsesNativeBatchWriting(false);
                getSession().getLogin().dontCacheAllStatements();
                getSession().getLogin().dontUseBatchWriting();
            }

            public void startTest() {
                ((DatabaseAccessor)((AbstractSession)getSession()).getAccessor()).clearStatementCache((AbstractSession)getSession());
            }

            public void endTest() {
                ((DatabaseAccessor)((AbstractSession)getSession()).getAccessor()).clearStatementCache((AbstractSession)getSession());
            }
        };
        test.setName("NativeBatchWritingInsertBatchTest");
        test.setAllowableDecrease(25);
        addTest(test);
    }

    /**
     * Insert the batch with batch writing on.
     */
    public void addBatchWritingTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().getLogin().useBatchWriting();
                getSession().getLogin().dontBindAllParameters();
                InsertBatchUnitOfWorkComparisonTest.this.test();
                getSession().getLogin().dontUseBatchWriting();
                getSession().getLogin().bindAllParameters();
            }
        };
        test.setName("BatchWritingInsertBatchTest");
        test.setAllowableDecrease(-300);
        addTest(test);
    }

    /**
     * Insert the batch with buffered batch writing on.
     */
    public void addBufferedBatchWritingTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().getLogin().useBatchWriting();
                getSession().getLogin().dontUseJDBCBatchWriting();
                getSession().getLogin().dontBindAllParameters();
                InsertBatchUnitOfWorkComparisonTest.this.test();
                getSession().getLogin().dontUseBatchWriting();
                getSession().getLogin().useJDBCBatchWriting();
                getSession().getLogin().bindAllParameters();
            }
        };
        test.setName("BufferedBatchWritingInsertBatchTest");
        test.setAllowableDecrease(-300);
        addTest(test);
    }

    /**
     * Insert the batch with using old prepared statements on.
     */
    public void addParameterizedStatementsTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                DatabaseAccessor.shouldUseDynamicStatements = false;
                getSession().getLogin().dontBindAllParameters();
                InsertBatchUnitOfWorkComparisonTest.this.test();
                DatabaseAccessor.shouldUseDynamicStatements = true;
                getSession().getLogin().bindAllParameters();
            }
        };
        test.setName("ParameterizedStatementsInsertBatchTest");
        test.setAllowableDecrease(-300);
        addTest(test);
    }
    
    /**
     * Insert the batch with using dynamic statements on.
     */
    public void addDynamicStatementsTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().getLogin().dontBindAllParameters();
                InsertBatchUnitOfWorkComparisonTest.this.test();
                getSession().getLogin().bindAllParameters();
            }
        };
        test.setName("DynamicStatementsInsertBatchTest");
        test.setAllowableDecrease(-300);
        addTest(test);
    }
}
