/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.transparentindirection;

import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.indirection.IndirectSet;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.transparentindirection.BidirectionalRelationshipSystem;
import org.eclipse.persistence.testing.models.transparentindirection.CustomIndirectContainerSystem;
import org.eclipse.persistence.testing.models.transparentindirection.Dog;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectListSystem;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectMapSystem;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectSetSystem;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

/**
 * Test transparent indirection.
 */
public class TransparentIndirectionModel extends TestModel {
    public TransparentIndirectionModel() {
        setDescription("Test Transparent Indirection");
    }

    @Override
    public void addRequiredSystems() {
        addRequiredSystem(new IndirectListSystem());
        addRequiredSystem(new IndirectMapSystem());
        addRequiredSystem(new IndirectSetSystem());
        addRequiredSystem(new CustomIndirectContainerSystem());
        addRequiredSystem(new BidirectionalRelationshipSystem());
    }

    @Override
    public void addTests() {
        addTest(new ZTestSuite(IndirectListTestDatabase.class));
        addTest(new ZTestSuite(IndirectMapTestDatabase.class));
        addTest(new ZTestSuite(IndirectSetTestDatabase.class));

        addTest(getCustomIndirectContainerTestSuite());
        addTest(getBidirectionalRelationshipTestSuite());
        // Bug 345495
        addTest(getNullDelegateInValueHolderTestSuite());
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

    public TestSuite getNullDelegateInValueHolderTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("NullDelegateInValueHolderTestSuite");
        suite.setDescription("This suite tests setting a ValueHolder in a transparent collection");

        suite.addTest(new NullDelegateInValueHolderTest(IndirectList.class));
        suite.addTest(new NullDelegateInValueHolderTest(IndirectSet.class));
        suite.addTest(new NullDelegateInValueHolderTest(IndirectMap.class));

        return suite;
    }

    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     */
    public static junit.framework.TestSuite suite() {
        return new TransparentIndirectionModel();
    }
}
