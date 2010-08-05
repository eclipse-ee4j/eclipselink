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
package org.eclipse.persistence.testing.tests.simultaneous;

import java.util.Vector;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.models.events.AboutToInsertMultiTableObject;
import org.eclipse.persistence.testing.models.events.EventHookSystem;
import org.eclipse.persistence.testing.models.events.AboutToInsertSingleTableObject;
import org.eclipse.persistence.testing.tests.events.MultipleTableAboutToInsertTest;
import org.eclipse.persistence.testing.tests.events.SingleTableAboutToInsertTest;

public class SimultaneousTestsModel extends TestModel {
    protected Session originalSession;
    
    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     * Unfortunately JUnit only allows suite methods to be static,
     * so it is not possible to generically do this.
     */
    public static junit.framework.TestSuite suite() {
        return new SimultaneousTestsModel();
    }
    
    public SimultaneousTestsModel() {
        setDescription("This model runs MultithreadTestCases.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeSystem());
        addRequiredSystem(new EventHookSystem());
    }

    public void addTests() {
        addTest(new ConcurrencyTest());
        addTest(new AppendLockTest());
        addTest(new UpdateCacheKeyPropertiesTest());
        addTest(getReadEmployeeTestSuite());
        addTest(getAboutToUpdateEventTestSuite());
        addTest(getDescriptorQueryManagerAddQueryTest());
        addTest(getQueryCacheMultithreadedTest());
        addTest(new ConcurrentDecryptionTest());
    }

    public static TestSuite getReadEmployeeTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Employee Multithread Test Suite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the employee demo.");

        Vector tests = new Vector();
        Class employeeClass = org.eclipse.persistence.testing.models.employee.domain.Employee.class;
        Class largeProjectClass = org.eclipse.persistence.testing.models.employee.domain.LargeProject.class;
        Class smallProjectClass = org.eclipse.persistence.testing.models.employee.domain.SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        tests.add(new ReadObjectTest(manager.getObject(employeeClass, "0001")));
        tests.add(new ReadObjectTest(manager.getObject(employeeClass, "0002")));
        tests.add(new ReadObjectTest(manager.getObject(employeeClass, "0003")));
        tests.add(new ReadObjectTest(manager.getObject(employeeClass, "0004")));
        tests.add(new ReadObjectTest(manager.getObject(employeeClass, "0005")));

        org.eclipse.persistence.testing.models.employee.domain.Project project = (org.eclipse.persistence.testing.models.employee.domain.Project)manager.getObject(largeProjectClass, "0001");
        ReadObjectTest test = new ReadObjectTest(project);
        test.setQuery(new org.eclipse.persistence.queries.ReadObjectQuery(org.eclipse.persistence.testing.models.employee.domain.Project.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("id").equal(project.getId())));
        tests.add(test);

        tests.add(new ReadObjectTest(manager.getObject(smallProjectClass, "0001")));
        tests.add(new ReadObjectTest(manager.getObject(smallProjectClass, "0002")));
        tests.add(new ReadObjectTest(manager.getObject(smallProjectClass, "0003")));

        tests.add(new ReadObjectTest(manager.getObject(largeProjectClass, "0001")));
        tests.add(new ReadObjectTest(manager.getObject(largeProjectClass, "0002")));
        tests.add(new ReadObjectTest(manager.getObject(largeProjectClass, "0003")));

        tests.add(new ReadAllTest(org.eclipse.persistence.testing.models.employee.domain.Employee.class, 12));
        tests.add(new ReadAllTest(org.eclipse.persistence.testing.models.employee.domain.Project.class, 15));
        tests.add(new ReadAllTest(org.eclipse.persistence.testing.models.employee.domain.LargeProject.class, 5));
        tests.add(new ReadAllTest(org.eclipse.persistence.testing.models.employee.domain.SmallProject.class, 10));

        suite.addTest(new MultithreadTestCase(tests));

        return suite;
    }

    /**
     *  CR#3237
     *  Multithreaded tests for changing Database rows in AboutToInsertEvent
     */
    public static TestSuite getAboutToUpdateEventTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("About to Update Multithread Test Suite");
        suite.setDescription("This suite tests use of aboutToUpdateEvents to change what is going to the database.");

        Vector tests = new Vector();

        tests.add(new SingleTableAboutToInsertTest(new AboutToInsertSingleTableObject(), true));
        tests.add(new MultipleTableAboutToInsertTest(new AboutToInsertMultiTableObject(), true));
        tests.add(new SingleTableAboutToInsertTest(new AboutToInsertSingleTableObject(), true));
        tests.add(new MultipleTableAboutToInsertTest(new AboutToInsertMultiTableObject(), true));
        tests.add(new SingleTableAboutToInsertTest(new AboutToInsertSingleTableObject(), true));
        tests.add(new MultipleTableAboutToInsertTest(new AboutToInsertMultiTableObject(), true));
        tests.add(new SingleTableAboutToInsertTest(new AboutToInsertSingleTableObject(), true));
        tests.add(new MultipleTableAboutToInsertTest(new AboutToInsertMultiTableObject(), true));
        tests.add(new SingleTableAboutToInsertTest(new AboutToInsertSingleTableObject(), true));
        tests.add(new MultipleTableAboutToInsertTest(new AboutToInsertMultiTableObject(), true));
        tests.add(new SingleTableAboutToInsertTest(new AboutToInsertSingleTableObject(), true));
        tests.add(new MultipleTableAboutToInsertTest(new AboutToInsertMultiTableObject(), true));
        MultithreadTestCase test = new MultithreadTestCase(tests);

        // Using a sequence connection pool gives us closer to real multithreaded operation since it 
        // moves the sequence number acquisition outside of the transaction in our tests
        test.useSequenceConnectionPool();
        suite.addTest(test);

        return suite;
    }

    public static TestSuite getDescriptorQueryManagerAddQueryTest() {
        TestSuite suite = new TestSuite();
        suite.setName("DescriptorQueryManagerAddQueryTest");
        suite.setDescription("This suite tests use of descriptorQueryManager.addQuery() when called simultaneously with multiple threads..");
        suite.addTest(new DescriptorQueryManagerMultithreadedTest());

        return suite;
    }

    public static TestSuite getQueryCacheMultithreadedTest() {
        TestSuite suite = new TestSuite();
        suite.setName("QueryCacheMultithreadedTest");
        suite.setDescription("This suite runs queries with cached results many times in different threads.");
        suite.addTest(new QueryCacheMultithreadedTest());

        return suite;
    }
}
