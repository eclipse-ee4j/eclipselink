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
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.inherited;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;

import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Blue;
import org.eclipse.persistence.testing.models.jpa.inherited.Alpine;
import org.eclipse.persistence.testing.models.jpa.inherited.BlueLight;
import org.eclipse.persistence.testing.models.jpa.inherited.ExpertBeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;
import org.eclipse.persistence.testing.models.jpa.inherited.NoviceBeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.SerialNumber;
 
public class InheritedModelJunitTest extends JUnitTestCase {
    private static BigDecimal m_blueId;
    private static Integer m_beerConsumerId;
    private static Integer m_noviceBeerConsumerId;
    private static Integer m_expertBeerConsumerId;
    
    public InheritedModelJunitTest() {
        super();
    }
    
    public InheritedModelJunitTest(String name) {
        super(name);
    }
    
    public void setUp() {
        super.setUp();
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("InheritedModelJunitTest");
        
        suite.addTest(new InheritedModelJunitTest("testSetup"));
        suite.addTest(new InheritedModelJunitTest("testCreateBlue"));
        suite.addTest(new InheritedModelJunitTest("testReadBlue"));
        suite.addTest(new InheritedModelJunitTest("testCreateBeerConsumer"));
        suite.addTest(new InheritedModelJunitTest("testCreateNoviceBeerConsumer"));
        suite.addTest(new InheritedModelJunitTest("testCreateExpertBeerConsumer"));
        suite.addTest(new InheritedModelJunitTest("testReadNoviceBeerConsumer"));
        suite.addTest(new InheritedModelJunitTest("testReadExpertBeerConsumer"));
        suite.addTest(new InheritedModelJunitTest("testUpdateBeerConsumer"));
        suite.addTest(new InheritedModelJunitTest("testInheritedClone"));
        suite.addTest(new InheritedModelJunitTest("testCascadeRemove"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new InheritedTableManager().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    public void testCreateBlue() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            Blue blue = new Blue();
            blue.setAlcoholContent(new Float(5.3));
            em.persist(blue);
            m_blueId = blue.getId();
            blue.setUniqueKey(m_blueId.toBigInteger());
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
    }
    
    public void testReadBlue() {
        Blue blue = createEntityManager().find(Blue.class, m_blueId);
        
        assertTrue("Error on reading back a Blue beer", blue != null);
    }
    
    public void testCreateBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {    
            BeerConsumer beerConsumer = new BeerConsumer();
            beerConsumer.setName("Blue Consumer");
            em.persist(beerConsumer);
            m_beerConsumerId = beerConsumer.getId();
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
    }
    
    public void testCreateNoviceBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        ServerSession session = JUnitTestCase.getServerSession();
        ClassDescriptor desc = session.getDescriptor(NoviceBeerConsumer.class);
        System.out.println(desc.getMappings().size());
        
        try {    
            NoviceBeerConsumer beerConsumer = new NoviceBeerConsumer();
            beerConsumer.setName("Novice Beer Consumer");
            beerConsumer.setIQ(100);
            
            beerConsumer.getAcclaims().add(1);
            beerConsumer.getAcclaims().add(2);
            beerConsumer.getAcclaims().add(3);
            
            beerConsumer.getAwards().put(1, 1);
            beerConsumer.getAwards().put(2, 2);
            beerConsumer.getAwards().put(3, 3);
            
            em.persist(beerConsumer);
            m_noviceBeerConsumerId = beerConsumer.getId();
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during create operation for a novice beer consumer: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
    }
    
    public void testCreateExpertBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {    
            ExpertBeerConsumer beerConsumer = new ExpertBeerConsumer();
            beerConsumer.setName("Expert Beer Consumer");
            beerConsumer.setIQ(110);
            
            beerConsumer.getAcclaims().add("A");
            beerConsumer.getAcclaims().add("B");
            beerConsumer.getAcclaims().add("C");
            
            beerConsumer.getAwards().put("A", "A");
            beerConsumer.getAwards().put("B", "B");
            beerConsumer.getAwards().put("C", "C");
            
            em.persist(beerConsumer);
            m_expertBeerConsumerId = beerConsumer.getId();
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during create operation for an expert beer consumer: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
    }
    
    public void testReadNoviceBeerConsumer() {
        NoviceBeerConsumer consumer = createEntityManager().find(NoviceBeerConsumer.class, m_noviceBeerConsumerId);
        
        assertTrue("Error on reading back a NoviceBeerConsumer", consumer != null);
        
        assertTrue("IQ Level was not persisted.", consumer.getIQ() == 100);
        
        assertTrue("", consumer.getAcclaims().size() == 3);
        assertTrue("Missing acclaim - 1", consumer.getAcclaims().contains(1));
        assertTrue("Missing acclaim - 2", consumer.getAcclaims().contains(2));
        assertTrue("Missing acclaim - 3", consumer.getAcclaims().contains(3));
        
        assertTrue("", consumer.getAwards().size() == 3);
        Integer awardCode = consumer.getAwards().get(1);
        assertFalse("Missing award code - 1", awardCode == null);
        assertTrue("Award code 1 is incorrect", awardCode.equals(1));
        
        awardCode = consumer.getAwards().get(2);
        assertFalse("Missing award code - 2", awardCode == null);
        assertTrue("Award code 2 is incorrect", awardCode.equals(2));
        
        awardCode = consumer.getAwards().get(3);
        assertFalse("Missing award code - 3", awardCode == null);
        assertTrue("Award code 3 is incorrect", awardCode.equals(3));        
    }
    
    public void testReadExpertBeerConsumer() {
        ExpertBeerConsumer consumer = createEntityManager().find(ExpertBeerConsumer.class, m_expertBeerConsumerId);
        
        assertTrue("Error on reading back an ExpertBeerConsumer", consumer != null);
        
        assertTrue("IQ Level was not persisted.", consumer.getIQ() == 110);
        
        assertTrue("", consumer.getAcclaims().size() == 3);
        assertTrue("Missing acclaim - A", consumer.getAcclaims().contains("A"));
        assertTrue("Missing acclaim - B", consumer.getAcclaims().contains("B"));
        assertTrue("Missing acclaim - C", consumer.getAcclaims().contains("C"));
        
        assertTrue("", consumer.getAwards().size() == 3);
        String awardCode = consumer.getAwards().get("A");
        assertFalse("Missing award code - A", awardCode == null);
        assertTrue("Award code A is incorrect", awardCode.equals("A"));
        
        awardCode = consumer.getAwards().get("B");
        assertFalse("Missing award code - B", awardCode == null);
        assertTrue("Award code B is incorrect", awardCode.equals("B"));
        
        awardCode = consumer.getAwards().get("C");
        assertFalse("Missing award code - C", awardCode == null);
        assertTrue("Award code C is incorrect", awardCode.equals("C"));
    }
    
    public void testUpdateBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {    
            BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
            
            // Create a detached object that mirrors the beer consumer from
            // testCreateBeerConsumer.
            BeerConsumer beerConsumerDetached = new BeerConsumer();
            beerConsumerDetached.setName(beerConsumer.getName());
            beerConsumerDetached.setId(beerConsumer.getId());
            beerConsumerDetached.setVersion(beerConsumer.getVersion());
            
            Blue blue = em.find(Blue.class, m_blueId);
            beerConsumerDetached.addBlueBeerToConsume(blue);
            
            em.merge(beerConsumerDetached);
            commitTransaction(em);    
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during the merge of the detached beer consumer: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
    }
    
    // Test the clone method works with LAZY attributes at multiple levels of an inheritance hierarchy
    public void testInheritedClone() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        SerialNumber serialNumber = new SerialNumber();
        em.persist(serialNumber);
        Alpine alpine = new Alpine(serialNumber);
        alpine.setBestBeforeDate(Helper.dateFromYearMonthDate(2005, 8, 21));
        alpine.setAlcoholContent(5.0);
        alpine.setClassification(Alpine.Classification.BITTER);
        alpine.addInspectionDate(new Date(System.currentTimeMillis()));
        alpine.getBeerConsumer();
        Alpine clone = null;
        try{
            clone = (Alpine)alpine.clone();
        } catch (CloneNotSupportedException ex){
            fail("Caught CloneNotSupportedException " + ex);
        }
        
        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Keith Alexander");
        
        BlueLight blueLight = new BlueLight();
        blueLight.setAlcoholContent(new Float(4.0));
        blueLight.setUniqueKey(new BigInteger((new Long(System.currentTimeMillis()).toString())));
        em.persist(blueLight);
        consumer.addBlueLightBeerToConsume(blueLight);
        clone.setBeerConsumer(consumer);
        consumer.addAlpineBeerToConsume(clone);
        em.persist(clone);
        em.persist(consumer);
        if (alpine.getBeerConsumer() == clone.getBeerConsumer()) {
            fail("Changing clone beerConsumer changed original.");
        }
        commitTransaction(em);
        Integer alpineId = clone.getId();
        Integer consumerId = consumer.getId();
        BigDecimal blueLightId = blueLight.getId();
        clearCache();
        closeEntityManager(em);
        em = createEntityManager();
        beginTransaction(em);
        alpine = em.find(Alpine.class, clone.getId());
        try{
            clone = (Alpine)alpine.clone();
        } catch (CloneNotSupportedException ex){
            fail("Caught CloneNotSupportedException " + ex);
        }
        
        consumer = new BeerConsumer();
        consumer.setName("Frank Keith");
        clone.setBeerConsumer(consumer);
        consumer.addAlpineBeerToConsume(clone);
        clone.getBeerConsumer();
        if (alpine.getBeerConsumer() == clone.getBeerConsumer()) {
            fail("Changing clone beerConsumer changed original.");
        }
        if (alpine.getBeerConsumer() == null) {
            fail("Changing clone address reset original to null.");
        }
        if (clone.getBeerConsumer() != consumer) {
            fail("Changing clone did not work.");
        }
        
        try{
            consumer = em.find(BeerConsumer.class, consumerId);
            BeerConsumer consumerClone = (BeerConsumer)consumer.clone();
            
            Iterator<Alpine> alpineIterator = consumer.getAlpineBeersToConsume().iterator();
            while (alpineIterator.hasNext()){
                alpine = alpineIterator.next();
                assertTrue("The original beerConsumer has an alpine beer with an incorrect back pointer.", consumer == alpine.getBeerConsumer());
            }
            
            alpineIterator = consumerClone.getAlpineBeersToConsume().iterator();
            while (alpineIterator.hasNext()){
                alpine = alpineIterator.next();
                assertTrue("The cloned beerConsumer has an alpine beer with an incorrect back pointer.", consumerClone == alpine.getBeerConsumer());
            }
            
            Iterator<BlueLight> blueLightIterator = consumer.getBlueLightBeersToConsume().iterator();
            while (blueLightIterator.hasNext()){
                blueLight = blueLightIterator.next();
                assertTrue("The original beerConsumer has an BlueLight beer with an incorrect back pointer.", consumer == blueLight.getBeerConsumer());
            }
            
            blueLightIterator = consumerClone.getBlueLightBeersToConsume().iterator();
            while (blueLightIterator.hasNext()){
                blueLight = blueLightIterator.next();
                assertTrue("The cloned beerConsumer has an BlueLight beer with an incorrect back pointer.", consumerClone == blueLight.getBeerConsumer());
            }
        } catch (CloneNotSupportedException e){
            fail("Call to clone threw CloneNotSupportedException");
        }
        
        alpine = em.find(Alpine.class, clone.getId());
        em.remove(alpine);
        blueLight = em.find(BlueLight.class, blueLightId);
        em.remove(blueLight);
        consumer = em.find(BeerConsumer.class, consumerId);
        em.remove(consumer);
        commitTransaction(em);
        closeEntityManager(em);
    }
    
    // BUG 227345
    public void testCascadeRemove() {
        BeerConsumer beerConsumer = null;
        BlueLight blueLightPersisted = null;
        BlueLight blueLightDetached = null;
        EntityManager em = createEntityManager();

        // Create and persist the beerConsumer
        beginTransaction(em);
        beerConsumer = new BeerConsumer();
        beerConsumer.setName("Beer Man");

        blueLightPersisted = new BlueLight();
        beerConsumer.getBlueLightBeersToConsume().add(blueLightPersisted);
        blueLightPersisted.setBeerConsumer(beerConsumer);

        em.persist(beerConsumer);

        // Unique key must be set before commit.
        blueLightPersisted.setUniqueKey(blueLightPersisted.getId().toBigInteger());

        commitTransaction(em);

        // They should be known by the EM
        assertTrue(em.contains(beerConsumer));
        assertTrue(em.contains(blueLightPersisted));

        // Create BlueLightDetached and manage the relations
        beginTransaction(em);
        blueLightDetached = new BlueLight();
        blueLightDetached.setUniqueKey(new BigDecimal(blueLightPersisted.getUniqueKey().intValue() + 1).toBigInteger());

        // Set the pointers
        beerConsumer.getBlueLightBeersToConsume().add(blueLightDetached);
        blueLightDetached.setBeerConsumer(beerConsumer);

        // And now remove the beer consumer. The remove-operation should cascade
        em.remove(beerConsumer);

        // It's o.k. should be detached
        assertFalse(em.contains(blueLightDetached));

        try {
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            fail("An exception was caught during the remove of the BeerConsumer: [" + e.getMessage() + "]");
        }

        // Ensure neither the beer consumer nor the blue light
        // persisted/detached are available.
        assertFalse("The beer consumer was not removed", em.contains(beerConsumer));
        assertFalse("The blue light persisted was not removed even though the its owning beer comsumer was removed", em.contains(blueLightPersisted));
        assertFalse("The blue light detached was persisted even though the its owning beer comsumer was removed", em.contains(blueLightDetached));

        closeEntityManager(em);
    }
}
