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
package org.eclipse.persistence.testing.tests.readonly;

import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.models.mapping.*;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;
import org.eclipse.persistence.testing.models.readonly.ReadOnlySystem;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;

public class ReadOnlyTestModel extends TestModel {
    public ReadOnlyTestModel() {
        setDescription("This model contains tests that must be verified manually.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new ReadOnlySystem());
        addRequiredSystem(new InheritanceSystem());
        addRequiredSystem(new MappingSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.models.aggregate.AggregateSystem());
    }

    public void addTests() {
        addTest(getReadTestSuite());
        addTest(getReadOnlyClassesTestSuite());
        addTest(getBidirectionalMMDeleteTestSuite());
        addTest(new WriteableMappingReadOnlyClassTest());
    }

    public static TestSuite getBidirectionalMMDeleteTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("BidirectionalMMDeleteTestSuite");
        suite.setDescription("This suite tests deletion in a bidirectional MM private owned relationship.");

        suite.addTest(new BidirectionalMMDeleteTest());

        return suite;
    }

    public static TestSuite getReadOnlyClassesTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ReadOnlyClassesTestSuite");
        suite.setDescription("This suite tests read-only classes and descriptors in the UnitOfWork.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadOnlyClassManyToManyTestCase());
        suite.addTest(new ReadOnlyClassOneToManyTestCase());
        suite.addTest(new ReadOnlyClassAggregateTestCase());
        suite.addTest(new ReadOnlyClassInsertTestCase());
        suite.addTest(new ReadOnlyClassUpdateTestCase());
        suite.addTest(new ReadOnlyClassDeleteTestCase());
        suite.addTest(new ReadOnlyClassAccessingTestCase());
        suite.addTest(new InsertReadOnlyClassTestCase());
        suite.addTest(new InsertReadOnlyDescriptorTestCase());
        suite.addTest(new UpdateReadOnlyClassTestCase());
        suite.addTest(new DeleteReadOnlyClassTestCase());
        suite.addTest(new DeleteReadOnlyDescriptorTestCase());
        suite.addTest(new ReadOnlyDescriptorInsertTestCase());
        suite.addTest(new ReadOnlyClassDeepMergeCloneTest());

        return suite;
    }

    public static TestSuite getReadTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ReadOnlyReadTestSuite");
        suite.setDescription("This suite tests that read only was handled properly.");

        suite.addTest(new ReadTest());

        return suite;
    }
}
