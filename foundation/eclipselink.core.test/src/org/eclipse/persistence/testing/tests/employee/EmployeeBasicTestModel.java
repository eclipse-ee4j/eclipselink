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
package org.eclipse.persistence.testing.tests.employee;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;

/**
 * This model tests reading/writing/deleting through using the employee demo.
 */
public class EmployeeBasicTestModel extends TestModel {

    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     * Unfortunately JUnit only allows suite methods to be static,
     * so it is not possible to generically do this.
     */
    public static junit.framework.TestSuite suite() {
        return new EmployeeBasicTestModel();
    }

    public EmployeeBasicTestModel() {
        setDescription("This model tests reading/writing/deleting using the employee demo.");
    }

    public EmployeeBasicTestModel(boolean isSRG) {
        this();
        this.isSRG = isSRG;
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
        addTest(getMiscTestSuite());
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public void addSRGTests() {
        addTest(getSRGReadObjectTestSuite());
        addTest(getSRGUpdateObjectTestSuite());
        addTest(getSRGInsertObjectTestSuite());
        addTest(getSRGDeleteObjectTestSuite());
        addTest(getSRGReadAllTestSuite());
        addTest(getSRGMiscTestSuite());
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = getSRGDeleteObjectTestSuite();

        //Add new tests here ...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGDeleteObjectTestSuite() {
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
        TestSuite suite = getSRGInsertObjectTestSuite();

        //Add new tests here ...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the employee demo.");
        org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator system = new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator();

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
        TestSuite suite = getSRGReadAllTestSuite();

        //Add new tests here ...
        Expression orderBy = new ExpressionBuilder().get("firstName").ascending();
        Call call = new SQLCall("SELECT t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER, t0.END_DATE, t0.START_DATE, t0.MANAGER_ID, t0.ADDR_ID FROM EMPLOYEE t0, SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID");
        suite.addTest(new ReadAllCallWithOrderingTest(org.eclipse.persistence.testing.models.employee.domain.Employee.class, 12, call, orderBy));

        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the employee demo.");

        suite.addTest(new ReadAllTest(org.eclipse.persistence.testing.models.employee.domain.Employee.class, 12));
        suite.addTest(new ReadAllTest(org.eclipse.persistence.testing.models.employee.domain.Project.class, 15));
        suite.addTest(new ReadAllTest(org.eclipse.persistence.testing.models.employee.domain.LargeProject.class, 5));
        suite.addTest(new ReadAllTest(org.eclipse.persistence.testing.models.employee.domain.SmallProject.class, 10));

        suite.addTest(new ReadAllCallTest(org.eclipse.persistence.testing.models.employee.domain.Employee.class, 12, new SQLCall("SELECT  t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER, t0.END_DATE, t0.START_DATE, t0.MANAGER_ID, t0.ADDR_ID FROM EMPLOYEE t0, SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID")));

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = getSRGReadObjectTestSuite();

        //Add new tests here ...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGReadObjectTestSuite() {
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

		suite.addTest(new ReadObjectCallTest(employeeClass, new SQLCall("SELECT t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER, t0.END_DATE, t0.START_DATE, t0.MANAGER_ID, t0.END_TIME, t0.START_TIME, t0.ADDR_ID FROM EMPLOYEE t0, SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID AND t0.F_NAME = '"+employee.getFirstName()+"' AND t0.L_NAME = '"+employee.getLastName()+"'")));
        employee = (Employee)manager.getObject(employeeClass, "0002");
		suite.addTest(new ReadObjectCallTest(employeeClass, new SQLCall("SELECT  t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER, t0.END_DATE, t0.START_DATE, t0.MANAGER_ID, t0.END_TIME, t0.START_TIME, t0.ADDR_ID FROM EMPLOYEE t0, SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID AND t0.F_NAME = '"+employee.getFirstName()+"' AND t0.L_NAME = '"+employee.getLastName()+"'")));
        employee = (Employee)manager.getObject(employeeClass, "0003");
		suite.addTest(new ReadObjectCallTest(employeeClass, new SQLCall("SELECT  t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER, t0.END_DATE, t0.START_DATE, t0.MANAGER_ID, t0.END_TIME, t0.START_TIME, t0.ADDR_ID FROM EMPLOYEE t0, SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID AND t0.F_NAME = '"+employee.getFirstName()+"' AND t0.L_NAME = '"+employee.getLastName()+"'")));

        org.eclipse.persistence.testing.models.employee.domain.Project project = (org.eclipse.persistence.testing.models.employee.domain.Project)manager.getObject(largeProjectClass, "0001");
        ReadObjectTest test = new ReadObjectTest(project);
        test.setQuery(new org.eclipse.persistence.queries.ReadObjectQuery(org.eclipse.persistence.testing.models.employee.domain.Project.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("id").equal(project.getId())));
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
        TestSuite suite = getSRGUpdateObjectTestSuite();

        //Add new tests here ...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the employee demo.");

        Class employeeClass = org.eclipse.persistence.testing.models.employee.domain.Employee.class;
        Class largeProjectClass = org.eclipse.persistence.testing.models.employee.domain.LargeProject.class;
        Class smallProjectClass = org.eclipse.persistence.testing.models.employee.domain.SmallProject.class;
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

    public static TestSuite getMiscTestSuite() {
        TestSuite suite = getSRGMiscTestSuite();

        //Add new tests here ...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGMiscTestSuite() {
        TestSuite suite;

        suite = new TestSuite();
        suite.setName("MiscellaneousTests");

        suite.addTest(new AddDescriptorsTest());

        return suite;
    }
}
