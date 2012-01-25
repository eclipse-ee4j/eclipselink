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
package org.eclipse.persistence.testing.tests;

import java.lang.reflect.Method;
import java.util.*;

import junit.framework.Test;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.testing.framework.*;

/**
 * This class create test runs, i.e. models of model to allow all tests to be run a once.
 */
public class TestRunModel extends TestModel {
    protected DatabaseLogin login;
    protected DatabaseLogin oldLogin;
    protected boolean usesNativeMode = false;
    protected boolean isLight = true;
    protected boolean isAll = false;
    protected Vector testList;

    public TestRunModel() {
        // Setup as LRG by default.
        setName("LRGTestModel");
        setDescription("This model runs all of the LRG tests.");
    }

    /**
     * You must add new tests to this method.
     * If the new tests should be part of SRG as well then contact QA to update the SRG model.
     */
    public void addTests() {
        if (!getTests().isEmpty()) {
            return;
        }
        Vector tests = new Vector();

        if (isLight) {        
            tests.add("org.eclipse.persistence.testing.tests.workbenchintegration.MappingWMIntegrationStoredProcedureTestModel");
            tests.add("org.eclipse.persistence.testing.tests.workbenchintegration.MappingWorkbenchIntegrationTestModel");
            tests.add("org.eclipse.persistence.testing.tests.mapping.MappingTestModel");
            tests.add("org.eclipse.persistence.testing.tests.directmap.DirectMapMappingModel");
            tests.add("org.eclipse.persistence.testing.tests.feature.FeatureTestModel");
            tests.add("org.eclipse.persistence.testing.tests.feature.FeatureTestModelWithoutBinding");
            tests.add("org.eclipse.persistence.testing.tests.feature.TopLinkBatchUpdatesTestModel");
            tests.add("org.eclipse.persistence.testing.tests.feature.JDBCBatchUpdatesTestModel");
            tests.add("org.eclipse.persistence.testing.tests.feature.ParameterizedBatchUpdatesTestModel");
            tests.add("org.eclipse.persistence.testing.tests.feature.NativeBatchWritingTestModel");
            tests.add("org.eclipse.persistence.testing.tests.feature.EmployeeJoinFetchTestModel");
            tests.add("org.eclipse.persistence.testing.tests.types.TypeTestModelWithAccessors");
            tests.add("org.eclipse.persistence.testing.tests.types.TypeTestModelWithOutAccessors");
            tests.add("org.eclipse.persistence.testing.tests.conversion.ConversionManagerTestModel");
            tests.add("org.eclipse.persistence.testing.tests.conversion.ConversionManagerTestModelWithoutBinding");
            tests.add("org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel");
            tests.add("org.eclipse.persistence.testing.tests.unidirectional.UnidirectionalEmployeeBasicTestModel");
            tests.add("org.eclipse.persistence.testing.tests.onetoonejointable.OneToOneJoinTableEmployeeBasicTestModel");
            tests.add("org.eclipse.persistence.testing.tests.orderedlist.OrderListTestModel");
            tests.add("org.eclipse.persistence.testing.tests.insurance.InsuranceBasicTestModel");
            tests.add("org.eclipse.persistence.testing.tests.insurance.InsuranceObjectRelationalTestModel");
            tests.add("org.eclipse.persistence.testing.tests.legacy.LegacyTestModel");
            tests.add("org.eclipse.persistence.testing.tests.aggregate.AggregateTestModel");
            tests.add("org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel");
            tests.add("org.eclipse.persistence.testing.tests.ownership.OwnershipTestModel");
            tests.add("org.eclipse.persistence.testing.tests.mapping.OuterJoinWithMultipleTablesTestModel");
            tests.add("org.eclipse.persistence.testing.tests.interfaces.InterfaceWithTablesTestModel");
            tests.add("org.eclipse.persistence.testing.tests.interfaces.InterfaceWithoutTablesTestModel");
            tests.add("org.eclipse.persistence.testing.tests.optimisticlocking.OptimisticLockingTestModel");
            tests.add("org.eclipse.persistence.testing.tests.relationshipmaintenance.RelationshipsTestModel");
            tests.add("org.eclipse.persistence.testing.tests.jpql.JPQLTestModel");
            tests.add("org.eclipse.persistence.testing.tests.simultaneous.SimultaneousTestsModel");
            tests.add("org.eclipse.persistence.testing.tests.writing.ComplexUpdateAndUnitOfWorkTestModel");
            tests.add("org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkClientSessionTestModel");
            tests.add("org.eclipse.persistence.testing.tests.unitofwork.ProtectedUnitOfWorkTestModel");
            tests.add("org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkIsolatedClientSessionTestModel");
            tests.add("org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkIsolatedAlwaysTestModel");
            tests.add("org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkSynchNewObjectsClientSessionTestModel");
            tests.add("org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkPartitionedTestModel");
            tests.add("org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkPartitionedIsolatedAlwaysTestModel");
            tests.add("org.eclipse.persistence.testing.tests.unitofwork.transactionisolation.UnitOfWorkTransactionIsolationTestModel");
            tests.add("org.eclipse.persistence.testing.tests.transparentindirection.TransparentIndirectionModel");
            tests.add("org.eclipse.persistence.testing.tests.collections.CollectionsTestModel");
            tests.add("org.eclipse.persistence.testing.tests.collections.map.MapCollectionsTestModel");
            tests.add("org.eclipse.persistence.testing.tests.customsqlstoredprocedures.CustomSQLTestModel");
            tests.add("org.eclipse.persistence.testing.tests.validation.ValidationModel");
            tests.add("org.eclipse.persistence.testing.tests.readonly.ReadOnlyTestModel");
            tests.add("org.eclipse.persistence.testing.tests.forceupdate.FUVLTestModel");
            tests.add("org.eclipse.persistence.testing.tests.sessionsxml.SessionsXMLBasicTestModel");
            tests.add("org.eclipse.persistence.testing.tests.sessionsxml.SessionsXMLTestModel");
            tests.add("org.eclipse.persistence.testing.tests.unitofwork.changeflag.EmployeeChangeTrackingTestModel");
            tests.add("org.eclipse.persistence.testing.tests.jms.JMSTestModel");
            tests.add("org.eclipse.persistence.testing.tests.helper.HelperTestModel");
            tests.add("org.eclipse.persistence.testing.tests.schemaframework.AutoTableGeneratorBasicTestModel");
            tests.add("org.eclipse.persistence.testing.tests.schemaframework.StoredProcedureGeneratorModel");
            tests.add("org.eclipse.persistence.testing.tests.proxyindirection.ProxyIndirectionTestModel");
            tests.add("org.eclipse.persistence.testing.tests.localization.LocalizationTestModel");
            tests.add("org.eclipse.persistence.testing.tests.security.SecurityTestModel");
            tests.add("org.eclipse.persistence.testing.tests.history.HistoryTestRunModel");
            tests.add("org.eclipse.persistence.testing.tests.isolatedsession.IsolatedSessionTestModel");
            tests.add("org.eclipse.persistence.testing.tests.unitofwork.writechanges.UnitOfWorkWriteChangesTestModel");
            tests.add("org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation.CacheExpiryModel");
            tests.add("org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation.EmployeeTimeToLiveTestModel");
            tests.add("org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation.EmployeeDailyExpiryTestModel");
            tests.add("org.eclipse.persistence.testing.tests.nondeferredwrites.NonDeferredWritesTestModel");
            tests.add("org.eclipse.persistence.testing.tests.weaving.SimpleWeavingTestModel");
            tests.add("org.eclipse.persistence.testing.tests.multipletable.MultipleTableModel");
            tests.add("org.eclipse.persistence.testing.tests.distributedcache.DistributedCacheModel");
            tests.add("org.eclipse.persistence.testing.tests.tableswithspacesmodel.EmployeeWithSpacesTestModel");
            tests.add("org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting.QueryAndSQLCountingTestModel");
            tests.add("org.eclipse.persistence.testing.tests.identitymaps.cache.CacheIdentityMapTestModel");
            tests.add("org.eclipse.persistence.testing.tests.failover.FailoverTestModel");
        }

        // ** All new tests should be in light, unless they require specific db/config support
        // or take a really long time, or need some kind of manual verification.        
        if (isAll) {
            // Requires specific classpath.
            tests.add("org.eclipse.persistence.testing.tests.classpath.ClassPathTestModel");

            // Requires user "scott" unlocked and granted special privileges on oracle database
            tests.add("org.eclipse.persistence.testing.tests.feature.NativeModeCreatorTestModel");

            // Requires usage of Japanese machine and database.
            tests.add("org.eclipse.persistence.testing.tests.nls.japanese.NLSMappingWorkbenchIntegrationTestModel");

            // Requires specific LAB databases.
            tests.add("org.eclipse.persistence.testing.tests.sessionbroker.BrokerTestModel");
            tests.add("org.eclipse.persistence.testing.tests.sessionbroker.MultipleClientBrokersTestModel");
            tests.add("org.eclipse.persistence.testing.tests.sessionbroker.RMISessionBrokerRemoteModel");
            tests.add("org.eclipse.persistence.testing.tests.sessionbroker.ServerBrokerTestModel");
 
            // Requires remote config.
            tests.add("org.eclipse.persistence.testing.tests.remote.RMIRemoteModel");
            tests.add("org.eclipse.persistence.testing.tests.remote.rmi.IIOP.RMIIIOPRemoteModel");
            tests.add("org.eclipse.persistence.testing.tests.remote.suncorba.SunCORBARemoteModel");
            tests.add("org.eclipse.persistence.testing.tests.distributedservers.DistributedSessionBrokerServersModel");
            tests.add("org.eclipse.persistence.testing.tests.distributedservers.rcm.RCMDistributedServersModel");

            // Can take a long time, can deadlock.
            tests.add("org.eclipse.persistence.testing.tests.clientserver.ClientServerTestModel");

            // Requires EIS datasources config.
            tests.add("org.eclipse.persistence.testing.tests.eis.cobol.CobolTestModel");
            tests.add("org.eclipse.persistence.testing.tests.eis.xmlfile.XMLFileTestModel");
            
            // PLSQL
            tests.add("org.eclipse.persistence.testing.tests.plsql.PLSQLTestModel");
            tests.add("org.eclipse.persistence.testing.tests.plsql.PLSQLXMLTestModel");
        }

        for (int index = 0; index < tests.size(); ++index) {
            try {
                addTest((TestModel)Class.forName((String)tests.elementAt(index)).newInstance());
            } catch (Throwable exception) {
                System.out.println("Failed to set up " + tests.elementAt(index) + " \n" + exception);
                //exception.printStackTrace();
            }
        }

        // Sort the tests alphabetically.
        Collections.sort(this.getTests(), new Comparator() {
                public int compare(Object left, Object right) {
                    return Helper.getShortClassName(left.getClass()).compareTo(Helper.getShortClassName(right.getClass()));
                }
            }
        );
        testList = tests;
    }

    /**
     * Return all of the models for the testing tool.
     * To facilitate exporting the testing browser outside of visual age this method has been modified
     * to create the tests reflectively that way if a particular test fails it will not prevent the rest of the tests from building
     */
    public static Vector buildAllModels() {
        Vector models = new Vector();

        try {
            models.add(Class.forName("org.eclipse.persistence.testing.tests.SRGTestModel").newInstance());
        } catch (Exception exception) {
            System.out.println("Failed to set up org.eclipse.persistence.testing.tests.SRGTestModel" + " \n" + exception);
        }
        models.add(buildLRGTestModel());
        models.add(buildNonLRGTestModel());
        models.add(buildOracleTestModel());
        models.add(buildOracleNoSQLTestModel());
        models.add(buildNoSQLTestModel());
        models.add(buildJPATestModel());
        models.add(buildPerformanceTestModel());

        Vector manualTest = new Vector();
        manualTest.add("org.eclipse.persistence.testing.tests.stress.StressTestModel");
        manualTest.add("org.eclipse.persistence.testing.tests.manual.ManualVerificationModel");

        TestModel manual = new TestModel();
        manual.setName("Manual Tests");
        for (int index = 0; index < manualTest.size(); ++index) {
            try {
                manual.addTest((TestModel)Class.forName((String)manualTest.elementAt(index)).newInstance());
            } catch (Exception exception) {
                System.out.println("Failed to set up " + manualTest.elementAt(index) + " \n" + exception);
            }
        }
        models.add(manual);

        return models;
    }

    /**
     * Build and return a model of all JPA tests.
     */
    public static TestModel buildJPATestModel() {
        List tests = new ArrayList();
        tests.add("org.eclipse.persistence.testing.tests.jpa.AllCMP3TestRunModel");
                    
        TestModel model = new TestModel();
        model.setName("JPA Tests");
        for (int index = 0; index < tests.size(); ++index) {
            try {
                model.addTest((TestModel)Class.forName((String)tests.get(index)).newInstance());
            } catch (Throwable exception) {
                System.out.println("Failed to set up " + tests.get(index) + " \n" + exception);
            }
        }
        return model;
    }    
    
    /**
     * Build and return a model of all Oracle specific tests.
     */
    public static TestModel buildOracleTestModel() {
        List tests = new ArrayList();
        tests.add("org.eclipse.persistence.testing.tests.OracleTestModel");
        tests.add("org.eclipse.persistence.testing.tests.OracleJPATestSuite");
        // Requires specific oracle database/driver (oci).
        tests.add("org.eclipse.persistence.testing.tests.xdb.XDBTestModel");
        tests.add("org.eclipse.persistence.testing.tests.xdb.XDBTestModelMWIntegration");
        tests.add("org.eclipse.persistence.testing.tests.unwrappedconnection.UnwrapConnectionXDBTestModel");
        
        TestModel model = new TestModel();
        model.setName("Oracle Tests");
        for (int index = 0; index < tests.size(); ++index) {
            Class cls;
            try {
                cls = Class.forName((String)tests.get(index));
                if(TestModel.class.isAssignableFrom(cls)) {
                    model.addTest((TestModel)cls.newInstance());
                } else {
                    Method suite = cls.getDeclaredMethod("suite", new Class[]{});
                    model.addTest((Test)suite.invoke(null, new Object[]{}));
                }
            } catch (Throwable exception) {
                System.out.println("Failed to set up " + tests.get(index) + " \n" + exception);
            }
        }
        return model;
    }   
    
    /**
     * Build and return a model of all Oracle NoSQL tests.
     */
    public static TestModel buildOracleNoSQLTestModel() {
        List tests = new ArrayList();
        tests.add("org.eclipse.persistence.testing.tests.eis.nosql.NoSQLTestModel");
        tests.add("org.eclipse.persistence.testing.tests.jpa.nosql.NoSQLTestSuite");
        tests.add("org.eclipse.persistence.testing.tests.jpa.nosql.NoSQLMappedTestSuite");

        TestModel model = new TestModel();
        model.setName("Oracle NoSQL Tests");
        for (int index = 0; index < tests.size(); ++index) {
            Class cls;
            try {
                cls = Class.forName((String)tests.get(index));
                if(TestModel.class.isAssignableFrom(cls)) {
                    model.addTest((TestModel)cls.newInstance());
                } else {
                    Method suite = cls.getDeclaredMethod("suite", new Class[]{});
                    model.addTest((Test)suite.invoke(null, new Object[]{}));
                }
            } catch (Throwable exception) {
                System.out.println("Failed to set up " + tests.get(index) + " \n" + exception);
            }
        }
        return model;
    }

    /**
     * Build and return a model of all EIS specific tests.
     */
    public static TestModel buildNoSQLTestModel() {
        List tests = new ArrayList();
        tests.add("org.eclipse.persistence.testing.tests.NoSQLJPATestSuite");
        
        TestModel model = new TestModel();
        model.setName("NoSQL Tests");
        for (int index = 0; index < tests.size(); ++index) {
            Class cls;
            try {
                cls = Class.forName((String)tests.get(index));
                if(TestModel.class.isAssignableFrom(cls)) {
                    model.addTest((TestModel)cls.newInstance());
                } else {
                    Method suite = cls.getDeclaredMethod("suite", new Class[]{});
                    model.addTest((Test)suite.invoke(null, new Object[]{}));
                }
            } catch (Throwable exception) {
                System.out.println("Failed to set up " + tests.get(index) + " \n" + exception);
            }
        }
        return model;
    }
    
    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     */
    public static junit.framework.TestSuite suite() {
        return buildLRGTestModel();
    }
    
    public static TestRunModel buildLRGTestModel() {
        TestRunModel model = new TestRunModel();
        model.setName("LRGTestModel");
        model.setDescription("This model runs all of the LRG tests.");
        model.isLight = true;
        model.addTests();

        return model;
    }

    public static TestRunModel buildAllTestModels() {
        TestRunModel model = new TestRunModel();
        model.setName("AllTestModels");
        model.setDescription("This model runs all of the tests under a single database, without client/server and JTS.");
        model.isLight = true;
        model.isAll = true;
        model.addTests();

        return model;
    }

    public static TestRunModel buildNonLRGTestModel() {
        TestRunModel model = new TestRunModel();
        model.setName("NonLRGTestModel");
        model.setDescription("This model includes all of the tests not in the LRG.");
        model.isLight = false;
        model.isAll = true;
        model.addTests();

        return model;
    }

    /**
     *  Created by Edwin Tang and used for BatchTestRunner
     */
    public static Vector buildLRGTestList() {
        TestRunModel model = new TestRunModel();
        model.isLight = true;
        model.addTests();
        return model.testList;
    }

    /**
     *  Created by Edwin Tang and used for BatchTestRunner
     */
    public static Vector buildAllTestModelsList() {
        TestRunModel model = new TestRunModel();
        model.isLight = true;
        model.isAll = true;
        model.addTests();
        return model.testList;
    }

    /**
     *  Created by Edwin Tang and used for BatchTestRunner
     */
    public static Vector buildNonLRGTestList() {
        TestRunModel model = new TestRunModel();
        model.isLight = false;
        model.isAll = true;
        model.addTests();
        return model.testList;
    }

    /**
     * Build and return a model of all performance tests.
     */
    public static TestModel buildPerformanceTestModel() {
        Vector performanceTests = new Vector();
        performanceTests.add("org.eclipse.persistence.testing.tests.performance.PerformanceComparisonModel");
        performanceTests.add("org.eclipse.persistence.testing.tests.performance.PerformanceTestModel");
        performanceTests.add("org.eclipse.persistence.testing.tests.performance.PerformanceTestModelRun");
        performanceTests.add("org.eclipse.persistence.testing.tests.performance.ConcurrencyComparisonTestModel");
        performanceTests.add("org.eclipse.persistence.testing.tests.performance.ConcurrencyRegressionTestModel");
        performanceTests.add("org.eclipse.persistence.testing.tests.performance.JavaPerformanceComparisonModel");
        performanceTests.add("org.eclipse.persistence.testing.tests.jpa.performance.JPAPerformanceTestModel");
        performanceTests.add("org.eclipse.persistence.testing.tests.jpa.memory.JPAMemoryTestModel");
                    
        TestModel performanceModel = new TestModel();
        performanceModel.setName("Performance Tests");
        for (int index = 0; index < performanceTests.size(); ++index) {
            try {
                performanceModel.addTest((TestModel)Class.forName((String)performanceTests.elementAt(index)).newInstance());
            } catch (Exception exception) {
                System.out.println("Failed to set up " + performanceTests.elementAt(index) + " \n" + exception);
            }
        }
        return performanceModel;
    }
    
    /**
     * Reset to the old login.
     */
    public void reset() {
        // Change the login if specified.
        if (login != null) {
            getDatabaseSession().logout();
            getDatabaseSession().login(oldLogin);
        }

        getExecutor().initializeConfiguredSystems();
    }

    /**
     * Allow the login to be configured.
     */
    public void setup() {
        // Change the login if specified.
        if (login != null) {
            oldLogin = getSession().getLogin();
            DatabaseLogin newLogin = (DatabaseLogin)login.clone();
            getDatabaseSession().logout();
            getDatabaseSession().login(newLogin.clone());
        }

        // Change to native mode if specified.
        if (usesNativeMode) {
            getSession().getLogin().setUsesNativeSQL(true);
            getSession().getLogin().useNativeSequencing();
        }
    }
}
