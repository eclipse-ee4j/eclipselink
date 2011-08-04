/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.performance;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.persistence.testing.models.jpa.performance2.*;
import org.eclipse.persistence.testing.tests.jpa.performance.misc.JPA2BootstrapPerformanceTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPA2ReadAllEmployeeCompletelyPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPA2ReadAllEmployeeComplexExpressionPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPA2ReadAllEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPA2ReadAllProjectPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPA2ReadObjectCompletelyEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPA2ComplexUpdateEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPA2InsertDeleteEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPA2InsertEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPA2MassInsertEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance.writing.JPA2UpdateEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedDriver;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.*;

/**
 * Performance tests that compare JPA 2 performance.
 */
public class JPA2PerformanceRegressionModel extends JPAPerformanceRegressionModel {
    
    public boolean isEmulated;

    public JPA2PerformanceRegressionModel() {
        setDescription("Performance tests that compare JPA 2 performance.");
    }

    public void addTests() {
        addTest(getReadingTestSuite());
        addTest(getWritingTestSuite());
        addTest(getMiscTestSuite());
    }

    public TestSuite getReadingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPA2ReadingTestSuite");
        suite.setDescription("This suite tests reading performance.");

        suite.addTest(new JPA2ReadAllEmployeePerformanceComparisonTest());
        suite.addTest(new JPA2ReadAllEmployeeComplexExpressionPerformanceComparisonTest());
        suite.addTest(new JPA2ReadAllEmployeeCompletelyPerformanceComparisonTest());
        suite.addTest(new JPA2ReadAllProjectPerformanceComparisonTest());
        suite.addTest(new JPA2ReadObjectCompletelyEmployeePerformanceComparisonTest());

        return suite;
    }

    public TestSuite getWritingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPA2WritingTestSuite");
        suite.setDescription("This suite tests uow/writing performance.");

        suite.addTest(new JPA2InsertEmployeePerformanceComparisonTest());
        suite.addTest(new JPA2InsertDeleteEmployeePerformanceComparisonTest());
        suite.addTest(new JPA2UpdateEmployeePerformanceComparisonTest());
        suite.addTest(new JPA2ComplexUpdateEmployeePerformanceComparisonTest());
        suite.addTest(new JPA2MassInsertEmployeePerformanceComparisonTest());
        
        return suite;        
    }

    public TestSuite getMiscTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPAMiscTestSuite");
        suite.setDescription("This suite tests miscellaneous performance.");

        suite.addTest(new JPA2BootstrapPerformanceTest());
        
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
        EntityManager em = getExecutor().createEntityManager();
        setupDatabase(em);
        
        em.getTransaction().begin();

        for (int j = 0; j < 100; j++) {
            Employee empInsert = new Employee();
            empInsert.setFirstName("Brendan");
            empInsert.setGender(Gender.Male);
            empInsert.setLastName("" + j + "");
            empInsert.setSalary(100000);
            EmploymentPeriod employmentPeriod = new EmploymentPeriod();
            employmentPeriod.setEndDate(1895, 1, 1);
            employmentPeriod.setStartDate(1901, 12, 31);
            empInsert.setPeriod(employmentPeriod);
            empInsert.setAddress(new Address());
            empInsert.getAddress().setCity("Nepean");
            empInsert.getAddress().setPostalCode("N5J2N5");
            empInsert.getAddress().setProvince("ON");
            empInsert.getAddress().setStreet("1111 Mountain Blvd. Floor 13, suite " + j);
            empInsert.getAddress().setCountry("Canada");
            empInsert.addPhoneNumber(new PhoneNumber("Work Fax", "613", "2255943"));
            empInsert.addPhoneNumber(new PhoneNumber("Home", "613", "2224599"));
            empInsert.setJobTitle(new JobTitle("CO-OP"));
            empInsert.addDegree("BComp");
            empInsert.addDegree("MComp");
            empInsert.addEmailAddress("work", "bren@oracle.com");
            empInsert.addEmailAddress("home", "bren@rogers.com");
            empInsert.addResponsibility("fix bugs");
            empInsert.addResponsibility("run tests");
            em.persist(empInsert);
        }

        for (int j = 0; j < 50; j++) {
            Project project = new SmallProject("Tracker", "tracker app");
            project.setName("Tracker");
            em.persist(project);
            project = new LargeProject();
            project.setName("Tracker");
            em.persist(project);
        }
        
        em.getTransaction().commit();
        em.close();
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
        getExecutor().setEntityManagerFactory(provider.createEntityManagerFactory("performance2", properties));
    }
}
