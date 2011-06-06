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
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.models.interfaces.*;

/**
 * This model tests interface support, where the interfaces have tables in the database
 */
public class InterfaceWithTablesTestModel extends TestModel {

    public InterfaceWithTablesTestModel() {
        setDescription("This model tests interface support, where the interfaces have tables in the database");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new InterfaceWithTablesSystem());
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
        suite.setName("InterfaceWithTablesDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the interface model (with tables).");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new DeleteObjectTest(manager.getObject(TVSchedule.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Show.class, "example2")));

        return suite;
    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("InserfaceWithTablesInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the interface model (with tables).");

        Show show = new Show();
        show.setName("Flintstones");

        suite.addTest(new InsertObjectTest(show));

        return suite;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("InterfaceWithTablesReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the interface model (with tables).");

        suite.addTest(new ReadAllTest(Program.class, 2));
        suite.addTest(new ReadAllTest(TVSchedule.class, 1));

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("InterfaceWithTablesReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the interface model (with tables).");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(TVSchedule.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Show.class, "example2")));
        suite.addTest(new ReadObjectTest(manager.getObject(Network.class, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(Commercial.class, "example1")));

        return suite;
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("InterfaceWithTablesUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the interface model (with tables).");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(TVSchedule.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(TVSchedule.class, "example1")));

        return suite;
    }
}
