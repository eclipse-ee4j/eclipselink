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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;
import org.eclipse.persistence.testing.tests.insurance.InsuranceBasicTestModel;
import org.eclipse.persistence.testing.tests.writing.*;
import org.eclipse.persistence.testing.tests.queries.*;
import org.eclipse.persistence.testing.models.readonly.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.transparentindirection.*;
import org.eclipse.persistence.testing.tests.transparentindirection.*;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;

public abstract class RemoteModel extends TestModel {
    public static Session originalSession;
    protected static Session serverSession;

    public RemoteModel() {
        setDescription("It tests TopLink three tier model.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.models.insurance.InsuranceSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.tests.unitofwork.UOWSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.models.mapping.MappingSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.models.ownership.OwnershipSystem());
        addRequiredSystem(new InheritanceSystem());
        addRequiredSystem(new IndirectListSystem());
        addRequiredSystem(new IndirectMapSystem());
        addRequiredSystem(new MasterSlaveSystem());
        addRequiredSystem(new ReadOnlySystem());
        addRequiredSystem(new org.eclipse.persistence.testing.tests.queries.options.QueryOptionSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.tests.queries.report.ReportQuerySystem());
    }

    public void addTests() {
        addTest(getBasicReadTestSuite());
        addTest(getBasicUnitOfWorkTestSuite());
        addTest(getUnitOfWorkEventTestSuite());
        addTest(getFeatureTestSuite());
        addTest(getQueryTestSuite());
        addTest(getRefreshObjectTestSuite());
        addTest(new org.eclipse.persistence.testing.tests.queries.report.ReportQueryTestSuite());
        addTest(getTransparentIndirectionTestSuite());
        addTest(new RemoteValueHolderGarbageCollectedOriginalTest(getSession()));
    }

    public Session buildServerSession() {
        org.eclipse.persistence.sessions.server.ServerSession server = (org.eclipse.persistence.sessions.server.ServerSession)((org.eclipse.persistence.sessions.Project)getSession().getProject().clone()).createServerSession(1, 1);
        server.useReadConnectionPool(1, 1);
        server.setSessionLog(getSession().getSessionLog());

        server.login();
        // ** this is a big hack but for the existing uow tests we need the read and writer connections to be the same to avoid transaction problems.
        DatabaseAccessor readConnection = (DatabaseAccessor)server.getReadConnectionPool().getConnectionsAvailable().get(0);
        DatabaseAccessor writeConnection = (DatabaseAccessor)server.getDefaultConnectionPool().getConnectionsAvailable().get(0);
        writeConnection.disconnect(server);
        server.getDefaultConnectionPool().getConnectionsAvailable().remove(writeConnection);
        server.getDefaultConnectionPool().getConnectionsAvailable().add(readConnection);

        // Explicitly add a default read-only class to the server session since the default read-only
        // classes are not transferred at set up time in the test framework.
        getSession().getProject().addDefaultReadOnlyClass(DefaultReadOnlyTestClass.class);

        serverSession = server.acquireClientSession();
        return serverSession;
    }

    public static TestSuite getBasicReadTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("BasicReadTestSuite");
        suite.setDescription("This suite tests the reading of objects remotely.");

        suite.addTest(EmployeeBasicTestModel.getReadObjectTestSuite());
        suite.addTest(EmployeeBasicTestModel.getReadAllTestSuite());

        suite.addTest(InsuranceBasicTestModel.getReadObjectTestSuite());
        suite.addTest(InsuranceBasicTestModel.getReadAllTestSuite());

        return suite;
    }

    public static TestSuite getBasicUnitOfWorkTestSuite() {
        return ComplexUpdateAndUnitOfWorkTestModel.getUnitOfWorkTestSuite();
    }

    public static TestSuite getFeatureTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("FeatureTestSuite");
        suite.setDescription("This suite tests the features on the remote model.");

        suite.addTest(new CursoredStreamTest(org.eclipse.persistence.testing.models.employee.domain.Employee.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("address").get("city").notEqual("Tuck chu")));
        suite.addTest(new ScrollableCursorTest(org.eclipse.persistence.testing.models.employee.domain.Employee.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("firstName").like("B%")));
        suite.addTest(new ScrollableCursorTest(org.eclipse.persistence.testing.models.employee.domain.LargeProject.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("teamLeader").get("firstName").like("Sarah%")));
        suite.addTest(new ScrollableCursorAPITest());
        suite.addTest(new ScrollableCursorBackwardReadingTest());
        suite.addTest(new ScrollableCursorNavigationAPITest());
        suite.addTest(new PredefinedQueryReadObjectTest(org.eclipse.persistence.tools.schemaframework.PopulationManager.getDefaultManager().getObject(org.eclipse.persistence.testing.models.employee.domain.Employee.class, "0001")));
        suite.addTest(new PredefinedInQueryReadAllTest(org.eclipse.persistence.testing.models.employee.domain.Employee.class, 1));
        suite.addTest(new PessimisticLockTest(org.eclipse.persistence.queries.ObjectLevelReadQuery.LOCK));
        suite.addTest(new PessimisticLockTest(org.eclipse.persistence.queries.ObjectLevelReadQuery.LOCK_NOWAIT));
        suite.addTest(new org.eclipse.persistence.testing.tests.feature.PrintIdentityMapTest(org.eclipse.persistence.testing.models.employee.domain.Employee.class));
        suite.addTest(new org.eclipse.persistence.testing.tests.feature.PrintIdentityMapTest());
        suite.addTest(new DefaultReadOnlyClassTest());
        suite.addTest(new RemoteSessionReadTimeTransferTest());
        suite.addTest(new CacheExpiryRemoteTest());
        return suite;
    }

    public static TestSuite getQueryTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("QueryTestSuite");
        suite.setDescription("This suite tests features of queries (cascading, cache updating).");
        suite.addTest(new RefreshTest());
        suite.addTest(new RefreshCascadeNonPrivateTest());
        suite.addTest(new CascadeWithoutIMOnRemote());

        /** Test cascaded read queries */
        suite.addTest(new CascadingAllCacheTest());
        suite.addTest(new CascadingAllNoCacheTest());
        suite.addTest(new CascadingNoneCacheTest());
        suite.addTest(new CascadingNoneNoCacheTest());
        suite.addTest(new CascadingPrivateCacheTest());
        suite.addTest(new CascadingPrivateNoCacheTest());
        suite.addTest(new ReadingThroughRemoteScrollableCursor());
        suite.addTest(new BatchReadingForDirectCollectionMapping());

        suite.addTest(new PredefinedQueryFromRemoteSessionTest());

        suite.addTest(new QueryCacheHitEnabledAndDescriptorDisabledOnRemoteTest());
        suite.addTest(new QueryCacheHitUndefinedAndDescriptorEnabledOnRemoteTest());
        suite.addTest(new QueryCacheHitUndefinedAndDescriptorDisabledOnRemoteTest());
        suite.addTest(new QueryCacheTest());
        return suite;
    }

    public static TestSuite getRefreshObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("RefreshObjectTestSuite");
        suite.setDescription("This suite tests remote refreshing: Checks if null pointer exception is thrown.");
        suite.addTest(new RefreshObjectTest());
        suite.addTest(new DescriptorRefreshCacheOnRemoteTest());
        suite.addTest(new RefreshMaintainIdentityTest());
        suite.addTest(new RefreshRemoteIdentityMapResultsTest());

        return suite;
    }

    public static Session getServerSession() {
        return serverSession;
    }

    public static TestSuite getTransparentIndirectionTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("TransparentIndirectionTestSuite");
        suite.setDescription("Test remote Transparent Indirection");

        suite.addTest(new ZTestSuite(IndirectListTestDatabaseRemote.class));
        suite.addTest(new ZTestSuite(IndirectMapTestDatabaseRemote.class));
        suite.addTest(new RemoteDataReadQueryTest());

        return suite;
    }

    public TestSuite getUnitOfWorkEventTestSuite() {
        TestSuite suite = new org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkEventTestSuite();
        return suite;
    }

    public void reset() {
        // Setup might not be run yet.
        if (originalSession != null) {
            getExecutor().getSession().release();
            getExecutor().setSession(originalSession);
        }
        getServerSession().release();
        ((org.eclipse.persistence.sessions.server.ClientSession)getServerSession()).getParent().logout();
    }

    public static void setServerSession(Session session) {
        serverSession = session;
    }

    public abstract void setup();
}
