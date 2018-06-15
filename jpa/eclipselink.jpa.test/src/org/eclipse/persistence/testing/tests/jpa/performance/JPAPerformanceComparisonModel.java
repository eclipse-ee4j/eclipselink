/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.testing.tests.jpa.performance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;


import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedDriver;
import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaCache;
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
        suite.addTest(buildReadAllVsReadAllResultSet(Address.class));
        suite.addTest(buildReadAllVsReadAllResultSet(Employee.class));
        // ResultSetSideBySideTest useful for profiling regular vs ResultSet Access optimization.
/*        suite.addTest(new ResultSetSideBySideTest(Address.class, true));
        suite.addTest(new ResultSetSideBySideTest(Address.class, false));
        suite.addTest(new ResultSetSideBySideTest(Employee.class, true));
        suite.addTest(new ResultSetSideBySideTest(Employee.class, false));*/
        suite.addTest(buildBatchFetchTest());
        suite.addTest(buildLoadTest());
        addTest(suite);
        suite = new TestSuite();
        suite.setName("WritingSuite");
        suite.addTest(buildBatchWriteTest());
        suite.addTest(buildBatchUpdateTest());
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

        /** For emulated connection testing.
        try {
            Class.forName(getSession().getLogin().getDriverClassName());
        } catch (Exception ignore) {}
        properties.put("eclipselink.jdbc.driver", "org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedDriver");
        properties.put("eclipselink.jdbc.url", "emulate:" + getSession().getLogin().getConnectionString());
        properties.put("eclipselink.jdbc.user", getSession().getLogin().getUserName());
        properties.put("eclipselink.jdbc.password", getSession().getLogin().getPassword());*/

        //properties.put("eclipselink.jdbc.batch-writing", "JDBC");
        //properties.put("eclipselink.persistence-context.close-on-commit", "true");
        properties.put("eclipselink.sequencing.default-sequence-to-table", "true");
        properties.put("eclipselink.logging.level", getSession().getSessionLog().getLevelString());
        // properties.put("eclipselink.jdbc.cache-statements", "true");
        // properties.put("eclipselink.cache.shared.default", "false");
        return properties;
    }

    static class PerformanceComparisonTestCaseWithTargetClass extends PerformanceComparisonTestCase {
        Class targetClass;
        public PerformanceComparisonTestCaseWithTargetClass(Class targetClass) {
            super();
            this.targetClass = targetClass;
        }
    }
    /**
     * Add a test to see if the provider is using change tracking.
     */
    public TestCase buildReadAllVsReadAllResultSet(Class targetClass) {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCaseWithTargetClass(targetClass) {
            ReadAllQuery query;
            ReadAllQuery resultSetQuery;
            ReadAllQuery resultSetAccessQuery;

            public void setup() {
                this.query = new ReadAllQuery(targetClass);
                this.resultSetQuery = new ReadAllQuery(targetClass);
                this.resultSetQuery.setIsResultSetOptimizedQuery(true);
                this.resultSetAccessQuery = new ReadAllQuery(targetClass);
                this.resultSetAccessQuery.setIsResultSetAccessOptimizedQuery(true);

                boolean isSimple = createEntityManager().unwrap(Session.class).getDescriptor(targetClass).getObjectBuilder().isSimple();

                if (!getTests().isEmpty()) {
                    return;
                }

                PerformanceComparisonTestCase test;
                if (isSimple) {
                    // Read from result set.
                    test = new PerformanceComparisonTestCase() {
                        public void test() {
                            EntityManager em = createEntityManager();
                            ((JpaEntityManager)em).createQuery(resultSetQuery).getResultList();
                            em.close();
                        }
                    };
                    test.setName("ReadAllResultSet");
                    addTest(test);
                }

                // Read with result set access optimization.
                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        EntityManager em = createEntityManager();
                        ((JpaEntityManager)em).createQuery(resultSetAccessQuery).getResultList();
                        em.close();
                    }
                };
                test.setName("ReadAllResultSetAccess");
                addTest(test);

                // Read, no cache.
                test = new PerformanceComparisonTestCase() {
                    public void startTest() {
                        EntityManager em = createEntityManager();
                        createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setCacheIsolation(CacheIsolationType.ISOLATED);
                        createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_ALWAYS);
                        em.close();
                    }
                    public void test() {
                        EntityManager em = createEntityManager();
                        ((JpaEntityManager)em).createQuery(query).getResultList();
                        em.close();
                    }
                    public void endTest() {
                        EntityManager em = createEntityManager();
                        createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setCacheIsolation(CacheIsolationType.SHARED);
                        createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION);
                        em.close();
                    }
                };
                test.setName("ReadAllNoCache");
                addTest(test);

                if (isSimple) {
                    // Read from result set, no cache.
                    test = new PerformanceComparisonTestCase() {
                        public void startTest() {
                            EntityManager em = createEntityManager();
                            createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setCacheIsolation(CacheIsolationType.ISOLATED);
                            createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_ALWAYS);
                            em.close();
                        }
                        public void test() {
                            EntityManager em = createEntityManager();
                            ((JpaEntityManager)em).createQuery(resultSetQuery).getResultList();
                            em.close();
                        }
                        public void endTest() {
                            EntityManager em = createEntityManager();
                            createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setCacheIsolation(CacheIsolationType.SHARED);
                            createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION);
                            em.close();
                        }
                    };
                    test.setName("ReadAllResultSetNoCache");
                    addTest(test);
                }

                // Read with result set access optimization, no cache.
                test = new PerformanceComparisonTestCase() {
                    public void startTest() {
                        EntityManager em = createEntityManager();
                        createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setCacheIsolation(CacheIsolationType.ISOLATED);
                        createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_ALWAYS);
                        em.close();
                    }
                    public void test() {
                        EntityManager em = createEntityManager();
                        ((JpaEntityManager)em).createQuery(resultSetAccessQuery).getResultList();
                        em.close();
                    }
                    public void endTest() {
                        EntityManager em = createEntityManager();
                        createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setCacheIsolation(CacheIsolationType.SHARED);
                        createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION);
                        em.close();
                    }
                };
                test.setName("ReadAllResultSetAccessNoCache");
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
        test.setName("ReadAllVsReadAllResultSet(" + Helper.getShortClassName(targetClass) + ')');
        return test;
    }

    /**
     * Add a test to compare various batch fetching options.
     */
    public TestCase buildBatchFetchTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void setup() {
                createEntityManager().unwrap(Session.class).getDescriptor(Employee.class).setCacheIsolation(CacheIsolationType.ISOLATED);
                createEntityManager().unwrap(Session.class).getDescriptor(Employee.class).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_ALWAYS);
                createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setCacheIsolation(CacheIsolationType.ISOLATED);
                createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_ALWAYS);
                createEntityManager().unwrap(Session.class).getProject().setHasIsolatedClasses(true);

                if (!getTests().isEmpty()) {
                    return;
                }

                PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
                    public void test() {
                        testFetchQuery("findAllEmployeesBatch");
                    }
                };
                test.setName("BatchFetchTest");
                addTest(test);

                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        testFetchQuery("findAllEmployeesBatchEXISTS");
                    }
                };
                test.setName("EXISTSBatchFetchTest");
                addTest(test);

                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        testFetchQuery("findAllEmployeesBatchIN");
                    }
                };
                test.setName("INBatchFetchTest");
                addTest(test);

                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        testFetchQuery("findAllEmployeesJoin");
                    }
                };
                test.setName("JoinFetchTest");
                addTest(test);
            }

            public void test() throws Exception {
                testFetchQuery("findAllEmployees");
            }

            public void reset() {
                createEntityManager().unwrap(Session.class).getDescriptor(Employee.class).setCacheIsolation(CacheIsolationType.SHARED);
                createEntityManager().unwrap(Session.class).getDescriptor(Employee.class).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION);
                createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setCacheIsolation(CacheIsolationType.SHARED);
                createEntityManager().unwrap(Session.class).getDescriptor(Address.class).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION);
                createEntityManager().unwrap(Session.class).getProject().setHasIsolatedClasses(false);
            }

        };
        test.setName("BatchFetchTest");
        return test;
    }

    /**
     * Add a test to compare various batch fetching options.
     */
    public TestCase buildLoadTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            @Override
            public void setup() {

                if (!getTests().isEmpty()) {
                    return;
                }

                PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
                    public void test() {
                        testQuery("findAllEmployeesLoad");
                        ((JpaCache)getExecutor().getEntityManagerFactory().getCache()).clear();
                    }
                };
                test.setName("LoadTest");
                addTest(test);

                test = new PerformanceComparisonTestCase() {
                    @Override
                    public void startTest() {
                        AbstractSession session = getExecutor().getEntityManagerFactory().createEntityManager().unwrap(AbstractSession.class);
                        ((ReadAllQuery)session.getQuery("findAllEmployeesLoad")).getLoadGroup().setIsConcurrent(true);
                        session.setIsConcurrent(true);
                    }

                    @Override
                    public void endTest() {
                        AbstractSession session = getExecutor().getEntityManagerFactory().createEntityManager().unwrap(AbstractSession.class);
                        ((ReadAllQuery)session.getQuery("findAllEmployeesLoad")).getLoadGroup().setIsConcurrent(false);
                        session.setIsConcurrent(false);
                    }

                    @Override
                    public void test() {
                        testQuery("findAllEmployeesLoad");
                        ((JpaCache)getExecutor().getEntityManagerFactory().getCache()).clear();
                    }
                };
                test.setName("ConcurrentLoadTest");
                addTest(test);

                test = new PerformanceComparisonTestCase() {
                    @Override
                    public void startTest() {
                        AbstractSession session = getExecutor().getEntityManagerFactory().createEntityManager().unwrap(AbstractSession.class);
                        session.setIsConcurrent(true);
                    }

                    @Override
                    public void endTest() {
                        AbstractSession session = getExecutor().getEntityManagerFactory().createEntityManager().unwrap(AbstractSession.class);
                        session.setIsConcurrent(false);
                    }

                    @Override
                    public void test() {
                        testQuery("findAllEmployees");
                        ((JpaCache)getExecutor().getEntityManagerFactory().getCache()).clear();
                    }
                };
                test.setName("ConcurrentReadTest");
                addTest(test);
            }

            @Override
            public void test() throws Exception {
                testQuery("findAllEmployees");
                ((JpaCache)getExecutor().getEntityManagerFactory().getCache()).clear();
            }

            public void reset() {
            }

        };
        test.setName("LoadTest");
        return test;
    }

    /**
     * Execute the named query and traverse the results.
     */
    protected void testQuery(String query) {
        EntityManager em = getExecutor().createEntityManager();
        List<Employee> employees = em.createNamedQuery(query).getResultList();
        for (Employee employee : employees) {
            employee.toString();
        }
        em.close();
    }

    /**
     * Execute the named query and traverse the results.
     */
    protected void testFetchQuery(String query) {
        EntityManager em = getExecutor().createEntityManager();
        List<Employee> employees = em.createNamedQuery(query).getResultList();
        for (Employee employee : employees) {
            employee.getAddress().toString();
        }
        em.close();
    }

    /**
     * Execute the named query and traverse the results.
     */
    protected void testJPQL(String query) {
        EntityManager em = getExecutor().createEntityManager();
        em.createNamedQuery(query).getResultList().size();
        em.close();
    }

    /**
     * Add a test to compare various batch writing options.
     */
    public TestCase buildBatchWriteTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            int objects = 50;
            boolean originalCacheStatements;

            public void setup() {
                originalCacheStatements = getExecutor().createEntityManager().unwrap(ServerSession.class).getLogin().shouldCacheAllStatements();
                getExecutor().createEntityManager().unwrap(ServerSession.class).getLogin().cacheAllStatements();

                if (!getTests().isEmpty()) {
                    getTests().clear();
                }

                PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
                    public void test() {
                        EntityManager em = getExecutor().createEntityManager();
                        em.unwrap(ServerSession.class).getLogin().useBatchWriting();
                        try {
                            em.getTransaction().begin();
                            persistBatch(em);
                            em.getTransaction().commit();
                        } finally {
                            em.unwrap(ServerSession.class).getLogin().dontUseBatchWriting();
                            em.close();
                        }
                    }
                };
                test.setName("JDBCBatchTest");
                addTest(test);

                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        EntityManager em = getExecutor().createEntityManager();
                        em.unwrap(ServerSession.class).getLogin().dontBindAllParameters();
                        try {
                            em.getTransaction().begin();
                            persistBatch(em);
                            em.getTransaction().commit();
                        } finally {
                            em.unwrap(ServerSession.class).getLogin().bindAllParameters();
                            em.close();
                        }
                    }
                };
                test.setName("DynamicSQLTest");
                addTest(test);

                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        EntityManager em = getExecutor().createEntityManager();
                        em.unwrap(ServerSession.class).getLogin().dontCacheAllStatements();
                        try {
                            em.getTransaction().begin();
                            persistBatch(em);
                            em.getTransaction().commit();
                        } finally {
                            em.unwrap(ServerSession.class).getLogin().cacheAllStatements();
                            em.close();
                        }
                    }
                };
                test.setName("NoStatementCachingTest");
                addTest(test);

                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        EntityManager em = getExecutor().createEntityManager();
                        em.unwrap(ServerSession.class).getLogin().useBatchWriting();
                        em.unwrap(ServerSession.class).getLogin().dontBindAllParameters();
                        try {
                            em.getTransaction().begin();
                            persistBatch(em);
                            em.getTransaction().commit();
                        } finally {
                            em.unwrap(ServerSession.class).getLogin().dontUseBatchWriting();
                            em.unwrap(ServerSession.class).getLogin().bindAllParameters();
                            em.close();
                        }
                    }
                };
                test.setName("DynamicBatchTest");
                addTest(test);

                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        EntityManager em = getExecutor().createEntityManager();
                        em.unwrap(ServerSession.class).getLogin().useBatchWriting();
                        em.unwrap(ServerSession.class).getLogin().dontUseJDBCBatchWriting();
                        em.unwrap(ServerSession.class).getLogin().dontBindAllParameters();
                        try {
                            em.getTransaction().begin();
                            persistBatch(em);
                            em.getTransaction().commit();
                        } finally {
                            em.unwrap(ServerSession.class).getLogin().dontUseBatchWriting();
                            em.unwrap(ServerSession.class).getLogin().useJDBCBatchWriting();
                            em.unwrap(ServerSession.class).getLogin().bindAllParameters();
                            em.close();
                        }
                    }
                };
                test.setName("BufferedBatchTest");
                addTest(test);

                /*test = new PerformanceComparisonTestCase() {
                    public void test() {
                        EntityManager em = getExecutor().createEntityManager();
                        em.unwrap(ServerSession.class).getLogin().useBatchWriting();
                        DatabaseAccessor accessor =  null;
                        try {
                            em.getTransaction().begin();
                            persistBatch(em);
                            UnitOfWorkImpl uow = (UnitOfWorkImpl)em.unwrap(UnitOfWork.class);
                            uow.beginEarlyTransaction();
                            accessor = (DatabaseAccessor)uow.getAccessor();
                            accessor.setActiveBatchWritingMechanism(new DynamicParameterizedBatchWritingMechanism());
                            accessor.getActiveBatchWritingMechanism(uow.getParent()).setAccessor(accessor, uow.getParent());
                            em.getTransaction().commit();
                        } finally {
                            em.unwrap(ServerSession.class).getLogin().dontUseBatchWriting();
                            em.close();
                            if (accessor != null) {
                                accessor.setActiveBatchWritingMechanism(null);
                            }
                        }
                    }
                };
                test.setName("DynamicParameterizedBatchTest");
                addTest(test);

                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        EntityManager em = getExecutor().createEntityManager();
                        em.unwrap(ServerSession.class).getLogin().useBatchWriting();
                        DatabaseAccessor accessor =  null;
                        try {
                            em.getTransaction().begin();
                            persistBatch(em);
                            UnitOfWorkImpl uow = (UnitOfWorkImpl)em.unwrap(UnitOfWork.class);
                            uow.beginEarlyTransaction();
                            accessor = (DatabaseAccessor)uow.getAccessor();
                            accessor.setActiveBatchWritingMechanism(new DynamicParameterizedHybridBatchWritingMechanism());
                            accessor.getActiveBatchWritingMechanism(uow.getParent()).setAccessor(accessor, uow.getParent());
                            em.getTransaction().commit();
                        } finally {
                            em.unwrap(ServerSession.class).getLogin().dontUseBatchWriting();
                            em.close();
                            if (accessor != null) {
                                accessor.setActiveBatchWritingMechanism(null);
                            }
                        }
                    }
                };
                test.setName("DynamicParameterizedHybridBatchTest");
                addTest(test);*/
            }

            public void test() throws Exception {
                EntityManager em = getExecutor().createEntityManager();
                em.getTransaction().begin();
                persistBatch(em);
                em.getTransaction().commit();
                em.close();
            }

            public void persistBatch(EntityManager em) {
                for (int index = 0; index < this.objects; index++) {
                    Address address = new Address();
                    address.setStreet("100 Bank Street");
                    address.setProvince("ON");
                    address.setPostalCode("K2H8C5");
                    address.setCity("Ottawa");
                    address.setCountry("Canada");
                    em.persist(address);
                }
            }

            public void reset() {
                EntityManager em = getExecutor().createEntityManager();
                em.getTransaction().begin();
                em.createQuery("Delete from Address a where a.street = '100 Bank Street'").executeUpdate();
                em.getTransaction().commit();
                em.close();
                if (!originalCacheStatements) {
                    getExecutor().createEntityManager().unwrap(ServerSession.class).getLogin().dontCacheAllStatements();
                }
            }

        };
        test.setName("BatchWriteTest");
        return test;
    }


    /**
     * Add a test to compare various batch writing options.
     */
    public TestCase buildBatchUpdateTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            boolean originalCacheStatements;

            public void setup() {
                originalCacheStatements = getExecutor().createEntityManager().unwrap(ServerSession.class).getLogin().shouldCacheAllStatements();
                getExecutor().createEntityManager().unwrap(ServerSession.class).getLogin().cacheAllStatements();

                if (!getTests().isEmpty()) {
                    getTests().clear();
                }

                PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
                    public void test() {
                        EntityManager em = getExecutor().createEntityManager();
                        em.unwrap(ServerSession.class).getLogin().useBatchWriting();
                        em.getTransaction().begin();
                        persistBatch(em);
                        em.getTransaction().commit();
                        em.unwrap(ServerSession.class).getLogin().dontUseBatchWriting();
                        em.close();
                    }
                };
                test.setName("JDBCBatchUpdateTest");
                addTest(test);

                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        EntityManager em = getExecutor().createEntityManager();
                        em.unwrap(ServerSession.class).getLogin().useBatchWriting();
                        em.unwrap(ServerSession.class).getLogin().dontBindAllParameters();
                        em.getTransaction().begin();
                        persistBatch(em);
                        em.getTransaction().commit();
                        em.unwrap(ServerSession.class).getLogin().dontUseBatchWriting();
                        em.unwrap(ServerSession.class).getLogin().useJDBCBatchWriting();
                        em.unwrap(ServerSession.class).getLogin().bindAllParameters();
                        em.close();
                    }
                };
                test.setName("DynamicBatchUpdateTest");
                addTest(test);

                /*test = new PerformanceComparisonTestCase() {
                    public void test() {
                        EntityManager em = getExecutor().createEntityManager();
                        em.unwrap(ServerSession.class).getLogin().useBatchWriting();
                        em.getTransaction().begin();
                        persistBatch(em);
                        UnitOfWorkImpl uow = (UnitOfWorkImpl)em.unwrap(UnitOfWork.class);
                        uow.beginEarlyTransaction();
                        DatabaseAccessor accessor = (DatabaseAccessor)uow.getAccessor();
                        accessor.setActiveBatchWritingMechanism(new DynamicParameterizedBatchWritingMechanism());
                        accessor.getActiveBatchWritingMechanism(uow.getParent()).setAccessor(accessor, uow.getParent());
                        em.getTransaction().commit();
                        em.unwrap(ServerSession.class).getLogin().dontUseBatchWriting();
                        em.close();
                        accessor.setActiveBatchWritingMechanism(null);
                    }
                };
                test.setName("DynamicParameterizedBatchUpdateTest");
                addTest(test);

                test = new PerformanceComparisonTestCase() {
                    public void test() {
                        EntityManager em = getExecutor().createEntityManager();
                        em.unwrap(ServerSession.class).getLogin().useBatchWriting();
                        em.getTransaction().begin();
                        persistBatch(em);
                        UnitOfWorkImpl uow = (UnitOfWorkImpl)em.unwrap(UnitOfWork.class);
                        uow.beginEarlyTransaction();
                        DatabaseAccessor accessor = (DatabaseAccessor)uow.getAccessor();
                        accessor.setActiveBatchWritingMechanism(new DynamicParameterizedHybridBatchWritingMechanism());
                        accessor.getActiveBatchWritingMechanism(uow.getParent()).setAccessor(accessor, uow.getParent());
                        em.getTransaction().commit();
                        em.unwrap(ServerSession.class).getLogin().dontUseBatchWriting();
                        em.close();
                        accessor.setActiveBatchWritingMechanism(null);
                    }
                };
                test.setName("DynamicParameterizedHybridBatchUpdateTest");
                addTest(test);*/
            }

            public void test() throws Exception {
                EntityManager em = getExecutor().createEntityManager();
                em.getTransaction().begin();
                persistBatch(em);
                em.getTransaction().commit();
                em.close();
            }

            public void persistBatch(EntityManager em) {
                List<Employee> employees = em.createQuery("Select e from Employee e").getResultList();
                for (Employee employee : employees) {
                    if (employee.getGender().equals("Male")) {
                        employee.setFemale();
                    } else {
                        employee.setMale();
                    }
                }
            }

            public void reset() {
                if (!originalCacheStatements) {
                    getExecutor().createEntityManager().unwrap(ServerSession.class).getLogin().dontCacheAllStatements();
                }
            }

        };
        test.setName("BatchUpdateTest");
        return test;
    }

    /**
     * Useful for side-by-side profiling regular vs ResultSet Access optimization.
     */
    static class ResultSetSideBySideTest extends TestCase {
        Class targetClass;
        boolean withCache;
        ReadAllQuery query;
        ReadAllQuery resultSetQuery;
        int n = 1000;
        long time0;
        long time1;
        long time0square;
        long time1square;
        public ResultSetSideBySideTest(Class targetClass, boolean withCache) {
            super();
            this.targetClass = targetClass;
            this.withCache = withCache;
            setName("ResultSetOptimizationSideBySideTest(" + Helper.getShortClassName(targetClass) + ')' + (withCache ? " CACHE" : " NO CACHE"));
        }

        @Override
        protected void setup() {
            this.query = new ReadAllQuery(targetClass);
            this.query.setIsResultSetAccessOptimizedQuery(false);
            this.resultSetQuery = new ReadAllQuery(targetClass);
            this.resultSetQuery.setIsResultSetAccessOptimizedQuery(true);
            if (!withCache) {
                createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setCacheIsolation(CacheIsolationType.ISOLATED);
                createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_ALWAYS);
            }
            time0 = 0;
            time1 = 0;
            time0square = 0;
            time1square = 0;
        }

        @Override
        protected void test() {
            for (int i=0; i < n; i++) {
                long testTime0 = test0();
                long testTime1 = test1();
                if (testTime0 != 0l) {
                    time0 += testTime0;
                    time0square += testTime0 * testTime0;
                }
                if (testTime1 != 0l) {
                    time1 += testTime1;
                    time1square += testTime1 * testTime1;
                }
            }
            System.out.println(getName());
            System.out.println("No optimization time:");
            System.out.println(Long.toString(time0 / 1000000));
            System.out.println("Mean per test:");
            System.out.println(Double.toString((double)time0 / (n*1000000)));
            System.out.println("std dev:");
            System.out.println(Double.toString(Math.sqrt((double)time0square/n - (double)(time0/n)*(time0/n))/1000000));
            System.out.println("zero time tests:");
            System.out.println("ResultSet optimization time:");
            System.out.println(Long.toString(time1 / 1000000));
            System.out.println("Mean per test:");
            System.out.println(Double.toString((double)time1 / (n*1000000)));
            System.out.println("std dev:");
            System.out.println(Double.toString(Math.sqrt((double)time1square/n - (double)(time1/n)*(time1/n))/1000000));
            System.out.println("zero time tests:");
            System.out.println("No Optimization - Optimization:");
            System.out.println(Long.toString((time0 - time1)/1000000));
        }

        long test0() {
            EntityManager em = createEntityManager();
            long timeStart = System.nanoTime();
            List result = ((JpaEntityManager)em).createQuery(query).getResultList();
            long testTime = System.nanoTime() - timeStart;
            assertTrue(result.size() == 100);
            em.close();
            return testTime;
        }

        long test1() {
            EntityManager em = createEntityManager();
            long timeStart = System.nanoTime();
            List result = ((JpaEntityManager)em).createQuery(resultSetQuery).getResultList();
            long testTime = System.nanoTime() - timeStart;
            assertTrue(result.size() == 100);
            em.close();
            return testTime;
        }

        @Override
        public void reset() {
            if (!withCache) {
                createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setCacheIsolation(CacheIsolationType.SHARED);
                createEntityManager().unwrap(Session.class).getDescriptor(targetClass).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION);
            }
        }
    }
}
