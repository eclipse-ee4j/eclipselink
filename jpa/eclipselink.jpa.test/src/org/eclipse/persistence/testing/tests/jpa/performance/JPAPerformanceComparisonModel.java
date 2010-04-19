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
 package org.eclipse.persistence.testing.tests.jpa.performance;

//import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.Session;
//import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedDriver;
import org.eclipse.persistence.descriptors.ClassDescriptor;
//import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.helper.Helper;
//import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.*;

/**
 * Performance tests that compare JPA performance.
 */
public class JPAPerformanceComparisonModel extends TestModel {

    public JPAPerformanceComparisonModel() {
        setDescription("Performance tests that compare JPA performance.");
    }

    public void addTests() {
        TestSuite suite = new TestSuite();
        suite.setName("ReadingSuite");
        suite.addTest(buildReadAllVsReadAllResultSet());
        suite.addTest(buildBatchFetchTest());
        addTest(suite);
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
        
        // For emulated connection testing.
        try {
            Class.forName(getSession().getLogin().getDriverClassName());
        } catch (Exception ignore) {}
        properties.put("eclipselink.jdbc.driver", "org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedDriver");
        properties.put("eclipselink.jdbc.url", "emulate:" + getSession().getLogin().getConnectionString());
        properties.put("eclipselink.jdbc.user", getSession().getLogin().getUserName());
        properties.put("eclipselink.jdbc.password", getSession().getLogin().getPassword());

        //properties.put("eclipselink.jdbc.batch-writing", "JDBC");
        //properties.put("eclipselink.persistence-context.close-on-commit", "true");
        properties.put("eclipselink.logging.level", getSession().getSessionLog().getLevelString());
        // properties.put("eclipselink.jdbc.cache-statements", "true");
        // properties.put("eclipselink.cache.shared.default", "false");
        return properties;
    }
    
    /**
     * Add a test to see if the provider is using change tracking.
     */
    public TestCase buildReadAllVsReadAllResultSet() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            ReadAllQuery query;
            ReadAllQuery resultSetQuery;

            public void setup() {
                this.query = new ReadAllQuery(Address.class);
                this.resultSetQuery = new ReadAllQuery(Address.class);
                this.resultSetQuery.setIsResultSetOptimizedQuery(true);

                if (!getTests().isEmpty()) {
                    return;
                }
                
                // Read from result set.
                PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
                    public void test() {
                        EntityManager em = createEntityManager();
                        ((JpaEntityManager)em).createQuery(resultSetQuery).getResultList();
                        em.close();
                    }
                };
                test.setName("ReadAllResultSet");
                addTest(test);
                
                // Read, no cache.
                test = new PerformanceComparisonTestCase() {
                    public void startTest() {
                        EntityManager em = createEntityManager();
                        createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setIsIsolated(true);
                        createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_ALWAYS);
                        em.close();
                    }
                    public void test() {
                        EntityManager em = createEntityManager();
                        ((JpaEntityManager)em).createQuery(query).getResultList();
                        em.close();
                    }
                    public void endTest() {
                        EntityManager em = createEntityManager();
                        createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setIsIsolated(false);
                        createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION);
                        em.close();
                    }
                };
                test.setName("ReadAllNoCache");
                addTest(test);
                
                // Read from result set, no cache.
                test = new PerformanceComparisonTestCase() {
                    public void startTest() {
                        EntityManager em = createEntityManager();
                        createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setIsIsolated(true);
                        em.close();
                    }
                    public void test() {
                        EntityManager em = createEntityManager();
                        ((JpaEntityManager)em).createQuery(resultSetQuery).getResultList();
                        em.close();
                    }
                    public void endTest() {
                        EntityManager em = createEntityManager();
                        createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setIsIsolated(false);
                        em.close();
                    }
                };
                test.setName("ReadAllResultSetNoCache");
                addTest(test);
                
                /**
                // Raw JDBC only.
                test = new PerformanceComparisonTestCase() {
                    public void test() throws Exception {
                        ServerSession session = ((EntityManagerFactoryImpl)getExecutor().getEntityManagerFactory()).getServerSession();
                        Accessor accessor = session.getReadConnectionPool().acquireConnection();
                        java.sql.Connection connection = accessor.getConnection();
                        java.sql.PreparedStatement statement = connection.prepareStatement("SELECT ADDRESS_ID, P_CODE, STREET, PROVINCE, COUNTRY, CITY FROM P_ADDRESS");
                        java.sql.ResultSet result = statement.executeQuery();
                        while (result.next()) {
                            result.getLong(1);
                            result.getString(2);
                            result.getString(3);
                            result.getString(4);
                            result.getString(5);
                            result.getString(6);
                        }
                        result.close();
                        statement.close();
                        session.getReadConnectionPool().releaseConnection(accessor);
                    }
                };
                test.setName("ReadAllRawJDBC");
                addTest(test);
                
                // Direct JDBC build objects.
                test = new PerformanceComparisonTestCase() {
                    public void test() throws Exception {
                        ServerSession session = ((EntityManagerFactoryImpl)getExecutor().getEntityManagerFactory()).getServerSession();
                        Accessor accessor = session.getReadConnectionPool().acquireConnection();
                        java.sql.Connection connection = accessor.getConnection();
                        java.sql.PreparedStatement statement = connection.prepareStatement("SELECT ADDRESS_ID, P_CODE, STREET, PROVINCE, COUNTRY, CITY FROM P_ADDRESS");
                        java.sql.ResultSet result = statement.executeQuery();
                        List addresses = new ArrayList();
                        while (result.next()) {
                            Address address = new Address();
                            address.setId(result.getLong(1));
                            address.setPostalCode(result.getString(2));
                            address.setStreet(result.getString(3));
                            address.setProvince(result.getString(4));
                            address.setCountry(result.getString(5));
                            address.setCity(result.getString(6));
                            addresses.add(address);
                        }
                        result.close();
                        statement.close();
                        session.getReadConnectionPool().releaseConnection(accessor);
                    }
                };
                test.setName("ReadAllORMJDBC");
                addTest(test);
                
                // Direct JDBC, unwrap EM, build objects.
                test = new PerformanceComparisonTestCase() {
                    public void test() throws Exception {
                        EntityManager em = createEntityManager();
                        java.sql.Connection connection = em.unwrap(java.sql.Connection.class);
                        java.sql.PreparedStatement statement = connection.prepareStatement("SELECT ADDRESS_ID, P_CODE, STREET, PROVINCE, COUNTRY, CITY FROM P_ADDRESS");
                        java.sql.ResultSet result = statement.executeQuery();
                        List addresses = new ArrayList();
                        while (result.next()) {
                            Address address = new Address();
                            address.setId(result.getLong(1));
                            address.setPostalCode(result.getString(2));
                            address.setStreet(result.getString(3));
                            address.setProvince(result.getString(4));
                            address.setCountry(result.getString(5));
                            address.setCity(result.getString(6));
                            addresses.add(address);
                        }
                        result.close();
                        statement.close();
                        em.close();
                    }
                };
                test.setName("ReadAllUnwrapJDBC");
                addTest(test);*/
            }
            
            public void test() throws Exception {
                EntityManager em = createEntityManager();
                ((JpaEntityManager)em).createQuery(this.query).getResultList();
                em.close();
            }
        };
        test.setName("ReadAllVsReadAllResultSet");
        return test;
    }
    

    
    /**
     * Add a test to compare various batch fetching options.
     */
    public TestCase buildBatchFetchTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void setup() {
                createEntityManager().unwrap(Session.class).getDescriptor(Employee.class).setIsIsolated(true);
                createEntityManager().unwrap(Session.class).getDescriptor(Employee.class).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_ALWAYS);
                createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setIsIsolated(true);
                createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_ALWAYS);
                createEntityManager().unwrap(Session.class).getProject().setHasIsolatedClasses(true);
                
                if (!getTests().isEmpty()) {
                    return;
                }
                
                PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
                    public void test() {
                        testQuery("findAllEmployeesBatch");
                    }
                };
                test.setName("BatchFetchTest");
                addTest(test);
                
                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        testQuery("findAllEmployeesBatchEXISTS");
                    }
                };
                test.setName("EXISTSBatchFetchTest");
                addTest(test);
                
                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        testQuery("findAllEmployeesBatchIN");
                    }
                };
                test.setName("INBatchFetchTest");
                addTest(test);
                
                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        testQuery("findAllEmployeesJoin");
                    }
                };
                test.setName("JoinFetchTest");
                addTest(test);
            }
            
            public void test() throws Exception {
                testQuery("findAllEmployees");
            }
            
            public void reset() {
                createEntityManager().unwrap(Session.class).getDescriptor(Employee.class).setIsIsolated(false);
                createEntityManager().unwrap(Session.class).getDescriptor(Employee.class).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION);
                createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setIsIsolated(false);
                createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION);
                createEntityManager().unwrap(Session.class).getProject().setHasIsolatedClasses(false);
            }

        };
        test.setName("BatchFetchTest");
        return test;
    }
    
    /**
     * Execute the named query and traverse the results.
     */
    protected void testQuery(String query) {
        EntityManager em = getExecutor().createEntityManager();
        List<Employee> employees = em.createNamedQuery(query).getResultList();
        for (Employee employee : employees) {
            employee.getAddress().toString();
        }
        em.close();
    }
}
