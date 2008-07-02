/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;
import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.relationships.Lego;
import org.eclipse.persistence.testing.models.jpa.relationships.Item;
import org.eclipse.persistence.testing.models.jpa.relationships.Mattel;
import org.eclipse.persistence.testing.models.jpa.relationships.MegaBrands;
import org.eclipse.persistence.testing.models.jpa.relationships.Namco;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsTableManager;
import org.eclipse.persistence.testing.models.jpa.relationships.Order;
import org.eclipse.persistence.testing.models.jpa.relationships.TestInstantiationCopyPolicy;

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
        
        suite.addTest(new RelationshipModelJUnitTestSuite("testCreateItem")); 
        suite.addTest(new RelationshipModelJUnitTestSuite("testModifyItem"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testVerifyItem"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testInstantiationCopyPolicy"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testCopyPolicy"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testCloneCopyPolicy"));

        return new TestSetup(suite) {
            protected void setUp(){
                new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession());
            }

            protected void tearDown() {
                clearCache();
            }
        };
    }
   
    public void setUp () {
        super.setUp();
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
}
