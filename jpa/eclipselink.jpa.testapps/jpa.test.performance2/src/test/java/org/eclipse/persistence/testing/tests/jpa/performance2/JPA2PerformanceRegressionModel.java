/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
 package org.eclipse.persistence.testing.tests.jpa.performance2;

import jakarta.persistence.EntityManager;
import jakarta.persistence.spi.PersistenceProvider;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.performance2.Address;
import org.eclipse.persistence.testing.models.jpa.performance2.Employee;
import org.eclipse.persistence.testing.models.jpa.performance2.EmployeeTableCreator;
import org.eclipse.persistence.testing.models.jpa.performance2.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.performance2.Gender;
import org.eclipse.persistence.testing.models.jpa.performance2.JobTitle;
import org.eclipse.persistence.testing.models.jpa.performance2.LargeProject;
import org.eclipse.persistence.testing.models.jpa.performance2.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.performance2.Project;
import org.eclipse.persistence.testing.models.jpa.performance2.SmallProject;
import org.eclipse.persistence.testing.tests.jpa.performance.JPAPerformanceRegressionModel;
import org.eclipse.persistence.testing.tests.jpa.performance2.misc.JPA2BootstrapPerformanceTest;
import org.eclipse.persistence.testing.tests.jpa.performance2.reading.JPA2ReadAllEmployeeCompletelyPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance2.reading.JPA2ReadAllEmployeeComplexExpressionPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance2.reading.JPA2ReadAllEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance2.reading.JPA2ReadAllProjectPerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance2.reading.JPA2ReadObjectCompletelyEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance2.writing.JPA2ComplexUpdateEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance2.writing.JPA2InsertDeleteEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance2.writing.JPA2InsertEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance2.writing.JPA2MassInsertEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.jpa.performance2.writing.JPA2UpdateEmployeePerformanceComparisonTest;
import org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedDriver;

import java.util.Map;

/**
 * Performance tests that compare JPA 2 performance.
 */
public class JPA2PerformanceRegressionModel extends JPAPerformanceRegressionModel {

    public boolean isEmulated;

    public JPA2PerformanceRegressionModel() {
        setDescription("Performance tests that compare Jakarta Persistence 2 performance.");
    }

    @Override
    public void addTests() {
        addTest(getReadingTestSuite());
        addTest(getWritingTestSuite());
        addTest(getMiscTestSuite());
    }

    @Override
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

    @Override
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

    @Override
    public TestSuite getMiscTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPAMiscTestSuite");
        suite.setDescription("This suite tests miscellaneous performance.");

        suite.addTest(new JPA2BootstrapPerformanceTest());

        return suite;
    }

    @Override
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
    @Override
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
    @Override
    public void setupProvider() {
        if (JUnitTestCase.isOnServer()) {
            return;
        }
        // Configure provider to be EclipseLink.
        String providerClass = "org.eclipse.persistence.jpa.PersistenceProvider";
        PersistenceProvider provider = null;
        try {
            provider = (PersistenceProvider)Class.forName(providerClass).getConstructor().newInstance();
        } catch (Exception error) {
            throw new TestProblemException("Failed to create persistence provider.", error);
        }
        Map<String, Object> properties = getPersistenceProperties();
        getExecutor().setEntityManagerFactory(provider.createEntityManagerFactory("performance2", properties));
    }
}
