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
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.interfaces.*;

/**
 * This model tests interface support, where the interfaces do not have tables in the database.
 */
public class InterfaceWithoutTablesTestModel extends TestModel {
    public InterfaceWithoutTablesTestModel() {
        setDescription("This model tests interface support, where the interfaces have tables in the database");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new InterfaceWithoutTablesSystem());
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
        addTest(getVariable1To1TestSuite());
        addTest(getManyDescriptorTestSuite());
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();

        suite.setName("InterfaceWithoutTablesDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the interface model (without tables).");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new DeleteObjectTest(manager.getObject(Film.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Documentary.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Secretary.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Receptionist.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(ProductDeveloper.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(CourseDeveloper.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(ProductManager.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(PersonnelManager.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Employee.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Phone.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Email.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Company.class, "example1")));

        return suite;
    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();

        suite.setName("InserfaceWithoutTablesInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the interface model (without tables).");

        suite.addTest(new InsertObjectTest(Film.example2()));
        suite.addTest(new InsertObjectTest(Secretary.example2()));
        suite.addTest(new InsertObjectTest(Company.example3()));
        suite.addTest(new InsertObjectTest(CourseDeveloper.example1()));
        suite.addTest(new InsertObjectTest(Email.example2()));
        //suite.addTest(new InsertObjectTest(Employee.example3())); Cannot use insert test on emp as bi-1-1
        suite.addTest(new InsertObjectTest(PersonnelManager.example1()));
        suite.addTest(new InsertObjectTest(Phone.example1()));
        suite.addTest(new InsertObjectTest(ProductDeveloper.example3()));
        suite.addTest(new InsertObjectTest(ProductManager.example1()));
        suite.addTest(new InsertObjectTest(Receptionist.example1()));
        return suite;
    }

    public static TestSuite getManyDescriptorTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("InterfaceWithoutTablesManyDescriptorTestSuite");
        suite.setDescription("This suite tests the support of many descriptors. (without tables).");

        suite.addTest(new DescriptorInitTest());

        return suite;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("InterfaceWithoutTablesReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the interface model (without tables).");

        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Unionized.class);
        query.useCollectionClass(java.util.ArrayList.class);
        ReadAllTest aTest = new ReadAllTest(Unionized.class, 6);
        aTest.setQuery(query);
        suite.addTest(aTest);

        suite.addTest(new ReadAllTest(Actor.class, 4));
        suite.addTest(new ReadAllTest(Documentary.class, 1));
        suite.addTest(new ReadAllTest(Film.class, 3));
        suite.addTest(new ReadAllTest(Job.class, 18));
        suite.addTest(new ReadAllTest(ManagerialJob.class, 6));
        suite.addTest(new ReadAllTest(VIP.class, 3));
        // Used to test Cursored Streams
        ReadAllTest test = new ReadAllTest(VIP.class, 3);
        test.setQuery(new ReadAllQuery());
        test.getQuery().setReferenceClass(VIP.class);
        test.getQuery().useCursoredStream();
        suite.addTest(test);

        // Test the non-availability of batch reading
        test = new ReadAllBatchTest(Employee.class, 3);
        test.setName("Batch Read Test");
        test.setQuery(new ReadAllQuery());
        test.getQuery().setReferenceClass(Employee.class);
        test.getQuery().addBatchReadAttribute("contact");
        suite.addTest(test);

        suite.addTest(new OneToManyVariableBackBatchReadingTest());

        // Test cascading
        test = new ReadAllTest(Employee.class, 4);
        test.setQuery(new ReadAllQuery());
        test.getQuery().setReferenceClass(Employee.class);
        test.getQuery().cascadeAllParts();
        suite.addTest(test);

        suite.addTest(new ReadAllTest(Employee.class, 4));
        suite.addTest(new ReadAllConformInUowTest());
        return suite;
    }

    public static ReadObjectTest getReadInterfaceObjectTest() {
        TestSuite suite = new TestSuite();
        PopulationManager manager = PopulationManager.getDefaultManager();
        Contact contact = ((Employee)manager.getObject(Employee.class, "example1")).getContact();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("id").equal(contact.getEmp().getId());

        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(ContactHolder.class);
        query.setSelectionCriteria(expression);

        ReadObjectTest test = new ReadObjectTest(contact.getEmp());
        test.setQuery(query);

        return test;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("InterfaceWithoutTablesReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the interface model (without tables).");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(Actor.class, "example4")));
        suite.addTest(new ReadObjectTest(manager.getObject(Documentary.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Film.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Employee.class, "example2")));
        suite.addTest(new ReadObjectTest(manager.getObject(CourseDeveloper.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Email.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(PersonnelManager.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Phone.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(ProductDeveloper.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(ProductManager.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Receptionist.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Secretary.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Employee.class, "example4")));
        suite.addTest(new ReadObjectTest(manager.getObject(Employee.class, "example1")));

        suite.addTest(getReadInterfaceObjectTest());

        return suite;
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("InterfaceWithoutTablesUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the interface model (without tables).");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(Employee.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Employee.class, "example1")));
        suite.addTest(new WriteObjectTest(manager.getObject(CourseDeveloper.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(CourseDeveloper.class, "example1")));
        suite.addTest(new WriteObjectTest(manager.getObject(Email.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Email.class, "example1")));
        suite.addTest(new WriteObjectTest(manager.getObject(PersonnelManager.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(PersonnelManager.class, "example1")));
        suite.addTest(new WriteObjectTest(manager.getObject(Phone.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Phone.class, "example1")));
        suite.addTest(new WriteObjectTest(manager.getObject(ProductDeveloper.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(ProductDeveloper.class, "example1")));
        suite.addTest(new WriteObjectTest(manager.getObject(ProductManager.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(ProductManager.class, "example1")));
        suite.addTest(new WriteObjectTest(manager.getObject(Receptionist.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Receptionist.class, "example1")));
        suite.addTest(new WriteObjectTest(manager.getObject(Secretary.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Secretary.class, "example1")));
        suite.addTest(new UpdateObjectTest());
        suite.addTest(new VariableOneToOneNonPrivatelyOwnedTest());

        return suite;
    }

    public static QueryAcrossV11Test getV11QueryTest() {
        return new QueryAcrossV11Test();
    }

    public static TestSuite getVariable1To1TestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("InterfaceWithoutTablesVariable1To1TestSuite");
        suite.setDescription("This suite tests certain aspects of the Variable 1 to 1 implementation using the interface model (without tables).");

        suite.addTest(new VariableOneToOneDeepMergeCloneTest());
        suite.addTest(new VariableOneToOneShallowWriteTest());
        suite.addTest(getV11QueryTest());
        suite.addTest(new QueryAccrossV11ProxyIndirectionTest());
        suite.addTest(new PostCommitEventPrimaryKeyTest());
        suite.addTest(new VariableOneToOneUpdateTest());
        suite.addTest(new VariableOneToOneDeleteTest());
        suite.addTest(new VariableOneToOneCodeCoverageTest());
        suite.addTest(new VariableOneToOneInsertTest());
        suite.addTest(new VariableOneToOneGetTypeFieldTest());
        return suite;
    }
}
