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
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangeTrackingPolicy;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.testing.framework.InsertObjectTest;
import org.eclipse.persistence.testing.framework.ReadAllCallTest;
import org.eclipse.persistence.testing.framework.ReadAllTest;
import org.eclipse.persistence.testing.framework.ReadObjectCallTest;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.collections.CollectionsSystem;
import org.eclipse.persistence.testing.models.collections.Restaurant;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;
import org.eclipse.persistence.testing.tests.employee.EmployeeDeleteTest;
import org.eclipse.persistence.testing.tests.employee.ProjectDeleteTest;
import org.eclipse.persistence.testing.tests.unitofwork.DeepMergeCloneSerializedObjectReferenceChangesTest;
import org.eclipse.persistence.testing.tests.unitofwork.DeepMergeCloneSerializedTest;
import org.eclipse.persistence.testing.tests.unitofwork.DeepNestedUnitOfWorkTest;
import org.eclipse.persistence.testing.tests.unitofwork.MultipleUnitOfWorkTest;
import org.eclipse.persistence.testing.tests.unitofwork.NestedUnitOfWorkMultipleCommitTest;
import org.eclipse.persistence.testing.tests.unitofwork.NestedUnitOfWorkTest;
import org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkCommitAndResume;
import org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkCommitResumeOnFailureNoFailureTest;
import org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkResumeOnFailureTest;
import org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkResumeTest;
import org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkRevertTest;
import org.eclipse.persistence.testing.tests.writing.ComplexUpdateTest;
import org.eclipse.persistence.testing.tests.writing.UpdateChangeNothingTest;
import org.eclipse.persistence.testing.tests.writing.UpdateChangeObjectTest;
import org.eclipse.persistence.testing.tests.writing.UpdateChangeValueTest;
import org.eclipse.persistence.testing.tests.writing.UpdateToNullTest;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;


/**
 * This model tests reading/writing/deleting through using the employee demo.
 * This model is set up to test the use of ObjectLevelChangeTracking policy.  It uses
 * the employee demo test framework to ensure everything works as it did before when
 * the new change policy is used.  It also makes use of several of the tests from the UnitOfWork
 * model to ensure that the actual updates function correctly.
 * @author Tom Ware
 */
public class EmployeeChangeFlagBasicTestModel extends EmployeeBasicTestModel {

    protected ObjectChangePolicy employeeChangePolicy;
    protected ObjectChangePolicy addressChangePolicy;
    protected ObjectChangePolicy projectChangePolicy;
    protected ObjectChangePolicy smallProjectChangePolicy;
    protected ObjectChangePolicy largeProjectChangePolicy;
    protected ObjectChangePolicy employmentPeriodChangePolicy;
    protected ObjectChangePolicy phoneNumberChangePolicy;

    protected ObjectChangePolicy restaurantChangePolicy;

    public EmployeeChangeFlagBasicTestModel() {
        setDescription("This model tests reading/writing/deleting using the employee demo with ObjectChangeTrackingPolicy flag.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeSystem());
        addRequiredSystem(new CollectionsSystem());
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getUnitOfWorkTestSuite());
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the employee demo.");

        Class employeeClass = org.eclipse.persistence.testing.models.employee.domain.Employee.class;
        Class largeProjectClass = org.eclipse.persistence.testing.models.employee.domain.LargeProject.class;
        Class smallProjectClass = org.eclipse.persistence.testing.models.employee.domain.SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new ProjectDeleteTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new ProjectDeleteTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(largeProjectClass, "0003")));

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

        suite.addTest(new ReadAllCallTest(Employee.class, 12, 
                                          new SQLCall("SELECT  t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER, t0.END_DATE, t0.START_DATE, t0.MANAGER_ID, t0.END_TIME, t0.START_TIME, t0.ADDR_ID FROM EMPLOYEE t0, SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID")));

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the employee demo.");

        Class employeeClass = org.eclipse.persistence.testing.models.employee.domain.Employee.class;
        Class largeProjectClass = org.eclipse.persistence.testing.models.employee.domain.LargeProject.class;
        Class smallProjectClass = org.eclipse.persistence.testing.models.employee.domain.SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0005")));

        Employee employee = (Employee)manager.getObject(employeeClass, "0001");

        suite.addTest(new ReadObjectCallTest(employeeClass, 
                                             new SQLCall("SELECT t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER, t0.END_DATE, t0.START_DATE, t0.MANAGER_ID, t0.END_TIME, t0.START_TIME, t0.ADDR_ID FROM EMPLOYEE t0, SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID AND t0.F_NAME = '" + 
                                                         employee.getFirstName() + "' AND t0.L_NAME = '" + 
                                                         employee.getLastName() + "'")));
        employee = (Employee)manager.getObject(employeeClass, "0002");
        suite.addTest(new ReadObjectCallTest(employeeClass, 
                                             new SQLCall("SELECT  t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER, t0.END_DATE, t0.START_DATE, t0.MANAGER_ID, t0.END_TIME, t0.START_TIME, t0.ADDR_ID FROM EMPLOYEE t0, SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID AND t0.F_NAME = '" + 
                                                         employee.getFirstName() + "' AND t0.L_NAME = '" + 
                                                         employee.getLastName() + "'")));
        employee = (Employee)manager.getObject(employeeClass, "0003");
        suite.addTest(new ReadObjectCallTest(employeeClass, 
                                             new SQLCall("SELECT  t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER, t0.END_DATE, t0.START_DATE, t0.MANAGER_ID, t0.END_TIME, t0.START_TIME, t0.ADDR_ID FROM EMPLOYEE t0, SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID AND t0.F_NAME = '" + 
                                                         employee.getFirstName() + "' AND t0.L_NAME = '" + 
                                                         employee.getLastName() + "'")));


        org.eclipse.persistence.testing.models.employee.domain.Project project = 
            (org.eclipse.persistence.testing.models.employee.domain.Project)manager.getObject(largeProjectClass, "0001");
        ReadObjectTest test = new ReadObjectTest(project);
        test.setQuery(new org.eclipse.persistence.queries.ReadObjectQuery(org.eclipse.persistence.testing.models.employee.domain.Project.class, 
                                                                        new org.eclipse.persistence.expressions.ExpressionBuilder().get("id").equal(project.getId())));
        suite.addTest(test);

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

        Class employeeClass = org.eclipse.persistence.testing.models.employee.domain.Employee.class;
        Class largeProjectClass = org.eclipse.persistence.testing.models.employee.domain.LargeProject.class;
        Class smallProjectClass = org.eclipse.persistence.testing.models.employee.domain.SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new WriteObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new WriteObjectTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new WriteObjectTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new WriteObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new WriteObjectTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new WriteObjectTest(manager.getObject(largeProjectClass, "0003")));

        return suite;
    }

    /**
     * Add a subset of the UnitOfWork tests in order to test the actual test flag functionality.
     */
    public TestSuite getUnitOfWorkTestSuite() {
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
        suite.addTest(new UnitOfWorkRevertTest(employee));
        suite.addTest(new ChangeFlagTest());
        suite.addTest(new ChangeEventTest());

        suite.addTest(new DeepMergeCloneSerializedTest());
        suite.addTest(new DeepMergeCloneSerializedObjectReferenceChangesTest());

        return suite;
    }

    public void setup() {
        // Save change policies for the all employee demo class in order to restore them at reset time.
        employeeChangePolicy = getSession().getDescriptor(Employee.class).getObjectChangePolicy();
        getSession().getDescriptor(Employee.class).setObjectChangePolicy(new ObjectChangeTrackingPolicy());

        addressChangePolicy = getSession().getDescriptor(Address.class).getObjectChangePolicy();
        getSession().getDescriptor(Address.class).setObjectChangePolicy(new ObjectChangeTrackingPolicy());
        ;

        projectChangePolicy = getSession().getDescriptor(Project.class).getObjectChangePolicy();
        getSession().getDescriptor(Project.class).setObjectChangePolicy(new ObjectChangeTrackingPolicy());
        ;

        smallProjectChangePolicy = getSession().getDescriptor(SmallProject.class).getObjectChangePolicy();
        getSession().getDescriptor(SmallProject.class).setObjectChangePolicy(new ObjectChangeTrackingPolicy());
        ;

        largeProjectChangePolicy = getSession().getDescriptor(LargeProject.class).getObjectChangePolicy();
        getSession().getDescriptor(LargeProject.class).setObjectChangePolicy(new ObjectChangeTrackingPolicy());
        ;

        employmentPeriodChangePolicy = getSession().getDescriptor(EmploymentPeriod.class).getObjectChangePolicy();
        getSession().getDescriptor(EmploymentPeriod.class).setObjectChangePolicy(new ObjectChangeTrackingPolicy());
        getSession().getDescriptor(Employee.class).getMappingForAttributeName("period").getReferenceDescriptor().setObjectChangePolicy(new ObjectChangeTrackingPolicy());

        phoneNumberChangePolicy = getSession().getDescriptor(PhoneNumber.class).getObjectChangePolicy();
        getSession().getDescriptor(PhoneNumber.class).setObjectChangePolicy(new ObjectChangeTrackingPolicy());
    }

    public void reset() {
        // restore old change policies.
        getSession().getDescriptor(Employee.class).setObjectChangePolicy(employeeChangePolicy);
        getSession().getDescriptor(Address.class).setObjectChangePolicy(addressChangePolicy);
        getSession().getDescriptor(Project.class).setObjectChangePolicy(projectChangePolicy);
        getSession().getDescriptor(SmallProject.class).setObjectChangePolicy(smallProjectChangePolicy);
        getSession().getDescriptor(LargeProject.class).setObjectChangePolicy(largeProjectChangePolicy);
        getSession().getDescriptor(EmploymentPeriod.class).setObjectChangePolicy(employmentPeriodChangePolicy);
        getSession().getDescriptor(Employee.class).getMappingForAttributeName("period").getReferenceDescriptor().setObjectChangePolicy(employmentPeriodChangePolicy);
        getSession().getDescriptor(PhoneNumber.class).setObjectChangePolicy(phoneNumberChangePolicy);
        getSession().getDescriptor(Restaurant.class).setObjectChangePolicy(restaurantChangePolicy);
    }

}
