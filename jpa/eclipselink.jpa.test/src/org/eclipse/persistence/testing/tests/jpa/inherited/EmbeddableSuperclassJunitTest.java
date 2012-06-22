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
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.inherited;

import java.util.Date;
import java.util.Vector;
import java.util.Calendar;

import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.inherited.Alpine;
import org.eclipse.persistence.testing.models.jpa.inherited.Canadian;
import org.eclipse.persistence.testing.models.jpa.inherited.Blue;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.SerialNumber;
import org.eclipse.persistence.testing.models.jpa.inherited.Certification;
import org.eclipse.persistence.testing.models.jpa.inherited.TelephoneNumber;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;
 
public class EmbeddableSuperclassJunitTest extends JUnitTestCase {
    private static Date m_savedDate;
    private static Integer m_alpineId, m_alpineId1, m_alpineId2;
    private static Integer m_canadianId;
    private static Integer m_beerConsumerId1, m_beerConsumerId2;
    private static Integer m_certId1, m_certId2, m_certId3, m_certId4;
    private static String m_canadianProperty1 = "string1";
    private static String m_canadianProperty2 = "string2";
    
    public EmbeddableSuperclassJunitTest() {
        super();
    }
    
    public EmbeddableSuperclassJunitTest(String name) {
        super(name);
    }
    
    public void setUp() {
        super.setUp();
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmbeddableSuperclassJunitTest");
        suite.addTest(new EmbeddableSuperclassJunitTest("testSetup"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testExistenceCheckingSetting"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testCreateBeerConsumer"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testCreateAlpine"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testCreateCanadian"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testCreateCertifications"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testCreateTelephoneNumbers"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testReadBeerConsumer"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testReadAlpine"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testReadCanadian"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testInsertNewAlpine"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testInsertNewAlpineAndModifyOrderOfExistingAlpines"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testUpdateAlpine"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testUpdateAlpineThroughBeerConsumer"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testUpdateBeerConsumer"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testUpdateCanadian"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testUpdateCanadianThroughBeerConsumer"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testUpdateCertifications"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testUpdateTelephoneNumberThroughBeerConsumer"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testShuffleTelephoneNumbersOnBeerConsumers"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testDeleteAlpine"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testDeleteCanadian"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testDeleteBeerConsumer"));
        suite.addTest(new EmbeddableSuperclassJunitTest("testOptimisticLockingTest"));

        return suite;
    }
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new InheritedTableManager().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    

    
    /**
     * Verifies that existence-checking metadata is correctly processed.
     */
    public void testExistenceCheckingSetting() {
        ServerSession session = JUnitTestCase.getServerSession();
        
        ClassDescriptor canadianDescriptor = session.getDescriptor(Canadian.class);
        assertTrue("Canadian existence checking was incorrect", canadianDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy() == DoesExistQuery.CheckDatabase);
        
        ClassDescriptor alpineDescriptor = session.getDescriptor(Alpine.class);
        assertTrue("Alpine existence checking was incorrect", alpineDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy() == DoesExistQuery.CheckCache);
        
        ClassDescriptor blueDescriptor = session.getDescriptor(Blue.class);
        assertTrue("Blue existence checking was incorrect", blueDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy() == DoesExistQuery.CheckCache);
    }
    
    public void testCreateAlpine() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
        
            BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId2);
            
            SerialNumber serialNumber1 = new SerialNumber();
            em.persist(serialNumber1);
            Alpine alpine1 = new Alpine(serialNumber1);
            alpine1.setBestBeforeDate(Helper.dateFromYearMonthDate(2005, 8, 21));
            alpine1.setAlcoholContent(5.0);
            alpine1.setBeerConsumer(beerConsumer);
            alpine1.setClassification(Alpine.Classification.BITTER);
            alpine1.addInspectionDate(new Date(System.currentTimeMillis()));
            em.persist(alpine1);
            m_alpineId = alpine1.getId();

            SerialNumber serialNumber2 = new SerialNumber();
            em.persist(serialNumber2);
            Alpine alpine2 = new Alpine(serialNumber2);
            alpine2.setBestBeforeDate(Helper.dateFromYearMonthDate(2005, 8, 17));
            alpine2.setAlcoholContent(5.5);
            alpine2.setBeerConsumer(beerConsumer);
            alpine2.setClassification(Alpine.Classification.STRONG);
            em.persist(alpine2);
            m_alpineId1 = alpine2.getId();
            
            SerialNumber serialNumber3 = new SerialNumber();
            em.persist(serialNumber3);
            Alpine alpine3 = new Alpine(serialNumber3);
            alpine3.setBestBeforeDate(Helper.dateFromYearMonthDate(2005, 8, 22));
            alpine3.setAlcoholContent(4.5);
            alpine3.setBeerConsumer(beerConsumer);
            alpine3.setClassification(Alpine.Classification.SWEET);
            em.persist(alpine3);
            m_alpineId2 = alpine3.getId();
            
            commitTransaction(em);    

            clearCache();
            Alpine alpineReadOut = createEntityManager().find(Alpine.class, m_alpineId);
            assertTrue("Alpine1 object is not same as readout", alpineReadOut.equals(alpine1));

            Alpine alpineReadOut1 = createEntityManager().find(Alpine.class, m_alpineId1);
            assertTrue("Alpine2 object is not same as readout", alpineReadOut1.equals(alpine2));

            Alpine alpineReadOut2 = createEntityManager().find(Alpine.class, m_alpineId2);
            assertTrue("Alpine3 object is not same as readout", alpineReadOut2.equals(alpine3));
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        }
        closeEntityManager(em);
        
    }
    
    public void testCreateBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            
            BeerConsumer beerConsumer1 = new BeerConsumer();
            beerConsumer1.setName("Guy Pelletier");
            em.persist(beerConsumer1);
            m_beerConsumerId1 = beerConsumer1.getId();
            
            BeerConsumer beerConsumer2 = new BeerConsumer();
            beerConsumer2.setName("Tom Ware");
            em.persist(beerConsumer2);
            m_beerConsumerId2 = beerConsumer2.getId();
            
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
    
    public void testCreateCanadian() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            
            BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId1);
            
            Canadian canadian1 = new Canadian();
            m_savedDate = Calendar.getInstance().getTime();
            canadian1.setBornOnDate(m_savedDate);
            canadian1.setAlcoholContent(5.0);
            canadian1.setBeerConsumer(beerConsumer);
            canadian1.setFlavor(Canadian.Flavor.LAGER);
            canadian1.getProperties().put(m_canadianProperty1, new Date(System.currentTimeMillis()) );
            em.persist(canadian1);
            m_canadianId = canadian1.getId();

            Canadian canadian2 = new Canadian();
            canadian2.setBornOnDate(Calendar.getInstance().getTime());
            canadian2.setAlcoholContent(5.5);
            canadian2.setFlavor(Canadian.Flavor.ICE);
            canadian2.setBeerConsumer(beerConsumer);
            em.persist(canadian2);
            
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
    
    public void testCreateCertifications() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            
            Certification cert1 = new Certification();
            cert1.setDescription("Certification 1");
            em.persist(cert1);
            m_certId1 = cert1.getId();
            
            Certification cert2 = new Certification();
            cert2.setDescription("Certification 2");
            em.persist(cert2);
            m_certId2 = cert2.getId();
           
            Certification cert3 = new Certification();
            cert3.setDescription("Certification 3");
            em.persist(cert3);
            m_certId3 = cert3.getId();
            
            Certification cert4 = new Certification();
            cert4.setDescription("Certification 4");
            em.persist(cert4);
            m_certId4 = cert4.getId();
            
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
    
    public void testCreateTelephoneNumbers() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            
            BeerConsumer beerConsumer1 = em.find(BeerConsumer.class, m_beerConsumerId1);
           
            TelephoneNumber telephoneNumber1 = new TelephoneNumber();
            telephoneNumber1.setType("Home");
            telephoneNumber1.setAreaCode("975");
            telephoneNumber1.setNumber("1234567");
            beerConsumer1.addTelephoneNumber(telephoneNumber1);
            
            TelephoneNumber telephoneNumber2 = new TelephoneNumber();
            telephoneNumber2.setType("Cell");
            telephoneNumber2.setAreaCode("975");
            telephoneNumber2.setNumber("7654321");
            beerConsumer1.addTelephoneNumber(telephoneNumber2);
            
            BeerConsumer beerConsumer2 = em.find(BeerConsumer.class, m_beerConsumerId2);
            
            TelephoneNumber telephoneNumber3 = new TelephoneNumber();
            telephoneNumber3.setType("Home");
            telephoneNumber3.setAreaCode("555");
            telephoneNumber3.setNumber("5555555");
            beerConsumer2.addTelephoneNumber(telephoneNumber3);
            
            TelephoneNumber telephoneNumber4 = new TelephoneNumber();
            telephoneNumber4.setType("Cell");
            telephoneNumber4.setAreaCode("555");
            telephoneNumber4.setNumber("3331010");
            beerConsumer2.addTelephoneNumber(telephoneNumber4);
            
            TelephoneNumber telephoneNumber5 = new TelephoneNumber();
            telephoneNumber5.setType("Work");
            telephoneNumber5.setAreaCode("999");
            telephoneNumber5.setNumber("8648363");
            beerConsumer2.addTelephoneNumber(telephoneNumber5);
            
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
    
    public void testDeleteAlpine() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            
            em.remove(em.find(Alpine.class, m_alpineId));
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertTrue("Error deleting an Alpine beer", em.find(Alpine.class, m_alpineId) == null);
    }
    
    public void testDeleteBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.remove(em.find(BeerConsumer.class, m_beerConsumerId1));
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertTrue("Error deleting a BeerConsumer", em.find(BeerConsumer.class, m_beerConsumerId1) == null);
    }
    
    public void testDeleteCanadian() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.remove(em.find(Canadian.class, m_canadianId));
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertTrue("Error deleting a Canadian beer", em.find(Canadian.class, m_canadianId) == null);
    }
    
    public void testInsertNewAlpine() {
        Alpine alpine = null;
        EntityManager em = createEntityManager();
        // Part 1 ... add an alpine beer to the collection.
        BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId2);
        beginTransaction(em);
        try {
            beerConsumer = em.merge(beerConsumer);
            SerialNumber serialNumber = new SerialNumber();
            em.persist(serialNumber);
            alpine = new Alpine(serialNumber);
            em.persist(alpine);
            alpine.setBestBeforeDate(Helper.dateFromYearMonthDate(2005, 8, 18));
            alpine.setAlcoholContent(5.4);
            alpine.setClassification(Alpine.Classification.BITTER);
            beerConsumer.addAlpineBeerToConsume(alpine, 0);
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
            
        // Part 2 ... read from cache.
        beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId2);
        
        // Read the beerConsumer back from the cache, check the ordering.
        Vector alpinesFromCache = (Vector) beerConsumer.getAlpineBeersToConsume();
        assertTrue("The new alpine was not added at the correct index in the cache.", alpinesFromCache.indexOf(alpine) == 0);
        
        // Part 3 ... read from database.
        clearCache();
        em.clear();
        beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId2);
        
        // Read the beerConsumer back from the database, check the ordering.
        Vector alpinesFromDB = (Vector) beerConsumer.getAlpineBeersToConsume();
        assertTrue("The new alpine was not added at the correct index when retrieving from the database.", alpinesFromDB.indexOf(alpine) == 1);
    }
    
    public void testInsertNewAlpineAndModifyOrderOfExistingAlpines() {
        Alpine alpine1 = null, alpine2 = null;
        EntityManager em = createEntityManager();
        // Part 1 ... add an alpine beer to the collection.
        BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId2);
        beginTransaction(em);
        try {
        
            beerConsumer = em.merge(beerConsumer);
            SerialNumber serialNumber = new SerialNumber();
            em.persist(serialNumber);
            alpine1 = new Alpine(serialNumber);
            em.persist(alpine1);
            alpine1.setBestBeforeDate(Helper.dateFromYearMonthDate(2005, 8, 16));
            alpine1.setAlcoholContent(5.6);
            alpine1.setClassification(Alpine.Classification.STRONG);
            beerConsumer.addAlpineBeerToConsume(alpine1, 0);
            
            // Part 2 ... change the date, hence the index, of an alpine.
            alpine2 = beerConsumer.getAlpineBeerToConsume(4);
            alpine2.setBestBeforeDate(Helper.dateFromYearMonthDate(2005, 8, 20));
            beerConsumer.moveAlpineBeerToConsume(4, 3);
            
            // Part 3 ... remove 2 alpines ...
            beerConsumer.removeAlpineBeerToConsume(4);
            beerConsumer.removeAlpineBeerToConsume(2);
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
            
            // Part 3 ... read from cache.
        beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId2);
        
        // Read the beerConsumer back from the cache, check the ordering.
        Vector alpinesFromCache = (Vector) beerConsumer.getAlpineBeersToConsume();
        assertTrue("The new alpine was not added at the correct index in the cache.", alpinesFromCache.indexOf(alpine1) == 0);
        assertTrue("The alpine was not moved to the correct index in the cache.", alpinesFromCache.indexOf(alpine2) == 2);
        
        // Part 4 ... read from database.
        clearCache();
        em.clear();
        beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId2);
        
        // Read the beerConsumer back from the database, check the ordering.
        Vector alpinesFromDB = (Vector) beerConsumer.getAlpineBeersToConsume();
        assertTrue("The new alpine was not added at the correct index when retrieving from the database.", alpinesFromDB.indexOf(alpine1) == 0);
        assertTrue("The alpine was not moved to the correct index when retrieving from the database.", alpinesFromDB.indexOf(alpine2) == 2);
    }
    
    // This test came about while testing for BUG 5867008.
    public void testOptimisticLockingTest() {
        EntityManager em = createEntityManager();
    
        try {
            /////// Part 1 - create new objects to persist and mirror detached objects.
            beginTransaction(em);    
            BeerConsumer beerConsumer = new BeerConsumer();
            beerConsumer.setName("Guy Pelletier");
            SerialNumber serialNumber = new SerialNumber();
            em.persist(serialNumber);
            Alpine alpine = new Alpine(serialNumber);
            em.persist(alpine);
            
            alpine.setBestBeforeDate(Helper.dateFromYearMonthDate(2005, 8, 21));
            alpine.setAlcoholContent(5.0);
            alpine.setClassification(Alpine.Classification.BITTER);
            long currentTimeMillis = System.currentTimeMillis();
            alpine.addInspectionDate(new Date(currentTimeMillis));
            beerConsumer.addAlpineBeerToConsume(alpine);
            em.persist(beerConsumer); // persist is cascaded
            Integer bid = beerConsumer.getId();
            commitTransaction(em);

            /////// Part 2
            beginTransaction(em);
            // Step 1 - removed managed alpines from managed beer consumer.
            BeerConsumer beerConsumerManaged = em.find(BeerConsumer.class, bid);
            for (int i = beerConsumerManaged.getAlpineBeersToConsume().size() - 1; i >= 0; i--) {
                Alpine alpineManaged = beerConsumerManaged.removeAlpineBeerToConsume(i);
                em.remove(alpineManaged);
                em.flush();
            }

            BeerConsumer beerConsumerDetached = new BeerConsumer();
            beerConsumerDetached.setName("Guy Pelletier");
            SerialNumber serialNumberAtt = new SerialNumber();
            em.persist(serialNumberAtt);
            Alpine alpineDetached = new Alpine(serialNumberAtt);
            alpineDetached.setBestBeforeDate(Helper.dateFromYearMonthDate(2005, 8, 21));
            alpineDetached.setAlcoholContent(5.5); // change something
            alpineDetached.setClassification(Alpine.Classification.BITTER);
            alpineDetached.addInspectionDate(new Date(currentTimeMillis));
            beerConsumerDetached.addAlpineBeerToConsume(alpineDetached);
            // Step 2 - update id on detached object.
            beerConsumerDetached.setId(bid);
            // Step 3 - update the version field. If this was done before
            // the flush calls above this test would fail.
            beerConsumerDetached.setVersion(beerConsumerManaged.getVersion());
            alpineDetached.setVersion(null);
            // Step 4 - merge detached object.
            em.merge(beerConsumerDetached);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            e.printStackTrace();
            closeEntityManager(em);
            fail("An exception was caught: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
    }
    
    public void testReadAlpine() {
        Alpine alpine = createEntityManager().find(Alpine.class, m_alpineId);
        assertTrue("Error on reading back an Alpine beer", alpine != null);
        assertTrue("The enum was not read back in properly.", alpine.getClassification() == Alpine.Classification.BITTER);
    }
    
    public void testReadBeerConsumer() {
        BeerConsumer beerConsumer = createEntityManager().find(BeerConsumer.class, m_beerConsumerId1);
        assertTrue("Error on reading back a BeerConsumer", beerConsumer != null);
    }
    
    public void testReadCanadian() {
        Canadian canadian = createEntityManager().find(Canadian.class, m_canadianId);
        assertTrue("Error on reading back a Canadian beer", canadian != null);
        assertTrue("The enum was not read back in properly.", canadian.getFlavor() == Canadian.Flavor.LAGER);
    }
    
    public void testShuffleTelephoneNumbersOnBeerConsumers() {
        int beerConsumer1TelephoneCountStart = 0, beerConsumer2TelephoneCountStart = 0;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
        
            BeerConsumer beerConsumer1 = em.find(BeerConsumer.class, m_beerConsumerId1);
            beerConsumer1TelephoneCountStart = beerConsumer1.getTelephoneNumbers().size();
            
            BeerConsumer beerConsumer2 = em.find(BeerConsumer.class, m_beerConsumerId2);
            beerConsumer2TelephoneCountStart = beerConsumer2.getTelephoneNumbers().size();
            
            TelephoneNumber phone = (TelephoneNumber) beerConsumer1.getTelephoneNumbers().values().iterator().next();
            phone.setBeerConsumer(beerConsumer2);
            
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
        BeerConsumer newBeerConsumer1 = em.find(BeerConsumer.class, m_beerConsumerId1);
        int beerConsumer1TelephoneCountEnd = newBeerConsumer1.getTelephoneNumbers().size();
        
        BeerConsumer newBeerConsumer2 = em.find(BeerConsumer.class, m_beerConsumerId2);
        int beerConsumer2TelephoneCountEnd = newBeerConsumer2.getTelephoneNumbers().size();
        
        assertTrue("Error updating a TelephoneNumber's beer consumer", beerConsumer1TelephoneCountEnd + 1 == beerConsumer1TelephoneCountStart);
        assertTrue("Error updating a TelephoneNumber's beer consumer", beerConsumer2TelephoneCountEnd - 1 == beerConsumer2TelephoneCountStart);
    }
    
    public void testUpdateAlpine() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
        
            Alpine alpine = em.find(Alpine.class, m_alpineId);
            alpine.setBestBeforeDate(Helper.dateFromYearMonthDate(2005, 8, 19));
            em.merge(alpine);    
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
        Alpine newAlpine = em.find(Alpine.class, m_alpineId);
        assertTrue("Error updating an Alpine beer.", newAlpine.getBestBeforeDate().equals(Helper.dateFromYearMonthDate(2005, 8, 19)));
    }
    
    public void testUpdateAlpineThroughBeerConsumer() {
        int id = 0;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
        
            BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId2);
            Alpine alpine = (Alpine) beerConsumer.getAlpineBeersToConsume().iterator().next();
            alpine.setBestBeforeDate(Helper.dateFromYearMonthDate(2005, 9, 19));
            id = alpine.getId();
            
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
        Alpine newAlpine = em.find(Alpine.class, id);
        closeEntityManager(em);
        assertTrue("Error updating an Alpine beer.", newAlpine.getBestBeforeDate().equals(Helper.dateFromYearMonthDate(2005, 9, 19)));
    }
    
    public void testUpdateBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId1);
            beerConsumer.setName("Big beer gut");
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
        BeerConsumer newBeerConsumer = em.find(BeerConsumer.class, m_beerConsumerId1);        
        assertTrue("Error updating a BeerConsumer", newBeerConsumer.getName().equals("Big beer gut"));
    }
        
    public void testUpdateCanadian() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Canadian canadian = em.find(Canadian.class, m_canadianId);
            //canadian.setBornOnDate(Helper.dateFromYearMonthDate(2005, 8, 19));
            canadian.getBornOnDate().setTime((Helper.dateFromYearMonthDate(2005, 8, 19)).getTime());
            canadian.getProperties().put( m_canadianProperty2, new Date(System.currentTimeMillis()) );
            em.merge(canadian);
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
        Canadian newCanadian = em.find(Canadian.class, m_canadianId);
        assertTrue("Error updating a Canadian beer's BornOnDate", newCanadian.getBornOnDate().equals(Helper.dateFromYearMonthDate(2005, 8, 19)));
        assertTrue("Error updating a Canadian beer's Properties", newCanadian.getProperties().size()==2);
    }
    
    public void testUpdateCanadianThroughBeerConsumer() {
        int id =  0;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
        
            BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId1);
            Canadian canadian = (Canadian) beerConsumer.getCanadianBeersToConsume().values().iterator().next();
            canadian.setBornOnDate(Helper.dateFromYearMonthDate(2005, 9, 19));
            id = canadian.getId();
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        clearCache();
        
        Canadian newCanadian = em.find(Canadian.class, id);
        assertTrue("Error updating a Canadian beer.", newCanadian.getBornOnDate().equals(Helper.dateFromYearMonthDate(2005, 9, 19)));
    }
    
    public void testUpdateTelephoneNumberThroughBeerConsumer() {
        TelephoneNumber oldPhone = null, newPhone = null;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
        
            BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId1);
            
            oldPhone = (TelephoneNumber) beerConsumer.getTelephoneNumbers().values().iterator().next();
            beerConsumer.removePhoneNumber(oldPhone);
            em.remove(oldPhone);
            
            newPhone = new TelephoneNumber();
            newPhone.setAreaCode("XXX");
            newPhone.setNumber(oldPhone.getNumber());
            newPhone.setType(oldPhone.getType());
            beerConsumer.addTelephoneNumber(newPhone);
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        clearCache();

        BeerConsumer bc = em.find(BeerConsumer.class, m_beerConsumerId1);
        
        assertTrue("The new/updated phone was not persisted.", bc.hasTelephoneNumber(newPhone));
        assertFalse("The old phone was not removed.", bc.hasTelephoneNumber(oldPhone));
    }
    
    public void testUpdateCertifications() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
           
            BeerConsumer beerConsumer1 = em.find(BeerConsumer.class, m_beerConsumerId1);
            
            Certification cert1 = em.find(Certification.class, m_certId1);
            cert1.setBeerConsumer(beerConsumer1);
            
            Certification cert2 = em.find(Certification.class, m_certId2);
            cert2.setBeerConsumer(beerConsumer1);
    
            BeerConsumer beerConsumer2 = em.find(BeerConsumer.class, m_beerConsumerId2);
            
            Certification cert3 = em.find(Certification.class, m_certId3);
            cert3.setBeerConsumer(beerConsumer2);
            
            Certification cert4 = em.find(Certification.class, m_certId4);
            cert4.setBeerConsumer(beerConsumer2);
            
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }
}
