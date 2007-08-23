/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.tests.jpa.xml.relationships;

import javax.persistence.EntityManager;

import junit.framework.*;
import junit.extensions.TestSetup;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Customer;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Item;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Order;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.RelationshipsTableManager;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class EntityMappingsRelationshipsJUnitTestCase extends JUnitTestCase {
    private static Integer customerId;
    private static Integer itemId;
    private static Integer orderId;
    
    public EntityMappingsRelationshipsJUnitTestCase() {
        super();
    }
    
    public EntityMappingsRelationshipsJUnitTestCase(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Relationships Model");
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testCreateCustomer"));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testCreateItem"));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testCreateOrder"));

        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testReadCustomer"));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testReadItem"));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testReadOrder"));

        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testNamedQueryOnCustomer"));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testNamedQueryOnItem"));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testNamedQueryOnOrder"));

        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testUpdateCustomer"));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testUpdateItem"));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testUpdateOrder"));

        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testDeleteOrder"));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testDeleteCustomer"));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testDeleteItem"));
        
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
        
    }
    public void testCreateItem() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            Item item = new Item();
            item.setName("Widget");
            item.setDescription("This is a Widget");
            item.setImage(new byte[1024]);
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
        Customer customer = (Customer)createEntityManager().createNamedQuery("findAllXMLCustomers").getSingleResult();
        assertTrue("Error executing named query 'findAllXMLCustomers'", customer != null);
    }

    public void testNamedQueryOnOrder() {
        EJBQueryImpl query = (EJBQueryImpl) createEntityManager().createNamedQuery("findAllXMLOrdersByItem");
        query.setParameter("id", itemId);
        Order order = (Order) query.getSingleResult();
        assertTrue("Error executing named query 'findAllXMLOrdersByItem'", order != null);
    }

    public void testNamedQueryOnItem() {
        EJBQueryImpl query = (EJBQueryImpl) createEntityManager().createNamedQuery("findAllXMLItemsByName");
        query.setParameter("1", "Widget");
        Item item = (Item) query.getSingleResult();
        assertTrue("Error executing named query 'findAllXMLItemsByName'", item != null);
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
        Customer newCustomer = (Customer) em.find(Customer.class, customerId);
        assertTrue("Error updating Customer", newCustomer.getCity().equals("Dallas"));
    }

    public void testUpdateItem() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        try {
            Item item = (Item) em.find(Item.class, itemId);
            item.setDescription("A Widget");
            item.setImage(new byte[1280]);
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
        Item newItem = (Item) em.find(Item.class, itemId);
        assertTrue("Error updating Item description", newItem.getDescription().equals("A Widget"));
        assertTrue("Error updating Item image", newItem.getImage().length==1280);
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
        Customer newCustomer = (Customer) em.find(Customer.class, customerId);
        assertTrue("Error updating Customer", ((Order) newCustomer.getOrders().iterator().next()).getQuantity() == 100);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.main(args);
    }
}
