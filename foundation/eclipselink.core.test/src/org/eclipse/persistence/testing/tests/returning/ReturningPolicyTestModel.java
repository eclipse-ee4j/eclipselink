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
package org.eclipse.persistence.testing.tests.returning;

import java.math.BigDecimal;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;
import org.eclipse.persistence.testing.tests.aggregate.AggregateTestModel;
import org.eclipse.persistence.testing.tests.writing.ComplexUpdateAndUnitOfWorkTestModel;
import org.eclipse.persistence.testing.tests.returning.model.AdapterForReturningProject;
import org.eclipse.persistence.testing.tests.returning.model.ReturningMappingsTestModel;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel;

/**
 * This model tests the ReturningPolicy.
 * It runs a returning specific test suite through several returning configurations.
 * It also runs several existing test models through a test adapter that uses returning and triggers for sequencing.
 * Only Oracle returning is being tested.
 * There should also be tests for Sybase and SQL Server through stored procedures,
 * but these have not be done yet...
 */
public class ReturningPolicyTestModel extends TestModel {
    // used on SyBase platform only: stored procedures won't work unless
    // the flag set to false (at least with JConnect).
    // see DatabaseLogin.handleTransactionsManuallyForSybaseJConnect()
    boolean supportsAutoCommitOriginal;

    public ReturningPolicyTestModel() {
        super();
        setDescription("ReturningPolicy Test Model.");
    }

    public void addTests() {
        addTest(getDescriptorExceptionTestSuite());
        // SubstituteSequencingWithReturningPolicyAdapter works with Oracle only.
        // We should also have tests that return the optimistic lock value for Oracle,
        // once returning supports optimistic locking.
        if (getSession().getPlatform().canBuildCallWithReturning()) {
            addTest(new TestModelAdapted(new ComplexUpdateAndUnitOfWorkTestModel(), new SubstituteSequencingWithReturningPolicyAdapter(false)));
            addTest(new TestModelAdapted(new EmployeeBasicTestModel(), new SubstituteSequencingWithReturningPolicyAdapter()));

            // Need to add the tables that were mapped by AggregateCollectionMappings 
            // overriding the original settings in AggregateCollectionMapping descriptors
            SubstituteSequencingWithReturningPolicyAdapter substitute = new SubstituteSequencingWithReturningPolicyAdapter();
            // Builder.customers
            substitute.getTableToField().put("BUILDER_CUSTOMER", "CUSTOMER_ID");
            substitute.getTableToSequence().put("BUILDER_CUSTOMER", "CUSTOMER_SEQ");
            addTest(new TestModelAdapted(new AggregateTestModel(), substitute));

            addTest(new TestModelAdapted(new InheritanceTestModel(), new SubstituteSequencingWithReturningPolicyAdapter()));
        }
        if (getSession().getPlatform().canBuildCallWithReturning()) {
            AdapterForReturningProject adapter1 = new AdapterForReturningProject();
            adapter1.addInsertSequenceReadOnly("RETURNING.ID");
            adapter1.addInsert("RETURNING.A1", new BigDecimal(1.5), true);
            adapter1.addInsert("RETURNING.B1", new BigDecimal(0), false);
            adapter1.addInsert("RETURNING.C1", new BigDecimal(0.5), true);
            adapter1.addInsert("RETURNING.A2", new BigDecimal(1.5), true);
            adapter1.addInsert("RETURNING.B2", new BigDecimal(0), false);
            adapter1.addInsert("RETURNING.C2", new BigDecimal(0.5), true);
            adapter1.addUpdate("RETURNING.A1", new BigDecimal(3.5), true);
            adapter1.addUpdate("RETURNING.B1", new BigDecimal(2.5), false);
            adapter1.addUpdate("RETURNING.C1", new BigDecimal(1.5), true);
            adapter1.addUpdate("RETURNING.A2", new BigDecimal(3.5), true);
            adapter1.addUpdate("RETURNING.B2", new BigDecimal(2.5), false);
            adapter1.addUpdate("RETURNING.C2", new BigDecimal(1.5), true);
            TestModel testModel1 = new TestModelAdapted(new ReturningMappingsTestModel(adapter1), adapter1);
            testModel1.setName(testModel1.getName() + " 1");
            addTest(testModel1);

            AdapterForReturningProject adapter2 = new AdapterForReturningProject();
            adapter2.addInsertSequenceReadOnly("RETURNING.ID");
            adapter2.addInsert("RETURNING.B1", new BigDecimal(0), false);
            adapter2.addInsert("RETURNING.C1", new BigDecimal(0.5), true);
            adapter2.addInsert("RETURNING.B2", new BigDecimal(0), false);
            adapter2.addInsert("RETURNING.C2", new BigDecimal(0.5), true);
            adapter2.addUpdate("RETURNING.A1", null, false);
            adapter2.addUpdate("RETURNING.B1", null, false);
            adapter2.addUpdate("RETURNING.C1", null, false);
            adapter2.addUpdate("RETURNING.A2", null, false);
            adapter2.addUpdate("RETURNING.B2", null, false);
            adapter2.addUpdate("RETURNING.C2", null, false);
            TestModel testModel2 = new TestModelAdapted(new ReturningMappingsTestModel(adapter2), adapter2);
            testModel2.setName(testModel2.getName() + " 2");
            addTest(testModel2);

            AdapterForReturningProject adapter3 = new AdapterForReturningProject();
            adapter3.addInsertSequenceReadOnly("RETURNING.ID");
            adapter3.addInsert("RETURNING.A1", new BigDecimal(1.5), true);
            adapter3.addInsert("RETURNING.B1", new BigDecimal(0), false);
            adapter3.addInsert("RETURNING.C1", new BigDecimal(0.5), true);
            adapter3.addInsert("RETURNING.A2", new BigDecimal(1.5), true);
            adapter3.addInsert("RETURNING.B2", new BigDecimal(0), false);
            adapter3.addInsert("RETURNING.C2", new BigDecimal(0.5), true);
            adapter3.addUpdate("RETURNING.A1", new BigDecimal(3.5), false);
            adapter3.addUpdate("RETURNING.B1", new BigDecimal(2.5), false);
            adapter3.addUpdate("RETURNING.C1", new BigDecimal(1.5), false);
            adapter3.addUpdate("RETURNING.A2", new BigDecimal(3.5), false);
            adapter3.addUpdate("RETURNING.B2", new BigDecimal(2.5), false);
            adapter3.addUpdate("RETURNING.C2", new BigDecimal(1.5), false);
            TestModel testModel3 = new TestModelAdapted(new ReturningMappingsTestModel(adapter3), adapter3);
            testModel3.setName(testModel3.getName() + " 3");
            addTest(testModel3);

            AdapterForReturningProject adapter4 = new AdapterForReturningProject();
            adapter4.addInsertSequenceReadOnly("RETURNING.ID");
            adapter4.addInsert("RETURNING.A1", new BigDecimal(1.5), true);
            adapter4.addInsert("RETURNING.B1", new BigDecimal(0), false);
            adapter4.addInsert("RETURNING.C1", new BigDecimal(0.5), true);
            adapter4.addInsert("RETURNING.A2", null, false);
            adapter4.addInsert("RETURNING.B2", null, false);
            adapter4.addInsert("RETURNING.C2", null, false);
            //			adapter4.addUpdate("RETURNING.A1", new BigDecimal(3.5), true);
            adapter4.addUpdate("RETURNING.B1", new BigDecimal(2.5), true);
            adapter4.addUpdate("RETURNING.C1", new BigDecimal(1.5), true);
            //			adapter4.addUpdate("RETURNING.A2", new BigDecimal(3.5), true);
            adapter4.addUpdate("RETURNING.B2", new BigDecimal(2.5), true);
            adapter4.addUpdate("RETURNING.C2", new BigDecimal(1.5), true);
            TestModel testModel4 = new TestModelAdapted(new ReturningMappingsTestModel(adapter4), adapter4);
            testModel4.setName(testModel4.getName() + " 4");
            addTest(testModel4);
        }
        // Returning usage with stored procedures should also be tested,
        // but the procedure adapter does not generate returned values yet,
        // so these test should not be run until this has been fixed,
        // as they would not use returning.
        /*
        addTest(new TestModelAdapted(new EmployeeBasicTestModel(), new InsertUpdateStoredProcedureAdapter()));
        addTest(new TestModelAdapted(new AggregateTestModel(), new InsertUpdateStoredProcedureAdapter()));
        addTest(new TestModelAdapted(new InheritanceTestModel(), new InsertUpdateStoredProcedureAdapter()));
        addTest(new TestModelAdapted(new ComplexUpdateAndUnitOfWorkTestModel(), new InsertUpdateStoredProcedureAdapter()));
        */
    }

    public void setup() {
        if (getSession().getPlatform().isSybase() || getSession().getPlatform().isSQLAnywhere()) {
            supportsAutoCommitOriginal = getSession().getPlatform().supportsAutoCommit();
            getSession().getPlatform().setSupportsAutoCommit(false);
        }
        super.setup();
    }

    public void reset() {
        super.reset();
        if (getSession().getPlatform().isSybase()  || getSession().getPlatform().isSQLAnywhere()) {
            getSession().getPlatform().setSupportsAutoCommit(supportsAutoCommitOriginal);
        }
    }

    public static TestSuite getDescriptorExceptionTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("DescriptorExceptionTestSuite");
        suite.setDescription("This suite tests DescriptorExceptions thrown during ReturningPolicy.initialize");

        suite.addTest(new DescriptorExceptionTest(DescriptorException.RETURNING_POLICY_FIELD_TYPE_CONFLICT));
        suite.addTest(new DescriptorExceptionTest(DescriptorException.RETURNING_POLICY_FIELD_INSERT_CONFLICT));
        suite.addTest(new DescriptorExceptionTest(DescriptorException.RETURNING_POLICY_AND_DESCRIPTOR_FIELD_TYPE_CONFLICT));
        suite.addTest(new DescriptorExceptionTest(DescriptorException.RETURNING_POLICY_UNMAPPED_FIELD_TYPE_NOT_SET));
        suite.addTest(new DescriptorExceptionTest(DescriptorException.RETURNING_POLICY_MAPPING_NOT_SUPPORTED));
        suite.addTest(new DescriptorExceptionTest(DescriptorException.RETURNING_POLICY_FIELD_NOT_SUPPORTED, "sequence"));
        suite.addTest(new DescriptorExceptionTest(DescriptorException.RETURNING_POLICY_FIELD_NOT_SUPPORTED, "locking"));
        suite.addTest(new DescriptorExceptionTest(DescriptorException.RETURNING_POLICY_FIELD_NOT_SUPPORTED, "class"));
        suite.addTest(new DescriptorExceptionTest(DescriptorException.CUSTOM_QUERY_AND_RETURNING_POLICY_CONFLICT));
        suite.addTest(new DescriptorExceptionTest(DescriptorException.NO_CUSTOM_QUERY_FOR_RETURNING_POLICY));

        return suite;

    }
}
