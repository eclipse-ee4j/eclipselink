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
 *     03/26/2008-1.0M6 Guy Pelletier 
 *       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema 
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.relationships;

import javax.persistence.EntityManager;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;
import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.relationships.Auditor;
import org.eclipse.persistence.testing.models.jpa.relationships.CustomerCollection;
import org.eclipse.persistence.testing.models.jpa.relationships.CustomerServiceRepresentative;
import org.eclipse.persistence.testing.models.jpa.relationships.Lego;
import org.eclipse.persistence.testing.models.jpa.relationships.Item;
import org.eclipse.persistence.testing.models.jpa.relationships.Mattel;
import org.eclipse.persistence.testing.models.jpa.relationships.MegaBrands;
import org.eclipse.persistence.testing.models.jpa.relationships.Namco;
import org.eclipse.persistence.testing.models.jpa.relationships.OrderCard;
import org.eclipse.persistence.testing.models.jpa.relationships.OrderLabel;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsTableManager;
import org.eclipse.persistence.testing.models.jpa.relationships.Order;
import org.eclipse.persistence.testing.models.jpa.relationships.ServiceCall;
import org.eclipse.persistence.testing.models.jpa.relationships.TestInstantiationCopyPolicy;

import org.eclipse.persistence.testing.models.jpa.relationships.Customer;

public class RelationshipModelJUnitTestSuite extends JUnitTestCase {
    private static Integer itemId;
    
    public RelationshipModelJUnitTestSuite() {
        super();
    }
    
    public RelationshipModelJUnitTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("RelationshipModelJUnitTestSuite");
        
        suite.addTest(new RelationshipModelJUnitTestSuite("testSetup")); 
        suite.addTest(new RelationshipModelJUnitTestSuite("testCreateItem")); 
        suite.addTest(new RelationshipModelJUnitTestSuite("testModifyItem"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testVerifyItem"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testInstantiationCopyPolicy"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testCopyPolicy"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testCloneCopyPolicy"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testCollectionImplementation"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testCustomerServiceRepMap"));
        
        suite.addTest(new RelationshipModelJUnitTestSuite("testOne2OneRelationTables"));

        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    /**
     * Create a new item that has a variable one to one to a manufacturer.
     */
    public void testCreateItem() {
        EntityManager em = createEntityManager();
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
            itemId = item.getItemId();
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
    public void testModifyItem() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            Item item = em.find(Item.class, itemId);
            item.setName("Willy Waller");
            item.setDescription("For adults only!");
            
            assertTrue("The manufacturer was not persisted", item.getManufacturer() != null);
            assertTrue("The manufacturer of the item was incorrect", item.getManufacturer().getName().equals("Mattel Inc."));
            
            Lego lego = new Lego();
            lego.setName("The LEGO Group");
            item.setManufacturer(lego);

            assertTrue("The distributor was not persisted", item.getDistributor() != null);
            assertTrue("The distributor of the item was incorrect", item.getDistributor().getName().equals("Namco Games"));
                    
            MegaBrands megaBrands = new MegaBrands();
            megaBrands.setName("MegaBrands Inc.");
            item.setDistributor(megaBrands);
            
            em.merge(item);
            em.persist(lego);
            em.persist(megaBrands);
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
    public void testVerifyItem() {
        EntityManager em = createEntityManager();
        Item item = em.find(Item.class, itemId);
        
        assertTrue("The manufacturer was not persisted", item.getManufacturer() != null);
        assertTrue("The manufacturer of the item was incorrect", item.getManufacturer().getName().equals("The LEGO Group"));

        assertTrue("The distributor was not persisted", item.getDistributor() != null);
        assertTrue("The distributor of the item was incorrect", item.getDistributor().getName().equals("MegaBrands Inc."));
        
        closeEntityManager(em);
    }
    
    public void testInstantiationCopyPolicy(){
        assertTrue("The InstantiationCopyPolicy was not properly set.", getServerSession().getDescriptor(Item.class).getCopyPolicy() instanceof InstantiationCopyPolicy);
    }
    
    public void testCopyPolicy(){
        assertTrue("The CopyPolicy was not properly set.", getServerSession().getDescriptor(Order.class).getCopyPolicy() instanceof TestInstantiationCopyPolicy);
    }
    
    public void testCloneCopyPolicy(){
        CopyPolicy copyPolicy = getServerSession().getDescriptor(Namco.class).getCopyPolicy();
        assertTrue("The CloneCopyPolicy was not properly set.", copyPolicy  instanceof CloneCopyPolicy);
        assertTrue("The method on CloneCopyPolicy was not properly set.", ((CloneCopyPolicy)copyPolicy).getMethodName().equals("cloneNamco"));
        assertTrue("The workingCopyMethod on CloneCopyPolicy was not properly set.", ((CloneCopyPolicy)copyPolicy).getWorkingCopyMethodName().equals("cloneWorkingCopyNamco"));
    }
    
    /**
     * bug 236275: Use a Collection implementation type that does not implement List in an eager relationship
     * This test uses a HashSet 
     */
    public void testCollectionImplementation(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Customer c = new Customer();
            //Customer uses HashSet by default, but set it anyway to ensure the model doesn't change. 
            CustomerCollection collection = new CustomerCollection();
            c.setCCustomers(collection);
            em.persist(c);
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
    
    // Bug 282571 - Map and ManyToMany
    public void testCustomerServiceRepMap(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Customer cust = new Customer();
        cust.setName("Kovie");
        cust.setCity("Ottawa");
        
        CustomerServiceRepresentative rep = new CustomerServiceRepresentative();
        rep.setName("Brian");
        
        ServiceCall call = new ServiceCall();
        call.setDescription("Trade from Habs.");
        
        cust.addCSInteraction(call, rep);
        
        em.persist(call);
        em.persist(cust);
        em.flush();
        
        rollbackTransaction(em);
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
        
        assertTrue("Auditor read back did not match the original", getServerSession().compareObjects(auditor, refreshedAuditor));
        assertTrue("Order1 read back did not match the original", getServerSession().compareObjects(order1, refreshedOrder1));
        assertTrue("Order2 read back did not match the original", getServerSession().compareObjects(order2, refreshedOrder2));
        
        closeEntityManager(em);
    }
}
