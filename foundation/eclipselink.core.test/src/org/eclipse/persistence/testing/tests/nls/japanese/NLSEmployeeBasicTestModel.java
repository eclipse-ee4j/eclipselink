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
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.framework.InsertObjectTest;
import org.eclipse.persistence.testing.framework.ReadAllCallTest;
import org.eclipse.persistence.testing.framework.ReadAllTest;
import org.eclipse.persistence.testing.framework.ReadObjectCallTest;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee;

/**
 * This model tests reading/writing/deleting through using the employee demo.
 */
public class NLSEmployeeBasicTestModel extends TestModel {
    public NLSEmployeeBasicTestModel() {
        setDescription("This model tests reading/writing/deleting using the employee demo in NLS_Japanese(Using Japanese data).");
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
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeDeleteObjectTestSuite");
        suite.setDescription("[NLS_Japanese] This suite tests the deletion of each object in the employee demo.");

        Class employeeClass = NLSEmployee.class;

        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new NLSEmployeeDeleteTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new NLSEmployeeDeleteTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new NLSEmployeeDeleteTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new NLSEmployeeDeleteTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new NLSEmployeeDeleteTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new NLSProjectDeleteTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new NLSProjectDeleteTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new NLSProjectDeleteTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new NLSProjectDeleteTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new NLSProjectDeleteTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new NLSProjectDeleteTest(manager.getObject(largeProjectClass, "0003")));

        return suite;

    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeInsertObjectTestSuite");
        suite.setDescription("[NLS_Japanese] This suite tests the insertion of each object in the employee demo.");
        NLSEmployeePopulator system = new NLSEmployeePopulator();

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
        suite.setDescription("[NLS_Japanese] This suite tests the reading of all the objects of each class in the employee demo.");

        suite.addTest(new ReadAllTest(NLSEmployee.class, 12));
        suite.addTest(new ReadAllTest(Project.class, 15));
        suite.addTest(new ReadAllTest(LargeProject.class, 5));
        suite.addTest(new ReadAllTest(SmallProject.class, 10));

        suite.addTest(new ReadAllCallTest(NLSEmployee.class, 12, new SQLCall("SELECT  t0.\u306b\u304a\u3064\u3066\u3051\u305d\u305b, t1.\u304a\u3059\u305f_\u3051\u3048, t0.\u3057_\u305b\u3042\u3059\u304a, t0.\u304b_\u305b\u3042\u3059\u304a, t1.\u3066\u3042\u3057\u3042\u3064\u306e, t0.\u304a\u3059\u305f_\u3051\u3048, t0.\u304d\u304a\u305b\u3048\u304a\u3064, t0.\u304a\u305b\u3048_\u3048\u3042\u3068\u304a, t0.\u3066\u3068\u3042\u3064\u3068_\u3048\u3042\u3068\u304a, t0.\u3059\u3042\u305b\u3042\u304d\u304a\u3064_\u3051\u3048, t0.\u304a\u305b\u3048_\u3068\u3051\u3059\u304a, t0.\u3066\u3068\u3042\u3064\u3068_\u3068\u3051\u3059\u304a, t0.\u3042\u3048\u3048\u3064_\u3051\u3048 FROM \u304a\u3059\u305f t0, \u3066\u3042\u3057\u3042\u3064\u306e t1 WHERE t1.\u304a\u3059\u305f_\u3051\u3048 = t0.\u304a\u3059\u305f_\u3051\u3048")));

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeReadObjectTestSuite");
        suite.setDescription("[NLS_Japanese] This suite test the reading of each object in the employee demo.");

        Class employeeClass = NLSEmployee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0005")));
        NLSEmployee employee = (NLSEmployee)manager.getObject(employeeClass, "0001");

        suite.addTest(new ReadObjectCallTest(employeeClass, new SQLCall("SELECT t0.\u306b\u304a\u3064\u3066\u3051\u305d\u305b, t1.\u304a\u3059\u305f_\u3051\u3048, t0.\u3057_\u305b\u3042\u3059\u304a, t0.\u304b_\u305b\u3042\u3059\u304a, t1.\u3066\u3042\u3057\u3042\u3064\u306e, t0.\u304a\u3059\u305f_\u3051\u3048, t0.\u304d\u304a\u305b\u3048\u304a\u3064, t0.\u304a\u305b\u3048_\u3048\u3042\u3068\u304a, t0.\u3066\u3068\u3042\u3064\u3068_\u3048\u3042\u3068\u304a, t0.\u3059\u3042\u305b\u3042\u304d\u304a\u3064_\u3051\u3048, t0.\u304a\u305b\u3048_\u3068\u3051\u3059\u304a, t0.\u3066\u3068\u3042\u3064\u3068_\u3068\u3051\u3059\u304a, t0.\u3042\u3048\u3048\u3064_\u3051\u3048 FROM \u304a\u3059\u305f t0, \u3066\u3042\u3057\u3042\u3064\u306e t1 WHERE t1.\u304a\u3059\u305f_\u3051\u3048 = t0.\u304a\u3059\u305f_\u3051\u3048 AND t0.\u304b_\u305b\u3042\u3059\u304a = '" + employee.getFirstName() + "' AND t0.\u3057_\u305b\u3042\u3059\u304a = '" + employee.getLastName() + "'")));
        employee = (NLSEmployee)manager.getObject(employeeClass, "0002");
        suite.addTest(new ReadObjectCallTest(employeeClass, new SQLCall("SELECT  t0.\u306b\u304a\u3064\u3066\u3051\u305d\u305b, t1.\u304a\u3059\u305f_\u3051\u3048, t0.\u3057_\u305b\u3042\u3059\u304a, t0.\u304b_\u305b\u3042\u3059\u304a, t1.\u3066\u3042\u3057\u3042\u3064\u306e, t0.\u304a\u3059\u305f_\u3051\u3048, t0.\u304d\u304a\u305b\u3048\u304a\u3064, t0.\u304a\u305b\u3048_\u3048\u3042\u3068\u304a, t0.\u3066\u3068\u3042\u3064\u3068_\u3048\u3042\u3068\u304a, t0.\u3059\u3042\u305b\u3042\u304d\u304a\u3064_\u3051\u3048, t0.\u304a\u305b\u3048_\u3068\u3051\u3059\u304a, t0.\u3066\u3068\u3042\u3064\u3068_\u3068\u3051\u3059\u304a, t0.\u3042\u3048\u3048\u3064_\u3051\u3048 FROM \u304a\u3059\u305f t0, \u3066\u3042\u3057\u3042\u3064\u306e t1 WHERE t1.\u304a\u3059\u305f_\u3051\u3048 = t0.\u304a\u3059\u305f_\u3051\u3048 AND t0.\u304b_\u305b\u3042\u3059\u304a = '" + employee.getFirstName() + "' AND t0.\u3057_\u305b\u3042\u3059\u304a = '" + employee.getLastName() + "'")));
        employee = (NLSEmployee)manager.getObject(employeeClass, "0003");
        suite.addTest(new ReadObjectCallTest(employeeClass, new SQLCall("SELECT  t0.\u306b\u304a\u3064\u3066\u3051\u305d\u305b, t1.\u304a\u3059\u305f_\u3051\u3048, t0.\u3057_\u305b\u3042\u3059\u304a, t0.\u304b_\u305b\u3042\u3059\u304a, t1.\u3066\u3042\u3057\u3042\u3064\u306e, t0.\u304a\u3059\u305f_\u3051\u3048, t0.\u304d\u304a\u305b\u3048\u304a\u3064, t0.\u304a\u305b\u3048_\u3048\u3042\u3068\u304a, t0.\u3066\u3068\u3042\u3064\u3068_\u3048\u3042\u3068\u304a, t0.\u3059\u3042\u305b\u3042\u304d\u304a\u3064_\u3051\u3048, t0.\u304a\u305b\u3048_\u3068\u3051\u3059\u304a, t0.\u3066\u3068\u3042\u3064\u3068_\u3068\u3051\u3059\u304a, t0.\u3042\u3048\u3048\u3064_\u3051\u3048 FROM \u304a\u3059\u305f t0, \u3066\u3042\u3057\u3042\u3064\u306e t1 WHERE t1.\u304a\u3059\u305f_\u3051\u3048 = t0.\u304a\u3059\u305f_\u3051\u3048 AND t0.\u304b_\u305b\u3042\u3059\u304a = '" + employee.getFirstName() + "' AND t0.\u3057_\u305b\u3042\u3059\u304a = '" + employee.getLastName() + "'")));

        Project project = (Project)manager.getObject(largeProjectClass, "0001");
        ReadObjectTest test = new ReadObjectTest(project);
        test.setQuery(new org.eclipse.persistence.queries.ReadObjectQuery(Project.class, new ExpressionBuilder().get("id").equal(project.getId())));
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
        suite.setDescription("[NLS_Japanese] This suite tests the updating of each object in the employee demo.");

        Class employeeClass = NLSEmployee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
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
}
