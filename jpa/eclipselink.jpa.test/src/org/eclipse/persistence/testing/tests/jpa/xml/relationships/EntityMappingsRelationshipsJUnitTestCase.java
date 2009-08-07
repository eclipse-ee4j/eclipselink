/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.xml.relationships;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.*;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;
import org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy;

import org.eclipse.persistence.testing.models.jpa.xml.relationships.Lego;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Mattel;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.MegaBrands;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Namco;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Customer;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Item;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Order;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.RelationshipsTableManager;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.TestInstantiationCopyPolicy;
import org.eclipse.persistence.testing.tests.jpa.TestingProperties;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class EntityMappingsRelationshipsJUnitTestCase extends JUnitTestCase {
    private static Integer customerId;
    private static Integer itemId;
    private static Integer extendedItemId;
    private static Integer orderId;
    
    private String m_persistenceUnit;
    
    public EntityMappingsRelationshipsJUnitTestCase() {
        super();
    }
    
    public EntityMappingsRelationshipsJUnitTestCase(String name) {
        super(name);
    }
    
    public EntityMappingsRelationshipsJUnitTestCase(String name, String persistenceUnit) {
        super(name);
        
        m_persistenceUnit = persistenceUnit;
    }
    
    public static Test suite() {
        String ormTesting = TestingProperties.getProperty(TestingProperties.ORM_TESTING, TestingProperties.JPA_ORM_TESTING);
        final String persistenceUnit = ormTesting.equals(TestingProperties.JPA_ORM_TESTING)? "default" : "extended-relationships";

        TestSuite suite = new TestSuite("Relationships Model - " + persistenceUnit);
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testSetup", persistenceUnit));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testCreateCustomer", persistenceUnit));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testCreateItem", persistenceUnit));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testCreateOrder", persistenceUnit));

        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testReadCustomer", persistenceUnit));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testReadItem", persistenceUnit));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testReadOrder", persistenceUnit));

        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testNamedQueryOnCustomer", persistenceUnit));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testNamedQueryOnItem", persistenceUnit));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testNamedQueryOnOrder", persistenceUnit));

        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testUpdateCustomer", persistenceUnit));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testUpdateItem", persistenceUnit));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testUpdateOrder", persistenceUnit));

        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testDeleteOrder", persistenceUnit));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testDeleteCustomer", persistenceUnit));
        suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testDeleteItem", persistenceUnit));
        
        if (persistenceUnit.equals("extended-relationships")) {
            suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testExcludeDefaultMappings", persistenceUnit));
            suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testCreateExtendedItem", persistenceUnit)); 
            suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testModifyExtendedItem", persistenceUnit));
            suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testVerifyExtendedItem", persistenceUnit));
            suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testCopyPolicy", persistenceUnit));
            suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testCloneCopyPolicy", persistenceUnit));
            suite.addTest(new EntityMappingsRelationshipsJUnitTestCase("testInstantiationCopyPolicy", persistenceUnit));
        }
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        DatabaseSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        new RelationshipsTableManager().replaceTables(session);
        clearCache(m_persistenceUnit);
    }
    
    public void testCreateCustomer() {
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        assertTrue("Error deleting Customer", em.find(Customer.class, customerId) == null);
        closeEntityManager(em);
    }

    public void testDeleteItem() {
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        closeEntityManager(em);
    }

    public void testDeleteOrder() {
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        assertTrue("Error deleting Order", em.find(Order.class, orderId) == null);
        closeEntityManager(em);
    }

    public void testNamedQueryOnCustomer() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        Customer customer = (Customer)em.createNamedQuery("findAllXMLCustomers").getSingleResult();
        assertTrue("Error executing named query 'findAllXMLCustomers'", customer != null);
        closeEntityManager(em);
    }

    public void testNamedQueryOnOrder() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        Query query = em.createNamedQuery("findAllXMLOrdersByItem");
        query.setParameter("id", itemId);
        Order order = (Order) query.getSingleResult();
        assertTrue("Error executing named query 'findAllXMLOrdersByItem'", order != null);
        closeEntityManager(em);
    }

    public void testNamedQueryOnItem() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        Query query = em.createNamedQuery("findAllXMLItemsByName");
        query.setParameter("1", "Widget");
        Item item = (Item) query.getSingleResult();
        assertTrue("Error executing named query 'findAllXMLItemsByName'", item != null);
        closeEntityManager(em);
    }

    public void testReadCustomer() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        Customer customer = em.find(Customer.class, customerId);
        assertTrue("Error reading Customer", customer.getCustomerId().equals(customerId));
        closeEntityManager(em);
    }
    
    public void testReadItem() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        Item item = em.find(Item.class, itemId);
        assertTrue("Error reading Item", item.getItemId().equals(itemId));
        closeEntityManager(em);
    }

    public void testReadOrder() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        Order order = em.find(Order.class, orderId);
        assertTrue("Error reading Order", order.getOrderId().equals(orderId));
        closeEntityManager(em);
    }

    public void testUpdateCustomer() {
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        clearCache(m_persistenceUnit);
        Customer newCustomer = em.find(Customer.class, customerId);
        assertTrue("Error updating Customer", newCustomer.getCity().equals("Dallas"));
        closeEntityManager(em);
    }

    public void testUpdateItem() {
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        clearCache(m_persistenceUnit);
        Item newItem = em.find(Item.class, itemId);
        assertTrue("Error updating Item description", newItem.getDescription().equals("A Widget"));
        assertTrue("Error updating Item image", newItem.getImage().length==1280);
        closeEntityManager(em);
    }

    public void testUpdateOrder() {
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        clearCache(m_persistenceUnit);
        Customer newCustomer = em.find(Customer.class, customerId);
        assertTrue("Error updating Customer", (newCustomer.getOrders().iterator().next()).getQuantity() == 100);
        closeEntityManager(em);
    }
    
    public void testExcludeDefaultMappings() {
        ClassDescriptor descriptor = getServerSession(m_persistenceUnit).getDescriptor(Mattel.class);
        assertNull("The 'ignoredBasic' attribute from the clas Mattel was mapped despite an exclude-default-mappings setting of true.", descriptor.getMappingForAttributeName("ignoredBasic"));
        assertNull("The 'ignoredOneToOne' attribute from the clas Mattel was mapped despite an exclude-default-mappings setting of true.", descriptor.getMappingForAttributeName("ignoredOneToOne"));
        assertNull("The 'ignoredVariableOneToOne' attribute from the clas Mattel was mapped despite an exclude-default-mappings setting of true.", descriptor.getMappingForAttributeName("ignoredVariableOneToOne"));
        assertNull("The 'ignoredOneToMany' attribute from the clas Mattel was mapped despite an exclude-default-mappings setting of true.", descriptor.getMappingForAttributeName("ignoredOneToMany"));
    }
    
    /**
     * Create a new item that has a variable one to one to a manufacturer.
     */
    public void testCreateExtendedItem() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        
        try {
            Item item = new Item();
            item.setName("Synergizer2000");
            item.setDescription("Every kid must have one ... ");
            
            // Manufacturer does not cascade persist
            Mattel mattel = new Mattel();
            mattel.setName("Mattel Inc.");
            em.persist(mattel);
            item.setManufacturer(mattel);
            
            // Distributor will cascade persist
            Namco namco = new Namco();
            namco.setName("Namco Games");
            item.setDistributor(namco);
            
            em.persist(item);
            extendedItemId = item.getItemId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        closeEntityManager(em);
    }
    
    /**
     * Read an item, verify it contents, modify it and commit.
     */
    public void testModifyExtendedItem() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        beginTransaction(em);
        
        try {
            Item item = em.find(Item.class, extendedItemId);
            item.setName("Willy Waller");
            item.setDescription("For adults only!");
            
            assertTrue("The manufacturer was not persisted", item.getManufacturer() != null);
            assertTrue("The manufacturer of the item was incorrect", item.getManufacturer().getName().equals("Mattel Inc."));
            
            Lego lego = new Lego();
            lego.setName("The LEGO Group");
            em.persist(lego);
            item.setManufacturer(lego);

            assertTrue("The distributor was not persisted", item.getDistributor() != null);
            assertTrue("The distributor of the item was incorrect", item.getDistributor().getName().equals("Namco Games"));
                    
            MegaBrands megaBrands = new MegaBrands();
            megaBrands.setName("MegaBrands Inc.");
            em.persist(megaBrands);
            item.setDistributor(megaBrands);
            
            em.merge(item); // no op really ...
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        closeEntityManager(em);
    }
    
    /**
     * Verify the final contents of item.
     */
    public void testVerifyExtendedItem() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        Item item = em.find(Item.class, extendedItemId);
        
        assertTrue("The manufacturer was not persisted", item.getManufacturer() != null);
        assertTrue("The manufacturer of the item was incorrect [" + item.getManufacturer().getName() + "]", item.getManufacturer().getName().equals("The LEGO Group"));

        assertTrue("The distributor was not persisted", item.getDistributor() != null);
        assertTrue("The distributor of the item was incorrect [" + item.getDistributor().getName() + "]", item.getDistributor().getName().equals("MegaBrands Inc."));
        
        closeEntityManager(em);
    }
    
    public void testInstantiationCopyPolicy(){
        assertTrue("The InstantiationCopyPolicy was not properly set.", getServerSession(m_persistenceUnit).getDescriptor(Item.class).getCopyPolicy() instanceof InstantiationCopyPolicy);
    }
    
    public void testCopyPolicy(){
        assertTrue("The CopyPolicy was not properly set.", getServerSession(m_persistenceUnit).getDescriptor(Order.class).getCopyPolicy() instanceof TestInstantiationCopyPolicy);
    }
    
    public void testCloneCopyPolicy(){
        CopyPolicy copyPolicy = getServerSession(m_persistenceUnit).getDescriptor(Namco.class).getCopyPolicy();
        assertTrue("The CloneCopyPolicy was not properly set.", copyPolicy  instanceof CloneCopyPolicy);
        assertTrue("The method on CloneCopyPolicy was not properly set.", ((CloneCopyPolicy)copyPolicy).getMethodName().equals("cloneNamco"));
        assertTrue("The workingCopyMethod on CloneCopyPolicy was not properly set.", ((CloneCopyPolicy)copyPolicy).getWorkingCopyMethodName().equals("cloneWorkingCopyNamco"));
    }
}
