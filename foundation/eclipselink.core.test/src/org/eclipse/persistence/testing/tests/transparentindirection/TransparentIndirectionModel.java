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
package org.eclipse.persistence.testing.tests.transparentindirection;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.models.transparentindirection.*;

/**
 * Test transparent indirection.
 */
public class TransparentIndirectionModel extends TestModel {
    public TransparentIndirectionModel() {
        setDescription("Test Transparent Indirection");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new IndirectListSystem());
        addRequiredSystem(new IndirectMapSystem());
        addRequiredSystem(new IndirectSetSystem());
        addRequiredSystem(new CustomIndirectContainerSystem());
        addRequiredSystem(new BidirectionalRelationshipSystem());
    }

    public void addTests() {
        addTest(new ZTestSuite(IndirectListTestAPI.class));
        addTest(new ZTestSuite(IndirectMapTestAPI.class));
        addTest(new ZTestSuite(IndirectSetTestAPI.class));

        addTest(new ZTestSuite(IndirectListTestDatabase.class));
        addTest(new ZTestSuite(IndirectMapTestDatabase.class));
        addTest(new ZTestSuite(IndirectSetTestDatabase.class));

        addTest(getCustomIndirectContainerTestSuite());
        addTest(getBidirectionalRelationshipTestSuite());
    }

    public TestSuite getCustomIndirectContainerTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CustomIndirectContainerTestSuite");
        suite.setDescription("This suite tests custom IndirectContainers.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(Dog.class, "Bart")));
        suite.addTest(new UpdateDogTest());
        suite.addTest(new NullCollectionTest());
        return suite;
    }
    
    public TestSuite getBidirectionalRelationshipTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("BidirectionalRelationshipTestSuite");
        suite.setDescription("This suite tests bidirectional relationship maintenance.");

        suite.addTest(new BidirectionalRelationshipMaintenanceTest());

        return suite;
    }
}
