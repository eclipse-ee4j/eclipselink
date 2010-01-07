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
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.AggregateSystem;
import org.eclipse.persistence.testing.models.aggregate.Builder;
import org.eclipse.persistence.testing.models.aggregate.Client;
import org.eclipse.persistence.testing.models.aggregate.Employee;
import org.eclipse.persistence.testing.models.aggregate.Employee1;
import org.eclipse.persistence.testing.models.aggregate.EvaluationClient;
import org.eclipse.persistence.testing.models.aggregate.Transport;

/**
 * This model tests reading/writing/deleting through using the aggregate model.
 */
public class AggregateTestModel extends TestModel {
    // The new apis added to AggregateCollectionMapping
    // in order to support jpa 2.0 element collections currently
    // are not compatible with project.xml
    // The flag provided so that XMLProjectWriterTestModel
    // could remove all tests that for this new feature.
    public static boolean useNewAggregateCollection = true;
    public AggregateTestModel() {
        setDescription("This model tests reading/writing/deleting of the complex aggregate model.");
    }

    public AggregateTestModel(boolean isSRG) {
        this();
        this.isSRG = isSRG;
    }

    public void addForcedRequiredSystems() {
        //We need to ensure that the correct database schema is created
        addForcedRequiredSystem(new AggregateSystem());
    }

    public void addRequiredSystems() {
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getUnitOfWorkTestSuite());
        addTest(getUnitOfWorkCommitResumeTestSuite());
        addTest(getCheckForNullUnitOfWorkTestSuite());
        addTest(getMergingUnitOfWorkTestSuite());
        addTest(getDescriptorPropertiesTestSuite());
        addTest(getEventTestSuite());
        addTest(getNestedAggregateTestSuite());
        addTest(getAggregateInheritanceTestSuite());
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public void addSRGTests() {
        addTest(getSRGReadObjectTestSuite());
        addTest(getSRGUpdateObjectTestSuite());
        addTest(getSRGReadAllTestSuite());
        addTest(getSRGDeleteObjectTestSuite());
        addTest(getSRGInsertObjectTestSuite());
        addTest(getSRGUnitOfWorkTestSuite());
        addTest(getSRGUnitOfWorkCommitResumeTestSuite());
        addTest(getSRGCheckForNullUnitOfWorkTestSuite());
        addTest(getSRGMergingUnitOfWorkTestSuite());
        addTest(getSRGDescriptorPropertiesTestSuite());
        addTest(getSRGEventTestSuite());
        addTest(getSRGNestedAggregateTestSuite());
        addTest(getSRGAggregateInheritanceTestSuite());
    }

    public static TestSuite getAggregateInheritanceTestSuite() {
        TestSuite suite = getSRGAggregateInheritanceTestSuite();
        suite.addTest(new AggregateInheritanceInitAggregateTestCase());

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGAggregateInheritanceTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("AggregateInheritanceTestSuite");
        suite.setDescription("This suite test that aggregate inheritance is handled correctly");

        suite.addTest(new AggregateInheritanceTestCase());
        suite.addTest(new AggregateInheritanceTypeFieldTestCase());
        suite.addTest(new AggregateWithoutAttributesInheritanceTestCase());
        suite.addTest(new AggregateTransientValueTestCase());

        return suite;
    }

    public static TestSuite getCheckForNullUnitOfWorkTestSuite() {
        TestSuite suite = getSRGCheckForNullUnitOfWorkTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGCheckForNullUnitOfWorkTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("AggregateCheckForNullUnitOfWorkTestSuite");
        suite.setDescription("This suite tests updating objects with UOW in the aggregate model.");

        Class employeeClass = Employee.class;
        Class clientClass = Client.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new CheckForNullUnitOfWorkTest(manager.getObject(employeeClass, "example1")));
        suite.addTest(new CheckForNullUnitOfWorkTest(manager.getObject(employeeClass, "example2")));
        suite.addTest(new CheckForNullUnitOfWorkTest(manager.getObject(employeeClass, "example3")));

        return suite;
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = getSRGDeleteObjectTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("AggregateDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the aggregate model.");

        Class employeeClass = Employee.class;
        Class clientClass = Client.class;
        Class evaluationClientClass = EvaluationClient.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "example2")));
        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "example3")));

        suite.addTest(new DeleteObjectTest(manager.getObject(clientClass, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(clientClass, "example2")));
        suite.addTest(new DeleteObjectTest(manager.getObject(clientClass, "example3")));

        suite.addTest(new DeleteObjectTest(manager.getObject(evaluationClientClass, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(evaluationClientClass, "example2")));
        suite.addTest(new DeleteObjectTest(manager.getObject(evaluationClientClass, "example3")));

        //aggregate 1:m delete test
        suite.addTest(new DeleteObjectTest(manager.getObject(Agent.class, "example1")));
        suite.addTest(new VerifyCascadeDelete(Agent.class));
        suite.addTest(new AggregateCollectionClearTest(Agent.class));

        if(useNewAggregateCollection) {
            suite.addTest(new DeleteObjectTest(manager.getObject(Builder.class, "example1")));
            suite.addTest(new VerifyCascadeDelete(Builder.class));
            suite.addTest(new AggregateCollectionClearTest(Builder.class));
        }
        return suite;
    }

    public static TestSuite getDescriptorPropertiesTestSuite() {
        TestSuite suite = getSRGDescriptorPropertiesTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGDescriptorPropertiesTestSuite() {
        PopulationManager manager = PopulationManager.getDefaultManager();

        TestSuite suite = new TestSuite();
        suite.setName("AggregateDescriptorPropertiesTestSuite");
        suite.setDescription("This suite tests descriptor properties on the aggregate model.");

        suite.addTest(new DescriptorRefreshCacheTest(manager.getObject(Employee.class, "example2")));
        if(useNewAggregateCollection) {
            suite.addTest(new AgentBuilderTablesTest());
        }
        return suite;
    }

    public static TestSuite getEventTestSuite() {
        TestSuite suite = getSRGEventTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGEventTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("AggregateEventTestSuite");
        suite.setDescription("This suite test that aggregate events are being thrown appropiately");

        suite.addTest(new AggregateEventTestCase());

        return suite;
    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = getSRGInsertObjectTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("AggregateInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the aggregate model.");

        suite.addTest(new InsertObjectTest(Employee.example1()));
        suite.addTest(new InsertObjectTest(Employee.example2()));
        suite.addTest(new InsertObjectTest(Employee.example3()));

        suite.addTest(new InsertObjectTest(Client.example1()));
        suite.addTest(new InsertObjectTest(Client.example2()));
        suite.addTest(new InsertObjectTest(Client.example3()));

        suite.addTest(new InsertObjectTest(EvaluationClient.example1()));
        suite.addTest(new InsertObjectTest(EvaluationClient.example2()));
        suite.addTest(new InsertObjectTest(EvaluationClient.example3()));

        //insert aggregate collection object
        suite.addTest(new InsertObjectTest(Agent.example2()));
        if(useNewAggregateCollection) {
            suite.addTest(new InsertObjectTest(Builder.example1()));
        }
        suite.addTest(new AggregateMappingNullNotAllow());

        return suite;
    }

    public static TestSuite getMergingUnitOfWorkTestSuite() {
        TestSuite suite = getSRGMergingUnitOfWorkTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGMergingUnitOfWorkTestSuite() {
        PopulationManager manager = PopulationManager.getDefaultManager();

        TestSuite suite = new TestSuite();
        suite.setName("AggregateMergingUnitOfWorkTestSuite");
        suite.setDescription("This suite tests merging of objects from two UOWs in the aggregate model.");

        suite.addTest(new MergingUnitOfWorkTest(manager.getObject(Employee.class, "example1")));

        return suite;
    }

    public static TestSuite getNestedAggregateTestSuite() {
        TestSuite suite = getSRGNestedAggregateTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGNestedAggregateTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("NestedAggregateTestSuite");
        suite.setDescription("Tested that nested aggregates are handled properly.");

        suite.addTest(new NestedAggregateTestCase());
        //For CR#2587
        suite.addTest(new NestedAggregateCollectionTest(Agent.class));

        // CR#2896
        suite.addTest(new NestedAggregateCollectionAbstractTestCase(Agent.class));
        
        if(useNewAggregateCollection) {
            suite.addTest(new NestedAggregateCollectionTest(Builder.class));
            suite.addTest(new NestedAggregateCollectionAbstractTestCase(Builder.class));
        }
        return suite;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = getSRGReadAllTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("AggregateReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the aggregate model.");

        suite.addTest(new ReadAllTest(Employee1.class, 2));

        //====================
        suite.addTest(new ReadAllTest(Employee.class, 3));
        suite.addTest(new ReadAllTest(Client.class, 3));
        suite.addTest(new ReadAllTest(EvaluationClient.class, 3));

        //aggregate collection read all
        suite.addTest(new ReadAllTest(Agent.class, 1));

        // Added May 5, 2000 - Jon D. for pr381
        suite.addTest(new ReadAllTest(Transport.class, 4));
        // Bug 2847621
        suite.addTest(new AggregateCollectionOuterJoinTest(Agent.class));
        
        if(useNewAggregateCollection) {
            suite.addTest(new ReadAllTest(Builder.class, 1));
            suite.addTest(new AggregateCollectionOuterJoinTest(Builder.class));
        }

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = getSRGReadObjectTestSuite();

        // Add new tests here...
        suite.addTest(new JoinInAggregateAndOwnerTest());

        suite.addTest(new InMemoryQueryKeyToAggregateTest(true));
        suite.addTest(new InMemoryQueryKeyToAggregateTest(false));
        // added for bug 5478648 - D.Minsky
        suite.addTest(new AggregateWithOneToOneRelationshipTest());
        // added for bug 6033380
        suite.addTest(new AggregateInvalidationIdentityTest());
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("AggregateReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the aggregate model.");

        //=====================================
        Class employee1Class = Employee1.class;

        //======================================
        Class employeeClass = Employee.class;
        Class clientClass = Client.class;
        Class evaluationClientClass = EvaluationClient.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        //===============================================================================
        suite.addTest(new ReadObjectTest(manager.getObject(employee1Class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(employee1Class, "example2")));

        //==================================================================================
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "example2")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "example3")));

        suite.addTest(new ReadObjectTest(manager.getObject(clientClass, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(clientClass, "example2")));
        suite.addTest(new ReadObjectTest(manager.getObject(clientClass, "example3")));

        suite.addTest(new ReadObjectTest(manager.getObject(evaluationClientClass, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(evaluationClientClass, "example2")));
        suite.addTest(new ReadObjectTest(manager.getObject(evaluationClientClass, "example3")));

        //aggregate collection read
        suite.addTest(new ReadObjectTest(manager.getObject(Agent.class, "example1")));
        if(useNewAggregateCollection) {
            suite.addTest(new ReadObjectTest(manager.getObject(Builder.class, "example1")));
        }
        return suite;
    }

    public static TestSuite getUnitOfWorkCommitResumeTestSuite() {
        TestSuite suite = getSRGUnitOfWorkCommitResumeTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGUnitOfWorkCommitResumeTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("AggregateUnitOfWorkCommitResumeTestSuite");
        suite.setDescription("This suite tests updating objects with UOW in the aggregate model using the commitAndResume method.");

        Class employeeClass = Employee.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new BatchReadingWithAggregateCollectionMapping(Agent.class));
        if(useNewAggregateCollection) { 
            suite.addTest(new BatchReadingWithAggregateCollectionMapping(Builder.class));
        }
        suite.addTest(new UnitOfWorkCommitResumeTest(manager.getObject(employeeClass, "example1")));
        suite.addTest(new UnitOfWorkCommitResumeTest(manager.getObject(employeeClass, "example2")));
        suite.addTest(new UnitOfWorkCommitResumeTest(manager.getObject(employeeClass, "example3")));

        return suite;
    }

    public static TestSuite getUnitOfWorkTestSuite() {
        TestSuite suite = getSRGUnitOfWorkTestSuite();
        // Add new tests here...

        // bug 3443738
        suite.addTest(new AggregateVersionOpimisticLockingTest());

        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGUnitOfWorkTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("AggregateUnitOfWorkTestSuite");
        suite.setDescription("This suite tests updating objects with UOW in the aggregate model.");

        Class employeeClass = Employee.class;
        Class clientClass = Client.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new UnitOfWorkTest(manager.getObject(employeeClass, "example1")));
        suite.addTest(new UnitOfWorkTest(manager.getObject(employeeClass, "example2")));
        suite.addTest(new UnitOfWorkTest(manager.getObject(employeeClass, "example3")));

        /*    suite.addTest(new DeletionUnitOfWorkTest(manager.getObject(clientClass, "example1")));
            suite.addTest(new DeleteObjectTest(manager.getObject(clientClass, "example2")));
            suite.addTest(new DeleteObjectTest(manager.getObject(clientClass, "example3"))); */
        //aggregate collection UoW test
        suite.addTest(new AggregateCollectionUoWTest(manager.getObject(Agent.class, "example1")));
        suite.addTest(new AggregateCollectionMultipleUoWTest(manager.getObject(Agent.class, "example1")));

        //aggregate with transformation mapping test
        suite.addTest(new TransformationMappingTest());

        //For CR#2285, handle adding nulls to a collection.
        suite.addTest(new AddNullToAggregateCollectionTest((Agent)manager.getObject(Agent.class, "example1")));

        if(useNewAggregateCollection) {
            suite.addTest(new AggregateCollectionUoWTest(manager.getObject(Builder.class, "example1")));
            suite.addTest(new AggregateCollectionMultipleUoWTest(manager.getObject(Builder.class, "example1")));
            suite.addTest(new AddNullToAggregateCollectionTest((Builder)manager.getObject(Builder.class, "example1")));
        }

        return suite;
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = getSRGUpdateObjectTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("AggregateUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the aggregate model.");

        Class employee1Class = Employee1.class;
        Class employeeClass = Employee.class;
        Class clientClass = Client.class;
        Class evaluationClientClass = EvaluationClient.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        //=================================================================================
        suite.addTest(new WriteObjectTest(manager.getObject(employee1Class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employee1Class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employee1Class, "example2")));

        //=================================================================================
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "example2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "example3")));

        suite.addTest(new WriteObjectTest(manager.getObject(clientClass, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(clientClass, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(clientClass, "example2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(clientClass, "example3")));

        suite.addTest(new WriteObjectTest(manager.getObject(evaluationClientClass, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(evaluationClientClass, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(evaluationClientClass, "example2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(evaluationClientClass, "example3")));

        //update testing on aggregate collection mapping
        suite.addTest(new WriteObjectTest(manager.getObject(Agent.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Agent.class, "example1")));
        if(useNewAggregateCollection) {
            suite.addTest(new WriteObjectTest(manager.getObject(Builder.class, "example1")));
            suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Builder.class, "example1")));
        }

        return suite;
    }
}
