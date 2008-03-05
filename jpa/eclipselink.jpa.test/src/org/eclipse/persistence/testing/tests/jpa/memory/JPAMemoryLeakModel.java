/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import org.eclipse.persistence.testing.models.performance.*;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.config.CacheUsage;
import org.eclipse.persistence.testing.framework.*;

/**
 * Memory tests that test for memory leaks.
 */
public class JPAMemoryLeakModel extends TestModel {

    public JPAMemoryLeakModel() {
        setDescription("Memory tests that test for memory leaks.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeSystem());
    }

    public void addTests() {
        addTest(buildReadTest());
        addTest(buildInsertTest());
        addTest(buildUpdateTest());
    }

    /**
     * Create/populate database.
     */
    public void setup() {
        // Populate database (through TopLink).
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
            getDatabaseSession().insertObject(empInsert);
        }
        setupProvider();
        getSession().logMessage(getExecutor().getEntityManagerFactory().getClass().toString());
        System.out.println(getExecutor().getEntityManagerFactory().getClass().toString());
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
                query.setHint("eclipselink.return-shared", true);
                query.setHint("eclipselink.cache-usage", CacheUsage.CheckCacheOnly);
                addWeakReferences(query.getResultList());
                addWeakReference(manager);
                addWeakReference(((JpaEntityManager)manager).getUnitOfWork());
                addWeakReference(((JpaEntityManager)manager).getSession());
                manager.close();
            }
            
            public void reset() {
                getSession().executeNonSelectingSQL("Delete from PHONE where P_NUMBER = '9991111'");
                getSession().executeNonSelectingSQL("Delete from EMPLOYEE where F_NAME = 'NewGuy'");
                getSession().executeNonSelectingSQL("Delete from ADDRESS where STREET = 'Hasting Perf'");
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
                addWeakReference(((JpaEntityManager)manager).getSession());
                manager.close();
            }
        };
        test.setName("ReadMemoryLeakTest");
        test.setThreshold(100);
        return test;
    }
}