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
package org.eclipse.persistence.testing.tests.collections;

import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.framework.DeleteObjectTest;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.collections.CollectionsSystem;
import org.eclipse.persistence.testing.models.collections.Restaurant;

/**
 * This model tests reading/writing/deleting through using the complex mapping model.
 */
public class CollectionsTestModel extends TestModel {
    public CollectionsTestModel() {
        setDescription("This model tests reading/writing/deleting of the complex mapping model.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new CollectionsSystem());
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getWriteObjectTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getUnitOfWorkTestSuite());
        addTest(new CompareKeyWithBackupTest());
        addTest(new TreeSetComparatorTest());
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CollectionsDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the collections model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new DeleteObjectTest(manager.getObject(Restaurant.class, "example1")));
        suite.addTest(new DeleteObjectTest(manager.getObject(Restaurant.class, "example2")));
        suite.addTest(new OTMPrivateOwnedDeleteObjectTest());

        return suite;
    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CollectionsInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the collections model.");
        PopulationManager manager = PopulationManager.getDefaultManager();
        suite.addTest(new CollectionInsertObjectTest(CollectionInsertObjectTest
                                                     .buildInstanceToInsert()));
        suite.addTest(new CollectionInsertDetectionTest());
        
        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CollectionsReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the Collections model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new CollectionReadObjectTest(manager.getObject(Restaurant.class, "example1")));
        suite.addTest(new CollectionReadObjectTest(manager.getObject(Restaurant.class, "example2")));

        return suite;
    }

    public static TestSuite getUnitOfWorkTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CollectionsUnitOfWorkTestSuite");
        suite.setDescription("This suite test the reading of each object in the mapping model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new UnitOfWorkTest(manager.getObject(Restaurant.class, "example1")));
        suite.addTest(new UnitOfWorkTest(manager.getObject(Restaurant.class, "example2")));
        //	suite.addTest(new DirectCollectionUnitOfWorkTest());
        suite.addTest(new OTMHashtableObjectUpdateTest());

        // TRANSPARENTMAPPING OF VECTOR IS NOT MAINTAINED IF UOW.SETSHOULDPERFORMDELETESFIRST
        suite.addTest(new PerformDeletesFirstCollectionObjectRemovalTest(false));
        suite.addTest(new PerformDeletesFirstCollectionObjectRemovalTest(true));
        
        return suite;
    }

    public static TestSuite getWriteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CollectionsWriteObjectTestSuite");
        suite.setDescription("This suite test the writing of each object in the mapping model.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(Restaurant.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Restaurant.class, "example1")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(Restaurant.class, "example2")));

        return suite;
    }
}
