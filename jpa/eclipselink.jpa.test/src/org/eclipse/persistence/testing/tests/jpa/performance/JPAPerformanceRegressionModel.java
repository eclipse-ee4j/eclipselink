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
 package org.eclipse.persistence.testing.tests.jpa.performance;

import java.lang.reflect.Field;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.*;

/**
 * Performance tests that compare JPA performance.
 */
public class JPAPerformanceRegressionModel extends TestModel {

    public JPAPerformanceRegressionModel() {
        setDescription("Performance tests that compare JPA performance.");
    }

    public void addTests() {
        addTest(getReadingTestSuite());
        addTest(getWritingTestSuite());
        TestSuite suite = new TestSuite();
        suite.setName("ChangeTrackingSuite");
        suite.addTest(buildChangeTrackingTest());
        suite.addTest(buildFieldAccessChangeTrackingTest());
        suite.addTest(buildEmployeeChangeTrackingTest());
        suite.addTest(buildDateChangeTrackingTest());
        addTest(suite);
    }

    public TestSuite getReadingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPAReadingTestSuite");
        suite.setDescription("This suite tests reading performance.");

        suite.addTest(new JPAReadAllAddressPerformanceComparisonTest(true));
        suite.addTest(new JPAReadAllAddressPerformanceComparisonTest(false));
        suite.addTest(new JPAReadAllAddressSimpleExpressionPerformanceComparisonTest(true));
        suite.addTest(new JPAReadAllAddressSimpleExpressionPerformanceComparisonTest(false));
        suite.addTest(new JPAReadAllAddressNamedQueryPerformanceComparisonTest(true));
        suite.addTest(new JPAReadAllAddressNamedQueryPerformanceComparisonTest(false));
        suite.addTest(new JPAReadAllEmployeePerformanceComparisonTest(true));
        suite.addTest(new JPAReadAllEmployeePerformanceComparisonTest(false));
        suite.addTest(new JPAReadAllEmployeeComplexExpressionPerformanceComparisonTest(true));
        suite.addTest(new JPAReadAllEmployeeComplexExpressionPerformanceComparisonTest(false));
        suite.addTest(new JPAReadAllEmployeeComplexDynamicExpressionPerformanceComparisonTest(true));
        suite.addTest(new JPAReadAllEmployeeCompletelyPerformanceComparisonTest(true));
        suite.addTest(new JPAReadAllEmployeeCompletelyPerformanceComparisonTest(false));
        suite.addTest(new JPAReadAllEmployeeCompletelyJoinedPerformanceComparisonTest(true));
        suite.addTest(new JPAReadAllEmployeeCompletelyJoinedPerformanceComparisonTest(false));
        suite.addTest(new JPAReadAllProjectPerformanceComparisonTest(true));
        suite.addTest(new JPAReadAllProjectPerformanceComparisonTest(false));
        suite.addTest(new JPAReadAllSmallProjectPerformanceComparisonTest(true));
        suite.addTest(new JPAReadAllSmallProjectPerformanceComparisonTest(false));
        suite.addTest(new JPAReadAllLargeProjectPerformanceComparisonTest(true));
        suite.addTest(new JPAReadAllLargeProjectPerformanceComparisonTest(false));
        suite.addTest(new JPAReadObjectAddressPerformanceComparisonTest());
        suite.addTest(new JPAReadObjectGetAddressPerformanceComparisonTest());
        suite.addTest(new JPAReadObjectAddressExpressionPerformanceComparisonTest(true));
        suite.addTest(new JPAReadObjectAddressExpressionPerformanceComparisonTest(false));
        suite.addTest(new JPAReadObjectAddressNamedQueryPerformanceComparisonTest());
        suite.addTest(new JPAReadObjectEmployeePerformanceComparisonTest());
        suite.addTest(new JPAReadObjectCompletelyEmployeePerformanceComparisonTest());

        return suite;
    }

    public TestSuite getWritingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPAWritingTestSuite");
        suite.setDescription("This suite tests uow/writing performance.");

        suite.addTest(new JPAInsertAddressPerformanceComparisonTest());
        suite.addTest(new JPAInsertDeleteAddressPerformanceComparisonTest());
        suite.addTest(new JPAMassInsertAddressPerformanceComparisonTest());
        suite.addTest(new JPAUpdateAddressPerformanceComparisonTest());
        suite.addTest(new JPAInsertEmployeePerformanceComparisonTest());
        suite.addTest(new JPAInsertDeleteEmployeePerformanceComparisonTest());
        suite.addTest(new JPAUpdateEmployeePerformanceComparisonTest());
        suite.addTest(new JPAComplexUpdateEmployeePerformanceComparisonTest());
        suite.addTest(new JPAMassInsertEmployeePerformanceComparisonTest());
        // number of management levels:
        int nLevels = 2;
        // mumber of direct employees each manager has
        int nDirects = 10;
        // true == insert; false == don't use sequencing.
        suite.addTest(new JPAMassInsertOrMergeEmployeeWithManagementLevelsPerformanceComparisonTest(true, false, nLevels, nDirects));
        // true == insert; true == use sequencing.
        suite.addTest(new JPAMassInsertOrMergeEmployeeWithManagementLevelsPerformanceComparisonTest(true, true, nLevels, nDirects));
        // false == merge; false == don't use sequencing.
        suite.addTest(new JPAMassInsertOrMergeEmployeeWithManagementLevelsPerformanceComparisonTest(false, false, nLevels, nDirects));
        // false == merge; true == use sequencing.
        suite.addTest(new JPAMassInsertOrMergeEmployeeWithManagementLevelsPerformanceComparisonTest(false, true, nLevels, nDirects));

        return suite;        
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
        // This line should be commented out when comparing against Hibernate as they do not have statement caching support.
        properties.put("eclipselink.jdbc.cache-statements", "true");
        return properties;
    }
    
    /**
     * Add a test to see if the provider is using change tracking.
     */
    public TestCase buildChangeTrackingTest() {
        TestCase test = new TestCase() {
            public void test() throws Exception {
                EntityManager manager = createEntityManager();
                manager.getTransaction().begin();
                Address address = (Address)manager.createQuery("Select a from Address a").getResultList().get(0);
                try {
                    Field field = Address.class.getDeclaredField("street");
                    field.setAccessible(true);
                    field.set(address, "Hastings");
                } finally {
                    manager.getTransaction().commit();
                    manager.close();
                }
                manager = createEntityManager();
                address = manager.find(Address.class, new Long(address.getId()));
                if (address.getStreet().equals("Hastings")) {
                    throwError("Change tracking detected the change (not used?).");
                } else {
                    throwError("Change tracking did not detect the change (used?).");
                }
            }
        };
        test.setName("TestChangeTracking");
        return test;
    }
    
    /**
     * Add a test to see if the provider is using change tracking.
     */
    public TestCase buildFieldAccessChangeTrackingTest() {
        TestCase test = new TestCase() {
            public void test() throws Exception {                
                EntityManager manager = createEntityManager();
                manager.getTransaction().begin();
                Address address = (Address)manager.createQuery("Select a from Address a").getResultList().get(0);
                try {
                    address.internalSetStreet("Hastings");
                } finally {
                    manager.getTransaction().commit();
                    manager.close();
                }
                manager = createEntityManager();
                address = manager.find(Address.class, new Long(address.getId()));
                if (address.getStreet().equals("Hastings")) {
                    throwError("Change tracking detected the change (not used?).");
                } else {
                    throwError("Change tracking did not detect the change (used?).");
                }
            }
        };
        test.setName("TestFieldAccessChangeTracking");
        return test;
    }
    
    /**
     * Add a test to see if the provider is using change tracking.
     */
    public TestCase buildEmployeeChangeTrackingTest() {
        TestCase test = new TestCase() {
            public void test() throws Exception {                
                EntityManager manager = createEntityManager();
                manager.getTransaction().begin();
                Employee employee = (Employee)manager.createQuery("Select e from Employee e").getResultList().get(0);
                try {
                    Field field = Employee.class.getDeclaredField("lastName");
                    field.setAccessible(true);
                    field.set(employee, "Hastings");
                } finally {
                    manager.getTransaction().commit();
                    manager.close();
                }
                manager = createEntityManager();
                employee = manager.getReference(Employee.class, new Long(employee.getId()));
                if (employee.getLastName().equals("Hastings")) {
                    throwError("Change tracking detected the change (not used?).");
                } else {
                    throwError("Change tracking did not detect the change (used?).");
                }
            }
        };
        test.setName("TestEmployeeChangeTracking");
        return test;
    }
    
    /**
     * Add a test to see if the provider is using change tracking.
     */
    public TestCase buildDateChangeTrackingTest() {
        TestCase test = new TestCase() {
            public void test() throws Exception {                
                EntityManager manager = createEntityManager();
                manager.getTransaction().begin();
                Employee employee = (Employee)manager.createQuery("Select e from Employee e").getResultList().get(0);
                try {
                    employee.getPeriod().getStartDate().setDate(7);
                } finally {
                    manager.getTransaction().commit();
                    manager.close();
                }
                manager = createEntityManager();
                employee = manager.getReference(Employee.class, new Long(employee.getId()));
                manager.refresh(employee);
                if (employee.getPeriod().getStartDate().getDate() == 7) {
                    throwError("Change tracking detected the change (not used?).");
                } else {
                    throwError("Change tracking did not detect the change (used?).");
                }
            }
        };
        test.setName("TestDateChangeTracking");
        return test;
    }
}