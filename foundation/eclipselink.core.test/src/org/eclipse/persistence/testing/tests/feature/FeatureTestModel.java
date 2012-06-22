/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.models.insurance.InsuranceSystem;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.queries.*;
import org.eclipse.persistence.testing.models.multipletable.ProjectSystem;
import org.eclipse.persistence.testing.models.ownership.*;
import org.eclipse.persistence.testing.models.legacy.*;
import org.eclipse.persistence.testing.tests.queries.options.*;
import org.eclipse.persistence.testing.tests.queries.repreparation.*;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;
import org.eclipse.persistence.testing.tests.events.EventHookTestSuite;
import org.eclipse.persistence.testing.models.events.EventHookSystem;
import org.eclipse.persistence.testing.tests.expressions.ExpressionInMemoryTestSuite;
import org.eclipse.persistence.testing.tests.expressions.ExpressionOperatorUnitTestSuite;
import org.eclipse.persistence.testing.tests.expressions.ExpressionOuterJoinTestSuite;
import org.eclipse.persistence.testing.tests.expressions.ExpressionSubSelectTestSuite;
import org.eclipse.persistence.testing.tests.expressions.ExpressionTestSuite;
import org.eclipse.persistence.testing.tests.expressions.ExpressionUnitTestSuite;
import org.eclipse.persistence.testing.tests.identitymaps.IdentityMapTestSuite;
import org.eclipse.persistence.testing.tests.transactions.ReadingThroughWriteConnectionInTransactionTest;
import org.eclipse.persistence.testing.tests.transactions.TransactionTestSuite;
import org.eclipse.persistence.testing.tests.writing.CommitOrderTest;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.tests.sessioncache.SessionCacheTestSuite;

public class FeatureTestModel extends TestModel {

    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     * Unfortunately JUnit only allows suite methods to be static,
     * so it is not possible to generically do this.
     */
    public static junit.framework.TestSuite suite() {
        return new FeatureTestModel();
    }

    public FeatureTestModel() {
        setDescription("This model tests selected TopLink features using the employee demo.");
    }

    public FeatureTestModel(boolean isSRG) {
        this();
        this.isSRG = isSRG;
    }

    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.models.aggregate.AggregateSystem());
        addRequiredSystem(new InheritanceSystem());
        addRequiredSystem(new InsuranceSystem());
        addRequiredSystem(new OwnershipSystem());
        addRequiredSystem(new LegacySystem());
        addRequiredSystem(new EventHookSystem());
        addRequiredSystem(new ProjectSystem());
        addRequiredSystem(new IdentitySystem());
        addRequiredSystem(new org.eclipse.persistence.testing.models.collections.CollectionsSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.models.mapping.MappingSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.models.sequencing.SequenceTestSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.tests.queries.report.ReportQuerySystem());
        addRequiredSystem(new org.eclipse.persistence.testing.tests.queries.options.QueryOptionSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.models.bigbad.BigBadSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.models.vehicle.VehicleSystem());
        // Force field names to upper case for custom SQL tests on postgres.
        if (getSession().getPlatform().isPostgreSQL()) {
            getSession().getPlatform().setShouldForceFieldNamesToUpperCase(true);
        }
    }

    public void addTests() {
        addTest(new IdentityMapTestSuite());
        addTest(new ExpressionTestSuite());
        addTest(new ExpressionInMemoryTestSuite());
        addTest(new ExpressionSubSelectTestSuite());
        addTest(new ExpressionOperatorUnitTestSuite());
        addTest(new ExpressionOuterJoinTestSuite());
        addTest(new ExpressionUnitTestSuite());
        addTest(new QueryFrameworkTestSuite());
        addTest(new org.eclipse.persistence.testing.tests.queries.inmemory.CacheHitAndInMemoryTestSuite());
        addTest(new EventHookTestSuite());
        addTest(getOptimisticLockingTestSuite());
        addTest(new org.eclipse.persistence.testing.tests.queries.optimization.QueryOptimizationTestSuite());
        addTest(new org.eclipse.persistence.testing.tests.queries.report.ReportQueryTestSuite());
        addTest(new TransactionTestSuite());
        addTest(getReadingThroughWriteConnectionSuite());
        addTest(getIntegrityCheckerTestSuite());
        addTest(getExceptionHandlerTestSuite());
        addTest(getMessageLoggingTestSuite());
        addTest(getFieldedMessageLoggingTestSuite());
        addTest(getNullValueTestSuite());
        addTest(getMiscTestSuite());
        if (!getExecutor().isServer) {
            addTest(getJNDIConnectionTestSuite());
        }
        addTest(getCustomSequenceTestSuite());
        addTest(getSequenceTestSuite());
        addTest(getCopyingTestSuite());
        addTest(new QueryOptionTestSuite());
        addTest(new QueryRepreparationTestSuite());
        addTest(getDatabaseLoginCodeCoverageTestSuite());
        addTest(getInstantiationPoicyTestSuite());
        addTest(new SessionCacheTestSuite());
        addTest(getSessionAPITestSuite());
        addTest(new CommitOrderTest());
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public void addSRGTests() {
        addTest(new ExpressionTestSuite(true));
        addTest(new QueryFrameworkTestSuite(true));
        addTest(new TransactionTestSuite(true));
        addTest(new EventHookTestSuite(true));
    }

    // For CR#4334, test reading through write connection when in transaction and
    // using a client/server setup.
    //
    public static TestSuite getReadingThroughWriteConnectionSuite() {
        TestSuite suite;
        suite = new TestSuite();
        suite.setName("ReadingThroughWriteConnectionSuite");
        suite.setDescription("This suite tests reading through write connection when in transaction and using a client/server.");
        suite.addTests(ReadingThroughWriteConnectionInTransactionTest.buildTests());
        return suite;
    }
    // CR3855


    public static TestSuite getDatabaseLoginCodeCoverageTestSuite() {
        TestSuite suite;
        suite = new TestSuite();
        suite.setName("DatabaseLoginCodeCoverageTestSuite");
        suite.setDescription("This tests code coverage for DatabaseLogin.");
        suite.addTest(new DatabaseLoginCodeCoverageTest());
        return suite;
    }

    public static TestSuite getCopyingTestSuite() {
        TestSuite suite;
        suite = new TestSuite();
        suite.setName("CopyingTestSuite");
        suite.setDescription("This tests object copying.");
        suite.addTest(new ObjectCopyingTest());
        return suite;
    }

    public static TestSuite getCustomSequenceTestSuite() {
        TestSuite suite;

        suite = new TestSuite();
        suite.setName("CustomSequenceTestSuite");
        suite.setDescription("This tests custom sequencing.");

        suite.addTest(new CustomSequenceTest());

        return suite;

    }

    public static TestSuite getExceptionHandlerTestSuite() {
        TestSuite suite;
        suite = new TestSuite();
        suite.setName("ExceptionHandler");
        suite.setDescription("This Suite tests the functionality of ExceptionHandler.");
        suite.addTest(new ExceptionHandlerTest1());
        suite.addTest(new ExceptionHandlerTest2());
        suite.addTest(new ExceptionHandlerLoginIntegrityTest());
        return suite;

    }

    public static TestSuite getMessageLoggingTestSuite() {
        TestSuite suite;
        suite = new TestSuite();
        suite.setName("Message Logging");
        suite.setDescription("This Suite tests the functionality of Logging messages.");
        suite.addTest(new MessageLoggingEfficiencyTest());
        return suite;
    }

    public static TestSuite getFieldedMessageLoggingTestSuite() {
        TestSuite suite;
        suite = new TestSuite();
        suite.setName("Fielded Message Logging");
        suite.setDescription("This Suite tests the functionality of Logging messages containing formatting strings.");
        suite.addTest(new FieldedMessageLoggingTest());
        // This test exercises the code that does a key:value lookup for messages that are not translated
        suite.addTest(new FieldedMessageLoggingTraceLocalizationTest());
        return suite;
    }

    
    public static TestSuite getIntegrityCheckerTestSuite() {
        TestSuite suite;

        suite = new TestSuite();
        suite.setName("Integrity Checker");
        suite.setDescription("This Suite tests the functionality of Integrity Checker.");

        suite.addTest(new IntegrityCheckerTest());
        suite.addTest(new IntegrityCheckerLazyCreationTest());
        suite.addTest(new ShouldThroughIntegrityCheckerTest());
        suite.addTest(new ShouldNotThroughIntegrityCheckerTest());
        suite.addTest(new ShouldCheckDatabaseTest());
        suite.addTest(new ShouldNotCheckDatabaseTest());

        return suite;

    }

    public static TestSuite getJNDIConnectionTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("JNDIConnectionTestSuite");
        suite.setDescription("Test connecting to a database with a JNDI-supplied DataSource");

        suite.addTest(new JNDIConnectionTest());

        return suite;
    }

    public static TestSuite getMiscTestSuite() {
        TestSuite suite;

        suite = new TestSuite();
        suite.setName("MiscellaneousTests");
        suite.setDescription("Tests that don't fit anywhere else.  Okay, sue me.");

        suite.addTest(new OptomizeValueHolderTest());
        suite.addTest(new NullPasswordLoginTest());
        suite.addTest(new HelperSystemPropertyTest());
        suite.addTest(new GermanUmlautsWriteTest());
        suite.addTest(new ReconnectFlushStatementCacheTest());
        suite.addTest(new TransactionIsolationTest());
        suite.addTest(new SessionIsConnectedFlagTest());
        suite.addTest(new ContainerIndirectionPolicySerializationTest());

        return suite;

    }

    public static TestSuite getNullValueTestSuite() {
        TestSuite suite;

        suite = new TestSuite();
        suite.setName("NullValueTestSuite");
        suite.setDescription("Test default null value settings");

        suite.addTest(new NullValueTest());
        suite.addTest(new NoDefaultNullValueTest());
        return suite;

    }

    public static TestSuite getOptimisticLockingTestSuite() {
        TestSuite suite;

        suite = new TestSuite();
        suite.setName("OptimisticLockingTestSuite");
        suite.setDescription("This suite tests the functionality of the optimistic locking feature.");

        suite.addTest(new OptimisticLockingDeleteRowTest());
        suite.addTest(new OptimisticLockingChangedValueTest());
        suite.addTest(new OptimisticLockingChangedValueUpdateTest());
        suite.addTest(new OptimisticLockingDeleteValueUpdateTest());

        return suite;

    }

    public static TestSuite getSequenceTestSuite() {
        TestSuite suite;
        suite = new TestSuite();
        suite.setName("SequenceTestSuite");
        suite.setDescription("This tests both custom and regular sequencing.");
        suite.addTest(new CustomSequenceTest());
        suite.addTest(new SequencingConcurrencyTest(7, 100, false, false));
        suite.addTest(new SequencingConcurrencyTest(7, 100, false, false, 2));
        suite.addTest(new SequencingConcurrencyTest(7, 100, false, true));
        suite.addTest(new SequencingConcurrencyTest(7, 100, true, false));
        suite.addTest(new SequencingConcurrencyTest(7, 100, true, false, 2));
        suite.addTest(new SequencingConcurrencyTest(7, 100, true, true));
        suite.addTest(new SequencingRollbackTest(true));
        suite.addTest(new SequencingRollbackTest(false));
        suite.addTest(new SequenceStringPKInsertTest());
        suite.addTest(new SequenceStringExistingPKTest());
        suite.addTest(new CustomSequencingPolicyTest());
        suite.addTest(new InitializeDescriptorsBeforeLoginTest());
        suite.addTest(new SequencingTableQualifierTest(SequencingTableQualifierTest.TABLE_SEQUENCE));
        suite.addTest(new SequencingTableQualifierTest(SequencingTableQualifierTest.UNARY_TABLE_SEQUENCE));
        suite.addTest(new SequenceFieldRemovalForAcquireValueAfterInsertTest());
        suite.addTest(new CannotOverrideConnectedSequenceTest());
        return suite;
    }

    public static TestSuite getInstantiationPoicyTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Instantiation Policy Test Suite");

        suite.setDescription("Test instantiation policy feature.");
        suite.addTest(new InstantiationPolicyTest());
        return suite;
    }

    public static TestSuite getSessionAPITestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Session API Test Suite");

        suite.setDescription("Test Session API.");
        suite.addTest(new GetClassDescriptorForAliasTest());
        return suite;
    }

}
