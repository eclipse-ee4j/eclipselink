/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2014, 2017 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     11/04/2014 - Rick Curtis
//       - 450010 : Add java se test bucket
//     12/19/2014 - Dalia Abo Sheasha
//       - 454917 : Added a test to use the IDENTITY strategy to generate values
//     02/16/2017 - Jody Grassel
//       - 512255 : Eclipselink JPA/Auditing capablity in EE Environment fails with JNDI name parameter type
//     04/24/2017-2.6 Jody Grassel
//       - 515712: ServerSession numberOfNonPooledConnectionsUsed can become invalid when Exception is thrown connecting accessor
package org.eclipse.persistence.jpa.test.basic;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.sql.DataSource;

import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.connwrapper.DriverWrapper;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.basic.model.Dog;
import org.eclipse.persistence.jpa.test.basic.model.Employee;
import org.eclipse.persistence.jpa.test.basic.model.Person;
import org.eclipse.persistence.jpa.test.basic.model.XmlFish;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.SQLListener;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.server.ConnectionPolicy;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestBasicPersistence {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { Dog.class, XmlFish.class, Person.class, Employee.class }, 
            properties = {@Property(name = "eclipselink.cache.shared.default", value = "false")}, 
            mappingFiles = { "META-INF/fish-orm.xml" })
    private EntityManagerFactory emf;

    @SQLListener
    List<String> _sql;

    private static final int rmiPort;
    private static final String dsName = "mockDataSource";
    private static final BogusDataSource mockDataSource = new BogusDataSource("tmpDataSourceImp getConnection called");
    private static Registry rmiRegistry = null;
    private static JMXConnectorServer connector = null;

    static {
        int rmiPortVal = 1099;
        
        String rmiPortProp = System.getProperty("rmi.port");
        if (!(rmiPortProp == null || rmiPortProp.isEmpty())) {
            try {
                rmiPortVal = new Integer(rmiPortProp);
            } catch (NumberFormatException nfe) {
                // Use default value.
            }
        }
            
        rmiPort = rmiPortVal;
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        rmiRegistry = LocateRegistry.createRegistry(rmiPort);
        
        // Create and Bind Mock Data Source
        rmiRegistry.bind(dsName, mockDataSource);       

        connector = JMXConnectorServerFactory.newJMXConnectorServer(new JMXServiceURL("service:jmx:rmi://localhost:" + rmiPort),
                new HashMap<String, Object>(), ManagementFactory.getPlatformMBeanServer());
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (rmiRegistry != null) {
            rmiRegistry.unbind(dsName);
        } 

        if (connector != null) {
            connector.stop();
        }
    }

    @Test
    public void activeTransaction() {
        Assert.assertNotNull(emf);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @Test
    public void testNonNullEmf() {
        Assert.assertNotNull(emf);
    }

    @Test
    public void persistTest() {
        if (emf == null)
            return;
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Person p = new Person();
            Dog d = new Dog("Bingo");
            p.setDog(d);
            d.setOwner(p);

            em.persist(p);
            em.persist(d);

            em.persist(new XmlFish());
            em.getTransaction().commit();
            em.clear();

            Dog foundDog = em.find(Dog.class, d.getId());
            foundDog.getOwner();
            Assert.assertTrue(_sql.size() > 0);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void identityStrategyTest() {
        if (emf == null)
            return;
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Employee e = new Employee();
            em.persist(e);
            em.getTransaction().commit();
            em.clear();

            Employee foundEmp = em.find(Employee.class, e.getId());
            Assert.assertNotNull(foundEmp);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testNonJTADataSourceOverride() throws Exception {
        if (emf == null)
            return;

        InitialContext ic = new InitialContext();
        Assert.assertNotNull(ic.lookup(dsName));

        EntityManager em = null;
        boolean pass = false;
        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, dsName);
        properties.put("eclipselink.jdbc.exclusive-connection.mode", "Always");

        try {
            em = emf.createEntityManager(properties);
            em.clear();
            em.find(Dog.class, 1);
        } catch (RuntimeException expected) {
            pass = expected.getMessage().indexOf("tmpDataSourceImp getConnection called") != -1;
            if (!pass) {
                throw expected;
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        Assert.assertTrue("Non JTA datasource was not set or accessed as expected through map of properties", pass);
    }

    /*
     * Verify that the number of non pooled connections used is accounted for accurately in regular non-error scenario usage.
     */
    @Test
    public void testNonpooledConnection() throws Exception {
        System.out.println("********************");
        System.out.println("*BEGIN testNonpooledConnection()");
        if (emf == null) 
            return;

        final EntityManagerFactoryImpl emfi = emf.unwrap(EntityManagerFactoryImpl.class);
        Assert.assertNotNull(emfi);

        // Create an em with a unpooled connection policy, idea taken from EntityManagerJUnitTestSuite.testNonPooledConnection()
        final ServerSession ss = emfi.getServerSession();

        // Clone the connection policy and set the pool name to null to emulate using non-pooled connections
        final ConnectionPolicy connectionPolicy = (ConnectionPolicy)ss.getDefaultConnectionPolicy().clone();
        connectionPolicy.setLogin(ss.getLogin());
        connectionPolicy.setPoolName(null);

        final Map<String, Object> properties = new HashMap<>();
        properties.put(EntityManagerProperties.CONNECTION_POLICY, connectionPolicy);

        final int initialNonPooledConnections = ss.getNumberOfNonPooledConnectionsUsed();
        final int maxNonPooledConnections = ss.getMaxNumberOfNonPooledConnections();
        System.out.println("initialNonPooledConnections = " + initialNonPooledConnections);
        System.out.println("maxNonPooledConnections = " + maxNonPooledConnections);
        if (maxNonPooledConnections != -1 && initialNonPooledConnections >= maxNonPooledConnections) {
            Assert.fail("initialNonPooledConnections >= maxNonPooledConnections, other tests may be leaking.");
        }

        EntityManager em = emf.createEntityManager(properties);
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();

            Person p = new Person();
            Dog d = new Dog("Bingo");
            p.setDog(d);
            d.setOwner(p);

            em.persist(p);
            em.persist(d);
            em.persist(new XmlFish());

            em.flush();

            int nonPooledConnectionsAfterFlush = ss.getNumberOfNonPooledConnectionsUsed();
            System.out.println("nonPooledConnectionsAfterFlush = " + nonPooledConnectionsAfterFlush);
            Assert.assertTrue("Test problem: connection should be not pooled", em.unwrap(UnitOfWork.class).getParent().getAccessor().getPool() == null);
            Assert.assertEquals(initialNonPooledConnections + 1, nonPooledConnectionsAfterFlush);

            et.commit();
            em.clear();

            int nonPooledConnectionsAfterCommit = ss.getNumberOfNonPooledConnectionsUsed();
            System.out.println("nonPooledConnectionsAfterCommit = " + nonPooledConnectionsAfterCommit);
            Assert.assertEquals(initialNonPooledConnections, nonPooledConnectionsAfterCommit);

            Dog foundDog = em.find(Dog.class, d.getId());
            foundDog.getOwner();
            Assert.assertTrue(_sql.size() > 0);

            int nonPooledConnectionsAfterFind = ss.getNumberOfNonPooledConnectionsUsed();
            System.out.println("nonPooledConnectionsAfterFind = " + nonPooledConnectionsAfterFind);
            Assert.assertEquals(initialNonPooledConnections, nonPooledConnectionsAfterFind);
        } finally {
            if (et.isActive()) {
                et.rollback();
            }
            em.close();

            System.out.println("*END testNonpooledConnection()");
        }
    }

    @Test
    public void testNonpooledConnectionWithErrorOnAcquireConnection() throws Exception {
        System.out.println("********************");
        System.out.println("*BEGIN testNonpooledConnectionWithErrorOnAcquireConnection()");
        if (emf == null) 
            return;

        final EntityManagerFactoryImpl emfi = emf.unwrap(EntityManagerFactoryImpl.class);
        Assert.assertNotNull(emfi);

        // Create an em with a unpooled connection policy, idea taken from EntityManagerJUnitTestSuite.testNonPooledConnection()
        final ServerSession ss = emfi.getServerSession();

        // Set up the DriverWrapper/ConnectionWrapper so we can emulate database connection failure
        // cache the original driver name and connection string.
        try {
            setupDriverWrapper(ss);

            // Clone the connection policy and set the pool name to null to emulate using non-pooled connections
            final ConnectionPolicy connectionPolicy = (ConnectionPolicy)ss.getDefaultConnectionPolicy().clone();
            connectionPolicy.setLogin(ss.getLogin());
            connectionPolicy.setPoolName(null);

            final Map<String, Object> properties = new HashMap<>();
            properties.put(EntityManagerProperties.CONNECTION_POLICY, connectionPolicy);

            // Validate against initial non-pooled connection count
            final int initialNonPooledConnections = ss.getNumberOfNonPooledConnectionsUsed();
            final int maxNonPooledConnections = ss.getMaxNumberOfNonPooledConnections();
            System.out.println("initialNonPooledConnections = " + initialNonPooledConnections);
            System.out.println("maxNonPooledConnections = " + maxNonPooledConnections);
            if (maxNonPooledConnections != -1 && initialNonPooledConnections >= maxNonPooledConnections) {
                Assert.fail("initialNonPooledConnections >= maxNonPooledConnections, other tests may be leaking.");
            }

            EntityManager em = emf.createEntityManager(properties);
            EntityTransaction et = em.getTransaction();

            try {
                Person p = new Person();
                Dog d = new Dog("Bingo");
                p.setDog(d);
                d.setOwner(p);

                et.begin();
                final int nonPooledConnectionsBeforePersist = ss.getNumberOfNonPooledConnectionsUsed();
                System.out.println("nonPooledConnectionsBeforePersist = " + nonPooledConnectionsBeforePersist);

                em.persist(p);
                em.persist(d);
                em.persist(new XmlFish());

                final int nonPooledConnectionsBeforeFlush = ss.getNumberOfNonPooledConnectionsUsed();
                System.out.println("nonPooledConnectionsBeforeFlush = " + nonPooledConnectionsBeforeFlush);
                try {
                    DriverWrapper.breakNewConnections(); // .breakDriver(); // would be ideal, but results in bug #515961
                    em.flush();
                    Assert.fail("No PersistenceException was thrown.");
                } catch (PersistenceException pe) {
                    // Expected
                } finally {
                    DriverWrapper.repairAll();
                }

                final int nonPooledConnectionsAfterFlush = ss.getNumberOfNonPooledConnectionsUsed();
                System.out.println("nonPooledConnectionsAfterFlush = " + nonPooledConnectionsAfterFlush);
                Assert.assertEquals("nonPooledConnectionsBeforeFlush == nonPooledConnectionsAfterFlush", nonPooledConnectionsBeforeFlush, nonPooledConnectionsAfterFlush);

                try {
                et.rollback();
                } catch (Throwable t) {
                    // Some databases such as mysql may see the Connection as still set to autocommit=true, and
                    // the DriverWrapper was set to make new connections broken, but the real Connection is
                    // still established, but being "Broken" prevents the Connection from being set autocommit=false.
                }

                final int nonPooledConnectionsAfterRollback = ss.getNumberOfNonPooledConnectionsUsed();
                System.out.println("nonPooledConnectionsAfterRollback = " + nonPooledConnectionsAfterRollback);
                Assert.assertTrue("initialNonPooledConnections >= nonPooledConnectionsAfterRollback", initialNonPooledConnections >= nonPooledConnectionsAfterRollback);
            } finally {
                if (et.isActive()) {
                    et.rollback();
                }
                em.close();
            }
        } finally {
            DriverWrapper.repairAll();
            System.out.println("*END testNonpooledConnectionWithErrorOnAcquireConnection()");
        }
    }

    @Test
    public void testNonpooledConnectionWithErrorOnReleaseConnection() throws Exception {
        System.out.println("********************");
        System.out.println("*BEGIN testNonpooledConnectionWithErrorOnReleaseConnection()");
        if (emf == null) 
            return;

        final EntityManagerFactoryImpl emfi = emf.unwrap(EntityManagerFactoryImpl.class);
        Assert.assertNotNull(emfi);

        // Create an em with a unpooled connection policy, idea taken from EntityManagerJUnitTestSuite.testNonPooledConnection()
        final ServerSession ss = emfi.getServerSession();

        // Set up the DriverWrapper/ConnectionWrapper so we can emulate database connection failure
        // cache the original driver name and connection string.
        try {
            setupDriverWrapper(ss);
            DriverWrapper.repairAll();

            // Clone the connection policy and set the pool name to null to emulate using non-pooled connections
            final ConnectionPolicy connectionPolicy = (ConnectionPolicy)ss.getDefaultConnectionPolicy().clone();
            connectionPolicy.setLogin(ss.getLogin());
            connectionPolicy.setPoolName(null);

            final Map<String, Object> properties = new HashMap<>();
            properties.put(EntityManagerProperties.CONNECTION_POLICY, connectionPolicy);

            // Validate against initial non-pooled connection count
            final int initialNonPooledConnections = ss.getNumberOfNonPooledConnectionsUsed();
            final int maxNonPooledConnections = ss.getMaxNumberOfNonPooledConnections();
            System.out.println("initialNonPooledConnections = " + initialNonPooledConnections);
            System.out.println("maxNonPooledConnections = " + maxNonPooledConnections);
            if (maxNonPooledConnections != -1 && initialNonPooledConnections >= maxNonPooledConnections) {
                Assert.fail("initialNonPooledConnections >= maxNonPooledConnections, other tests may be leaking.");
            }

            EntityManager em = emf.createEntityManager(properties);
            EntityTransaction et = em.getTransaction();

            try {
                et.begin();

                Person p = new Person();
                Dog d = new Dog("Bingo");
                p.setDog(d);
                d.setOwner(p);

                em.persist(p);
                em.persist(d);
                em.persist(new XmlFish());

                final int nonPooledConnectionsBeforeFlush = ss.getNumberOfNonPooledConnectionsUsed();
                System.out.println("nonPooledConnectionsBeforeFlush = " + nonPooledConnectionsBeforeFlush);

                em.flush();
                final int nonPooledConnectionsAfterFlush = ss.getNumberOfNonPooledConnectionsUsed();
                System.out.println("nonPooledConnectionsAfterFlush = " + nonPooledConnectionsAfterFlush);
                Assert.assertEquals("nonPooledConnectionsBeforeFlush + 1 == nonPooledConnectionsAfterFlush", nonPooledConnectionsBeforeFlush + 1, nonPooledConnectionsAfterFlush);

                // The non-pooled Connection would be released after the commit.
                DriverWrapper.breakOldConnections();
                try {
                    et.commit();
                    Assert.fail("No RollbackException was thrown.");
                } catch (RollbackException re) {
                    // Expected
                } finally {
                    DriverWrapper.repairAll();
                }

                int nonPooledConnectionsAfterCommit = ss.getNumberOfNonPooledConnectionsUsed();
                System.out.println("nonPooledConnectionsAfterCommit = " + nonPooledConnectionsAfterCommit);
                Assert.assertTrue("initialNonPooledConnections >= nonPooledConnectionsAfterCommit", initialNonPooledConnections >= nonPooledConnectionsAfterCommit);
            } finally {
                DriverWrapper.repairAll();
                if (et.isActive()) {
                    et.rollback();
                }
                em.close();
            }
        } finally {
            DriverWrapper.repairAll();
            System.out.println("*END testNonpooledConnectionWithErrorOnReleaseConnection()");
        }
    }

    private void setupDriverWrapper(ServerSession ss) {
        // Set up the DriverWrapper/ConnectionWrapper so we can emulate database connection failure
        // cache the original driver name and connection string.
        String originalDriverName = ss.getLogin().getDriverClassName();
        String originalConnectionString = ss.getLogin().getConnectionString();

        if (DriverWrapper.class.getName().equals(originalDriverName)) {
            // DriverWrapper is already set up, so just return.
            return;
        }

        // the new driver name and connection string to be used by the test
        String newDriverName = DriverWrapper.class.getName();
        String newConnectionString = DriverWrapper.codeUrl(originalConnectionString);

        // setup the wrapper driver
        DriverWrapper.initialize(originalDriverName);

        ss.logout();
        ss.getLogin().setDriverClassName(newDriverName);
        ss.getLogin().setConnectionHealthValidatedOnError(true);
        ss.getLogin().setConnectionString(newConnectionString);
        ss.login();
    }
    
    /*
     * Taken from org.eclipse.persistence.testing.tests.jpa.validation.ValidationTestSuite
     */
    public static class BogusDataSource implements DataSource, Remote, Serializable {

        private static final long serialVersionUID = 1L;

        private String text = "foo";

        public BogusDataSource(String text){
            super();
            this.text = text;
        }

        public Connection getConnection() throws SQLException {
            RuntimeException exception = new RuntimeException(text);
            throw exception;
        }

        public Connection getConnection(String username, String password) throws SQLException {
            return getConnection();
        }

        //rest are ignored
        public java.io.PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        public void setLogWriter(java.io.PrintWriter out) throws SQLException{}
        public void setLoginTimeout(int seconds) throws SQLException{}
        public int getLoginTimeout() throws SQLException { return 1; }
        public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
        public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
        public Logger getParentLogger() { return null; }
    }

}
