/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved. This
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      05/01/2015 - 2.6.0 - Lukas Jungmann
//        - 455905: initial implementation
package org.eclipse.persistence.testing.tests.jpa.advanced.multitenant;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.MultitenantPolicy;
import org.eclipse.persistence.descriptors.SchemaPerMultitenantPolicy;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Customer;
import org.eclipse.persistence.testing.tests.feature.TestDataSource;

/**
 * Test for {@link SchemaPerMultitenantPolicy}. Uses two distinct DB schemas
 * Currently supported on MySQL only.
 *
 * @author lukas
 */
public class AdvancedMultiTenantSchemaJunitTest extends JUnitTestCase {

    private static boolean skipTest = false;

    /** Contains properties required for working with proxy datasource*/
    private static Properties emfProperties;
    /** Stored in emfProperties but also useful to explicitly switch real
     *  DB connection during test */
    private static ProxyDS proxyDataSource;
    /** schema names being used */
    private static String schema1, schema2;
    /** used to store test specific instance so it can be closed in tearDown */
    private EntityManagerFactory emf;

    public AdvancedMultiTenantSchemaJunitTest() {
        super();
    }

    public AdvancedMultiTenantSchemaJunitTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedMultiTenantSchemaJunitTest");
        suite.addTest(new AdvancedMultiTenantSchemaJunitTest("testSetup"));
        suite.addTest(new AdvancedMultiTenantSchemaJunitTest("testPolicyConfigurationDefault"));
        suite.addTest(new AdvancedMultiTenantSchemaJunitTest("testPolicyConfigurationCustom"));
        suite.addTest(new AdvancedMultiTenantSchemaJunitTest("testSequencing"));
        // keep this last, used to drop schema created in testSetup
        suite.addTest(new AdvancedMultiTenantSchemaJunitTest("testCleanup"));
        return suite;
    }

    @Override
    public String getPersistenceUnitName() {
        return "multi-tenant-schema-per-tenant";
    }

    @Override
    public void tearDown() {
        if (emf != null) {
            if (emf.isOpen()) {
                emf.close();
            }
        }
        if (schema1 != null) {
            try {
                getDatabaseSession().executeNonSelectingSQL("use " + schema1 + ";");
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     * test setup
     * -uses existing connection to obtain DB info - username, pwd and connection string/schema name
     * -creates new DB schema named '$existingSchema+"_MT"'
     * -creates 2 data sources (first points to original schema, second to the newly created one)
     *  and 1 proxy datasource (wraps original DSs and all DB requests are going through this DS)
     * -prepares tables in both DSs through the usage of proxy DS
     * -stores properties necessary for proper EMF creation in emfProperties field
     */
    public void testSetup() {
        if (!getPlatform().isMySQL()) {
            warning("this test is supported on MySQL only");
            return;
        }
        DatabaseSessionImpl databaseSession = getDatabaseSession();
        DatabaseLogin login = getDatabaseSession().getLogin();
        String connectionString = login.getConnectionString();
        int schemaIdx = connectionString.lastIndexOf('/');
        int queryIdx = connectionString.indexOf('?', schemaIdx);
        schema1 = connectionString.substring(schemaIdx + 1, queryIdx < 0 ? connectionString.length() : queryIdx);
        schema2 = schema1 + "_MT";
        String connectionStringMT = queryIdx < 0
                ? connectionString + "_MT"
                : connectionString.substring(0, queryIdx) + "_MT" + connectionString.substring(queryIdx);
        assertNotNull(schema1);
        assertNotNull(schema2);
        databaseSession.executeNonSelectingSQL("use " + schema1 + ";");
        try {
            databaseSession.executeNonSelectingSQL("drop schema " + schema2 + ";");
        } catch (Throwable t) {
            // ignore
        }
        try {
            databaseSession.executeNonSelectingSQL("create schema " + schema2 + ";");
        } catch (Throwable t) {
            // we may not have enough permissions to create additional schema,
            // in such case, skip all tests and log a warning
            skipTest = true;
            warning("Cannot create additional schema to run related tests.");
            databaseSession.logThrowable(SessionLog.WARNING, SessionLog.CONNECTION, t);
            return;
        } finally {
            databaseSession.logout();
        }

        Map<String, String> currentProps = JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName());
        TestDataSource ds1 = new TestDataSource(login.getDriverClassName(), connectionString, (Properties) login.getProperties().clone());
        TestDataSource ds2 = new TestDataSource(login.getDriverClassName(), connectionStringMT, (Properties) login.getProperties().clone());
        proxyDataSource = new ProxyDS(databaseSession, currentProps.get(PersistenceUnitProperties.JDBC_USER), currentProps.get(PersistenceUnitProperties.JDBC_PASSWORD));
        proxyDataSource.add(schema1, ds1);
        proxyDataSource.add(schema2, ds2);

        emfProperties = new Properties();
        emfProperties.putAll(currentProps);
        // need DS, not real JDBC Connections
        emfProperties.remove(PersistenceUnitProperties.JDBC_DRIVER);
        emfProperties.remove(PersistenceUnitProperties.JDBC_USER);
        emfProperties.remove(PersistenceUnitProperties.JDBC_URL);
        emfProperties.remove(PersistenceUnitProperties.JDBC_PASSWORD);
        emfProperties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, proxyDataSource);
        emfProperties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
        emfProperties.put(PersistenceUnitProperties.MULTITENANT_STRATEGY, "external");

        // prepare 1st tenant
        proxyDataSource.setCurrentDS(schema1);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(getPersistenceUnitName(), emfProperties);
        assertNotNull(emf);
        new AdvancedTableCreator().replaceTables(((EntityManagerFactoryImpl) emf).getServerSession());
        emf.close();

        // prepare 2nd tenant
        proxyDataSource.setCurrentDS(schema2);
        emf = Persistence.createEntityManagerFactory(getPersistenceUnitName(), emfProperties);
        assertNotNull(emf);
        new AdvancedTableCreator().replaceTables(((EntityManagerFactoryImpl) emf).getServerSession());
        emf.close();
    }

    public void testPolicyConfigurationDefault() {
        if (!getPlatform().isMySQL()) {
            warning("this test is supported on MySQL only");
            return;
        }
        if (skipTest) {
            warning("this test requires DB schema created in testSetup to be available.");
            return;
        }
        // default configuration: shared EMF = true, shared cache = false
        // strategy = 'external'
        emf = Persistence.createEntityManagerFactory(getPersistenceUnitName(), emfProperties);
        ServerSession session = ((EntityManagerFactoryImpl) emf).getServerSession();
        MultitenantPolicy policy = session.getProject().getMultitenantPolicy();

        assertNotNull("project MultitenantPolicy is null", policy);
        assertTrue("not SchemaPerMultitenantPolicy", policy.isSchemaPerMultitenantPolicy());
        assertTrue("not shared EMF", ((SchemaPerMultitenantPolicy) policy).shouldUseSharedEMF());
        assertFalse("shared cache", ((SchemaPerMultitenantPolicy) policy).shouldUseSharedCache());
        assertEquals(PersistenceUnitProperties.MULTITENANT_SCHEMA_PROPERTY_DEFAULT, ((SchemaPerMultitenantPolicy) policy).getContextProperty());
        assertEquals(EntityManagerProperties.MULTITENANT_SCHEMA_PROPERTY_DEFAULT, ((SchemaPerMultitenantPolicy) policy).getContextProperty());
        assertTrue("unknown context property", session.getMultitenantContextProperties().contains(PersistenceUnitProperties.MULTITENANT_SCHEMA_PROPERTY_DEFAULT));
    }

    public void testPolicyConfigurationCustom() {
        if (!getPlatform().isMySQL()) {
            warning("this test is supported on MySQL only");
            return;
        }
        if (skipTest) {
            warning("this test requires DB schema created in testSetup to be available.");
            return;
        }
        // custom configuration: shared EMF = false, shared cache = true
        Properties emfP = new Properties();
        emfP.putAll(emfProperties);
        emfP.put(PersistenceUnitProperties.MULTITENANT_SCHEMA_PROPERTY_DEFAULT, "not-shared");
        emfP.put(PersistenceUnitProperties.MULTITENANT_SHARED_EMF, "false");
        emfP.put(PersistenceUnitProperties.MULTITENANT_SHARED_CACHE, "true");
        emf = Persistence.createEntityManagerFactory(getPersistenceUnitName(), emfP);
        ServerSession session = ((EntityManagerFactoryImpl) emf).getServerSession();
        MultitenantPolicy policy = session.getProject().getMultitenantPolicy();

        assertNotNull("project MultitenantPolicy is null", policy);
        assertTrue("not SchemaPerMultitenantPolicy", policy.isSchemaPerMultitenantPolicy());
        assertFalse("shared EMF", ((SchemaPerMultitenantPolicy) policy).shouldUseSharedEMF());
        assertTrue("not shared cache", ((SchemaPerMultitenantPolicy) policy).shouldUseSharedCache());
        assertEquals(PersistenceUnitProperties.MULTITENANT_SCHEMA_PROPERTY_DEFAULT, ((SchemaPerMultitenantPolicy) policy).getContextProperty());
        assertEquals(EntityManagerProperties.MULTITENANT_SCHEMA_PROPERTY_DEFAULT, ((SchemaPerMultitenantPolicy) policy).getContextProperty());
        assertTrue("unknown context property", session.getMultitenantContextProperties().contains(PersistenceUnitProperties.MULTITENANT_SCHEMA_PROPERTY_DEFAULT));
    }

    /**
     * -uses shared EMF, proxy DS
     * -creates EM, 2 entities and persists them reaching default schema
     * -switches DB context to another schema
     * -creates EM, 2 entities and persists them reaching second schema
     *
     * sequencing in 2nd schema should start from the beginning,
     * not from some state in the 1st schema; both should grow by 1
     */
    public void testSequencing() {
        if (!getPlatform().isMySQL()) {
            warning("this test is supported on MySQL only");
            return;
        }
        if (skipTest) {
            warning("this test requires DB schema created in testSetup to be available.");
            return;
        }
        emf = Persistence.createEntityManagerFactory(getPersistenceUnitName(), emfProperties);
        Customer customer1 = new Customer();
        customer1.setLastName("Simpson");
        customer1.setFirstName("Bart");

        Customer customer1a = new Customer();
        customer1a.setLastName("Simpson");
        customer1a.setFirstName("Homer");

        proxyDataSource.setCurrentDS(schema1);
        Properties props1 = new Properties();
        props1.put(EntityManagerProperties.MULTITENANT_SCHEMA_PROPERTY_DEFAULT, "Simpsons");
        EntityManager em1 = emf.createEntityManager(props1);
        em1.getTransaction().begin();
        try {
            em1.persist(customer1);
            em1.persist(customer1a);
            em1.getTransaction().commit();
        } catch (Throwable t) {
            if (em1.getTransaction().isActive()) {
                em1.getTransaction().rollback();
            }
        } finally {
            em1.close();
        }
        int id1 = customer1.getId();
        int id1a = customer1a.getId();

        Customer customer2 = new Customer();
        customer2.setLastName("Cooper");
        customer2.setFirstName("Sheldon");

        Customer customer2a = new Customer();
        customer2a.setLastName("Hofstadter");
        customer2a.setFirstName("Leonard");

        proxyDataSource.setCurrentDS(schema2);
        Properties props2 = new Properties();
        props2.put(EntityManagerProperties.MULTITENANT_SCHEMA_PROPERTY_DEFAULT, "TBBT");
        EntityManager em2 = emf.createEntityManager(props2);
        em2.getTransaction().begin();
        try {
            em2.persist(customer2);
            em2.persist(customer2a);
            em2.getTransaction().commit();
        } catch (Throwable t) {
            if (em2.getTransaction().isActive()) {
                em2.getTransaction().rollback();
            }
        } finally {
            em2.close();
        }
        int id2 = customer2.getId();
        int id2a = customer2a.getId();

        // cannot rely on any value in schema1 as there may or may not be some tests run earlier
        // which would move the sequence or maybe failed before preallocating/moving the sequence
        // but what we know for sure is that IDs in the second schema should start from
        // the beginning (with 50) regardless of values in the first schema
        assertEquals(50, id2);
        assertEquals(51, id2a);
        // and IDs in both schemas should grow by 1
        assertEquals("IDs should grow by 1", id1 + 1, id1a);
        assertEquals("IDs should grow by 1", id2 + 1, id2a);
    }

    public void testCleanup() {
        try {
            getDatabaseSession(getPersistenceUnitName()).executeNonSelectingSQL("drop schema " + schema2 + ";");
        } catch (Throwable t) {
            // ignore
        }
    }

    static final class ProxyDS implements DataSource {

        private final Map<String, TestDataSource> datasources;
        private DataSource currentDS;
        private DatabaseSession session;
        private final String userName, pwd;

        public ProxyDS(DatabaseSession session, String userName, String pwd) {
            datasources = new HashMap<>();
            this.session = session;
            this.userName = userName;
            this.pwd = pwd;
        }

        public void add(String key, TestDataSource ds) {
            datasources.put(key, ds);
            currentDS = ds;
        }

        public void setCurrentDS(String key) {
            session.executeNonSelectingSQL("use " + key + "");
            currentDS = datasources.get(key);
        }

        public DataSource getCurrentDS() {
            return currentDS;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return getConnection(userName, pwd);
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return currentDS.getConnection(username, password);
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return currentDS.getLogWriter();
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            currentDS.setLogWriter(out);
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            currentDS.setLoginTimeout(seconds);
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return currentDS.getLoginTimeout();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return currentDS.getParentLogger();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return currentDS.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return currentDS.isWrapperFor(iface);
        }
    }
}
