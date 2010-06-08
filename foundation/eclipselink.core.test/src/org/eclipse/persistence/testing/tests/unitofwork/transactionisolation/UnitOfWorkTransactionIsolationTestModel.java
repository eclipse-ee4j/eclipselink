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
package org.eclipse.persistence.testing.tests.unitofwork.transactionisolation;

import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.models.insurance.*;
import org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkClientSessionTestModel;
import org.eclipse.persistence.sessions.server.Server;


/**
 * Tests the Session read refactoring / reading through the write connection
 * properly feature.
 * On three tier, when UnitOfWork is in transaction, must not only use the
 * write/dirty connection, but also must not put objects read with that dirty
 * connection into the shared cache.
 * <p>
 * UnitOfWork needed to be able to execute queries directly, and to build
 * working copy clones directly from a database row, as opposed to from an
 * original already placed in the shared cache.
 * <p>
 * This series of tests is on a ClientSession, and all UnitOfWorks begin
 * transaction early.  Tests verify that no objects are placed in the UnitOfWork
 * cache until commit time, and that merging where originals may or may not exist
 * in the shared cache works.
 * @author  smcritch
 */
public class UnitOfWorkTransactionIsolationTestModel extends UnitOfWorkClientSessionTestModel {
    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeSystem());
        addRequiredSystem(new InsuranceSystem());
    }

    public void addTests() {
        addTest(UnitOfWorkTransactionIsolationTestModel.getTransactionIsolationTestSuite());
    }

    public static TestSuite getTransactionIsolationTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("TransactionIsolationTestSuite");
        suite.addTest(new TransactionIsolationAlwaysRefreshTest());
        suite.addTest(new TransactionIsolationBatchReadingTest());
        suite.addTest(new TransactionIsolationIndirectionConformingTest());
        suite.addTest(new TransactionIsolationJoinedTest());
        suite.addTest(new TransactionIsolationM2MBatchReadTest());
        suite.addTest(new TransactionIsolationMergeCircularIndirectionTest());
        suite.addTest(new TransactionIsolationMergeIndirectionOriginalsExistTest());
        suite.addTest(new TransactionIsolationMergeIndirectionTest());
        suite.addTest(new TransactionIsolationMergeOriginalsExistTest());
        suite.addTest(new TransactionIsolationMergeTest());
        suite.addTest(new TransactionIsolationNoNewObjectsTest());
        suite.addTest(new TransactionIsolationNoOriginalsIndirectionTest());
        suite.addTest(new TransactionIsolationNoOriginalsTest());
        suite.addTest(new TransactionIsolationRefreshTest());
        suite.addTest(new TransactionIsolationBuildObjectCacheHitTest());
        return suite;
    }

    public Server buildServerSession() {
        // possibly override to use more than one write connection.
        Server server = 
            ((org.eclipse.persistence.sessions.Project)getSession().getProject().clone()).createServerSession(1, 1);
        server.useReadConnectionPool(1, 1);
        server.setSessionLog(getSession().getSessionLog());
        server.login();
        return server;
    }
}
