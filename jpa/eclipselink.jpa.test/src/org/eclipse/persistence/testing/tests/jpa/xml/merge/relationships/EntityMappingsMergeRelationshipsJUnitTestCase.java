/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.xml.merge.relationships;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.xml.merge.relationships.Customer;
import org.eclipse.persistence.testing.models.jpa.xml.merge.relationships.Item;
import org.eclipse.persistence.testing.models.jpa.xml.merge.relationships.Order;
import org.eclipse.persistence.testing.models.jpa.xml.merge.relationships.PartsList;
import org.eclipse.persistence.testing.models.jpa.xml.merge.relationships.RelationshipsTableManager;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class EntityMappingsMergeRelationshipsJUnitTestCase extends JUnitTestCase {
    private static Integer customerId;
    private static Integer itemId;
    private static Integer orderId;
    
    public EntityMappingsMergeRelationshipsJUnitTestCase() {
        super();
    }
    
    public EntityMappingsMergeRelationshipsJUnitTestCase(String name) {
        super(name);
    }
    
    public void setUp() {try{super.setUp();}catch(Exception x){}}
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Relationships Model");
        
        // These tests will verify some merging rules.
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testSetup"));
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testCustomerOrdersMapping"));
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testOrderCustomerMapping"));
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testItemNameMapping"));
        
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testCreateCustomer"));
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testCreateItem"));
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testCreateOrder"));

        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testReadCustomer"));
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testReadItem"));
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testReadOrder"));

        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testNamedQueryOnCustomer"));
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testNamedQueryOnItem"));
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testNamedQueryOnOrder"));

        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testUpdateCustomer"));
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testUpdateItem"));
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testUpdateOrder"));

        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testDeleteOrder"));
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testDeleteCustomer"));
        suite.addTest(new EntityMappingsMergeRelationshipsJUnitTestCase("testDeleteItem"));
        
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
    
    /**
     * Verifies the merging of Customer's one to many mapping 'orders'.
     */
    public void testCustomerOrdersMapping() {
        ServerSession session = JUnitTestCase.getServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Customer.class);
        ForeignReferenceMapping mapping = (ForeignReferenceMapping) descriptor.getMappingForAttributeName("orders");
        
        assertTrue("Orders mapping on Customer is not set to cascade persist.", mapping.isCascadePersist());
        assertTrue("Orders mapping on Customer is not set to cascade remove.", mapping.isCascadeRemove());
        assertFalse("Orders mapping on Customer is set to cascade refresh.", mapping.isCascadeRefresh());
        assertFalse("Orders mapping on Customer is set to cascade merge.", mapping.isCascadeMerge());
        assertFalse("Orders mapping on Customer is set to private owned.", mapping.isPrivateOwned());
    }
    
    /**
     * Verifies the merging of Order's many to one mapping 'customer'.
     */
    public void testOrderCustomerMapping() {
        ServerSession session = JUnitTestCase.getServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Order.class);
        ForeignReferenceMapping mapping = (ForeignReferenceMapping) descriptor.getMappingForAttributeName("customer");
        if (isWeavingEnabled()) {
            assertTrue("Customer mapping on Order is not set to LAZY loading.", mapping.usesIndirection());
        }
    }
    
    /**
     * Verifies the merging of Item's basic mapping 'name'.
     */
    public void testItemNameMapping() {
        ServerSession session = JUnitTestCase.getServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Item.class);
        DirectToFieldMapping mapping = (DirectToFieldMapping) descriptor.getMappingForAttributeName("name");
        
        assertFalse("Customer mapping on Order is not set to LAZY loading.", mapping.isMutable());
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

            PartsList pl = new PartsList();
            em.persist(pl);
            PartsList pl2 = new PartsList();
            em.persist(pl2);
            
            java.util.ArrayList partsLists = new java.util.ArrayList();
            partsLists.add(pl);
            partsLists.add(pl2);

            Item item = new Item();
            item.name = "PartA";
            item.setDescription("This is part of a widget.");
            item.setImage(new byte[1024]);
            //item.image = new byte[1024];
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

    public void testNamedQueryOnCustomer() {
        Customer customer = (Customer)createEntityManager().createNamedQuery("findAllXMLMergeCustomers").getSingleResult();
        assertTrue("Error executing named query 'findAllXMLMergeCustomers'", customer != null);
    }

    public void testNamedQueryOnOrder() {
        Query query = createEntityManager().createNamedQuery("findAllXMLMergeOrdersByItem");
        query.setParameter("id", itemId);
        Order order = (Order) query.getSingleResult();
        assertTrue("Error executing named query 'findAllXMLMergeOrdersByItem'", order != null);
    }

    public void testNamedQueryOnItem() {
        Query query = createEntityManager().createNamedQuery("findAllXMLMergeItemsByName");
        query.setParameter("1", "PartA");
        Item item = (Item) query.getSingleResult();
        assertTrue("Error executing named query 'findAllXMLMergeItemsByName'", item != null);
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
