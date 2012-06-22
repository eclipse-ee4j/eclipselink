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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.mapping.*;

/**
 * This model tests reading/writing/deleting through using the complex mapping model.
 */
public class MappingTestModel extends TestModel {
    public MappingTestModel() {
        setDescription("This model tests reading/writing/deleting of the complex mapping model.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new MappingSystem());
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
        addTest(getUnitOfWorkTestSuite());
        addTest(getPublic1MTestSuite());
        addTest(getPrivateMMTestSuite());
        addTest(getTransformationMappingTestSuite());
        addTest(getUnitOfWorkCommitResumeTestSuite());
        addTest(getBidirectionalUnitOfWorkTestSuite());
        addTest(getAdditionalJoinTest());
        //CR3922  Test buildSelectionCriteria in one-to-one and one-to-many mapping
        addTest(getBuildSelectionCriteriaTestSuite());
        addTest(getSameNameMappingTestSuite());
    }

    public static TestSuite getAdditionalJoinTest() {
        TestSuite suite = new TestSuite();
        suite.setName("AdditionalJoinTestSuite");
        suite.setDescription("This suite tests the addition of an additionalJoinExpression.");
        suite.addTest(new AdditionalJoinExpressionTest());

        PopulationManager manager = PopulationManager.getDefaultManager();
        suite.addTest(new MultipleTableReadObjectTest(manager.getObject(Employee1.class, "example1"), 
                                                      "amendEmployee1WithJoinWithInsert"));
        suite.addTest(new MultipleTableReadObjectTest(manager.getObject(Employee1.class, "example1"), 
                                                      "amendEmployee1WithFKInfo"));
        suite.addTest(new MultipleTableReadObjectTest(manager.getObject(Employee1.class, "example1"), 
                                                      "amendEmployee1WithJoinOnly"));

        suite.addTest(new MultipleTableInsertObjectTest(manager.getObject(Employee1.class, "example2"), 
                                                        "amendEmployee1WithJoinWithInsert"));
        suite.addTest(new MultipleTableInsertObjectTest(manager.getObject(Employee1.class, "example2"), 
                                                        "amendEmployee1WithFKInfo"));

        suite.addTest(new MultipleTableReadAllTest(Employee1.class, 2, "amendEmployee1WithJoinWithInsert"));
        suite.addTest(new MultipleTableReadAllTest(Employee1.class, 2, "amendEmployee1WithJoinOnly"));
        suite.addTest(new MultipleTableReadAllTest(Employee1.class, 2, "amendEmployee1WithFKInfo"));

        // The delete with join info would not work because of the lack of PK info.	
        suite.addTest(new MultipleTableDeleteObjectTest(manager.getObject(Employee1.class, "example3"), 
                                                        "amendEmployee1WithFKInfo"));

        // The update with join info would not work because of the lack of PK info.	
        suite.addTest(new MultipleTableWriteObjectTest(manager.getObject(Employee1.class, "example3"), 
                                                       "amendEmployee1WithFKInfo"));

        // Add tests for multiple table with fk from secondary table to primary table.
        suite.addTest(new ReadObjectTest(manager.getObject(Employee2.class, "example1")));
        suite.addTest(new WriteObjectTest(manager.getObject(Employee2.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Employee2.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Employee2.class, "example1")));
        suite.addTest(new InsertObjectTest(manager.getObject(Employee2.class, "example2")));

        return suite;
    }

    public static TestSuite getBidirectionalUnitOfWorkTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("BidirectionalUnitOfWorkTestSuite");
        suite.setDescription("This suite tests bidirection in a unit of work.");

        suite.addTest(new BidirectionalUOWInsertTest());
        suite.addTest(new BidirectionalUOWInsertAndDeleteTest());

        return suite;
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("MappingDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the mapping model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new DeleteObjectTest(manager.getObject(Employee.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Employee.class, "example2")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Employee.class, "example3")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Computer.class, "example2")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Shipment.class, "example2")));
        suite.addTest(new MTMPrivateOwnedWithValueholderDeleteObjectTest());
        suite.addTest(new DirectCollectionMappingDeleteNullValueTest());

        return suite;
    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("MappingInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the mapping model.");

        suite.addTest(new InsertObjectTest(Employee.example7()));
        suite.addTest(new InsertObjectTest(Employee.example8()));
        suite.addTest(new InsertObjectTest(Employee.example9()));
        suite.addTest(new InsertObjectTest(Shipment.example1()));
        suite.addTest(new ObjectTypeMappingDefaultNullValues());
        suite.addTest(new MTMPrivateOwnedWithValueholderDeleteObjectTest());

        return suite;
    }

    public static TestSuite getPrivateMMTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("MappingPrivateMMTestSuite");
        suite.setDescription("This suite tests deleting an object, and verifies that private parts are deleted as well.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new PrivateMMTest(manager.getObject(Employee.class, "example1")));
        suite.addTest(new PrivateMMTest(manager.getObject(Employee.class, "example2")));
        suite.addTest(new PrivateMMTest(manager.getObject(Employee.class, "example3")));

        return suite;
    }

    public static TestSuite getPublic1MTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("MappingPublic1MTestSuite");
        suite.setDescription("This suite tests deleting an object, and verifies that public parts are not deleted as well.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new Public1MTest(manager.getObject(Employee.class, "example1")));
        suite.addTest(new Public1MTest(manager.getObject(Employee.class, "example2")));
        suite.addTest(new Public1MTest(manager.getObject(Employee.class, "example3")));

        return suite;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("MappingReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the mapping model.");

        suite.addTest(new ReadAllTest(Employee.class, 6));
        suite.addTest(new ReadAllTest(Shipment.class, 8));
        suite.addTest(new ReadAllTest(Computer.class, 6));
        // EL Bug 375463
        suite.addTest(new ObjectTypeMappingBooleanToCharTest());

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("MappingReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the mapping model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(Employee.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Employee.class, "example2")));
        suite.addTest(new ReadObjectTest(manager.getObject(Employee.class, "example3")));
        suite.addTest(new ReadObjectTest(manager.getObject(Shipment.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Computer.class, "example2")));
        suite.addTest(new TwoLevelJoinedAttributeTest());
        suite.addTest(new LoopingJoinedAttributeTest());
        suite.addTest(new OneToOnePKTest());

        return suite;
    }

    public static TestSuite getSameNameMappingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("SameNamePrimaryKeyTestSuite");
        suite.setDescription("This suite test working with objects that have two related objects with attributes with the same name..");

        suite.addTest(new SameNamePKTest());

        return suite;
    }

    public static TestSuite getTransformationMappingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("MappingTransformationTestSuite");
        suite.setDescription("This suite tests the reading of objects where the transformation mapping uses two parameters in the accessor method");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new TwoParametersTransformationMappingTest(manager.getObject(Address.class, "example1")));
        suite.addTest(new TwoParametersTransformationMappingTest(manager.getObject(Address.class, "example2")));
        return suite;
    }

    public static TestSuite getUnitOfWorkCommitResumeTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("UnitOfWorkCommitResumeTestSuite");
        suite.setDescription("This suite tests the unit of work commitResume feature within the mapping model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new UnitOfWorkCommitResumeTest(manager.getObject(Employee.class, "example2")));

        return suite;
    }

    public static TestSuite getUnitOfWorkTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("UnitOfWorkTestSuite");
        suite.setDescription("This suite tests the unit of work feature within the mapping model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new UnitOfWorkTest(manager.getObject(Employee.class, "example2")));
        suite.addTest(new ConstraintOrderTest());

        return suite;
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("MappingUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the mapping model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(Employee.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Employee.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Employee.class, "example2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Employee.class, "example3")));
        suite.addTest(new WriteObjectTest(manager.getObject(Shipment.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Shipment.class, "example1")));
        suite.addTest(new AddObjectNonPrimaryKeyManyToManyTest());

        return suite;
    }

    //CR3922  Test buildSelectionCriteria in one-to-one and one-to-many mapping

    public static TestSuite getBuildSelectionCriteriaTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("BuildSelectionCriteriaTestSuite");
        suite.setDescription("This suite tests buildSelectionCriteria in one-to-one and one-to-many mapping.");

        suite.addTest(new SelectionCriteriaInOneToOneTest());
        suite.addTest(new SelectionCriteriaInOneToManyTest());
        suite.addTest(new SelectionCriteriaInTargetOneToOneTest());

        return suite;
    }
}
