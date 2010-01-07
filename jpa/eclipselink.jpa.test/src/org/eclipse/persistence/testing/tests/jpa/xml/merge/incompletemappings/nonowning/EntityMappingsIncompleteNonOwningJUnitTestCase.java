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


package org.eclipse.persistence.testing.tests.jpa.xml.merge.incompletemappings.nonowning;

import javax.persistence.EntityManager;

import junit.framework.*;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning.Address;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning.Customer;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning.Item;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning.Order;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning.PartsList;
import org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning.RelationshipsTableManager;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class EntityMappingsIncompleteNonOwningJUnitTestCase extends JUnitTestCase {
    private static Integer customerId;
    private static Integer itemId;
    private static Integer orderId;
    
    public EntityMappingsIncompleteNonOwningJUnitTestCase() {
        super();
    }
    
    public EntityMappingsIncompleteNonOwningJUnitTestCase(String name) {
        super(name);
    }
    
    public void setUp() {try{super.setUp();}catch(Exception x){}}
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Non-owning Model");
        suite.addTest(new EntityMappingsIncompleteNonOwningJUnitTestCase("testSetup"));
        suite.addTest(new EntityMappingsIncompleteNonOwningJUnitTestCase("testCreateCustomer"));
        suite.addTest(new EntityMappingsIncompleteNonOwningJUnitTestCase("testCreateItem"));
        suite.addTest(new EntityMappingsIncompleteNonOwningJUnitTestCase("testCreateOrder"));

        suite.addTest(new EntityMappingsIncompleteNonOwningJUnitTestCase("testReadCustomer"));
        suite.addTest(new EntityMappingsIncompleteNonOwningJUnitTestCase("testReadItem"));
        suite.addTest(new EntityMappingsIncompleteNonOwningJUnitTestCase("testReadOrder"));

        suite.addTest(new EntityMappingsIncompleteNonOwningJUnitTestCase("testUpdateCustomer"));
        suite.addTest(new EntityMappingsIncompleteNonOwningJUnitTestCase("testUpdateItem"));
        suite.addTest(new EntityMappingsIncompleteNonOwningJUnitTestCase("testUpdateOrder"));

        suite.addTest(new EntityMappingsIncompleteNonOwningJUnitTestCase("testDeleteOrder"));
        suite.addTest(new EntityMappingsIncompleteNonOwningJUnitTestCase("testDeleteCustomer"));
        suite.addTest(new EntityMappingsIncompleteNonOwningJUnitTestCase("testDeleteItem"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        DatabaseSession session = JUnitTestCase.getServerSession();
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
            
            java.util.ArrayList partsLists = new java.util.ArrayList();
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
        assertTrue("Error deleting Customer", em.find(Customer.class, customerId) == null);
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
        assertTrue("Error deleting Item", em.find(Item.class, itemId) == null);
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
        assertTrue("Error deleting Order", em.find(Order.class, orderId) == null);
    }

    public void testReadCustomer() {
        Customer customer = createEntityManager().find(Customer.class, customerId);
        assertTrue("Error reading Customer", customer.getCustomerId() == customerId);
    }
    
    public void testReadItem() {
        Item item = createEntityManager().find(Item.class, itemId);
        assertTrue("Error reading Item", item.getItemId() == itemId);
    }

    public void testReadOrder() {
        Order order = createEntityManager().find(Order.class, orderId);
        assertTrue("Error reading Order", order.getOrderId() == orderId);
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
        assertTrue("Error updating Customer", newCustomer.getCity().equals("Dallas"));
    }

    public void testUpdateItem() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            PartsList pl = new PartsList();
            em.persist(pl);
            java.util.ArrayList partsLists = new java.util.ArrayList();
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
        assertTrue("Error updating Item description", newItem.getDescription().equals("A Widget"));
        assertTrue("Error updating Item image", newItem.getImage().length==1280);
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
        assertTrue("Error updating Customer", (newCustomer.getOrders().iterator().next()).getQuantity() == 100);
    }

}
