/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.tests.jpa.xml.merge.relationships;

import javax.persistence.EntityManager;

import junit.framework.*;
import junit.extensions.TestSetup;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.sessions.DatabaseSession;
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
        
        return new TestSetup(suite) {
            
            protected void setUp(){  
            	DatabaseSession session = JUnitTestCase.getServerSession();   
                new RelationshipsTableManager().replaceTables(session);
            }
        
            protected void tearDown() {
                clearCache();
            }
        };
    }
    
    public void testCreateCustomer() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            Customer customer = new Customer();
            customer.setName("Joe Black");
            customer.setCity("Austin");
            em.persist(customer);
            customerId = customer.getCustomerId();
            em.getTransaction().commit();    
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        em.close();        
    }
    
    public void testCreateItem() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
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
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        em.close();
    }

    public void testCreateOrder() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            Order order = new Order();
            order.setShippingAddress("50 O'Connor St.");
            Customer customer = (Customer) em.find(Customer.class, customerId);
            order.setCustomer(customer);
            order.setQuantity(1);
            Item item = (Item) em.find(Item.class, itemId);
            order.setItem(item);
            em.persist(order);
            orderId = order.getOrderId();
            em.getTransaction().commit();    
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
    }

    public void testDeleteCustomer() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            em.remove(em.find(Customer.class, customerId));
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        assertTrue("Error deleting Customer", em.find(Customer.class, customerId) == null);
    }

    public void testDeleteItem() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            em.remove(em.find(Item.class, itemId));
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        assertTrue("Error deleting Item", em.find(Item.class, itemId) == null);
    }

    public void testDeleteOrder() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            em.remove(em.find(Order.class, orderId));
        	em.refresh(em.find(Customer.class, customerId)); //refresh Customer
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        assertTrue("Error deleting Order", em.find(Order.class, orderId) == null);
    }

    public void testNamedQueryOnCustomer() {
        Customer customer = (Customer)createEntityManager().createNamedQuery("findAllXMLMergeCustomers").getSingleResult();
        assertTrue("Error executing named query 'findAllXMLMergeCustomers'", customer != null);
    }

    public void testNamedQueryOnOrder() {
        EJBQueryImpl query = (EJBQueryImpl) createEntityManager().createNamedQuery("findAllXMLMergeOrdersByItem");
        query.setParameter("id", itemId);
        Order order = (Order) query.getSingleResult();
        assertTrue("Error executing named query 'findAllXMLMergeOrdersByItem'", order != null);
    }

    public void testNamedQueryOnItem() {
        EJBQueryImpl query = (EJBQueryImpl) createEntityManager().createNamedQuery("findAllXMLMergeItemsByName");
        query.setParameter("1", "PartA");
        Item item = (Item) query.getSingleResult();
        assertTrue("Error executing named query 'findAllXMLMergeItemsByName'", item != null);
    }

    public void testReadCustomer() {
        Customer customer = (Customer) createEntityManager().find(Customer.class, customerId);
        assertTrue("Error reading Customer", customer.getCustomerId() == customerId);
    }
    
    public void testReadItem() {
        Item item = (Item) createEntityManager().find(Item.class, itemId);
        assertTrue("Error reading Item", item.getItemId() == itemId);
    }

    public void testReadOrder() {
        Order order = (Order) createEntityManager().find(Order.class, orderId);
        assertTrue("Error reading Order", order.getOrderId() == orderId);
    }

    public void testUpdateCustomer() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            Customer customer = (Customer) em.find(Customer.class, customerId);
            customer.setCity("Dallas");
            em.merge(customer);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        clearCache();
        em.clear();
        Customer newCustomer = (Customer) em.find(Customer.class, customerId);
        assertTrue("Error updating Customer", newCustomer.getCity().equals("Dallas"));
    }

    public void testUpdateItem() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            PartsList pl = new PartsList();
            em.persist(pl);
            java.util.ArrayList partsLists = new java.util.ArrayList();
            partsLists.add(pl);
            
            Item item = (Item) em.find(Item.class, itemId);
            item.setDescription("A Widget");
            item.setImage(new byte[1280]);
            item.setPartsLists(partsLists);
            em.merge(item);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        clearCache();
        em.clear();
        Item newItem = (Item) em.find(Item.class, itemId);
        assertTrue("Error updating Item description", newItem.getDescription().equals("A Widget"));
        assertTrue("Error updating Item image", newItem.getImage().length==1280);
        assertTrue("Error updating Item parts lists", newItem.getPartsLists().size() != 1);
    }

    public void testUpdateOrder() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            Customer customer = (Customer) em.find(Customer.class, customerId);
            Order order = (Order) customer.getOrders().iterator().next();
            order.setQuantity(100);
            em.merge(customer);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        clearCache();
        em.clear();
        Customer newCustomer = (Customer) em.find(Customer.class, customerId);
        assertTrue("Error updating Customer", ((Order) newCustomer.getOrders().iterator().next()).getQuantity() == 100);
    }

}
