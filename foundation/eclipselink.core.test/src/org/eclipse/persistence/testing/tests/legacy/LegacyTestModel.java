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
package org.eclipse.persistence.testing.tests.legacy;

import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.framework.DeleteObjectTest;
import org.eclipse.persistence.testing.framework.InsertObjectTest;
import org.eclipse.persistence.testing.framework.ReadAllTest;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.legacy.Employee;
import org.eclipse.persistence.testing.models.legacy.LegacySystem;
import org.eclipse.persistence.testing.models.legacy.LegacyTables;

/**
 * This model tests reading/writing/deleting through using the complex mapping model.
 */
public class LegacyTestModel extends TestModel {
    public LegacyTestModel() {
        setDescription("This model tests reading/writing/deleting of the complex legacy model.");
    }

    public void addRequiredSystems() {
        //this part is added to help test for possibility of having a # in a field name:
        if (getSession().getLogin().getPlatform().isOracle() || getSession().getLogin().getPlatform().isSybase()  || getSession().getLogin().getPlatform().isSQLAnywhere()) {
            LegacyTables.computerDescriptionFieldName = "DESCRIP#";
        } else {
            LegacyTables.computerDescriptionFieldName = "DESCRIP";
        }

        addRequiredSystem(new LegacySystem());
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("LegacyDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the mapping model.");

        Class employeeClass = Employee.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "example2")));
        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "example3")));

        return suite;
    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("LegacyInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the mapping model.");

        suite.addTest(new InsertObjectTest(Employee.example4()));
        suite.addTest(new InsertObjectTest(Employee.example5()));
        suite.addTest(new InsertObjectTest(Employee.example6()));

        return suite;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("LegacyReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the mapping model.");

        suite.addTest(new ReadAllTest(Employee.class, 3));

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("LegacyReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the mapping model.");

        Class employeeClass = Employee.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "example2")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "example3")));

        return suite;
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("LegacyUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the mapping model.");

        Class employeeClass = Employee.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "example2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "example3")));

        return suite;
    }
}
