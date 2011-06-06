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
package org.eclipse.persistence.testing.tests.eis.xmlfile;

import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * This model tests reading/writing/deleting through using the employee demo.
 */
public class EmployeeBasicTestModel extends TestModel {
    public EmployeeBasicTestModel() {
        setDescription("This model tests reading/writing/deleting JCA XFA using the employee demo.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeSystem());
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getReadAllTestSuite());
        //addTest(getUnitOfWorkTestSuite()); - need dynamic readObject to work first
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the employee demo.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new DeleteObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new DeleteObjectTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new DeleteObjectTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new DeleteObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new DeleteObjectTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new DeleteObjectTest(manager.getObject(largeProjectClass, "0003")));

        return suite;
    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the employee demo.");
        EmployeePopulator system = new EmployeePopulator();

        suite.addTest(new InsertObjectTest(system.basicEmployeeExample1()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample2()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample3()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample4()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample5()));

        suite.addTest(new InsertObjectTest(system.basicSmallProjectExample1()));
        suite.addTest(new InsertObjectTest(system.basicSmallProjectExample2()));
        suite.addTest(new InsertObjectTest(system.basicSmallProjectExample3()));

        suite.addTest(new InsertObjectTest(system.basicLargeProjectExample1()));
        suite.addTest(new InsertObjectTest(system.basicLargeProjectExample2()));
        suite.addTest(new InsertObjectTest(system.basicLargeProjectExample3()));

        return suite;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the employee demo.");

        suite.addTest(new ReadAllTest(Employee.class, 12));
        suite.addTest(new ReadAllTest(Project.class, 15));
        suite.addTest(new ReadAllTest(LargeProject.class, 5));
        suite.addTest(new ReadAllTest(SmallProject.class, 10));

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the employee demo.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0003")));

        return suite;
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the employee demo.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new WriteObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new WriteObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(largeProjectClass, "0003")));

        return suite;
    }

    /**
     * Add a subset of the UnitOfWork tests in order to test the actual test flag functionality.
     *
    public static TestSuite getUnitOfWorkTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Unit Of Work Update Test Suite");
        suite.setDescription("This suite tests change flags for updates using UnitOfWork");
        PopulationManager manager = PopulationManager.getDefaultManager();
        Employee employee = (Employee)manager.getObject(Employee.class, "0001");

        // Tests with using unit of work.
        ComplexUpdateTest test = new UpdateToNullTest(employee);
        test.usesUnitOfWork = true;
        suite.addTest(test);

        test = new UpdateChangeValueTest(employee);
        test.usesUnitOfWork = true;
        suite.addTest(test);

        test = new UpdateChangeNothingTest(employee);
        test.usesUnitOfWork = true;
        suite.addTest(test);

        test = new UpdateChangeObjectTest(employee);
        test.usesUnitOfWork = true;
        suite.addTest(test);

        suite.addTest(new NestedUnitOfWorkTest(employee));
        suite.addTest(new NestedUnitOfWorkMultipleCommitTest(employee));
        suite.addTest(new DeepNestedUnitOfWorkTest(employee));
        suite.addTest(new MultipleUnitOfWorkTest(employee));
        suite.addTest(new UnitOfWorkResumeTest(employee));
        suite.addTest(new UnitOfWorkResumeOnFailureTest(employee));
        suite.addTest(new UnitOfWorkCommitResumeOnFailureNoFailureTest(employee));
        suite.addTest(new UnitOfWorkCommitAndResume(employee));

        return suite;
    }*/
}
