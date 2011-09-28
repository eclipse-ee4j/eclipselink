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
package org.eclipse.persistence.testing.tests.inheritance;

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.models.inheritance.*;
import org.eclipse.persistence.testing.tests.writing.ComplexDeleteTest;

/**
 * This test model tests basic CRUD operations with complex inheritance models.
 * It also tests some complex querying and unit of work inheritance use cases.
 */
public class InheritanceTestModel extends TestModel {
    public InheritanceTestModel() {
    }

    public InheritanceTestModel(boolean isSRG) {
        this();
        this.isSRG = isSRG;
    }

    public void addRequiredSystems() {
        addRequiredSystem(new InheritanceSystem());
    }

    public void addTests() {
        addTest(getDuplicateFieldTestSuite());
        addTest(getReadObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
        addTest(getUnitOfWorkTestSuite());
        addTest(getUnitOfWorkCommitResumeTestSuite());
        addTest(getDeepInheritanceTestSuite());
        addTest(getTranslatedKeyInheritanceTestSuite());
        addTest(getSingleInheritanceTestSuite());
        addTest(new CursoredStreamInheritanceTestCase());
        addTest(new ReinitializeJoiningOnClassDescriptorWithInheritanceTest());
        addTest(new UnitOfWorkIdentityMapAccessorTest());
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public void addSRGTests() {
        addTest(getSRGDuplicateFieldTestSuite());
        addTest(getSRGReadObjectTestSuite());
        addTest(getSRGReadAllTestSuite());
        addTest(getSRGDeleteObjectTestSuite());
        addTest(getSRGInsertObjectTestSuite());
        addTest(getSRGUpdateObjectTestSuite());
        addTest(getSRGUnitOfWorkTestSuite());
        addTest(getSRGUnitOfWorkCommitResumeTestSuite());
        addTest(getSRGDeepInheritanceTestSuite());
        addTest(getSRGTranslatedKeyInheritanceTestSuite());
        addTest(getSRGSingleInheritanceTestSuite());
    }

    public static TestSuite getDeepInheritanceTestSuite() {
        TestSuite suite = getSRGDeepInheritanceTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGDeepInheritanceTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Deep Inheritance Tests");
        suite.addTest(new LeafQueryTest());
        suite.addTest(new SecondaryTableUpdateTest());
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
        suite.setName("InheritanceDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the inheritance model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new DeleteObjectTest(manager.getObject(Cat.class, "catExample1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Dog.class, "dogExample1")));

        suite.addTest(new DeleteObjectTest(manager.getObject(Cat.class, "catExample2")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Dog.class, "dogExample2")));

        suite.addTest(new DeleteObjectTest(manager.getObject(Cat.class, "catExample3")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Dog.class, "dogExample3")));

        suite.addTest(new DeleteObjectTest(manager.getObject(Person.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(SalesRep.class, "example3")));

        suite.addTest(new DeleteObjectTest(manager.getObject(Company.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Company.class, "example2")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Company.class, "example3")));

        suite.addTest(new DeleteObjectTest(manager.getObject(Mac.class, "example1")));

        suite.addTest(new DeleteObjectTest(manager.getObject(IBMPC.class, "example2")));
        suite.addTest(new DeleteObjectTest(manager.getObject(IBMPC.class, "example3")));
        suite.addTest(new DeleteObjectTest(manager.getObject(PC.class, "example4")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Mainframe.class, "example5")));

        Vector dependants = new Vector(1);
        Alligator alligator = (Alligator)manager.getObject(Alligator.class, "example1");
        dependants.add(alligator.getLatestVictim());
        suite.addTest(new ComplexDeleteTest(alligator, dependants));
        return suite;
    }

    public static TestSuite getDuplicateFieldTestSuite() {
        TestSuite suite = getSRGDuplicateFieldTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGDuplicateFieldTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("InheritanceDuplicateFieldTestSuite");
        suite.setDescription("This suite tests ther read from the root class generated duplicate filed in SQL in the inheritance model.");

        //duplicate field SQL testing read
        ReadAllTest rs = new ReadAllTest(A_King2.class, 5);
        ReadAllQuery rq = new ReadAllQuery(A_King2.class);
        rq.addAscendingOrdering("foo");
        rs.setQuery(rq);
        suite.addTest(rs);
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
        suite.setName("InheritanceInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the inheritance model.");

        suite.addTest(new InsertObjectTest(Cat.example4()));
        suite.addTest(new InsertObjectTest(Dog.example4()));

        suite.addTest(new InsertObjectTest(Company.example1()));
        suite.addTest(new InsertObjectTest(Company.example2()));
        suite.addTest(new InsertObjectTest(Company.example3()));

        suite.addTest(new InsertObjectTest(Person.example1()));
        suite.addTest(new InsertObjectTest(Person.example3()));
        suite.addTest(new InsertObjectTest(Person.example6()));

        suite.addTest(new InsertObjectTest(Car.example1()));
        suite.addTest(new InsertObjectTest(Car.example2()));
        suite.addTest(new InsertObjectTest(Car.example3()));
        suite.addTest(new InsertObjectTest(Car.example4()));

        suite.addTest(new InsertObjectTest(Computer.example1()));
        suite.addTest(new InsertObjectTest(Computer.example2()));
        suite.addTest(new InsertObjectTest(Computer.example3()));
        suite.addTest(new InsertObjectTest(Computer.example4()));
        suite.addTest(new InsertObjectTest(Computer.example5()));

        //insert using native sequencing in the branch class Aug.18, 1999
        suite.addTest(new InsertObjectTest(Developer_King.exp2()));
        suite.addTest(new InsertObjectTest(Developer_King.exp3()));
        suite.addTest(new InsertObjectTest(SeniorDeveloper_King.exp4()));

        //testing subclass overriding of an inherited mapping.
        suite.addTest(new OverrideInheritedMappingTest());
        // Bug 2996585
        suite.addTest(new BindingWithShallowInsertTest());

        return suite;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = getSRGReadAllTestSuite();

        suite.addTest(new QueryInheritanceTest());
        suite.addTest(new JoinWithSecondaryTableTest());
        
        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("InheritanceReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the inheritance model.");

        suite.addTest(new ReadAllTest(Cat.class, 3));
        suite.addTest(new ReadAllTest(Dog.class, 3));

        suite.addTest(new ReadAllTest(Company.class, 3));
        suite.addTest(new ReadAllTest(Person.class, 1));
        suite.addTest(new ReadAllTest(SalesRep.class, 2));
        suite.addTest(new ReadAllTest(Engineer.class, 2));
        suite.addTest(new ReadAllTest(SoftwareEngineer.class, 1));

        suite.addTest(new ReadAllTest(Computer.class, 5));
        suite.addTest(new ReadAllTest(PC.class, 4));
        suite.addTest(new ReadAllTest(IBMPC.class, 2));
        suite.addTest(new ReadAllTest(Mac.class, 1));
        suite.addTest(new ReadAllTest(Mainframe.class, 1));

        suite.addTest(new ReadAllTest(Car.class, 4));
        suite.addTest(new ReadAllTest(SportsCar.class, 1));
        suite.addTest(new ReadAllTest(Bus.class, 4));
        suite.addTest(new ReadAllTest(Bicycle.class, 3));
        suite.addTest(new ReadAllTest(Boat.class, 4));
        suite.addTest(new ReadAllTest(Vehicle.class, 19));
        suite.addTest(new ReadAllTest(NonFueledVehicle.class, 10));
        suite.addTest(new ReadAllTest(FueledVehicle.class, 1));

        suite.addTest(new ReadAllTest(A_King2.class, 5));

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = getSRGReadObjectTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("InheritanceReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the inheritance model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(Cat.class, "catExample1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Dog.class, "dogExample1")));

        suite.addTest(new ReadObjectTest(manager.getObject(Cat.class, "catExample2")));
        suite.addTest(new ReadObjectTest(manager.getObject(Dog.class, "dogExample2")));

        suite.addTest(new ReadObjectTest(manager.getObject(Cat.class, "catExample3")));
        suite.addTest(new ReadObjectTest(manager.getObject(Dog.class, "dogExample3")));

        suite.addTest(new ReadObjectTest(manager.getObject(Company.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Company.class, "example2")));
        suite.addTest(new ReadObjectTest(manager.getObject(Company.class, "example3")));

        Bus bus = (Bus)manager.getObject(Bus.class, "example1");
        ReadObjectTest test = new ReadObjectTest(bus);
        test.setQuery(new ReadObjectQuery(Vehicle.class, new ExpressionBuilder().get("id").equal(bus.id)));
        suite.addTest(test);

        suite.addTest(new ReadObjectTest(manager.getObject(Person.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Engineer.class, "example2")));
        suite.addTest(new ReadObjectTest(manager.getObject(SalesRep.class, "example4")));
        suite.addTest(new ReadObjectTest(manager.getObject(SoftwareEngineer.class, "example5")));
        suite.addTest(new ReadObjectTest(manager.getObject(SalesRep.class, "example3")));

        suite.addTest(new ReadObjectTest(manager.getObject(PC.class, "example4")));
        suite.addTest(new ReadObjectTest(manager.getObject(IBMPC.class, "example3")));
        suite.addTest(new ReadObjectTest(manager.getObject(IBMPC.class, "example2")));
        suite.addTest(new ReadObjectTest(manager.getObject(Mac.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Mainframe.class, "example5")));

        return suite;
    }

    public static TestSuite getTranslatedKeyInheritanceTestSuite() {
        TestSuite suite = getSRGTranslatedKeyInheritanceTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGTranslatedKeyInheritanceTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("TranslatedKeyInheritance");
        suite.setDescription("Test when a subclass has a key different from the superclass");
        suite.addTest(new TranslatedKeyInheritanceTestCase());
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
        suite.setName("InheritanceUnitOfWorkCommitResumeTestSuite");
        suite.setDescription("This suite tests updating objects with UOW in the inheritance model using the commitAndResume method.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new UnitOfWorkCommitResumeTest(manager.getObject(Company.class, "example1")));
        suite.addTest(new UnitOfWorkCommitResumeTest(manager.getObject(Company.class, "example2")));
        suite.addTest(new UnitOfWorkCommitResumeTest(manager.getObject(Company.class, "example3")));

        return suite;
    }

    public static TestSuite getUnitOfWorkTestSuite() {
        TestSuite suite = getSRGUnitOfWorkTestSuite();
        suite.addTest(new InfiniteRecursionWithNoDescriptorTest());
        // Add new tests here...
        return suite;
        
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGUnitOfWorkTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("InheritanceUnitOfWorkTestSuite");
        suite.setDescription("This suite tests updating objects with UOW in the inheritance model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new UnitOfWorkTest(manager.getObject(Company.class, "example1")));
        suite.addTest(new UnitOfWorkTest(manager.getObject(Company.class, "example2")));
        suite.addTest(new UnitOfWorkTest(manager.getObject(Company.class, "example3")));

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
        suite.setName("InheritanceUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the inheritance model.");

        Class companyClass = Company.class;
        Class personClass = Person.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(Cat.class, "catExample1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Cat.class, "catExample1")));
        suite.addTest(new WriteObjectTest(manager.getObject(Dog.class, "dogExample1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Dog.class, "dogExample1")));

        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Cat.class, "catExample2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Dog.class, "dogExample2")));

        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Cat.class, "catExample3")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Dog.class, "dogExample3")));

        suite.addTest(new WriteObjectTest(manager.getObject(companyClass, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(companyClass, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(companyClass, "example2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(companyClass, "example3")));

        suite.addTest(new WriteObjectTest(manager.getObject(personClass, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(personClass, "example1")));
        suite.addTest(new WriteObjectTest(manager.getObject(Engineer.class, "example2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Engineer.class, "example2")));
        suite.addTest(new WriteObjectTest(manager.getObject(SalesRep.class, "example3")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(SalesRep.class, "example3")));

        suite.addTest(new WriteObjectTest(manager.getObject(Mac.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Mac.class, "example1")));
        suite.addTest(new WriteObjectTest(manager.getObject(IBMPC.class, "example2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(IBMPC.class, "example2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(IBMPC.class, "example3")));
        suite.addTest(new WriteObjectTest(manager.getObject(PC.class, "example4")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(PC.class, "example4")));
        suite.addTest(new WriteObjectTest(manager.getObject(Mainframe.class, "example5")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Mainframe.class, "example5")));

        return suite;
    }

    public static TestSuite getSingleInheritanceTestSuite() {
        TestSuite suite = getSRGSingleInheritanceTestSuite();

        // Add new tests here...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGSingleInheritanceTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("SingleInheritanceTestSuite");
        suite.setDescription("This suite tests an inheritance model containing only a single class .");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new SingleInheritanceTest());

        return suite;
    }
}
