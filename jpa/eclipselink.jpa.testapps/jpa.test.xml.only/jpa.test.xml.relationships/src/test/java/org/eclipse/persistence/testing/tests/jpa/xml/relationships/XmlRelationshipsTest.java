/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.xml.relationships;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Auditor;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Customer;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Item;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Order;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.OrderCard;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.OrderLabel;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.RelationshipsTableManager;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class XmlRelationshipsTest extends JUnitTestCase {
    private static Integer customerId;
    private static Integer itemId;
    private static Integer extendedItemId;
    private static Integer orderId;

    public XmlRelationshipsTest() {
        super();
    }

    public XmlRelationshipsTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "xml-relationships";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Relationships Model - xml-relationships");
        suite.addTest(new XmlRelationshipsTest("testSetup"));
        suite.addTest(new XmlRelationshipsTest("testCreateCustomer"));
        suite.addTest(new XmlRelationshipsTest("testCreateItem"));
        suite.addTest(new XmlRelationshipsTest("testCreateOrder"));

        suite.addTest(new XmlRelationshipsTest("testReadCustomer"));
        suite.addTest(new XmlRelationshipsTest("testReadItem"));
        suite.addTest(new XmlRelationshipsTest("testReadOrder"));

        suite.addTest(new XmlRelationshipsTest("testNamedQueryOnCustomer"));
        suite.addTest(new XmlRelationshipsTest("testNamedQueryOnItem"));
        suite.addTest(new XmlRelationshipsTest("testNamedQueryOnOrder"));

        suite.addTest(new XmlRelationshipsTest("testUpdateCustomer"));
        suite.addTest(new XmlRelationshipsTest("testUpdateItem"));
        suite.addTest(new XmlRelationshipsTest("testUpdateOrder"));

        suite.addTest(new XmlRelationshipsTest("testOne2OneRelationTables"));

        suite.addTest(new XmlRelationshipsTest("testDeleteOrder"));
        suite.addTest(new XmlRelationshipsTest("testDeleteCustomer"));
        suite.addTest(new XmlRelationshipsTest("testDeleteItem"));

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
            Item item = new Item();
            item.setName("Widget");
            item.setDescription("This is a Widget");
            item.setImage(new byte[1024]);
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

        closeEntityManager(em);
    }

    public void testDeleteCustomer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Customer c = em.find(Customer.class, customerId);
            em.refresh(c);
            em.remove(c);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Customer", em.find(Customer.class, customerId));
        closeEntityManager(em);
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
        closeEntityManager(em);
    }

    public void testDeleteOrder() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.remove(em.find(Order.class, orderId));
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Order", em.find(Order.class, orderId));
        closeEntityManager(em);
    }

    public void testNamedQueryOnCustomer() {
        EntityManager em = createEntityManager();
        Customer customer = (Customer)em.createNamedQuery("findAllXMLCustomers").getSingleResult();
        assertNotNull("Error executing named query 'findAllXMLCustomers'", customer);
        closeEntityManager(em);
    }

    public void testNamedQueryOnOrder() {
        EntityManager em = createEntityManager();
        Query query = em.createNamedQuery("findAllXMLOrdersByItem");
        query.setParameter("id", itemId);
        Order order = (Order) query.getSingleResult();
        assertNotNull("Error executing named query 'findAllXMLOrdersByItem'", order);
        closeEntityManager(em);
    }

    public void testNamedQueryOnItem() {
        EntityManager em = createEntityManager();
        Query query = em.createNamedQuery("findAllXMLItemsByName");
        query.setParameter("1", "Widget");
        Item item = (Item) query.getSingleResult();
        assertNotNull("Error executing named query 'findAllXMLItemsByName'", item);
        closeEntityManager(em);
    }

    public void testReadCustomer() {
        EntityManager em = createEntityManager();
        Customer customer = em.find(Customer.class, customerId);
        assertEquals("Error reading Customer", customer.getCustomerId(), customerId);
        closeEntityManager(em);
    }

    public void testReadItem() {
        EntityManager em = createEntityManager();
        Item item = em.find(Item.class, itemId);
        assertEquals("Error reading Item", item.getItemId(), itemId);
        closeEntityManager(em);
    }

    public void testReadOrder() {
        EntityManager em = createEntityManager();
        Order order = em.find(Order.class, orderId);
        assertEquals("Error reading Order", order.getOrderId(), orderId);
        closeEntityManager(em);
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
        Customer newCustomer = em.find(Customer.class, customerId);
        assertEquals("Error updating Customer", "Dallas", newCustomer.getCity());
        closeEntityManager(em);
    }

    public void testUpdateItem() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Item item = em.find(Item.class, itemId);
            item.setDescription("A Widget");
            item.setImage(new byte[1280]);
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
        Item newItem = em.find(Item.class, itemId);
        assertEquals("Error updating Item description", "A Widget", newItem.getDescription());
        assertEquals("Error updating Item image", 1280, newItem.getImage().length);
        closeEntityManager(em);
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
        Customer newCustomer = em.find(Customer.class, customerId);
        assertEquals("Error updating Customer", 100, (newCustomer.getOrders().iterator().next()).getQuantity());
        closeEntityManager(em);
    }

    /**
     * This tests a couple scenarios:
     * - 1-M mapped by a M-1 using a JoinTable
     * - 1-1 mapped using a JoinTable (uni-directional)
     * - 1-1 mapped using a JoinTable (bi-directional)
     */
    public void testOne2OneRelationTables() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Order order1 = new Order();
        Order order2 = new Order();
        Auditor auditor = new Auditor();
        try {
            OrderCard order1Card = new OrderCard();
            OrderLabel order1Label = new OrderLabel();
            order1Label.setDescription("I describe order 1");
            order1.setOrderLabel(order1Label);
            order1.setOrderCard(order1Card);
            em.persist(order1);

            OrderCard order2Card = new OrderCard();
            OrderLabel order2Label = new OrderLabel();
            order2Label.setDescription("I describe order 2");
            order2.setOrderLabel(order2Label);
            order2.setOrderCard(order2Card);
            em.persist(order2);

            auditor.setName("Guillaume");
            auditor.addOrder(order1);
            auditor.addOrder(order2);
            em.persist(auditor);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw e;
        }

        closeEntityManager(em);


        clearCache();
        em = createEntityManager();

        Auditor refreshedAuditor = em.find(Auditor.class, auditor.getId());
        Order refreshedOrder1 = em.find(Order.class, order1.getOrderId());
        Order refreshedOrder2 = em.find(Order.class, order2.getOrderId());

        assertTrue("Auditor read back did not match the original", getPersistenceUnitServerSession().compareObjects(auditor, refreshedAuditor));
        assertTrue("Order1 read back did not match the original", getPersistenceUnitServerSession().compareObjects(order1, refreshedOrder1));
        assertTrue("Order2 read back did not match the original", getPersistenceUnitServerSession().compareObjects(order2, refreshedOrder2));

        closeEntityManager(em);
    }
}
