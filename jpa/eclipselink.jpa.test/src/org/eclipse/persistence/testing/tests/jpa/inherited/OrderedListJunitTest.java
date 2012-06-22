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
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     08/15/2008-1.0.1 Chris Delahunt 
 *       - 237545: List attribute types on OneToMany using @OrderBy does not work with attribute change tracking
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.inherited;

import java.util.Date;
import java.util.Vector;

import javax.persistence.EntityManager;

import junit.framework.*;
import org.eclipse.persistence.testing.models.jpa.inherited.Alpine;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.SerialNumber;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;
 
@SuppressWarnings("deprecation")
public class OrderedListJunitTest extends JUnitTestCase {
    private static Integer m_beerConsumerId;
    
    public OrderedListJunitTest() {
        super();
    }
    
    public OrderedListJunitTest(String name) {
        super(name);
    }
    
    public void setUp() {
        super.setUp();
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("OrderedListJunitTest");
        suite.addTest(new OrderedListJunitTest("testSetup"));
        suite.addTest(new OrderedListJunitTest("testInitialize"));
        suite.addTest(new OrderedListJunitTest("test1"));
        suite.addTest(new OrderedListJunitTest("testInitialize"));
        suite.addTest(new OrderedListJunitTest("test2"));
        suite.addTest(new OrderedListJunitTest("testInitialize"));
        suite.addTest(new OrderedListJunitTest("test3"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new InheritedTableManager().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    public void testInitialize() {
        
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            BeerConsumer beerConsumer = new BeerConsumer();
            beerConsumer.setName("Guy Pelletier");
            em.persist(beerConsumer);
            m_beerConsumerId = beerConsumer.getId();

            SerialNumber serialNumber1 = new SerialNumber();
            em.persist(serialNumber1);
            Alpine alpine1 = new Alpine(serialNumber1);
            em.persist(alpine1);
            alpine1.setBestBeforeDate(new Date(2005, 8, 17));
            alpine1.setAlcoholContent(5.0);
            alpine1.setClassification(Alpine.Classification.STRONG);
            beerConsumer.addAlpineBeerToConsume(alpine1);
            
            SerialNumber serialNumber2 = new SerialNumber();
            em.persist(serialNumber2);
            Alpine alpine2 = new Alpine(serialNumber2);
            em.persist(alpine2);
            alpine2.setBestBeforeDate(new Date(2005, 8, 19));
            alpine2.setAlcoholContent(4.0);
            alpine2.setClassification(Alpine.Classification.STRONG);
            beerConsumer.addAlpineBeerToConsume(alpine2);
            
            SerialNumber serialNumber3 = new SerialNumber();
            em.persist(serialNumber3);
            Alpine alpine3 = new Alpine(serialNumber3);
            em.persist(alpine3);
            alpine3.setBestBeforeDate(new Date(2005, 8, 21));
            alpine3.setAlcoholContent(3.0);
            alpine3.setClassification(Alpine.Classification.STRONG);
            beerConsumer.addAlpineBeerToConsume(alpine3);
            
            SerialNumber serialNumber4 = new SerialNumber();
            em.persist(serialNumber4);
            Alpine alpine4 = new Alpine(serialNumber4);
            em.persist(alpine4);
            alpine4.setBestBeforeDate(new Date(2005, 8, 23));
            alpine4.setAlcoholContent(2.0);
            alpine4.setClassification(Alpine.Classification.BITTER);
            beerConsumer.addAlpineBeerToConsume(alpine4);

            SerialNumber serialNumber5 = new SerialNumber();
            em.persist(serialNumber5);
            Alpine alpine5 = new Alpine(serialNumber5);
            em.persist(alpine5);
            alpine5.setBestBeforeDate(new Date(2005, 8, 25));
            alpine5.setAlcoholContent(1.0);
            alpine5.setClassification(Alpine.Classification.SWEET);
            beerConsumer.addAlpineBeerToConsume(alpine5);
            
            commitTransaction(em);
            
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        }
        
    }
    
    public void test1() {
        BeerConsumer beerConsumer = null;
        Alpine alpine1 = null;
        Alpine alpine2 = null;
        EntityManager em = createEntityManager();
        try {
            beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
            
            beginTransaction(em);
        
            beerConsumer = em.merge(beerConsumer);

            alpine1 = beerConsumer.removeAlpineBeerToConsume(1);
            alpine2 = beerConsumer.removeAlpineBeerToConsume(1);
            
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            fail("An exception was caught during test1 : [" + ex.getMessage() + "]");
        }
            
        // Read the beerConsumer back from the EM.
        beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
        Vector alpinesFromCache =  (Vector) beerConsumer.getAlpineBeersToConsume();
        
        assertTrue("Incorrect number of alpines in the list", alpinesFromCache.size() == 3);
        assertFalse("Alpine 1 was not removed from the list", alpinesFromCache.contains(alpine1));
        assertFalse("Alpine 2 was not removed from the list", alpinesFromCache.contains(alpine2));
        
        
        // Read the beerConsumer back from the shared cache.
        em.clear();
        beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
        alpinesFromCache =  (Vector) beerConsumer.getAlpineBeersToConsume();
        
        assertTrue("Shared cache: Incorrect number of alpines in the list", alpinesFromCache.size() == 3);
        assertFalse("Shared cache: Alpine 1 was not removed from the list", alpinesFromCache.contains(alpine1));
        assertFalse("Shared cache: Alpine 2 was not removed from the list", alpinesFromCache.contains(alpine2));
        
        // Read the beerConsumer back from the database.
        this.clearCache();
        em.clear();
        beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
        alpinesFromCache =  (Vector) beerConsumer.getAlpineBeersToConsume();
        
        assertTrue("database: Incorrect number of alpines in the list", alpinesFromCache.size() == 3);
        assertFalse("database: Alpine 1 was not removed from the list", alpinesFromCache.contains(alpine1));
        assertFalse("database: Alpine 2 was not removed from the list", alpinesFromCache.contains(alpine2));
            
    }
    
    public void test2() {
        BeerConsumer beerConsumer = null;
        Alpine alpine1 = null;
        Alpine alpine2 = null;
        EntityManager em = createEntityManager();
        try {
            beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
            
            beginTransaction(em);
        
            beerConsumer = em.merge(beerConsumer);

            alpine1 = beerConsumer.moveAlpineBeerToConsume(2, 4);
            alpine2 = beerConsumer.moveAlpineBeerToConsume(1, 3);
            
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            fail("An exception was caught while adding the new alpine at a specific index: [" + ex.getMessage() + "]");
        }
        
        // Read the beerConsumer back from the EM.
        beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
        Vector alpinesFromCache =  (Vector) beerConsumer.getAlpineBeersToConsume();
        
        assertTrue("Incorrect number of alpines in the list", alpinesFromCache.size() == 5);
        assertTrue("Alpine 1 was not at the correct index.", alpinesFromCache.indexOf(alpine1) == 4);
        assertTrue("Alpine 2 was not at the correct index.", alpinesFromCache.indexOf(alpine2) == 3);
        
        // Read the beerConsumer back from the shared cache.
        em.clear();
        beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
        alpinesFromCache =  (Vector) beerConsumer.getAlpineBeersToConsume();
        
        assertTrue("Shared cache: Incorrect number of alpines in the list", alpinesFromCache.size() == 5);
        assertTrue("Shared cache: Alpine 1 was not at the correct index.", alpinesFromCache.indexOf(alpine1) == 4);
        assertTrue("Shared cache: Alpine 2 was not at the correct index.", alpinesFromCache.indexOf(alpine2) == 3);
        
        // Read the beerConsumer back from the database.
        this.clearCache();
        em.clear();
        beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
        alpinesFromCache =  (Vector) beerConsumer.getAlpineBeersToConsume();
        //ensure the collection was reordered correctly
        assertTrue("database: Incorrect number of alpines in the list", alpinesFromCache.size() == 5);
        assertTrue("database: Alpine 1 was not at the correct index.", alpinesFromCache.indexOf(alpine1) == 2);
        assertTrue("database: Alpine 2 was not at the correct index.", alpinesFromCache.indexOf(alpine2) == 1);
    }
    
    public void test3() {
        
        BeerConsumer beerConsumer = null;
        Alpine alpine1 = null, alpine2 = null, alpine3 = null, alpine4 = null;
        EntityManager em = createEntityManager();
        //beerConsumer = (BeerConsumer)em.createQuery("select b from BeerConsumer b JOIN FETCH b.alpineBeersToConsume where b.id = "+m_beerConsumerId+" order by b.alpineBeersToConsume.bestBeforeDate").getSingleResult();
        try {
            beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);

            
            beginTransaction(em);
        
            beerConsumer = em.merge(beerConsumer);

            alpine1 = beerConsumer.moveAlpineBeerToConsume(4, 1);
            
            alpine2 = beerConsumer.removeAlpineBeerToConsume(0);
            
            alpine3 = beerConsumer.moveAlpineBeerToConsume(1, 3);
            
            SerialNumber serialNumber = new SerialNumber();
            em.persist(serialNumber);
            alpine4 = new Alpine(serialNumber);
            em.persist(alpine4);
            alpine4.setBestBeforeDate(new Date(2005, 8, 29));
            alpine4.setAlcoholContent(7.0);
            alpine4.setClassification(Alpine.Classification.SWEET);
            beerConsumer.addAlpineBeerToConsume(alpine4, 3);
                
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            fail("An exception was caught while adding the new alpine at a specific index: [" + ex.getMessage() + "]");
        }
        
        // Read the beerConsumer back from the shared cache.
        beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
        Vector alpinesFromCache =  (Vector) beerConsumer.getAlpineBeersToConsume();
        
        assertTrue("Incorrect number of alpines in the list", alpinesFromCache.size() == 5);
        assertTrue("Alpine 1 was not at the correct index.", alpinesFromCache.indexOf(alpine1) == 0);
        assertFalse("Alpine 2 was not removed from the list", alpinesFromCache.contains(alpine2));
        assertTrue("Alpine 3 was not at the correct index.", alpinesFromCache.indexOf(alpine3) == 4);
        assertTrue("Alpine 4 was not at the correct index.", alpinesFromCache.indexOf(alpine4) == 3);
        
        // Read the beerConsumer back from the shared cache.
        em.clear();
        beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
        alpinesFromCache =  (Vector) beerConsumer.getAlpineBeersToConsume();
        
        assertTrue("Shared cache: Incorrect number of alpines in the list", alpinesFromCache.size() == 5);
        assertTrue("Shared cache: Alpine 1 was not at the correct index.", alpinesFromCache.indexOf(alpine1) == 0);
        assertFalse("Shared cache: Alpine 2 was not removed from the list", alpinesFromCache.contains(alpine2));
        assertTrue("Shared cache: Alpine 3 was not at the correct index.", alpinesFromCache.indexOf(alpine3) == 4);
        assertTrue("Shared cache: Alpine 4 was not at the correct index.", alpinesFromCache.indexOf(alpine4) == 3);
        
        // Read the beerConsumer back from the database. 
        this.clearCache();
        em.clear();
        beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
        alpinesFromCache =  (Vector) beerConsumer.getAlpineBeersToConsume();
        //ensure the collection was reordered correctly
        assertTrue("database: Incorrect number of alpines in the list", alpinesFromCache.size() == 5);
        assertTrue("database: Alpine 1 was not at the correct index.", alpinesFromCache.indexOf(alpine1) == 3);
        assertFalse("database: Alpine 2 was not removed from the list", alpinesFromCache.contains(alpine2));
        assertTrue("database: Alpine 3 was not at the correct index.", alpinesFromCache.indexOf(alpine3) == 0);
        assertTrue("database: Alpine 4 was not at the correct index.", alpinesFromCache.indexOf(alpine4) == 4);
        
    }
}
