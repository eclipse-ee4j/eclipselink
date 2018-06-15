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
//     Oracle - initial API and implementation.
//     04/11/2016-2.7 Tomas Kraus
//       - 490677: Allow Oracle NoSQL connection to be specified in build properties bundle
package org.eclipse.persistence.testing.tests.jpa.nosql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.eis.interactions.MappedInteraction;
import org.eclipse.persistence.eis.interactions.XMLInteraction;
import org.eclipse.persistence.internal.nosql.adapters.nosql.OracleNoSQLOperation;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.nosql.adapters.nosql.OracleNoSQLPlatform;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.testing.framework.junit.LogTestExecution;
import org.eclipse.persistence.testing.models.jpa.nosql.Address;
import org.eclipse.persistence.testing.models.jpa.nosql.Customer;
import org.eclipse.persistence.testing.models.jpa.nosql.Order;
import org.eclipse.persistence.testing.tests.nosql.EntityManagerHelper;
import org.eclipse.persistence.testing.tests.nosql.JPAModelHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * Oracle NoSQL JPA database tests.
 */
public class NoSQLJPATest {

    /** Log the test being currently executed. */
    @Rule public LogTestExecution logExecution = new LogTestExecution();

    /** The persistence unit name. */
    private static final String PU_NAME = "nosql";

    /** Stored {@link Order} instances count. */
    private static final int ORDERS_COUNT = 10;

    /** Stored {@link Order} instances. */
    private static Order[] orders;

    /** Entity manager used by tests. */
    EntityManager em;

    /**
     * Get random order instance from test setup.
     * @return Random order instance from test setup.
     */
    private static Order getRandomOrder() {
        final int index = (int)(Math.random() * ORDERS_COUNT);
        return orders[index];
    }

    /**
     * Initialize data model for tests.
     */
    @BeforeClass
    public static void initModel() {
        EntityManager em = EntityManagerHelper.createEntityManager(PU_NAME);
        try {
            JPAModelHelper.deleteModel(em);
            orders = JPAModelHelper.buildModel(em, ORDERS_COUNT);
        } finally {
            EntityManagerHelper.clearCache(em);
            EntityManagerHelper.closeEntityManagerAndTransaction(em);
        }
    }

    /**
     * Delete data model after tests.
     */
    @AfterClass
    public static void deleteModel() {
        EntityManager em = EntityManagerHelper.createEntityManager(PU_NAME);
        try {
            JPAModelHelper.deleteModel(em);
        } finally {
            EntityManagerHelper.clearCache(em);
            EntityManagerHelper.closeEntityManagerAndTransaction(em);
        }
    }

    /**
     * Setup test.
     */
    @Before
    public void setUp() {
        em = EntityManagerHelper.createEntityManager(PU_NAME);
    }

    /**
     * Cleanup test.
     */
    @After
    public void tearDown() {
        EntityManagerHelper.closeEntityManagerAndTransaction(em);
    }

    /**
     * Test inserts.
     */
    @Test
    public void testInsert() {
        final Order order = new Order();
        EntityManagerHelper.beginTransaction(em);
        try {
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            order.customer = new Customer();
            order.customer.name = "ACME";
            em.persist(order.customer);
            EntityManagerHelper.commitTransaction(em);
        } finally {
            EntityManagerHelper.clearCache(em);
            EntityManagerHelper.closeEntityManagerAndTransaction(em);
        }
        em = EntityManagerHelper.createEntityManager(PU_NAME);
        EntityManagerHelper.beginTransaction(em);
        Order fromDatabase = null;
        try {
            fromDatabase = em.find(Order.class, order.id);
            EntityManagerHelper.compareObjects(order, fromDatabase, em);
        } finally {
            if (fromDatabase != null) {
                em.remove(fromDatabase);
            }
        }
    }

    /**
     * Test find on random {@link Order} key.
     */
    @Test
    public void testFind() {
        EntityManagerHelper.beginTransaction(em);
        final Order existingOrder = getRandomOrder();
        final Order order = em.find(Order.class, existingOrder.id);
        EntityManagerHelper.compareObjects(existingOrder, order, em);
    }

    /**
     * Test updates.
     */
    @Test
    public void testUpdate() {
        EntityManagerHelper.beginTransaction(em);
        Order order = new Order();
        String orderId = null;
        try {
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            EntityManagerHelper.commitTransaction(em);
            orderId = order.id;
        } finally {
            EntityManagerHelper.clearCache(em);
            EntityManagerHelper.closeEntityManagerAndTransaction(em);
        }
        em = EntityManagerHelper.createEntityManager(PU_NAME);
        EntityManagerHelper.beginTransaction(em);
        try {
            final Order existingOrder = getRandomOrder();
            order = em.find(Order.class, existingOrder.id);
            order.orderedBy = "Fred Jones";
            order.address.addressee = "Fred Jones";
            EntityManagerHelper.commitTransaction(em);
        } finally {
            EntityManagerHelper.clearCache(em);
            EntityManagerHelper.closeEntityManagerAndTransaction(em);
        }
        em = EntityManagerHelper.createEntityManager(PU_NAME);
        EntityManagerHelper.beginTransaction(em);
        try {
            Order fromDatabase = em.find(Order.class, order.id);
            EntityManagerHelper.compareObjects(order, fromDatabase, em);
        } finally {
            Order toDelete = em.find(Order.class, orderId);
            em.remove(toDelete);
        }
    }

    /**
     * Test merge.
     */
    @Test
    public void testMerge() {
        EntityManagerHelper.beginTransaction(em);
        Order order = new Order();
        String orderId = null;
        try {
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            EntityManagerHelper.commitTransaction(em);
            orderId = order.id;
        } finally {
            EntityManagerHelper.clearCache(em);
            EntityManagerHelper.closeEntityManagerAndTransaction(em);
        }
        em = EntityManagerHelper.createEntityManager(PU_NAME);
        EntityManagerHelper.beginTransaction(em);
        try {
            order = em.find(Order.class, order.id);
            order.orderedBy = "Fred Jones";
            order.address.addressee = "Fred Jones";
        } finally {
            EntityManagerHelper.closeEntityManagerAndTransaction(em);
        }
        em = EntityManagerHelper.createEntityManager(PU_NAME);
        EntityManagerHelper.beginTransaction(em);
        try {
            em.merge(order);
            EntityManagerHelper.commitTransaction(em);
        } finally {
            EntityManagerHelper.clearCache(em);
            EntityManagerHelper.closeEntityManagerAndTransaction(em);
        }
        em = EntityManagerHelper.createEntityManager(PU_NAME);
        EntityManagerHelper.beginTransaction(em);
        try {
            Order fromDatabase = em.find(Order.class, order.id);
            EntityManagerHelper.compareObjects(order, fromDatabase, em);
        } finally {
            Order toDelete = em.find(Order.class, orderId);
            em.remove(toDelete);
        }
    }

    /**
     * Test refresh.
     */
    @Test
    public void testRefresh() {
        EntityManagerHelper.beginTransaction(em);
        Order order = new Order();
        try {
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            EntityManagerHelper.commitTransaction(em);
        } finally {
            EntityManagerHelper.clearCache(em);
            EntityManagerHelper.closeEntityManagerAndTransaction(em);
        }
        em = EntityManagerHelper.createEntityManager(PU_NAME);
        EntityManagerHelper.beginTransaction(em);
        try {
            order = em.find(Order.class, order.id);
            order.orderedBy = "Fred Jones";
            em.refresh(order);
            assertEquals(String.format("Refresh failed: %s", order.orderedBy), "ACME", order.orderedBy);
        } finally {
            em.remove(order);
        }
    }

    /**
     * Test JPQL.
     */
    @Test
    public void testJPQL() {
        // We need known amount of records in DB.
        initModel();
        final TypedQuery<Order> query1 = em.createQuery("Select o from Order o", Order.class);
        final int count1 = query1.getResultList().size();
        assertEquals(String.format("Find all did not work, expected 10 got: %d", count1), 10, count1);
        final Order existingOrder = getRandomOrder();
        final TypedQuery<Order> query2 = em.createQuery("Select o from Order o where o.id = :id", Order.class);
        query2.setParameter("id", existingOrder.id);
        final int count2 = query2.getResultList().size();
        assertEquals(String.format("Find all did not work, expected 1 got: %d", count2), 1, count2);
    }

    /**
     * Test native query.
     */
    @Test
    public void testNativeQuery() {
        MappedInteraction interaction = new MappedInteraction();
        final Order existingOrder = getRandomOrder();
        interaction.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.GET.name());
        interaction.addArgumentValue("[ORDER," + existingOrder.id + "]", "");
        final Query query1 = em.unwrap(JpaEntityManager.class).createQuery(interaction);
        @SuppressWarnings("unchecked")
        final List<Object> result1 = query1.getResultList();
        assertEquals(String.format("Expected result of size 1, got %d", result1.size()), 1, result1.size());
        assertTrue(String.format("Result is not instance of Record but %s", result1.get(0).getClass().getSimpleName()),
                (result1.get(0) instanceof Record));
        assertTrue(String.format("Incorrect result: %s", result1),
                (((Record)result1.get(0)).containsKey("[ORDER," + existingOrder.id + "]")));

        interaction = new XMLInteraction();
        interaction.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.GET.name());
        interaction.addArgumentValue("@id", existingOrder.id);
        final Query query2 = em.unwrap(JpaEntityManager.class).createQuery(interaction, Order.class);
        @SuppressWarnings("unchecked")
        final List<Object> result2 = query2.getResultList();
        assertEquals(String.format("Expected result of size 1, got %d", result2.size()), 1, result2.size());
        assertTrue(String.format("Result is not instance of Order but %s", result2.get(0).getClass().getSimpleName()),
                (result2.get(0) instanceof Order));
    }

    /**
     * Test deletes.
     */
    @Test
    public void testDelete() {
        EntityManagerHelper.beginTransaction(em);
        Order order = new Order();
        try {
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            EntityManagerHelper.commitTransaction(em);
        } finally {
            EntityManagerHelper.clearCache(em);
            EntityManagerHelper.closeEntityManagerAndTransaction(em);
        }
        em = EntityManagerHelper.createEntityManager(PU_NAME);
        EntityManagerHelper.beginTransaction(em);
        try {
            order = em.find(Order.class, order.id);
            em.remove(order);
            EntityManagerHelper.commitTransaction(em);
        } finally {
            EntityManagerHelper.clearCache(em);
            EntityManagerHelper.closeEntityManagerAndTransaction(em);
        }
        em = EntityManagerHelper.createEntityManager(PU_NAME);
        EntityManagerHelper.beginTransaction(em);
        final Order fromDatabase = em.find(Order.class, order.id);
        assertNull(String.format("Object not deleted: %s", fromDatabase), fromDatabase);
    }

}
