/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
 package org.eclipse.persistence.testing.tests.jpa.memory;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.persistence.testing.models.jpa.performance.Address;
import org.eclipse.persistence.testing.models.jpa.performance.Employee;
import org.eclipse.persistence.testing.models.jpa.performance.EmployeeTableCreator;
import org.eclipse.persistence.testing.models.jpa.performance.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.performance.PhoneNumber;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.*;

/**
 * Memory tests that test for memory leaks.
 */
public class JPAMemoryLeakModel extends TestModel {

    public JPAMemoryLeakModel() {
        setDescription("Memory tests that test for memory leaks.");
    }

    public void addTests() {
        addTest(buildReadTest());
        addTest(buildInsertTest());
        addTest(buildUpdateTest());
        addTest(buildParameterizedBatchWriteTest());
        addTest(buildExpressionCacheTest());
    }

    /**
     * Create/populate database.
     */
    public void setup() {
        setupProvider();
        getSession().logMessage(getExecutor().getEntityManagerFactory().getClass().toString());
        System.out.println(getExecutor().getEntityManagerFactory().getClass().toString());
        // Populate database.
        EntityManager manager = getExecutor().createEntityManager();
        // Create schema using session from entity manager to create sequences correctly.
        try {
            // Create schema.
            new EmployeeTableCreator().replaceTables(((JpaEntityManager)manager).getServerSession());
        } catch (ClassCastException cast) {
            // Create using DatabaseSession if not EclipseLink JPA.
            new EmployeeTableCreator().replaceTables(getDatabaseSession());
        }
        
        manager.getTransaction().begin();

        // Populate database
        for (int j = 0; j < 100; j++) {
            Employee empInsert = new Employee();
            empInsert.setFirstName("Brendan");
            empInsert.setMale();
            empInsert.setLastName("" + j + "");
            empInsert.setSalary(100000);
            EmploymentPeriod employmentPeriod = new EmploymentPeriod();
            java.sql.Date startDate = Helper.dateFromString("1901-12-31");
            java.sql.Date endDate = Helper.dateFromString("1895-01-01");
            employmentPeriod.setEndDate(startDate);
            employmentPeriod.setStartDate(endDate);
            empInsert.setPeriod(employmentPeriod);
            empInsert.setAddress(new Address());
            empInsert.getAddress().setCity("Nepean");
            empInsert.getAddress().setPostalCode("N5J2N5");
            empInsert.getAddress().setProvince("ON");
            empInsert.getAddress().setStreet("1111 Mountain Blvd. Floor 13, suite " + j);
            empInsert.getAddress().setCountry("Canada");
            empInsert.addPhoneNumber(new PhoneNumber("Work Fax", "613", "2255943"));
            empInsert.addPhoneNumber(new PhoneNumber("Home", "613", "2224599"));
            manager.persist(empInsert);
        }

        manager.getTransaction().commit();
        manager.close();
    }
    
    /**
     * Setup the JPA provider.
     */
    public void setupProvider() {
        // Configure provider to be TopLink.
        String providerClass = "org.eclipse.persistence.jpa.PersistenceProvider";
        PersistenceProvider provider = null;
        try {
            provider = (PersistenceProvider)Class.forName(providerClass).newInstance();
        } catch (Exception error) {
            throw new TestProblemException("Failed to create persistence provider.", error);
        }
        Map properties = getPersistenceProperties();
        getExecutor().setEntityManagerFactory(provider.createEntityManagerFactory("performance", properties));
    }
    
    /**
     * Build the persistence properties.
     */
    public Map getPersistenceProperties() {    
        Map properties = new HashMap();
        properties.put("eclipselink.jdbc.driver", getSession().getLogin().getDriverClassName());
        properties.put("eclipselink.jdbc.url", getSession().getLogin().getConnectionString());
        properties.put("eclipselink.jdbc.user", getSession().getLogin().getUserName());
        properties.put("eclipselink.jdbc.password", getSession().getLogin().getPassword());
        properties.put("eclipselink.logging.level", getSession().getSessionLog().getLevelString());
        properties.put("eclipselink.jdbc.cache-statements", "true");
        return properties;
    }
    
    /**
     * Test that inserts allow the garbage collection of the persisted objects.
     */
    public TestCase buildInsertTest() {
        MemoryLeakTestCase test = new MemoryLeakTestCase() {
            public void test() {
                EntityManager manager = createEntityManager();
                ((JpaEntityManager)manager).getUnitOfWork().getParent().getIdentityMapAccessor().initializeAllIdentityMaps();
                manager.getTransaction().begin();
                for (int count = 0; count < 500; count++) {
                    Employee employee = new Employee();
                    employee.setFirstName("NewGuy");
                    employee.setLastName("Smith");
    
                    manager.persist(employee);
                    addWeakReference(employee);
                }
                manager.getTransaction().commit();
                Query query = manager.createQuery("Select e from Employee e");
                query.setHint("eclipselink.read-only", true);
                query.setHint("eclipselink.cache-usage", CacheUsage.CheckCacheOnly);
                addWeakReferences(query.getResultList());
                addWeakReference(manager);
                addWeakReference(((JpaEntityManager)manager).getUnitOfWork());
                addWeakReference(((JpaEntityManager)manager).getUnitOfWork().getParent());
                manager.close();
            }
            
            public void reset() {
                getSession().executeNonSelectingSQL("Delete from P_EMPLOYEE where F_NAME = 'NewGuy'");
            }
        };
        test.setName("InsertMemoryLeakTest");
        test.setThreshold(100);
        return test;
    }

    /**
     * Test that update allow the garbage collection of the objects.
     */
    public TestCase buildUpdateTest() {
        MemoryLeakTestCase test = new MemoryLeakTestCase() {
            public void test() {
                EntityManager manager = createEntityManager();
                ((JpaEntityManager)manager).getUnitOfWork().getParent().getIdentityMapAccessor().initializeAllIdentityMaps();
                manager.getTransaction().begin();
                Query query = manager.createQuery("Select e from Employee e");
                List<Employee> employees = query.getResultList();
                for (Employee employee : employees) {
                    employee.setFirstName("UpdatedGuy");
        
                    addWeakReference(employee);
                }
                manager.getTransaction().commit();
                query = manager.createQuery("Select e from Employee e");
                query.setHint("eclipselink.return-shared", true);
                addWeakReferences(query.getResultList());
                addWeakReference(manager);
                addWeakReference(((JpaEntityManager)manager).getUnitOfWork());
                addWeakReference(((JpaEntityManager)manager).getUnitOfWork().getParent());
                manager.close();
            }
        };
        test.setName("UpdateMemoryLeakTest");
        test.setThreshold(100);
        return test;
    }
    
    /**
     * Test that update allow the garbage collection of the objects.
     */
    public TestCase buildExpressionCacheTest() {
        MemoryLeakTestCase test = new MemoryLeakTestCase() {
            public void test() {
                EntityManager manager = createEntityManager();
                manager.getTransaction().begin();
                ((JpaEntityManager)manager).getUnitOfWork().getParent().getIdentityMapAccessor().initializeAllIdentityMaps();
                DescriptorQueryManager queryManager = ((JpaEntityManager)manager).getUnitOfWork().getDescriptor(Employee.class).getQueryManager();
                queryManager.setExpressionQueryCacheMaxSize(queryManager.getExpressionQueryCacheMaxSize());
                ((JpaEntityManager)manager).getUnitOfWork().readAllObjects(Employee.class);
                addWeakReference(((JpaEntityManager)manager).getUnitOfWork());
                manager.getTransaction().commit();
                addWeakReference(manager);
                addWeakReference(((JpaEntityManager)manager).getUnitOfWork());
                addWeakReference(((JpaEntityManager)manager).getUnitOfWork().getParent());
                manager.close();
            }
        };
        test.setName("ExpressionCacheTest");
        return test;
    }

    /**
     * Test that read allow the garbage collection of the objects.
     */
    public TestCase buildReadTest() {
        MemoryLeakTestCase test = new MemoryLeakTestCase() {
            public void test() {
                EntityManager manager = createEntityManager();
                ((JpaEntityManager)manager).getUnitOfWork().getParent().getIdentityMapAccessor().initializeAllIdentityMaps();
                Query query = manager.createQuery("Select e from Employee e");
                query.setHint("eclipselink.return-shared", true);
                addWeakReferences(query.getResultList());
                addWeakReference(query);
                query = manager.createQuery("Select e from Employee e");
                addWeakReferences(query.getResultList());
                addWeakReference(query);
                addWeakReference(manager);
                addWeakReference(((JpaEntityManager)manager).getUnitOfWork());
                addWeakReference(((JpaEntityManager)manager).getUnitOfWork().getParent());
                manager.close();
            }
        };
        test.setName("ReadMemoryLeakTest");
        test.setThreshold(100);
        return test;
    }
    /**
     * Test for bug 229831 : BATCH WRITING CAUSES MEMORY LEAKS WITH UOW
     * Tests that parameterized batch insert allows the garbage collection of the persisted objects and uow.
     */
    public TestCase buildParameterizedBatchWriteTest() {
        MemoryLeakTestCase test = new MemoryLeakTestCase() {
            public void test() {
                EntityManager manager = createEntityManager();
                boolean usesBatchWriting = ((JpaEntityManager)manager).getServerSession().getPlatform().usesBatchWriting();
                if(!usesBatchWriting) {
                    ((JpaEntityManager)manager).getServerSession().getPlatform().setUsesBatchWriting(true);
                }
                boolean shouldBindAllParameters = ((JpaEntityManager)manager).getServerSession().getPlatform().shouldBindAllParameters();
                if(!shouldBindAllParameters) {
                    ((JpaEntityManager)manager).getServerSession().getPlatform().setShouldBindAllParameters(true);
                }
                manager.getTransaction().begin();
                for (int count = 0; count < 5; count++) {
                    Employee employee = new Employee();
                    employee.setFirstName("NewBatchGuy");
                    employee.setLastName("Smith");
    
                    manager.persist(employee);
                    addWeakReference(employee);
                }
                addWeakReference(manager);
                addWeakReference(((JpaEntityManager)manager).getUnitOfWork());
                addWeakReference(((JpaEntityManager)manager).getUnitOfWork().getParent());
                manager.getTransaction().commit();
                if(!usesBatchWriting) {
                    ((JpaEntityManager)manager).getServerSession().getPlatform().setUsesBatchWriting(false);
                }
                if(!shouldBindAllParameters) {
                    ((JpaEntityManager)manager).getServerSession().getPlatform().setShouldBindAllParameters(false);
                }
                manager.close();
            }
            
            public void reset() {
                getSession().executeNonSelectingSQL("Delete from P_EMPLOYEE where F_NAME = 'NewBatchGuy'");
            }
        };
        test.setName("ParametrizedBatchWriteMemoryLeakTest");
        return test;
    }
}
