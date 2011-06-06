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
package org.eclipse.persistence.testing.tests.weaving;

import java.lang.reflect.Field;

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.internal.weaving.*;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.performance.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

/**
 * This model tests reading/writing using weaved performance employee model.
 */
public class EmployeeWeavingTestModel extends TestModel {

    public EmployeeWeavingTestModel() {
        setDescription("This model tests reading/writing using weaved performance employee model.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeSystem());
    }

    public void addTests() {
        addTest(getWeaverTestSuite());
        addTest(getReadObjectTestSuite());
        addTest(getUpdateTestSuite());
        addTest(getInsertTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getFetchGroupsTestSuite());
    }

    public TestSuite getInsertTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeWeavedInsertTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the employee weaved performance model.");
        EmployeePopulator system = new EmployeePopulator();

        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicEmployeeExample1()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicEmployeeExample2()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicEmployeeExample3()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicEmployeeExample4()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicEmployeeExample5()));

        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicSmallProjectExample1()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicSmallProjectExample2()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicSmallProjectExample3()));

        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicLargeProjectExample1()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicLargeProjectExample2()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicLargeProjectExample3()));

        return suite;
    }

    public TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeWeavedReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the employee weaved performance model.");

        suite.addTest(new ReadAllTest(Employee.class, 12));
        suite.addTest(new ReadAllTest(Project.class, 15));
        suite.addTest(new ReadAllTest(LargeProject.class, 5));
        suite.addTest(new ReadAllTest(SmallProject.class, 10));

        return suite;
    }

    public TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeWeavedReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the employee weaved performance model.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0003")));

        return suite;
    }

    public TestSuite getUpdateTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeWeavedUpdateTestSuite");
        suite.setDescription("This suite tests the updating of each object in the employee demo.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(largeProjectClass, "0003")));

        return suite;
    }
    
    public TestSuite getWeaverTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("WeaverTestSuite");
        suite.setDescription("This suite tests that the weaver ran on the class files correctly.");

        suite.addTest(buildWeaverTest());
        return suite;
    }
    
    public TestSuite getFetchGroupsTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("FetchGroupsTestSuite");
        suite.setDescription("This suite tests weaving of fetch groups.");

        suite.addTest(buildFetchGroupReadObjectTest());
        suite.addTest(buildFetchGroupReadAllTest());
        suite.addTest(buildFetchGroupReadObjectUOWTest());
        suite.addTest(buildFetchGroupReadObjectUOWTransactionTest());
        suite.addTest(buildFetchGroupUpdateObjectUOWTest());
        suite.addTest(buildFetchGroupUpdateNonFetchedObjectUOWTest());
        return suite;
    }
    
    public TestCase buildWeaverTest() {
        TestCase test = new TestCase() {
            public void test() {
                testWeaving(new Employee(), true);
                testWeaving(new SmallProject(), true);
                testWeaving(new LargeProject(), true);
                testWeaving(new EmploymentPeriod(), false);
                testWeaving(new PhoneNumber(), true);
                testWeaving(new Address(), false);
                if ((new EmployeeSystem()) instanceof PersistenceWeaved) {
                    throwError("EmployeeSystem should not be weaved.");
                }
            }
            
            public void testWeaving(Object object, boolean indirection) {
                if (!(object instanceof PersistenceWeaved)) {
                    throwError("Object not weaved:" + object);
                }
                if ((object instanceof PersistenceWeavedLazy) != indirection) {
                    throwError("Object not weaved for indirection:" + object);
                }
                if (!(object instanceof ChangeTracker)) {
                    throwError("Object not weaved for ChangeTracker:" + object);
                }
                // Aggregate descriptors are stored on the mapping, nor weaved for PersistenceEntity.
                if (object instanceof EmploymentPeriod) {
                    if (!getSession().getDescriptor(Employee.class).getMappingForAttributeName("period").getReferenceDescriptor().getObjectChangePolicy().isAttributeChangeTrackingPolicy()) {
                        throwError("Descriptor not set to use change tracking:" + object);
                    }
                } else {
                    if (!getSession().getDescriptor(object).getObjectChangePolicy().isAttributeChangeTrackingPolicy()) {
                        throwError("Descriptor not set to use change tracking:" + object);
                    }
                    if (!(object instanceof PersistenceEntity)) {
                        throwError("Object not weaved for PersistenceEntity:" + object);
                    }
                    if (!(object instanceof FetchGroupTracker)) {
                        throwError("Object not weaved for FetchGroupTracker:" + object);
                    }
                }
            }
        };
        test.setName("WeaverTeest");
        return test;
    }
    
    /**
     * Test a basic fetch group read.
     */
    public TestCase buildFetchGroupReadObjectTest() {
        TestCase test = new TestCase() {
            public void test() throws Exception {
                Employee fetched = (Employee)getSession().readObject(Employee.class);
                getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                ReadObjectQuery query = new ReadObjectQuery(fetched);
                FetchGroup fetchGroup = new FetchGroup();
                fetchGroup.addAttribute("firstName");
                fetchGroup.addAttribute("lastName");
                query.setFetchGroup(fetchGroup);
                
                Employee partial = (Employee)getSession().executeQuery(query);
                verfiyFetchGroup(this, partial, fetched);
            }
        };
        test.setName("FetchGroupReadObjectTest");
        return test;
    }
    
    /**
     * Test a fetch group read in a unit of work.
     */
    public TestCase buildFetchGroupReadObjectUOWTest() {
        TestCase test = new TestCase() {
            public void test() throws Exception {
                Employee fetched = (Employee)getSession().readObject(Employee.class);
                getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                ReadObjectQuery query = new ReadObjectQuery(fetched);
                FetchGroup fetchGroup = new FetchGroup();
                fetchGroup.addAttribute("firstName");
                fetchGroup.addAttribute("lastName");
                query.setFetchGroup(fetchGroup);
                
                Employee partial = (Employee)getSession().acquireUnitOfWork().executeQuery(query);
                verfiyFetchGroup(this, partial, fetched);
            }
        };
        test.setName("FetchGroupReadObjectUOWTest");
        return test;
    }
    
    /**
     * Test a fetch group update in a unit of work.
     */
    public TestCase buildFetchGroupUpdateObjectUOWTest() {
        TestCase test = new TransactionalTestCase() {
            public void test() throws Exception {
                Employee fetched = (Employee)getSession().readObject(Employee.class);
                getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                ReadObjectQuery query = new ReadObjectQuery(fetched);
                FetchGroup fetchGroup = new FetchGroup();
                fetchGroup.addAttribute("firstName");
                fetchGroup.addAttribute("lastName");
                query.setFetchGroup(fetchGroup);
                
                UnitOfWork uow = getSession().acquireUnitOfWork();
                Employee partial = (Employee)uow.executeQuery(query);
                partial.setFirstName("partial");
                uow.commit();
                fetched = (Employee)getSession().readObject(partial);
                if (!fetched.getFirstName().equals("partial")) {
                    throwError("Partial update not merged.");
                }                
            }
        };
        test.setName("FetchGroupUpdateObjectUOWTest");
        return test;
    }
    
    /**
     * Test a fetch group update in a unit of work.
     */
    public TestCase buildFetchGroupUpdateNonFetchedObjectUOWTest() {
        TestCase test = new TransactionalTestCase() {
            public void test() throws Exception {
                Employee fetched = (Employee)getSession().readObject(Employee.class);
                getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                ReadObjectQuery query = new ReadObjectQuery(fetched);
                FetchGroup fetchGroup = new FetchGroup();
                fetchGroup.addAttribute("firstName");
                fetchGroup.addAttribute("lastName");
                fetchGroup.addAttribute("address");
                query.setFetchGroup(fetchGroup);
                
                UnitOfWork uow = getSession().acquireUnitOfWork();
                Employee partial = (Employee)uow.executeQuery(query);
                partial.setSalary(1234);
                uow.commit();
                fetched = (Employee)getSession().readObject(partial);
                if (fetched.getSalary() != 1234) {
                    throwError("Partial update not merged.");
                }                
            }
        };
        test.setName("FetchGroupUpdateNonFetchedObjectUOWTest");
        return test;
    }
    
    /**
     * Test a fetch group read in a unit of work early transaction.
     */
    public TestCase buildFetchGroupReadObjectUOWTransactionTest() {
        TestCase test = new TestCase() {
            public void test() throws Exception {
                Employee fetched = (Employee)getSession().readObject(Employee.class);
                getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                ReadObjectQuery query = new ReadObjectQuery(fetched);
                FetchGroup fetchGroup = new FetchGroup();
                fetchGroup.addAttribute("firstName");
                fetchGroup.addAttribute("lastName");
                fetchGroup.addAttribute("manager");
                query.setFetchGroup(fetchGroup);
                
                UnitOfWork uow = getSession().acquireUnitOfWork();
                uow.beginEarlyTransaction();
                try {
                    Employee partial = (Employee)uow.executeQuery(query);
                    verfiyFetchGroup(this, partial, fetched);
                } finally {
                    uow.release();
                }
            }
        };
        test.setName("FetchGroupReadObjectUOWTransactionTest");
        return test;
    }
    
    /**
     * Test a basic fetch group read.
     */
    public TestCase buildFetchGroupReadAllTest() {
        TestCase test = new TestCase() {
            public void test() throws Exception {
                getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                FetchGroup fetchGroup = new FetchGroup();
                fetchGroup.addAttribute("firstName");
                fetchGroup.addAttribute("lastName");
                fetchGroup.addAttribute("period");
                query.setFetchGroup(fetchGroup);
                
                List result = (List)getSession().executeQuery(query);
                for (Iterator iterator = result.iterator(); iterator.hasNext(); ) {
                    Employee partial = (Employee)iterator.next();
                    partial.getFirstName();
                    partial.getAddress().getCity();
                }
            }
        };
        test.setName("FetchGroupReadAllTest");
        return test;
    }
    
    /**
     * Helper verify method used to verify partially fetch objects (first/lastName).
     */
    public void verfiyFetchGroup(TestCase test, Employee partial, Employee fetched) throws Exception {    
        Field firstNameField = Employee.class.getDeclaredField("firstName");
        firstNameField.setAccessible(true);
        if (!(fetched.getFirstName().equals(firstNameField.get(partial)))) {
            test.throwError("FirstName not fetch correctly.");
        }
        Field salaryField = Employee.class.getDeclaredField("salary");
        salaryField.setAccessible(true);
        if (((Number)salaryField.get(partial)).longValue() != 0) {
            test.throwError("Salary should not be fetched.");
        }
        if (!(partial.getFirstName().equals(fetched.getFirstName()))) {
            test.throwError("FirstName not fetch correctly.");
        }
        if (!(partial.getLastName().equals(fetched.getLastName()))) {
            test.throwError("LastName not fetch correctly.");
        }
        if (partial.getSalary() != fetched.getSalary()) {
            test.throwError("Salary not fetch correctly.");
        }
        if (!partial.getAddress().getCity().equals(fetched.getAddress().getCity())) {
            test.throwError("Address not fetch correctly.");
        }
    }
}
