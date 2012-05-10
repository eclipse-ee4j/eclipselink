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
 *     James Sutherland - initial impl
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.performance;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.tests.jpa.performance.concurrent.JPAAddPhoneConcurrencyComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.concurrent.JPAComplexUpdateEmployeeConcurrencyComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.concurrent.JPAUpdateAddressConcurrencyComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.concurrent.JPAUpdateEmployeeConcurrencyComparisonTest;
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
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAInsertAddressPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAInsertDeleteAddressPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAInsertDeleteEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPAInsertEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.performance.concurrent.BasicMathConcurrentTest;
import org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedDriver;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.*;

/**
 * Performance tests that compare JPA concurrency.
 */
public class JPAConcurrencyComparisonModel extends TestModel {

    public JPAConcurrencyComparisonModel() {
        setDescription("Performance tests that compare JPA concurrency.");
    }

    public void addTests() {
        addTest(new BasicMathConcurrentTest());
        addTest(getReadingTestSuite());
        addTest(getWritingTestSuite());
    }

    public TestSuite getReadingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPAConcurrentReadingTestSuite");
        suite.setDescription("This suite tests reading concurrency.");

        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllAddressPerformanceComparisonTest(true)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllAddressPerformanceComparisonTest(false)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllAddressSimpleExpressionPerformanceComparisonTest(true)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllAddressNamedQueryPerformanceComparisonTest(true)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllEmployeePerformanceComparisonTest(true)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllEmployeePerformanceComparisonTest(false)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllEmployeeComplexExpressionPerformanceComparisonTest(true)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllEmployeeComplexDynamicExpressionPerformanceComparisonTest(true)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllEmployeeCompletelyPerformanceComparisonTest(true)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllEmployeeCompletelyPerformanceComparisonTest(false)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllEmployeeCompletelyJoinedPerformanceComparisonTest(true)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllEmployeeCompletelyJoinedPerformanceComparisonTest(false)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllProjectPerformanceComparisonTest(true)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllProjectPerformanceComparisonTest(false)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllSmallProjectPerformanceComparisonTest(true)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllSmallProjectPerformanceComparisonTest(false)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllLargeProjectPerformanceComparisonTest(true)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadAllLargeProjectPerformanceComparisonTest(false)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadObjectAddressPerformanceComparisonTest()));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadObjectGetAddressPerformanceComparisonTest()));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadObjectAddressExpressionPerformanceComparisonTest(true)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadObjectAddressExpressionPerformanceComparisonTest(false)));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadObjectAddressNamedQueryPerformanceComparisonTest()));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadObjectEmployeePerformanceComparisonTest()));
        suite.addTest(new ConcurrencyTestAdapter(new JPAReadObjectCompletelyEmployeePerformanceComparisonTest()));

        return suite;
    }

    public TestSuite getWritingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPAConcurrentWritingTestSuite");
        suite.setDescription("This suite tests writing concurrency.");

        suite.addTest(new ConcurrencyTestAdapter(new JPAInsertAddressPerformanceComparisonTest()));
        suite.addTest(new ConcurrencyTestAdapter(new JPAInsertDeleteAddressPerformanceComparisonTest()));
        suite.addTest(new ConcurrencyTestAdapter(new JPAInsertEmployeePerformanceComparisonTest()));
        suite.addTest(new ConcurrencyTestAdapter(new JPAInsertDeleteEmployeePerformanceComparisonTest()));
        suite.addTest(new JPAUpdateAddressConcurrencyComparisonTest());
        suite.addTest(new JPAUpdateEmployeeConcurrencyComparisonTest());
        suite.addTest(new JPAComplexUpdateEmployeeConcurrencyComparisonTest());
        suite.addTest(new JPAAddPhoneConcurrencyComparisonTest());

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
        properties.put("eclipselink.jdbc.write-connections.min", "32");
        properties.put("eclipselink.jdbc.write-connections.max", "32");
        properties.put("eclipselink.jdbc.read-connections.min", "32");
        properties.put("eclipselink.jdbc.read-connections.max", "32");
        //properties.put("eclipselink.cache.shared.default", "false");
        /*
        // For emulated connection testing.
        try {
            Class.forName(getSession().getLogin().getDriverClassName());
        } catch (Exception ignore) {}
        properties.put("eclipselink.jdbc.driver", "org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedDriver");
        properties.put("eclipselink.jdbc.url", "emulate:" + getSession().getLogin().getConnectionString());
        properties.put("eclipselink.jdbc.user", getSession().getLogin().getUserName());
        properties.put("eclipselink.jdbc.password", getSession().getLogin().getPassword());
        properties.put("eclipselink.jdbc.write-connections.min", "32");
        properties.put("eclipselink.jdbc.write-connections.max", "32");
        properties.put("eclipselink.jdbc.read-connections.min", "32");
        properties.put("eclipselink.jdbc.read-connections.max", "32");*/

        properties.put("eclipselink.jdbc.batch-writing", "JDBC");
        //properties.put("eclipselink.persistence-context.close-on-commit", "true");
        properties.put("eclipselink.logging.level", getSession().getSessionLog().getLevelString());
        // This line should be commented out when comparing against Hibernate as they do not have statement caching support.
        properties.put("eclipselink.jdbc.cache-statements", "true");
        return properties;
    }    
}
