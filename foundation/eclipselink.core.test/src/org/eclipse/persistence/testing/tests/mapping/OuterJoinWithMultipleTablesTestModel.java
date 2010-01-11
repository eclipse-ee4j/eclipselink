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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.mapping.OuterJoinSystem;
import org.eclipse.persistence.testing.models.mapping.Student;

/**
 * This model tests reading/writing/deleting through using the outer join with multiple tables.
 */
public class OuterJoinWithMultipleTablesTestModel extends TestModel {
    public OuterJoinWithMultipleTablesTestModel() {
        setDescription("This model tests reading/writing/deleting of multiple tables using outer joins.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new OuterJoinSystem());
    }

    public void addTests() {
        addTest(getInsertObjectTestSuite());
        addTest(getReadObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("OuterJoinWithMultipleTablesDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the outer join test model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new DeleteObjectTest(manager.getObject(Student.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Student.class, "example2")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Student.class, "example3")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Student.class, "example4")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Student.class, "example5")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Student.class, "example6")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Student.class, "example7")));

        return suite;
    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("OuterJoinWithMultipleTablesInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the outer join test model.");

        //PopulationManager manager = PopulationManager.getDefaultManager();
        suite.addTest(new InsertObjectTest(Student.example8()));
        suite.addTest(new InsertObjectTest(Student.example9()));
        suite.addTest(new InsertObjectTest(Student.example10()));
        suite.addTest(new InsertObjectTest(Student.example11()));
        suite.addTest(new InsertObjectTest(Student.example12()));
        suite.addTest(new InsertObjectTest(Student.example13()));
        suite.addTest(new InsertObjectTest(Student.example14()));

        return suite;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("OuterJoinWithMultipleReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the mapping model.");

        suite.addTest(new ReadAllTest(Student.class, 8));

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("OuterJoinWithMultipleTablesReadObjectTestSuite");
        suite.setDescription("This suite tests the reading of each object in the outer join test model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(Student.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Student.class, "example5")));
        suite.addTest(new ReadObjectTest(manager.getObject(Student.class, "example3")));

        return suite;
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("OuterJoinWithMultipleTablesUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the Outer join test model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(Student.class, "example2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Student.class, "example2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Student.class, "example3")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Student.class, "example4")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Student.class, "example5")));

        return suite;
    }

    public void setup() {
        if (!((getSession().getLogin().getPlatform().isOracle()) || (getSession().getLogin().getPlatform().isTimesTen()))) {
            throw new TestWarningException("This test is not supported on this Database");

        }
    }
}
