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
 package org.eclipse.persistence.testing.tests.jpa.memory;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.persistence.testing.models.jpa.performance.Address;
import org.eclipse.persistence.testing.models.jpa.performance.Employee;
import org.eclipse.persistence.testing.models.jpa.performance.EmployeeTableCreator;
import org.eclipse.persistence.testing.models.jpa.performance.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.performance.PhoneNumber;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.*;

/**
 * Memory tests that compare JPA memory usage.
 */
public class JPAMemoryRegressionModel extends TestModel {

    public JPAMemoryRegressionModel() {
        setDescription("Memory tests that compare JPA memory usage.");
    }

    public void addTests() {
        addTest(buildReadTest());
        addTest(buildInsertTest());
        addTest(buildUpdateTest());
        addTest(buildBootstrapTest());
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
        for (int j = 0; j < 1000; j++) {
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
        ((JpaEntityManager)manager).getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();
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
     * Measure the amount of memory used by inserts.
     */
    public TestCase buildBootstrapTest() {
        MemoryRegressionTestCase test = new MemoryRegressionTestCase() {
            @Override
            public void startTest() {
                getExecutor().getEntityManagerFactory().close();
                getExecutor().setEntityManagerFactory(null);
            }
            
            public void test() {
                EntityManager manager = createEntityManager();
                Query query = manager.createQuery("Select e from Employee e");
                query.getResultList();
                manager.close();
            }
        };
        test.setName("BootstrapMemoryTest");
        return test;
    }
    
    /**
     * Measure the amount of memory used by inserts.
     */
    public TestCase buildInsertTest() {
        MemoryRegressionTestCase test = new MemoryRegressionTestCase() {
            @Override
            public void startTest() {
                EntityManager manager = createEntityManager();
                ((JpaEntityManager)manager).getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                manager.close();
            }
            
            public void test() {
                for (int count = 0; count < 500; count++) {
                    EntityManager manager = createEntityManager();
                    manager.getTransaction().begin();
                    Employee employee = new Employee();
                    employee.setFirstName("NewGuy");
                    employee.setLastName("Smith");
                    manager.persist(employee);
                    manager.getTransaction().commit();
                    manager.close();
                }
            }
            
            @Override
            public void endTest() {
                EntityManager manager = createEntityManager();
                manager.getTransaction().begin();
                manager.createNativeQuery("Delete from P_EMPLOYEE where F_NAME = 'NewGuy'").executeUpdate();
                manager.getTransaction().commit();
                ((JpaEntityManager)manager).getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                manager.close();
            }
        };
        test.setName("InsertMemoryTest");
        return test;
    }

    /**
     * Measure the amount of memory used by updates.
     */
    public TestCase buildUpdateTest() {
        MemoryRegressionTestCase test = new MemoryRegressionTestCase() {
            @Override
            public void startTest() {
                EntityManager manager = createEntityManager();
                ((JpaEntityManager)manager).getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                manager.close();
            }
            
            public void test() {
                EntityManager manager = createEntityManager();
                Query query = manager.createQuery("Select e from Employee e");
                List<Employee> employees = query.getResultList();
                for (Employee employee : employees) {
                    manager.getTransaction().begin();
                    employee.setFirstName("UpdatedGuy");
                    manager.getTransaction().commit();
                }
                manager.close();
            }
            
            @Override
            public void endTest() {
                EntityManager manager = createEntityManager();
                ((JpaEntityManager)manager).getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                manager.close();
            }
        };
        test.setName("UpdateMemoryTest");
        return test;
    }

    /**
     * Measure the amount of memory used by reads.
     */
    public TestCase buildReadTest() {
        MemoryRegressionTestCase test = new MemoryRegressionTestCase() {
            @SuppressWarnings("unused")
            List results;
            
            @Override
            public void startTest() {
                EntityManager manager = createEntityManager();
                ((JpaEntityManager)manager).getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                manager.close();
            }
            public void test() {
                EntityManager manager = createEntityManager();
                Query query = manager.createQuery("Select e from Employee e");
                results = query.getResultList();
                manager.close();
            }
            
            @Override
            public void endTest() {
                results = null;
                EntityManager manager = createEntityManager();
                ((JpaEntityManager)manager).getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                manager.close();
            }
        };
        test.setName("ReadMemoryTest");
        return test;
    }
    
}
