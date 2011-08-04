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
 package org.eclipse.persistence.testing.tests.jpa.performance;

import java.lang.reflect.Field;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.tests.jpa.performance.misc.JPABootstrapPerformanceTest;
import org.eclipse.persistence.testing.tests.jpa.performance.misc.JPAMetadataPerformanceTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadAllAddressNamedQueryPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadAllAddressPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadAllAddressSimpleExpressionPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadAllEmployeeCompletelyJoinedPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadAllEmployeeCompletelyPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadAllEmployeeComplexDynamicExpressionPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadAllEmployeeComplexExpressionPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadAllEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadAllLargeProjectPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadAllProjectPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadAllSmallProjectPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadObjectAddressExpressionPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadObjectAddressNamedQueryPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadObjectAddressPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadObjectCompletelyEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadObjectEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadObjectGetAddressPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAComplexUpdateEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAInsertAddressPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAInsertDeleteAddressPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAInsertDeleteEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAInsertEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAMassInsertAddressPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAMassInsertEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAMassInsertOrMergeEmployeeWithManagementLevelsPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAUpdateAddressPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAUpdateEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedDriver;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.*;

/**
 * Performance tests that compare JPA performance.
 */
public class JPAPerformanceRegressionModel extends TestModel {
    
    public boolean isEmulated;

    public JPAPerformanceRegressionModel() {
        setDescription("Performance tests that compare JPA performance.");
    }

    public void addTests() {
        addTest(getReadingTestSuite());
        addTest(getWritingTestSuite());
        addTest(getMiscTestSuite());
        
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

    public void setupDatabase(EntityManager manager) {
        // Create schema using session from entity manager to create sequences correctly.
        try {
            // Create schema.
            new EmployeeTableCreator().replaceTables(((JpaEntityManager)manager).getServerSession());
        } catch (ClassCastException cast) {
            // Create using DatabaseSession if not EclipseLink JPA.
            new EmployeeTableCreator().replaceTables(getDatabaseSession());
        }
    }

    public TestSuite getMiscTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPAMiscTestSuite");
        suite.setDescription("This suite tests miscellaneous performance.");

        suite.addTest(new JPABootstrapPerformanceTest());
        suite.addTest(new JPAMetadataPerformanceTest());
        
        return suite;        
    }

    /**
     * Create/populate database.
     */
    public void setup() {
        /*
        // Setup DataSource for apples to apples comparison (otherwise we crush them).
        try {
            System.setProperty("java.naming.factory.initial", "org.eclipse.persistence.testing.framework.naming.InitialContextFactoryImpl");
            oracle.jdbc.pool.OracleDataSource datasource = new oracle.jdbc.pool.OracleDataSource();
            datasource.setURL(getSession().getLogin().getConnectionString());
            datasource.setUser(getSession().getLogin().getUserName());
            datasource.setPassword(getSession().getLogin().getPassword());
            datasource.setConnectionCachingEnabled(true);
            java.util.Properties properties = new java.util.Properties();
            properties.setProperty("MinLimit", "5"); 
            properties.setProperty("MaxLimit", "100");
            properties.setProperty("InitialLimit", "1");
            properties.setProperty("MaxStatementsLimit", "50");
            datasource.setConnectionCacheProperties(properties);

            new javax.naming.InitialContext().bind("datasource", datasource);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }*/
        
        setupProvider();
        getSession().logMessage(getExecutor().getEntityManagerFactory().getClass().toString());
        System.out.println(getExecutor().getEntityManagerFactory().getClass().toString());
        // Populate database.
        EmulatedDriver.emulate = false;
        EntityManager manager = getExecutor().createEntityManager();
        setupDatabase(manager);
        
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

        for (int j = 0; j < 50; j++) {
            Project project = new SmallProject();
            project.setName("Tracker");
            manager.persist(project);
            project = new LargeProject();
            project.setName("Tracker");
            manager.persist(project);
        }
        
        manager.getTransaction().commit();
        manager.close();
        EmulatedDriver.emulate = true;
    }
    
    /**
     * Setup the JPA provider.
     */
    public void setupProvider() {
        if (org.eclipse.persistence.testing.framework.junit.JUnitTestCase.isOnServer()) {
            return;
        }
        // Configure provider to be EclipseLink.
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
        
        // For DataSource testing.
        //properties.put("javax.persistence.nonJtaDataSource", "datasource");

        // For JSE testing.
        properties.put("eclipselink.jdbc.driver", getSession().getLogin().getDriverClassName());
        properties.put("eclipselink.jdbc.url", getSession().getLogin().getConnectionString());
        properties.put("eclipselink.jdbc.user", getSession().getLogin().getUserName());
        properties.put("eclipselink.jdbc.password", getSession().getLogin().getPassword());
        
        if (this.isEmulated) {
            // For emulated connection testing.
            try {
                Class.forName(getSession().getLogin().getDriverClassName());
            } catch (Exception ignore) {}
            properties.put("eclipselink.jdbc.driver", "org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedDriver");
            properties.put("eclipselink.jdbc.url", "emulate:" + getSession().getLogin().getConnectionString());
            LoadBuildSystem.loadBuild.loginChoice = "emulate:" + getSession().getLogin().getConnectionString();
        }

        //properties.put("eclipselink.jdbc.batch-writing", "JDBC");
        //properties.put("eclipselink.persistence-context.close-on-commit", "true");
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
            @SuppressWarnings("deprecation")
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
