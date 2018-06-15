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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.mongo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.eis.interactions.MappedInteraction;
import org.eclipse.persistence.eis.interactions.QueryStringInteraction;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoDatabaseConnection;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoDatabaseConnectionFactory;
import org.eclipse.persistence.internal.nosql.adapters.mongo.MongoOperation;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.nosql.adapters.mongo.Mongo3ConnectionSpec;
import org.eclipse.persistence.nosql.adapters.mongo.MongoPlatform;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa.mongo.Address;
import org.eclipse.persistence.testing.models.jpa.mongo.Address.AddressType;
import org.eclipse.persistence.testing.models.jpa.mongo.Buyer;
import org.eclipse.persistence.testing.models.jpa.mongo.Customer;
import org.eclipse.persistence.testing.models.jpa.mongo.LineItem;
import org.eclipse.persistence.testing.models.jpa.mongo.Order;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

/**
 * TestSuite to test MongoDB database.
 * To run this test suite the MongoDB must be running.
 */
public class MongoDatabaseTestSuite extends JUnitTestCase {

    /** The persistence unit name. */
    private static final String PU_NAME = "mongo";
    /** The persistence unit property key for the MongoDB connection host name or IP. */
    private static final String PROPERTY_HOST_KEY = PersistenceUnitProperties.NOSQL_PROPERTY + "mongo.host";
    /** The persistence unit property key for the MongoDB connection port. */
    private static final String PROPERTY_PORT_KEY = PersistenceUnitProperties.NOSQL_PROPERTY + "mongo.port";
    /** The persistence unit property key for the MongoDB database name. */
    private static final String PROPERTY_DB_KEY = PersistenceUnitProperties.NOSQL_PROPERTY + "mongo.db";
    /** The database (MongoDB) connection URL. */
    private static final String DB_URL_KEY = JUnitTestCaseHelper.insertIndex(JUnitTestCaseHelper.DB_URL_KEY, null);
    /** The database (MongoDB) connection user name test configuration property key. */
    private static final String DB_USER_KEY = JUnitTestCaseHelper.insertIndex(JUnitTestCaseHelper.DB_USER_KEY, null);
    /** The database (MongoDB) connection user password test configuration property key. */
    private static final String DB_PWD_KEY = JUnitTestCaseHelper.insertIndex(JUnitTestCaseHelper.DB_PWD_KEY, null);
    /** The database (MongoDB) platform class test configuration property key. */
    private static final String DB_PLATFORM_KEY
            = JUnitTestCaseHelper.insertIndex(JUnitTestCaseHelper.DB_PLATFORM_KEY, null);
    /** The test configuration property key of the EISConnectionSpec class name used to connect to the MongoDB
     *  data source. */
    private static final String DB_SPEC_KEY = JUnitTestCaseHelper.insertIndex(JUnitTestCaseHelper.DB_SPEC_KEY, null);
    private static final String SERVER_SELECTION_TIMEOUT_PROPERTY = PersistenceUnitProperties.NOSQL_PROPERTY
            + Mongo3ConnectionSpec.SERVER_SELECTION_TIMEOUT;
    private static final String SERVER_SELECTION_TIMEOUT = "100";

    public static Order existingOrder;

    /** The MongoDB persistence unit properties {@link Map} from the test properties {@code db.<name>}. */
    private final Map<String, String> properties;

    /**
     * Create an instance of MongoDB database test suite.
     */
    public MongoDatabaseTestSuite() {
        properties = initProperties();
    }

    /**
     * Create an instance of MongoDB database test suite.
     * @param name Test suite name.
     */
    public MongoDatabaseTestSuite(String name){
        super(name);
        properties = initProperties();
    }

    boolean isEnabled() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            MongoDatabaseConnection con = ((MongoDatabaseConnection)em.unwrap(javax.resource.cci.Connection.class));
            String version = con.getMetaData().getEISProductVersion();
            return version.compareTo("2.6") > 0;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    /**
     * Build MongoDB database test suite. Creates an instance of the {@link TestSuite} class and adds all the tests
     * into it.
     * @return MongoDB database test suite with all the tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("MongoDatabaseTestSuite");
        MongoDatabaseTestSuite testSetup = new MongoDatabaseTestSuite("testSetup");
        if (testSetup.isEnabled()) {
            suite.addTest(testSetup);
            suite.addTest(new MongoDatabaseTestSuite("testInsert"));
            suite.addTest(new MongoDatabaseTestSuite("testFind"));
            suite.addTest(new MongoDatabaseTestSuite("testUpdate"));
            suite.addTest(new MongoDatabaseTestSuite("testUpdateAdd"));
            suite.addTest(new MongoDatabaseTestSuite("testMerge"));
            suite.addTest(new MongoDatabaseTestSuite("testLockError"));
            suite.addTest(new MongoDatabaseTestSuite("testRefresh"));
            suite.addTest(new MongoDatabaseTestSuite("testDelete"));
            suite.addTest(new MongoDatabaseTestSuite("testSimpleJPQL"));
            suite.addTest(new MongoDatabaseTestSuite("testJPQLLike"));
            suite.addTest(new MongoDatabaseTestSuite("testComplexJPQL"));
            suite.addTest(new MongoDatabaseTestSuite("testJPQLEnum"));
            suite.addTest(new MongoDatabaseTestSuite("testNativeQuery"));
            suite.addTest(new MongoDatabaseTestSuite("testExternalFactory"));
            suite.addTest(new MongoDatabaseTestSuite("testUserPassword"));
            suite.addTest(new MongoDatabaseTestSuite("testDynamicEntities"));
        }
        return suite;
    }

    /**
     * Get the name of the persistence unit used in the tests.
     * @return The name of the persistence unit used in the tests.
     */
    @Override
    public String getPersistenceUnitName() {
        return PU_NAME;
    }

    /**
     * Build MongoDB persistence unit properties {@link Map} from test properties {@code db.<name>}.
     * @return Persistence unit properties {@link Map}.
     */
    private static Map<String, String> initProperties() {
        final Map<String, String> properties = new HashMap<>();
        final String dbUrl = JUnitTestCaseHelper.getProperty(DB_URL_KEY);
        final String dbUser = JUnitTestCaseHelper.getProperty(DB_USER_KEY);
        final String dbPwd = JUnitTestCaseHelper.getProperty(DB_PWD_KEY);
        final String platform = JUnitTestCaseHelper.getProperty(DB_PLATFORM_KEY);
        final String dbSpec = JUnitTestCaseHelper.getProperty(DB_SPEC_KEY);
        final String logLevel = JUnitTestCaseHelper.getProperty(JUnitTestCaseHelper.LOGGING_LEVEL_KEY);
        String uriUser = null;
        char[] uriPwd = null;
        if (dbUrl != null) {
            final MongoClientURI uri = new MongoClientURI(dbUrl);
            final List<String> hosts = uri.getHosts();
            final String name = uri.getDatabase();
            uriUser = uri.getUsername();
            uriPwd = uri.getPassword();
            if (hosts.size() > 0) {
                final String hostPort = hosts.get(0);
                final int splitPos = hostPort.indexOf(':');
                if (splitPos >= 0) {
                    final String host = hostPort.substring(0, splitPos);
                    final String port = hostPort.substring(splitPos);
                    properties.put(PROPERTY_HOST_KEY, host);
                    if (port != null && port.length() > 0) {
                        properties.put(PROPERTY_PORT_KEY, port);
                    }
                } else {
                    properties.put(PROPERTY_HOST_KEY, hostPort);
                }
            }
            if (name != null && name.length() > 0) {
                properties.put(PROPERTY_DB_KEY, name);
            }
        }
        // Database connection user name from db.user has higher priority than from URL.
        if (dbUser != null) {
            properties.put(PersistenceUnitProperties.JDBC_USER, dbUser);
        } else if (uriUser != null && uriUser.length() > 0) {
            properties.put(PersistenceUnitProperties.JDBC_USER, uriUser);
        }
        // Database connection user password from db.pwd has higher priority than from URL.
        if (dbPwd != null) {
            properties.put(PersistenceUnitProperties.JDBC_PASSWORD, dbPwd);
        } else if (uriPwd != null && uriPwd.length > 0) {
            properties.put(PersistenceUnitProperties.JDBC_PASSWORD, new String(uriPwd));
        }
        if (platform != null) {
            properties.put(PersistenceUnitProperties.TARGET_DATABASE, platform);
        }
        if (dbSpec != null) {
            properties.put(PersistenceUnitProperties.NOSQL_CONNECTION_SPEC, dbSpec);
        }
        if (logLevel != null) {
            properties.put(PersistenceUnitProperties.LOGGING_LEVEL, logLevel);
        }
        return properties;
    }

    /**
     * Update the connection user name persistence unit property value.
     * @param properties The properties {@link Map} to be modified.
     * @param key The connection user name persistence unit property key ({@code PersistenceUnitProperties.JDBC_USER}
     *            or {@code PersistenceUnitProperties.NOSQL_USER)}).
     * @param value The connection user name persistence unit property value.
     */
    private static void updateUserProperty(
            final Map<String, String> properties, final String key, final String value) {
        if (key.equals(PersistenceUnitProperties.JDBC_USER)
                || key.equals(PersistenceUnitProperties.NOSQL_USER)) {
            properties.remove(PersistenceUnitProperties.JDBC_USER);
            properties.remove(PersistenceUnitProperties.NOSQL_USER);
            properties.put(key, value);
        } else {
            throw new IllegalArgumentException("Invalid user persistence property key");
        }
    }

    /**
     * Update the connection user password persistence unit property value.
     * @param properties The properties {@link Map} to be modified.
     * @param key The connection user name persistence unit property key ({@code PersistenceUnitProperties.JDBC_PASSWORD}
     *            or {@code PersistenceUnitProperties.NOSQL_PASSWORD)}).
     * @param value The connection user name persistence unit property value.
     */
    private static void updatePasswordProperty(
            final Map<String, String> properties, final String key, final String value) {
        if (key.equals(PersistenceUnitProperties.JDBC_PASSWORD)
                || key.equals(PersistenceUnitProperties.NOSQL_PASSWORD)) {
            properties.remove(PersistenceUnitProperties.JDBC_PASSWORD);
            properties.remove(PersistenceUnitProperties.NOSQL_PASSWORD);
            properties.put(key, value);
        } else {
            throw new IllegalArgumentException("Invalid user persistence property key");
        }
    }

    /**
     * Get the MongoDB persistence unit properties {@link Map}.
     * @return The MongoDB persistence unit properties {@link Map}.
     */
    @Override
    public Map<String, String> getPersistenceProperties() {
        return properties;
    }

    /**
     * Initialize test suite.
     */
    public void testSetup() {
        EntityManager em = createEntityManager();
        // First clear old database.
        beginTransaction(em);
        MongoDatabase db = ((MongoDatabaseConnection)em.unwrap(javax.resource.cci.Connection.class)).getDB();
        db.drop();
        commitTransaction(em);
        beginTransaction(em);
        try {
            for (int index = 0; index < 10; index++) {
                existingOrder = new Order();
                existingOrder.orderedBy = "ACME";
                existingOrder.address = new Address();
                existingOrder.address.city = "Ottawa";
                existingOrder.address.addressee = "Bob Jones";
                existingOrder.address.state = "CA";
                existingOrder.address.country = "Mexico";
                existingOrder.address.zipCode = "12345";
                LineItem line = new LineItem();
                line.itemName = "stuff";
                line.itemPrice = new BigDecimal("10.99");
                line.lineNumber = 1;
                line.quantity = 100;
                existingOrder.lineItems.add(line);
                line = new LineItem();
                line.itemName = "more stuff";
                line.itemPrice = new BigDecimal("20.99");
                line.lineNumber = 2;
                line.quantity = 50;
                existingOrder.lineItems.add(line);
                existingOrder.comments.add("priority order");
                existingOrder.comments.add("next day");
                em.persist(existingOrder);
            }
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
    }

    /**
     * Test inserts.
     */
    public void testInsert() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = new Order();
        try {
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            order.address.type = AddressType.Work;

            LineItem item = new LineItem();
            item.itemName = "stuff";
            item.itemPrice = new BigDecimal("9.99");
            item.lineNumber = 1;
            item.quantity = 50;
            order.lineItems.add(item);

            item = new LineItem();
            item.itemName = "more stuff";
            item.itemPrice = new BigDecimal(500);
            item.lineNumber = 2;
            item.quantity = 1;
            order.lineItems.add(item);

            item = new LineItem();
            item.itemName = "stuff";
            item.itemPrice = new BigDecimal("9.99");
            item.lineNumber = 1;
            item.quantity = 50;
            order.lineItemsByNumber.put(1L, item);

            item = new LineItem();
            item.itemName = "more stuff";
            item.itemPrice = new BigDecimal(500);
            item.lineNumber = 2;
            item.quantity = 1;
            order.lineItemsByNumber.put(2L, item);

            em.persist(order);

            Customer customer = new Customer();
            customer.name = "ACME";
            em.persist(customer);
            order.customer = customer;
            order.customers.add(customer);
            customer = new Customer();
            customer.name = "Startup";
            em.persist(customer);
            order.customers.add(customer);

            Buyer buyer = new Buyer();
            buyer.id1 = 1;
            buyer.id2 = 1;
            buyer.name = "FOO Inc.";
            em.persist(buyer);
            order.buyer = buyer;
            order.buyers.add(buyer);
            buyer = new Buyer();
            buyer.id1 = 2;
            buyer.id2 = 2;
            buyer.name = "BAR Corp.";
            em.persist(buyer);
            order.buyers.add(buyer);

            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        verifyObjectInCacheAndDatabase(order);
        verifyObjectInEntityManager(order);
    }

    /**
     * Test pass an external factory when connecting.
     */
    public void testExternalFactory() throws Exception {
        Map properties = new HashMap();
        MongoClient mongo = new MongoClient();
        MongoDatabase db = mongo.getDatabase("mydb");
        properties.put(PersistenceUnitProperties.NOSQL_CONNECTION_FACTORY, new MongoDatabaseConnectionFactory(db));
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(getPersistenceUnitName(), properties);
        EntityManager em = factory.createEntityManager();
        em.close();
        factory.close();
        mongo.close();
    }

    /**
     * Test user/password connecting.
     */
    public void testUserPassword() throws Exception {
        Map<String, String> properties = new HashMap<String, String>(this.properties);
        updateUserProperty(properties, PersistenceUnitProperties.JDBC_USER, "unknownuser");
        updatePasswordProperty(properties, PersistenceUnitProperties.JDBC_PASSWORD, "password");
        properties.put(SERVER_SELECTION_TIMEOUT_PROPERTY, SERVER_SELECTION_TIMEOUT);
        boolean errorCaught = false;
        EntityManagerFactory factory = null;
        EntityManager em = null;
        try {
            factory = Persistence.createEntityManagerFactory(getPersistenceUnitName(), properties);
            em = factory.createEntityManager();
        } catch (Exception expected) {
            if (expected.getMessage().indexOf("Authentication failed") == -1 && expected.getMessage().indexOf("auth failed") == -1) {
                throw expected;
            }
            errorCaught = true;
        } finally {
            if (em != null) {
                em.close();
            }
            if (factory != null) {
                factory.close();
            }
        }
        if (!errorCaught) {
            fail("authentication should have failed");
        }
        properties = new HashMap<String, String>(this.properties);
        updateUserProperty(properties, PersistenceUnitProperties.NOSQL_USER, "unknownuser");
        updatePasswordProperty(properties, PersistenceUnitProperties.NOSQL_PASSWORD, "password");
        properties.put(SERVER_SELECTION_TIMEOUT_PROPERTY, SERVER_SELECTION_TIMEOUT);
        errorCaught = false;
        factory = null;
        em = null;
        try {
            factory = Persistence.createEntityManagerFactory(getPersistenceUnitName(), properties);
            em = factory.createEntityManager();
        } catch (Exception expected) {
            if (expected.getMessage().indexOf("Authentication failed") == -1 && expected.getMessage().indexOf("auth failed") == -1) {
                throw expected;
            }
            errorCaught = true;
        } finally {
            if (em != null) {
                em.close();
            }
            if (factory != null) {
                factory.close();
            }
        }
        if (!errorCaught) {
            fail("authentication should have failed");
        }
    }

    /**
     * Test find.
     */
    public void testFind() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = null;
        try {
            order = em.find(Order.class, existingOrder.id);
            compareObjects(existingOrder, order);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    /**
     * Test updates.
     */
    public void testUpdate() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = new Order();
        try {
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            order = em.find(Order.class, order.id);
            order.orderedBy = "Fred Jones";
            order.address.addressee = "Fred Jones";
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        verifyObjectInCacheAndDatabase(order);
        verifyObjectInEntityManager(order);
    }


    /**
     * Test updates.
     */
    public void testUpdateAdd() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = new Order();
        try {
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            order = em.find(Order.class, order.id);
            Customer customer = new Customer();
            em.persist(customer);
            order.customers.add(customer);
            order.comments.add("foo");
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        verifyObjectInCacheAndDatabase(order);
        verifyObjectInEntityManager(order);
    }

    /**
     * Test merge.
     */
    public void testMerge() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = new Order();
        try {
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            order = em.find(Order.class, order.id);
            order.orderedBy = "Fred Jones";
            order.address.addressee = "Fred Jones";
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        em = createEntityManager();
        beginTransaction(em);
        try {
            em.merge(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            Order fromDatabase = em.find(Order.class, order.id);
            order.version = order.version + 1;
            compareObjects(order, fromDatabase);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    /**
     * Test locking.
     */
    public void testLockError() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = new Order();
        try {
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            Order updatedOrder = em.find(Order.class, order.id);
            updatedOrder.orderedBy = "Fred Jones";
            updatedOrder.address.addressee = "Fred Jones";
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        OptimisticLockException exception = null;
        em = createEntityManager();
        beginTransaction(em);
        try {
            em.merge(order);
            commitTransaction(em);
        } catch (OptimisticLockException expected) {
            exception = expected;
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        if (exception == null) {
            fail("Lock error did not occur.");
        }
    }

    /**
     * Test refresh.
     */
    public void testRefresh() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = new Order();
        try {
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            order = em.find(Order.class, order.id);
            order.orderedBy = "Fred Jones";
            em.refresh(order);
            if (order.orderedBy.equals("Fred Jones")) {
                fail("Refresh failed: " + order.orderedBy);
            }
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    /**
     * Test JPQL.
     */
    public void testSimpleJPQL() {
        // Clear db first.
        testSetup();
        EntityManager em = createEntityManager();
        try {
            // all
            Query query = em.createQuery("Select o from Order o");
            List results = query.getResultList();
            if (results.size() != 10) {
                fail("Expected 10 results: " + results);
            }
            Order order = (Order)results.get(0);
            // =
            query = em.createQuery("Select o from Order o where o.id = :oid");
            query.setParameter("oid", order.id);
            results = query.getResultList();
            if (results.size() != 1) {
                fail("Expected 1 result: " + results);
            }
            // !=
            query = em.createQuery("Select o from Order o where o.id <> :nextId");
            query.setParameter("nextId", order.id);
            results = query.getResultList();
            if (results.size() != 9) {
                fail("Expected 9 result: " + results);
            }
            // in
            query = em.createQuery("Select o from Order o where o.orderedBy in (:by, :by)");
            query.setParameter("by", "ACME");
            results = query.getResultList();
            if (results.size() != 10) {
                fail("Expected 1 result: " + results);
            }
            // nin
            query = em.createQuery("Select o from Order o where o.orderedBy not in (:by, :by)");
            query.setParameter("by", "ACME");
            results = query.getResultList();
            if (results.size() != 0) {
                fail("Expected 0 result: " + results);
            }
            // and
            query = em.createQuery("Select o from Order o where o.id = :nextId and o.orderedBy = 'ACME'");
            query.setParameter("nextId", order.id);
            results = query.getResultList();
            if (results.size() != 1) {
                fail("Expected 1 result: " + results);
            }
            // or
            query = em.createQuery("Select o from Order o where o.id = :nextId or o.orderedBy = 'ACME'");
            query.setParameter("nextId", order.id);
            results = query.getResultList();
            if (results.size() != 10) {
                fail("Expected 10 result: " + results);
            }
            // not
            query = em.createQuery("Select o from Order o where o.id = :nextId or not(o.orderedBy = 'ACME')");
            query.setParameter("nextId", order.id);
            results = query.getResultList();
            if (results.size() != 1) {
                fail("Expected 1 result: " + results);
            }
            // not + operator
            query = em.createQuery("Select o from Order o where o.id = :nextId or not(o.id <> :nextId)");
            query.setParameter("nextId", order.id);
            results = query.getResultList();
            if (results.size() != 1) {
                fail("Expected 10 result: " + results);
            }
            // > <
            query = em.createQuery("Select o from Order o where o.id > :nextId and o.id < :nextId");
            query.setParameter("nextId", order.id);
            results = query.getResultList();
            if (results.size() != 0) {
                fail("Expected 0 result: " + results);
            }
            // >= <=
            query = em.createQuery("Select o from Order o where o.id >= :nextId and o.id <= :nextId");
            query.setParameter("nextId", order.id);
            results = query.getResultList();
            if (results.size() != 1) {
                fail("Expected 1 result: " + results);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Test JPQL.
     */
    public void testComplexJPQL() {
        // Clear db first.
        testSetup();
        EntityManager em = createEntityManager();
        try {
            // embedded
            Query query = em.createQuery("Select o from Order o where o.address.city = :city");
            query.setParameter("city", "Ottawa");
            List results = query.getResultList();
            if (results.size() != 10) {
                fail("Expected 10 result: " + results);
            }
            query = em.createQuery("Select o from Order o join o.lineItems l where l.itemName = :name");
            query.setParameter("name", "stuff");
            results = query.getResultList();
            if (results.size() != 10) {
                fail("Expected 10 result: " + results);
            }
            // order by
            query = em.createQuery("Select o from Order o order by o.address.city");
            results = query.getResultList();
            if (results.size() != 10) {
                fail("Expected 10 result: " + results);
            }
            query = em.createQuery("Select o from Order o order by o.orderedBy desc");
            results = query.getResultList();
            if (results.size() != 10) {
                fail("Expected 10 result: " + results);
            }
            query = em.createQuery("Select o from Order o order by o.orderedBy desc, o.address.city desc");
            //query = em.createQuery("Select o from Order o order by o.orderedBy desc, o.address.city asc");
            // Can't seem to order asc and desc at same time.
            results = query.getResultList();
            if (results.size() != 10) {
                fail("Expected 10 result: " + results);
            }
            // select
            query = em.createQuery("Select o.id from Order o where o.orderedBy like :orderedBy");
            query.setParameter("orderedBy", "ACME");
            results = query.getResultList();
            if (!(results.get(0) instanceof String)) {
                fail("Incorrect result: " + results);
            }
            query = em.createQuery("Select o.id, o.address.city from Order o where o.orderedBy like :orderedBy");
            query.setParameter("orderedBy", "ACME");
            results = query.getResultList();
            if (!(results.get(0) instanceof Object[])) {
                fail("Incorrect result: " + results);
            }
            // error
            try {
                query = em.createQuery("Select count(o) from Order o");
                query.setParameter("orderedBy", "ACME");
                results = query.getResultList();
                fail("Exception expected");
            } catch (Exception expected) {
                // OK
            }
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Test JPQL.
     */
    public void testJPQLLike() {
        // Clear db first.
        testSetup();
        EntityManager em = createEntityManager();
        try {
            // like
            Query query = em.createQuery("Select o from Order o where o.orderedBy like :orderedBy");
            query.setParameter("orderedBy", "ACME");
            List results = query.getResultList();
            if (results.size() != 10) {
                fail("Expected 10 result: " + results);
            }
            query.setParameter("orderedBy", "A%");
            results = query.getResultList();
            if (results.size() != 10) {
                fail("Expected 10 result: " + results);
            }
            query.setParameter("orderedBy", "%");
            results = query.getResultList();
            if (results.size() != 10) {
                fail("Expected 10 result: " + results);
            }
            query.setParameter("orderedBy", "A__E");
            results = query.getResultList();
            if (results.size() != 10) {
                fail("Expected 10 result: " + results);
            }
            query.setParameter("orderedBy", "*");
            results = query.getResultList();
            if (results.size() != 0) {
                fail("Expected 0 result: " + results);
            }
            query.setParameter("orderedBy", "A");
            results = query.getResultList();
            if (results.size() != 0) {
                fail("Expected 0 result: " + results);
            }
            query.setParameter("orderedBy", "");
            results = query.getResultList();
            if (results.size() != 0) {
                fail("Expected 0 result: " + results);
            }
            query.setParameter("orderedBy", "%E");
            results = query.getResultList();
            if (results.size() != 10) {
                fail("Expected 10 result: " + results);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Test enum query.
     */
    public void testJPQLEnum() {
        // Clear db first.
        testSetup();
        EntityManager em = createEntityManager();
        try {
            // like
            Query query = em.createQuery("Select o from Order o where o.address.type = :type");
            query.setParameter("type", AddressType.Home);
            List results = query.getResultList();
            if (results.size() != 0) {
                fail("Expected 0 result: " + results);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Test native query.
     */
    public void testNativeQuery() {
        EntityManager em = createEntityManager();

        MappedInteraction interaction = new MappedInteraction();
        interaction.setProperty(MongoPlatform.OPERATION, MongoOperation.FIND.name());
        interaction.setProperty(MongoPlatform.COLLECTION, "ORDER");
        interaction.setProperty(MongoPlatform.BATCH_SIZE, "10");
        interaction.setProperty(MongoPlatform.READ_PREFERENCE, "PRIMARY");
        interaction.addArgumentValue("_id", existingOrder.id);
        Query query = em.unwrap(JpaEntityManager.class).createQuery(interaction);
        List result = query.getResultList();
        if ((result.size() != 1)
                || (!(result.get(0) instanceof Record))
                || !(((Record)result.get(0)).get("_id").equals(existingOrder.id))) {
            fail("Incorrect result: " + result);
        }

        interaction = new MappedInteraction();
        interaction.setProperty(MongoPlatform.OPERATION, MongoOperation.FIND.name());
        interaction.setProperty(MongoPlatform.COLLECTION, "ORDER");
        interaction.setProperty(MongoPlatform.BATCH_SIZE, "10");
        interaction.setProperty(MongoPlatform.READ_PREFERENCE, "PRIMARY");
        interaction.addArgumentValue("_id", existingOrder.id);
        query = em.unwrap(JpaEntityManager.class).createQuery(interaction, Order.class);
        Order order = (Order)query.getSingleResult();
        if ((order == null) || (!order.id.equals(existingOrder.id))) {
            fail("Incorrect result: " + order);
        }

        QueryStringInteraction mqlInteraction = new QueryStringInteraction();
        mqlInteraction.setQueryString("db.ORDER.findOne({\"_id\":\"" + existingOrder.id + "\"})");
        query = em.unwrap(JpaEntityManager.class).createQuery(mqlInteraction, Order.class);
        order = (Order)query.getSingleResult();
        if ((order == null) || (!order.id.equals(existingOrder.id))) {
            fail("Incorrect result: " + order);
        }

        query = em.createNativeQuery("db.ORDER.findOne({\"_id\":\"" + existingOrder.id + "\"})", Order.class);
        order = (Order)query.getSingleResult();
        if ((order == null) || (!order.id.equals(existingOrder.id))) {
            fail("Incorrect result: " + order);
        }

    }

    /**
     * Test deletes.
     */
    public void testDelete() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = new Order();
        try {
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            order = em.find(Order.class, order.id);
            em.remove(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            Order fromDatabase = em.find(Order.class, order.id);
            if (fromDatabase != null) {
                fail("Object not deleted: " + fromDatabase);
            }
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    /**
     * Test dynamic entities.
     */
    public void testDynamicEntities() {
        final Map properties = new HashMap<String, String>(this.properties);
        properties.put("eclipselink.classloader", new DynamicClassLoader(getClass().getClassLoader()));
        properties.put("eclipselink.logging.level", "finest");
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("mongo-dynamic", properties);
        final EntityManager em = factory.createEntityManager();
        beginTransaction(em);
        try {
            JPADynamicHelper helper = new JPADynamicHelper(em);
            final DynamicEntity person = helper.newDynamicEntity("Person");
            person.set("firstname", "Boy");
            person.set("lastname", "Pelletier");
            DynamicEntity address = helper.newDynamicEntity("org.eclipse.persistence.testing.models.jpa.nosql.dynamic.Address");
            address.set("city", "Ottawa");
            List<DynamicEntity> addresses = new ArrayList<>();
            addresses.add(address);
            person.set("addresses", addresses);
            em.persist(person);
            commitTransaction(em);

            em.createNamedQuery("Person.findAll").getResultList();

            beginTransaction(em);
            address = helper.newDynamicEntity("org.eclipse.persistence.testing.models.jpa.nosql.dynamic.Address");
            address.set("city", "Ottawa2");
            addresses = person.get("addresses");
            addresses.add(address);
            person.set("addresses", addresses);
            commitTransaction(em);

            factory.getCache().evictAll();
            em.clear();
            final DynamicEntity dbPerson = em.find(helper.getType("Person").getJavaClass(), person.get("id"));
            if (((Collection)dbPerson.get("addresses")).size() != 2) {
                fail("Expected 2 addresses: " + dbPerson.get("addresses"));
            }
        } finally {
            closeEntityManagerAndTransaction(em);
            factory.close();
        }
    }

}
