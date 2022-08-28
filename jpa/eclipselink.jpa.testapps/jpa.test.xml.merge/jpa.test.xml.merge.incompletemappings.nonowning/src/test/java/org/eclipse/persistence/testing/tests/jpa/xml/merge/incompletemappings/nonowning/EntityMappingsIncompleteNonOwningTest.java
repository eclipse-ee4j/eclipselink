/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink


package org.eclipse.persistence.testing.tests.jpa.xml.merge.incompletemappings.nonowning;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning.Address;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning.Customer;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning.Item;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning.Order;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning.PartsList;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning.RelationshipsTableManager;

import java.util.ArrayList;
import java.util.List;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class EntityMappingsIncompleteNonOwningTest extends JUnitTestCase {
    private static Integer customerId;
    private static Integer itemId;
    private static Integer orderId;

    public EntityMappingsIncompleteNonOwningTest() {
        super();
    }

    public EntityMappingsIncompleteNonOwningTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "xml-merge-incomplete-nonowning-mappings";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Non-owning Model");
        suite.addTest(new EntityMappingsIncompleteNonOwningTest("testSetup"));
        suite.addTest(new EntityMappingsIncompleteNonOwningTest("testCreateCustomer"));
        suite.addTest(new EntityMappingsIncompleteNonOwningTest("testCreateItem"));
        suite.addTest(new EntityMappingsIncompleteNonOwningTest("testCreateOrder"));

        suite.addTest(new EntityMappingsIncompleteNonOwningTest("testReadCustomer"));
        suite.addTest(new EntityMappingsIncompleteNonOwningTest("testReadItem"));
        suite.addTest(new EntityMappingsIncompleteNonOwningTest("testReadOrder"));

        suite.addTest(new EntityMappingsIncompleteNonOwningTest("testUpdateCustomer"));
        suite.addTest(new EntityMappingsIncompleteNonOwningTest("testUpdateItem"));
        suite.addTest(new EntityMappingsIncompleteNonOwningTest("testUpdateOrder"));

        suite.addTest(new EntityMappingsIncompleteNonOwningTest("testDeleteOrder"));
        suite.addTest(new EntityMappingsIncompleteNonOwningTest("testDeleteCustomer"));
        suite.addTest(new EntityMappingsIncompleteNonOwningTest("testDeleteItem"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        DatabaseSession session = getPersistenceUnitServerSession();
        new RelationshipsTableManager().replaceTables(session);
        clearCache();
    }

    public void testCreateCustomer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Customer customer = new Customer();
            customer.setName("Joe Black");
            customer.setCity("Austin");
            Address billingAddress = new Address("19 Somestreet", "Anytown", "ON", "Canada", "X0X-0X0");
            billingAddress.setCustomer(customer);
            em.persist(customer);
            customerId = customer.getCustomerId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        closeEntityManager(em);
    }

    public void testCreateItem() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

            PartsList pl = new PartsList();
            em.persist(pl);
            PartsList pl2 = new PartsList();
            em.persist(pl2);

            List<PartsList> partsLists = new ArrayList<>();
            partsLists.add(pl);
            partsLists.add(pl2);

            Item item = new Item();
            item.setName("PartA");
            item.setDescription("This is part of a widget.");
            item.setImage(new byte[1024]);
            item.setPartsLists(partsLists);
            em.persist(item);
            itemId = item.getItemId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        closeEntityManager(em);
    }

    public void testCreateOrder() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Order order = new Order();
            order.setShippingAddress("50 O'Connor St.");
            Customer customer = em.find(Customer.class, customerId);
            order.setCustomer(customer);
            order.setQuantity(1);
            Item item = em.find(Item.class, itemId);
            order.setItem(item);
            em.persist(order);
            orderId = order.getOrderId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    public void testDeleteCustomer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.remove(em.find(Customer.class, customerId));
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Customer", em.find(Customer.class, customerId));
    }

    public void testDeleteItem() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.remove(em.find(Item.class, itemId));
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Item", em.find(Item.class, itemId));
    }

    public void testDeleteOrder() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.remove(em.find(Order.class, orderId));
            em.refresh(em.find(Customer.class, customerId)); //refresh Customer
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Order", em.find(Order.class, orderId));
    }

    public void testReadCustomer() {
        Customer customer = createEntityManager().find(Customer.class, customerId);
        assertSame("Error reading Customer", customer.getCustomerId(), customerId);
    }

    public void testReadItem() {
        Item item = createEntityManager().find(Item.class, itemId);
        assertSame("Error reading Item", item.getItemId(), itemId);
    }

    public void testReadOrder() {
        Order order = createEntityManager().find(Order.class, orderId);
        assertSame("Error reading Order", order.getOrderId(), orderId);
    }

    public void testUpdateCustomer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Customer customer = em.find(Customer.class, customerId);
            customer.setCity("Dallas");
            em.merge(customer);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        clearCache();
        em.clear();
        Customer newCustomer = em.find(Customer.class, customerId);
        assertEquals("Error updating Customer", "Dallas", newCustomer.getCity());
    }

    public void testUpdateItem() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            PartsList pl = new PartsList();
            em.persist(pl);
            List<PartsList> partsLists = new ArrayList<>();
            partsLists.add(pl);

            Item item = em.find(Item.class, itemId);
            item.setDescription("A Widget");
            item.setImage(new byte[1280]);
            item.setPartsLists(partsLists);
            em.merge(item);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        clearCache();
        em.clear();
        Item newItem = em.find(Item.class, itemId);
        assertEquals("Error updating Item description", "A Widget", newItem.getDescription());
        assertEquals("Error updating Item image", 1280, newItem.getImage().length);
        assertTrue("Error updating Item parts lists", newItem.getPartsLists().size() != 1);
    }

    public void testUpdateOrder() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Customer customer = em.find(Customer.class, customerId);
            Order order = customer.getOrders().iterator().next();
            order.setQuantity(100);
            em.merge(customer);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        clearCache();
        em.clear();
        Customer newCustomer = em.find(Customer.class, customerId);
        assertEquals("Error updating Customer", 100, (newCustomer.getOrders().iterator().next()).getQuantity());
    }
}
