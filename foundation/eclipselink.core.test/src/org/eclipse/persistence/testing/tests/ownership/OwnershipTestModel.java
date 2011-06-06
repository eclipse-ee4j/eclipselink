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
package org.eclipse.persistence.testing.tests.ownership;

import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.ownership.*;

/**
 * This model tests reading/writing/deleting through using the ownership model.
 */
public class OwnershipTestModel extends TestModel {
    public OwnershipTestModel() {
        setDescription("This model tests reading/writing/deleting of the complex ownership model.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new OwnershipSystem());
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
        addTest(getUnitOfWorkTestSuite());
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("OwnershipDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the ownership model.");

        Class objectAClass = ObjectA.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new DeleteObjectTest(manager.getObject(objectAClass, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(objectAClass, "example2")));
        suite.addTest(new DeleteObjectTest(manager.getObject(objectAClass, "example3")));

        return suite;
    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("OwnershipInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the ownership model.");

        suite.addTest(new InsertObjectTest(ObjectA.example1()));
        suite.addTest(new InsertObjectTest(ObjectA.example2()));
        suite.addTest(new InsertObjectTest(ObjectA.example3()));

        return suite;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("OwnershipReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the ownership model.");

        suite.addTest(new ReadAllTest(ObjectA.class, 3));
        suite.addTest(new ReadAllTest(ObjectB.class, 3));
        suite.addTest(new ReadAllTest(ObjectC.class, 6));
        suite.addTest(new ReadAllTest(ObjectD.class, 6));
        suite.addTest(new ReadAllTest(ObjectE.class, 18));

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("OwnershipReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the ownership model.");

        Class objectAClass = ObjectA.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(objectAClass, "example1")));
        suite.addTest(new ReadObjectTest(manager.getObject(objectAClass, "example2")));
        suite.addTest(new ReadObjectTest(manager.getObject(objectAClass, "example3")));

        return suite;
    }

    public static TestSuite getUnitOfWorkTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("OwnershipUnitOfWorkTestSuite");
        suite.setDescription("This suite verifies that UOW works in the ownership model");

        Class objectAClass = ObjectA.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new UnitOfWorkTest(manager.getObject(objectAClass, "example1")));
        suite.addTest(new UnitOfWorkTest(manager.getObject(objectAClass, "example2")));
        suite.addTest(new UnitOfWorkTest(manager.getObject(objectAClass, "example3")));

        return suite;
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("OwnershipUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the ownership model.");

        Class objectAClass = ObjectA.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(objectAClass, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(objectAClass, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(objectAClass, "example2")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(objectAClass, "example3")));

        return suite;
    }
}
